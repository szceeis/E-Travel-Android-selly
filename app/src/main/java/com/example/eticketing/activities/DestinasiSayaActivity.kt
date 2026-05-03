package com.example.eticketing.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eticketing.adapters.DestinationPengelolaAdapter
import com.example.eticketing.data.AppDatabase
import com.example.eticketing.databinding.ActivityDestinasiSayaBinding
import kotlinx.coroutines.launch

class DestinasiSayaActivity : BaseActivity() {

    private lateinit var binding: ActivityDestinasiSayaBinding
    private lateinit var adapter: DestinationPengelolaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDestinasiSayaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupBackButton("Destinasi Saya")

        val pengelolaId = intent.getLongExtra("pengelolaId", -1L)
        val db = AppDatabase.getDatabase(this)

        adapter = DestinationPengelolaAdapter(
            onEdit = { destination ->
                val intent = Intent(this, FormDestinasiActivity::class.java)
                intent.putExtra("destinationId", destination.id)
                intent.putExtra("isPengelola", true)
                startActivity(intent)
            }
        )

        binding.rvDestinasiSaya.layoutManager = LinearLayoutManager(this)
        binding.rvDestinasiSaya.adapter = adapter

        lifecycleScope.launch {
            db.destinationDao().getDestinationsByPengelola(pengelolaId).collect { list ->
                adapter.submitList(list)
                binding.tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
            }
        }
    }
}