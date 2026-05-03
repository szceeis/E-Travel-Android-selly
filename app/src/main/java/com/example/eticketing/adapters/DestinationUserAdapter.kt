package com.example.eticketing.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.eticketing.data.Destination
import com.example.eticketing.databinding.ItemDestinationUserBinding
import java.io.File
import java.text.NumberFormat
import java.util.Locale

class DestinationUserAdapter(
    private val onClick: (Destination) -> Unit
) : ListAdapter<Destination, DestinationUserAdapter.ViewHolder>(DiffCallback()) {

    // Set ini dari luar untuk filter hanya destinasi dengan pengelola
    var showStatusPengelola: Boolean = false

    inner class ViewHolder(private val binding: ItemDestinationUserBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(destination: Destination) {
            binding.tvDestName.text = destination.name
            binding.tvDestLocation.text = "📍 ${destination.location}"
            binding.tvDestCategory.text = destination.category
            val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
            binding.tvDestPrice.text = formatter.format(destination.price)

            // Gambar
            if (!destination.imageUrl.isNullOrEmpty()) {
                Glide.with(binding.root.context)
                    .load(File(destination.imageUrl))
                    .centerCrop()
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(binding.ivDestImage)
            } else {
                binding.ivDestImage.setImageDrawable(null)
                binding.ivDestImage.setBackgroundColor(Color.parseColor("#BBDEFB"))
            }

            // Status pengelola — hanya tampil jika showStatusPengelola = true (untuk admin)
            if (showStatusPengelola) {
                binding.tvStatusPengelola.visibility = View.VISIBLE
                if (destination.pengelolaId == 0L) {
                    binding.tvStatusPengelola.text = "⚠️ Belum ada pengelola — tidak bisa dibooking"
                    binding.tvStatusPengelola.setTextColor(Color.parseColor("#E53935"))
                } else {
                    binding.tvStatusPengelola.text = "✅ Tersedia"
                    binding.tvStatusPengelola.setTextColor(Color.parseColor("#2E7D32"))
                }
            } else {
                binding.tvStatusPengelola.visibility = View.GONE
            }

            binding.root.setOnClickListener { onClick(destination) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDestinationUserBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<Destination>() {
        override fun areItemsTheSame(oldItem: Destination, newItem: Destination) =
            oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Destination, newItem: Destination) =
            oldItem == newItem
    }
}