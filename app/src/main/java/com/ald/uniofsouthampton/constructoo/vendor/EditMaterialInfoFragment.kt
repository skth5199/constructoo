package com.ald.uniofsouthampton.constructoo.vendor

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.ald.uniofsouthampton.constructoo.R
import com.ald.uniofsouthampton.constructoo.authentication.AuthActivity
import com.ald.uniofsouthampton.constructoo.databinding.FragmentAddMaterialBinding
import com.ald.uniofsouthampton.constructoo.databinding.FragmentEditMaterialInfoBinding
import com.ald.uniofsouthampton.constructoo.recycler.VendorMaterialModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class EditMaterialInfoFragment : Fragment() {

    private lateinit var binding : FragmentEditMaterialInfoBinding
    private lateinit var rootRef : DatabaseReference
    private lateinit var mAuth : FirebaseAuth
    private var materialID : String? = null
    private var materialName  : String? = null
    private var materialColour : String? = null
    private var materialDimensions : String? = null
    private var materialWeight  : String? = null
    private var uid : String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_edit_material_info, container, false)
        rootRef = FirebaseDatabase.getInstance().reference
        mAuth = FirebaseAuth.getInstance()

        if(mAuth.currentUser == null || arguments==null){findNavController().popBackStack()}
        else{ uid = mAuth.currentUser!!.uid }

        materialID = arguments?.getString("materialID" , "")
        materialName = arguments?.getString("name" , "")
        materialColour = arguments?.getString("colour" , "")
        materialDimensions = arguments?.getString("dimensions" , "")
        materialWeight = arguments?.getString("weight" , "")

        if(materialID.isNullOrEmpty() || materialName.isNullOrEmpty() || materialColour.isNullOrEmpty() || materialDimensions.isNullOrEmpty() || materialWeight.isNullOrEmpty()){
            findNavController().popBackStack()
        }

        binding.materialNameET.setText(materialName)
        binding.materialColourET.setText(materialColour)
        binding.materialDimensionsET.setText(materialDimensions)
        binding.materialWeightET.setText(materialWeight)

        VendorsActivity.vendorActionBar?.show()
        VendorsActivity.vendorActionBar?.title = "Material Info"
        VendorsActivity.vendorActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.updateInfoFAB.setOnClickListener { checkFields() }
        binding.deleteMaterialFAB.setOnClickListener { deleteMaterial() }

        return binding.root
    }


    private fun checkFields(){
        val name = binding.materialNameET.text.toString()
        if(name.length<3){
            showSnackMessage("Material name must contain at least 3 characters.")
            return
        }
        val weight = binding.materialWeightET.text.toString()
        if(weight.isEmpty()){
            showSnackMessage("Material weight can not be left empty.")
            return
        }
        val dimensions = binding.materialDimensionsET.text.toString()
        if(dimensions.length<3){
            showSnackMessage("Material name must contain at least 3 characters.")
            return
        }
        val colour = binding.materialColourET.text.toString()
        if(colour.length<3){
            showSnackMessage("material colour must contain at least 3 characters.")
            return
        }
        if(name==materialName && dimensions == materialDimensions && weight == materialWeight && colour == materialColour){
            showSnackMessage("No updates to the values were detected.")
            return
        }
        saveChangesToDB(VendorMaterialModel(materialID!!,name,weight,dimensions,colour))
    }

    private fun deleteMaterial(){

        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("Confirm action")
        builder.setMessage("Delete this material from your list?")

        builder.setPositiveButton("Delete") { dialog, which ->
            val mRef = rootRef.child("VendorMaterials").child(uid!!).child(materialID!!)
            mRef.setValue(null).addOnCompleteListener {
                if(it.isSuccessful){
                    Toast.makeText(requireActivity(),"Material deleted from your list.",Toast.LENGTH_LONG).show()
                    findNavController().popBackStack()
                }
                else{
                    showSnackMessage("failed to delete material from your list.")
                }
            }
        }

        builder.setNegativeButton("Cancel") { dialog, which ->
            dialog.cancel()
        }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun saveChangesToDB(materialModel : VendorMaterialModel){
        binding.progressLayout.visibility = View.VISIBLE

        if (materialID != null) {
            val mRef = rootRef.child("VendorMaterials").child(uid!!).child(materialID!!)
            mRef.setValue(materialModel).addOnCompleteListener {

                binding.progressLayout.visibility = View.GONE

                if(it.isSuccessful){
                    showSnackMessage("Successfully updated material info.")
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
        Snackbar.make(binding.addMaterialContainer,msg, Snackbar.LENGTH_LONG).show()
    }

}