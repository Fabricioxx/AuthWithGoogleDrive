package com.galactapp.authwithgoogledrive.service

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task


class AuthResult : ActivityResultContract<Unit, Task<GoogleSignInAccount>?>() {
    override fun createIntent(context: Context, input: Unit): Intent =
        getGoogleSignInClient(context).signInIntent

    override fun parseResult(resultCode: Int, intent: Intent?): Task<GoogleSignInAccount>? {
        return if (resultCode == Activity.RESULT_OK && intent != null) {
            GoogleSignIn.getSignedInAccountFromIntent(intent)
        } else {
            null
        }
    }
}


//class AuthResult: ActivityResultContract<Int, Task<GoogleSignInAccount>?>() {
//    override fun createIntent(context: Context, input: Int): Intent =
//        getGoogleSignInClient(context = context).signInIntent.putExtra("input", input)
//
//    override fun parseResult(resultCode: Int, intent: Intent?): Task<GoogleSignInAccount>? {
//        return when (resultCode) {
//            Activity.RESULT_OK -> GoogleSignIn.getSignedInAccountFromIntent(intent)
//            else -> null
//        }
//    }
//}