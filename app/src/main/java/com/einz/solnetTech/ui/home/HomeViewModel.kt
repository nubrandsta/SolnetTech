package com.einz.solnetTech.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.einz.solnetTech.data.Repository
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: Repository): ViewModel() {

    private val _laporanLiveData = repository.laporanListLiveData
    val laporanLiveData = _laporanLiveData

    private val _teknisiLiveData = repository.teknisiLiveData
    val teknisiLiveData = _teknisiLiveData

    private val _takeLaporanLiveData = repository.takeLaporanLiveData
    val takeLaporanLiveData = _takeLaporanLiveData

    fun getLaporan(){
        viewModelScope.launch {
            repository.listenForUserReportsWithStatusZero()
        }
    }

    fun getTeknisi(){
        viewModelScope.launch {
            repository.getTeknisiData()
        }
    }


}