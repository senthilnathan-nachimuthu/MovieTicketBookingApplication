package Utilities;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import DAO.bookingDAO;
import DAO.movieDAO;
import DAO.screenDAO;
import DAO.showDAO;
import Exceptions.ApplicationException;
import Schemas.Booking;
import Schemas.Pricing;
import Schemas.Screen;
import Schemas.Show;
import userInteraction.InputHandler;

public class ShowUtility {

	List<ShowCreationResult> showNotCreatedList = new ArrayList<>();

	public List<ShowCreationResult> createShows(LocalDate fromDate, LocalDate toDate,
			Map<Long, List<LocalTime>> screenList, long movieId, String language) {

		showDAO show = new showDAO();
		screenDAO screen = new screenDAO();
		MovieUtility movieUtilityObj = new MovieUtility();

		long language_id = movieUtilityObj.getLanguageId(language);
		long language_mapping_id = movieUtilityObj.getLanguageMappingId(movieId, language_id);
		if (language_mapping_id == -1) {
			throw new ApplicationException("Movie for Selected Language not found");
		}

		Map<LocalDate, Long> dateList = show.insertDate(fromDate, toDate);

		for (Map.Entry<Long, List<LocalTime>> s : screenList.entrySet()) {

			long screenId = s.getKey();
			Screen screenObj = screen.findScreen(screenId);
			if (screenObj != null) {

				long theatreId = screenObj.getTheatreId();
				long theatre_details_id = show.insertTheatreMovieDetails(theatreId, screenId, language_mapping_id);

				Map<LocalTime, Long> timeList = show.findTimeId(screenId);

				List<Object[]> ObjectParams = generateShowParams(fromDate, toDate, dateList, timeList, screenId,
						theatreId, screenObj.getScreenName(), s.getValue(), theatre_details_id);
				List<Pricing> PricingList = screen.findAllPricing(screenId);

				if (ObjectParams.size() > 0) {
					show.insertShows(ObjectParams, screenObj.getSeats(), PricingList);
				}

			} else {
				throw new ApplicationException("Screen not found.");
			}
		}
		return showNotCreatedList;
	}

	public List<Object[]> generateShowParams(LocalDate fromDate, LocalDate toDate, Map<LocalDate, Long> dateList,
			Map<LocalTime, Long> timeList, long screenId, long theatreId, String screenName, List<LocalTime> showTimes,
			long theatre_details_id) {

		boolean isShowCreated = false;
		List<Object[]> ObjectParams = new ArrayList<>();
		for (LocalDate i = fromDate; !i.isAfter(toDate); i = i.plusDays(1)) {

			Long date_id = dateList.get(i);
			if (date_id == null) {
				showNotCreatedList.add(new ShowCreationResult(screenId, i.toString(), null, false,
						"Invalid Date,Shows not created for this date"));
				continue;
			}

			for (LocalTime currTime : showTimes) {

				if (!isShowExists(theatreId, screenId, i, currTime)) {

					Long time_id = timeList.get(currTime);
					if (time_id == null) {
						showNotCreatedList.add(new ShowCreationResult(screenId, i.toString(), currTime.toString(),
								false, "Invalid Time"));
						continue;
					}

					Object[] params = new Object[] { theatre_details_id, date_id, time_id };
					ObjectParams.add(params);
					isShowCreated = true;
				} else {
					showNotCreatedList.add(new ShowCreationResult(screenId, i.toString(), currTime.toString(), false,
							"Already Show Created for this Screen on same Date and Time."));
				}
			}
		}
		if (isShowCreated == false) {
			throw new ApplicationException("No Shows Created Due to Date and Time Conflict.");
		}
		return ObjectParams;
	}

	private boolean isShowExists(long theatreId, long screenId, LocalDate date, LocalTime time) {
		showDAO show = new showDAO();
		List<Show> showList = show.findShowsByTheatreandScreen(theatreId, screenId);

		for (Show s : showList) {
			if (s.getDate().equals(date) && s.getTime().equals(time)) {
				return true;
			}
		}

		return false;
	}

	public List<Show> getShowsByTheatreandScreen(long theatreId, long screenId) {

		showDAO show = new showDAO();
		List<Show> temp = show.findShowsByTheatreandScreen(theatreId, screenId);

		return temp;

	}

	public List<Show> getShowsByTheatre(long theatreId) {

		showDAO show = new showDAO();
		List<Show> temp = show.findShowsByTheatre(theatreId);
		if (temp.isEmpty()) {
			throw new ApplicationException("No Show For this Theatre.");
		}

		return temp;

	}

	public List<Show> getShowsByMovie(long movieId) {
		showDAO show = new showDAO();
		List<Show> showList = show.getShowsByMovie(movieId);
		return showList;

	}

	public long getShowId(long theatreId, long screenId, int choice) {
		List<Show> showList = getShowsByTheatreandScreen(theatreId, screenId);
		if (choice <= showList.size()) {
			return showList.get(choice - 1).showId;
		}
		return -1;
	}

