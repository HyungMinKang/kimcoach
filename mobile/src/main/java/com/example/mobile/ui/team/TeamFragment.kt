package com.example.mobile.ui.team

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.mobile.R
import com.example.mobile.databinding.FragmentTeamBinding
import com.example.mobile.domain.model.TeamMember


class TeamFragment : Fragment() {

    private lateinit var binding: FragmentTeamBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_team, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = TeamAdapter()
        binding.rvTeamMemberList.adapter = adapter
        adapter.submitList(makeDummyTeamMembers())

    }

    fun makeDummyTeamMembers(): MutableList<TeamMember> {
        val members = mutableListOf<TeamMember>()
        members.add(TeamMember("XXX", 21, listOf("CF, RW")))
        members.add(TeamMember("XXX", 22, listOf("CF, RW")))
        members.add(TeamMember("XXX", 22, listOf("CF, RW")))
        members.add(TeamMember("XXX", 22, listOf("CF, RW")))
        members.add(TeamMember("XXX", 22, listOf("CF, RW")))
        members.add(TeamMember("XXX", 22, listOf("CF, RW")))
        return members

    }

}