package Servlets.JsonUtility;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import Exceptions.ApplicationException;

public class JsonUtil {
	private static final Gson gsonObj = new Gson();

	public static JsonElement getDataFieldObject(String field, JsonObject data) {
		if (data.get(field) == null) {
			throw new ApplicationException(field + " not found in the request");
		}
		return data.get(field);
	}

	public static int getJsonInt(String field, JsonObject data)
			throws JsonSyntaxException, JsonIOException, IOException {
		int value = getDataFieldObject(field, data).getAsInt();
		return value;
	}

	public static String getJsonString(String field, JsonObject data)
			throws JsonSyntaxException, JsonIOException, IOException {
		String value = data.get(field).getAsString();
		return value;
	}

	public static boolean getJsonBoolean(String field, JsonObject data) {
		boolean value = data.get(field).getAsBoolean();
		return value;
	}

	public static double getJsonDouble(String field, JsonObject data)
			throws JsonSyntaxException, JsonIOException, IOException {
		double value = data.get(field).getAsDouble();
		return value;
	}

	public static long getJsonLong(String field, JsonObject data)
			throws JsonSyntaxException, JsonIOException, IOException {

		long value = getDataFieldObject(field, data).getAsLong();
		return value;
	}

	public static LocalDate getJsonLocalDate(String field, JsonObject data) {

		String dateString = getDataFieldObject(field, data).getAsString();
		LocalDate date = LocalDate.parse(dateString);
		return date;
	}

	public static JsonObject getJsonObject(HttpServletRequest request)
			throws JsonSyntaxException, JsonIOException, IOException {
		JsonObject data = gsonObj.fromJson(request.getReader(), JsonObject.class);
		return data;
	}

	public static JsonArray getJsonArray(JsonObject data, String field) {
		JsonArray jsonString = data.getAsJsonArray(field);
		return jsonString;

	}

	public static <T> JsonObject convertListToJsonObject(List<T> list, String name) {

		JsonArray arr = gsonObj.toJsonTree(list).getAsJsonArray();
		JsonObject jsonObj = new JsonObject();
		jsonObj.add(name, arr);
		return jsonObj;
	}

	public static List<String> convertJsonArrayToStringList(JsonArray array) {

		List<String> temp = new ArrayList<>();
		for (int i = 0; i < array.size(); i++) {
			temp.add(array.get(i).getAsString());
		}
		return temp;
	}

	public static JsonObject convertMapToJson(Map<Long, Long> seatStatus) {

		JsonObject curr = new JsonObject();
		String bookingArray = gsonObj.toJson(seatStatus);
		curr.addProperty("bookingStatus", bookingArray);
		return curr;
	}

	public static List<Long> convertJsonArrayToLongList(JsonArray array) {
		List<Long> temp = new ArrayList<>();
		for (int i = 0; i < array.size(); i++) {
			temp.add(array.get(i).getAsLong());
		}
		return temp;
	}

}
