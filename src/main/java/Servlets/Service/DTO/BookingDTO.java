package Servlets.Service.DTO;

import java.util.List;

public class BookingDTO {

	long showId;
	List<Long> seatIds;
	double totalAmount;
	boolean isPasswordVerified;

	public BookingDTO(long showId, List<Long> seatIds, double totalAmount, boolean isPasswordVerified) {
		super();
		this.showId = showId;
		this.seatIds = seatIds;
		this.totalAmount = totalAmount;
		this.isPasswordVerified = isPasswordVerified;
	}

	public long getShowId() {
		return showId;
	}

	public void setShowId(long showId) {
		this.showId = showId;
	}

	public List<Long> getSeatIds() {
		return seatIds;
	}

	public void setSeatIds(List<Long> seatIds) {
		this.seatIds = seatIds;
	}

	public double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public boolean isPasswordVerified() {
		return isPasswordVerified;
	}

	public void setPasswordVerified(boolean isPasswordVerified) {
		this.isPasswordVerified = isPasswordVerified;
	}

}
