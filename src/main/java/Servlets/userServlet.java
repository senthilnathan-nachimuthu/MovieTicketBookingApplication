package Servlets;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import Schemas.Users;
import Servlets.JsonUtility.JsonUtil;
import Servlets.Validators.UserDataValidation;
import Servlets.Web.ServerResponse;
import Utilities.userUtilities;

@WebServlet({ "/login", "/signup", "/userHome", "/adminHome", "/logout" })
public class userServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String path = request.getServletPath();
		System.out.println("Login Servlet Reached");
		System.out.println(request.getUserPrincipal());

		if (path.equals("/") || path.equals("/login")) {
			RequestDispatcher rd = request.getRequestDispatcher("Login.jsp");
			rd.forward(request, response);
		} else if (path.equals("/signup")) {
			request.getRequestDispatcher("Signup.jsp").forward(request, response);
		} else if (path.equals("/userHome")) {

			HttpSession session = request.getSession(false);
			if (session != null && session.getAttribute("username") != null) {
				request.getRequestDispatcher("userHome.jsp").forward(request, response);
			} else {
				response.sendRedirect("login");
			}
		} else if (path.equals("/adminHome")) {
			HttpSession session = request.getSession(false);
			if (session != null && session.getAttribute("username") != null) {
				request.getRequestDispatcher("adminHome.jsp").forward(request, response);
			} else {
				response.sendRedirect("login");
			}
		} else if (path.equals("/logout")) {

			request.getSession().invalidate();
			response.sendRedirect("login");
		} else {

			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String path = request.getServletPath();

		if (path != null) {
			if (path.equals("/login")) {
				handleLoginPostRequest(request, response);
			} else if (path.equals("/signup")) {
				handleSignUpPostRequest(request, response);
			} else {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
			}
		}
	}

	private void handleSignUpPostRequest(HttpServletRequest request, HttpServletResponse response)
			throws JsonSyntaxException, JsonIOException, IOException {

		JsonObject data = JsonUtil.getJsonObject(request);
		String name = JsonUtil.getJsonString("name", data);
		int age = JsonUtil.getJsonInt("age", data);
		String gender = JsonUtil.getJsonString("gender", data);
		String username = JsonUtil.getJsonString("username", data);
		String password = JsonUtil.getJsonString("password", data);

		String usernameValidation = UserDataValidation.validateUsernamePassword(username, password);
		String userDetailValidation = UserDataValidation.validateUserDetails(name, age, gender);

		if (usernameValidation.equals("VALID")) {
			if (userDetailValidation.equals("VALID")) {

				userUtilities userUtilObj = new userUtilities();
				HttpSession session = request.getSession();

				Users userObj = userUtilObj.doSignUp(name, gender, age, username, password);
				if (userObj != null) {
					session.setAttribute("username", username);
					session.setAttribute("isAdmin", userObj.isadmin);
					session.setAttribute("userId", userObj.userId);
					ServerResponse.sendResponse(response, HttpServletResponse.SC_OK, "Success", userObj);
				} else {
					String error = userUtilObj.getError();
					ServerResponse.sendResponse(response, 404, error, null);
				}
			} else {
				ServerResponse.sendResponse(response, 404, userDetailValidation, null);

			}
		} else {
			ServerResponse.sendResponse(response, 404, usernameValidation, null);
		}

	}

	private void handleLoginPostRequest(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		System.out.println("Login Reached");
		HttpSession session = request.getSession(false);

		JsonObject data = JsonUtil.getJsonObject(request);
		String username = JsonUtil.getJsonString("username", data);
		String password = JsonUtil.getJsonString("password", data);
		userUtilities utilityObj = new userUtilities();
		String validation = UserDataValidation.validateUsernamePassword(username, password);

		if (validation.equals("VALID")) {

			Users userObj = utilityObj.doLogin(username, password);
			if (userObj != null) {
				session.setAttribute("username", username);
				session.setAttribute("isAdmin", userObj.isadmin);
				session.setAttribute("userId", userObj.userId);
				ServerResponse.sendResponse(response, HttpServletResponse.SC_OK, "Success", userObj);

			} else {
				String error = utilityObj.getError();
				ServerResponse.sendResponse(response, 404, error, null);
			}
		} else {
			ServerResponse.sendResponse(response, 404, validation, null);
		}

	}

}