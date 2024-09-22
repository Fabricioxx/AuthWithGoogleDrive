package com.galactapp.authwithgoogledrive.service

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.ByteArrayContent
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.File
import java.io.IOException


class DriveRepository(private val context: Context) {

    private val driveService: Drive by lazy { initializeDriveService() }

    private fun initializeDriveService(): Drive {
        val account = GoogleSignIn.getLastSignedInAccount(context)
            ?: throw IllegalStateException("User must be signed in to access Google Drive")

        val credentials = GoogleAccountCredential.usingOAuth2(
            context, listOf(DriveScopes.DRIVE_FILE)
        ).apply {
            selectedAccount = account.account
        }

        return Drive.Builder(
            NetHttpTransport(),
            GsonFactory.getDefaultInstance(),
            credentials
        ).setApplicationName("my-drive").build()
    }

    @Throws(IOException::class)
    fun uploadBasic(): String {
        val fileMetadata = File().apply { name = "NewDocument.txt" }
        val fileContent = "Hello, this is a test document!"
        val mediaContent = ByteArrayContent.fromString("text/plain", fileContent)

        val file = driveService.files().create(fileMetadata, mediaContent)
            .setFields("id")
            .execute()

        return file.id.also {
            println("File created successfully with ID: $it")
        }
    }
}



