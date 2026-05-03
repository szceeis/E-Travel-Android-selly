package com.example.eticketing.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eticketing.R
import com.example.eticketing.activities.DetailDestinasiActivity
import com.example.eticketing.activities.LoginActivity
import com.example.eticketing.adapters.DestinationUserAdapter
import com.example.eticketing.data.AppDatabase
import com.example.eticketing.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = requireContext().getSharedPreferences("session", Context.MODE_PRIVATE)
        val nama = prefs.getString("userName", "Traveler")
        _binding?.tvGreeting?.text = "Halo, $nama! 👋"

        val db = AppDatabase.getDatabase(requireContext())

        val adapter = DestinationUserAdapter { destination ->
            val intent = Intent(requireContext(), DetailDestinasiActivity::class.java)
            intent.putExtra("destinationId", destination.id)
            startActivity(intent)
        }

        _binding?.rvPopuler?.layoutManager = LinearLayoutManager(requireContext())
        _binding?.rvPopuler?.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            db.destinationDao().getAllDestinations().collect { list ->
                if (_binding == null) return@collect

                val available = list.filter { it.pengelolaId != 0L }
                if (available.isEmpty()) {
                    _binding?.tvEmptyDestinasi?.visibility = View.VISIBLE
                    _binding?.rvPopuler?.visibility = View.GONE
                } else {
                    _binding?.tvEmptyDestinasi?.visibility = View.GONE
                    _binding?.rvPopuler?.visibility = View.VISIBLE
                    adapter.submitList(available.take(5))
                }
            }
        }

        _binding?.cardPantai?.setOnClickListener { navigateToDestinasi("Pantai") }
        _binding?.cardGunung?.setOnClickListener { navigateToDestinasi("Gunung") }
        _binding?.cardKota?.setOnClickListener { navigateToDestinasi("Kota") }

        _binding?.tvLihatSemua?.setOnClickListener {
            findNavController().navigate(R.id.nav_destinasi)
        }

        _binding?.btnLogout?.setOnClickListener {
            prefs.edit().clear().apply()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }
    }

    private fun navigateToDestinasi(category: String) {
        val bundle = Bundle().apply { putString("filterCategory", category) }
        findNavController().navigate(R.id.nav_destinasi, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}