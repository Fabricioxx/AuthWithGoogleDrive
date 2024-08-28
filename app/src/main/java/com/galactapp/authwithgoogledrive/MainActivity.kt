package com.galactapp.authwithgoogledrive

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.galactapp.authwithgoogledrive.model.User
import com.galactapp.authwithgoogledrive.service.AuthResult
import com.galactapp.authwithgoogledrive.ui.component.GoogleSignInButton
import com.galactapp.authwithgoogledrive.ui.theme.AuthWithGoogleDriveTheme
import com.galactapp.authwithgoogledrive.viewmodel.SignInViewModel
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val signInViewModel: SignInViewModel by viewModels()

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        signInViewModel.ouvinteAutenticacao(applicationContext)

        enableEdgeToEdge()
        setContent {
            AuthWithGoogleDriveTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {


                    SignInScreen(signInViewModel)

                }
            }
        }
    }
}

@Composable
fun SignInScreen(signInViewModel: SignInViewModel) {
    val scope = rememberCoroutineScope()
    var text by remember { mutableStateOf<String?>(null) }
    val user by remember(signInViewModel) { signInViewModel.user }.collectAsState()
    val signInRequestCode = 1
    var isLoading by remember { mutableStateOf(false) }  // Declare aqui

    val authResultLauncher = rememberLauncherForActivityResult(contract = AuthResult()) { task ->
        try {
            val account = task?.getResult(ApiException::class.java)
            if (account == null) {
                text = "Google Sign In Failed"
                isLoading = false  // Atualize o estado aqui
            } else {
                isLoading = true  // Atualize o estado aqui
                scope.launch {
                    signInViewModel.setSignInValue(
                        email = account.email!!,
                        displayName = account.displayName!!
                    )
                    isLoading = false  // Atualize o estado após a conclusão
                }
            }
        } catch (e: ApiException) {
            text = e.localizedMessage
            isLoading = false  // Atualize o estado aqui
        }
    }

    AuthView(
        errorText = text,
        isLoading = isLoading,  // Passe como parâmetro
        onClick = {
            text = null
            authResultLauncher.launch(signInRequestCode)
        }
    )

    user?.let {
        GoogleSignInScreen(user = it,
            onLogout = {
                signInViewModel.logout()
            }
        )
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
    onLogout: () -> Unit
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
    ) { innerPadding ->
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
                onClick = { onLogout() },
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