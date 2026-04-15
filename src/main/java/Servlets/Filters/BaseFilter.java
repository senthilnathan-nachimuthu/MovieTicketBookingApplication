package Servlets.Filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Servlet Filter implementation class BaseFilter
 */

public class BaseFilter extends HttpFilter implements Filter {

	/**
	 * @see HttpFilter#HttpFilter()
	 */
	public BaseFilter() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	protected boolean isStaticResource(String path) {
		return path.endsWith(".js") || path.endsWith(".css") || path.endsWith(".html") || path.endsWith(".jpg")
				|| path.endsWith(".jpeg") || path.endsWith(".png") || path.endsWith(".ico") || path.endsWith(".svg")
				|| path.endsWith(".woff") || path.endsWith(".ttf");
	}

	protected boolean isAjaxRequest(HttpServletRequest request) {
		String requestType = request.getHeader("X-Requested-With");
		return "XMLHttpRequest".equals(requestType);
	}

	protected boolean isAdmin(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null)
			return false;
		Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
		return isAdmin != null && isAdmin;
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		chain.doFilter(request, response);
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}
