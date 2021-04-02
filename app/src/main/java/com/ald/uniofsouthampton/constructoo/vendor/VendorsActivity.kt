package com.ald.uniofsouthampton.constructoo.vendor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.navigation.findNavController
import com.ald.uniofsouthampton.constructoo.R

class VendorsActivity : AppCompatActivity() {
    companion object{
        var vendorActionBar : ActionBar? = null
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vendors)
        vendorActionBar = supportActionBar
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.vendors_host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}