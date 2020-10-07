import javax.servlet.http.*;
import javax.servlet.*;
import java.io.*;
import java.sql.*;
import java.util.*;

public class HitServlet extends HttpServlet {
	private int mCount;
	static int id = 0;
	static ArrayList<String> users;

	public void doGet(HttpServletRequest request,
	                  HttpServletResponse response)
			throws ServletException, IOException {
		// Set response content type


		response.setContentType("text/html");

		HttpSession session = request.getSession(false);
		if (session == null) {
			PrintWriter out = response.getWriter();
			out.println("<html>\n" +
					"<body>\n" +
					"<form action=\"/midp/hits\" method=\"POST\">\n" +
					"Username: <input type=\"text\" name=\"username\">\n" +
					"<br />\n" +
					"Password: <input type=\"text\" name=\"password\" />\n" +
					"<input type=\"submit\" value=\"Submit\" />\n"
					+
					"</form>\n</body>\n</html\n");
		} else {
			doPost(request, response);
		}

	}

	// Method to handle POST method request.
	public void doPost(HttpServletRequest request,
	                   HttpServletResponse response)
			throws ServletException, IOException {

		String errMsg = "Testing";
		// Set response content type
		try {
			try {
				Class.forName("oracle.jdbc.OracleDriver");
			} catch (Exception ex) {
			}
			Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "system", "root");
			errMsg += "Con";
			Statement stmt = con.createStatement();
			errMsg += "stmt";


			users = new ArrayList<String>();

			if (users.isEmpty()) {
				System.out.println("There is more than one item in users");
				ResultSet rs = stmt.executeQuery("select * from users");
				while (rs.next()) {
					users.add(rs.getString(2));
				}
				for (int i = 0; i < users.size(); i++) {
					System.out.println(users.get(i));
				}
			}


			String username = request.getParameter("username");
			System.out.println("my current username is " + username);

			boolean userExist = false;

			if (!users.isEmpty()) {
				for (int i = 0; i < users.size(); i++) {
					if (username.equals(users.get(i))) {
						userExist = true;
						i = users.size();
					}
				}
			} else {
				id = 0;
			}

			if (!userExist) {
				System.out.println("UserExist is truee dude");
				stmt.executeUpdate("INSERT INTO users (ID, UserId, Password) VALUES (" + id + ",'" + username + "','" + request.getParameter("password") + "')");
				users.add(username);
			}
			stmt.close();
			con.close();
			errMsg += "End";
		} catch (SQLException ex) {
			errMsg = errMsg + "\n--- SQLException caught ---\n";
			while (ex != null) {
				errMsg += "Message: " + ex.getMessage();
				errMsg += "SQLState: " + ex.getSQLState();
				errMsg += "ErrorCode: " + ex.getErrorCode();
				ex = ex.getNextException();
				errMsg += "";
			}
		}
		PrintWriter out = response.getWriter();

		String title = "Using Post Method to Read Form Data";


		HttpSession session = request.getSession(false);


		if (session == null) {
			session = request.getSession(true);
			session.setAttribute("username", request.getParameter("username"));
			session.setAttribute("password", request.getParameter("password"));


		} else {
			session = request.getSession(true);

		}

		title = (String) session.getAttribute("username") + ". id: " + session.getId();

		response.setContentType("text/html");

		String docType =
				"<!doctype html public \"-//w3c//dtd html 4.0 " +
						"transitional//en\">\n";
		out.println(docType +
				"<html>\n" +
				"<head><title>" + title + "</title></head>\n" +
				"<body bgcolor=\"#f0f0f0\">\n" +
				"<h1 align=\"center\">" + title + "</h1>\n" +
				"<ul>\n" +
				"  <li><b>Username</b>: "
				+ session.getAttribute("username") + "\n" +
				"  <li><b>Password</b>: "
				+ session.getAttribute("password") + "\n" +


				"<li><form action=\"/midp/login\" method=\"GET\">\n" +
				"<input type=\"submit\" value=\"Log Out\" />\n"
				+
				"</form>" +

				"</ul>\n" +
				"</body></html>");

	}

}
