package Servlets.Web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

public class ServerResponse {

	static final Gson gson = new Gson();

	public static void sendResponse(HttpServletResponse response, int statusCode, String message, Object list)
			throws IOException {

		ApiResponse apiResp = new ApiResponse(statusCode, message, list);

		String jsonObj = gson.toJson(apiResp);

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		PrintWriter out = response.getWriter();
		out.write(jsonObj);
		out.flush();
	}

	public static void sendUnauthorizedResponse(HttpServletResponse response, int scUnauthorized, String message) throws IOException {
		response.setStatus(scUnauthorized);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(message);
		
	}
}
