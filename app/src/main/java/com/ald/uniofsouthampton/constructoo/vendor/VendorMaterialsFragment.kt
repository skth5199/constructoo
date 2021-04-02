package com.ald.uniofsouthampton.constructoo.vendor

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
import com.ald.uniofsouthampton.constructoo.databinding.FragmentVendorMaterialsBinding
import com.ald.uniofsouthampton.constructoo.recycler.MyVendorsAdapter
import com.ald.uniofsouthampton.constructoo.recycler.VendorMaterialModel
import com.ald.uniofsouthampton.constructoo.recycler.VendorMaterialsAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class VendorMaterialsFragment : Fragment() {

    private lateinit var binding: FragmentVendorMaterialsBinding
    private lateinit var rootRef: DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    private val materialsList = arrayListOf<VendorMaterialModel>()
    private val idList = arrayListOf<String>()
    private lateinit var adapter: VendorMaterialsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_vendor_materials, container, false)
        rootRef = FirebaseDatabase.getInstance().reference
        mAuth = FirebaseAuth.getInstance()

        VendorsActivity.vendorActionBar?.show()
        VendorsActivity.vendorActionBar?.title = "Materials List"
        VendorsActivity.vendorActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.addMaterialFAB.setOnClickListener {
            findNavController().navigate(R.id.action_vendorMaterialsFragment_to_addMaterialFragment)
        }

        // set up RecyclerView
        binding.vendorMaterialsRV.layoutManager = LinearLayoutManager(requireActivity())
        adapter = VendorMaterialsAdapter(requireActivity(), materialsList,findNavController())
        binding.vendorMaterialsRV.adapter = adapter
        getMyMaterialsList()

        return binding.root
    }

    private fun getMyMaterialsList() {
        if(mAuth.uid!=null){

            val materialsRef = rootRef.child("VendorMaterials").child(mAuth.uid!!)

            materialsList.clear()
            idList.clear()

            materialsRef.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    binding.progressBar.visibility = View.GONE

                    if(dataSnapshot.childrenCount>0){
                        for(snapshot: DataSnapshot in dataSnapshot.children){
                            val materialID : String? = snapshot.key
                            if(materialID!=null && !idList.contains(materialID)){ // To avoid adding duplicates
                                val name : String? = snapshot.child("name").value as String?
                                val colour : String? = snapshot.child("colour").value as String?
                                val dimensions : String? = snapshot.child("dimensions").value as String?
                                val weight : String? = snapshot.child("weight").value as String?

                                if(name!=null && weight!=null && dimensions!=null && colour!=null){
                                    if(!idList.contains(materialID)){
                                        idList.add(materialID)
                                        materialsList.add(VendorMaterialModel(materialID,name,weight,dimensions,colour))
                                        adapter.notifyDataSetChanged()
                                    }
                                }
                            }
                        }
                    }
                    else{
                        binding.noMaterialsLayout.visibility = View.VISIBLE
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }
    }

}