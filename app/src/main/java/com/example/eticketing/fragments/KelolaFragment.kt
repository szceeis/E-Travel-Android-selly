package com.example.eticketing.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.eticketing.activities.AdminTiketActivity
import com.example.eticketing.activities.KategoriActivity
import com.example.eticketing.activities.KelolaDestinasiActivity
import com.example.eticketing.activities.KelolaUserActivity
import com.example.eticketing.databinding.FragmentKelolaBinding

class KelolaFragment : Fragment() {

    private var _binding: FragmentKelolaBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentKelolaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cardDestinasi.setOnClickListener {
            startActivity(Intent(requireContext(), KelolaDestinasiActivity::class.java))
        }
        binding.cardUser.setOnClickListener {
            startActivity(Intent(requireContext(), KelolaUserActivity::class.java))
        }
        binding.cardTiket.setOnClickListener {
            startActivity(Intent(requireContext(), AdminTiketActivity::class.java))
        }
        binding.cardKategori.setOnClickListener {
            startActivity(Intent(requireContext(), KategoriActivity::class.java))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}