package com.ald.uniofsouthampton.constructoo.recycler

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.ald.uniofsouthampton.constructoo.R
import com.ald.uniofsouthampton.constructoo.manager.manager_fragments.AddConstructionSiteFrag
import com.ald.uniofsouthampton.constructoo.manager.manager_fragments.AddVendorFragment
import com.ald.uniofsouthampton.constructoo.manager.manager_fragments.ManageConstructionSiteFrag
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MySitesAdapter(private val context: Context,
                     private val mySitesList : ArrayList<ManageConstructionSiteFrag.ManagerConstSitesModel>) : RecyclerView.Adapter<MySitesAdapter.VendorsListViewHolder>()
{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VendorsListViewHolder {
        return VendorsListViewHolder(LayoutInflater.from(context).inflate(R.layout.item_my_vendors, parent, false))
    }

    override fun onBindViewHolder(holder: VendorsListViewHolder, position: Int) {
        val siteInfo = mySitesList[position]
        holder.nameTV.text = siteInfo.siteManager
        holder.emailTV.text = siteInfo.siteManagerContact
        holder.addressTV.text = siteInfo.siteAddress
        holder.deleteSiteFAB.setOnClickListener {
            deleteVendor(siteInfo.siteID,holder.deleteSiteFAB,position)
        }
    }

    private fun deleteVendor(siteID: String, deleteSiteFAB: FloatingActionButton, position: Int){
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Remove Building Site")
        builder.setMessage("Are you sure you want to remove this site from your list?")
        builder.setCancelable(false)
        builder.setPositiveButton("Yes") { dialog, which ->
            deleteSiteFAB.isClickable = false
            FirebaseDatabase.getInstance().reference
                .child("ManagerSites")
                .child(FirebaseAuth.getInstance().uid!!)
                .child(siteID)
                .setValue(null)
                .addOnCompleteListener {
                    if(it.isSuccessful){
                        mySitesList.removeAt(position)
                        notifyDataSetChanged()
                    }
                    else{
                        deleteSiteFAB.isClickable = true
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
        return mySitesList.size
    }

    class VendorsListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTV : TextView = itemView.findViewById(R.id.vendorNameTV)
        val emailTV : TextView = itemView.findViewById(R.id.vendorEmailTV)
        val addressTV : TextView = itemView.findViewById(R.id.vendorAddressTV)
        val deleteSiteFAB : FloatingActionButton = itemView.findViewById(R.id.deleteVendorFAB)
    }
}