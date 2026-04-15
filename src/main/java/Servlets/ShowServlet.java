package Servlets;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import Exceptions.ApplicationException;
import Schemas.Pricing;
import Schemas.Show;
import Servlets.JsonUtility.JsonUtil;
import Servlets.Service.ScreenService;
import Servlets.Service.ShowService;
import Servlets.Service.DTO.ShowDTO;
import Servlets.Validators.ShowValidation;
import Servlets.Web.ServerResponse;
import Utilities.AppLogger;
import Utilities.ShowCreationResult;
import Utilities.ShowUtility;

@WebServlet({ "/ShowServlet", "/createShow", "/getAllShows", "/getSeatTypes", "/updateShowPrice", "/deleteShows",
		"/updateShowMovie", "/getShowsByMovie", "/getShowsByTheatre", "/getBookingStatus" })
public class ShowServlet extends HttpServlet {
	private static final Logger logger = AppLogger.getLogger(ShowServlet.class);

	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String path = request.getServletPath();
		if (path.equals("/getAllShows")) {
			handleGetAllShowsGetRequest(request, response);
		} else if (path.equals("/getSeatTypes")) {
			handleGetScreenSeatTypes(request, response);
		} else if (path.equals("/getShowsByMovie")) {
			handleGetShowsByMovie(request, response);
		} else if (path.equals("/getShowsByTheatre")) {
			handleGetShowsByTheatre(request, response);
		} else if (path.equals("/getBookingStatus")) {
			handleGetSeatBookingStatus(request, response);
		}

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String path = request.getServletPath();
		if (path.equals("/createShow")) {
			handleCreateShowPostRequest(request, response);
		} else if (path.equals("/updateShowPrice")) {
			handleUpdateShowPricing(request, response);
		} else if (path.equals("/deleteShows")) {
			handleDeleteShows(request, response);
		} else if (path.equals("/updateShowMovie")) {
			handleShowMovieUpdation(request, response);
		}
	}

	private void handleCreateShowPostRequest(HttpServletRequest request, HttpServletResponse response)
			throws JsonSyntaxException, JsonIOException, IOException {

		JsonObject data = JsonUtil.getJsonObject(request);
		ShowDTO showDtoObj = ShowService.parseShowDToObject(data);
		logger.info("Show Creation Request by Admin");
		if (ShowValidation.validateShowDetails(showDtoObj.getFromDate(), showDtoObj.getToDate(),
				showDtoObj.getScreenList(), showDtoObj.getLanguage())) {
			logger.info("Show Creation Data is Validated");
			ShowUtility showObj = new ShowUtility();
			List<ShowCreationResult> showNotCreatedList = showObj.createShows(showDtoObj.getFromDate(),
					showDtoObj.getToDate(), showDtoObj.getScreenList(), showDtoObj.getMovieId(),
					showDtoObj.getLanguage());
			if (showNotCreatedList.isEmpty()) {
				logger.info("All Shows are SucessFully created by Admin");
				ServerResponse.sendResponse(response, 200, "All Shows Created SucessFully", data);
			} else {
				logger.error("Some Shows are Skipped Due to Date Conflict");
				ServerResponse.sendResponse(response, 201, "Some Shows are Skipped ", showNotCreatedList);
			}
		}
	}

	private void handleGetAllShowsGetRequest(HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		ShowUtility showUtilObj = new ShowUtility();
		List<Show> showList = showUtilObj.getAllShows();
		if (!showList.isEmpty()) {
			JsonObject showObj = ShowService.constructJsonShowObject(showList);
			ServerResponse.sendResponse(response, 200, "All Shows Fetched Successfully", showObj);
		}

	}

	private void handleGetScreenSeatTypes(HttpServletRequest request, HttpServletResponse response) throws IOException {

		long showId = Long.parseLong(request.getParameter("showId"));
		ShowUtility show = new ShowUtility();
		List<Pricing> list = show.getShowPricing(showId);

		if (!list.isEmpty()) {
			ServerResponse.sendResponse(response, 200, "seatTypes Feteched", list);
		}
	}

	private void handleUpdateShowPricing(HttpServletRequest request, HttpServletResponse response)
			throws JsonSyntaxException, JsonIOException, IOException {

		JsonObject data = JsonUtil.getJsonObject(request);
		long showId = JsonUtil.getJsonLong("showId", data);
		List<Pricing> pricingList = ScreenService.getSeatType(data);
		ShowUtility show = new ShowUtility();
		if (show.CreateShowPricing(showId, pricingList)) {
			ServerResponse.sendResponse(response, 200, "Show Pricing defined Sucessfully", data);
		}
	}

	private void handleDeleteShows(HttpServletRequest request, HttpServletResponse response)
			throws JsonSyntaxException, JsonIOException, IOException {

		JsonObject data = JsonUtil.getJsonObject(request);
		JsonArray showId = JsonUtil.getJsonArray(data, "showId");
		List<String> list = JsonUtil.convertJsonArrayToStringList(showId);
		ShowUtility show = new ShowUtility();
		if (!list.isEmpty() && show.removeShows(list)) {
			ServerResponse.sendResponse(response, 200, "All Shows are Deleted Sucessfully", list);
		} else {
			throw new ApplicationException("Show Deletion Failed, Due to Empty List.");
		}
	}

	private void handleShowMovieUpdation(HttpServletRequest request, HttpServletResponse response)
			throws JsonSyntaxException, JsonIOException, IOException {

		JsonObject data = JsonUtil.getJsonObject(request);
		long showId = JsonUtil.getJsonLong("showId", data);
		long movieId = JsonUtil.getJsonLong("movieId", data);
		String language = JsonUtil.getJsonString("movieLanguage", data);

		ShowUtility showUtil = new ShowUtility();
		if (showUtil.changeShowMovie(showId, movieId, language)) {
			ServerResponse.sendResponse(response, 200, "Show movie Updated Sucessfully.", data);
		}

	}

	private void handleGetShowsByTheatre(HttpServletRequest request, HttpServletResponse response) throws IOException {

		String theatreIdString = request.getParameter("theatreId");
		long theatreId = -1;
		if (theatreIdString != null) {
			theatreId = Long.parseLong(theatreIdString);
		}
		logger.info("Get Request for Movies filter out by theatre Id " + theatreId);
		if (theatreId != -1) {
			ShowUtility showUtil = new ShowUtility();
			List<Show> showList = showUtil.getShowsByTheatre(theatreId);
			if (!showList.isEmpty()) {
				JsonObject showObj = ShowService.constructJsonShowObject(showList);
				ServerResponse.sendResponse(response, 200, "Shows in this theatre fetched successfully", showObj);
			}
		}
	}

	private void handleGetShowsByMovie(HttpServletRequest request, HttpServletResponse response) throws IOException {

		String movieIdString = request.getParameter("movieId");
		String language = request.getParameter("Language");
		if (movieIdString == null) {
			throw new ApplicationException("Invalid Movie (MovieId not Null)");
		}
		long movieId = Long.parseLong(movieIdString);

		if (movieId != -1) {
			ShowUtility showUtil = new ShowUtility();
			List<Show> ShowList = showUtil.getShowsByMovieLanguage(movieId, language);
			if (!ShowList.isEmpty()) {
				JsonObject showObj = ShowService.constructJsonShowObject(ShowList);
				ServerResponse.sendResponse(response, 200, "Theatres for this Movie Fetched Successfully", showObj);
			}

		}
	}

	private void handleGetSeatBookingStatus(HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		String showIdString = request.getParameter("showId");
		long showId = Long.parseLong(showIdString);
		ShowUtility show = new ShowUtility();
		Map<Long, Long> seatStatus = show.getSeatBookingStatus(showId);
		if (!seatStatus.isEmpty()) {
			JsonObject data = JsonUtil.convertMapToJson(seatStatus);
			ServerResponse.sendResponse(response, 200, "Seat Status Fetched Successfully", data);
		}

	}
}
