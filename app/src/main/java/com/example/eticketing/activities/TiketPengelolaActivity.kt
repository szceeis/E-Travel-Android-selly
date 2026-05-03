package com.example.eticketing.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eticketing.adapters.TiketAdminAdapter
import com.example.eticketing.data.AppDatabase
import com.example.eticketing.databinding.ActivityTiketPengelolaBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TiketPengelolaActivity : BaseActivity() {

    private lateinit var binding: ActivityTiketPengelolaBinding
    private lateinit var adapter: TiketAdminAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTiketPengelolaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupBackButton("Tiket Masuk")

        val pengelolaId = intent.getLongExtra("pengelolaId", -1L)
        val db = AppDatabase.getDatabase(this)

        // Pengelola bisa konfirmasi & batalkan — isReadOnly = false
        adapter = TiketAdminAdapter(
            onKonfirmasi = { ticket ->
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        db.ticketDao().updateStatus(ticket.id, "CONFIRMED")
                    }
                    Toast.makeText(this@TiketPengelolaActivity, "Tiket dikonfirmasi", Toast.LENGTH_SHORT).show()
                }
            },
            onBatal = { ticket ->
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        db.ticketDao().updateStatus(ticket.id, "CANCELLED")
                    }
                    Toast.makeText(this@TiketPengelolaActivity, "Tiket dibatalkan", Toast.LENGTH_SHORT).show()
                }
            },
            isReadOnly = false
        )

        binding.rvTiketPengelola.layoutManager = LinearLayoutManager(this)
        binding.rvTiketPengelola.adapter = adapter

        lifecycleScope.launch {
            val users = withContext(Dispatchers.IO) { db.userDao().getAllUsers() }
            val userMap = users.associate { it.id to it.nama }

            db.ticketDao().getTicketsByPengelola(pengelolaId).collect { tickets ->
                val destMap = mutableMapOf<Long, String>()
                withContext(Dispatchers.IO) {
                    tickets.forEach { ticket ->
                        if (!destMap.containsKey(ticket.destinationId)) {
                            val dest = db.destinationDao().getDestinationById(ticket.destinationId)
                            destMap[ticket.destinationId] = dest?.name ?: "Destinasi #${ticket.destinationId}"
                        }
                    }
                }
                adapter.userNames = userMap
                adapter.destinationNames = destMap
                adapter.submitList(tickets)
                binding.tvEmptyTiket.visibility = if (tickets.isEmpty()) View.VISIBLE else View.GONE
            }
        }
    }
}