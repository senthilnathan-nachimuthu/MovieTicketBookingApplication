package Servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import Exceptions.ApplicationException;
import Schemas.Theatre;
import Servlets.JsonUtility.JsonUtil;
import Servlets.Service.TheatreService;
import Servlets.SessionUtil.SessionValidation;
import Servlets.Validators.TheatreValidation;
import Servlets.Web.ServerResponse;
import Utilities.TheatreUtility;

@WebServlet({ "/TheatreServlet", "/addTheatre", "/getAllTheatres", "/updateTheatre", "/deleteTheatre" })
public class TheatreServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String path = request.getServletPath();
		if (path.equals("/getAllTheatres")) {
			handleGetAllTheatresGetRequest(request, response);
		}

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String path = request.getServletPath();

		if (path.equals("/addTheatre")) {
			handleAddTheatrePostRequest(request, response);
		} else if (path.equals("/updateTheatre")) {
			handleUpdateTheatrePostRequest(request, response);
		} else if (path.equals("/deleteTheatre")) {
			handleDeleteTheatrePostRequest(request, response);
		}
	}

	private void handleDeleteTheatrePostRequest(HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		JsonObject data = JsonUtil.getJsonObject(request);
		long theatreId = JsonUtil.getJsonLong("theatreId", data);
		TheatreUtility theatreUtilityObj = new TheatreUtility();
		if (theatreUtilityObj.deleteTheatre(theatreId)) {
			ServerResponse.sendResponse(response, 200, "Theatres deleted Successfully", null);

		}
	}

	private void handleGetAllTheatresGetRequest(HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		TheatreUtility theatreUtilityObj = new TheatreUtility();
		List<Theatre> theatreList = new ArrayList<>();
		if (SessionValidation.isAdmin(request, response)) {
			theatreList = theatreUtilityObj.getAllTheatres();
		} else {
			theatreList = theatreUtilityObj.getAvailableTheatres();
		}
		
		
		if (!theatreList.isEmpty()) {
			JsonObject jsObj = JsonUtil.convertListToJsonObject(theatreList, "TheatreDetails");
			ServerResponse.sendResponse(response, 200, "Theatres Fetched Successfully", jsObj);
		} else {
			throw new ApplicationException("No Theatre Exists");
		}

	}

	private void handleUpdateTheatrePostRequest(HttpServletRequest request, HttpServletResponse response)
			throws JsonSyntaxException, JsonIOException, IOException, ApplicationException {

		JsonObject data = JsonUtil.getJsonObject(request);
		long theatreId = JsonUtil.getJsonLong("theatreId", data);
		Theatre theatreObj = TheatreService.constructTheatreObject(data);

		if (theatreObj != null && TheatreValidation.validateTheatreDetails(theatreObj.getTheatreName(),
				theatreObj.getTheatreLocation(), theatreObj.getTheatreCredit())) {

			TheatreUtility theatreUtilityObj = new TheatreUtility();

			if (theatreUtilityObj.updateTheatreDetails(theatreId, theatreObj.getTheatreName(),
					theatreObj.getTheatreLocation(), theatreObj.getTheatreCredit())) {
				ServerResponse.sendResponse(response, 200, "Theatre details updated successfully.", data);
			}

		} else {
			throw new ApplicationException("Invalid Theatre details");
		}

	}

	private void handleAddTheatrePostRequest(HttpServletRequest request, HttpServletResponse response)
			throws JsonSyntaxException, JsonIOException, IOException, ApplicationException {

		JsonObject data = JsonUtil.getJsonObject(request);
		System.out.println(data);
		System.out.println("Theatre Servlet Reached");
		Theatre theatreObj = TheatreService.constructTheatreObject(data);

		if (theatreObj != null && TheatreValidation.validateTheatreDetails(theatreObj.getTheatreName(),
				theatreObj.getTheatreLocation(), theatreObj.getTheatreCredit())) {

			TheatreUtility theatreUtilityObj = new TheatreUtility();

			Theatre theatreObj1 = theatreUtilityObj.createTheatre(theatreObj.getTheatreName(),
					theatreObj.getTheatreLocation(), theatreObj.getTheatreCredit());

			if (theatreObj1 != null) {
				ServerResponse.sendResponse(response, 200, "Theatre Created Successfully", data);
			}
		} else {
			throw new ApplicationException("Invalid Theatre details");
		}

	}


}