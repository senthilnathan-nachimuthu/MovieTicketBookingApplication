package Interfaces;

import java.util.Collection;
import java.util.List;

import Schemas.Screen;
import Schemas.Theatre;

public interface TheatreCommonActions {

	public List<Screen> getAllScreens(long theatreKey);

	public Collection<Theatre> getAllTheatres();

	public Theatre getTheatre(String theatreName);

	public Screen getScreen(long theatreId, long screenId);

	public Screen getScreenByShowId(long showId);
}
