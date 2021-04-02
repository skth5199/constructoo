package com.ald.uniofsouthampton.constructoo.manager.manager_fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.ald.uniofsouthampton.constructoo.R
import com.ald.uniofsouthampton.constructoo.databinding.FragmentManagerHomeBinding


class ManagerHomeFrag : Fragment() {

    private lateinit var binding : FragmentManagerHomeBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_manager_home, container, false)
        binding.addPackageFAB.setOnClickListener { findNavController().navigate(R.id.action_navigation_home_to_addNewPackageFrag) }
        return binding.root
    }

}