	public boolean removeShows(List<String> list) {

		showDAO showDaoObj = new showDAO();
		List<Object[]> showToRemove = new ArrayList<>();
		for (String s : list) {

			Long id = Long.parseLong(s);
			if (id != null) {
				showToRemove.add(new Object[] { id });
			}
			CancelShowBookings(id, "Booking Cancelled Due to Show Deletion");
//			BookingUtility bookingUtil = new BookingUtility();
//			bookingDAO booking = new bookingDAO();
//			List<Booking> bookingList = booking.findbookingByShows(id);
//			bookingUtil.deleteListOfBooking(bookingList, "Booking Cancelled Due to Show Deletion");
		}
		return showDaoObj.deleteShows(showToRemove);

	}

	public boolean CancelShowBookings(long showId, String reason) {
		BookingUtility bookingUtil = new BookingUtility();
		bookingDAO booking = new bookingDAO();
		List<Booking> bookingList = booking.findbookingByShows(showId);
		bookingUtil.deleteListOfBooking(bookingList, reason);
		return true;
	}

	public boolean removeBookingSeats(List<Long> temp, long showId) {

		List<Object[]> list = new ArrayList<>();
		for (Long i : temp) {
			list.add(new Object[] { i, showId });
		}
		showDAO show = new showDAO();
		return show.updateBookedSeats(list);
			
	}

	public boolean changeShowMovie(long showId, long movieid, String language) {
		showDAO show = new showDAO();
		movieDAO movie = new movieDAO();

		long language_id = movie.findLanguage(language);
		if (language_id == -1) {
			throw new ApplicationException("Invalid Language");
		}
		long language_mapping_id = movie.findMovieMappingId(movieid, language_id);
		if (language_mapping_id != -1) {
			if (show.updateShowMovie(showId, language_mapping_id)) {
				CancelShowBookings(showId, "Booking Cancelled - Show Movie Changed");
				return true;
			}
		}
		throw new ApplicationException("Show Movie not Updated.");
	}

	public Show getShowById(long showId) {

		showDAO show = new showDAO();
		return show.findShowById(showId);

	}

	public boolean updatePrice(long show_id, List<Pricing> l) {

		showDAO show = new showDAO();
		return show.updateShowPrice(show_id, l);

	}

	public Map<Long, Long> getSeatBookingStatus(long showId) {
		showDAO show = new showDAO();
		Map<Long, Long> seatStatus = show.findSeatBookingStatus(showId);
		if (seatStatus.isEmpty()) {
			throw new ApplicationException("Booking Status Not found for this show");
		}
		return seatStatus;
	}

	public boolean bookSeat(Show show, long booking_id, List<Long> seats) {

		if (show != null) {
			showDAO showDaoObj = new showDAO();
			return showDaoObj.bookSeats(show.showId, booking_id, seats);
		}
		return false;

	}

	public List<Integer> generateSeats(int fromSeat, int toSeat) {
		List<Integer> temp = new ArrayList<>();
		for (int i = fromSeat; i <= toSeat; i++) {
			temp.add(i);
		}
		return temp;
	}

//	public boolean removeBooking(List<Booking> seats) {
//
//		showDAO show = new showDAO();
//		return show.cancelBookedSeats(seats);
//
//	}

	public List<Show> getAllShows() {
		showDAO show = new showDAO();
		List<Show> showList = show.getAllShows();
		if (showList.isEmpty()) {
			throw new ApplicationException("No Shows Found");
		}
		return showList;
	}
//
//	public Show getBinShowById(int showId) {
//
//		return showBin.get(showId);
//	}

	public List<Show> getShowsByMovieLanguage(long movieId, String language) {

		movieDAO movie = new movieDAO();
		long language_id = movie.findLanguage(language);
		long language_mapping_id = movie.findMovieMappingId(movieId, language_id);
		showDAO show = new showDAO();
		List<Show> showList = show.getShowsByMovieLanguage(movieId, language_mapping_id);
		if (showList.isEmpty()) {
			throw new ApplicationException("No Shows Found for this Movie");
		}
		return showList;

	}

	public List<String> getActualSeats(Show showObj, long booking_id, List<Long> l) {

		InputHandler ipObj = new InputHandler();
		if (showObj != null) {
			screenDAO screenDao = new screenDAO();
			Screen screenObj = screenDao.findDeletedScreenByShow(showObj.showId);
			if (l != null) {
				return ipObj.getSeatLabel(l, booking_id, screenObj);

			}
		}
		return null;
	}

	public List<Show> getSimilarShows(long showId) {

		showDAO show = new showDAO();
		return show.findSimilarShows(showId);

	}

	public Show getShowForBooking(long showId) {
		showDAO show = new showDAO();
		return show.findDeletedShow(showId);

	}

	public boolean CreateShowPricing(long showId, List<Pricing> pricingList) {
		showDAO show = new showDAO();
		if (show.insertShowPricing(pricingList, showId)) {
			return true;
		}
		throw new ApplicationException("Pricing for the Show is not defined.");
	}

	public List<Pricing> getShowPricing(long showId) {
		showDAO show = new showDAO();
		List<Pricing> pricingList = show.findShowPricing(showId);
		if (pricingList.isEmpty() || pricingList == null) {
			throw new ApplicationException("Seat Types not found");
		}
		return pricingList;
	}

}
