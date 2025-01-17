<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Login</title>
    <meta name="_csrf" content="${_csrf.token}"/>
    <meta name="_csrf_header" content="${_csrf.headerName}"/>
    <script src="https://code.jquery.com/jquery-3.7.1.js"></script>
    <style>
        .error-message {
            color: red;
            text-align: center;
            margin-bottom: 10px;
        }
    </style>
</head>
<body>
<div class="error-message" id="auth-error" style="display:none;">
    인증이 필요합니다. 이메일 인증을 진행해주세요.
</div>
<div class="error-message" id="login-error" style="display:none;">
    아이디 또는 비밀번호가 일치하지 않습니다.
</div>
<div class="error-message" id="withdrawed-error" style="display:none;">
    탈퇴된 회원입니다.
</div>
<!-- 로그인 폼 -->
<form action="../auth/login" method="post">
    <input type="email" name="email"> <br>
    <input type="password" name="password"> <br>
    <input type="submit" value="로그인">
    <!-- CSRF 토큰 -->
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
    <button type="button" onclick="location.href='../member/signup'">회원가입</button>
</form>

<script>
    $(document).ready(function () {
        const urlParams = new URLSearchParams(window.location.search);
        const authRequired = urlParams.get('error') === 'authRequired';
        const loginError = urlParams.get('error') === 'true';
        const withdrawed = urlParams.get('error') === 'withdrawed';

        if (authRequired) {
            $('#auth-error').show();
        } else if (loginError) {
            $('#login-error').show();
        } else if (withdrawed) {
            $('#withdrawed-error').show();
        }
    });
</script>
</body>
</html>