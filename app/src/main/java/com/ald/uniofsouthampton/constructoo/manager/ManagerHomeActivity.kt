package com.ald.uniofsouthampton.constructoo.manager

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.ald.uniofsouthampton.constructoo.MyFirebaseIDService
import com.ald.uniofsouthampton.constructoo.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging

class ManagerHomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manager_home)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.manager_host_fragment)
        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.navigation_home, R.id.navigation_construction_site, R.id.navigation_driver,R.id.navigation_vendors,R.id.navigation_settings)
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.manager_host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onStart() {
        val uid = FirebaseAuth.getInstance().uid
        if (uid != null) {
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (task.result != null) {
                        val newToken = task.result.toString()
                        val tokenREF = FirebaseDatabase.getInstance().reference.child("UserTokens").child(uid).child("fcmToken")
                        tokenREF.setValue(newToken)
//                        val prevToken = MyFirebaseIDService.getToken(this)
//                        if (prevToken != null && newToken != prevToken) {
//                            val tokenREF = FirebaseDatabase.getInstance().reference.child("UserTokens").child(uid).child("fcmToken")
//                            tokenREF.setValue(newToken)
//                        }
                    }
                }
            })
        }
        super.onStart()
    }

}