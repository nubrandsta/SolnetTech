package com.einz.solnetTech.ui.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.einz.solnetTech.data.Repository
import kotlinx.coroutines.launch

class LaporanViewModel(private val repository: Repository): ViewModel()  {


    private val _laporanLiveData = repository.laporanLiveData
    val laporanLiveData  = _laporanLiveData

    private val _activeLaporanLiveData = repository.activeLaporanIdLiveData
    val activeLaporanLiveData = _activeLaporanLiveData

    private val _takeLaporanLiveData = repository.takeLaporanLiveData
    val takeLaporanLiveData = _takeLaporanLiveData

    fun resetLaporan(){
        repository.resetLaporan()
    }


    fun getLaporan(idLaporan: String){
        viewModelScope.launch {
            repository.getLaporanById(idLaporan)
        }
    }

    fun getActiveLaporan(){
        viewModelScope.launch {
            repository.checkForActiveLaporanId()
        }
    }

    fun takeLaporan(idLaporan: String, state:Int, idTeknisi:Int ){
        viewModelScope.launch {
            repository.updateLaporanStatusAndSetActiveIdLaporan(idLaporan, state, idTeknisi)
        }
    }

    fun updateLaporanStatus(idLaporan:String, state:Int, solution:String){
        viewModelScope.launch {
            repository.updateLaporanStatus(idLaporan, state, solution)
        }
    }

}