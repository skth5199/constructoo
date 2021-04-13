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
import com.ald.uniofsouthampton.constructoo.databinding.FragmentManageConstructionSiteBinding
import com.ald.uniofsouthampton.constructoo.recycler.MySitesAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ManageConstructionSiteFrag : Fragment() {

    private lateinit var binding : FragmentManageConstructionSiteBinding
    private lateinit var rootRef: DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    private val sitesList = arrayListOf<ManagerConstSitesModel>()
    private val idList = arrayListOf<String>()
    private lateinit var adapter: MySitesAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_manage_construction_site, container, false)
        rootRef = FirebaseDatabase.getInstance().reference
        mAuth = FirebaseAuth.getInstance()
        binding.addConstSiteFAB.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_construction_site_to_addConstructionSiteFrag)
        }

        binding.mySitesRV.layoutManager = LinearLayoutManager(requireActivity())
        adapter = MySitesAdapter(requireActivity(), sitesList)
        binding.mySitesRV.adapter = adapter
        getSitesList()

        return binding.root
    }

    private fun getSitesList() {
        if(mAuth.uid!=null){
            val sitesRef = rootRef.child("ManagerSites").child(mAuth.uid!!)
            sitesList.clear()
            idList.clear() // to avoid duplication of list items
            sitesRef.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    binding.mPrgrs.visibility = View.GONE
                    if(dataSnapshot.childrenCount>0){
                        for(snapshot: DataSnapshot in dataSnapshot.children){
                            val siteID: String? = snapshot.key
                            if(siteID!=null && !idList.contains(siteID)){
                                idList.add(siteID)
                                val siteName : String? = snapshot.child("siteName").value as String?
                                val siteAddress : String? = snapshot.child("siteAddress").value as String?
                                val siteManager : String? = snapshot.child("siteManager").value as String?

                                if(siteName!=null && siteManager!=null && siteAddress!=null){
                                    sitesList.add(ManagerConstSitesModel(siteID,siteName,siteAddress,siteManager))
                                    adapter.notifyDataSetChanged()
                                }
                            }
                        }
                    }
                    else{
                        binding.noSitesLayout.visibility = View.VISIBLE
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })
        }
    }


    data class ManagerConstSitesModel(val siteID : String,val siteName : String, val siteAddress:String, val siteManager: String)
}