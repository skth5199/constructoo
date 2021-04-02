package com.ald.uniofsouthampton.constructoo.vendor

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.ald.uniofsouthampton.constructoo.R
import com.ald.uniofsouthampton.constructoo.databinding.FragmentVendorSettingsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class VendorSettingsFragment : Fragment() {

    private lateinit var binding : FragmentVendorSettingsBinding
    private lateinit var rootRef : DatabaseReference
    private lateinit var mAuth : FirebaseAuth

    private var username : String? = null
    private var address : String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_vendor_settings,container,false)
        VendorsActivity.vendorActionBar?.show()
        VendorsActivity.vendorActionBar?.title = "Vendor Info"
        VendorsActivity.vendorActionBar?.setDisplayHomeAsUpEnabled(true)
        rootRef = FirebaseDatabase.getInstance().reference.child("Users")
        mAuth = FirebaseAuth.getInstance()

        getVendorInfo()
        binding.saveVendorInfoFAB.setOnClickListener {
            updateInfo()
        }
        return binding.root
    }

    private fun getVendorInfo(){
        val uid = mAuth.currentUser?.uid

        if(uid!=null){
            val email = mAuth.currentUser?.email
            if(email!=null){binding.emailET.setText(email)}

            val vendorRef = rootRef.child("Vendor").child(uid)
            vendorRef.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    username = snapshot.child("username").value as String?
                    address  = snapshot.child("address").value as String?
                    if(username!=null){
                        binding.usernameET.setText(username)
                    }
                    if(address!=null){
                        binding.vendorAddressET.setText(address)
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }
    }

    private fun updateInfo(){
        val username = binding.usernameET.text.toString()
        val address = binding.vendorAddressET.text.toString()

        if(username.length<3){
            Toast.makeText(requireActivity(),"Username must contain at least 3 characters.",Toast.LENGTH_LONG).show()
            return
        }
        if(address.length<8){
            Toast.makeText(requireActivity(),"Username must contain at least 8 characters.",Toast.LENGTH_LONG).show()
            return
        }
        binding.saveVendorInfoFAB.isClickable = false
        binding.progressBar.visibility = View.VISIBLE
        val uid = mAuth.currentUser?.uid
        val vendorRef = rootRef.child("Vendor").child(uid!!)
        vendorRef.child("username").setValue(username)
        vendorRef.child("address").setValue(address).addOnCompleteListener {
            binding.saveVendorInfoFAB.isClickable = true
            binding.progressBar.visibility = View.GONE
            if(it.isSuccessful){
                Toast.makeText(requireActivity(),"Info updated successfully",Toast.LENGTH_LONG).show()
            }
            else{
                Toast.makeText(requireActivity(),"Failed to update info",Toast.LENGTH_LONG).show()
            }
        }
    }

}