package Interfaces;

import java.time.LocalTime;
import java.util.List;

import Exceptions.ApplicationException;
import Schemas.Pricing;
import Schemas.Theatre;
import userInteraction.seat_Structure;

public interface TheatreAdminActions extends TheatreCommonActions {
	public Theatre createTheatre(String theatreName, String theatreLocation, double credit) throws ApplicationException;

	public boolean createScreen(long theatreId, List<seat_Structure> seatList, List<Pricing> list,String screenType, List<LocalTime> l,String ScreenName);

	public boolean deleteTheatre(long theatreChoice);

	public boolean deleteScreen(long screenId);

	public long getTheatreId(int theatreToDelete);

	public List<LocalTime> getTiming(long theatreId, long schoice);

	public List<seat_Structure> getSeats(long theatreId, long screenId);

	public long getScreenId(long theatreId, int screenChoice);

}
