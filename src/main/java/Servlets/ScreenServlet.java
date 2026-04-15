package Servlets;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import Schemas.Screen;
import Servlets.JsonUtility.JsonUtil;
import Servlets.Service.ScreenService;
import Servlets.Service.DTO.ScreenDTO;
import Servlets.Validators.ScreenValidation;
import Servlets.Web.ServerResponse;
import Utilities.TheatreUtility;

/**
 * Servlet implementation class ScreenServlet
 */
@WebServlet({"/ScreenServlet","/addScreen",
		"/getAllScreens", "/deleteScreen", "/updateScreen"})
public class ScreenServlet extends HttpServlet {


	
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String path=request.getServletPath();
		 if (path.equals("/getAllScreens")) {
			handleGetAllScreensGetRequest(request, response);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String path=request.getServletPath();
	 if (path.equals("/addScreen")) {
			handleAddScreenPostRequest(request, response);
		} else if (path.equals("/deleteScreen")) {
			handleDeleteScreenPostRequest(request, response);
		} else if (path.equals("/updateScreen")) {
			handleUpdateScreenPostRequest(request, response);
		} 

	}
	private void handleAddScreenPostRequest(HttpServletRequest request, HttpServletResponse response)
			throws JsonSyntaxException, JsonIOException, IOException {

		JsonObject data = JsonUtil.getJsonObject(request);
		ScreenDTO screenObj = ScreenService.ParseScreenObject(data);

		TheatreUtility theatreUtilityObj = new TheatreUtility();
		if (ScreenValidation.validateScreenDetails(screenObj.getSeats(), screenObj.getSeatTypes(),
				screenObj.getScreenType(), screenObj.getShowTimes(), screenObj.getScreenName())) {

			if (theatreUtilityObj.createScreen(screenObj.getTheatreId(), screenObj.getSeats(), screenObj.getSeatTypes(),
					screenObj.getScreenType(), screenObj.getShowTimes(), screenObj.getScreenName())) {
				ServerResponse.sendResponse(response, 200, "Screen SuccessFully Created", data);
			}
		}
	}

	private void handleUpdateScreenPostRequest(HttpServletRequest request, HttpServletResponse response)
			throws JsonSyntaxException, JsonIOException, IOException {

		JsonObject data = JsonUtil.getJsonObject(request);
		ScreenDTO screenObj = ScreenService.ParseScreenObject(data);

		TheatreUtility thObj = new TheatreUtility();

		if ((ScreenValidation.validateScreenDetails(screenObj.getSeats(), screenObj.getSeatTypes(),
				screenObj.getScreenType(), screenObj.getShowTimes(), screenObj.getScreenName()))) {
			if (thObj.updateScreenDetails(screenObj.getScreenId(), screenObj.getSeats(), screenObj.getSeatTypes(),
					screenObj.getScreenType(), screenObj.getShowTimes(), screenObj.getScreenName())) {
				ServerResponse.sendResponse(response, 200, "Screen details updated.", data);
			}
		}
	}

	private void handleDeleteScreenPostRequest(HttpServletRequest request, HttpServletResponse response)
			throws JsonSyntaxException, JsonIOException, IOException {

		JsonObject data = JsonUtil.getJsonObject(request);
		long screenId = JsonUtil.getJsonLong("ScreenId", data);

		TheatreUtility thObj = new TheatreUtility();

		if (thObj.deleteScreen(screenId)) {
			ServerResponse.sendResponse(response, 200, "Screen Deleted Successfully", data);
		}
	}

	private void handleGetAllScreensGetRequest(HttpServletRequest request, HttpServletResponse response)
			throws JsonSyntaxException, JsonIOException, IOException {

		TheatreUtility theatreUtilityObj = new TheatreUtility();
		String theatreId = request.getParameter("theatreId");

		if (theatreId != null) {
			List<Screen> screenList = theatreUtilityObj.getAllScreens(Integer.parseInt(theatreId));
			JsonObject jsonObj = ScreenService.constructScreenObject(screenList);

			if (!screenList.isEmpty()) {
				ServerResponse.sendResponse(response, 200, "Screen Details Fetched Successfully", jsonObj);
			}
		}
	}
}
