package com.ald.uniofsouthampton.constructoo.authentication

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.ald.uniofsouthampton.constructoo.HideKeyboardHelper
import com.ald.uniofsouthampton.constructoo.R
import com.ald.uniofsouthampton.constructoo.databinding.FragmentSignUpBinding
import com.ald.uniofsouthampton.constructoo.helpers.FormatCheckHelpers
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.FirebaseDatabase


class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignUpBinding
    private var accountType = ""
    private var selectionIndex = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sign_up, container, false)
        binding.switchToLoginTV.setOnClickListener { findNavController().navigate(R.id.action_signUpFragment_to_loginFragment) }
        binding.signUpFAB.setOnClickListener { attemptSignUp() }
        setUpSpinner()

        return binding.root
    }

    private fun setUpSpinner() {
        binding.accountTypeSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    binding.spinnerLayout.setBackgroundResource(R.drawable.spinner_unselected_bk)
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    accountType =
                        requireActivity().resources.getStringArray(R.array.accountTypeArray)[position]
                    selectionIndex = position
                    binding.spinnerLayout.setBackgroundResource(R.drawable.spinner_selected_bk)

                    when (position) {
                        0 -> {
                            binding.spinnerLayout.setBackgroundResource(R.drawable.spinner_unselected_bk)
                            binding.phoneNumField.visibility = View.INVISIBLE
                            binding.addressField.visibility = View.INVISIBLE
                        }
                        1 -> {
                            binding.phoneNumField.visibility = View.INVISIBLE
                            binding.addressField.visibility = View.INVISIBLE
                        }
                        2 -> {
                            binding.phoneNumField.visibility = View.VISIBLE
                            binding.addressField.visibility = View.INVISIBLE
                        }
                        3 -> {
                            binding.phoneNumField.visibility = View.INVISIBLE
                            binding.addressField.visibility = View.VISIBLE
                        }
                    }
                }
            }
    }

    private fun attemptSignUp() {
        HideKeyboardHelper.hideKeyboardFrom(requireActivity(), binding.signUpContainer)
        val username = binding.usernameET.text.toString()
        if (username.length < 3) {
            showSnackMsg("Username must contain at least 3 characters.")
            return
        }
        val email = binding.emailET.text.toString()
        if (email.isEmpty() || !FormatCheckHelpers.isEmailValid(email)) {
            showSnackMsg("Please enter a valid email address")
            return
        }
        val password = binding.passwordET.text.toString()
        if (password.length < 8) {
            showSnackMsg("Password must contain at least 8 characters.")
            return
        }
        if (selectionIndex == 0) {
            showSnackMsg("Please choose an account type.")
            return
        }
        val address = binding.addressET.text.toString()
        if (accountType == "Vendor" && address.length < 8) {
            showSnackMsg("Address must contain at least 8 characters.")
            return
        }
        var contactNum = ""
        if (accountType == "Driver") {
            contactNum = binding.contactNumET.text.toString()
            if (contactNum.length < 11) {
                showSnackMsg("Driver account requires a valid contact number.")
                return
            }
        }
        binding.signUpPrg.visibility = View.VISIBLE
        binding.signUpFAB.isClickable = false
        // if program reaches this point then all values a valid for SignUp Attempt
        val mAuth = FirebaseAuth.getInstance()
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                val userID = mAuth.currentUser?.uid
                if (userID.isNullOrEmpty()) {
                    binding.signUpFAB.isClickable = true
                    binding.signUpPrg.visibility = View.GONE
                    showSnackMsg("Sign up process failed to complete.")
                } else {
                    val sharedPreferences = requireActivity().getSharedPreferences(
                        "myPrefs",
                        AppCompatActivity.MODE_PRIVATE
                    )
                    sharedPreferences.edit().putString("accountType", accountType).apply()
                    pushNewUserToDB(userID, username, email, password, address, contactNum)
                }
            } else {
                binding.signUpPrg.visibility = View.GONE
                binding.signUpFAB.isClickable = true
                if (it.exception is FirebaseAuthUserCollisionException) {
                    showSnackMsg("Registration failed. User with this email already exist.")
                }
                else{
                    showSnackMsg("Something went wrong while creating account.")
                }
            }
        }
    }

    private fun pushNewUserToDB(
        userID: String,
        username: String,
        email: String,
        password: String,
        address: String,
        contactNum: String
    ) {
        val dbRef =
            FirebaseDatabase.getInstance().reference.child("Users").child(accountType).child(userID)
        val newUserMap: HashMap<String, String> = HashMap()
        newUserMap["isApproved"] = "false"
        newUserMap["username"] = username
        newUserMap["email"] = email
        newUserMap["password"] = password
        newUserMap["address"] = address
        newUserMap["contactNum"] = contactNum
        dbRef.setValue(newUserMap).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(
                    requireActivity(),
                    "Account created successfully.",
                    Toast.LENGTH_LONG
                ).show()
                startActivity(Intent(requireActivity(), AccountVerificationActivity::class.java))
                requireActivity().finish()
            } else {
                binding.signUpFAB.isClickable = true
                binding.signUpPrg.visibility = View.GONE
                showSnackMsg("Account created, but registration not complete. Contact admin to verify account")
            }
        }
    }


    private fun showSnackMsg(msg: String) {
        Snackbar.make(binding.signUpContainer, msg, Snackbar.LENGTH_LONG).show()
    }

}