package Servlets.Validators;

import Exceptions.ApplicationException;
import Servlets.Service.DTO.BookingDTO;

public class bookingValidators {

	public static boolean validateBookingData(BookingDTO booking) {

		if (booking.isPasswordVerified() == false) {
			throw new ApplicationException("Password not verified,Booking Process Stopped");
		} else if (booking.getSeatIds() == null || booking.getSeatIds().isEmpty()) {
			throw new ApplicationException("No Seats Selected/Received.Booking Process Stopped");
		} else if (booking.getTotalAmount() <= 0) {
			throw new ApplicationException("Invalid Amount, Booking Process Stopped");
		} else if (booking.getShowId() <= 0) {
			throw new ApplicationException("Invalid Show, Booking Process Stopped");
		}
		return true;
	}
}
