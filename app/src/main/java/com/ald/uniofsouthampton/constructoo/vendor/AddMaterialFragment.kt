package com.ald.uniofsouthampton.constructoo.vendor

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.ald.uniofsouthampton.constructoo.R
import com.ald.uniofsouthampton.constructoo.databinding.FragmentAddMaterialBinding
import com.ald.uniofsouthampton.constructoo.recycler.VendorMaterialModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AddMaterialFragment : Fragment() {

    private lateinit var binding : FragmentAddMaterialBinding
    private lateinit var rootRef : DatabaseReference
    private lateinit var mAuth : FirebaseAuth
    private var uid : String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_add_material, container, false)
        rootRef = FirebaseDatabase.getInstance().reference
        mAuth = FirebaseAuth.getInstance()

        if(mAuth.currentUser == null){findNavController().popBackStack()}
        else{ uid = mAuth.currentUser!!.uid }

        VendorsActivity.vendorActionBar?.show()
        VendorsActivity.vendorActionBar?.title = "Add New Material"
        VendorsActivity.vendorActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.addNewMaterialFAB.setOnClickListener { checkFields() }
        return binding.root
    }

    private fun checkFields(){
        val name = binding.materialNameET.text.toString()
        if(name.length<3){
            showSnackMessage("Material name must contain at least 3 characters")
            return
        }
        val weight = binding.materialWeightET.text.toString()
        if(weight.isEmpty()){
            showSnackMessage("Material weight can not be left empty")
            return
        }
        val dimensions = binding.materialDimensionsET.text.toString()
        if(dimensions.length<3){
            showSnackMessage("Material name must contain at least 3 characters")
            return
        }
        val colour = binding.materialColourET.text.toString()
        if(colour.length<3){
            showSnackMessage("material colour must contain at least 3 characters")
            return
        }

        saveNewMaterialToDB(name,weight,dimensions,colour)
    }

    private fun saveNewMaterialToDB(
        name: String,
        weight: String,
        dimensions: String,
        colour: String
    ) {
        binding.progressLayout.visibility = View.VISIBLE

        val mRef = rootRef.child("VendorMaterials").child(uid!!)
        val materialID = mRef.push().key

        if (materialID != null) {
            mRef.child(materialID).setValue(VendorMaterialModel(materialID,name,weight,dimensions,colour)).addOnCompleteListener {

                binding.progressLayout.visibility = View.GONE

                if(it.isSuccessful){
                    Toast.makeText(requireActivity(),"Successfully added material to your list.",Toast.LENGTH_LONG).show()
                    findNavController().popBackStack()
                }
                else{
                    showSnackMessage("failed to add material to your list.")
                }
            }
        }
        else{
            binding.progressLayout.visibility = View.GONE
            showSnackMessage("Failed to generate new material ID.")
        }
    }

    private fun showSnackMessage(msg : String){
        Snackbar.make(binding.addMaterialContainer,msg,Snackbar.LENGTH_LONG).show()
    }

}