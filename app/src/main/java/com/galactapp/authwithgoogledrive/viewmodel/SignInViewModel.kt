package com.galactapp.authwithgoogledrive.viewmodel

//avalie meu codigo e me fale o que voce acha   


import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.galactapp.authwithgoogledrive.model.User
import com.galactapp.authwithgoogledrive.service.DriveRepository
import com.galactapp.authwithgoogledrive.service.getGoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
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
                notifyLogoutComplete(context = context)
            } catch (e: Exception) {
                handleLogoutFailure(context = context, exception = e)
            }
        }
    }


    private fun notifyLogoutComplete(context: Context) {
        Toast.makeText(context, "Logout completo de todos os serviços", Toast.LENGTH_LONG).show()
    }

    private fun handleRevokeFailure(context: Context, exception: Exception?) {
        Toast.makeText(context, "Falha ao revogar acesso: ${exception?.localizedMessage}", Toast.LENGTH_LONG).show()
        // Implementar ações adicionais conforme necessário, como logs ou avisos ao usuário
    }

    private fun handleLogoutFailure(context: Context, exception: Exception?) {
        Toast.makeText(context, "Falha ao deslogar: ${exception?.localizedMessage}", Toast.LENGTH_LONG).show()
        // Implementar ações adicionais conforme necessário
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


