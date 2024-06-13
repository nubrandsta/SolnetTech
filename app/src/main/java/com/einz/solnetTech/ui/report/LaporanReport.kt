package com.einz.solnetTech.ui.report

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.einz.solnetTech.R
import com.einz.solnetTech.data.State
import com.einz.solnetTech.data.di.ViewModelFactory
import com.einz.solnetTech.databinding.ActivityLaporanBinding
import com.einz.solnetTech.databinding.ActivityLaporanReportBinding
import java.text.SimpleDateFormat
import java.util.Locale

class LaporanReport : AppCompatActivity() {
    private lateinit var binding: ActivityLaporanReportBinding
    private lateinit var factory: ViewModelFactory

    private val viewModel : LaporanViewModel by viewModels{factory}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLaporanReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        factory = ViewModelFactory.getInstance(this)

        val idLaporanString = intent.getStringExtra(LaporanActivity.EXTRA_LAPORAN)
        val idTeknisiString = intent.getIntExtra(LaporanActivity.EXTRA_TEKNISI, 0)

        viewModel.getLaporan(idLaporanString!!)

        viewModel.laporanLiveData.observe(this){
                laporan ->
            when(laporan){
                is State.Success -> {
                    binding.apply{
                        val desiredTime = laporan.data?.desiredTime?.toTimestamp()?.toDate()
                        val formattedDesiredTime = if (desiredTime != null) {
                            SimpleDateFormat("dd MMM HH:mm", Locale.getDefault()).format(desiredTime)
                        } else {
                            "Waktu tidak ditentukan"
                        }

                        val timeStamp = laporan.data?.timestamp?.toTimestamp()?.toDate()
                        val formattedTimeStamp = if (timeStamp != null) {
                            SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()).format(timeStamp)
                        } else {
                            "Waktu tidak ditentukan"
                        }

                        val time_process = laporan.data?.time_processed?.toTimestamp()?.toDate()
                        val formatted_time_process = if (time_process != null) {
                            SimpleDateFormat("dd MMMM yyyy HH:mm", Locale.getDefault()).format(time_process)
                        } else {
                            "Waktu tidak ditentukan"
                        }

                        val time_start = laporan.data?.time_repair_started?.toTimestamp()?.toDate()
                        val formatted_time_start = if (time_start != null) {
                            SimpleDateFormat("dd MMMM yyyy HH:mm", Locale.getDefault()).format(time_start)
                        } else {
                            "Waktu tidak ditentukan"
                        }


                        val time_finish = laporan.data?.time_repair_finished?.toTimestamp()?.toDate()
                        val formatted_time_finish = if (time_finish != null) {
                            SimpleDateFormat("dd MMMM yyyy HH:mm", Locale.getDefault()).format(time_finish)
                        } else {
                            "Waktu tidak ditentukan"
                        }

                        val time_closed = laporan.data?.time_repair_closed?.toTimestamp()?.toDate()
                        val formatted_time_closed = if (time_closed != null) {
                            SimpleDateFormat("dd MMMM yyyy HH:mm", Locale.getDefault()).format(time_closed)
                        } else {
                            "Waktu tidak ditentukan"
                        }

                        tvDateReported.text = formattedTimeStamp
                        tvInfo.text = "Laporan ditutup pada $formatted_time_closed"
                        tvNoReferensi.text = laporan.data?.idLaporan
                        tvDaerah.text = laporan.data?.daerah
                        if(laporan?.data?.daerah == "Tanjungpinang"){
                            tvPelapor.text = "SI-TP${laporan.data?.idCustomer}"
                        }
                        else{
                            tvPelapor.text = "SI-BTN${laporan.data?.idCustomer}"
                        }
                        tvJenisGangguan.text = laporan.data?.kategori
                        tvAlamatGangguan.text = laporan.data?.alamat
                        tvDeskripsiGangguan.text = laporan.data?.deskripsi

                        tvSolusi.text = laporan.data?.proposed_solution
                        tvTechID.text = "${laporan.data?.idTeknisi} - ${laporan.data?.teknisi}"
                        tvProses.text = "$formatted_time_process"
                        tvStart.text = "$formatted_time_start"
                        tvFinish.text = "$formatted_time_finish"
                        tvClosed.text = "$formatted_time_closed"
                        tvFinalSolution.text = laporan.data?.solution

                        progressBar.visibility = View.GONE

                    }

                }
                is State.Error -> {

                    binding.progressBar.visibility = View.GONE
                }
                is State.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                else -> {}
            }
        }

        binding.btnConfirm.setOnClickListener {
            finish()
        }
    }
}