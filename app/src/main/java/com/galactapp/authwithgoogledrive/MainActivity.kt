package com.galactapp.authwithgoogledrive

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.galactapp.authwithgoogledrive.model.User
import com.galactapp.authwithgoogledrive.service.AuthResult
import com.galactapp.authwithgoogledrive.service.DriveRepository
import com.galactapp.authwithgoogledrive.ui.component.GoogleSignInButton
import com.galactapp.authwithgoogledrive.ui.theme.AuthWithGoogleDriveTheme
import com.galactapp.authwithgoogledrive.viewmodel.SignInViewModel
import com.galactapp.authwithgoogledrive.viewmodel.SignInViewModelFactory
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.IOException

class MainActivity : ComponentActivity() {


    private val repository = DriveRepository(this)
    private lateinit var viewModel: SignInViewModel

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModelFactory = SignInViewModelFactory(repository, applicationContext)
        viewModel = ViewModelProvider(this, viewModelFactory).get(SignInViewModel::class.java)

        enableEdgeToEdge()
        setContent {
            AuthWithGoogleDriveTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    SignInScreen(viewModel)
                }
            }
        }
    }
}

@Composable
fun SignInScreen(viewModel: SignInViewModel) {
    val scope = rememberCoroutineScope()
    val text = remember { mutableStateOf<String?>(null) }
    val user by viewModel.user.collectAsState()
    val isLoading = remember { mutableStateOf(false) }
    val isUploading = remember { mutableStateOf(false) }
    val context = LocalContext.current

    val authResultLauncher = rememberLauncherForActivityResult(contract = AuthResult()) { task ->
        handleAuthResult(task, viewModel, text, isLoading)
    }

    if (user == null) {
        AuthView(
            errorText = text.value,
            isLoading = isLoading.value,
            onClick = {
                text.value = null
                isLoading.value = true
                authResultLauncher.launch(Unit)
            }
        )
    } else {
        GoogleSignInScreen(
            user = user!!,
            isUploading = isUploading.value,
            onLogout = { viewModel.logout() },
            onUpload = { uploadFile(viewModel, scope, context, text, isUploading) }
        )
    }
}



private fun handleAuthResult(
    task: Task<GoogleSignInAccount>?,
    viewModel: SignInViewModel,
    text: MutableState<String?>,
    isLoading: MutableState<Boolean>
) {
    viewModel.viewModelScope.launch {
        try {
            val account = task?.getResult(ApiException::class.java)
            if (account != null) {
                viewModel.setSignInValue(account.email!!, account.displayName!!)
            } else {
                text.value = "Google Sign-In failed. Please try again."
            }
        } catch (e: ApiException) {
            text.value = "Authentication failed: ${e.localizedMessage}"
        } finally {
            isLoading.value = false
        }
    }
}


private fun uploadFile(
    viewModel: SignInViewModel,
    scope: CoroutineScope,
    context: Context,
    text: MutableState<String?>,
    isUploading: MutableState<Boolean>
) {
    scope.launch {
        try {
            isUploading.value = true
            viewModel.uploadFile()

            // Exibe um Toast ao concluir o upload
            Toast.makeText(context, "Upload realizado com sucesso!", Toast.LENGTH_LONG).show()

        } catch (e: IOException) {
            text.value = "Upload failed: ${e.localizedMessage}"

            // Exibe um Toast ao falhar no upload
            Toast.makeText(context, "Falha no upload: ${e.localizedMessage}", Toast.LENGTH_LONG).show()

        } finally {
            isUploading.value = false
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Suppress("UnusedMaterialScaffoldPaddingParameter")
@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AuthView(
    errorText: String?,
    isLoading: Boolean,  // Adicione como parâmetro
    onClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Google Sign In",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            )
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GoogleSignInButton(
                text = "Sign In with Google",
                icon = painterResource(id = R.drawable.google_sign_in_btn),
                loadingText = "Signing In...",
                isLoading = isLoading,  // Use o estado passado
                onClick = onClick
            )

            errorText?.let {
                Spacer(modifier = Modifier.height(30.dp))
                Text(text = it)
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoogleSignInScreen(
    user: User,
    isUploading: Boolean, // Adicionado o parâmetro isUploading
    onLogout: () -> Unit,
    onUpload: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Sign In Successful",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            )
        }
    ) { innerPadding -> // Certifique-se de que este bloco está aqui
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Welcome! ${user.displayName}",
                fontSize = 30.sp,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = user.email,
                color = Color.Gray,
                fontSize = 20.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onUpload,
                enabled = !isUploading, // Use o estado isUploading aqui
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondary),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isUploading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .height(16.dp)
                            .width(16.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                } else {
                    Text(
                        text = "Upload File",
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onLogout,
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Logout",
                    color = Color.White
                )
            }
        }
    }
}
