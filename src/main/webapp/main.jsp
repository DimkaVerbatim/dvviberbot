<%@ page contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@page import="java.sql.*, javax.sql.*, javax.naming.*"%>
<!DOCTYPE HTML>
<html lang="ukr">
<head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8" />
    <link rel="stylesheet" href="../css/bootstrap.min.css">
    <script src="../js/bootstrap.min.js"></script>
    <title>Dimkaverbatim viber bot</title>
</head>
<body>
<div class="container">
    <% if (session.getAttribute("loggedInUser") == null) { %>
    <% if(session!=null) {
            session.invalidate();
            response.sendRedirect("index.jsp");
        }%>
    <% } else {%>
    <h1>Viber Bot</h1>
    <h2>Senders</h2>
    <form action="/sender" method="get" id="seachSenderForm" role="form" >
        <input type="hidden" id="searchAction" name="searchAction" value="searchByName"/>
        <div style="display: -webkit-box" class="form-group col-xs-5">
            <input style="width: 80%;margin-right: 10px;" type="text" name="senderName" id="senderName" class="form-control" required="true"
                   placeholder="Type the Name of the sender"/>
            <button style="width: 100%" type="button" class="btn btn-info">
                <span class="glyphicon glyphicon-search"></span> Search
            </button>

        </div>

        <%
            DataSource ds = null;
            Connection conn = null;
            ResultSet result = null;
            Statement stmt = null;
            ResultSetMetaData rsmd = null;
            try{
                Context context = new InitialContext();
                Context envCtx = (Context) context.lookup("java:comp/env");
                ds =  (DataSource)envCtx.lookup("jdbc/dvviberbotdb");
                if (ds != null) {
                    conn = ds.getConnection();
                    stmt = conn.createStatement();
                    result = stmt.executeQuery("SELECT * FROM senders");
                }
            }
            catch (SQLException e) {
                System.out.println("Error occurred " + e);
            }
            int columns=0;
            try {
                rsmd = result.getMetaData();
                columns = rsmd.getColumnCount();
            }
            catch (SQLException e) {
                System.out.println("Error occurred " + e);
            }
        %>
        <table class="table table-striped">
            <thead>
            <tr>
                <% // write out the header cells containing the column labels
                    try {
                        for (int i=1; i<=columns; i++) {
                            out.print("<th>" + rsmd.getColumnLabel(i) + "</th>");
                        }
                %>
            </tr>
            </thead>
            <% // now write out one row for each entry in the database table
                while (result.next()) {
                    out.write("<tr>");
                    for (int i=1; i<=columns; i++) {
                        if (i==3 && result.getString(i) != null){
                            out.write("<td> <a href=\"" +result.getString(i)+ "\"> avatar </a> </td>");
                        }
                        else {
                            out.write("<td>" + result.getString(i) + "</td>");
                        }
                    }
                    out.write("</tr>");
                }

                // close the connection, resultset, and the statement
                result.close();
                stmt.close();
                conn.close();
            } // end of the try block
            catch (SQLException e) {
                System.out.println("Error " + e);
            }
            // ensure everything is closed
            finally {
                try {
                    if (stmt != null)
                        stmt.close();
                }  catch (SQLException e) {}
                try {
                    if (conn != null)
                        conn.close();
                } catch (SQLException e) {}
            }

            %>
        </table>
    </form>
    <% } %>

</div>
</body>
</html>
