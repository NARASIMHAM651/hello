package studentlogin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;



import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.sql.*;

@WebServlet("/StudentloginServlet")
public class StudentloginServlet extends HttpServlet {

	    private static final long serialVersionUID = 1L;

	    protected void doPost(HttpServletRequest request, HttpServletResponse response)
	            throws ServletException, IOException {

	        String email = request.getParameter("email");
	        String password = request.getParameter("password");

	        try {
	            // Load JDBC Driver
	            Class.forName("com.mysql.cj.jdbc.Driver");

	            // Connect to DB (replace "your_password" with actual root password)
	            Connection conn = DriverManager.getConnection(
	                "jdbc:mysql://localhost:3306/zidio_connect", "root", "student");

	            // Step 1: Query users table
	            String userSql = "SELECT * FROM users WHERE email = ?";
	            PreparedStatement userStmt = conn.prepareStatement(userSql);
	            userStmt.setString(1, email);

	            ResultSet rs = userStmt.executeQuery();

	            if (rs.next()) {
	                String dbPassword = rs.getString("password");
	                String role = rs.getString("role");
	                int userId = rs.getInt("id");

	                if ("student".equals(role) && dbPassword.equals(password)) {

	                    // Step 2: Get student name from students table
	                    String studentSql = "SELECT full_name FROM students WHERE student_id = ?";
	                    PreparedStatement studentStmt = conn.prepareStatement(studentSql);
	                    studentStmt.setInt(1, userId);

	                    ResultSet studentRs = studentStmt.executeQuery();

	                    if (studentRs.next()) {
	                        String studentName = studentRs.getString("full_name");

	                        // Set session attributes
	                        HttpSession session = request.getSession();
	                        session.setAttribute("studentEmail", email);
	                        session.setAttribute("studentName", studentName);

	                        // Redirect to dashboard with name parameter
	                        response.sendRedirect("studentdashboard.html?name=" + studentName);

	                    } else {
	                        // No student data found
	                        response.sendRedirect("student_login.html?error=Student+data+not+found");
	                    }

	                } else {
	                    // User exists but not as student
	                    response.sendRedirect("student_login.html?error=Invalid+credentials");
	                }

	            } else {
	                // Email not found
	                response.sendRedirect("student_login.html?error=Invalid+credentials");
	            }

	        } catch (Exception e) {
	            e.printStackTrace();
	            response.sendRedirect("student_login.html?error=Server+error");
	        }
	    }
	}