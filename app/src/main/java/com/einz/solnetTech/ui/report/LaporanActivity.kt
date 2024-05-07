package com.einz.solnetTech.ui.report

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog.*
import com.einz.solnetTech.data.di.ViewModelFactory
import com.einz.solnetTech.databinding.ActivityLaporanBinding
import com.einz.solnetTech.data.State
import java.text.SimpleDateFormat
import java.util.Locale

class LaporanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLaporanBinding
    private lateinit var factory: ViewModelFactory

    private val viewModel : LaporanViewModel by viewModels{factory}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLaporanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        factory = ViewModelFactory.getInstance(this)

        val idLaporanString = intent.getStringExtra(EXTRA_LAPORAN)
        val idTeknisiString = intent.getIntExtra(EXTRA_TEKNISI, 0)

        let{
            viewModel.getLaporan(idLaporanString!!)
        }

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

                        tvDateReported.text = formattedTimeStamp
                        tvNoReferensi.text = laporan.data?.idLaporan
                        tvInfo.text = "Perbaikan diharapkan pada $formattedDesiredTime"
                        tvJenisGangguan.text = laporan.data?.kategori
                        tvAlamatGangguan.text = laporan.data?.alamat
                        tvDeskripsiGangguan.text = laporan.data?.deskripsi

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
            val builder = Builder(this@LaporanActivity)
            builder.setTitle("Konfirmasi Ambil Ticket Gangguan")
            builder.setMessage("Apakah anda yakin mengambil ticket Gangguan ini?")

            // Set a positive button (Yes action)
            builder.setPositiveButton("Iya") { _: DialogInterface, _: Int ->
                viewModel.takeLaporan(idLaporanString!!,1, idTeknisiString!!)
            }

            // Set a negative button (Cancel action)
            builder.setNegativeButton("Batal") { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
            }

            // Create and show the dialog
            val dialog = builder.create()
            dialog.show()

        }

        viewModel.takeLaporanLiveData.observe(this){
            when(it){
                is State.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val intent = Intent(this, ActiveLaporanActivity::class.java)
                    startActivity(intent)
                    finish()

                }
                is State.Error -> {
                    binding.progressBar.visibility = View.GONE
                }
                is State.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
            }

        }
    }

    companion object {
        const val EXTRA_LAPORAN = "idLaporan"
        const val EXTRA_TEKNISI = "idTeknisi"
    }
}