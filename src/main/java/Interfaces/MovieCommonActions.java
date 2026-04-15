package Interfaces;

import java.util.List;

import Schemas.Movie;

public interface MovieCommonActions {
	

		public List<Movie> getAllMovies();

		public Movie getMovieId(String name);

		public List<String> getLanguages(long movieId);

	}

