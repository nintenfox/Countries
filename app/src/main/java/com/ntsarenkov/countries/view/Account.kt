package com.ntsarenkov.countries.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ntsarenkov.countries.databinding.FragmentAccountBinding

class Account : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentAccountBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountBinding.inflate(inflater, container, false)

        auth = Firebase.auth

        val currentUser = Firebase.auth.currentUser
        currentUser?.let {
            val email = it.email
            binding.tvPersEmail.text = email
        }

        binding.btChangePassword.setOnClickListener { updatePassword() }

        binding.btSignOut.setOnClickListener {
            auth.signOut()
            startActivity(Intent(context, LoginActivity::class.java))
        }
        return binding.root
    }

    private fun updatePassword() {
        val newPassword = binding.etPersPassword.text.toString()
        if (newPassword.isEmpty()) {
            Toast.makeText(context, "Password shouldn't be empty!", Toast.LENGTH_SHORT).show()
        } else {
            auth.currentUser!!.updatePassword(newPassword).addOnCompleteListener { task ->
                if (task.isSuccessful)
                    Toast.makeText(context, "Password successfully updated!", Toast.LENGTH_SHORT)
                        .show()
                else if (!task.isSuccessful)
                    Toast.makeText(
                        context,
                        "Please Sign Out and Sign In to change password",
                        Toast.LENGTH_SHORT
                    ).show()
            }
        }
    }
}