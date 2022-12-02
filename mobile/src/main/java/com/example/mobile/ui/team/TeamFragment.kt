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
        members.add(TeamMember("Player2", 21, listOf("CF, RW")))
        members.add(TeamMember("Player3", 24, listOf("LW")))
        members.add(TeamMember("Player4", 23, listOf("RW")))
        members.add(TeamMember("Player5", 28, listOf("CAM, RM")))
        members.add(TeamMember("Player6", 30, listOf("CAM, LM")))
        members.add(TeamMember("Player7", 26, listOf("LM, LDM, CDM")))
        members.add(TeamMember("Player8", 22, listOf("CB, LB")))
        members.add(TeamMember("Player9", 23, listOf("RB, CB")))
        members.add(TeamMember("Player10", 22, listOf("CB, RB")))
        members.add(TeamMember("Player11", 24, listOf("GK")))
        members.add(TeamMember("Player12", 25, listOf("GK")))
        members.add(TeamMember("Player13", 25, listOf("CB")))
        return members

    }

}