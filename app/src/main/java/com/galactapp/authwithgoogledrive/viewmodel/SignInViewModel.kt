package com.galactapp.authwithgoogledrive.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.galactapp.authwithgoogledrive.model.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SignInViewModel: ViewModel() {
    private val _user: MutableStateFlow<User?> = MutableStateFlow(null)
    val user: StateFlow<User?> = _user

    suspend fun setSignInValue(email: String, displayName: String) {
        delay(2000)
        _user.value = User(email, displayName)
    }


    fun logout() {

        Firebase.auth.signOut()

        _user.value = null
        // Lógica para deslogar o usuário, pode incluir limpar dados locais ou atualizar o estado
    }


    fun ouvinteAutenticacao(context: Context) {
        Firebase.auth.addAuthStateListener { auth ->
            if (auth.currentUser != null) {
                Toast.makeText(context, "Usuário logado", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Usuário deslogado", Toast.LENGTH_SHORT).show()
            }
        }
    }


}