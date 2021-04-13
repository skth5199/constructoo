package com.ald.uniofsouthampton.constructoo.manager.manager_fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ald.uniofsouthampton.constructoo.R
import com.ald.uniofsouthampton.constructoo.databinding.FragmentAddDriverBinding
import com.ald.uniofsouthampton.constructoo.recycler.DriversListAdapter
import com.google.firebase.database.*


class AddDriverFragment : Fragment() {

    private lateinit var binding : FragmentAddDriverBinding
    private lateinit var rootRef : DatabaseReference
    private val driversList = arrayListOf<DriverModel>()
    private val idList = arrayListOf<String>()
    private lateinit var adapter : DriversListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_add_driver, container, false)
        rootRef = FirebaseDatabase.getInstance().reference

        // set up RecyclerView
        binding.driversListRV.layoutManager = LinearLayoutManager(requireActivity())
        adapter = DriversListAdapter(requireActivity(),driversList,findNavController())
        binding.driversListRV.adapter = adapter
        getDriversList()

        return binding.root
    }

    private fun getDriversList (){
        val vendorsRef = rootRef.child("Users").child("Driver")
        driversList.clear()
        idList.clear()
        vendorsRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                binding.mPrgBar.visibility = View.GONE
                binding.prgMsg.visibility = View.GONE

                val driverID : String? = snapshot.key
                if(driverID!=null && !idList.contains(driverID)){ // To avoid adding duplicates

                    val email : String? = snapshot.child("email").value as String?
                    val username : String? = snapshot.child("username").value as String?
                    val contactNum : String? = snapshot.child("contactNum").value as String?

                    if(email!=null && username!=null && contactNum!=null){
                        idList.add(driverID)
                        driversList.add(DriverModel(driverID, username, email,contactNum))
                        adapter.notifyDataSetChanged()
                    }
                }

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }
    data class DriverModel(val driverID :String, val driverName : String, val driverEmail: String,val contactNum:String)

}