package com.mkl.inscanner.Pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mkl.inscanner.R


@Composable
fun IntroPage() {
    Scaffold(
        modifier = Modifier.fillMaxSize(), // Scaffold modifier
        content = { paddingValues ->  // Content block of the Scaffold
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF000E09))
                    .padding(16.dp)
                    .padding(paddingValues)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.inscanner),
                    contentDescription = "Main Logo",
                    modifier = Modifier
                        .width(275.dp)
                )
                Spacer(modifier = Modifier.height(22.dp))
                Text(text = "Developed by MKL Studio", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFFF0F8FF))
            }
        }
    )
}