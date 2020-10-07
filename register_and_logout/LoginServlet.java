import javax.servlet.http.*;
import javax.servlet.*;
import java.io.*;
import java.sql.*;
import java.io.*;

public class LoginServlet extends HttpServlet {
	private int mCount;
	private static final String VALID_REDIRECT = "localhost:8081/midp/hits";

	public void doGet(HttpServletRequest request,
	                  HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession(false);
		HitServlet hitServlet = new HitServlet();

		if (session == null) {
			System.out.println("Session is null, no one is logged in, but nothing happens");
			hitServlet.doGet(request, response);
//			response.setHeader(VALID_REDIRECT, "\\");
		} else {
			session = request.getSession(true);
			session.invalidate();
//			response.setHeader("Refresh", "0; URL=" + VALID_REDIRECT);

			hitServlet.doGet(request, response);

		}


	}

	// Method to handle POST method request.
	public void doPost(HttpServletRequest request,
	                   HttpServletResponse response)
			throws ServletException, IOException {


	}
}


