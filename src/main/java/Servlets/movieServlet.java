package Servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Logger;

import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import Exceptions.ApplicationException;
import Schemas.Movie;
import Servlets.JsonUtility.JsonUtil;
import Servlets.Service.MovieService;
import Servlets.SessionUtil.SessionValidation;
import Servlets.Web.ServerResponse;
import Utilities.AppLogger;
import Utilities.MovieUtility;

/**
 * Servlet implementation class movieServlet
 */
@WebServlet({ "/movieServlet", "/addMovie", "/getAllMovies", "/deleteMovie", "/updateMovie" })
public class movieServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = AppLogger.getLogger(movieServlet.class);

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String path = request.getServletPath();
		if (path.equals("/getAllMovies")) {
			handleGetAllMoviesRequest(request, response);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String path = request.getServletPath();
		if (path.equals("/addMovie")) {
			handleAddMoviePostRequest(request, response);
		} else if (path.equals("/deleteMovie")) {
			handleDeleteMoviePostRequest(request, response);
		} else if (path.equals("/updateMovie")) {
			handleUpdateMoviePostRequest(request, response);
		}

	}

	private void handleAddMoviePostRequest(HttpServletRequest request, HttpServletResponse response)
			throws JsonSyntaxException, JsonIOException, IOException {

		JsonObject data = JsonUtil.getJsonObject(request);
		Movie movieObj = MovieService.constructMovieObject(data);
		MovieUtility movieUtilObj = new MovieUtility();
		if (movieUtilObj.CreateMovie(movieObj.getMovieName(), movieObj.getAvailableLanguages(),
				movieObj.getMovieDuration())) {
			ServerResponse.sendResponse(response, 200, "Movie Added successfully", data);
		} else {
			throw new ApplicationException("Operation Failed,Movie Not added.");

		}

	}

	private void handleGetAllMoviesRequest(HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		MovieUtility movieUtilObj = new MovieUtility();
		List<Movie> movieList = new ArrayList<>();
		
		if (SessionValidation.isAdmin(request, response)) {
			movieList = movieUtilObj.getAllMovies();
		} else {
			movieList = movieUtilObj.getAvailableMoviesForBooking();
		}
		
		if (!movieList.isEmpty()) {
			JsonObject data = MovieService.constructJsonMovieObject(movieList);
			if (data != null) {
				ServerResponse.sendResponse(response, 200, "Movie Details fetched successfully", data);
			}
		} else {
			throw new ApplicationException("No Movies found.");

		}
	}

	private void handleDeleteMoviePostRequest(HttpServletRequest request, HttpServletResponse response)
			throws JsonSyntaxException, JsonIOException, IOException {

		JsonObject data = JsonUtil.getJsonObject(request);
		long movieId = JsonUtil.getJsonLong("movieId", data);
		MovieUtility movieUtilObj = new MovieUtility();
		if (movieUtilObj.removeMovie(movieId)) {
			ServerResponse.sendResponse(response, 200, "Movie deleted Successfully", data);
		}

	}

	private void handleUpdateMoviePostRequest(HttpServletRequest request, HttpServletResponse response)
			throws JsonSyntaxException, JsonIOException, IOException {

		JsonObject data = JsonUtil.getJsonObject(request);
		Movie movieObj = MovieService.constructMovieObject(data);
		long movieId = JsonUtil.getJsonLong("movieId", data);
		movieObj.setMovieId(movieId);

		MovieUtility movieUtilObj = new MovieUtility();
		if (movieUtilObj.updateMovieDetails(movieObj)) {
			ServerResponse.sendResponse(response, 200, "Movie Details Updated Successfully.", data);
		}

	}

//	private void handleGetMovieByTheatreRequest(HttpServletRequest request, HttpServletResponse response)
//			throws JsonSyntaxException, JsonIOException, IOException {
//
//		String theatreIdString = request.getParameter("theatreId");
//		long theatreId = -1;
//		if (theatreIdString != null) {
//			theatreId = Long.parseLong(theatreIdString);
//		}
//		logger.info("Get Request for Movies filter out by theatre Id " + theatreId);
//		if (theatreId != -1) {
//			MovieUtility movieUtil = new MovieUtility();
//			List<Movie> movieList = movieUtil.getMoviesByTheatre(theatreId);
//			if (!movieList.isEmpty()) {
//				ServerResponse.sendResponse(response, 200, "Movies in this theatre fetched successfully", movieList);
//			}
//		}
//
//	}

}
