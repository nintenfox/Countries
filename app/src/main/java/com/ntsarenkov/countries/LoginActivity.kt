package com.ntsarenkov.countries

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.ntsarenkov.countries.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    companion object {
        const val TAG = "FirebaseAuth"
        const val RC_SIGN_IN = 9001
    }

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding
    private val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z0-9.-]+\$"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        //Смена режимов вход/регистрация
        binding.btnSignUpModeDisabled.setOnClickListener {
            binding.btnSignUpModeDisabled.visibility = View.GONE
            binding.btnSignUpModeEnabled.visibility = View.VISIBLE
            binding.btnSignInModeDisabled.visibility = View.VISIBLE
            binding.btnSignInModeEnabled.visibility = View.GONE
            binding.btnSignIn.visibility = View.GONE
            binding.btnSignUp.visibility = View.VISIBLE
        }
        binding.btnSignInModeDisabled.setOnClickListener {
            binding.btnSignUpModeDisabled.visibility = View.VISIBLE
            binding.btnSignUpModeEnabled.visibility = View.GONE
            binding.btnSignInModeDisabled.visibility = View.GONE
            binding.btnSignInModeEnabled.visibility = View.VISIBLE
            binding.btnSignIn.visibility = View.VISIBLE
            binding.btnSignUp.visibility = View.GONE
        }
        //Регистрация
        binding.btnSignUp.setOnClickListener {
            binding.tvEmail.visibility = View.GONE
            binding.tvPassword.visibility = View.GONE
            if (binding.etEmail.text.toString()
                    .isEmpty() or !isValidEmail(binding.etEmail.text.toString())
            ) {
                binding.tvEmail.visibility = View.VISIBLE
                return@setOnClickListener
            }
            if (binding.etPassword.text.toString().isEmpty()) {
                binding.tvPassword.visibility = View.VISIBLE
                return@setOnClickListener
            }
            auth.createUserWithEmailAndPassword(
                binding.etEmail.text.toString(),
                binding.etPassword.text.toString()
            )
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        toast("Register success")
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    } else
                        toast("Register failed")
                }
        }
        //Вход
        binding.btnSignIn.setOnClickListener {
            binding.tvEmail.visibility = View.GONE
            binding.tvPassword.visibility = View.GONE
            if (binding.etEmail.text.toString()
                    .isEmpty() or !isValidEmail(binding.etEmail.text.toString())
            ) {
                binding.tvEmail.visibility = View.VISIBLE
                return@setOnClickListener
            }
            if (binding.etPassword.text.toString().isEmpty()) {
                binding.tvPassword.visibility = View.VISIBLE
                return@setOnClickListener
            }
            auth.signInWithEmailAndPassword(
                binding.etEmail.text.toString(),
                binding.etPassword.text.toString()
            )
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        toast("Sign in success as " + auth.currentUser?.email)
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    } else
                        binding.tvPassword.visibility = View.VISIBLE
                }
        }
        //Вход через Google
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.btnSignInGoogle.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                }
            }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            toast("User is signed in")
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
        }
    }

    private fun toast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }

    private fun isValidEmail(email: String): Boolean {
        return email.matches(emailRegex.toRegex())
    }
}