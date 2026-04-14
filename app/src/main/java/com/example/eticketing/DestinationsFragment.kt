package com.example.eticketing

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eticketing.data.AppDatabase
import com.example.eticketing.data.Ticket
import com.example.eticketing.databinding.FragmentDestinationsBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DestinationsFragment : Fragment() {
    private var _binding: FragmentDestinationsBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: AppDatabase
    private lateinit var adapter: DestinationAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDestinationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = AppDatabase.getDatabase(requireContext())

        val sharedPref = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val role = sharedPref.getString("role", "USER")
        val userId = sharedPref.getLong("user_id", -1)

        if (role == "ADMIN") {
            binding.fabAddDestination.visibility = View.VISIBLE
        }

        binding.fabAddDestination.setOnClickListener {
            startActivity(Intent(requireContext(), AddDestinationActivity::class.java))
        }

        adapter = DestinationAdapter(
            emptyList(),
            { destination ->
                if (userId != -1L) {
                    lifecycleScope.launch {
                        val ticket = Ticket(
                            userId = userId,
                            destinationId = destination.id,
                            bookingDate = System.currentTimeMillis(),
                            quantity = 1,
                            totalPrice = destination.price,
                            status = "CONFIRMED"
                        )
                        db.ticketDao().bookTicket(ticket)
                        Toast.makeText(requireContext(), "Tiket untuk ${destination.name} berhasil dipesan!", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            role ?: "USER"
        )

        binding.rvDestinations.layoutManager = LinearLayoutManager(requireContext())
        binding.rvDestinations.adapter = adapter

        lifecycleScope.launch {
            db.destinationDao().getAllDestinations().collectLatest { list ->
                adapter.updateData(list)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
