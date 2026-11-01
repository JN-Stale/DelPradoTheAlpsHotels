package ph.edu.comteq.delpradothealpshotels

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBookingsScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    bookingViewModel: BookingViewModel = viewModel()
) {
    val bookings by bookingViewModel.bookings.collectAsStateWithLifecycle()
    val message by bookingViewModel.message.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("My Bookings", fontWeight = FontWeight.Bold) },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "List of my bookings",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            message?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }

            if (bookings.isEmpty()) {
                Text("No bookings yet.")
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(bookings, key = { _, booking -> booking.id }) { index, booking ->
                    BookingCard(number = index + 1, booking = booking)
                }
            }
        }
    }
}

@Composable
fun BookingCard(
    number: Int,
    booking: Booking,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // mumerical sequence
            Text(
                text = "$number",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "${booking.firstName} ${booking.lastName}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = booking.hotelName,
                    fontWeight = FontWeight.Bold
                )
                Text("${formatDate(booking.checkIn)} to ${formatDate(booking.checkOut)}")

                val adultsLabel = if (booking.adults == 1) "Adult" else "Adults"
                val childrenLabel = if (booking.children == 1) "Child" else "Children"
                val roomsLabel = if (booking.rooms == 1) "Room" else "Rooms"
                Text("${booking.adults} $adultsLabel, ${booking.children} $childrenLabel, ${booking.rooms} $roomsLabel")

                val bookingType = if (booking.forBusiness) "For business" else "For sightseeing"
                Text(
                    text = "$bookingType  •  Pay with ${booking.paymentMethod.lowercase()}",
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = "€ ${booking.totalPrice}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}