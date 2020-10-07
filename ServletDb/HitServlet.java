import javax.servlet.http.*;
import javax.servlet.*;
import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


import com.oreilly.servlet.MultipartRequest;


public class HitServlet extends HttpServlet {
    private int mCount;

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
            throws ServletException, IOException {
        // Set response content type

        HttpSession session = request.getSession(true);
        if (request.getParameterMap().containsKey("userid")) {

            session.setAttribute("userid", request.getParameter("userid"));
            session.setAttribute("count", 0);

        }
        String userid = (String) session.getAttribute("userid");


        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();

        out.println("<html>\n" +
                "<body>\n" +
                "<br />\n" +
                "<form action=\"/midp/hits\" method=\"POST\" enctype=\"multipart/form-data\">\n" +
                "User id: <input type=\"text\" name=\"userid\">\n" +
                "Password: <input type=\"text\" name=\"password\" />\n" +
                "Caption: <input type=\"text\" name=\"caption\" />\n" +
                " Date : <input type=\"date\" id=\"date\" name=\"date\" >\n" +
                "<br />\n" +

                "Photo : <input type=\"file\" name=\"photo\" size=\"50\">\n" +
                "<input type=\"submit\" value=\"Submit\" />\n"
                +
                "</form>\n</body>\n</html\n");


    }


    //Method to handle POST method request.
    public void doPost(HttpServletRequest request,
                       HttpServletResponse response)
            throws ServletException, IOException {
        MultipartRequest multipartRequest = new MultipartRequest(request, "C:\\tomcat\\webapps\\midp\\images");
        String photoPath = multipartRequest.getFile("photo").toString();
        String fileName = multipartRequest.getOriginalFileName("photo");
        String userId = multipartRequest.getParameter("userid");
        String password = multipartRequest.getParameter("password");
        String captions = multipartRequest.getParameter("caption");
//        String date = multipartRequest.getParameter("date");
//        SimpleDateFormat formatter = new SimpleDateFormat("dd-mmm-yy");
//        String dates = formatter.format(date);
//        Long datess = Long.parseLong(dates);
//        System.out.print(datess);

        String errMsg = "Testing";
        // Set response content type
        try {
            FileInputStream fis;
            try {
                Class.forName("oracle.jdbc.OracleDriver");
            } catch (Exception ex) {
            }
            Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "system", "dydwn4534");

            con.setAutoCommit(false);
            //using Transactions
            PreparedStatement preparedStatement = con.prepareStatement("INSERT INTO users (userid, password) VALUES (?,?)");

            PreparedStatement preparedStatement1 = con.prepareStatement("INSERT INTO photos (caption, datetaken, picture, filename) VALUES (?,?,?,?)");

            preparedStatement.setString(1, userId);
            preparedStatement.setString(2, password);
            int row = preparedStatement.executeUpdate();

            preparedStatement1.setString(1, captions);

            java.util.Date datesss = new java.util.Date();
            long t = datesss.getTime();
            java.sql.Date sqlDate = new java.sql.Date(t);

            preparedStatement1.setDate(2, sqlDate);
            File image = new File(String.valueOf(photoPath));
            fis = new FileInputStream(image);
            preparedStatement1.setBinaryStream(3, (InputStream) fis, (int) (image.length()));
            preparedStatement1.setString(4, fileName);
            row = preparedStatement1.executeUpdate();


            if (row > 0) {
                System.out.println("File uploaded and saved into database");
            }

            con.commit();

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
        response.setContentType("text/html");
        HttpSession sessions = request.getSession();
//        out.println("<p>'"+ datess+ " <p/>");
        String docType =
                "<!doctype html public \"-//w3c//dtd html 4.0 " +
                        "transitional//en\">\n";
        out.println(docType +
                "<html>\n" +
                "<body bgcolor=\"#f0f0f0\">\n");
        out.println("<p align=\"right\">\n" + "<b>User Id is : </b> " + userId + "</p>\n");

        out.println("<img  src='" + "./images/" + photoPath.substring(photoPath.lastIndexOf("\\") + 1) + "' alt='NO Image' height='100' width='100' />" + "\n" +

                "<button class='button'  id='prev'>Prev</button>\n" +
                "<button class='button' id='next'>Next</button>\n" +
                "<br />\n");


        List<String> allImageCaption = getAllImages();

        for (String s : allImageCaption) {

            out.println("<img src='"+"./images/" +s+ "'+ height='100' width='100' />");
            System.out.println("./images"+s);
        }
        out.println("</body></html>");
    }



    public static List<String> getAllImages() {

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;


        List<String> images = new ArrayList<String>();

        // 담아주는 코드
        try {

            conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "system", "dydwn4534");


            String query = " SELECT filename FROM photos ";
            stmt = conn.prepareStatement(query);


            rs = stmt.executeQuery();

            while (rs.next()) {

                String imagesCaption = rs.getString("filename");
                System.out.println(imagesCaption);
                images.add(imagesCaption);

            }


        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                }
            }
        }

        return images;
    }


    public static void main(String[] agr) {
        getAllImages();

    }

}
