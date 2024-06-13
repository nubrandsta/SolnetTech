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

    private val _finishedLaporanLiveData = repository.finishedLaporanLiveData
    val finishedLaporanLiveData = _finishedLaporanLiveData

    fun getLaporan(daerah: String){
        viewModelScope.launch {
            repository.listenForConfirmedReport(daerah)
        }
    }

    fun getFinishedLaporan(idTech: Int){
        viewModelScope.launch {
            repository.listenForFinishedReport(idTech)
        }
    }

    fun getTeknisi(){
        viewModelScope.launch {
            repository.getTeknisiData()
        }
    }


}