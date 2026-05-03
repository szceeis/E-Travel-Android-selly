package com.example.eticketing.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.eticketing.data.Destination
import com.example.eticketing.databinding.ItemDestinationBinding
import java.text.NumberFormat
import java.util.Locale

class DestinationAdapter(
    private var destinations: List<Destination>,
    private val onBookClick: (Destination) -> Unit,
    private val role: String
) : RecyclerView.Adapter<DestinationAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemDestinationBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDestinationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dest = destinations[position]
        holder.binding.tvDestName.text = dest.name
        holder.binding.tvDestLocation.text = dest.location

        val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        holder.binding.tvDestPrice.text = formatter.format(dest.price)

        holder.binding.btnBook.setOnClickListener {
            onBookClick(dest)
        }
        if (role == "ADMIN") {
            holder.binding.btnBook.visibility = View.GONE
        } else {
            holder.binding.btnBook.visibility = View.VISIBLE
        }
    }

    override fun getItemCount() = destinations.size

    fun updateData(newList: List<Destination>) {
        destinations = newList
        notifyDataSetChanged()
    }
}