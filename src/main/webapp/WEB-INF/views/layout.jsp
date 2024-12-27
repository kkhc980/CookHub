<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>CookHub</title>
    <style>
        .navbar {
            display: flex;
            justify-content: space-between;
            background-color: #333;
            padding: 10px;
            color: white;
        }
        .navbar a {
            color: white;
            text-decoration: none;
            margin: 0 10px;
        }
        .navbar a:hover {
            color: #ff9900;
        }
        .content {
            padding: 20px;
        }
    </style>
</head>
<body>
    <!-- 공통 네비게이션 바 -->
    <div class="navbar">
        <div>
			<a href="${pageContext.request.contextPath}/noticeboard/list">공지</a>
			<a href="${pageContext.request.contextPath}/recipeboard/list">분류</a>
			<a href="${pageContext.request.contextPath}/rankingboard/list">랭킹</a>
        </div>
    </div>

    <!-- 페이지별 콘텐츠 -->
    <div class="content">
        <jsp:include page="${pageContent}" />
    </div>
</body>
</html>
