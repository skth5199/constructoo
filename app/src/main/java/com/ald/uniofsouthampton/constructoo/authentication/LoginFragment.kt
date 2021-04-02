package com.ald.uniofsouthampton.constructoo.authentication

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.ald.uniofsouthampton.constructoo.R
import com.ald.uniofsouthampton.constructoo.databinding.FragmentLoginBinding
import com.ald.uniofsouthampton.constructoo.helpers.FormatCheckHelpers
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {

    private lateinit var binding : FragmentLoginBinding
    private var accountType = ""
    private var selectionIndex = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        binding.switchToSignUpTV.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }
        setUpSpinner()
        binding.loginFAB.setOnClickListener {
            val email = binding.emailET.text.toString()
            if(email.isEmpty() || !FormatCheckHelpers.isEmailValid(email)){
                showSnackMsg("Please enter a valid email address")
                return@setOnClickListener
            }
            val password = binding.passwordET.text.toString()
            if(password.isEmpty()){
                showSnackMsg("Please enter your password.")
                return@setOnClickListener
            }
            if(selectionIndex==0){
                showSnackMsg("Select an account type.")
                return@setOnClickListener
            }
            binding.loginFAB.isClickable = false
            val sharedPreferences = requireActivity().getSharedPreferences("myPrefs", AppCompatActivity.MODE_PRIVATE)
            sharedPreferences.edit().putString("accountType",accountType).apply()
            attemptLogin(email,password)
        }
        return binding.root
    }

    private fun setUpSpinner(){
        binding.accountTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                binding.spinnerLayout.setBackgroundResource(R.drawable.spinner_unselected_bk)
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                accountType = requireActivity().resources.getStringArray(R.array.accountTypeArray)[position]
                selectionIndex = position
                if(position==0){
                    binding.spinnerLayout.setBackgroundResource(R.drawable.spinner_unselected_bk)
                }
                else{
                    binding.spinnerLayout.setBackgroundResource(R.drawable.spinner_selected_bk)
                }
            }
        }
    }

    private fun attemptLogin(email: String, password: String){
        binding.loginPrg.visibility = View.VISIBLE
        val mAuth = FirebaseAuth.getInstance()
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener {
            if(it.isSuccessful){
                startActivity(Intent(requireActivity(),AccountVerificationActivity::class.java))
                requireActivity().finish()
            }
            else{
                binding.loginFAB.isClickable = true
                binding.loginPrg.visibility = View.GONE
                showSnackMsg("Failed to Login, make sure that the provided credentials are correct.")
            }
        }
    }

    private fun showSnackMsg(msg : String){
        Snackbar.make(binding.loginFragContainer,msg,Snackbar.LENGTH_LONG).show()
    }

}