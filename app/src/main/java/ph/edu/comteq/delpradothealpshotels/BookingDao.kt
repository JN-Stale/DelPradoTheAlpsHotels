package ph.edu.comteq.delpradothealpshotels

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BookingDao {

    @Insert
    suspend fun insert(booking: Booking)

    @Query("SELECT * FROM bookings ORDER BY id ASC")
    fun observeAll(): Flow<List<Booking>>
}