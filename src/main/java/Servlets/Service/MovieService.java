package Servlets.Service;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import Exceptions.ApplicationException;
import Schemas.Movie;
import Servlets.JsonUtility.JsonUtil;
import Servlets.Validators.CommonValidation;

public class MovieService {
	private static 	final Gson gson = new Gson();
	public static Movie constructMovieObject(JsonObject data) throws JsonSyntaxException, JsonIOException, IOException {

		String movieName = JsonUtil.getJsonString("movieName", data);
		double duration = JsonUtil.getJsonDouble("movieDuration", data);
		JsonArray languages = JsonUtil.getJsonArray(data, "movieLanguages");
		Type typeOfList = new TypeToken<List<String>>() {
		}.getType();
		List<String> movieLanguages = gson.fromJson(languages, typeOfList);
		if (CommonValidation.generalValidation(movieName) == false) {
			throw new ApplicationException("Invalid Movie Name");
		}
		if (CommonValidation.PatternValidation("^[a-zA-z0-9]+$", movieName) == false) {
			throw new ApplicationException("Movie Name should not contain special characters.");
		}
		if (duration <= 0) {
			throw new ApplicationException("Invalid Movie Duration, Duration must be greater than zero");
		}

		Movie movieObj = new Movie(movieName, movieLanguages, duration);
		return movieObj;
	}

	public  static JsonObject constructJsonMovieObject(List<Movie> movieList) {

		JsonArray jsonObj = new JsonArray();
		Gson g = new Gson();
		for (Movie s : movieList) {
			JsonObject curr = new JsonObject();
			String movieName = s.getMovieName();
			double duration = s.getMovieDuration();
			List<String> languages = s.getAvailableLanguages();

			curr.addProperty("movieId", s.movieId);
			curr.addProperty("movieName", movieName);
			curr.addProperty("movieDuration", duration);
			JsonElement ele = g.toJsonTree(languages);
			JsonArray languageArray = ele.getAsJsonArray();
			curr.add("availableLanguages", languageArray);
			jsonObj.add(curr);
		}
		JsonObject finalObj = new JsonObject();
		finalObj.add("Movies", jsonObj);
		return finalObj;
	}
}
