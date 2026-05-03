package com.example.eticketing.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.eticketing.data.AppDatabase
import com.example.eticketing.data.SessionManager
import com.example.eticketing.data.Ticket
import com.example.eticketing.databinding.ActivityDetailDestinasiBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.NumberFormat
import java.util.Locale

class DetailDestinasiActivity : BaseActivity() {

    private lateinit var binding: ActivityDetailDestinasiBinding
    private var quantity = 1
    private var pricePerTicket = 0.0
    private var hasPengelola = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailDestinasiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupBackButton("Detail Destinasi")

        val db = AppDatabase.getDatabase(this)
        val destinationId = intent.getLongExtra("destinationId", -1L)
        val userId = SessionManager.getUserId(this)
        val userRole = SessionManager.getUserRole(this)
        val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

        lifecycleScope.launch {
            val destination = withContext(Dispatchers.IO) {
                db.destinationDao().getDestinationById(destinationId)
            }

            destination?.let { dest ->
                pricePerTicket = dest.price
                binding.tvDetailName.text = dest.name
                binding.tvDetailLocation.text = "📍 ${dest.location}"
                binding.tvDetailCategory.text = dest.category
                binding.tvDetailPrice.text = "${formatter.format(dest.price)} / tiket"
                binding.tvDetailDescription.text = dest.description

                // Load gambar
                if (!dest.imageUrl.isNullOrEmpty()) {
                    Glide.with(this@DetailDestinasiActivity)
                        .load(File(dest.imageUrl))
                        .centerCrop()
                        .placeholder(android.R.drawable.ic_menu_gallery)
                        .into(binding.ivDetailImage)
                }

                // Cek pengelola
                if (dest.pengelolaId == 0L) {
                    hasPengelola = false
                    binding.tvDetailPengelola.text = "⚠️ Belum ada pengelola"
                    binding.tvDetailPengelola.setTextColor(
                        android.graphics.Color.parseColor("#E53935")
                    )

                    // Sembunyikan booking jika tidak ada pengelola
                    // kecuali admin tetap bisa lihat
                    if (userRole != "admin") {
                        binding.tvLabelJumlahTiket.visibility = View.GONE
                        binding.btnMinus.visibility = View.GONE
                        binding.tvQuantity.visibility = View.GONE
                        binding.btnPlus.visibility = View.GONE
                        binding.tvTotalHarga.visibility = View.GONE
                        binding.btnBook.visibility = View.GONE
                    } else {
                        binding.btnBook.isEnabled = false
                        binding.btnBook.alpha = 0.5f
                        binding.btnBook.text = "Tidak Tersedia — Belum Ada Pengelola"
                    }
                } else {
                    hasPengelola = true
                    val pengelola = withContext(Dispatchers.IO) {
                        db.userDao().getUserById(dest.pengelolaId)
                    }
                    binding.tvDetailPengelola.text = "👤 Dikelola oleh: ${pengelola?.nama ?: "-"}"
                    binding.tvDetailPengelola.setTextColor(
                        android.graphics.Color.parseColor("#2E7D32")
                    )
                }

                updateTotal(formatter)
            }
        }

        binding.btnPlus.setOnClickListener {
            if (quantity < 10) {
                quantity++
                binding.tvQuantity.text = quantity.toString()
                updateTotal(formatter)
            }
        }

        binding.btnMinus.setOnClickListener {
            if (quantity > 1) {
                quantity--
                binding.tvQuantity.text = quantity.toString()
                updateTotal(formatter)
            }
        }

        binding.btnBook.setOnClickListener {
            if (!hasPengelola) {
                Toast.makeText(this, "Destinasi ini belum memiliki pengelola", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (userId == -1L) {
                Toast.makeText(this, "Sesi tidak valid, silakan login ulang", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val total = pricePerTicket * quantity
            AlertDialog.Builder(this)
                .setTitle("Konfirmasi Pemesanan")
                .setMessage("Pesan $quantity tiket?\nTotal: ${formatter.format(total)}")
                .setPositiveButton("Pesan") { _, _ ->
                    lifecycleScope.launch {
                        withContext(Dispatchers.IO) {
                            AppDatabase.getDatabase(this@DetailDestinasiActivity)
                                .ticketDao().bookTicket(
                                    Ticket(
                                        userId = userId,
                                        destinationId = intent.getLongExtra("destinationId", -1L),
                                        bookingDate = System.currentTimeMillis(),
                                        quantity = quantity,
                                        totalPrice = total,
                                        status = "PENDING"
                                    )
                                )
                        }
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@DetailDestinasiActivity,
                                "Pemesanan berhasil! Status: PENDING",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        }
                    }
                }
                .setNegativeButton("Batal", null)
                .show()
        }
    }

    private fun updateTotal(formatter: NumberFormat) {
        val total = pricePerTicket * quantity
        binding.tvTotalHarga.text = "Total: ${formatter.format(total)}"
    }
}