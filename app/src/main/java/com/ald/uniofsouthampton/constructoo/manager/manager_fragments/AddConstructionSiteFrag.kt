package com.ald.uniofsouthampton.constructoo.manager.manager_fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.ald.uniofsouthampton.constructoo.R
import com.ald.uniofsouthampton.constructoo.databinding.FragmentAddConstructionSiteBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AddConstructionSiteFrag : Fragment() {

    private lateinit var binding : FragmentAddConstructionSiteBinding
    private lateinit var rootRef : DatabaseReference
    private lateinit var mAuth : FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_add_construction_site, container, false)
        rootRef = FirebaseDatabase.getInstance().reference
        mAuth = FirebaseAuth.getInstance()

        binding.apply {
            saveNewSiteFAB.setOnClickListener {
                val siteName = siteNameET.text.toString()
                if(siteName.length<3){
                    showSnackMsg("Site name must contain at least 3 characters.")
                    return@setOnClickListener
                }
                val siteAddress = siteAddressET.text.toString()
                if(siteAddress.length<8){
                    showSnackMsg("Site Address must contain at least 8 characters.")
                    return@setOnClickListener
                }
                addNewSiteToDB(ConstructionSiteModel(siteName,siteAddress,siteManagerET.text.toString()))
            }
        }

        return binding.root
    }

    private fun addNewSiteToDB(siteInfo : ConstructionSiteModel){
        if(mAuth.uid!=null){
            binding.progressLayout.visibility = View.VISIBLE
            val dbRef = FirebaseDatabase.getInstance().reference.child("ManagerSites").child(mAuth.uid!!).push()
            dbRef.setValue(siteInfo).addOnCompleteListener {
                binding.progressLayout.visibility = View.GONE
                if(it.isSuccessful){
                    Toast.makeText(requireActivity(),"Successfully added new site",Toast.LENGTH_LONG).show()
                    findNavController().popBackStack()
                }
                else{ showSnackMsg("Failed to add this construction site.") }
            }
        }
    }

    private fun showSnackMsg(msg : String){
        Snackbar.make(binding.addSiteContainer,msg,Snackbar.LENGTH_LONG).show()
    }

    data class ConstructionSiteModel(val siteName : String, val siteAddress:String, val siteManager: String)
}