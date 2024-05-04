package com.einz.solnetTech.ui.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.einz.solnetTech.R
import com.einz.solnetTech.data.di.ViewModelFactory
import com.einz.solnetTech.databinding.ActivityHomeBinding
import com.einz.solnetTech.data.Result
import com.einz.solnetTech.ui.report.LaporanActivity
import com.einz.solnetTech.ui.setting.SettingActivity

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var factory: ViewModelFactory
    private val viewModel: HomeViewModel by viewModels{factory}

    private var idTeknisiString: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        factory = ViewModelFactory.getInstance(this)

        viewModel.getTeknisi()

        viewModel.teknisiLiveData.observe(this){
            result ->
            when(result){
                is Result.Success -> {
                    binding.apply{
                        val nameTeknisi = result.data?.namaTeknisi
                        val idTech = result.data?.idTeknisi
                        val daerahTeknisi = result.data?.daerahTeknisi
                        val teleponTeknisi = result.data?.noTelpTeknisi

                        username.text = "$nameTeknisi"
                        idTeknisi.text = "ID TEKNISI: $idTech"
                        daerah.text = " DAERAH: $daerahTeknisi"
                        telepon.text = "NO TELP: $teleponTeknisi"

                        progressBar.visibility = View.GONE
                        idTeknisiString = idTech!!

                    }
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                }
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                else -> {}
            }
        }

        viewModel.getLaporan()

        viewModel.laporanLiveData.observe(this) { result ->
            when (result) {
                is Result.Success -> {

                    binding.progressBar.visibility = View.GONE

                        val adapterLaporan = LaporanAdapter(onClick = { laporan ->
                            val intent = Intent(this, LaporanActivity::class.java)
                            intent.putExtra(LaporanActivity.EXTRA_LAPORAN, laporan.idLaporan)
                            intent.putExtra(LaporanActivity.EXTRA_TEKNISI, idTeknisiString )
                            startActivity(intent)
                        })

                        with(binding.rvLaporan) {
                            layoutManager = LinearLayoutManager(this@HomeActivity)
                            setHasFixedSize(true)
                            this.adapter = adapterLaporan

                        }
                    adapterLaporan.submitList(null)
                    adapterLaporan.submitList(result.data)


                }

                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                }

                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                else -> {}
            }
        }

        binding.fabSetting.setOnClickListener {
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }
    }
}