package com.example.eticketing.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.eticketing.data.Destination
import com.example.eticketing.databinding.ItemDestinationAdminBinding
import java.io.File
import java.text.NumberFormat
import java.util.Locale

class DestinationAdminAdapter(
    private val onEdit: (Destination) -> Unit,
    private val onDelete: (Destination) -> Unit
) : ListAdapter<Destination, DestinationAdminAdapter.ViewHolder>(DiffCallback()) {

    var pengelolaNames: Map<Long, String> = emptyMap()

    inner class ViewHolder(private val binding: ItemDestinationAdminBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(destination: Destination) {
            binding.tvDestName.text = destination.name
            binding.tvDestLocation.text = "📍 ${destination.location}"
            binding.tvDestCategory.text = "🏷️ ${destination.category}"

            val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
            binding.tvDestPrice.text = formatter.format(destination.price)

            // Info pengelola
            binding.tvPengelola.text = if (destination.pengelolaId == 0L) {
                "⚠️ Belum ada pengelola"
            } else {
                "👤 Pengelola: ${pengelolaNames[destination.pengelolaId] ?: "ID ${destination.pengelolaId}"}"
            }

            // Load gambar
            if (!destination.imageUrl.isNullOrEmpty()) {
                Glide.with(binding.root.context)
                    .load(File(destination.imageUrl))
                    .centerCrop()
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(binding.ivDestImage)
            } else {
                binding.ivDestImage.setImageDrawable(null)
                binding.ivDestImage.setBackgroundColor(
                    android.graphics.Color.parseColor("#BBDEFB")
                )
            }

            binding.btnEdit.setOnClickListener { onEdit(destination) }
            binding.btnDelete.setOnClickListener { onDelete(destination) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDestinationAdminBinding.inflate(
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