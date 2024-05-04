package com.einz.solnetTech.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.einz.solnetTech.data.Repository
import com.einz.solnetTech.data.model.Teknisi
import kotlinx.coroutines.launch

class RegisterViewModel(private val repository: Repository): ViewModel() {

    private val _userLiveData = repository.registerSuccessLiveData
    val userLiveData = _userLiveData

    fun register(user: Teknisi, textPassword: String){
        viewModelScope.launch {
            repository.register(user,textPassword)
        }
    }


}