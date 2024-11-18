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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.inscanner.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(navController: NavHostController){
    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF000E09)),
        //verticalArrangement = Arrangement.Center,
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
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Box(
                    modifier =
                        Modifier.border(
                            width = 2.dp,
                            color = Color.White,
                            shape = RoundedCornerShape(16.dp)
                        ).clickable{navController.navigate("QRCodeScannerScreen")}
                ){
                    Image(
                        painter = painterResource(id = R.drawable.mdi_line_scan),
                        contentDescription = "Scan",
                        modifier = Modifier.size(100.dp).padding(24.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = "Scan", color = Color.White, fontSize = 22.sp )
            }
            Spacer(modifier = Modifier.height(18.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ){
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Box(modifier = Modifier.border(width = 2.dp, color = Color.White, shape = RoundedCornerShape(16.dp))){
                        Image(
                            painter = painterResource(id = R.drawable.material_symbols_history),
                            contentDescription = "History",
                            modifier = Modifier.size(100.dp).padding(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "History", color = Color.White, fontSize = 22.sp )
                }
                Spacer(modifier = Modifier.width(50.dp))
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Box(modifier = Modifier.border(width = 2.dp, color = Color.White, shape = RoundedCornerShape(16.dp))){
                        Image(
                            painter = painterResource(id = R.drawable.system_uicons_create),
                            contentDescription = "Create",
                            modifier = Modifier.size(100.dp).padding(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "Create", color = Color.White, fontSize = 22.sp )
                }
            }
        }
    }
}