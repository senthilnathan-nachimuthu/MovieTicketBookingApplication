package Servlets.Service;

import java.io.IOException;

import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import Schemas.Theatre;
import Servlets.JsonUtility.JsonUtil;

public class TheatreService {
	
	public static Theatre constructTheatreObject(JsonObject data) throws JsonSyntaxException, JsonIOException, IOException {
		
		String theatreName = JsonUtil.getJsonString("theatreName", data);
		String theatreLocation = JsonUtil.getJsonString("theatreLocation", data);
		double theatreCredit = JsonUtil.getJsonDouble("theatreCredit", data);
		Theatre theatreObj = new Theatre(theatreName, theatreLocation, theatreCredit);
		return theatreObj;
	}

}
