package Interfaces;

public interface commonAction {

	public boolean displayMovies();

	public boolean displayTheatres();

	public boolean displayScreens(long theatreId);

	public void searchByTheatre(String theatreName);
	public boolean displayMovieLanguages(long movie_id);
	public boolean searchByMovie(String movieName);
}
