package com.ald.uniofsouthampton.constructoo.manager.manager_fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Spinner
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ald.uniofsouthampton.constructoo.R
import com.ald.uniofsouthampton.constructoo.databinding.FragmentManagerHomeBinding
import com.ald.uniofsouthampton.constructoo.recycler.DriverOrdersAdapter
import com.ald.uniofsouthampton.constructoo.recycler.ManagerOrdersAdapter
import com.ald.uniofsouthampton.constructoo.recycler.OrderDetailsModel
import com.ald.uniofsouthampton.constructoo.recycler.VendorMaterialModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ManagerHomeFrag : Fragment() {

    private lateinit var binding : FragmentManagerHomeBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var rootRef: DatabaseReference
    private lateinit var ordersRef: DatabaseReference
    private lateinit var ordersRefListener: ValueEventListener
    private lateinit var adapter : ManagerOrdersAdapter
    private val orderDisplayList = arrayListOf<OrderDetailsModel>()
    private val ordersList = arrayListOf<OrderDetailsModel>()
    private var currentStatus = "all"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_manager_home, container, false)

        binding.apply {
            addPackageFAB.setOnClickListener { findNavController().navigate(R.id.action_navigation_home_to_addNewPackageFrag) }

            mAuth = FirebaseAuth.getInstance()
            rootRef = FirebaseDatabase.getInstance().reference

            adapter = ManagerOrdersAdapter(requireContext(),1,orderDisplayList,findNavController())
            managerOrdersRV.layoutManager = LinearLayoutManager(requireActivity())
            managerOrdersRV.adapter = adapter


            allBtn.setOnClickListener {
                currentStatus = "all"
                switchOrderStatus()
            }
            pendingBtn.setOnClickListener {
                currentStatus = "pending"
                switchOrderStatus()
            }
            completedBtn.setOnClickListener {
                currentStatus = "complete"
                switchOrderStatus()
            }

        }

        getManagerOrders()

        return binding.root
    }

    private fun switchOrderStatus(){
        binding.apply {
            orderDisplayList.clear()
            when(currentStatus){
                "all"->{
                    aoBar.visibility = View.VISIBLE
                    poBar.visibility = View.INVISIBLE
                    coBar.visibility = View.INVISIBLE
                    orderDisplayList.addAll(ordersList)
                }
                "pending"->{
                    aoBar.visibility = View.INVISIBLE
                    poBar.visibility = View.VISIBLE
                    coBar.visibility = View.INVISIBLE
                    for(item : OrderDetailsModel in ordersList){
                        if(item.status=="pending"){
                            orderDisplayList.add(item)
                        }
                    }
                }
                "complete"->{
                    aoBar.visibility = View.INVISIBLE
                    poBar.visibility = View.INVISIBLE
                    coBar.visibility = View.VISIBLE
                    for(item : OrderDetailsModel in ordersList){
                        if(item.status=="complete"){
                            orderDisplayList.add(item)
                        }
                    }
                }
            }
            adapter = ManagerOrdersAdapter(requireContext(),1,orderDisplayList,findNavController())
            managerOrdersRV.adapter = adapter
        }
    }

    private fun getManagerOrders() {
        val uid = mAuth.uid
        if(uid!=null){
            ordersRef = rootRef.child("Orders").child(uid)
            ordersRefListener = object : ValueEventListener{
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    binding.managerHomePrg.visibility = View.GONE
                    binding.layout1.visibility = View.VISIBLE
                    binding.layout2.visibility = View.VISIBLE
                    if (dataSnapshot.childrenCount > 0) {
                        ordersList.clear()
                        orderDisplayList.clear()
                        for (snapshot: DataSnapshot in dataSnapshot.children) {
                            val orderID: String? = snapshot.key
                            val managerID: String? = snapshot.child("managerID").value as String?
                            val siteManagerID : String? = snapshot.child("siteManagerID").value as String?
                            val status: String? = snapshot.child("status").value as String?

                            if (orderID != null &&siteManagerID!=null&& managerID != null && status != null) {
                                // get initial package data
                                val siteManagerContact: String? = snapshot.child("siteManagerContact").value as String?
                                val dateAdded : String? = snapshot.child("dateAdded").value as String?
                                val siteAddress: String? = snapshot.child("siteAddress").value as String?
                                val driverName: String? = snapshot.child("driverName").value as String?
                                val driverID: String? = snapshot.child("driverID").value as String?
                                val driverContact: String? = snapshot.child("driverContact").value as String?
                                val vendorName: String? = snapshot.child("vendorName").value as String?
                                val vendorID: String? = snapshot.child("vendorID").value as String?
                                val vendorAddress: String? = snapshot.child("vendorAddress").value as String?

                                // get list of materials in package
                                val packageDetailsRef = rootRef.child("Orders").child(managerID).child(orderID).child("packageContents")
                                val materialsList = arrayListOf<VendorMaterialModel>()

                                packageDetailsRef.addValueEventListener(object :
                                    ValueEventListener {
                                    override fun onDataChange(packageDetailsDataSnapshot: DataSnapshot) {
                                        for (packageSnap in packageDetailsDataSnapshot.children) {
                                            val materialID = packageSnap.child("materialID").value as String?
                                            val name = packageSnap.child("name").value as String?
                                            val weight = packageSnap.child("weight").value as String?
                                            val dimensions = packageSnap.child("dimensions").value as String?
                                            val colour = packageSnap.child("colour").value as String?

                                            if (materialID != null && name != null && weight != null && dimensions != null && colour != null) {
                                                materialsList.add(
                                                    VendorMaterialModel(
                                                        materialID,
                                                        name,
                                                        weight,
                                                        dimensions,
                                                        colour
                                                    )
                                                )
                                            }
                                        }
                                        if(materialsList.isNotEmpty()){
                                            val orderObj = OrderDetailsModel(orderID, managerID,siteManagerID, dateAdded!!, status, siteManagerContact, siteAddress, driverName, driverID, driverContact, vendorName, vendorID, vendorAddress, materialsList)
                                            ordersList.add(orderObj)
                                            orderDisplayList.add(orderObj)
                                            adapter.notifyDataSetChanged()
                                            binding.noOrdersLayout.visibility = View.GONE
                                        }
                                    }
                                    override fun onCancelled(error: DatabaseError) {}
                                })
                            }
                        }
                    }
                    else{
                        binding.noOrdersLayout.visibility = View.VISIBLE
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            }
            ordersRef.addValueEventListener(ordersRefListener)
        }
    }

    override fun onDestroy() {
        if(ordersRef!=null &&
            ordersRefListener!=null){
            ordersRef.removeEventListener(ordersRefListener)
        }
        super.onDestroy()
    }

}