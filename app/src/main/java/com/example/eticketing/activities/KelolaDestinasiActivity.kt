package com.example.eticketing.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eticketing.adapters.DestinationAdminAdapter
import com.example.eticketing.data.AppDatabase
import com.example.eticketing.databinding.ActivityKelolaDestinasiBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class KelolaDestinasiActivity : BaseActivity() {

    private lateinit var binding: ActivityKelolaDestinasiBinding
    private lateinit var adapter: DestinationAdminAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKelolaDestinasiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupBackButton("Kelola Destinasi")

        val db = AppDatabase.getDatabase(this)

        adapter = DestinationAdminAdapter(
            onEdit = { destination ->
                val intent = Intent(this, FormDestinasiActivity::class.java)
                intent.putExtra("destinationId", destination.id)
                startActivity(intent)
            },
            onDelete = { destination ->
                AlertDialog.Builder(this)
                    .setTitle("Hapus Destinasi")
                    .setMessage("Yakin ingin menghapus \"${destination.name}\"?")
                    .setPositiveButton("Hapus") { _, _ ->
                        lifecycleScope.launch {
                            withContext(Dispatchers.IO) {
                                db.destinationDao().deleteDestination(destination)
                            }
                        }
                    }
                    .setNegativeButton("Batal", null)
                    .show()
            }
        )

        binding.rvDestinasi.layoutManager = LinearLayoutManager(this)
        binding.rvDestinasi.adapter = adapter

        lifecycleScope.launch {
            // Load nama pengelola
            val pengelolaList = withContext(Dispatchers.IO) {
                db.userDao().getAllPengelola()
            }
            val pengelolaMap = pengelolaList.associate { it.id to it.nama }
            adapter.pengelolaNames = pengelolaMap

            db.destinationDao().getAllDestinations().collect { list ->
                adapter.submitList(list)
            }
        }

        binding.btnTambah.setOnClickListener {
            startActivity(Intent(this, FormDestinasiActivity::class.java))
        }
    }
}