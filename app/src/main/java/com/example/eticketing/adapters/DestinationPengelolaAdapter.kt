package com.example.eticketing.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.eticketing.data.Destination
import com.example.eticketing.databinding.ItemDestinationPengelolaBinding
import java.text.NumberFormat
import java.util.Locale

class DestinationPengelolaAdapter(
    private val onEdit: (Destination) -> Unit
) : ListAdapter<Destination, DestinationPengelolaAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val binding: ItemDestinationPengelolaBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(destination: Destination) {
            binding.tvDestName.text = destination.name
            binding.tvDestLocation.text = "📍 ${destination.location}"
            binding.tvDestCategory.text = "🏷️ ${destination.category}"
            val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
            binding.tvDestPrice.text = formatter.format(destination.price)
            binding.btnEdit.setOnClickListener { onEdit(destination) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDestinationPengelolaBinding.inflate(
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