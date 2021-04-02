package com.ald.uniofsouthampton.constructoo.manager.manager_fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ald.uniofsouthampton.constructoo.R
import com.ald.uniofsouthampton.constructoo.databinding.FragmentManageVendorsBinding
import com.ald.uniofsouthampton.constructoo.recycler.MyVendorsAdapter
import com.ald.uniofsouthampton.constructoo.recycler.VendorsListAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ManageVendorsFrag : Fragment() {

    private lateinit var binding: FragmentManageVendorsBinding
    private lateinit var rootRef: DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    private val vendorsList = arrayListOf<AddVendorFragment.VendorModel>()
    private val idList = arrayListOf<String>()
    private lateinit var adapter: MyVendorsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_manage_vendors, container, false)
        rootRef = FirebaseDatabase.getInstance().reference
        mAuth = FirebaseAuth.getInstance()

        binding.addVendorFAB.setOnClickListener { findNavController().navigate(R.id.action_navigation_vendors_to_addVendorFragment) }

        // set up RecyclerView
        binding.myVendorsRV.layoutManager = LinearLayoutManager(requireActivity())
        adapter = MyVendorsAdapter(requireActivity(), vendorsList)
        binding.myVendorsRV.adapter = adapter
        getVendorsList()

        return binding.root
    }

    private fun getVendorsList() {
        if (mAuth.uid != null) {
            val vendorsRef = rootRef.child("ManagerVendors").child(mAuth.uid!!)
            vendorsList.clear()
            idList.clear()
            vendorsRef.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    binding.mPrgrs.visibility = View.GONE
                    if(dataSnapshot.childrenCount>0){
                        for(snapshot: DataSnapshot in dataSnapshot.children){
                            val vendorID : String? = snapshot.key
                            if(vendorID!=null && !idList.contains(vendorID)){ // To avoid adding duplicates
                                idList.add(vendorID)
                                val email : String? = snapshot.child("vendorEmail").value as String?
                                val username : String? = snapshot.child("vendorName").value as String?
                                val address : String? = snapshot.child("vendorAddress").value as String?

                                if(email!=null && username!=null && address!=null){
                                    vendorsList.add(AddVendorFragment.VendorModel(vendorID, username, address, email))
                                    adapter.notifyDataSetChanged()
                                }
                            }
                        }
                    }
                    else{
                        binding.noVendorsLayout.visibility= View.VISIBLE
                        Log.d("xLogs->","No vendors in your list")
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }

            })
        }
    }
}