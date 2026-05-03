package com.example.eticketing.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.eticketing.data.User
import com.example.eticketing.databinding.ItemUserAdminBinding

class UserAdminAdapter(
    private val onDelete: (User) -> Unit
) : ListAdapter<User, UserAdminAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val binding: ItemUserAdminBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User) {
            binding.tvUserNama.text = user.nama
            binding.tvUserEmail.text = user.email
            binding.tvUserRole.text = "Role: ${user.role.replaceFirstChar { it.uppercase() }}"

            // Sembunyikan tombol hapus untuk admin
            if (user.role == "admin") {
                binding.btnHapusUser.isEnabled = false
                binding.btnHapusUser.alpha = 0.3f
            } else {
                binding.btnHapusUser.isEnabled = true
                binding.btnHapusUser.alpha = 1f
                binding.btnHapusUser.setOnClickListener { onDelete(user) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemUserAdminBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: User, newItem: User) = oldItem == newItem
    }
}