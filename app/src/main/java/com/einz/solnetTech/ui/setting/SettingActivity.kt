package com.einz.solnetTech.ui.setting

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.einz.solnetTech.R
import com.einz.solnetTech.data.di.ViewModelFactory
import com.einz.solnetTech.databinding.ActivitySettingBinding
import com.einz.solnetTech.data.Result
import com.einz.solnetTech.ui.auth.LoginActivity

class SettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingBinding
    private lateinit var factory: ViewModelFactory

    private val viewModel: SettingViewModel by viewModels{factory}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        factory = ViewModelFactory.getInstance(this)

        viewModel.getTeknisi()
        viewModel.teknisiLiveData.observe(this){
            result ->
            when(result){
                is Result.Success -> {
                    binding.apply{
                        val nameTeknisi = result.data?.namaTeknisi
                        val idTech = result.data?.idTeknisi

                        username.text = "$nameTeknisi"
                        idTeknisi.text = "ID TEKNISI: $idTech"

                        progressBar.visibility = View.GONE
                    }
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                }
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                else -> {}
            }
        }

        binding.changePassword.setOnClickListener {
            val intent = Intent(this, ChangePasswordActivity::class.java)
            startActivity(intent)

        }
        binding.changePhone.setOnClickListener {
            val intent = Intent(this, ChangePhoneActivity::class.java)
            startActivity(intent)
        }
        binding.logout.setOnClickListener {
            // Create a confirmation dialog
            val builder = AlertDialog.Builder(this@SettingActivity)
            builder.setTitle("Konfirmasi Keluar")
            builder.setMessage("Apakah anda yakin untuk keluar akun?")

            // Set a positive button (Yes action)
            builder.setPositiveButton("Iya") { _: DialogInterface, _: Int ->
                // User confirmed, perform the logout action
                viewModel.logout()
            }

            // Set a negative button (Cancel action)
            builder.setNegativeButton("Batal") { dialog: DialogInterface, _: Int ->
                // User canceled, dismiss the dialog
                dialog.dismiss()
            }

            // Create and show the dialog
            val dialog = builder.create()
            dialog.show()

        }

        viewModel.loggedOutLiveData.observe(this){
                result ->
            when(result){
                is Result.Success -> {
                    if(result.data == true){
                        val intent = Intent(this, LoginActivity::class.java)
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)
                        finish()
                    }

                }
                is Result.Error -> {

                }
                is Result.Loading -> {
                    Log.d("SettingActivity", "Loading")
                }
            }
        }
    }
}