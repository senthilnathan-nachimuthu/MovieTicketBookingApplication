package Servlets.Filters;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import Servlets.Web.ServerResponse;

@WebFilter("/*")
public class AdminAuthenticationFilter extends BaseFilter {

	private static final long serialVersionUID = 1L;
	private final List<String> admin_routes = Arrays.asList("/addMovie", "/deleteMovie", "/updateMovie", "/addTheatre",
			"/deleteTheatre", "/updateTheatre", "/addScreen", "/deleteScreen", "/updateScreen", "/createShow",
			"/deleteShows", "/updateShowPrice", "/updateShowMovie");

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

//		HttpServletRequest req = (HttpServletRequest) request;
//		HttpServletResponse res = (HttpServletResponse) response;
//		String path = req.getServletPath();
//		if (!admin_routes.contains(path)) {
//			chain.doFilter(request, response);
//			return;
//		}
//		
//		if (!isAdmin(req)) {
//			if (isAjaxRequest(req)) {
//				ServerResponse.sendUnauthorizedResponse(res, HttpServletResponse.SC_UNAUTHORIZED, "Admin Access only");
//				return;
//			} else {
//				res.sendRedirect(req.getContextPath() + "/userHome");
//				return;
//			}
//		}

		chain.doFilter(request, response);
	}

	public void init(FilterConfig fConfig) throws ServletException {

	}

}
