package com.example.eticketing.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eticketing.adapters.KategoriAdapter
import com.example.eticketing.data.AppDatabase
import com.example.eticketing.data.Category
import com.example.eticketing.databinding.ActivityKategoriBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class KategoriActivity : BaseActivity() {

    private lateinit var binding: ActivityKategoriBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKategoriBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupBackButton("Kelola Kategori")

        val db = AppDatabase.getDatabase(this)
        val categoryDao = db.categoryDao()

        val adapter = KategoriAdapter(
            onDelete = { category ->
                AlertDialog.Builder(this)
                    .setTitle("Hapus Kategori")
                    .setMessage("Yakin hapus kategori \"${category.name}\"?")
                    .setPositiveButton("Hapus") { _, _ ->
                        lifecycleScope.launch {
                            withContext(Dispatchers.IO) { categoryDao.delete(category) }
                        }
                    }
                    .setNegativeButton("Batal", null)
                    .show()
            }
        )

        binding.rvKategori.layoutManager = LinearLayoutManager(this)
        binding.rvKategori.adapter = adapter

        lifecycleScope.launch {
            categoryDao.getAllCategories().collect { list ->
                adapter.submitList(list)
            }
        }

        binding.btnTambahKategori.setOnClickListener {
            val nama = binding.etKategoriNama.text.toString().trim()
            val emoji = binding.etKategoriEmoji.text.toString().trim()

            if (nama.isEmpty()) {
                Toast.makeText(this, "Nama kategori tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    categoryDao.insert(
                        Category(
                            name = nama,
                            emoji = if (emoji.isEmpty()) "🏷️" else emoji
                        )
                    )
                }
                withContext(Dispatchers.Main) {
                    binding.etKategoriNama.setText("")
                    binding.etKategoriEmoji.setText("")
                    Toast.makeText(this@KategoriActivity, "Kategori berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}