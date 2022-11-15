package com.example.mobile.ui.team

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile.databinding.ItemTeamCardBinding
import com.example.mobile.domain.model.TeamMember

class TeamAdapter : RecyclerView.Adapter<TeamAdapter.ViewHolder>() {
    val items = mutableListOf<TeamMember>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(ItemTeamCardBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: TeamAdapter.ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun submitList(items: List<TeamMember>) {
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemTeamCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(teamMember: TeamMember) {
            binding.member = teamMember
            binding.age = teamMember.age.toString()
            binding.position = teamMember.position.toString()
        }
    }
}