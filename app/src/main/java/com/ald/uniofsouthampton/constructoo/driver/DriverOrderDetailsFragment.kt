package com.ald.uniofsouthampton.constructoo.driver

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ald.uniofsouthampton.constructoo.R
import com.ald.uniofsouthampton.constructoo.databinding.FragmentOrderDetailsBinding
import com.ald.uniofsouthampton.constructoo.recycler.OrderDetailsModel
import com.ald.uniofsouthampton.constructoo.recycler.PackageContentsAdapter
import com.ald.uniofsouthampton.constructoo.recycler.VendorMaterialModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class DriverOrderDetailsFragment : Fragment() {
    private lateinit var binding: FragmentOrderDetailsBinding
    private lateinit var adapter: PackageContentsAdapter
    private lateinit var rootRef : DatabaseReference
    private var order: OrderDetailsModel? = null
    private var orderStatus : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            order = it.getParcelable("orderDetails")
            orderStatus = it.getString("orderStatus")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_order_details, container, false)
        DriversHomeActivity.driverActionBar?.title = "Order Details"
        DriversHomeActivity.driverActionBar?.setDisplayHomeAsUpEnabled(true)

        if(order!=null && orderStatus!=null){
            rootRef = FirebaseDatabase.getInstance().reference
            binding.apply {
                siteContactTV.text = order?.siteContactNum
                deliveryAddressTV.text = order?.siteAddress
                vendorNameTV.text = order?.vendorName
                vendorAddressTV.text = order?.vendorAddress
                dateAddedTV.text = order?.dateAdded

                setPackageContentsList(order?.packageDetails)

                if(orderStatus == "pending"){
                    markCompleteFAB.visibility = View.VISIBLE
                    markCompleteFAB.setOnClickListener {
                        markAsComplete(order!!)
                    }
                }
                else if (orderStatus == "complete"){
                    orderStatusTV.text = "complete"
                    orderStatusImage.setImageResource(R.drawable.order_complete_ico)
                    markCompleteFAB.visibility = View.GONE
                }

            }
        }

        return binding.root
    }

    private fun setPackageContentsList(packageDetails: ArrayList<VendorMaterialModel>?) {
        if(packageDetails!=null){
            adapter = PackageContentsAdapter(requireActivity(),packageDetails)
            binding.packageContentsRV.layoutManager = LinearLayoutManager(requireActivity())
            binding.packageContentsRV.adapter = adapter
        }
    }

    private fun markAsComplete(orderDetails : OrderDetailsModel){
        binding.driverPrg.visibility = View.VISIBLE
        val driversRef = rootRef.child("DriverOrders").child(orderDetails.driverID!!).child(orderDetails.orderID).child("status")
        val ordersRef = rootRef.child("Orders").child(orderDetails.managerID!!).child(orderDetails.orderID).child("status")
        val ordersRef2 = rootRef.child("siteManagerOrders").child(orderDetails.siteManagerID!!).child(orderDetails.orderID).child("status")
        driversRef.setValue("complete").addOnCompleteListener { task1->
            if(task1.isSuccessful){
                ordersRef.setValue("complete").addOnCompleteListener { task2->
                    if(task2.isSuccessful){
                        ordersRef2.setValue("complete")
                        //ordersRef2.child("status").setValue("complete")
                        Toast.makeText(requireActivity(),"Order marked as complete",Toast.LENGTH_LONG).show()
                        sendNotificationData(orderDetails)
                    }
                    else{
                        Toast.makeText(requireActivity(),"failed to mark order as complete",Toast.LENGTH_LONG).show()
                        binding.driverPrg.visibility = View.GONE
                    }
                }
            }
            else{
                Toast.makeText(requireActivity(),"failed to mark order as complete",Toast.LENGTH_LONG).show()
                binding.driverPrg.visibility = View.GONE
            }
        }
    }

    private fun sendNotificationData(orderDetails : OrderDetailsModel){
        val managerCompletionRef = rootRef.child("ManagerOrdersNotification").child(orderDetails.managerID!!).child(orderDetails.orderID)
        val map : HashMap<String,String> = HashMap()
        map["driverName"] = orderDetails.driverName!!
        map["siteAddress"] = orderDetails.siteAddress!!
        managerCompletionRef.setValue(map).addOnCompleteListener {
            if(it.isSuccessful){
                val completionRef = rootRef.child("SiteManagerOrdersNotification").child(orderDetails.siteManagerID!!).child(orderDetails.orderID)
                completionRef.setValue(map).addOnCompleteListener {
                    findNavController().navigate(R.id.action_back_pending_order_to_home)
                }
            }
        }


    }

}