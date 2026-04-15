package Servlets.Service;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import Exceptions.ApplicationException;
import Schemas.Pricing;
import Schemas.Screen;
import Servlets.JsonUtility.JsonUtil;
import Servlets.Service.DTO.ScreenDTO;
import userInteraction.seat_Structure;

public class ScreenService {

	private static final Gson g = new Gson();
	public static List<seat_Structure> getSeatStructure(JsonObject data) {

		JsonArray jsonString = JsonUtil.getJsonArray(data, "seatStructure");
		Type typeOfList = new TypeToken<List<List<seat_Structure>>>() {
		}.getType();
		List<List<seat_Structure>> list = g.fromJson(jsonString, typeOfList);
		List<seat_Structure> temp = new ArrayList<>();
		List<Pricing> pricingList = getSeatType(data);
		for (List<seat_Structure> l : list) {
			for (seat_Structure i : l) {
				int index = i.getSeatTypeIndex();
				if (index >= 0) {
					i.setSeatTypeObj(pricingList.get(index));
				} else {
					throw new ApplicationException("Seat types not assigned for some Seats");
				}
				temp.add(i);
			}
		}
		return temp;
	}

	public static List<Pricing> getSeatType(JsonObject data) {

		JsonArray jsonString = JsonUtil.getJsonArray(data, "seatTypes");
		Type typeOfList = new TypeToken<List<Pricing>>() {
		}.getType();
		List<Pricing> list = g.fromJson(jsonString, typeOfList);
		return list;
	}

	public static List<LocalTime> getShowTime(JsonObject data) {

		JsonArray jsonTime = JsonUtil.getJsonArray(data, "showTimes");

		List<LocalTime> list = new ArrayList<>();
		for (int i = 0; i < jsonTime.size(); i++) {
			String timeString = jsonTime.get(i).getAsString();
			LocalTime time = LocalTime.parse(timeString);
			list.add(time);
		}
		// System.out.println(list);
		return list;
	}

	public static ScreenDTO ParseScreenObject(JsonObject screenData)
			throws JsonSyntaxException, JsonIOException, IOException {

		ScreenDTO req = new ScreenDTO();

		if (screenData.has("theatreId"))
			req.setTheatreId(JsonUtil.getJsonLong("theatreId", screenData));

		if (screenData.has("screenId"))
			req.setScreenId(JsonUtil.getJsonLong("screenId", screenData));

		req.setScreenType(JsonUtil.getJsonString("screenType", screenData));
		req.setScreenName(JsonUtil.getJsonString("screenName", screenData));
		req.setSeats(ScreenService.getSeatStructure(screenData));
		req.setSeatTypes(ScreenService.getSeatType(screenData));
		req.setShowTimes(ScreenService.getShowTime(screenData));

		return req;
	}

	public static JsonObject constructScreenObject(List<Screen> screenList) {

		JsonArray jsonObj = new JsonArray();
		for (Screen s : screenList) {
			JsonObject curr = new JsonObject();
			List<seat_Structure> seatList = s.getSeats();
			String screenType = s.getScreenType();
			List<LocalTime> showTime = s.getShowTime();
			List<String> temp = new ArrayList<>();
			for (LocalTime t : showTime) {
				temp.add(t.toString());
			}
			JsonElement ele = g.toJsonTree(seatList);
			JsonArray seatArray = ele.getAsJsonArray();
			curr.add("seatStructure", seatArray);

			ele = g.toJsonTree(temp);
			JsonArray showObj = ele.getAsJsonArray();
			curr.add("showTimes", showObj);
			curr.addProperty("screenType", screenType);
			curr.addProperty("screenNo", s.getScreenNo());
			curr.addProperty("screenId", s.getScreenId());
			curr.addProperty("theatreId", s.getTheatreId());
			curr.addProperty("seatCapacity", s.getSeatCapacity());
			curr.addProperty("screenName", s.getScreenName());

			jsonObj.add(curr);
		}
		JsonObject finalObj = new JsonObject();
		finalObj.add("Screens", jsonObj);
		return finalObj;
	}

	public static JsonObject constructScreenObject(Screen s) {

		JsonArray jsonObj = new JsonArray();
		
			JsonObject curr = new JsonObject();
			List<seat_Structure> seatList = s.getSeats();
			String screenType = s.getScreenType();
			List<LocalTime> showTime = s.getShowTime();
			List<String> temp = new ArrayList<>();
			for (LocalTime t : showTime) {
				temp.add(t.toString());
			}
			JsonElement ele = g.toJsonTree(seatList);
			JsonArray seatArray = ele.getAsJsonArray();
			curr.add("seatStructure", seatArray);

			ele = g.toJsonTree(temp);
			JsonArray showObj = ele.getAsJsonArray();
			curr.add("showTimes", showObj);
			curr.addProperty("screenType", screenType);
			curr.addProperty("screenNo", s.getScreenNo());
			curr.addProperty("screenId", s.getScreenId());
			curr.addProperty("theatreId", s.getTheatreId());
			curr.addProperty("seatCapacity", s.getSeatCapacity());
			curr.addProperty("screenName", s.getScreenName());

			jsonObj.add(curr);
		
		JsonObject finalObj = new JsonObject();
		finalObj.add("Screens", jsonObj);
		return finalObj;
	}
}
