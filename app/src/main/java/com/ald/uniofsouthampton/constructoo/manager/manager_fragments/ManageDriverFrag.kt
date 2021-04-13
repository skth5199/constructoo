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
import com.ald.uniofsouthampton.constructoo.databinding.FragmentManageDriverBinding
import com.ald.uniofsouthampton.constructoo.databinding.FragmentManagerHomeBinding
import com.ald.uniofsouthampton.constructoo.recycler.MyDriversAdapter
import com.ald.uniofsouthampton.constructoo.recycler.MyVendorsAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class ManageDriverFrag : Fragment() {

    private lateinit var binding : FragmentManageDriverBinding
    private lateinit var rootRef: DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    private val driversList = arrayListOf<AddDriverFragment.DriverModel>()
    private val idList = arrayListOf<String>()
    private lateinit var adapter: MyDriversAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_manage_driver, container, false)
        rootRef = FirebaseDatabase.getInstance().reference
        mAuth = FirebaseAuth.getInstance()
        binding.addDriverFAB.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_driver_to_addDriverFragment)
        }

        // set up RecyclerView
        binding.myDriversRV.layoutManager = LinearLayoutManager(requireActivity())
        adapter = MyDriversAdapter(requireActivity(), driversList)
        binding.myDriversRV.adapter = adapter

        getDriversList()

        return binding.root
    }

    private fun getDriversList() {
        if (mAuth.uid != null) {
            val vendorsRef = rootRef.child("ManagerDrivers").child(mAuth.uid!!)
            driversList.clear()
            idList.clear()
            vendorsRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    binding.mPrgrs.visibility = View.GONE
                    if(dataSnapshot.childrenCount>0){
                        for(snapshot: DataSnapshot in dataSnapshot.children){
                            val driverID : String? = snapshot.key
                            if(driverID!=null && !idList.contains(driverID)){ // To avoid adding duplicates
                                idList.add(driverID)
                                val email : String? = snapshot.child("driverEmail").value as String?
                                val username : String? = snapshot.child("driverName").value as String?
                                val contactNum : String? = snapshot.child("contactNum").value as String?

                                if(email!=null && username!=null && contactNum!=null){
                                    driversList.add(AddDriverFragment.DriverModel(driverID,username,email,contactNum))
                                    adapter.notifyDataSetChanged()
                                }
                            }
                        }
                    }
                    else{
                        binding.noDriversTV.visibility= View.VISIBLE
                        Log.d("xLogs->","No drivers in your list")
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }

            })
        }
    }

}