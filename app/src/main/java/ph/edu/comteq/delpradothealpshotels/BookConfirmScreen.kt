package ph.edu.comteq.delpradothealpshotels

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

// turns a date number into text like "Tue, Sep 10, 2024"
fun formatDate(millis: Long): String {
    val formatter = SimpleDateFormat("EEE, MMM d, yyyy", Locale.ENGLISH)
    formatter.timeZone = TimeZone.getTimeZone("UTC")
    return formatter.format(Date(millis))
}

// text box that opens a calendar when the icon is tapped
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateField(
    label: String,
    dateMillis: Long?,
    onDateSelected: (Long?) -> Unit,
    modifier: Modifier = Modifier
) {
    var showPicker by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = if (dateMillis != null) formatDate(dateMillis) else "",
        onValueChange = {},
        readOnly = true,
        label = { Text(label) },
        trailingIcon = {
            IconButton(onClick = { showPicker = true }) {
                Icon(
                    imageVector = Icons.Filled.DateRange,
                    contentDescription = "Pick date"
                )
            }
        },
        modifier = modifier
    )

    if (showPicker) {
        val pickerState = rememberDatePickerState(initialSelectedDateMillis = dateMillis)

        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDateSelected(pickerState.selectedDateMillis)
                        showPicker = false
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = pickerState)
        }
    }
}

// one radio button with its label
@Composable
fun RadioOption(
    label: String,
    selected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable { onSelect() }
    ) {
        RadioButton(selected = selected, onClick = onSelect)
        Text(label)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookConfirmScreen(
    hotelId: Int,
    roomId: Int,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onBookingSaved: () -> Unit = {},
    bookingViewModel: BookingViewModel = viewModel()
) {
    val context = LocalContext.current
    var details by remember { mutableStateOf<HotelDetails?>(null) }

    val message by bookingViewModel.message.collectAsStateWithLifecycle()
    var saving by remember { mutableStateOf(false) }

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }

    var checkIn by remember { mutableStateOf<Long?>(null) }
    var checkOut by remember { mutableStateOf<Long?>(null) }

    var adultsText by remember { mutableStateOf("1") }
    var childrenText by remember { mutableStateOf("0") }

    var forBusiness by remember { mutableStateOf(false) }
    var paymentMethod by remember { mutableStateOf("Cash") }

    LaunchedEffect(hotelId) {
        val json = context.assets.open("hotels_details.$hotelId.json")
            .bufferedReader().use { it.readText() }
        details = Gson().fromJson(json, HotelDetails::class.java)
    }

    val room = details?.rooms?.find { it.room_id == roomId }
    val hotelName = details?.hotel_name ?: ""

    // nights are days between check-in and check-out
    val inDate = checkIn
    val outDate = checkOut
    val nights = if (inDate != null && outDate != null) {
        ((outDate - inDate) / 86_400_000L).toInt()
    } else 0
    val datesInvalid = inDate != null && outDate != null && nights <= 0

    // rooms is total guests divided by room capacity rounded up
    val adults = adultsText.toIntOrNull() ?: 0
    val children = childrenText.toIntOrNull() ?: 0
    val totalGuests = adults + children
    val capacity = room?.room_total_number_of_guests ?: 0
    val rooms = if (capacity > 0 && totalGuests > 0) {
        (totalGuests + capacity - 1) / capacity
    } else 0

    // price = nights x room price x rooms, + 150 for business
    val pricePerNight = room?.room_price_for_one_night ?: 0
    val businessFee = if (forBusiness) 150 else 0
    val totalPrice = if (nights > 0 && rooms > 0) {
        nights * pricePerNight * rooms + businessFee
    } else 0

    val canBook = room != null &&
            firstName.isNotBlank() &&
            lastName.isNotBlank() &&
            nights > 0 &&
            adults >= 1 &&
            rooms > 0

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
                text = hotelName,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            if (room != null) {
                RoomCard(room = room, modifier = Modifier.padding(horizontal = 0.dp))
            }

            Text(
                text = "Form",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            // names
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

            // dates
            DateField(
                label = "Check-in date",
                dateMillis = checkIn,
                onDateSelected = { checkIn = it },
                modifier = Modifier.fillMaxWidth()
            )
            DateField(
                label = "Check-out date",
                dateMillis = checkOut,
                onDateSelected = { checkOut = it },
                modifier = Modifier.fillMaxWidth()
            )

            if (datesInvalid) {
                Text(
                    text = "Check-out must be after check-in.",
                    color = MaterialTheme.colorScheme.error
                )
            } else if (nights > 0) {
                Text("Nights: $nights")
            }

            // room type
            Text(
                text = room?.room_type ?: "",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // fuests
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = adultsText,
                    onValueChange = {
                        if (it.length <= 2 && it.all { c -> c.isDigit() }) adultsText = it
                    },
                    label = { Text("Adults") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = childrenText,
                    onValueChange = {
                        if (it.length <= 2 && it.all { c -> c.isDigit() }) childrenText = it
                    },
                    label = { Text("Children") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            Text(
                text = "Rooms: $rooms",
                style = MaterialTheme.typography.titleMedium
            )

            // travel for business
            Text(
                text = "Travel for business?",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            RadioOption(
                label = "For sightseeing",
                selected = !forBusiness,
                onSelect = { forBusiness = false }
            )
            RadioOption(
                label = "+ €150 For business with a meeting room",
                selected = forBusiness,
                onSelect = { forBusiness = true }
            )

            // which way to pay?
            Text(
                text = "Which way to pay?",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            listOf("Cash", "Credit card", "E-pay").forEach { method ->
                RadioOption(
                    label = method,
                    selected = paymentMethod == method,
                    onSelect = { paymentMethod = method }
                )
            }

            // total price
            Text(
                text = "€ $totalPrice",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )

            // show the error only if a save failed
            if (!saving) {
                message?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error)
                }
            }

            Button(
                enabled = canBook && !saving,
                onClick = {
                    if (room != null && inDate != null && outDate != null) {
                        saving = true

                        bookingViewModel.saveBooking(
                            booking = Booking(
                                firstName = firstName.trim(),
                                lastName = lastName.trim(),
                                hotelName = hotelName,
                                roomType = room.room_type,
                                checkIn = inDate,
                                checkOut = outDate,
                                adults = adults,
                                children = children,
                                rooms = rooms,
                                forBusiness = forBusiness,
                                paymentMethod = paymentMethod,
                                totalPrice = totalPrice
                            ),
                            onSaved = onBookingSaved,
                            onFailed = { saving = false }
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Book now")
            }
        }
    }
}