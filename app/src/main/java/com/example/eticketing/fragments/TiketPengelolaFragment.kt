package com.example.eticketing.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eticketing.adapters.TiketAdminAdapter
import com.example.eticketing.data.AppDatabase
import com.example.eticketing.databinding.FragmentTiketPengelolaBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TiketPengelolaFragment : Fragment() {

    private var _binding: FragmentTiketPengelolaBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTiketPengelolaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = requireContext().getSharedPreferences("session", Context.MODE_PRIVATE)
        val pengelolaId = prefs.getLong("userId", -1L)
        val db = AppDatabase.getDatabase(requireContext())

        val adapter = TiketAdminAdapter(
            onKonfirmasi = { ticket ->
                viewLifecycleOwner.lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        db.ticketDao().updateStatus(ticket.id, "CONFIRMED")
                    }
                    Toast.makeText(requireContext(), "Tiket dikonfirmasi", Toast.LENGTH_SHORT).show()
                }
            },
            onBatal = { ticket ->
                viewLifecycleOwner.lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        db.ticketDao().updateStatus(ticket.id, "CANCELLED")
                    }
                    Toast.makeText(requireContext(), "Tiket dibatalkan", Toast.LENGTH_SHORT).show()
                }
            },
            isReadOnly = false
        )

        _binding?.rvTiketPengelola?.layoutManager = LinearLayoutManager(requireContext())
        _binding?.rvTiketPengelola?.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            val users = withContext(Dispatchers.IO) { db.userDao().getAllUsers() }
            val userMap = users.associate { it.id to it.nama }

            db.ticketDao().getTicketsByPengelola(pengelolaId).collect { tickets ->
                if (_binding == null) return@collect

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

                _binding?.tvEmptyTiket?.visibility =
                    if (tickets.isEmpty()) View.VISIBLE else View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}