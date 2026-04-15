package Utilities;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import DAO.bookingDAO;
import DAO.creditDAO;
import DAO.screenDAO;
import DAO.showDAO;
import DAO.theatreDAO;
import Exceptions.ApplicationException;
import Interfaces.TheatreAdminActions;
import Interfaces.TheatreUserActions;
import Schemas.Booking;
import Schemas.Pricing;
import Schemas.Screen;
import Schemas.Show;
import Schemas.Theatre;
import userInteraction.seat_Structure;

public class TheatreUtility implements TheatreAdminActions, TheatreUserActions {

	public Theatre createTheatre(String theatreName, String theatreLocation, double credit)
			throws ApplicationException {

		theatreDAO theatreDaoObj = new theatreDAO();

		Theatre existingTheatreObj = theatreDaoObj.isTheatreExists(theatreName, theatreLocation);
		System.out.println(existingTheatreObj);

		if (existingTheatreObj != null) {

			throw new ApplicationException("Same Theatre in same location already added.");

		}
		Theatre theatreObj = theatreDaoObj.addTheatre(theatreName, theatreLocation);

		if (theatreObj != null) {
			creditDAO creditObj = new creditDAO();
			creditObj.insertTheatreCredits(credit, theatreObj.theatreId);
		}
		return theatreObj;

	}

	public boolean createScreen(long theatreId, List<seat_Structure> seatList, List<Pricing> list, String screenType,
			List<LocalTime> l, String screenName) {

		if (theatreId <= 0) {
			throw new ApplicationException("Invalid theatreId, Screen not added");
		}
		screenDAO screenDaoObj = new screenDAO();
		long screen_id = screenDaoObj.insertScreen(theatreId, screenType, seatList, list, seatList.size(), l,
				screenName);
		if (screen_id <= 0) {
			throw new ApplicationException("Screen Not created.");
		}
		return true;
	}

	public Theatre getTheatre(String theatrename) {
		theatreDAO theatreDaoObj = new theatreDAO();
		return theatreDaoObj.findByTheatrename(theatrename);
	}

	public long getTheatreId(int theatreToDelete) {

		List<Theatre> list = getAllTheatres();
		if (theatreToDelete <= list.size()) {
			return list.get(theatreToDelete - 1).theatreId;
		}

		return -1;
	}

	public Screen getScreen(long theatreId, long screenId) {

		screenDAO screenDaoObj = new screenDAO();
		Screen screenObj = screenDaoObj.findScreen(screenId);
		return screenObj;
	}

	public long getScreenId(long theatreId, int screenChoice) {
		List<Screen> list = getAllScreens(theatreId);
		if (screenChoice <= list.size()) {
			return list.get(screenChoice - 1).screenId;
		}
		return -1;
	}

	public Screen getScreenByShowId(long showId) {
		screenDAO screen = new screenDAO();
		return screen.findScreenByShow(showId);
	}

	public List<Theatre> getAllTheatres() {
		theatreDAO theatreDaoObj = new theatreDAO();
		List<Theatre> list = theatreDaoObj.findAllTheatres();
		return list;

	}

	public List<Screen> getAllScreens(long theatreKey) {
		screenDAO theatreDaoObj = new screenDAO();
		List<Screen> screens = theatreDaoObj.findAllScreens(theatreKey);
		if (screens == null || screens.isEmpty()) {
			throw new ApplicationException("No Screen Exists for this theatre.");
		}
		return screens;

	}

	public List<seat_Structure> getSeats(long theatreId, long screenId) {

		Screen screenobj = getScreen(theatreId, screenId);
		if (screenobj != null) {
			return screenobj.getSeats();
		}
		return null;
	}

	public boolean deleteTheatre(long theatreID) {

		theatreDAO theatreDaoObj = new theatreDAO();
		if (!theatreDaoObj.deleteByTheatreId(theatreID)) {
			throw new ApplicationException("No Theatre Found deletion failed");
		}
		screenDAO screen = new screenDAO();
		screen.deleteAllScreensByTheatre(theatreID);
		showDAO show = new showDAO();
		show.deleteAllShowsByTheatre(theatreID);
		return true;

	}

	public boolean deleteScreen(long screenId) {

		if (screenId <= 0) {
			throw new ApplicationException("Invalid screenId, Screen not added");
		}
		bookingDAO booking = new bookingDAO();
		List<Booking> screenBookings = booking.findBookingByScreen(screenId);
		if (!screenBookings.isEmpty()) {
			throw new ApplicationException("Bookings Found in This Screen.Cancel Bookings First");
		}
		screenDAO screenDaoObj = new screenDAO();
		screenDaoObj.deleteScreen(screenId);
		showDAO show = new showDAO();
		show.deleteAllShowsByScreen(screenId);
		return true;

	}

