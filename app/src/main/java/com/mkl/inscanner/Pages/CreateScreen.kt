package com.mkl.inscanner.Pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.mkl.inscanner.MainActivity
import com.mkl.inscanner.R
import com.mkl.inscanner.models.QrtileItems

@Composable
fun CreateScreen(navController: NavHostController){
    val context = LocalContext.current as MainActivity
    Column (
        modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF000E09))
                .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
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
        LazyColumn{
            QrtileItems.forEach{ items ->
                item{
                    Row(
                        Modifier
                            .border(
                                width = 1.dp,
                                color = Color.White,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(12.dp)
                            .fillMaxWidth()

                    ){
                        Image(
                            painter = painterResource(id = items.iconRes),
                            contentDescription = "Scan",
                            modifier = Modifier.size(28.dp).padding(2.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = items.text, color = Color.White, fontSize = 18.sp )
                    }
                }
                item{
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

        }
    }
}