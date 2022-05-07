package com.example.googlelogin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.googlelogin.databinding.ActivityMainBinding
import com.example.googlelogin.social.GoogleLoginAPI
import com.google.firebase.auth.FirebaseUser

class MainActivity : AppCompatActivity(),View.OnClickListener{

    private val binding : ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    lateinit var googleLoginAPI: GoogleLoginAPI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.imgGmail.setOnClickListener(this)

        initGoogleLoginAPI()

    }

    private fun initGoogleLoginAPI() {
        googleLoginAPI = GoogleLoginAPI()
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(googleLoginAPI, "googleLoginAPI")
        fragmentTransaction.commit()
        fragmentManager.executePendingTransactions()
        googleLoginAPI.setGoogleLoginAPIListner(mGoogleMapAPIListner)
        googleLoginAPI.initSignin()
    }

    var mGoogleMapAPIListner = object : GoogleLoginAPI.GoogleLoginAPIListner {
        override fun onError(error: String) {
            Toast.makeText(this@MainActivity, "Gmail error :$error", Toast.LENGTH_SHORT).show()
        }

        override fun onLoginResultUpdate(user: FirebaseUser?) {
            if (user != null) {
                Log.d("Facebook", "Name  :" + user.displayName + " Email :" + user.email)
                //callSocialLoginAPI(user.uid, user.displayName!!, user.email!!)
            }
        }
    }
    override fun onClick(p0: View?) {
        when(p0?.id)
        {
            R.id.imgGmail ->{
                googleLoginAPI.signIn()
            }
        }
    }
}