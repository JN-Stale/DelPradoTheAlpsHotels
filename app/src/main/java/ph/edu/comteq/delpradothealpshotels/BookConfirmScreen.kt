package ph.edu.comteq.delpradothealpshotels

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.gson.Gson

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookConfirmScreen(
    hotelId: Int,
    roomId: Int,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    var details by remember { mutableStateOf<HotelDetails?>(null) }

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }

    // Load the hotel info again, same way as BookScreen
    LaunchedEffect(hotelId) {
        val json = context.assets.open("hotels_details.$hotelId.json")
            .bufferedReader().use { it.readText() }
        details = Gson().fromJson(json, HotelDetails::class.java)
    }

    // Find the room that was tapped
    val room = details?.rooms?.find { it.room_id == roomId }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Booking Confirm", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF664930),
                    titleContentColor = Color(0xFFFFDBBB),
                    navigationIconContentColor = Color(0xFFFFDBBB)
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "You are going to reserve:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = details?.hotel_name ?: "",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            if (room != null) {
                // Reusing the card from the room list
                RoomCard(room = room, modifier = Modifier.padding(horizontal = 0.dp))
            }

            Text(
                text = "Form",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text("First Name") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text("Last Name") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}