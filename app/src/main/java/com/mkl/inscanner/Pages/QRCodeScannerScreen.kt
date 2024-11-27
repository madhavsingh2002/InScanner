package com.mkl.inscanner.Pages

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.mkl.inscanner.R

@Composable
fun QRCodeScannerScreen() {
    var scannedText by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF000E09)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (scannedText.isEmpty()) {
            QRCodeScanner { qrCode ->
                scannedText = qrCode
            }
        } else {
            val isUrl = scannedText.startsWith("http://") || scannedText.startsWith("https://")
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = scannedText,
                    color = Color.White,
                    modifier = Modifier
                        .clickable {
                            if (isUrl) {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(scannedText))
                                ContextCompat.startActivity(context, intent, null)
                            } else {
                                Toast.makeText(context, "This is not a valid link", Toast.LENGTH_SHORT).show()
                            }
                        },
                )

                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF208E67),
                        contentColor = Color.White,
                    ),
                    onClick = {
                        val clipboard = ContextCompat.getSystemService(context, android.content.ClipboardManager::class.java)
                        val clip = android.content.ClipData.newPlainText("Scanned QR Code", scannedText)
                        clipboard?.setPrimaryClip(clip)
                        Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                }) {
                    Image(
                        painter = painterResource(id = R.drawable.copy),
                        contentDescription = "Main Logo",
                        modifier = Modifier
                            .size(24.dp)
                    )
                }
            }
        }
    }
}