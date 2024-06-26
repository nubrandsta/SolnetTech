package com.einz.solnetTech.ui.report

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.einz.solnetTech.R
import com.einz.solnetTech.data.di.ViewModelFactory
import com.einz.solnetTech.databinding.ActivityActiveLaporanBinding
import com.einz.solnetTech.data.State
import com.einz.solnetTech.ui.home.HomeActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.text.SimpleDateFormat
import java.util.Locale
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import com.einz.solnetTech.ui.util.phoneValidator

class ActiveLaporanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityActiveLaporanBinding
    private lateinit var factory : ViewModelFactory

    private val viewModel : LaporanViewModel by viewModels{factory}

    private lateinit var idLaporan : String
    private lateinit var activeLaporan : String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityActiveLaporanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val scope = CoroutineScope(Dispatchers.Default)

        factory = ViewModelFactory.getInstance(this)


//        viewModel.laporanLiveData.removeObserver(laporanDoneObserver)
        viewModel.resetLaporan()

        checkInput()


        viewModel.getActiveLaporan()
        viewModel.activeLaporanLiveData.observe(this){
            result ->
            when(result){
                is State.Success -> {
                    binding.progressBar.visibility = View.GONE
                    activeLaporan = result.data.toString()
                    viewModel.getLaporan(result.data.toString())

                }
                is State.Error -> {
//                    binding.progressBar.visibility = View.GONE
//                    val intent = Intent(this, HomeActivity::class.java)
//                    startActivity(intent)
//                    finish()

                }
                is State.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE

                }
            }
        }

        viewModel.laporanLiveData.observe(this){
                result ->
            when(result){
                is State.Success -> {
                    idLaporan = result.data?.idLaporan.toString()
                    binding.apply{
                        val desiredTime = result.data?.desiredTime?.toTimestamp()?.toDate()
                        val formattedDesiredTime = if (desiredTime != null) {
                            SimpleDateFormat("dd MMM HH:mm", Locale.getDefault()).format(desiredTime)
                        } else {
                            "Waktu tidak ditentukan"
                        }

                        val timeStamp = result.data?.timestamp?.toTimestamp()?.toDate()
                        val formattedTimeStamp = if (timeStamp != null) {
                            SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()).format(timeStamp)
                        } else {
                            "Waktu tidak ditentukan"
                        }

                        val time_process = result.data?.time_processed?.toTimestamp()?.toDate()
                        val formatted_time_process = if (time_process != null) {
                            SimpleDateFormat("dd MMMM yyyy HH:mm", Locale.getDefault()).format(time_process)
                        } else {
                            "Waktu tidak ditentukan"
                        }

                        val time_start = result.data?.time_repair_started?.toTimestamp()?.toDate()
                        val formatted_time_start = if (time_start != null) {
                            SimpleDateFormat("dd MMMM yyyy HH:mm", Locale.getDefault()).format(time_start)
                        } else {
                            "Waktu tidak ditentukan"
                        }


                        val time_finish = result.data?.time_repair_finished?.toTimestamp()?.toDate()
                        val formatted_time_finish = if (time_finish != null) {
                            SimpleDateFormat("dd MMMM yyyy HH:mm", Locale.getDefault()).format(time_finish)
                        } else {
                            "Waktu tidak ditentukan"
                        }

                        val time_closed = result.data?.time_repair_closed?.toTimestamp()?.toDate()
                        val formatted_time_closed = if (time_closed != null) {
                            SimpleDateFormat("dd MMMM yyyy HH:mm", Locale.getDefault()).format(time_closed)
                        } else {
                            "Waktu tidak ditentukan"
                        }

                        binding.fabChat.setOnClickListener {
                            var customerPhone = result.data?.customerPhone
                            customerPhone = phoneValidator(this@ActiveLaporanActivity,customerPhone!!)


                            try {
                                // Check if WhatsApp is installed
                                packageManager.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA)

                                // Start WhatsApp chat
                                val intent = Intent(Intent.ACTION_VIEW)
                                val whatsappUrl = "https://wa.me/$customerPhone" // WhatsApp's URL scheme
                                intent.data = Uri.parse(whatsappUrl)
                                startActivity(intent)
                            } catch (e: PackageManager.NameNotFoundException) {
                                // WhatsApp not installed, open Contacts app to add a new contact
                                val intent = Intent(Intent.ACTION_INSERT, ContactsContract.Contacts.CONTENT_URI).apply {
                                    putExtra(ContactsContract.Intents.Insert.PHONE, customerPhone)
                                    putExtra(ContactsContract.Intents.Insert.NAME, "Pelanggan")
                                }
                                startActivity(intent)
                            }
                        }

                        tvDateReported.text = formattedTimeStamp
                        tvNoReferensi.text = result.data?.idLaporan
                        tvInfo.text = "Perbaikan diharapkan pada $formattedDesiredTime"
                        tvJenisGangguan.text = result.data?.kategori
                        tvAlamatGangguan.text = result.data?.alamat
                        tvDeskripsiGangguan.text = result.data?.deskripsi

                        when(result.data?.status){

                            1 -> {
                                binding.apply{
                                    view1.visibility = View.GONE
                                    tvInfo.visibility = View.GONE

                                    fabChat.visibility = View.VISIBLE
                                    btnConfirm.visibility = View.VISIBLE
                                    etSolution.visibility = View.GONE
                                    binding.btnConfirm.isEnabled = true
                                    binding.btnConfirm.error = null
                                    btnConfirm.text = "Konfirmasi Perbaikan Selesai"

                                    binding.btnConfirm.setOnClickListener {
                                        val dialog = androidx.appcompat.app.AlertDialog.Builder(this@ActiveLaporanActivity)
                                        dialog.setTitle("Konfirmasi Perbaikan")
                                        dialog.setMessage("Apakah anda yakin ingin melakukan konfirmasi perbaikan selesai?")
                                        dialog.setPositiveButton("Ya") { _, _ ->
                                            viewModel.updateLaporanStatus(idLaporan,2, "")
                                            Toast.makeText(this@ActiveLaporanActivity,"Menunggu Konfirmasi Pelanggan", Toast.LENGTH_SHORT).show()

                                        }
                                        dialog.setNegativeButton("Tidak") { _, _ ->}
                                        dialog.show()
                                    }

                                    tvTeknisi.text = "Laporan sedang ditangani oleh Anda"
                                    tvTeknisiDesc.text = "Konfirmasi perbaikan setelah perbaikan selesai"

                                    dotProgress0.setBackgroundResource(R.drawable.progress_dot)
                                    tvProgress0.text = "Laporan sudah diproses"

                                    dotProgress1.setBackgroundResource(R.drawable.progress_dot)
                                    tvProgress1.text = "Perbaikan sedang dilakukan"

                                    dotProgress2.setBackgroundResource(R.drawable.progress_dot_inactive)
                                    tvProgress2.text = "Konfirmasi Perbaikan"

                                    dotProgress3.setBackgroundResource(R.drawable.progress_dot_inactive)
                                    tvProgress3.text = "Perbaikan Selesai"

                                    btnConfirm.visibility = View.VISIBLE

                                }
                            }
                            2 -> {
                                binding.apply{
                                    view1.visibility = View.GONE
                                    tvInfo.visibility = View.GONE

                                    fabChat.visibility = View.VISIBLE
                                    btnConfirm.visibility = View.VISIBLE
                                    btnConfirm.isEnabled = false
                                    etSolution.visibility = View.VISIBLE
                                    btnConfirm.text = "Konfirmasi Perbaikan Selesai"



                                    binding.btnConfirm.setOnClickListener {
                                        val dialog = androidx.appcompat.app.AlertDialog.Builder(this@ActiveLaporanActivity)
                                        dialog.setTitle("Konfirmasi Perbaikan")
                                        dialog.setMessage("Apakah anda yakin ingin melakukan konfirmasi perbaikan selesai?")
                                        dialog.setPositiveButton("Ya") { _, _ ->
                                            val solution = etSolution.text?.trim().toString()
                                            viewModel.updateLaporanStatus(idLaporan,3, solution)
                                            Toast.makeText(this@ActiveLaporanActivity, solution, Toast.LENGTH_SHORT).show()
                                            Toast.makeText(this@ActiveLaporanActivity,"Menunggu Konfirmasi Pelanggan", Toast.LENGTH_SHORT).show()

                                        }
                                        dialog.setNegativeButton("Tidak") { _, _ ->}
                                        dialog.show()
                                    }

                                    tvTeknisi.text = "Proses Perbaikan dimulai, Konfirmasi setelah selesai"
                                    tvTeknisiDesc.text = "Solusi yang disarankan CS: ${result.data?.proposed_solution}"

                                    dotProgress0.setBackgroundResource(R.drawable.progress_dot)
                                    tvProgress0.text = "Laporan sudah diproses"
                                    tvProgress0Timestamp.text = formatted_time_process

                                    dotProgress1.setBackgroundResource(R.drawable.progress_dot)
                                    tvProgress1.text = "Perbaikan sedang dilakukan"
                                    tvProgress1Timestamp.text = formatted_time_start

                                    dotProgress2.setBackgroundResource(R.drawable.progress_dot_inactive)
                                    tvProgress2.text = "Menunggu konfirmasi"

                                    dotProgress3.setBackgroundResource(R.drawable.progress_dot_inactive)
                                    tvProgress3.text = "Perbaikan Selesai"

                                }

                            }
                            3 -> {
                                binding.apply{
                                    view1.visibility = View.GONE
                                    tvInfo.visibility = View.GONE

                                    fabChat.visibility = View.VISIBLE
                                    btnConfirm.visibility = View.GONE
                                    etSolution.visibility = View.GONE
                                    tfLayoutSolution.visibility = View.GONE
                                    binding.btnConfirm.isEnabled = true
                                    binding.btnConfirm.error = null

                                    tvTeknisi.text = "Perbaikan telah anda konfirmasi"
                                    tvTeknisiDesc.text = "Menunggu Konfirmasi Perbaikan dari Pelanggan"

                                    dotProgress0.setBackgroundResource(R.drawable.progress_dot)
                                    tvProgress0.text = "Laporan sudah diproses"
                                    tvProgress0Timestamp.text = formatted_time_process

                                    dotProgress1.setBackgroundResource(R.drawable.progress_dot)
                                    tvProgress1.text = "Perbaikan sudah selesai"
                                    tvProgress1Timestamp.text = formatted_time_start

                                    dotProgress2.setBackgroundResource(R.drawable.progress_dot)
                                    tvProgress2.text = "Perbaikan dikonfirmasi"
                                    tvProgress2Timestamp.text = formatted_time_finish

                                    dotProgress3.setBackgroundResource(R.drawable.progress_dot_inactive)
                                    tvProgress3.text = "Perbaikan Selesai"

                                }

                            }
                            4 -> {
                                binding.apply{
                                    view1.visibility = View.GONE
                                    tvInfo.visibility = View.GONE

                                    fabChat.visibility = View.GONE
                                    btnConfirm.visibility = View.VISIBLE
                                    etSolution.visibility = View.GONE
                                    tfLayoutSolution.visibility = View.GONE
                                    binding.btnConfirm.isEnabled = true
                                    binding.btnConfirm.error = null

                                    tvTeknisi.text = "Perbaikan Telah Dikonfirmasi Pelanggan"
                                    tvTeknisiDesc.text = "Tiket Laporan ditutup"

                                    dotProgress0.setBackgroundResource(R.drawable.progress_dot)
                                    tvProgress0.text = "Laporan sudah diproses"
                                    tvProgress0Timestamp.text = formatted_time_process

                                    dotProgress1.setBackgroundResource(R.drawable.progress_dot)
                                    tvProgress1.text = "Perbaikan sudah selesai"
                                    tvProgress1Timestamp.text = formatted_time_start

                                    dotProgress2.setBackgroundResource(R.drawable.progress_dot)
                                    tvProgress2.text = "Perbaikan dikonfirmasi"
                                    tvProgress2Timestamp.text = formatted_time_finish

                                    dotProgress3.setBackgroundResource(R.drawable.progress_dot)
                                    tvProgress3.text = "Perbaikan Selesai"
                                    tvProgress3Timestamp.text = formatted_time_closed

                                    viewModel.resetLaporan()

                                    Toast.makeText(this@ActiveLaporanActivity,"Berhasil! 👍", Toast.LENGTH_SHORT).show()

                                    binding.btnConfirm.visibility = View.VISIBLE
                                    binding.btnConfirm.text = "Laman Utama"
                                    binding.btnConfirm.setOnClickListener() {
                                        val intent = Intent(this@ActiveLaporanActivity, HomeActivity::class.java)
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                        startActivity(intent)
                                        finish()
                                    }


//                                    if(result.data.idLaporan == activeLaporan){
//                                        val dialog = androidx.appcompat.app.AlertDialog.Builder(this@ActiveLaporanActivity)
//                                        dialog.setTitle("Perbaikan Selesai")
//                                        dialog.setMessage("Perbaikan selesai dikonfirmasi sistem 👍")
//                                        dialog.setPositiveButton("OK") { _, _ ->
//                                            val intent = Intent(this@ActiveLaporanActivity, HomeActivity::class.java)
//                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
//                                            startActivity(intent)
//                                            finish()
//                                        }
//                                        dialog.setOnDismissListener() {
//                                            val intent = Intent(this@ActiveLaporanActivity, HomeActivity::class.java)
//                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
//                                            startActivity(intent)
//                                            finish()
//                                        }
//                                        dialog.show()
//                                    }
                                }
                            }
                        }


                        progressBar.visibility = View.GONE
                    }

                }
                is State.Error -> {

                }
                is State.Loading -> {

                }
            }
        }
    }
    private fun checkValid(){
        val solution = binding.etSolution.text.toString()

        if(solution.isNotEmpty()){
            binding.btnConfirm.isEnabled = true
            binding.btnConfirm.error = null
        }
        else{
            binding.btnConfirm.isEnabled = false
            binding.btnConfirm.error = "Isi Solusi Perbaikan!"
        }
    }
    private fun checkInput(){
        binding.etSolution.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(string: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(string: CharSequence?, start: Int, before: Int, count: Int) {
                checkValid()
            }
            override fun afterTextChanged(string: Editable?) {}
        })

    }
}