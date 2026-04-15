package Schemas;
import java.time.LocalDateTime;
import java.util.List;
public class Booking {
	
	public long bookingId;
	public long userId;
	public long showId;
	public String dateTimeOfBooking;
	public String bookingDescription;
	public boolean bookingStatus;
	public double totalAmount;
	public double creditEarned;
	public List<String> seats;
	public Show showObj;
	
	public List<String> getSeats() {
		return seats;
	}
	public void setSeats(List<String> seats) {
		this.seats = seats;
	}
	public void setShowObj(Show showObj) {
		this.showObj = showObj;
	}
	public Booking(long bookingId, long userId, long showId,  
			LocalDateTime dateTime, String bookingDescription, boolean bookingStatus,
			double totalAmount, double creditEarned) {
		
		this.bookingId = bookingId;
		this.userId = userId;
		this.showId = showId;
		this.dateTimeOfBooking=dateTime.toString();
		this.bookingDescription = bookingDescription;
		this.bookingStatus = bookingStatus;
		this.totalAmount = totalAmount;
		this.creditEarned = creditEarned;
	}
	public long getBookingId() {
		return bookingId;
	}
	public void setBookingId(long bookingId) {
		this.bookingId = bookingId;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public long getShowObj() {
		return showId;
	}
	public void setShowObj(long showObj) {
		this.showId = showObj;
	}
		
	public long getShowId() {
		return showId;
	}
	public void setShowId(long showId) {
		this.showId = showId;
	}
	public String getDateTimeOfBooking() {
		return dateTimeOfBooking;
	}
	public void setDateTimeOfBooking(String dateTimeOfBooking) {
		this.dateTimeOfBooking = dateTimeOfBooking;
	}
	public String getBookingDescription() {
		return bookingDescription;
	}
	public void setBookingDescription(String bookingDescription) {
		this.bookingDescription = bookingDescription;
	}
	public boolean isBookingStatus() {
		return bookingStatus;
	}
	public void setBookingStatus(boolean bookingStatus) {
		this.bookingStatus = bookingStatus;
	}
	public double getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}
	public double getCreditEarned() {
		return creditEarned;
	}
	public void setCreditEarned(double creditEarned) {
		this.creditEarned = creditEarned;
	}
}
	
	