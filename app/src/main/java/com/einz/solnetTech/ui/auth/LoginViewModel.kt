package com.einz.solnetTech.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.einz.solnetTech.data.Repository
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: Repository): ViewModel() {

    private val _responseLogin = repository.userLiveData
    val responseLogin = _responseLogin

    private val _teknisiLiveData = repository.teknisiLiveData
    val teknisiLiveData = _teknisiLiveData

    private val _loggedOutLiveData = repository.loggedOutLiveData
    val loggedOutLiveData = _loggedOutLiveData

    private val _checkActiveLaporanLiveData = repository.activeLaporanIdLiveData
    val checkActiveLaporanLiveData = _checkActiveLaporanLiveData

    fun login(username : String, password : String){
        viewModelScope.launch{
            repository.login(username, password)
        }
    }

    fun getTeknisi(){
        viewModelScope.launch {
            repository.getTeknisiData()
        }
    }

    fun logout(){
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun checkActiveLaporan(){
        viewModelScope.launch {
            repository.checkForActiveLaporanId()
        }
    }


}