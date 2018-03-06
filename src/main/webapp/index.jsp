<%--
  Created by IntelliJ IDEA.
  User: Dimka
  Date: 01.03.2018
  Time: 15:38
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE HTML>
<html lang="ukr">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta name="description" content="Viber bot">
    <meta name="author" content="Ternavskiy Dmytro">
    <link rel="icon" href="../images/appico.ico">

    <!-- Bootstrap core CSS -->
    <link href="../css/bootstrap.min.css" rel="stylesheet">

    <!-- Custom styles for this template -->
    <link href="../css/signin.css" rel="stylesheet">
    <title>Viber bot DimkaVerbatim</title>
</head>
<body class="text-center">
<% if (session.getAttribute("loggedInUser") != null) { %>
<% response.sendRedirect("main.jsp");%>
<% } %>
    <form class="form-signin" action="logincontroler" method="post">
        <img class="mb-4" src="../images/logo_without_text.png" alt="" width="100" height="100">
        <h1 type="header" class="h3 mb-3 font-weight-bold">Авторизуйтесь</h1>
        <%
            String login_msg=(String)request.getAttribute("errorMessage");
            if(login_msg!=null)
                out.println("<p class=\"text-danger\">" + request.getAttribute("errorMessage") + "</p>");
        %>
        <label for="inputEmail" class="sr-only">E-mail</label>
        <input type="email" name="inputEmail" id="inputEmail" class="form-control" placeholder="E-mail" required autofocus>
        <label for="inputPassword" class="sr-only">Пароль</label>
        <input type="password" name="inputPassword" id="inputPassword" class="form-control" placeholder="Пароль" required>
        <div class="checkbox mb-3">
            <label>
                <input type="checkbox" value="remember-me"> Запам'ятати
            </label>
        </div>
        <button class="btn btn-lg btn-primary btn-block" type="submit">Вхід</button>
        <p class="mt-5 mb-3 text-muted">&copy; 2018</p>
    </form>
</body>
</html>
