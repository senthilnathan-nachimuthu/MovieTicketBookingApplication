package Servlets.SessionUtil;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import Schemas.Users;
import Utilities.userUtilities;

public class SessionValidation {
	public static String isLoggedIn(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession(false);
		String username = (String) session.getAttribute("username");
		if (username == null || username.isEmpty()) {
			// response.sendRedirect("Login.jsp");
			return null;
		}
		return username;
	}

	public static boolean isAdmin(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		boolean isAdmin = (boolean) session.getAttribute("isAdmin");
		if (isAdmin == false) {
			return false;
		}
		return true;
	}

	public static Users verifyAndGetUser(HttpServletRequest request, HttpServletResponse response) throws IOException {

		String username = isLoggedIn(request, response);
		HttpSession session = request.getSession(false);
		Long userId = (long) session.getAttribute("userId");
		if (username != null && userId != null) {
			Users user = userUtilities.getUser(userId);
			if (user.getusername().equals(username)) {
				return user;
			}
		}
		return null;
	}
}