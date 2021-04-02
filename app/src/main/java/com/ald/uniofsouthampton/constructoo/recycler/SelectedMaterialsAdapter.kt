package com.ald.uniofsouthampton.constructoo.recycler

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.ald.uniofsouthampton.constructoo.R

class SelectedMaterialsAdapter(private val context: Context,
                               private val noMaterialsTV : TextView,
                               private val selectedMaterials : ArrayList<VendorMaterialModel>) : RecyclerView.Adapter<SelectedMaterialsAdapter.VendorsListViewHolder>() {

    class VendorsListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTV : TextView = itemView.findViewById(R.id.materialTitleTV)
        val removeIV : ImageView = itemView.findViewById(R.id.removeIV)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VendorsListViewHolder {
        return VendorsListViewHolder(LayoutInflater.from(context).inflate(R.layout.selected_materials_item, parent, false))
    }

    override fun onBindViewHolder(holder: VendorsListViewHolder, position: Int) {
        holder.nameTV.text = selectedMaterials[position].name

        holder.removeIV.setOnClickListener {
            selectedMaterials.removeAt(position)
            notifyDataSetChanged()
            if(selectedMaterials.isEmpty()){
                noMaterialsTV.visibility = View.VISIBLE
            }
        }
    }

    override fun getItemCount(): Int {
        return selectedMaterials.size
    }
}