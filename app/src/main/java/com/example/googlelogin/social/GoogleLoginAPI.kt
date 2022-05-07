package com.example.googlelogin.social

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.googlelogin.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class GoogleLoginAPI : Fragment() {
    private val TAG: String = javaClass.simpleName
    private var listener: GoogleLoginAPIListner? = null

    // [START declare_auth]
    private lateinit var auth: FirebaseAuth
    // [END declare_auth]
    private lateinit var googleSignInClient: GoogleSignInClient



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun setGoogleLoginAPIListner(googleLoginAPIListner:  GoogleLoginAPIListner) {
        if (listener == null) {
            listener = googleLoginAPIListner
        }
    }

    interface GoogleLoginAPIListner {
        // TODO: Update argument type and name
        fun onError(error: String)
        fun onLoginResultUpdate(user: FirebaseUser?)

    }


    private fun postErrorMsg(msg: String) {
        listener?.onError(msg)
    }


    companion object {
        private const val TAG = "GoogleActivity"
        private const val RC_SIGN_IN = 9001
    }


    fun signIn() {

        if(!checkExistingUser()) {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }
    // [END signin]

    fun signOut() {
        // Firebase sign out
        auth.signOut()

        // Google sign out
        googleSignInClient.signOut()
        listener!!.onLoginResultUpdate(null)
    }

    fun revokeAccess() {
        // Firebase sign out
        auth.signOut()

        // Google revoke access
        googleSignInClient.revokeAccess()
        listener!!.onLoginResultUpdate(null)
    }

    // [START onactivityresult]
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                listener!!.onLoginResultUpdate(null)
                postErrorMsg(e.message.toString())
            }
        }
    }


    // [START auth_with_google]
    private fun firebaseAuthWithGoogle(idToken: String) {

        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener {task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Log.e(TAG, "signInWithCredential:success")
                val user = auth.currentUser
                if (listener != null) {
                    Log.e(TAG, "if block")
                    listener!!.onLoginResultUpdate(user)
                } else {
                    Log.e(TAG, "else block $listener")
                }
            } else {
                listener!!.onLoginResultUpdate(null)
                postErrorMsg("Authentication failed")
            }
        }
    }
    // [END auth_with_google]


    fun checkExistingUser():Boolean{

        val currentUser = auth.currentUser
        if(currentUser != null) {
            listener!!.onLoginResultUpdate(currentUser)
            return true
        }
        return false
    }

    fun initSignin(){
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        auth = Firebase.auth
    }
}