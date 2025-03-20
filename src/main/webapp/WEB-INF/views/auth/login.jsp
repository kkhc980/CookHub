<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Login</title>
    <meta name="_csrf" content="${_csrf.token}"/>
    <meta name="_csrf_header" content="${_csrf.headerName}"/>
    <script src="https://code.jquery.com/jquery-3.7.1.js"></script>
</head>
<body>
<!-- 로그인 폼 -->
<form action="../auth/login" method="post">
    <input type="email" name="email"> <br>
    <input type="password" name="password"> <br>
    <input type="submit" value="로그인">
    <!-- CSRF 토큰 -->
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
    <button type="button" onclick="location.href='../member/signup'">회원가입</button>
    <br>
    <button type="button" onclick="location.href='../member/findpw'">비밀번호 찾기</button>
</form>

<script>
    $(document).ready(function () {
        const urlParams = new URLSearchParams(window.location.search);
        const authRequired = urlParams.get('error') === 'authRequired';
        const loginError = urlParams.get('error') === 'true';
        const expired = urlParams.get('error') === 'expired';
        const canceled = urlParams.get('error') === 'canceled';
        const restricted = urlParams.get('error') === 'restricted';

        // 회원가입 성공 시 알림
        const signupSuccess = '${signupSuccess}'; // RedirectAttributes에서 전달된 속성값 받기
        if (signupSuccess === 'true') {
            alert("인증 메일이 발송되었습니다. 이메일을 확인해주세요.");
        }

        // 인증 성공 시 알림 추가
        const authSuccess = '${authSuccess}';
        if (authSuccess === 'true') {
            alert("이메일 인증이 완료되었습니다.");
        }

        if (authRequired) {
            alert("인증이 필요합니다. 이메일 인증을 진행해주세요.");
        } else if (loginError) {
            alert("아이디 또는 비밀번호가 일치하지 않습니다.");
        } else if (canceled) {
            alert("회원가입이 취소되었습니다.");
        } else if (restricted) {
            alert("로그인이 제한되었습니다.");
        }else if (expired) {
            const res = confirm("인증이 만료되었습니다. 재인증 하시겠습니까?");
            if (res) {
                // 재인증 로직
                location.href = '../member/reAuth?email=' + '${email}'
            } else {
                location.href = '../member/deleteExpired?email=' + '${email}'
            }
        }
    });

</script>
</body>
</html>