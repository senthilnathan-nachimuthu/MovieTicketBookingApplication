package Servlets.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import Schemas.Booking;
import Servlets.JsonUtility.JsonUtil;
import Servlets.JsonUtility.LocalDateAdapter;
import Servlets.JsonUtility.LocalDateTimeAdapter;
import Servlets.JsonUtility.LocalTimeAdapter;
import Servlets.Service.DTO.BookingDTO;

public class BookingService {

	public static BookingDTO constructBookingDTOObject(JsonObject data)
			throws JsonSyntaxException, JsonIOException, IOException {
		long showId = JsonUtil.getJsonLong("showId", data);
		JsonArray seatIds = data.get("seatId").getAsJsonArray();
		List<Long> seats = JsonUtil.convertJsonArrayToLongList(seatIds);
		boolean isPassVerified = JsonUtil.getJsonBoolean("isPasswordVerified", data);
		double amount = JsonUtil.getJsonDouble("Amount", data);
		BookingDTO booking = new BookingDTO(showId, seats, amount, isPassVerified);
		return booking;

	}

	public static String ConstructJsonBookingString(List<Booking> list) {
		Gson gson = new GsonBuilder()
		        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
		        .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
		        .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter()) 
		        .create();

		String json = gson.toJson(list);
		return json;
	}
}
