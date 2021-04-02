package com.ald.uniofsouthampton.constructoo.recycler

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.ald.uniofsouthampton.constructoo.R
import com.ald.uniofsouthampton.constructoo.manager.manager_fragments.AddVendorFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MyVendorsAdapter(private val context: Context,
                       private val myVendorsList : ArrayList<AddVendorFragment.VendorModel>) : RecyclerView.Adapter<MyVendorsAdapter.VendorsListViewHolder>() {
    class VendorsListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTV : TextView = itemView.findViewById(R.id.vendorNameTV)
        val emailTV : TextView = itemView.findViewById(R.id.vendorEmailTV)
        val addressTV : TextView = itemView.findViewById(R.id.vendorAddressTV)
        val deleteVendorFAB : FloatingActionButton = itemView.findViewById(R.id.deleteVendorFAB)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VendorsListViewHolder {
        return VendorsListViewHolder(LayoutInflater.from(context).inflate(R.layout.item_my_vendors, parent, false))
    }

    override fun onBindViewHolder(holder: VendorsListViewHolder, position: Int) {
        val vendor = myVendorsList[position]
        holder.nameTV.text = vendor.vendorName
        holder.emailTV.text = vendor.vendorEmail
        holder.addressTV.text = vendor.vendorAddress
        holder.deleteVendorFAB.setOnClickListener {
            deleteVendor(vendor.vendorID,holder.deleteVendorFAB,position)
        }
    }

    private fun deleteVendor(vendorID: String, deleteVendorFAB: FloatingActionButton, position: Int){
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Remove Vendor")
        builder.setMessage("Are you sure you want to remove this vendor from your list?")
        builder.setCancelable(false)
        builder.setPositiveButton("Yes") { dialog, which ->
            deleteVendorFAB.isClickable = false
            FirebaseDatabase.getInstance().reference
                .child("ManagerVendors")
                .child(FirebaseAuth.getInstance().uid!!)
                .child(vendorID)
                .setValue(null)
                .addOnCompleteListener {
                    if(it.isSuccessful){
                        myVendorsList.removeAt(position)
                        notifyDataSetChanged()
                    }
                    else{
                        deleteVendorFAB.isClickable = true
                    }
            }
        }
        builder.setNegativeButton("Cancel") { dialog, which ->
            dialog.dismiss()
        }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    override fun getItemCount(): Int {
        return myVendorsList.size
    }
}