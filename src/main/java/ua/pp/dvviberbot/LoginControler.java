package ua.pp.dvviberbot;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginControler extends HttpServlet {
    @Override
    public void doGet (HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        /*
        File file = new File("C:/temp/testN.pdf");
        OutputStream outSteam = new FileOutputStream(file);
        InvoiceFacade facade = new InvoiceFacade();
        AbonentMonths invoice = new AbonentMonths();
        facade.writeAsPdf(invoice,outSteam);
        outSteam.close();*/

        response.setContentType("text/html;charset=UTF-8");
        try(PrintWriter out = response.getWriter()){
            String SQL = "Select * from siteusers where email=? and paswd=?";

            String userName = request.getParameter("inputEmail");
            String userPwd = request.getParameter("inputPassword");

            try {
                DbConection dbConection = new DbConection();
                PreparedStatement pstmt = dbConection.getConnection().prepareStatement(SQL);
                pstmt.setString(1,userName);
                pstmt.setString(2,userPwd);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()){
                    request.getSession().setAttribute("loggedInUser", userName);
                    response.sendRedirect("main.jsp");
                }
                else {
                    request.setAttribute("errorMessage", "Невірний логін або пароль");
                    request.getRequestDispatcher("index.jsp").forward(request, response);
                }

            }
            catch (Exception ex){
                out.println("Error: " + ex.getMessage());
            }
        }

    }
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request,response);

    }
}
