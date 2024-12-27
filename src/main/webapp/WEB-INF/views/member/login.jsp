<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
    <title>Login</title>
    <script src="https://code.jquery.com/jquery-3.7.1.js" />
</head>
<body>
<form action="/member/login" method="post">
    <input type="email" name="email"> <br>
    <input type="password" name="password"> <br>
    <input type="submit" value="로그인"> <br>
    <button onclick="">회원가입</button>
</form>
</body>
</html>
