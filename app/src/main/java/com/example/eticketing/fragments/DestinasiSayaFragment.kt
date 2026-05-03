package com.example.eticketing.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eticketing.activities.FormDestinasiActivity
import com.example.eticketing.adapters.DestinationPengelolaAdapter
import com.example.eticketing.data.AppDatabase
import com.example.eticketing.databinding.FragmentDestinasiSayaBinding
import kotlinx.coroutines.launch

class DestinasiSayaFragment : Fragment() {

    private var _binding: FragmentDestinasiSayaBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDestinasiSayaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = requireContext().getSharedPreferences("session", Context.MODE_PRIVATE)
        val pengelolaId = prefs.getLong("userId", -1L)
        val db = AppDatabase.getDatabase(requireContext())

        val adapter = DestinationPengelolaAdapter(
            onEdit = { destination ->
                val intent = Intent(requireContext(), FormDestinasiActivity::class.java)
                intent.putExtra("destinationId", destination.id)
                intent.putExtra("isPengelola", true)
                startActivity(intent)
            }
        )

        binding.rvDestinasiSaya.layoutManager = LinearLayoutManager(requireContext())
        binding.rvDestinasiSaya.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            db.destinationDao().getDestinationsByPengelola(pengelolaId).collect { list ->
                adapter.submitList(list)
                binding.tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}