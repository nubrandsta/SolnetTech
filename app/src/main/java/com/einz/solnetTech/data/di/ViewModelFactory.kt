package com.einz.solnetTech.data.di


import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.einz.solnetTech.data.Repository


class ViewModelFactory private constructor(private val repository: Repository) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        when {
            modelClass.isAssignableFrom(com.einz.solnetTech.ui.auth.RegisterViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                return com.einz.solnetTech.ui.auth.RegisterViewModel(repository) as T
            }
            modelClass.isAssignableFrom(com.einz.solnetTech.ui.auth.LoginViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                return com.einz.solnetTech.ui.auth.LoginViewModel(repository) as T
            }
            modelClass.isAssignableFrom(com.einz.solnetTech.ui.home.HomeViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                return com.einz.solnetTech.ui.home.HomeViewModel(repository) as T
            }
            modelClass.isAssignableFrom(com.einz.solnetTech.ui.report.LaporanViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                return com.einz.solnetTech.ui.report.LaporanViewModel(repository) as T
            }
            modelClass.isAssignableFrom(com.einz.solnetTech.ui.setting.SettingViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                return com.einz.solnetTech.ui.setting.SettingViewModel(repository) as T
            }


            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(Injection.provideRepository(context))
            }.also { instance = it }
    }

}