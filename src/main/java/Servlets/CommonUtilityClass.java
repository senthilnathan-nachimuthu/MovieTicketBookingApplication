//package Servlets;
//
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.lang.reflect.Type;
//import java.time.LocalDate;
//import java.time.LocalTime;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.regex.Pattern;
//
//import javax.servlet.annotation.WebServlet;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;
//
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import com.google.gson.JsonArray;
//import com.google.gson.JsonElement;
//import com.google.gson.JsonIOException;
//import com.google.gson.JsonObject;
//import com.google.gson.JsonSyntaxException;
//import com.google.gson.reflect.TypeToken;
//
//import Exceptions.ApplicationException;
//import Schemas.Movie;
//import Schemas.Pricing;
//import Schemas.Screen;
//import Schemas.Show;
//import Schemas.Theatre;
//import Servlets.Web.ApiResponse;
//import userInteraction.seat_Structure;
//
//public class CommonUtilityClass {
//
//	public void sendResponse(HttpServletResponse response, int statusCode, String message, Object data)
//			throws IOException {
//
//		ApiResponse apiResp = new ApiResponse(statusCode, message, data);
//
//		Gson gson = new Gson();
//		String jsonObj = gson.toJson(apiResp);
//
//		response.setContentType("application/json");
//		response.setCharacterEncoding("UTF-8");
//
//		PrintWriter out = response.getWriter();
//		out.write(jsonObj);
//		out.flush();
//	}
//
//	public String isLoggedIn(HttpServletRequest request, HttpServletResponse response) throws IOException {
//		HttpSession session = request.getSession();
//		String username = (String) session.getAttribute("username");
//		if (username == null || username.isEmpty()) {
//			// response.sendRedirect("Login.jsp");
//			return null;
//		}
//		return username;
//	}
//
//	public JsonElement getDataFieldObject(String field, JsonObject data) {
//		if (data.get(field) == null) {
//			throw new ApplicationException(field + " not found in the request");
//		}
//		return data.get(field);
//	}
//
//	public int getJsonInt(String field, JsonObject data) throws JsonSyntaxException, JsonIOException, IOException {
//		int value = getDataFieldObject(field, data).getAsInt();
//		return value;
//	}
//
//	public String getJsonString(String field, JsonObject data)
//			throws JsonSyntaxException, JsonIOException, IOException {
//		String value = data.get(field).getAsString();
//		return value;
//	}
//
//	public double getJsonDouble(String field, JsonObject data)
//			throws JsonSyntaxException, JsonIOException, IOException {
//		double value = data.get(field).getAsDouble();
//		return value;
//	}
//
//	public long getJsonLong(String field, JsonObject data) throws JsonSyntaxException, JsonIOException, IOException {
//
//		long value = getDataFieldObject(field, data).getAsLong();
//		return value;
//	}
//
//	public LocalDate getJsonLocalDate(String field, JsonObject data) {
//
//		String dateString = getDataFieldObject(field, data).getAsString();
//		LocalDate date = LocalDate.parse(dateString);
//		return date;
//	}
//
//	public JsonObject getJsonObject(HttpServletRequest request)
//			throws JsonSyntaxException, JsonIOException, IOException {
//		JsonObject data = new Gson().fromJson(request.getReader(), JsonObject.class);
//		return data;
//	}
//
//	public boolean generalValidation(String field) {
//		if (field == null || field.isEmpty()) {
//			return false;
//		}
//		return true;
//	}
//
//	public boolean PatternValidation(String pattern, String field) {
//
//		if (Pattern.matches(pattern, field)) {
//			return true;
//		}
//		return false;
//	}
//
//	public List<String> convertJsonArrayToList(JsonArray jsonarray) {
//		List<String> temp = new ArrayList<>();
//		for (int i = 0; i < jsonarray.size(); i++) {
//			temp.add(jsonarray.get(i).getAsString());
//		}
//		return temp;
//	}
//
//	public <T> JsonObject convertListToJsonObject(List<T> theatreList, String name) {
//
//		Gson g = new Gson();
//		JsonArray arr = g.toJsonTree(theatreList).getAsJsonArray();
//		JsonObject jsonObj = new JsonObject();
//		jsonObj.add(name, arr);
//		return jsonObj;
//	}
//
//	public JsonArray getJsonArray(JsonObject data, String field) {
//		Gson g = new Gson();
//		JsonArray jsonString = data.getAsJsonArray(field);
//		return jsonString;
//
//	}
//
//	public boolean isAdmin(HttpServletRequest request, HttpServletResponse response) {
//		HttpSession session = request.getSession();
//		boolean isAdmin = (boolean) session.getAttribute("isAdmin");
//		if (isAdmin == false) {
//			return false;
//		}
//		return true;
//	}
//
//	public Map<Long, List<LocalTime>> convertJsonArrayToMap(JsonArray array, String key, String listName) {
//
//		Map<Long, List<LocalTime>> temp = new HashMap<>();
//		for (int i = 0; i < array.size(); i++) {
//			JsonObject obj = array.get(i).getAsJsonObject();
//			long keyValue = obj.get(key).getAsLong();
//			List<LocalTime> showTime = getShowTime(obj);
//			temp.put(keyValue, showTime);
//		}
//		return temp;
//	}
//
//	// Common schema Utilities
//
//	public Theatre constructTheatreObject(JsonObject data) throws JsonSyntaxException, JsonIOException, IOException {
//		String theatreName = getJsonString("theatreName", data);
//		String theatreLocation = getJsonString("theatreLocation", data);
//		double theatreCredit = getJsonDouble("theatreCredit", data);
//		Theatre theatreObj = new Theatre(theatreName, theatreLocation, theatreCredit);
//		return theatreObj;
//	}
//
//	public List<seat_Structure> getSeatStructure(JsonObject data) {
//
//		Gson g = new Gson();
//		JsonArray jsonString = getJsonArray(data, "seatStructure");
//		Type typeOfList = new TypeToken<List<List<seat_Structure>>>() {
//		}.getType();
//		List<List<seat_Structure>> list = g.fromJson(jsonString, typeOfList);
//		List<seat_Structure> temp = new ArrayList<>();
//		List<Pricing> pricingList = getSeatType(data);
//		for (List<seat_Structure> l : list) {
//			for (seat_Structure i : l) {
//				int index = i.getSeatTypeIndex();
//				if (index >= 0) {
//					i.setSeatTypeObj(pricingList.get(index));
//				} else {
//					throw new ApplicationException("Seat types not assigned for some Seats");
//				}
//				temp.add(i);
//			}
//		}
//		return temp;
//	}
//
//	public List<Pricing> getSeatType(JsonObject data) {
//
//		JsonArray jsonString = getJsonArray(data, "seatTypes");
//		Type typeOfList = new TypeToken<List<Pricing>>() {
//		}.getType();
//		Gson g = new Gson();
//		List<Pricing> list = g.fromJson(jsonString, typeOfList);
//		return list;
//	}
//
//	public List<LocalTime> getShowTime(JsonObject data) {
//
//		JsonArray jsonTime = getJsonArray(data, "showTimes");
//
//		List<LocalTime> list = new ArrayList<>();
//		for (int i = 0; i < jsonTime.size(); i++) {
//			String timeString = jsonTime.get(i).getAsString();
//			LocalTime time = LocalTime.parse(timeString);
//			list.add(time);
//		}
//		// System.out.println(list);
//		return list;
//	}
//
//	public JsonObject constructScreenObject(List<Screen> screenList) {
//
//		JsonArray jsonObj = new JsonArray();
//		Gson g = new Gson();
//		for (Screen s : screenList) {
//			JsonObject curr = new JsonObject();
//			List<seat_Structure> seatList = s.getSeats();
//			String screenType = s.getScreenType();
//			List<LocalTime> showTime = s.getShowTime();
//			List<String> temp = new ArrayList<>();
//			for (LocalTime t : showTime) {
//				temp.add(t.toString());
//			}
//			JsonElement ele = g.toJsonTree(seatList);
//			JsonArray seatArray = ele.getAsJsonArray();
//			curr.add("seatStructure", seatArray);
//
//			ele = g.toJsonTree(temp);
//			JsonArray showObj = ele.getAsJsonArray();
//			curr.add("showTimes", showObj);
//			curr.addProperty("screenType", screenType);
//			curr.addProperty("screenNo", s.getScreenNo());
//			curr.addProperty("screenId", s.getScreenId());
//			curr.addProperty("theatreId", s.getTheatreId());
//			curr.addProperty("seatCapacity", s.getSeatCapacity());
//			curr.addProperty("screenName", s.getScreenName());
//
//			jsonObj.add(curr);
//		}
//		JsonObject finalObj = new JsonObject();
//		finalObj.add("Screens", jsonObj);
//		return finalObj;
//	}
//
//	public Movie constructMovieObject(JsonObject data) throws JsonSyntaxException, JsonIOException, IOException {
//
//		String movieName = getJsonString("movieName", data);
//		double duration = getJsonDouble("movieDuration", data);
//		JsonArray languages = getJsonArray(data, "movieLanguages");
//		Gson gson = new Gson();
//		Type typeOfList = new TypeToken<List<String>>() {
//		}.getType();
//		List<String> movieLanguages = gson.fromJson(languages, typeOfList);
//		if (generalValidation(movieName) == false) {
//			throw new ApplicationException("Invalid Movie Name");
//		}
//		if (PatternValidation("^[a-zA-z0-9\s]+$", movieName) == false) {
//			throw new ApplicationException("Movie Name should not contain special characters.");
//		}
//		if (duration <= 0) {
//			throw new ApplicationException("Invalid Movie Duration, Duration must be greater than zero");
//		}
//
//		Movie movieObj = new Movie(movieName, movieLanguages, duration);
//		return movieObj;
//	}
//
//	public JsonObject constructJsonMovieObject(List<Movie> movieList) {
//
//		JsonArray jsonObj = new JsonArray();
//		Gson g = new Gson();
//		for (Movie s : movieList) {
//			JsonObject curr = new JsonObject();
//
//			String movieName = s.getMovieName();
//			double duration = s.getMovieDuration();
//			List<String> languages = s.getAvailableLanguages();
//
//			curr.addProperty("movieId", s.movieId);
//			curr.addProperty("movieName", movieName);
//			curr.addProperty("movieDuration", duration);
//			JsonElement ele = g.toJsonTree(languages);
//			JsonArray languageArray = ele.getAsJsonArray();
//			curr.add("movieLanguages", languageArray);
//			jsonObj.add(curr);
//		}
//		JsonObject finalObj = new JsonObject();
//		finalObj.add("Movies", jsonObj);
//		return finalObj;
//	}
//
//	public JsonObject constructJsonShowObject(List<Show> showList) {
//		JsonArray jsonArray = new JsonArray();
//		Gson g = new Gson();
//		for (Show s : showList) {
//			JsonObject curr = new JsonObject();
//			long showId = s.getShowId();
//			long screenId = s.getScreenId();
//			String movie = s.getMovie();
//			String language = s.getLanguage();
//			LocalDate date = s.getDate();
//			LocalTime time = s.getTime();
//			int AvailableCapacity = s.getAvailableCapacity();
//			String theatre = s.getTheatre();
//			List<Pricing> pricingList = s.getPricingList();
//			Map<Long, Long> bookingStatus = s.getBookingStatus();
//			curr.addProperty("showId", showId);
//			curr.addProperty("screenId", screenId);
//			curr.addProperty("movieName", movie);
//			curr.addProperty("movieLanguage", language);
//			curr.addProperty("date", date.toString());
//			curr.addProperty("time", time.toString());
//			curr.addProperty("availableCapcaity", AvailableCapacity);
//			curr.addProperty("theatre", theatre);
//
//			JsonElement ele = g.toJsonTree(pricingList);
//			JsonArray pricingArray = ele.getAsJsonArray();
//			curr.add("Pricing", pricingArray);
//			String bookingArray = g.toJson(bookingStatus);
//			curr.addProperty("bookingStatus", bookingArray);
//
//			jsonArray.add(curr);
//
//		}
//		JsonObject finalObj = new JsonObject();
//		finalObj.add("Shows", jsonArray);
//		return finalObj;
//	}
//
//	public boolean validateShowDetails(LocalDate fromDate, LocalDate toDate, Map<Long, List<LocalTime>> screenList, String language) {
//
//		if (fromDate == null) {
//			throw new ApplicationException("Invalid FromDate");
//		}
//		if (toDate == null) {
//			throw new ApplicationException("Invalid ToDate");
//		}
//		if (screenList.isEmpty()) {
//			throw new ApplicationException("No screens Choosen");
//		}
//		if (!PatternValidation("^[a-zA-Z]+$", language)) {
//			throw new ApplicationException("Invalid Language");
//		}
//		return true;
//	}
//
//}
