<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>CookHub - 관리자 페이지</title>
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
        /* 추가 스타일 (선택 사항) */
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
        }
    </style>
</head>
<body>
    <!-- 관리자 전용 네비게이션 바 -->
    <div class="navbar">
        <div>
            <a href="${pageContext.request.contextPath}/admin/dashboard">대시보드</a>
            <a href="${pageContext.request.contextPath}/admin/members/list">회원 관리</a>
            <a href="${pageContext.request.contextPath}/admin/recipeboard">레시피 관리</a>
            <a href="${pageContext.request.contextPath}/admin/recipeboard/register">레시피 등록</a>
            <a href="${pageContext.request.contextPath}/admin/statistics">통계</a>
            <!-- 필요한 다른 관리 메뉴 추가 -->
        </div>
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