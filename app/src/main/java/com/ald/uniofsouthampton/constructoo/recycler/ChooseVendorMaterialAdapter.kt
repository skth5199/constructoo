package com.ald.uniofsouthampton.constructoo.recycler

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ald.uniofsouthampton.constructoo.R

class ChooseVendorMaterialAdapter(private val context: Context,
                                  private val noMaterialsTV : TextView,
                                  private val materialsList: ArrayList<VendorMaterialModel>,
                                  private val selectionList: ArrayList<VendorMaterialModel>,
                                  private val selectedMaterialsAdapter: SelectedMaterialsAdapter
                                  ) : RecyclerView.Adapter<ChooseVendorMaterialAdapter.VendorsListViewHolder>() {

    class VendorsListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTV : TextView = itemView.findViewById(R.id.materialNameTV)
        val weightTV : TextView = itemView.findViewById(R.id.materialWeightTV)
        val colourTV : TextView = itemView.findViewById(R.id.materialColourTV)
        val dimensionsTV : TextView = itemView.findViewById(R.id.materialDimensionsTV)
        val materialItem : RelativeLayout = itemView.findViewById(R.id.materialItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VendorsListViewHolder {
        return VendorsListViewHolder(LayoutInflater.from(context).inflate(R.layout.vendor_material_item, parent, false))
    }

    override fun onBindViewHolder(holder: VendorsListViewHolder, position: Int) {
        val material = materialsList[position]

        holder.nameTV.text = material.name
        holder.weightTV.text = material.weight + " KG"
        holder.colourTV.text = material.colour
        holder.dimensionsTV.text = material.dimensions

        holder.materialItem.setOnClickListener {
            selectionList.add(material)
            noMaterialsTV.visibility = View.GONE
            selectedMaterialsAdapter.notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return materialsList.size
    }
}