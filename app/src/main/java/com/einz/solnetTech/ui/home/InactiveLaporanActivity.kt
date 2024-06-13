package com.einz.solnetTech.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.einz.solnetTech.data.State
import com.einz.solnetTech.data.di.ViewModelFactory
import com.einz.solnetTech.databinding.ActivityInactiveLaporanBinding
import com.einz.solnetTech.ui.report.LaporanActivity
import com.einz.solnetTech.ui.report.LaporanReport

class InactiveLaporanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInactiveLaporanBinding
    private lateinit var factory: ViewModelFactory
    private val viewModel: HomeViewModel by viewModels{factory}

    private var idTeknisiValue: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInactiveLaporanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        factory = ViewModelFactory.getInstance(this)

        val idTech = intent.getIntExtra("idTech", 0)

        viewModel.getFinishedLaporan(idTech)

        viewModel.finishedLaporanLiveData.observe(this) { result ->
            when (result) {
                is State.Success -> {

                    binding.progressBar.visibility = View.GONE

                    val adapterLaporan = LaporanAdapter(onClick = { laporan ->
                        val intent = Intent(this, LaporanReport::class.java)
                        intent.putExtra(LaporanActivity.EXTRA_LAPORAN, laporan.idLaporan)
                        intent.putExtra(LaporanActivity.EXTRA_TEKNISI, idTeknisiValue )
                        startActivity(intent)
                    })

                    with(binding.rvHistory) {
                        layoutManager = LinearLayoutManager(this@InactiveLaporanActivity)
                        setHasFixedSize(true)
                        this.adapter = adapterLaporan

                    }
                    adapterLaporan.submitList(null)
                    adapterLaporan.submitList(result.data)


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
    }
}