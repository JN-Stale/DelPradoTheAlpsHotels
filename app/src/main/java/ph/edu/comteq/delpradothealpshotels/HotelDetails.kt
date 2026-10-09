package ph.edu.comteq.delpradothealpshotels
data class HotelDetails(
    val hotel_id: Int,
    val hotel_name: String,
    val guest_reviews: GuestReviews,
    val rooms: List<Room>
)

data class GuestReviews(
    val ratings_categories: List<Map<String, Double>>,
    val reviews_objects: List<Review> = emptyList()
)

data class Review(
    val username: String,
    val country: String,
    val review_text: String
)

data class Room(
    val room_id: Int,
    val room_type: String,
    val room_bed_type: String,
    val room_total_number_of_guests: Int,
    val room_features: List<String>,
    val room_price_for_one_night: Int
)
