package com.ald.uniofsouthampton.constructoo.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ald.uniofsouthampton.constructoo.R
import com.google.firebase.auth.FirebaseAuth

class AuthActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        supportActionBar?.hide()

    }

    override fun onStart() {
        super.onStart()
        if (FirebaseAuth.getInstance().uid != null) {
            startActivity(Intent(this, AccountVerificationActivity::class.java))
            finish()
        }
    }

}