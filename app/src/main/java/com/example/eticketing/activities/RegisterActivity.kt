package com.example.eticketing.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.eticketing.R
import com.example.eticketing.data.AppDatabase
import com.example.eticketing.data.User
import com.example.eticketing.databinding.ActivityRegisterBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterActivity : BaseActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = AppDatabase.getDatabase(this)
        val userDao = db.userDao()

        binding.btnRegister.setOnClickListener {
            val nama = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            val role = when (binding.rgRole.checkedRadioButtonId) {
                R.id.rbPengelola -> "pengelola"
                else -> "user"
            }

            if (nama.isEmpty()) {
                binding.etName.error = "Nama tidak boleh kosong"
                return@setOnClickListener
            }
            if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.etEmail.error = "Email tidak valid"
                return@setOnClickListener
            }
            if (password.length < 6) {
                binding.etPassword.error = "Password minimal 6 karakter"
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val existing = withContext(Dispatchers.IO) {
                    userDao.getUserByEmail(email)
                }

                if (existing != null) {
                    withContext(Dispatchers.Main) {
                        binding.etEmail.error = "Email sudah terdaftar"
                        Toast.makeText(
                            this@RegisterActivity,
                            "Email sudah digunakan",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    return@launch
                }

                withContext(Dispatchers.IO) {
                    userDao.register(
                        User(
                            nama = nama,
                            email = email,
                            password = password,
                            role = role
                        )
                    )
                }

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Akun berhasil dibuat! Silakan login.",
                        Toast.LENGTH_SHORT
                    ).show()
                    startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                    finish()
                }
            }
        }

        binding.tvLogin.setOnClickListener { finish() }
    }
}