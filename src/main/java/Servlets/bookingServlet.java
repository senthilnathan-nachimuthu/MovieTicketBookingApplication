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

import Exceptions.ApplicationException;
import Schemas.Booking;
import Schemas.Users;
import Servlets.JsonUtility.JsonUtil;
import Servlets.Service.BookingService;
import Servlets.Service.DTO.BookingDTO;
import Servlets.SessionUtil.SessionValidation;
import Servlets.Validators.bookingValidators;
import Servlets.Web.ServerResponse;
import Utilities.BookingUtility;

@WebServlet({ "/bookingServlet", "/bookNow", "/myBookings", "/cancelBooking" })
public class bookingServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String path = request.getServletPath();
		if (path.equals("/myBookings")) {
			handleGetUserBookings(request, response);
		}

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String path = request.getServletPath();
		if (path.equals("/bookNow")) {
			handleBookingPostRequest(request, response);
		} else if (path.equals("/cancelBooking")) {
			handleCancelBookingPostRequest(request, response);
		}
	}

	private void handleBookingPostRequest(HttpServletRequest request, HttpServletResponse response)
			throws JsonSyntaxException, JsonIOException, IOException {

		JsonObject data = JsonUtil.getJsonObject(request);
		BookingDTO bookingDtoObj = BookingService.constructBookingDTOObject(data);

		Users userObj = SessionValidation.verifyAndGetUser(request, response);
		if (userObj != null) {
			if (bookingDtoObj != null) {
				if (bookingValidators.validateBookingData(bookingDtoObj)) {
					BookingUtility bookingUtil = new BookingUtility();
					double credits = bookingUtil.startBookingProcess(userObj, bookingDtoObj.getShowId(),
							bookingDtoObj.getSeatIds(), bookingDtoObj.getTotalAmount());
					
					System.out.println("Credits Received at Servlet After Inserting Booking" + credits);
					if (credits != -1) {
						ServerResponse.sendResponse(response, 200, "Booking SuccessFull", credits);
					}
				}
			}

		} else {
			throw new ApplicationException("Invalid User,Account Not found");
		}

	}

	private void handleGetUserBookings(HttpServletRequest request, HttpServletResponse response) throws IOException {

		Users userObj = SessionValidation.verifyAndGetUser(request, response);
		if (userObj != null) {
			BookingUtility bookingUtil = new BookingUtility();
			List<Booking> bookingList = bookingUtil.getUserBooking(userObj);
			if (!bookingList.isEmpty()) {
				String json = BookingService.ConstructJsonBookingString(bookingList);
				ServerResponse.sendResponse(response, 200, "User Bookings Fetched Successfully", json);
			}
		}
	}

	private void handleCancelBookingPostRequest(HttpServletRequest request, HttpServletResponse response)
			throws JsonSyntaxException, JsonIOException, IOException {

		JsonObject data = JsonUtil.getJsonObject(request);
		Long bookingId = JsonUtil.getJsonLong("bookingId", data);
		if (bookingId != null && bookingId != -1) {
			BookingUtility booking = new BookingUtility();
			if (booking.cancelBooking(bookingId)) {
				ServerResponse.sendResponse(response, 200, "Booking Cancelled Successfully, Refund Completed.", data);
			}
		}

	}

}
