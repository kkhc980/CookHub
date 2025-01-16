<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
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

        .navbar .logged-in-menu {
            display: flex; /* flexbox 적용 */
            align-items: center; /* 수직 가운데 정렬 (선택적) */
        }

        /* 로그아웃 버튼 스타일링 */
        .navbar .logged-in-menu form input[type="submit"] {
            background: none; /* 배경 제거 */
            border: none; /* 테두리 제거 */
            text-decoration: none; /* 밑줄 제거 */
            color: white; /* 글자색 */
            cursor: pointer; /* 마우스 커서 */
            padding: 0; /* 패딩 제거 */
            font-size: inherit; /* 부모 폰트 크기 상속 */
            margin-left: 10px;
        }

        .navbar .logged-in-menu form input[type="submit"]:hover {
            color: #ff9900; /* hover 시 글자색 */
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
    <div>
        <!-- 로그인 상태 -->
        <sec:authorize access="isAuthenticated()">
            <div class="logged-in-menu">
                <a href="${pageContext.request.contextPath}/member/detail"><sec:authentication property="principal.name" />님</a>
                <form action="../auth/logout" method="post">
                    <input type="submit" value="로그아웃">
                    <input type="hidden" name="${_csrf.parameterName }" value="${_csrf.token }">
                </form>
            </div>
        </sec:authorize>
        <!-- 로그아웃 상태 -->
        <sec:authorize access="isAnonymous()">
            <a href="../auth/login">로그인</a>
            <a href="../member/signup">회원가입</a>
        </sec:authorize>
    </div>
</div>

<!-- 페이지별 콘텐츠 -->
<div class="content">
    <jsp:include page="${pageContent}"/>
</div>
</body>
</html>
