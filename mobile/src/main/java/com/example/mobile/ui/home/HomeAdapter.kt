package com.example.mobile.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile.databinding.ItemHomeReservedMatchBinding
import com.example.mobile.domain.model.ReservedMatch

class HomeAdapter: RecyclerView.Adapter<HomeAdapter.ViewHolder>() {
    val items = mutableListOf<ReservedMatch>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(ItemHomeReservedMatchBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: HomeAdapter.ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun submitList(items:List<ReservedMatch>){
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemHomeReservedMatchBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(reservationItem: ReservedMatch) {
            binding.reservedMatch = reservationItem
        }
    }

}