package com.einz.solnetTech.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.einz.solnetTech.data.di.ViewModelFactory
import com.einz.solnetTech.databinding.ActivityLoginBinding
import com.einz.solnetTech.data.State
import com.einz.solnetTech.ui.home.HomeActivity
import com.einz.solnetcs.util.ErrorDialog

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var factory: ViewModelFactory
    private val viewModel: LoginViewModel by viewModels{factory}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        factory = ViewModelFactory.getInstance(this)
        binding.btnLogin.isEnabled = false

        checkValid()

        binding.btnLogin.setOnClickListener{
            val email = binding.tfEditEmail.text?.trim().toString()
            val password = binding.tfEditPassword.text?.trim().toString()
            viewModel.login(email, password)
            viewModel.responseLogin.observe(this){
                    result ->
                when(result){
                    is State.Success -> {
                        viewModel.getTeknisi()
                    }
                    is State.Error -> {
                        showError(result.errorMessage)
                    }
                    is State.Loading -> {
                        showLoading(true)
                    }

                    else -> {
                        Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show()}
                }
            }
            viewModel.teknisiLiveData.observe(this){
                    result1 ->
                when(result1){
                    is State.Success -> {
                        loginSuccess()
                    }
                    is State.Error -> {
                        showError("Akun terdaftar sebagai pelanggan! bukan Teknisi!")
                        viewModel.logout()
                    }
                    is State.Loading -> {
                        showLoading(true)
                    }
                }

            }
        }



        binding.btnRegister.setOnClickListener{
            startActivity(Intent(this, RegisterActivity::class.java))
        }


    }


    private fun checkInput(){
        val textEmail = binding.tfEditEmail.text?.trim().toString()
        val textPassword = binding.tfEditPassword.text?.trim().toString()

        if(android.util.Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()){
            if(textPassword.length>7){
                binding.btnLogin.isEnabled = true
                binding.btnLogin.error = null
                binding.tfLayoutPassword.error = null
            }
            else{
                binding.btnLogin.isEnabled = false
                binding.tfLayoutPassword.error = "Kolom tidak boleh kosong!"
            }
            binding.tfLayoutEmail.error = null
        }
        else{
            binding.btnLogin.isEnabled = false
            binding.tfLayoutEmail.error = "Email tidak valid!"
        }
    }

    private fun checkValid(){
        binding.tfEditPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(string: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(string: CharSequence?, start: Int, before: Int, count: Int) {
                checkInput()
            }
            override fun afterTextChanged(string: Editable?) {}
        })

        binding.tfEditEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(string: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(string: CharSequence?, start: Int, before: Int, count: Int) {
                checkInput()
            }
            override fun afterTextChanged(string: Editable?) {}
        })
    }

    private fun showLoading(state: Boolean) {
        if (state) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }


    private fun loginSuccess() {
        binding.progressBar.visibility = View.GONE
        Toast.makeText(this, "Selamat Datang", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    // show error using a message box with an OK button to continue
    private fun showError(message: String) {
        binding.progressBar.visibility = View.GONE

        // Create an instance of the custom dialog fragment
        val errorDialog = ErrorDialog().apply {
            arguments = Bundle().apply {
                putString("message", message)
            }
        }

        // Show the dialog
        errorDialog.show(supportFragmentManager, "ErrorDialogFragment")
    }
}