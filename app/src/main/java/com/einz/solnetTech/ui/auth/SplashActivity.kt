package com.einz.solnetTech.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.viewModels
import com.einz.solnetTech.data.di.ViewModelFactory
import com.einz.solnetTech.databinding.ActivitySplashBinding
import com.einz.solnetTech.ui.home.HomeActivity
import com.google.firebase.auth.FirebaseAuth
import com.einz.solnetTech.data.State
import com.einz.solnetTech.ui.report.ActiveLaporanActivity
import com.einz.solnetTech.ui.util.observeOnce

class SplashActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySplashBinding
    private val firebaseAuth : FirebaseAuth = FirebaseAuth.getInstance()

    private lateinit var factory: ViewModelFactory
    private val viewModel: LoginViewModel by viewModels{factory}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        factory = ViewModelFactory.getInstance(this)




        Handler(Looper.getMainLooper()).postDelayed({
            if (firebaseAuth.currentUser != null) {
                viewModel.checkActiveLaporan()
                viewModel.checkActiveLaporanLiveData.observeOnce(this){
                    result ->
                    when(result){
                        is State.Success -> {
                            val intent = Intent(this@SplashActivity, ActiveLaporanActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        is State.Error -> {
                            val intent = Intent(this@SplashActivity, HomeActivity::class.java)
                            startActivity(intent)
                            finish()

                        }
                        is State.Loading -> {

                        }
                    }
                }
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                Toast.makeText(this, "Anda perlu login", Toast.LENGTH_SHORT).show()
                finish()
                startActivity(intent)

            }
        }, 3000L)
    }
}