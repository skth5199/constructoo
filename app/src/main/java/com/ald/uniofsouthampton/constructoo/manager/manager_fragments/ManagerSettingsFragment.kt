package com.ald.uniofsouthampton.constructoo.manager.manager_fragments

import android.app.AlertDialog
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.ald.uniofsouthampton.constructoo.R
import com.ald.uniofsouthampton.constructoo.authentication.AuthActivity
import com.ald.uniofsouthampton.constructoo.databinding.FragmentManagerSettingsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class ManagerSettingsFragment : Fragment() {

    private lateinit var mAuth : FirebaseAuth
    private lateinit var prefs : SharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        val binding : FragmentManagerSettingsBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_manager_settings,container,false)
        mAuth = FirebaseAuth.getInstance()
        prefs = requireActivity().getSharedPreferences("myPrefs",MODE_PRIVATE)
        var accountType : String? = null
        if(prefs.contains("accountType")){
            accountType = prefs.getString("accountType",null)
        }
        binding.apply {
            if(accountType!=null){
                val userRef = FirebaseDatabase.getInstance().reference.child("Users").child(accountType).
                child(mAuth.uid!!).child("username")
                userRef.addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val username : String? = snapshot.value as String?
                        if(username!=null){
                            settingsUsernameET.setText(username)
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {}
                })
            }
            logOutFAB.setOnClickListener {
                logOutUser()
            }
            val email : String? = mAuth.currentUser?.email
            if(email!=null){
                settingsUserIdET.setText(email)
            }
        }
        return binding.root
    }

    private fun logOutUser(){
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("Confirm action")
        builder.setMessage("Are you sure you want to log out?")

        builder.setPositiveButton("Yes") { dialog, which ->
            mAuth.signOut()
            if(prefs.contains("accountType")){prefs.edit().remove("accountType").apply()}
            startActivity(Intent(requireActivity(),AuthActivity::class.java))
            requireActivity().finish()
        }

        builder.setNegativeButton("Cancel") { dialog, which ->
            dialog.cancel()
        }
        val alertDialog = builder.create()
        alertDialog.show()
    }

}