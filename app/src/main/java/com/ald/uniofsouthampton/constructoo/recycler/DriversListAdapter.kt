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
import com.ald.uniofsouthampton.constructoo.manager.manager_fragments.AddDriverFragment
import com.ald.uniofsouthampton.constructoo.manager.manager_fragments.AddVendorFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class DriversListAdapter(private val context: Context,
                         private val driversList : List<AddDriverFragment.DriverModel>,
                         private val parentNavController: NavController) : RecyclerView.Adapter<DriversListAdapter.VendorsListViewHolder>() {
    class VendorsListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val driverNameTV : TextView = itemView.findViewById(R.id.vendorNameTV)
        val driverEmailTV : TextView = itemView.findViewById(R.id.vendorEmailTV)
        val prgBar : ProgressBar = itemView.findViewById(R.id.item_prg)
        val addDriverFAB : FloatingActionButton = itemView.findViewById(R.id.addVendorFAB)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VendorsListViewHolder {
        return VendorsListViewHolder(LayoutInflater.from(context).inflate(R.layout.item_list_vendors, parent, false))
    }

    override fun onBindViewHolder(holder: VendorsListViewHolder, position: Int) {
        val driver = driversList[position]
        holder.driverNameTV.text = driver.driverName
        holder.driverEmailTV.text = driver.driverEmail
        holder.addDriverFAB.setOnClickListener {
            val uid : String? = FirebaseAuth.getInstance().uid
            if(uid!=null){
                holder.prgBar.visibility = View.VISIBLE
                holder.addDriverFAB.visibility = View.GONE
                val dbRef = FirebaseDatabase.getInstance().reference.child("ManagerDrivers").child(uid).child(driver.driverID)
                dbRef.setValue(driver).addOnCompleteListener {
                    if(it.isSuccessful){
                        Toast.makeText(context,"Successfully added new driver to your list.", Toast.LENGTH_LONG).show()
                        parentNavController.navigate(R.id.action_back_add_driver)
                    }
                    else{
                        holder.prgBar.visibility = View.GONE
                        holder.addDriverFAB.visibility = View.VISIBLE
                        Toast.makeText(context,"Failed to add drivers to your list.", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return driversList.size
    }
}