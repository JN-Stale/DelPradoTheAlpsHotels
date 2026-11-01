package ph.edu.comteq.delpradothealpshotels

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookings")
data class Booking(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val firstName: String,
    val lastName: String,

    val hotelName: String,
    val roomType: String,

    val checkIn: Long,
    val checkOut: Long,

    val adults: Int,
    val children: Int,
    val rooms: Int,

    val forBusiness: Boolean,
    val paymentMethod: String,

    val totalPrice: Int
)