package com.einz.solnetTech.ui.setting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.einz.solnetTech.R
import com.einz.solnetTech.data.di.ViewModelFactory
import com.einz.solnetTech.databinding.ActivityChangePhoneBinding
import com.einz.solnetTech.databinding.ActivitySettingBinding
import com.einz.solnetTech.data.Result

class ChangePhoneActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangePhoneBinding
    private lateinit var factory: ViewModelFactory

    private lateinit var idTeknisi:String

    private val viewModel: SettingViewModel by viewModels{factory}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePhoneBinding.inflate(layoutInflater)
        setContentView(binding.root)

        factory = ViewModelFactory.getInstance(this)

        viewModel.getTeknisi()
        viewModel.teknisiLiveData.observe(this){
            result ->
            when(result){
                is Result.Success -> {
                    idTeknisi = result.data?.idTeknisi.toString()
                    binding.progressBar.visibility = View.GONE
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                }
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
            }
        }

        binding.btnConfirm.isEnabled = false

        checkInput()

        binding.btnConfirm.setOnClickListener {
            val phone = binding.tfEditPhone.text?.trim().toString()
            viewModel.changePhone(idTeknisi, phone)
            viewModel.changePhoneLiveData.observe(this){
                result ->
                when(result){
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this, "Berhasil mengubah nomor telepon", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                    }
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun checkValid(){
        val textPhone = binding.tfEditPhone.text?.trim().toString()

        var isValid = true

        if (textPhone.length < 10) {
            binding.tfLayoutPhone.error = "Isi Nomor Telepon dengan benar"
            isValid = false
        } else {
            binding.tfLayoutPhone.error = null
        }

        binding.btnConfirm.isEnabled = isValid
    }

    private fun checkInput(){
        binding.tfEditPhone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(string: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(string: CharSequence?, start: Int, before: Int, count: Int) {
                checkValid()
            }
            override fun afterTextChanged(string: Editable?) {}
        })
    }
}