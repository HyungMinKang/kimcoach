package com.example.mobile.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile.databinding.ItemHomeCompleteMatchBinding
import com.example.mobile.domain.model.CompletedMatch

class HomeCompleteMatchAdapter(
    private val itemClick: (item: CompletedMatch) -> Unit,
    private val showResultClick: (item: CompletedMatch) -> Unit
) : RecyclerView.Adapter<HomeCompleteMatchAdapter.ViewHolder>() {
    val items = mutableListOf<CompletedMatch>()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HomeCompleteMatchAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(ItemHomeCompleteMatchBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: HomeCompleteMatchAdapter.ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun submitList(items: List<CompletedMatch>) {
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemHomeCompleteMatchBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CompletedMatch) {
            binding.match = item
            if (item.dataSync) {
                binding.btnCompleteSeeResult.isVisible = true
                binding.btnCompleteSyncData.isVisible = false
                binding.btnCompleteSeeResult.setOnClickListener {
                    showResultClick.invoke(item)
                }
            } else {
                binding.btnCompleteSeeResult.isVisible = false
                binding.btnCompleteSyncData.isVisible = true
                binding.btnCompleteSyncData.setOnClickListener {
                    itemClick.invoke(item)
                }
            }
        }
    }

}