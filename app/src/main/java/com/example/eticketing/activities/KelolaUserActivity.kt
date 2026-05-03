package com.example.eticketing.activities

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eticketing.adapters.UserAdminAdapter
import com.example.eticketing.data.AppDatabase
import com.example.eticketing.databinding.ActivityKelolaUserBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class KelolaUserActivity : BaseActivity() {

    private lateinit var binding: ActivityKelolaUserBinding
    private lateinit var adapter: UserAdminAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKelolaUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupBackButton("Kelola User")

        val db = AppDatabase.getDatabase(this)
        val userDao = db.userDao()

        adapter = UserAdminAdapter(
            onDelete = { user ->
                AlertDialog.Builder(this)
                    .setTitle("Hapus User")
                    .setMessage("Yakin ingin menghapus user \"${user.nama}\"?")
                    .setPositiveButton("Hapus") { _, _ ->
                        lifecycleScope.launch {
                            withContext(Dispatchers.IO) {
                                userDao.deleteUser(user.id)
                            }
                            loadUsers()
                        }
                    }
                    .setNegativeButton("Batal", null)
                    .show()
            }
        )

        binding.rvUser.layoutManager = LinearLayoutManager(this)
        binding.rvUser.adapter = adapter

        loadUsers()
    }

    private fun loadUsers() {
        lifecycleScope.launch {
            val users = withContext(Dispatchers.IO) {
                AppDatabase.getDatabase(this@KelolaUserActivity).userDao().getAllUsers()
            }
            adapter.submitList(users)
        }
    }
}