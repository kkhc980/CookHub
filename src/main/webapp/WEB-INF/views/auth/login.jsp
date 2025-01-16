<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<html>
<head>
    <title>Login</title>
    <meta name="_csrf" content="${_csrf.token}"/>
    <meta name="_csrf_header" content="${_csrf.headerName}"/>
    <script src="https://code.jquery.com/jquery-3.7.1.js"></script>
</head>
<body>
<!-- 에러 메시지 출력 -->
<h2>${errorMsg }</h2>

<!-- 로그아웃 메시지 출력 -->
<h2>${logoutMsg }</h2>

<!-- 로그인 폼 -->
<form action="../auth/login" method="post">
    <input type="email" name="email"> <br>
    <input type="password" name="password"> <br>
    <input type="submit" value="로그인">
    <!-- CSRF 토큰 -->
    <input type="hidden" name="${_csrf.parameterName }" value="${_csrf.token }">
    <button type="button" onclick="location.href='../member/signup'">회원가입</button>
</form>
</form>
</body>
</html>
