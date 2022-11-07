package com.example.mobile.ui.home

import android.opengl.Visibility
import android.service.voice.VoiceInteractionSession
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile.databinding.ItemHomeCompleteMatchBinding
import com.example.mobile.databinding.ItemHomeReservedMatchBinding
import com.example.mobile.domain.model.CompletedMatch

class HomeCompleteMatchAdapter: RecyclerView.Adapter<HomeCompleteMatchAdapter.ViewHolder>() {
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

    fun submitList(items:List<CompletedMatch>){
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    inner class ViewHolder(private  val binding: ItemHomeCompleteMatchBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(item:CompletedMatch){
            binding.match = item
            if(item.dataSync){
                binding.btnCompleteSeeResult.isVisible = true
                binding.btnCompleteSyncData.isVisible = false
            }
            else{
                binding.btnCompleteSeeResult.isVisible = false
                binding.btnCompleteSyncData.isVisible = true
            }
        }
    }

}