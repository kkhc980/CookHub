<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<html>
<head>
    <title>Login</title>
    <script src="https://code.jquery.com/jquery-3.7.1.js" ></script>
</head>
<body>
<form action="../member/login" method="post">
    <input type="email" name="email"> <br>
    <input type="password" name="password"> <br>
    <input type="submit" value="로그인">
    <button type="button" onclick="location.href='../member/signup'">회원가입</button>
</form>
</form>
</body>
</html>
