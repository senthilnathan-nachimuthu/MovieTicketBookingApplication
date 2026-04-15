package Utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import DAO.bookingDAO;
import DAO.creditDAO;
import DAO.showDAO;
import DAO.theatreDAO;
import Exceptions.ApplicationException;
import Schemas.Booking;
import Schemas.Show;
import Schemas.Theatre;
import Schemas.Users;
import WalletPackage.WalletManagement;

public class BookingUtility {

	static Map<Integer, Booking> bookingList = new HashMap<>();
	static Map<Integer, Booking> cancelledBookingList = new HashMap<>();

	public double startBookingProcess(Users userObj, long showId, List<Long> seatIds, double totalAmount) {

		if (!checkSeatAvailability(showId, seatIds)) {
			throw new ApplicationException("Selected Seats are no longer available");
		}
		ShowUtility showUtil = new ShowUtility();
		WalletManagement wmObj = new WalletManagement();
		if (wmObj.payThroughWallet(totalAmount, userObj)) {
			Show ToBeBooked = showUtil.getShowById(showId);
			double credits = calculateCredits(ToBeBooked, seatIds.size());
			if (bookNow(ToBeBooked, userObj, totalAmount, credits, seatIds)) {
				if (wmObj.addCredit(userObj, credits)) {
					System.out.println("Credits Added" + credits);
					return credits;
				}
			} else {
				wmObj.refundWalletBalance(userObj, totalAmount);
			}
		} else {
			throw new ApplicationException("Payment Failed.");
		}
		return -1;

	}

	private boolean checkSeatAvailability(long showId, List<Long> seatIds) {
		ShowUtility show = new ShowUtility();
		Map<Long, Long> seatStatus = show.getSeatBookingStatus(showId);
		for (Long id : seatIds) {
			if (seatStatus.get(id) != -1) {
				return false;
			}
		}
		return true;
	}

	public boolean bookNow(Show show, Users userObj, double totalAmount, double credit, List<Long> seats) {

		bookingDAO booking = new bookingDAO();
		long booking_id = booking.insertBooking(show.showId, totalAmount, credit, userObj.userId,
				("Booked by user with username " + userObj.getusername()), seats);
		System.out.println("Bookings inserted" + booking_id);
		ShowUtility showUtilityObj = new ShowUtility();
		if (showUtilityObj.bookSeat(show, booking_id, seats)) {
			return true;
		}
		return false;
	}

	public List<Booking> getUserBooking(Users u) {
		List<Booking> bookingList = new ArrayList<>();
		bookingDAO booking = new bookingDAO();
		bookingList = booking.findBookingByUserId(u.userId);
		if (bookingList.isEmpty()) {
			throw new ApplicationException("No Bookings found");
		}
		ShowUtility showutilityObj = new ShowUtility();
		for (Booking b : bookingList) {
			Show showObj = showutilityObj.getShowById(b.getShowId());
			if (showObj != null) {
				List<Long> seats = booking.findBookedSeats(b.getBookingId());
				List<String> seatLabel = showutilityObj.getActualSeats(showObj, b.getBookingId(), seats);
				b.setSeats(seatLabel);
				b.setShowObj(showObj);
			}
		}
		return bookingList;
	}

	public boolean deleteListOfBooking(List<Booking> bookingList, String description) {
		List<Object[]> seatListToRemove = new ArrayList<>();
		List<Object[]> bookingListToRemove = new ArrayList<>();
		for (Booking bookingObj : bookingList) {
			long bookingId = bookingObj.getBookingId();
			seatListToRemove.add(new Object[] { bookingId, bookingObj.getShowId() });
			bookingListToRemove.add(new Object[] { description, bookingId });
		}
		System.out.println(seatListToRemove);
		if (!seatListToRemove.isEmpty()) {
			showDAO show = new showDAO();
			show.cancelBookingsByShows(seatListToRemove);
		}
		if (!bookingListToRemove.isEmpty()) {
			bookingDAO booking = new bookingDAO();
			booking.cancelBooking(bookingListToRemove);
		}
		return true;
	}

	public boolean cancelBooking(long bookingId) {
		bookingDAO booking = new bookingDAO();
		Booking bookingObj = booking.findBookingById(bookingId);

		double refundAmount = bookingObj.getTotalAmount();
		double recoveredCredit = bookingObj.getCreditEarned();

		if (deleteBooking(bookingObj, "Booking Cancelled by User.")) {

			WalletManagement wmObj = new WalletManagement();
			Users userObj = userUtilities.getUser(bookingObj.getUserId());

			if (wmObj.refundWalletBalance(userObj, refundAmount)) {
				return true;
			}
		}
		throw new ApplicationException("Booking Cancellation Failed");
	}

	public boolean deleteBooking(Booking bookingObj, String description) {

		bookingDAO booking = new bookingDAO();
		ShowUtility showUtilityObj = new ShowUtility();
		if (bookingObj != null) {

			showDAO show = new showDAO();
			List<Long> temp = show.findSeatsByBookingId(bookingObj.getBookingId(), bookingObj.getShowId());
			System.out.println("Seats " + temp + " showId: " + bookingObj.getShowId());
			if (showUtilityObj.removeBookingSeats(temp, bookingObj.getShowId())) {
				return booking.updateBookingStatus(bookingObj.getBookingId(), description);
			}

		}
		return false;
	}

	public boolean isValid(long bookingId, Users userObj) {

		bookingDAO booking = new bookingDAO();
		Booking b = booking.findBookingById(bookingId);
		if (b != null && b.getUserId() == userObj.userId) {
			return true;
		}
		return false;
	}

	public Show getBooking(long bookingId) {

		bookingDAO booking = new bookingDAO();

		Booking bookingObj = booking.findBookingById(bookingId);
		if (bookingObj != null) {
			showDAO show = new showDAO();
			Show showObj = show.findShowById(bookingObj.getShowId());
			return showObj;

		}

		return null;

	}

	public double getTotalAmount(long bookingId) {
		bookingDAO booking = new bookingDAO();
		Booking b = booking.findBookingById(bookingId);
		if (b != null) {
			return b.getTotalAmount();
		}
		return 0;
	}

	private double calculateCredits(Show toBeBooked, int numberOfSeats) {

		double credit = 0;
		theatreDAO theatre = new theatreDAO();
		Theatre theatreObj = theatre.findByTheatrename(toBeBooked.getTheatre());
		creditDAO cd = new creditDAO();
		double num = cd.findCreditByTheatre(theatreObj.theatreId);
		credit = num * numberOfSeats;
		return credit;
	}

	public double getCredit(long bookingId) {
		bookingDAO booking = new bookingDAO();
		Booking b = booking.findBookingById(bookingId);
		if (b != null) {
			return b.getCreditEarned();
		}
		return 0;
	}

}
