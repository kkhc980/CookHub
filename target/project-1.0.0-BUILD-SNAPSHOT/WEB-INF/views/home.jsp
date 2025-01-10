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

<c:if test="${empty sessionScope.loginMember}">
    <div><a href="member/signup">회원가입 이동 테스트</a></div>
    <div><a href="member/login">로그인 이동 테스트</a></div>
</c:if>
<div>test kimheeseung</div>
<div>test kill</div>
</body>
</html>
