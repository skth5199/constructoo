package com.ald.uniofsouthampton.constructoo.manager.manager_fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.ald.uniofsouthampton.constructoo.R
import com.ald.uniofsouthampton.constructoo.databinding.FragmentAddNewPackageBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class AddNewPackageFrag : Fragment() {

    private lateinit var binding : FragmentAddNewPackageBinding
    private lateinit var rootRef : DatabaseReference
    private lateinit var mAuth : FirebaseAuth
    private var sitesDbListener   : ValueEventListener? = null
    private var driversDbListener : ValueEventListener? = null
    private var vendorsDbListener : ValueEventListener? = null
    private var vendorsRef : DatabaseReference? = null
    private var driversRef : DatabaseReference? = null
    private var sitesRef : DatabaseReference? = null
    private val siteNames = arrayListOf<String>()
    private val driverNames = arrayListOf<String>()
    private val vendorNames = arrayListOf<String>()
    private val mySitesList = arrayListOf<PartialInfoModel>()
    private val myDriversList = arrayListOf<PartialInfoModel>()
    private val myVendorsList = arrayListOf<PartialInfoModel>()
    private var vIndex = 0
    private var dIndex = 0
    private var sIndex = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_add_new_package, container, false)
        rootRef = FirebaseDatabase.getInstance().reference
        mAuth = FirebaseAuth.getInstance()

        if(mAuth.uid==null){findNavController().popBackStack()} // basically end fragment if managerID is null

        getManagerSitesList()
        getManagerDriversList()
        getManagerVendorsList()

        binding.nextFAB.setOnClickListener {
            if(vIndex < 0){
                showSnack("Please select the vendor to get the materials from.")
                return@setOnClickListener
            }
            if(dIndex < 0){
                showSnack("Please select the driver to deliver the package.")
                return@setOnClickListener
            }
            if(sIndex < 0){
                showSnack("Please select the construction site where this package is to be delivered.")
                return@setOnClickListener
            }
            val extras = Bundle()
            extras.putString("siteName"     ,mySitesList[sIndex].name)
            extras.putString("siteAddress"  ,mySitesList[sIndex].address)

            extras.putString("driverName"   ,myDriversList[dIndex].name)
            extras.putString("driverID"     ,myDriversList[dIndex].id)
            extras.putString("driverContact",myDriversList[dIndex].address)

            extras.putString("vendorID"     ,myVendorsList[vIndex].id)
            extras.putString("vendorName"   ,myVendorsList[vIndex].name)
            extras.putString("vendorAddress",myVendorsList[vIndex].address)
            findNavController().navigate(R.id.action_addNewPackageFrag_to_addPackageContentFrag,extras)
        }

        return binding.root
    }


    override fun onDestroy() {
        if(sitesDbListener!=null){
            sitesRef?.removeEventListener(sitesDbListener!!)
        }
        if(vendorsDbListener!=null){
            vendorsRef?.removeEventListener(vendorsDbListener!!)
        }
        if(driversDbListener!=null){
            driversRef?.removeEventListener(driversDbListener!!)
        }
        super.onDestroy()
    }

    private fun showSnack(msg : String){
        Snackbar.make(binding.addNewPackageContainer,msg,Snackbar.LENGTH_LONG).show()
    }

    private fun showToast(msg : String){
        Toast.makeText(requireActivity(),msg,Toast.LENGTH_LONG).show()
    }

    private fun getManagerSitesList(){
        sitesRef = rootRef.child("ManagerSites").child(mAuth.uid!!)
        sitesDbListener = (object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.childrenCount>0){
                    binding.sitePrgBar.visibility = View.GONE
                    binding.siteSpinnerLayout.visibility = View.VISIBLE
                    mySitesList.clear()
                    siteNames.clear()
                    siteNames.add("Choose")
                    for(snap: DataSnapshot in snapshot.children){
                        val siteName : String? = snap.child("siteName").value as String?
                        val siteAddress : String? = snap.child("siteAddress").value as String?
                        if(snap.key!=null && siteName!=null && siteAddress!=null){
                            mySitesList.add(PartialInfoModel(snap.key!!,siteName,siteAddress))
                            siteNames.add(siteName)
                        }
                    }
                    if(mySitesList.size>0){
                        setSiteSpinner()
                    }
                }
                else{
                    showToast("Can not add new package\nNo Construction Sites found in your list.")
                    findNavController().navigate(R.id.add_new_package_to_home)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
        if(sitesDbListener!=null)
         sitesRef?.addValueEventListener(sitesDbListener!!)
    }

    private fun getManagerDriversList(){
        driversRef = rootRef.child("ManagerDrivers").child(mAuth.uid!!)
        driversDbListener = (object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.childrenCount>0){
                    binding.driverPrgBar.visibility = View.GONE
                    binding.driverSpinnerLayout.visibility = View.VISIBLE
                    driverNames.clear()
                    driverNames.add("Choose")
                    myDriversList.clear()
                    for(snap: DataSnapshot in snapshot.children){
                        val driverID : String? = snap.key
                        val driverName : String? = snap.child("driverName").value as String?
                        val driverEmail : String? = snap.child("driverEmail").value as String?
                        val driverContactNum : String? = snap.child("contactNum").value as String?

                        if(driverID!=null && driverName!=null && driverEmail!=null&&driverContactNum!=null){
                            myDriversList.add(PartialInfoModel(driverID,driverName,driverContactNum))
                            driverNames.add(driverName)
                        }
                    }
                    if(myDriversList.size>0){
                        setDriversSpinner()
                    }
                }
                else{
                    showToast("Can not add new package\nNo Drivers found in your list.")
                    findNavController().navigate(R.id.add_new_package_to_home)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
        if(driversDbListener!=null)
         driversRef?.addValueEventListener(driversDbListener!!)
    }

    private fun getManagerVendorsList(){
        vendorsRef = rootRef.child("ManagerVendors").child(mAuth.uid!!)
        vendorsDbListener = (object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.childrenCount>0){
                    binding.vendorPrgBar.visibility = View.GONE
                    binding.vendorSpinnerLayout.visibility = View.VISIBLE
                    vendorNames.clear()
                    vendorNames.add("Choose")
                    myVendorsList.clear()
                    for(snap: DataSnapshot in snapshot.children){
                        val vendorID : String? = snap.key
                        val vendorName : String? = snap.child("vendorName").value as String?
                        val vendorAddress : String? = snap.child("vendorAddress").value as String?

                        if(vendorID!=null && vendorName!=null && vendorAddress!=null){
                            myVendorsList.add(PartialInfoModel(vendorID,vendorName,vendorAddress))
                            vendorNames.add(vendorName)
                        }
                    }
                    if(myVendorsList.size>0){
                        setVendorsSpinner()
                    }
                }
                else{
                    showToast("Can not add new package\nNo Vendors found in your list.")
                    findNavController().navigate(R.id.add_new_package_to_home)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
        if(vendorsDbListener!=null)
          vendorsRef?.addValueEventListener(vendorsDbListener!!)
    }

    private fun setSiteSpinner(){
        val aa = ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, siteNames)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.siteSpinner.adapter = aa
        binding.siteSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                sIndex = pos-1
            }
        }
    }

    private fun setDriversSpinner(){
        val aa = ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, driverNames)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.driverSpinner.adapter = aa
        binding.driverSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                dIndex = position-1
            }
        }
    }

    private fun setVendorsSpinner(){
        val aa = ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, vendorNames)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.vendorSpinner.adapter = aa
        binding.vendorSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                vIndex = position-1
            }
        }
    }

}

private data class PartialInfoModel (val id : String,val name : String , val address : String)