	public List<LocalTime> getTiming(long theatreId, long screenId) {

		Screen obj = getScreen(theatreId, screenId);
		if (obj != null) {
			return obj.getShowTime();
		}
		return null;
	}

	public double getTotalFair(Show showObj, List<Long> seats, Screen screenObj) {

		List<seat_Structure> seat_StructureList = screenObj.getSeats();

		Map<Long, Long> bookingStatus = showObj.getBookingStatus();
		double total = 0;
		for (Long l : seats) {
			for (seat_Structure s : seat_StructureList) {
				if (s.getSeatId() == l) {
					total += showObj.getShowPricing(s.getSeatType());

				}
			}
		}

		return total;
	}

	public boolean updateTheatreDetails(long theatreId, String theatreName, String theatreLocation,
			double theatreCredit) {

		theatreDAO theatreDaoObj = new theatreDAO();
		Theatre existingTheatreObj = theatreDaoObj.isTheatreExists(theatreName, theatreLocation);
		if (existingTheatreObj != null && existingTheatreObj.theatreId != theatreId) {
			throw new ApplicationException("Updation failed,Name and Location already matches with Existing Record.");
		}
		Theatre theatreObj = getTheatreById(theatreId);
		boolean isChanged = false;
		if (!theatreObj.getTheatreName().equals(theatreName)) {
			theatreObj.setTheatreName(theatreName);
			isChanged = true;
		}
		if (!theatreObj.getLocation().equals(theatreLocation)) {
			theatreObj.setTheatreLocation(theatreLocation);
			isChanged = true;
		}

		if (getTheatreCredits(theatreId) != theatreCredit) {
			theatreObj.setTheatreCredit(theatreCredit);
			isChanged = true;
		}
		if (isChanged == true) {
			theatreDaoObj.updateTheatreDetails(theatreObj);
			showDAO show = new showDAO();
			show.deleteAllShowsByTheatre(theatreId);
		} else {
			throw new ApplicationException("No changes Made to existing records.");
		}
		return true;
	}

	public double getTheatreCredits(long theatreId) {
		creditDAO credit = new creditDAO();
		return credit.findCreditByTheatre(theatreId);
	}

	private Theatre getTheatreById(long theatreId) {

		theatreDAO theatre = new theatreDAO();
		Theatre obj = theatre.findTheatreById(theatreId);

		return obj;
	}

	public boolean updateScreenDetails(long screenId, List<seat_Structure> seatList, List<Pricing> list,
			String screenType, List<LocalTime> showTime, String screenName) {
		if (screenId <= 0) {
			throw new ApplicationException("Invalid screenId, Screen not added");
		}

		bookingDAO booking = new bookingDAO();
		List<Booking> screenBookings = booking.findBookingByScreen(screenId);
		if (!screenBookings.isEmpty()) {
			throw new ApplicationException("Bookings Found in This Screen.Cancel Bookings First");
		}

		screenDAO screen = new screenDAO();
		screen.updateScreenData(screenId, seatList, screenType, showTime, list, screenName);
		return true;
	}

	public List<Pricing> getScreenSeatTypes(long screenId) {
		screenDAO screen = new screenDAO();
		List<Pricing> list = screen.findAllPricing(screenId);
		if (list == null || list.isEmpty()) {
			throw new ApplicationException("No SeatTypes found for this screen.");
		}
		return list;
	}

	public List<Theatre> getAvailableTheatres() {

		showDAO show = new showDAO();
		List<Show> showList = show.getAllShows();
		List<Theatre> theatreList = filterOutAvailableTheatres(showList);
		if (theatreList.isEmpty()) {
			throw new ApplicationException("No Theatre Available For booking.");
		}
		return theatreList;
	}

	private List<Theatre> filterOutAvailableTheatres(List<Show> showList) {

		Set<Long> theatreIds = new HashSet<>();
		theatreDAO theatre = new theatreDAO();
		Map<Long, Theatre> theatreList = theatre.findAllTheatresByMapping();
		List<Theatre> result = new ArrayList<>();
		for (Show sh : showList) {
			theatreIds.add(sh.getTheatreId());
		}
		for (Long Id : theatreIds) {
			Theatre theatreObj = theatreList.get(Id);
			if (theatreObj != null) {
				result.add(theatreObj);
			}
		}
		return result;
	}

}
