package ph.edu.comteq.delpradothealpshotels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BookingViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val dao = AppDatabase
        .getInstance(application)
        .bookingDao()

    private val _message = MutableStateFlow<String?>(null)
    val message = _message.asStateFlow()

    val bookings = dao.observeAll()
        .catch { exception ->
            if (exception is CancellationException) throw exception
            _message.value = "Could not load bookings."
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun saveBooking(booking: Booking, onSaved: () -> Unit) {
        performWrite {
            dao.insert(booking)
            _message.value = "Booking saved."
            onSaved()
        }
    }

    private fun performWrite(action: suspend () -> Unit) {
        viewModelScope.launch {
            _message.value = null

            try {
                action()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _message.value = "Could not save the change."
            }
        }
    }
}