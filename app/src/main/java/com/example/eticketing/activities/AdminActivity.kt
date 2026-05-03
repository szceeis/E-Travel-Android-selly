package com.example.eticketing.activities

import android.content.Intent
import android.os.Bundle
import com.example.eticketing.data.SessionManager
import com.example.eticketing.databinding.ActivityAdminBinding

class AdminActivity : BaseActivity() {

    private lateinit var binding: ActivityAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val nama = SessionManager.getUserName(this)
        binding.tvWelcome.text = "Selamat Datang, $nama 👑"

        binding.cardDestinasi.setOnClickListener {
            startActivity(Intent(this, KelolaDestinasiActivity::class.java))
        }
        binding.cardUser.setOnClickListener {
            startActivity(Intent(this, KelolaUserActivity::class.java))
        }
        binding.cardTiket.setOnClickListener {
            startActivity(Intent(this, AdminTiketActivity::class.java))
        }
        binding.cardBrowse.setOnClickListener {
            startActivity(Intent(this, AdminBrowseActivity::class.java))
        }
        binding.btnLogout.setOnClickListener {
            SessionManager.logout(this, LoginActivity::class.java)
        }
    }
}