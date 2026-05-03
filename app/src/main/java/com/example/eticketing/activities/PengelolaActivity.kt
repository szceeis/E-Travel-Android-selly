package com.example.eticketing.activities

import android.content.Intent
import android.os.Bundle
import com.example.eticketing.data.SessionManager
import com.example.eticketing.databinding.ActivityPengelolaBinding

class PengelolaActivity : BaseActivity() {

    private lateinit var binding: ActivityPengelolaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPengelolaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val nama = SessionManager.getUserName(this)
        val pengelolaId = SessionManager.getUserId(this)

        binding.tvWelcomePengelola.text = "Selamat Datang, $nama 🏢"

        binding.cardDestinasiSaya.setOnClickListener {
            val intent = Intent(this, DestinasiSayaActivity::class.java)
            intent.putExtra("pengelolaId", pengelolaId)
            startActivity(intent)
        }

        binding.cardTiketSaya.setOnClickListener {
            val intent = Intent(this, TiketPengelolaActivity::class.java)
            intent.putExtra("pengelolaId", pengelolaId)
            startActivity(intent)
        }

        binding.btnLogoutPengelola.setOnClickListener {
            SessionManager.logout(this, LoginActivity::class.java)
        }
    }
}