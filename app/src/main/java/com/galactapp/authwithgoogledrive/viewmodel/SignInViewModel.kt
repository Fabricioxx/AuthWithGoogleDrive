package com.galactapp.authwithgoogledrive.viewmodel

//avalie meu codigo e me fale o que voce acha   


import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.galactapp.authwithgoogledrive.model.User
import com.galactapp.authwithgoogledrive.service.DriveRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await


class SignInViewModel(
    private val driveRepository: DriveRepository,
    private val context: Context
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    init {
        // Verificar se o usuário já está logado
        val account = GoogleSignIn.getLastSignedInAccount(context)
        if (account != null) {
            _user.value = User(account.email!!, account.displayName!!)
        }
    }

    fun setSignInValue(email: String, displayName: String) {
        viewModelScope.launch {
            // Simulação de uma ação que poderia demorar (ex: fetch de dados)
            delay(1000)
            _user.value = User(email, displayName)
        }
    }

    fun uploadFile() {
        viewModelScope.launch {
            try {
                val fileId = withContext(Dispatchers.IO) {
                    driveRepository.uploadBasic()
                }
                println("Arquivo foi carregado com sucesso. ID: $fileId")
            } catch (e: Exception) {
                println("Erro ao carregar o arquivo: ${e.localizedMessage}")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val googleSignInClient = getGoogleSignInClient(context)
                    googleSignInClient.signOut().await()
                }
                _user.value = null
                notifyLogoutComplete()
            } catch (e: Exception) {
                handleLogoutFailure(e)
            }
        }
    }


    private fun notifyLogoutComplete() {
        // Implementar notificações ou callbacks aqui se necessário
        println("Logout completo de todos os serviços")
    }

    private fun handleRevokeFailure(exception: Exception?) {
        println("Falha ao revogar acesso: ${exception?.localizedMessage}")
        // Implementar ações adicionais conforme necessário, como logs ou avisos ao usuário
    }

    private fun handleLogoutFailure(exception: Exception?) {
        println("Falha ao deslogar: ${exception?.localizedMessage}")
        // Implementar ações adicionais conforme necessário
    }

    private fun getGoogleSignInClient(context: Context): GoogleSignInClient {
        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(Scope(Scopes.DRIVE_FILE)) // Certifique-se de incluir os escopos necessários
            .build()

        return GoogleSignIn.getClient(context, signInOptions)
    }
}


class SignInViewModelFactory(
    private val driveRepository: DriveRepository,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignInViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SignInViewModel(driveRepository, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


