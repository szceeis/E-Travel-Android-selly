package com.example.eticketing.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.eticketing.data.AppDatabase
import com.example.eticketing.data.Category
import com.example.eticketing.data.Destination
import com.example.eticketing.data.User
import com.example.eticketing.databinding.ActivityFormDestinasiBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class FormDestinasiActivity : BaseActivity() {

    private lateinit var binding: ActivityFormDestinasiBinding
    private var editDestinationId: Long = -1L
    private var existingDestination: Destination? = null
    private var selectedImagePath: String? = null
    private var selectedPengelolaId: Long = 0L
    private var pengelolaList: List<User> = emptyList()
    private val isPengelola: Boolean get() = intent.getBooleanExtra("isPengelola", false)

    // Tambahan untuk kategori spinner
    private var selectedCategory: String = ""
    private var categoryList: List<Category> = emptyList()

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedImagePath = copyImageToInternal(uri)
                binding.tvPlaceholder.visibility = View.GONE
                Glide.with(this).load(uri).centerCrop().into(binding.ivPreview)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormDestinasiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        editDestinationId = intent.getLongExtra("destinationId", -1L)
        val db = AppDatabase.getDatabase(this)
        val destinationDao = db.destinationDao()

        // Sembunyikan spinner pengelola jika yang buka adalah pengelola
        if (isPengelola) {
            binding.spinnerPengelola.visibility = View.GONE
            binding.tvPengelolaInfo.visibility = View.GONE
            setupBackButton("Edit Destinasi")
        } else {
            setupBackButton(if (editDestinationId == -1L) "Tambah Destinasi" else "Edit Destinasi")
            loadPengelola(db)
        }

        if (editDestinationId != -1L) {
            binding.tvFormTitle.text = "Edit Destinasi"
            binding.btnSave.text = "UPDATE"

            lifecycleScope.launch {
                val dest = withContext(Dispatchers.IO) {
                    destinationDao.getDestinationById(editDestinationId)
                }
                dest?.let {
                    existingDestination = it
                    binding.etName.setText(it.name)
                    binding.etLocation.setText(it.location)
                    binding.etPrice.setText(it.price.toString())
                    binding.etDescription.setText(it.description)
                    selectedPengelolaId = it.pengelolaId
                    selectedImagePath = it.imageUrl

                    // Load kategori dengan current category dari data edit
                    loadCategories(db, it.category)

                    if (!it.imageUrl.isNullOrEmpty()) {
                        binding.tvPlaceholder.visibility = View.GONE
                        Glide.with(this@FormDestinasiActivity)
                            .load(File(it.imageUrl))
                            .centerCrop()
                            .into(binding.ivPreview)
                    }
                }
            }
        } else {
            // Mode tambah: load kategori tanpa pre-select
            loadCategories(db)
        }

        binding.ivPreview.setOnClickListener { openGallery() }
        binding.btnPilihGambar.setOnClickListener { openGallery() }

        binding.btnSave.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val location = binding.etLocation.text.toString().trim()
            val category = selectedCategory   // <-- diganti dari etCategory ke selectedCategory
            val priceStr = binding.etPrice.text.toString().trim()
            val description = binding.etDescription.text.toString().trim()

            if (name.isEmpty() || location.isEmpty() || category.isEmpty() || priceStr.isEmpty()) {
                Toast.makeText(this, "Harap isi semua field yang wajib", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val price = priceStr.toDoubleOrNull()
            if (price == null || price <= 0) {
                Toast.makeText(this, "Harga tidak valid", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    if (editDestinationId == -1L) {
                        destinationDao.insertDestination(
                            Destination(
                                name = name,
                                location = location,
                                category = category,
                                price = price,
                                description = description,
                                imageUrl = selectedImagePath,
                                pengelolaId = selectedPengelolaId
                            )
                        )
                    } else {
                        existingDestination?.let {
                            destinationDao.updateDestination(
                                it.copy(
                                    name = name,
                                    location = location,
                                    category = category,
                                    price = price,
                                    description = description,
                                    imageUrl = selectedImagePath ?: it.imageUrl,
                                    pengelolaId = if (isPengelola) it.pengelolaId
                                    else selectedPengelolaId
                                )
                            )
                        }
                    }
                }
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@FormDestinasiActivity,
                        if (editDestinationId == -1L) "Destinasi berhasil ditambahkan"
                        else "Destinasi berhasil diupdate",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            }
        }
    }

    private fun loadCategories(db: AppDatabase, currentCategory: String = "") {
        lifecycleScope.launch {
            db.categoryDao().getAllCategories().collect { categories ->
                categoryList = categories
                val names = categories.map { "${it.emoji} ${it.name}" }
                val spinnerAdapter = ArrayAdapter(
                    this@FormDestinasiActivity,
                    android.R.layout.simple_spinner_item,
                    names
                ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

                binding.spinnerCategory.adapter = spinnerAdapter

                // Set posisi jika mode edit
                if (currentCategory.isNotEmpty()) {
                    val index = categories.indexOfFirst {
                        it.name.equals(currentCategory, ignoreCase = true)
                    }
                    if (index >= 0) binding.spinnerCategory.setSelection(index)
                    selectedCategory = currentCategory
                } else if (categories.isNotEmpty()) {
                    selectedCategory = categories[0].name
                }

                binding.spinnerCategory.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>, view: View?, position: Int, id: Long
                        ) {
                            selectedCategory = categories[position].name
                        }
                        override fun onNothingSelected(parent: AdapterView<*>) {}
                    }
            }
        }
    }

    private fun loadPengelola(db: AppDatabase) {
        lifecycleScope.launch {
            val list = withContext(Dispatchers.IO) { db.userDao().getAllPengelola() }
            pengelolaList = list

            val options = mutableListOf("-- Tidak ada pengelola --")
            options.addAll(list.map { it.nama })

            val spinnerAdapter = ArrayAdapter(
                this@FormDestinasiActivity,
                android.R.layout.simple_spinner_item,
                options
            ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

            binding.spinnerPengelola.adapter = spinnerAdapter

            // Set posisi spinner jika mode edit
            if (editDestinationId != -1L && selectedPengelolaId != 0L) {
                val index = list.indexOfFirst { it.id == selectedPengelolaId }
                if (index >= 0) binding.spinnerPengelola.setSelection(index + 1)
            }

            if (list.isEmpty()) {
                binding.tvPengelolaInfo.text = "⚠️ Belum ada pengelola terdaftar"
                binding.tvPengelolaInfo.visibility = View.VISIBLE
            } else {
                binding.tvPengelolaInfo.visibility = View.GONE
            }

            binding.spinnerPengelola.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>, view: View?, position: Int, id: Long
                    ) {
                        selectedPengelolaId = if (position == 0) 0L
                        else list[position - 1].id
                    }
                    override fun onNothingSelected(parent: AdapterView<*>) {}
                }
        }
    }

    private fun openGallery() {
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Intent(MediaStore.ACTION_PICK_IMAGES)
        } else {
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        }
        pickImageLauncher.launch(intent)
    }

    private fun copyImageToInternal(uri: Uri): String? {
        return try {
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val dir = File(filesDir, "images")
            if (!dir.exists()) dir.mkdirs()
            val file = File(dir, "dest_${System.currentTimeMillis()}.jpg")
            FileOutputStream(file).use { out -> inputStream.copyTo(out) }
            inputStream.close()
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}