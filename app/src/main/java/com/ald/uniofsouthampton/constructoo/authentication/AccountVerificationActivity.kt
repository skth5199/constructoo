package com.ald.uniofsouthampton.constructoo.authentication

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.ald.uniofsouthampton.constructoo.R
import com.ald.uniofsouthampton.constructoo.driver.DriversHomeActivity
import com.ald.uniofsouthampton.constructoo.manager.ManagerHomeActivity
import com.ald.uniofsouthampton.constructoo.site_manager.SiteManagerActivity
import com.ald.uniofsouthampton.constructoo.vendor.VendorsActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

// The purpose of this activity is simply to check if the account is verified/approved by the Firebase Super Admin, this is necessary to restrict access to the app features
class AccountVerificationActivity : AppCompatActivity() {

    private lateinit var mAuth : FirebaseAuth
    private lateinit var rootRef : DatabaseReference
    private lateinit var prefs : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_verification)
        rootRef = FirebaseDatabase.getInstance().reference
        mAuth = FirebaseAuth.getInstance()
        prefs = getSharedPreferences("myPrefs", MODE_PRIVATE)

        val email = mAuth.currentUser?.email
        val currentUserTV : TextView = findViewById(R.id.currentUserTV)
        currentUserTV.append(email)
    }

    fun backToAuthScreen(view : View){
        mAuth.signOut()
        if(prefs.contains("accountType")){prefs.edit().remove("accountType").apply()}
        startActivity(Intent(this,AuthActivity::class.java))
        finish()
    }

    override fun onStart() {
        super.onStart()

        var accountType : String? = null
        if(prefs.contains("accountType")){
            accountType = prefs.getString("accountType",null)
        }
        val uid = mAuth.uid
        if(accountType!=null && uid!=null){
            when(accountType){
                "Vendor"  -> {
                    startActivity(Intent(this@AccountVerificationActivity,VendorsActivity::class.java))
                    finish()
                }
                "Driver"  -> {
                    startActivity(Intent(this@AccountVerificationActivity,DriversHomeActivity::class.java))
                    finish()
                }
                "Site Manager"  -> {
                    startActivity(Intent(this@AccountVerificationActivity,SiteManagerActivity::class.java))
                    finish()
                }
                "Manager" -> { // when account type is manager then verify his account
                    val dbRef = rootRef.child("Users").child("Manager").child(uid)
                    dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val isApproved:String? = snapshot.child("isApproved").value as String?
                            if(isApproved.isNullOrEmpty()){
                                Toast.makeText(this@AccountVerificationActivity,"Account not found or the selected account type not correct.",Toast.LENGTH_LONG).show()
                                mAuth.signOut()
                                if(prefs.contains("accountType")){prefs.edit().remove("accountType").apply()}
                                startActivity(Intent(this@AccountVerificationActivity,AuthActivity::class.java))
                                finish()
                            }
                            else if(isApproved =="true"){
                                startActivity(Intent(this@AccountVerificationActivity,ManagerHomeActivity::class.java))
                                finish()
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {} // no implementation
                    })
                }
            }
        }
        else{
            mAuth.signOut()
            startActivity(Intent(this,AuthActivity::class.java))
            finish()
        }
    }
}