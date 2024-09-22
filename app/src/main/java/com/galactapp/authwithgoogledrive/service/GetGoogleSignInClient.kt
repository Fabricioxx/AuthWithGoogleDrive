package com.galactapp.authwithgoogledrive.service

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope


fun getGoogleSignInClient(context: Context): GoogleSignInClient {
    val signInOption = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        // Adicione o escopo para acessar o Google Drive
        .requestScopes(Scope(Scopes.DRIVE_FILE))
        .build()

    return GoogleSignIn.getClient(context, signInOption)
}


