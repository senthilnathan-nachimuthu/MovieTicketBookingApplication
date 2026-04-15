package Servlets.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import Schemas.Pricing;
import Schemas.Show;
import Servlets.JsonUtility.JsonUtil;
import Servlets.Service.DTO.ShowDTO;

public class ShowService {

	public static Map<Long, List<LocalTime>> convertJsonArrayToTimeMap(JsonArray array) {

		Map<Long, List<LocalTime>> temp = new HashMap<>();
		for (int i = 0; i < array.size(); i++) {
			JsonObject obj = array.get(i).getAsJsonObject();
			long keyValue = obj.get("screenId").getAsLong();
			List<LocalTime> showTime = ScreenService.getShowTime(obj);
			temp.put(keyValue, showTime);
		}
		return temp;
	}
	public static JsonObject constructJsonShowObject(List<Show> showList) {
		JsonArray jsonArray = new JsonArray();
		Gson g = new Gson();
		for (Show s : showList) {
			JsonObject curr = new JsonObject();
			long showId = s.getShowId();
			long screenId = s.getScreenId();
			String movie = s.getMovie();
			String language = s.getLanguage();
			LocalDate date = s.getDate();
			LocalTime time = s.getTime();
			int AvailableCapacity = s.getAvailableCapacity();
			String theatre = s.getTheatre();
			String screenName=s.getScreenName();
			List<Pricing> pricingList = s.getPricingList();
			Map<Long, Long> bookingStatus = s.getBookingStatus();
			curr.addProperty("showId", showId);
			curr.addProperty("screenId", screenId);
			curr.addProperty("movieName", movie);
			curr.addProperty("movieLanguage", language);
			curr.addProperty("date", date.toString());
			curr.addProperty("time", time.toString());
			curr.addProperty("availableCapcaity", AvailableCapacity);
			curr.addProperty("theatre", theatre);
			curr.addProperty("screenName", screenName);
			JsonObject screenObj=ScreenService.constructScreenObject(s.getScreenObj());
			curr.add("Screen", screenObj);
			

			JsonElement ele = g.toJsonTree(pricingList);
			JsonArray pricingArray = ele.getAsJsonArray();
			curr.add("Pricing", pricingArray);
			
			
			String bookingArray = g.toJson(bookingStatus);
			curr.addProperty("bookingStatus", bookingArray);

			jsonArray.add(curr);

		}
		JsonObject finalObj = new JsonObject();
		finalObj.add("Shows", jsonArray);
		return finalObj;
	}
	public static ShowDTO parseShowDToObject(JsonObject data) throws JsonSyntaxException, JsonIOException, IOException {
		LocalDate fromDate = JsonUtil.getJsonLocalDate("fromDate", data);
		LocalDate toDate = JsonUtil.getJsonLocalDate("toDate", data);
		JsonArray screenArray = JsonUtil.getJsonArray(data, "choosedScreens");
		Map<Long, List<LocalTime>> screenList = ShowService.convertJsonArrayToTimeMap(screenArray);
		long movieId = JsonUtil.getJsonLong("movieId", data);
		String Language = JsonUtil.getJsonString("movieLanguage", data);
		ShowDTO show=new ShowDTO(fromDate, toDate, screenList, movieId, Language);
		return show;
	}


}
