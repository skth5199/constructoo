package com.ald.uniofsouthampton.constructoo.manager.manager_fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ald.uniofsouthampton.constructoo.R
import com.ald.uniofsouthampton.constructoo.databinding.FragmentAddVendorBinding
import com.ald.uniofsouthampton.constructoo.helpers.FormatCheckHelpers
import com.ald.uniofsouthampton.constructoo.recycler.VendorsListAdapter
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class AddVendorFragment : Fragment() {
    private lateinit var binding : FragmentAddVendorBinding
    private lateinit var rootRef : DatabaseReference
    private val vendorsList = arrayListOf<VendorModel>()
    private val idList = arrayListOf<String>()
    private lateinit var adapter : VendorsListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_add_vendor, container, false)
        rootRef = FirebaseDatabase.getInstance().reference

        // set up RecyclerView
        binding.vendorsListRV.layoutManager = LinearLayoutManager(requireActivity())
        adapter = VendorsListAdapter(requireActivity(),vendorsList,findNavController())
        binding.vendorsListRV.adapter = adapter
        getVendorsList()

        return binding.root
    }

    private fun getVendorsList (){
        val vendorsRef = rootRef.child("Users").child("Vendor")
        vendorsList.clear()
        idList.clear()
        vendorsRef.addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                binding.mPrgBar.visibility = View.GONE
                binding.prgMsg.visibility = View.GONE

                val vendorID : String? = snapshot.key
                if(vendorID!=null && !idList.contains(vendorID)){ // To avoid adding duplicates
                    val email : String? = snapshot.child("email").value as String?
                    val username : String? = snapshot.child("username").value as String?
                    val address : String? = snapshot.child("address").value as String?

                    if(email!=null && username!=null && address!=null){
                        idList.add(vendorID)
                        vendorsList.add(VendorModel(vendorID,username,address,email))
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

    data class VendorModel(val vendorID :String, val vendorName : String, val vendorAddress:String, val vendorEmail: String)
}