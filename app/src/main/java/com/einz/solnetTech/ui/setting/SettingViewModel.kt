package com.einz.solnetTech.ui.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.einz.solnetTech.data.Repository
import kotlinx.coroutines.launch

class SettingViewModel(private val repository: Repository): ViewModel() {

    private val _teknisiLiveData = repository.teknisiLiveData
    val teknisiLiveData = _teknisiLiveData

    private val _changePasswordLiveData = repository.changePasswordLiveData
    val changePasswordLiveData = repository.changePasswordLiveData

    private val _changePhoneLiveData = repository.changePhoneLiveData
    val changePhoneLiveData = repository.changePhoneLiveData

    private val _loggedOutLiveData = repository.loggedOutLiveData
    val loggedOutLiveData = _loggedOutLiveData

    fun getTeknisi(){
        viewModelScope.launch {
            repository.getTeknisiData()
        }
    }

    fun changePassword(password: String){
        viewModelScope.launch {
            repository.changePassword(password)
        }
    }


    fun changePhone(idTeknisi:String, phone:String){
        viewModelScope.launch {
            repository.changePhone(idTeknisi, phone)
        }
    }

    fun logout(){
        viewModelScope.launch {
            repository.logout()
        }
    }

}