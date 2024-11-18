package com.example.inscanner.Pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.Text
@Composable
fun QRCodeScannerScreen() {
    var scannedText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (scannedText.isEmpty()) {
            QRCodeScanner { qrCode ->
                scannedText = qrCode
            }
        } else {
            Text(
                text = "Scanned QR Code: $scannedText",
                //style = MaterialTheme.typography.h6
            )
        }
    }
}
