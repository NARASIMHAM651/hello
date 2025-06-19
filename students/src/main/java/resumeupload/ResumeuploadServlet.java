package resumeupload;

import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.sql.*;




import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.sql.*;

@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 1, // 1MB
    maxFileSize = 1024 * 1024 * 10,     // 10MB
    maxRequestSize = 1024 * 1024 * 100   // 100MB
)
@WebServlet("/student-resume-upload")
public class ResumeuploadServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        String studentEmail = (String) session.getAttribute("studentEmail");

        int studentId = -1;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/zidio_connect", "root", "student");

            PreparedStatement userStmt = conn.prepareStatement("SELECT id FROM users WHERE email = ?");
            userStmt.setString(1, studentEmail);
            ResultSet rs = userStmt.executeQuery();

            if (!rs.next()) {
                response.sendRedirect("student_login.html");
                return;
            }

            studentId = rs.getInt("id");

            Part filePart = request.getPart("resume");
            String fileName = extractFileName(filePart, studentId);
            String uploadPath = getServletContext().getRealPath("") + File.separator + "uploads" + File.separator + "resumes";

            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            String filePath = uploadPath + File.separator + fileName;
            filePart.write(filePath);

            String dbPath = "/students/uploads/resumes/" + fileName;

            String sql = "UPDATE students SET resume_path = ? WHERE student_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, dbPath);
            stmt.setInt(2, studentId);
            stmt.executeUpdate();

            // Send back success response
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            out.print("{\"resume_path\":\"" + dbPath + "\"}");
            out.flush();

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Resume upload failed");
        }
    }

	private String extractFileName(Part filePart, int studentId) {
		// TODO Auto-generated method stub
		return null;
	}
}