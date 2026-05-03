package com.example.eticketing.fragments

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eticketing.activities.DetailDestinasiActivity
import com.example.eticketing.adapters.DestinationUserAdapter
import com.example.eticketing.data.AppDatabase
import com.example.eticketing.data.Destination
import com.example.eticketing.databinding.FragmentDestinationsBinding
import kotlinx.coroutines.launch

class DestinationsFragment : Fragment() {

    private var _binding: FragmentDestinationsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: DestinationUserAdapter
    private var allDestinations: List<Destination> = emptyList()
    private var activeCategory: String = "Semua"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDestinationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = AppDatabase.getDatabase(requireContext())

        adapter = DestinationUserAdapter { destination ->
            val intent = Intent(requireContext(), DetailDestinasiActivity::class.java)
            intent.putExtra("destinationId", destination.id)
            startActivity(intent)
        }

        binding.rvDestinasi.layoutManager = LinearLayoutManager(requireContext())
        binding.rvDestinasi.adapter = adapter

        val filterCategory = arguments?.getString("filterCategory")
        if (filterCategory != null) activeCategory = filterCategory

        // Load kategori dari database secara dinamis
        viewLifecycleOwner.lifecycleScope.launch {
            db.categoryDao().getAllCategories().collect { categories ->
                buildFilterButtons(categories.map { "${it.emoji} ${it.name}" to it.name })
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            db.destinationDao().getAllDestinations().collect { list ->
                allDestinations = list.filter { it.pengelolaId != 0L }
                applyFilter()
            }
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { applyFilter() }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun buildFilterButtons(categories: List<Pair<String, String>>) {
        val container = binding.layoutFilter
        container.removeAllViews()

        val allBtn = makeFilterButton("Semua", "Semua")
        container.addView(allBtn)

        categories.forEach { (label, value) ->
            container.addView(makeFilterButton(label, value))
        }

        highlightButton(container, activeCategory)
    }

    private fun makeFilterButton(label: String, value: String): Button {
        return Button(requireContext()).apply {
            text = label
            textSize = 12f
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { marginEnd = 8 }
            layoutParams = params
            backgroundTintList = ColorStateList.valueOf(
                if (value == activeCategory) Color.parseColor("#1565C0")
                else Color.TRANSPARENT
            )
            setTextColor(
                if (value == activeCategory) Color.WHITE
                else Color.parseColor("#1565C0")
            )
            setOnClickListener {
                activeCategory = value
                highlightButton(binding.layoutFilter, value)
                applyFilter()
            }
        }
    }

    private fun highlightButton(container: LinearLayout, active: String) {
        for (i in 0 until container.childCount) {
            val btn = container.getChildAt(i) as? Button ?: continue
            val isActive = (i == 0 && active == "Semua") ||
                    btn.text.toString().contains(active, ignoreCase = true)
            btn.backgroundTintList = ColorStateList.valueOf(
                if (isActive) Color.parseColor("#1565C0") else Color.TRANSPARENT
            )
            btn.setTextColor(if (isActive) Color.WHITE else Color.parseColor("#1565C0"))
        }
    }

    private fun applyFilter() {
        val query = binding.etSearch.text.toString().trim().lowercase()
        var filtered = allDestinations
        if (activeCategory != "Semua") {
            filtered = filtered.filter { it.category.equals(activeCategory, ignoreCase = true) }
        }
        if (query.isNotEmpty()) {
            filtered = filtered.filter {
                it.name.lowercase().contains(query) || it.location.lowercase().contains(query)
            }
        }
        adapter.submitList(filtered)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}