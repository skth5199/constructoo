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
import com.ald.uniofsouthampton.constructoo.manager.manager_fragments.AddDriverFragment
import com.ald.uniofsouthampton.constructoo.manager.manager_fragments.AddVendorFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MyDriversAdapter(private val context: Context,
                       private val driversList : ArrayList<AddDriverFragment.DriverModel>) : RecyclerView.Adapter<MyDriversAdapter.VendorsListViewHolder>() {
    class VendorsListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTV : TextView = itemView.findViewById(R.id.vendorNameTV)
        val emailTV : TextView = itemView.findViewById(R.id.vendorEmailTV)
        val deleteDriverFAB : FloatingActionButton = itemView.findViewById(R.id.deleteVendorFAB)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VendorsListViewHolder {
        return VendorsListViewHolder(LayoutInflater.from(context).inflate(R.layout.item_my_vendors, parent, false))
    }

    override fun onBindViewHolder(holder: VendorsListViewHolder, position: Int) {
        val driver = driversList[position]
        holder.nameTV.text = driver.driverName
        holder.emailTV.text = driver.driverEmail
        holder.deleteDriverFAB.setOnClickListener {
            deleteVendor(driver.driverID,holder.deleteDriverFAB,position)
        }
    }

    private fun deleteVendor(driverID: String, deleteDriverFAB: FloatingActionButton, position: Int){
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Remove Driver")
        builder.setMessage("Are you sure you want to remove this driver from your list?")
        builder.setCancelable(false)
        builder.setPositiveButton("Yes") { dialog, which ->
            deleteDriverFAB.isClickable = false
            FirebaseDatabase.getInstance().reference
                .child("ManagerDrivers")
                .child(FirebaseAuth.getInstance().uid!!)
                .child(driverID)
                .setValue(null)
                .addOnCompleteListener {
                    if(it.isSuccessful){
                        driversList.removeAt(position)
                        notifyDataSetChanged()
                    }
                    else{
                        deleteDriverFAB.isClickable = true
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
        return driversList.size
    }
}