package Utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import DAO.movieDAO;
import DAO.showDAO;
import Exceptions.ApplicationException;
import Interfaces.MovieAdminActions;
import Interfaces.MovieUserActions;
import Schemas.Movie;
import Schemas.Show;

public class MovieUtility implements MovieAdminActions, MovieUserActions {

	public boolean CreateMovie(String name, List<String> lang, double duration) {

		movieDAO movieDaoObj = new movieDAO();
		long movie_id = -1;
		Movie movieObj = movieDaoObj.isMovieExists(name, duration);

		if (movieObj != null) {
			List<String> newLanguages = getLanguageDifference(lang, movieObj.getAvailableLanguages());
			if (newLanguages.isEmpty()) {
				throw new ApplicationException(
						"This movie is already added in the database for the specified languages.");
			} else {
				movieDaoObj.LanguageMappingFunction(movieObj.movieId, newLanguages);
				return true;
			}
		} else {
			movie_id = movieDaoObj.insertMovie(name, lang, duration);
			return true;
		}
	}

	private List<String> getLanguageDifference(List<String> lang, List<String> availableLanguages) {

		List<String> temp = new ArrayList<>();
		for (String language : lang) {
			if (!availableLanguages.contains(language)) {
				temp.add(language);
			}
		}
		return temp;
	}

	public Movie getMovieId(String name) {

		movieDAO movie = new movieDAO();
		return movie.findMovieByName(name);
	}

	public Movie getMovie(long movieId) {

		movieDAO movie = new movieDAO();
		return movie.findMovieById(movieId);
	}

	public List<Movie> getAllMovies() {
		movieDAO movieObj = new movieDAO();
		List<Movie> movieList = movieObj.findAllMovies();
		return movieList;
	}

	public boolean removeMovie(long movieId) {
		movieDAO movieDao = new movieDAO();
		movieDao.deleteMovie(movieId);
		showDAO show = new showDAO();
		show.deleteAllShowsByMovie(movieId);
		return true;

	}

	public boolean changeMovieName(long movieId, String movieName) {

		movieDAO movieDao = new movieDAO();
		return movieDao.updateMovieName(movieId, movieName);
	}

	public long getMovieId(int movieChoice) {
		List<Movie> movies = getAllMovies();
		if (movieChoice <= movies.size()) {
			return movies.get(movieChoice - 1).movieId;
		}
		return -1;
	}

	public List<String> getLanguages(long movieId) {

		movieDAO movie = new movieDAO();
		return movie.findMovieLanguage(movieId);

	}

	public long getLanguageId(String language) {
		movieDAO movie = new movieDAO();
		return movie.findLanguage(language);

	}

	public long getLanguageMappingId(long movieId, long language_id) {

		movieDAO movie = new movieDAO();
		return movie.findMovieMappingId(movieId, language_id);
	}

	public boolean updateMovieDetails(Movie movieObj) {
		movieDAO movie = new movieDAO();
		Movie oldMovieObj = movie.findMovieById(movieObj.getMovieId());
		if (oldMovieObj == null) {
			throw new ApplicationException("Movie Not found, Updation Failed.");
		}

		Movie duplicateMovieObj = movie.isMovieExists(movieObj.getMovieName(), movieObj.getDuration());
		if (duplicateMovieObj != null && duplicateMovieObj.getMovieId() != oldMovieObj.getMovieId()) {
			throw new ApplicationException(
					"The updated Movie Details Matches with the existing records(Same movie name and duration).Updation failed!!");
		}
		movie.updateMovieDetails(movieObj, movieObj.getAvailableLanguages());

		return true;
	}

	public List<Movie> getAvailableMoviesForBooking() {

		showDAO show = new showDAO();
		List<Show> showList = show.getAllShows();
		if (showList.isEmpty()) {
			throw new ApplicationException("Currently No Movies Available For Booking");
		}
		List<Movie> movieList = filterOutMoviesFromShows(showList);
		return movieList;
	}

	private List<Movie> filterOutMoviesFromShows(List<Show> showList) {

		movieDAO movie = new movieDAO();
		Map<Long, Movie> totalMovieList = movie.findAllMovieByMapping();
		Map<Long, Movie> movieList = new HashMap<>();

		for (Show show : showList) {

			long movie_id = show.getMovieId();
			if (movieList.get(movie_id) != null) {

				Movie oldObj = movieList.get(movie_id);
				List<String> oldLanguage = oldObj.getShowCreatedLanguaes();
				if (!oldLanguage.contains(show.getLanguage())) {
					oldLanguage.add(show.getLanguage());
					oldObj.setShowCreatedLanguaes(oldLanguage);
					movieList.put(movie_id, oldObj);
				}

			} else {
				Movie newObj = totalMovieList.get(movie_id);
				List<String> newLanguage = new ArrayList<>();
				newLanguage.add(show.getLanguage());
				newObj.setShowCreatedLanguaes(newLanguage);
				movieList.put(movie_id, newObj);
			}
		}

		List<Movie> temp = new ArrayList<>();
		for (Map.Entry<Long, Movie> m : movieList.entrySet()) {
			Movie movieObj = m.getValue();
			movieObj.setAvailableLanguages(movieObj.getShowCreatedLanguaes());
			temp.add(movieObj);
		}

		return temp;
	}

}
