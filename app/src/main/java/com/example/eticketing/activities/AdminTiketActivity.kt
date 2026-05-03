package com.example.eticketing.activities

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eticketing.adapters.TiketAdminAdapter
import com.example.eticketing.data.AppDatabase
import com.example.eticketing.databinding.ActivityAdminTiketBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdminTiketActivity : BaseActivity() {

    private lateinit var binding: ActivityAdminTiketBinding
    private lateinit var adapter: TiketAdminAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminTiketBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupBackButton("Pantau Tiket")

        val db = AppDatabase.getDatabase(this)

        // Admin hanya pantau — tombol dikosongkan
        adapter = TiketAdminAdapter(
            onKonfirmasi = {},
            onBatal = {},
            isReadOnly = true  // ← tambah flag ini
        )

        binding.rvTiket.layoutManager = LinearLayoutManager(this)
        binding.rvTiket.adapter = adapter

        lifecycleScope.launch {
            val users = withContext(Dispatchers.IO) { db.userDao().getAllUsers() }
            val userMap = users.associate { it.id to it.nama }

            db.ticketDao().getAllTicketsAdmin().collect { tickets ->
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
            }
        }
    }
}