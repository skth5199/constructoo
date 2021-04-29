package com.ald.uniofsouthampton.constructoo.manager.manager_fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ald.uniofsouthampton.constructoo.R
import com.ald.uniofsouthampton.constructoo.databinding.FragmentAddConstructionSiteBinding
import com.ald.uniofsouthampton.constructoo.recycler.ConstructionSitesListAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class AddConstructionSiteFrag : Fragment() {

    private lateinit var binding : FragmentAddConstructionSiteBinding
    private lateinit var rootRef : DatabaseReference
    private lateinit var mAuth : FirebaseAuth
    private lateinit var adapter : ConstructionSitesListAdapter
    private val sitesList = arrayListOf<ConstructionSiteModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_add_construction_site, container, false)
        rootRef = FirebaseDatabase.getInstance().reference
        mAuth = FirebaseAuth.getInstance()

        // set up RecyclerView
        binding.constructionSitesRV.layoutManager = LinearLayoutManager(requireActivity())
        adapter = ConstructionSitesListAdapter(requireActivity(),sitesList,findNavController())
        binding.constructionSitesRV.adapter = adapter


        getSiteMangerSitesList()

        return binding.root
    }

    private fun getSiteMangerSitesList() {

        val sitesRef = rootRef.child("Users").child("Site Manager")
        sitesList.clear()

        sitesRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                binding.mPrgBar.visibility = View.GONE
                binding.prgMsg.visibility = View.GONE

                val managerID : String? = snapshot.key
                if(managerID!=null){

                    val siteAddress : String? = snapshot.child("address").value as String?
                    val username : String? = snapshot.child("username").value as String?
                    val contactNum : String? = snapshot.child("contactNum").value as String?

                    if(siteAddress!=null && username!=null && contactNum!=null){
                        sitesList.add(ConstructionSiteModel(managerID,username,contactNum,siteAddress))
                        adapter.notifyDataSetChanged()
                    }
                }

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    data class ConstructionSiteModel(val siteManagerID : String,val siteManagerName: String,val siteManagerContact:String, val siteAddress:String)
}