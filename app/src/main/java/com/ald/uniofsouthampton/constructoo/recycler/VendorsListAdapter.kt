package com.ald.uniofsouthampton.constructoo.recycler

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.ald.uniofsouthampton.constructoo.R
import com.ald.uniofsouthampton.constructoo.manager.manager_fragments.AddVendorFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class VendorsListAdapter(private val context: Context,
                         private val vendorsList : List<AddVendorFragment.VendorModel>,
                         private val parentNavController: NavController) : RecyclerView.Adapter<VendorsListAdapter.VendorsListViewHolder>() {
    class VendorsListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val vendorNameTV : TextView = itemView.findViewById(R.id.vendorNameTV)
        val vendorEmailTV : TextView = itemView.findViewById(R.id.vendorEmailTV)
        val prgBar : ProgressBar = itemView.findViewById(R.id.item_prg)
        val addVendorFAB : FloatingActionButton = itemView.findViewById(R.id.addVendorFAB)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VendorsListViewHolder {
        return VendorsListViewHolder(LayoutInflater.from(context).inflate(R.layout.item_list_vendors, parent, false))
    }

    override fun onBindViewHolder(holder: VendorsListViewHolder, position: Int) {
        val vendor = vendorsList[position]
        holder.vendorNameTV.text = vendor.vendorName
        holder.vendorEmailTV.text = vendor.vendorEmail
        holder.addVendorFAB.setOnClickListener {
            val uid : String? = FirebaseAuth.getInstance().uid
            if(uid!=null){
                holder.prgBar.visibility = View.VISIBLE
                holder.addVendorFAB.visibility = View.GONE
                val dbRef = FirebaseDatabase.getInstance().reference.child("ManagerVendors").child(uid).child(vendor.vendorID)
                dbRef.setValue(vendor).addOnCompleteListener {
                    if(it.isSuccessful){
                        Toast.makeText(context,"Successfully added new vendor to your list.", Toast.LENGTH_LONG).show()
                        parentNavController.navigate(R.id.action_back_add_vendor)
                    }
                    else{
                        holder.prgBar.visibility = View.GONE
                        holder.addVendorFAB.visibility = View.VISIBLE
                        Toast.makeText(context,"Failed to add vendor to your list.", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return vendorsList.size
    }
}