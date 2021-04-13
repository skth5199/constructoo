package com.ald.uniofsouthampton.constructoo.manager.manager_fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ald.uniofsouthampton.constructoo.R
import com.ald.uniofsouthampton.constructoo.databinding.FragmentAddPackageContentBinding
import com.ald.uniofsouthampton.constructoo.recycler.ChooseVendorMaterialAdapter
import com.ald.uniofsouthampton.constructoo.recycler.SelectedMaterialsAdapter
import com.ald.uniofsouthampton.constructoo.recycler.VendorMaterialModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class AddPackageContentFrag : Fragment() {
    private lateinit var binding : FragmentAddPackageContentBinding
    private lateinit var rootRef : DatabaseReference
    private lateinit var mAuth : FirebaseAuth
    private val vendorMaterials = arrayListOf<VendorMaterialModel>()
    private val selectedMaterials = arrayListOf<VendorMaterialModel>()
    private lateinit var materialsAdapter: ChooseVendorMaterialAdapter
    private lateinit var selectionAdapter : SelectedMaterialsAdapter
    private var siteName     : String? = ""
    private var siteAddress  : String? = ""
    private var driverName   : String? = ""
    private var driverID     : String? = ""
    private var driverContact: String? = ""
    private var vendorName   : String? = ""
    private var vendorAddress: String? = ""
    private var vendorID     : String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            driverContact= it.getString("driverContact","")
            siteName = it.getString("siteName","")
            siteAddress = it.getString("siteAddress","")
            driverID = it.getString("driverID","")
            driverName = it.getString("driverName","")
            vendorID = it.getString("vendorID","")
            vendorName = it.getString("vendorName","")
            vendorAddress = it.getString("vendorAddress","")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_add_package_content, container, false)
        rootRef = FirebaseDatabase.getInstance().reference
        mAuth = FirebaseAuth.getInstance()

        selectionAdapter = SelectedMaterialsAdapter(requireActivity(),binding.noMaterialsTV,selectedMaterials) // DO NOT Move This line below the "materialsAdapter" initialization
        materialsAdapter = ChooseVendorMaterialAdapter(requireActivity(),binding.noMaterialsTV,vendorMaterials,selectedMaterials,selectionAdapter)

        binding.vendorMaterialsRV.layoutManager = LinearLayoutManager(requireActivity())
        binding.vendorMaterialsRV.adapter = materialsAdapter

        binding.packageContentsRV.layoutManager = GridLayoutManager(requireActivity(),3,RecyclerView.VERTICAL,false)
        binding.packageContentsRV.adapter = selectionAdapter

        getVendorMaterials()

        binding.addPackageBtn.setOnClickListener {
            addNewPackage()
        }

        return binding.root
    }

    private fun getCurrentDate() : String {
        return SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
    }

    private fun getVendorMaterials() {
        if(mAuth.uid!=null){
            val materialsRef = rootRef.child("VendorMaterials").child(vendorID!!)
            vendorMaterials.clear()
            materialsRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    binding.progressBar.visibility = View.GONE

                    if(dataSnapshot.childrenCount>0){
                        for(snapshot: DataSnapshot in dataSnapshot.children){
                            val materialID : String? = snapshot.key
                            if(materialID!=null){
                                val name : String? = snapshot.child("name").value as String?
                                val colour : String? = snapshot.child("colour").value as String?
                                val dimensions : String? = snapshot.child("dimensions").value as String?
                                val weight : String? = snapshot.child("weight").value as String?

                                if(name!=null && weight!=null && dimensions!=null && colour!=null){
                                    vendorMaterials.add(VendorMaterialModel(materialID,name,weight,dimensions,colour))
                                    materialsAdapter.notifyDataSetChanged()
                                }
                            }
                        }
                    }
                    else{
                        Toast.makeText(requireActivity(),"This vendor has no materials in his list.",Toast.LENGTH_LONG).show()
                        findNavController().navigate(R.id.action_back_add_packageContent_to_add_new_package)
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
        }
    }

    private fun showSnack(msg : String){
        Snackbar.make(binding.addPackageContentsContainer,msg,Snackbar.LENGTH_LONG).show()
    }

    @SuppressLint("SetTextI18n")
    private fun addNewPackage(){
        if(selectedMaterials.isEmpty()){
            showSnack("Package has no contents.")
            return
        }
        val orderInfoRef = rootRef.child("Orders").child(mAuth.uid!!)
        val orderID = orderInfoRef.push().key
        if(orderID!=null){
            val orderMap : HashMap<String,String> = HashMap()
            orderMap["managerID"]     = mAuth.uid!!
            orderMap["driverContact"] = driverContact!!
            orderMap["dateAdded"]     = getCurrentDate()
            orderMap["status"]        = "pending"
            orderMap["siteName"]      = siteName!!
            orderMap["siteAddress"]   = siteAddress!!
            orderMap["driverName"]    = driverName!!
            orderMap["driverID"]      = driverID!!
            orderMap["vendorName"]    = vendorName!!
            orderMap["vendorAddress"] = vendorAddress!!
            orderMap["vendorID"]      = vendorID!!

            binding.progressLayout.visibility = View.VISIBLE
            orderInfoRef.child(orderID).setValue(orderMap).addOnCompleteListener { perlimanaryTask->
                if(perlimanaryTask.isSuccessful){
                    orderInfoRef.child(orderID).child("packageContents").setValue(selectedMaterials).addOnCompleteListener {
                        if(it.isSuccessful){
                            binding.progressMsg.text = "Notifying driver ....."
                            val driverRef = rootRef.child("DriverOrders").child(driverID!!).child(orderID)
                            driverRef.setValue(orderMap).addOnCompleteListener { finalTask->
                                if(finalTask.isSuccessful){
                                    Toast.makeText(requireActivity(),"Package successfully added for delivery.",Toast.LENGTH_LONG).show()
                                }
                                else{
                                    Toast.makeText(requireActivity(),"Failed to add package",Toast.LENGTH_LONG).show()
                                }
                                findNavController().navigate(R.id.action_addPackageContentFrag_to_navigation_home)
                            }
                        }
                        else{
                            showSnack("Failed to add package contents")
                        }
                    }
                }
                else{
                    binding.progressLayout.visibility = View.GONE
                    showSnack("Something went wrong :(")
                }
            }
        }
    }
}