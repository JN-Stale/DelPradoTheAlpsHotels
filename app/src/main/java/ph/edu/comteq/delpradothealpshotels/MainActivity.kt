package ph.edu.comteq.delpradothealpshotels

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.StarRate
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import com.google.gson.Gson
import kotlin.math.roundToInt
import ph.edu.comteq.delpradothealpshotels.ui.theme.DelPradoTheAlpsHotelsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DelPradoTheAlpsHotelsTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "home") {
                    composable("home") {
                        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                            Homepage(
                                modifier = Modifier.padding(innerPadding),
                                onHotelClick = { hotel ->
                                    navController.navigate("booking/${hotel.hotel_id}")
                                }
                            )
                        }
                    }
                    composable(
                        route = "booking/{hotelId}",
                        arguments = listOf(navArgument("hotelId") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val hotelId = backStackEntry.arguments?.getInt("hotelId") ?: return@composable
                        BookScreen(
                            hotelId = hotelId,
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Homepage(
    modifier: Modifier = Modifier,
    onHotelClick: (Hotel) -> Unit = {}
) {
    val context = LocalContext.current
    var hotels by remember { mutableStateOf(emptyList<Hotel>()) }

    // Add state to hold the current search text in the search bar
    var searchQuery by remember { mutableStateOf("") }

    // Load JSON data
    LaunchedEffect(Unit) {
        val json = context.assets.open("hotels.json")
            .bufferedReader().use { it.readText() }
        val gson = Gson()
        val hotelsArray = gson.fromJson(json, Array<Hotel>::class.java)
        hotels = hotelsArray.toList()
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Header Composable
        Header()

        // SearchBar Composable
        SearchBar(
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it }
        )

        // Filter hotel list functionality based on what is searched
        val filteredHotels = hotels.filter {
            it.hotel_name.contains(searchQuery, ignoreCase = true)
        }

        // List Composable
        HotelList(
            hotels = filteredHotels,
            onHotelClick = onHotelClick
        )
    }
}

@Composable
fun Header() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF664930))
            .height(80.dp)
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "The Alphs Hotel",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFFDBBB)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Image(
            painter = painterResource(id = R.drawable.france_national_flag),
            contentDescription = "France National Flag",
            modifier = Modifier.size(35.dp),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.weight(1f))

        Icon(
            imageVector = Icons.Outlined.Person,
            contentDescription = "User Icon",
            modifier = Modifier.size(35.dp)
        )
    }
}

@Composable
fun SearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit
) {
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        placeholder = { Text("Search hotels...") },
        singleLine = true
    )
}

@Composable
fun HotelList(
    hotels: List<Hotel>,
    modifier: Modifier = Modifier,
    onHotelClick: (Hotel) -> Unit = {}
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        items(hotels) { hotel ->
            HotelCard(
                hotel = hotel,
                onHotelClick = { onHotelClick(hotel) }
            )
        }
    }
}

@Composable
fun HotelCard(
    hotel: Hotel,
    modifier: Modifier = Modifier,
    onHotelClick: () -> Unit = {}
) {
    Card(
        onClick = onHotelClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2C2C2C)
        ),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = "file:///android_asset/${hotel.hotel_cover_image}",
                contentDescription = hotel.hotel_name,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Information Column
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Hotel Name
                Text(
                    text = hotel.hotel_name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                // Stars Row using star from Icon
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "Rating: ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )

                    val stars = (hotel.hotel_rating / 2.0).roundToInt().coerceIn(0, 5)
                    repeat(5) { index ->
                        Icon(
                            imageVector = Icons.Filled.StarRate,
                            contentDescription = "Star Rating",
                            tint = if (index < stars) {
                                Color(0xFFFFC107)
                            } else {
                                Color.Gray
                            },
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Description
                Text(
                    text = "Description",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.LightGray
                )

                // Distance
                Text(
                    text = "${hotel.hotel_to_ski_distance} km to ski slope",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.LightGray
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookScreen(
    hotelId: Int,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    var details by remember { mutableStateOf<HotelDetails?>(null) }
    var error by remember { mutableStateOf("") }
    var selectedTab by remember { mutableIntStateOf(0) }

    LaunchedEffect(hotelId) {
        val hotelDetails = "hotels_details.$hotelId.json"
        val assets = context.assets.list("") ?: emptyArray()

        if (hotelDetails in assets) {
            val json = context.assets.open(hotelDetails)
                .bufferedReader().use { it.readText() }
            details = Gson().fromJson(json, HotelDetails::class.java)
            error = ""
        } else {
            details = null
            error = "Details for hotel not found"
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Booking", fontWeight = FontWeight.Bold) },
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
        ) {
            PrimaryTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color(0xFF2C2C2C),
                contentColor = Color(0xFFFFDBBB)
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = "Guest reviews",
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    selectedContentColor = Color(0xFFFFDBBB),
                    unselectedContentColor = Color.LightGray
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            text = "Room selection",
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    selectedContentColor = Color(0xFFFFDBBB),
                    unselectedContentColor = Color.LightGray
                )
            }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                if (details != null) {
                    item {
                        Text(
                            text = details?.hotel_name ?: "",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 16.dp)
                        )
                    }
                }

                if (error.isNotEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = error,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.Red,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                details?.let { hotel ->
                    if (selectedTab == 0) {
                        item {
                            Text(
                                text = "Ratings",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .background(Color(0xFF2C2C2C))
                                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
                            )
                        }

                        items(hotel.guest_reviews.ratings_categories) { category ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .background(Color(0xFF2C2C2C))
                                    .padding(horizontal = 16.dp, vertical = 6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = category.keys.first(),
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = category.values.first().toString(),
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFFDBBB)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { (category.values.first() / 10).toFloat() },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp)),
                                    color = Color(0xFFFFDBBB),
                                    trackColor = Color(0xFF555555)
                                )
                            }
                        }

                        item {
                            Text(
                                text = "Reviews",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                            )
                        }

                        item {
                            LazyRow(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(hotel.guest_reviews.reviews_objects) { review ->
                                    ReviewCard(review = review)
                                }
                            }
                        }
                    } else {
                        item {
                            Text(
                                text = "Rooms",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                            )
                        }

                        items(hotel.rooms) { room ->
                            RoomCard(room = room)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReviewCard(
    review: Review,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(280.dp)
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2C2C2C)
        ),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF664930)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = review.username.firstOrNull()?.toString() ?: "?",
                        color = Color(0xFFFFDBBB),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = review.username,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = review.country,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.LightGray
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = review.review_text,
                color = Color(0xFFEEEEEE),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun RoomCard(
    room: Room,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2C2C2C)
        ),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = room.room_type,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Bed: ${room.room_bed_type}",
                color = Color(0xFFDDDDDD),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Total number of guests: ${room.room_total_number_of_guests}",
                color = Color(0xFFDDDDDD),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = room.room_features.joinToString(" • "),
                style = MaterialTheme.typography.bodySmall,
                color = Color.LightGray
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "€ ${room.room_price_for_one_night} / night",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFDBBB),
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomepagePreview() {
    DelPradoTheAlpsHotelsTheme {
        Homepage()
    }
}
