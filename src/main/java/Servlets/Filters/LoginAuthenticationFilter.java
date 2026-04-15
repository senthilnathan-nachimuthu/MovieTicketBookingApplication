package Servlets.Filters;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import Servlets.Web.ServerResponse;

@WebFilter("/*")
public class LoginAuthenticationFilter extends BaseFilter {

	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpFilter#HttpFilter()
	 */
	public LoginAuthenticationFilter() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-genenrated method stub
	}

	private final List<String> public_routes = Arrays.asList("/login", "/signup");

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;

		String path = req.getServletPath();
		if (isStaticResource(path)) {
			chain.doFilter(req, res);
			return;
		}
		if (public_routes.contains(path)) {
			chain.doFilter(request, response);
			return;
		}
//		HttpSession session = req.getSession(false);
//		boolean isLoggedIn = session != null && session.getAttribute("username") != null;
		boolean isLoggedIn=req.getUserPrincipal() != null;
		if (!isLoggedIn) {
			boolean isAjaxRequest = isAjaxRequest(req);
			if (isAjaxRequest) {
				ServerResponse.sendUnauthorizedResponse(res, HttpServletResponse.SC_UNAUTHORIZED,
						"Login to access the content");
				return;
			} else {
				res.sendRedirect(req.getContextPath() + "/login");
				return;
			}
		}
		System.out.println("Login Filter Reached ->> by "+req.getUserPrincipal().getName());

		chain.doFilter(request, response);
	}

	

}
