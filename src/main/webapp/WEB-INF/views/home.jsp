<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
<head>
    <title>Home</title>
</head>
<body>
<h1>
    Hello world!
</h1>

<P> The time on the sever is ${serverTime}. </P>

<c:choose>
    <c:when test="${empty sessionScope.loginMember}">
        <div><a href="member/signup">회원가입 이동 테스트</a></div>
        <div><a href="auth/login">로그인 이동 테스트</a></div>
    </c:when>
    <c:otherwise>
        <div>안녕하세요, ${sessionScope.loginMember.memberName}님!</div> <!-- 사용자 이름 표시 -->
        <div><a href="auth/logout">로그아웃</a></div>  <!-- 로그아웃 링크 추가 -->
    </c:otherwise>
</c:choose>
<div>test kimheeseung</div>
<div>test kill</div>
</body>
</html>
