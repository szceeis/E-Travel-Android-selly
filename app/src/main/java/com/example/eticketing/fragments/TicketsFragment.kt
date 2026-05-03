package com.example.eticketing.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eticketing.adapters.TiketUserAdapter
import com.example.eticketing.data.AppDatabase
import com.example.eticketing.databinding.FragmentTicketsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TicketsFragment : Fragment() {

    private var _binding: FragmentTicketsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTicketsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = requireActivity().getSharedPreferences("session", Context.MODE_PRIVATE)
        val userId = prefs.getLong("userId", -1L)
        val db = AppDatabase.getDatabase(requireContext())

        val adapter = TiketUserAdapter()

        binding.rvTiketSaya.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTiketSaya.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            db.ticketDao().getTicketsByUserId(userId).collect { tickets ->
                val destMap = mutableMapOf<Long, String>()
                withContext(Dispatchers.IO) {
                    tickets.forEach { ticket ->
                        if (!destMap.containsKey(ticket.destinationId)) {
                            val dest = db.destinationDao().getDestinationById(ticket.destinationId)
                            destMap[ticket.destinationId] = dest?.name ?: "Destinasi #${ticket.destinationId}"
                        }
                    }
                }

                adapter.destinationNames = destMap
                adapter.submitList(tickets)

                binding.tvEmptyTiket.visibility =
                    if (tickets.isEmpty()) View.VISIBLE else View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}