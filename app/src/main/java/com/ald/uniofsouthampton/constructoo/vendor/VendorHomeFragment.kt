package com.ald.uniofsouthampton.constructoo.vendor

import android.app.AlertDialog
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.ald.uniofsouthampton.constructoo.R
import com.ald.uniofsouthampton.constructoo.authentication.AuthActivity
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.firebase.auth.FirebaseAuth

class VendorHomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_vendor_home, container, false)
        VendorsActivity.vendorActionBar?.hide()
        val vendorMaterialsFAB : ExtendedFloatingActionButton = view.findViewById(R.id.vendorMaterialsFAB)
        val vendorInfoFAB : ExtendedFloatingActionButton = view.findViewById(R.id.vendorInfoFAB)
        val logOutFAB : ExtendedFloatingActionButton = view.findViewById(R.id.logOutFAB)

        vendorMaterialsFAB.setOnClickListener {
            findNavController().navigate(R.id.action_vendorHomeFragment_to_vendorMaterialsFragment)
        }
        vendorInfoFAB.setOnClickListener {
            findNavController().navigate(R.id.action_vendorHomeFragment_to_vendorSettingsFragment)
        }
        logOutFAB.setOnClickListener {
            logOutUser()
        }

        return view
    }

    private fun logOutUser(){
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("Confirm action")
        builder.setMessage("Are you sure you want to log out?")

        builder.setPositiveButton("Yes") { dialog, which ->
            FirebaseAuth.getInstance().signOut()
            val prefs = requireActivity().getSharedPreferences("myPrefs",MODE_PRIVATE)
            if(prefs.contains("accountType")){prefs.edit().remove("accountType").apply()}
            startActivity(Intent(requireActivity(), AuthActivity::class.java))
            requireActivity().finish()
        }

        builder.setNegativeButton("Cancel") { dialog, which ->
            dialog.cancel()
        }
        val alertDialog = builder.create()
        alertDialog.show()
    }
}