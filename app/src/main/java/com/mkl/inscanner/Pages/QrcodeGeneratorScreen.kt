package com.mkl.inscanner.Pages

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import com.mkl.inscanner.R
import com.mkl.inscanner.models.Qrtile
import java.io.ByteArrayOutputStream

fun generateQRCode(content: String): Bitmap? {
    return try {
        val qrCodeWriter = QRCodeWriter()
        val bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, 512, 512)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
            }
        }
        bitmap
    } catch (e: WriterException) {
        e.printStackTrace()
        null
    }
}
fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
    val stream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
    return stream.toByteArray()
}
fun byteArrayToBitmap(byteArray: ByteArray): Bitmap {
    return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
}
@Composable
fun QRCodeGeneratorScreen(navController: NavController, typeOfQrtile: Qrtile?) {
    var inputText by remember { mutableStateOf("") }
    var qrCodeBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isError by remember {mutableStateOf(false)}
    Column(
        modifier = androidx.compose.ui.Modifier
            .fillMaxSize()
            .background(Color(0xFF000E09))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
       // verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.inscanner),
                contentDescription = "Main Logo",
                modifier = Modifier
                    .width(126.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Add Icon",  // Add a content description for accessibility
                tint = Color.White,
                modifier = Modifier.clickable { navController.navigate("getstarted")}
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ){
            Image(
                painter = painterResource(id = typeOfQrtile?.iconRes ?: 0),
                contentDescription = "Main Logo",
                modifier = Modifier
                    .size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = "${typeOfQrtile?.text}", color = Color.White, fontSize = 20.sp)
        }
        Spacer(modifier = androidx.compose.ui.Modifier.height(16.dp))
        TextField(
            value = inputText,
            onValueChange = {
                inputText = it
                isError = false},
            label = { Text("Enter ${typeOfQrtile?.text}") },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(200.dp),
            maxLines = 5,
            singleLine = false,
            isError = isError
        )
        if (isError) {
            Text(
                text = "This field is mandatory.",
                color = Color.Red,
                fontSize = 14.sp,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
            )
        }
        Spacer(modifier = androidx.compose.ui.Modifier.height(16.dp))
        Button(
            onClick = {
                if (inputText.trim().isBlank()) {
                    isError = true // Show error message
                } else {
                    qrCodeBitmap = generateQRCode(inputText)
                    qrCodeBitmap?.let {
                        val byteArray = bitmapToByteArray(it)
                        navController.navigate("qrCodeScreen/${byteArray.joinToString(",")}")
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF208E67),
                contentColor = Color.White,
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = androidx.compose.ui.Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Generate QR Code")
        }
    }
}

