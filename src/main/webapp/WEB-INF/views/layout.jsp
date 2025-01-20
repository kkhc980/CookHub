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
            align-items: center; /* 수직 가운데 정렬 */
            background-color: #333;
            padding: 10px;
            color: white;
        }
        .navbar .left-menu { /* 왼쪽 메뉴 그룹 */
            display: flex;
        }

        .navbar .center-logo { /* 가운데 로고 스타일 */
            font-size: 24px;
            font-weight: bold;
            color: #ff9900;
            flex-grow: 1; /* 남은 공간 모두 차지 */
            text-align: center;
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
            display: flex;
            align-items: center;
        }

        /* 드롭다운 메뉴 스타일링 */
        .dropdown {
            position: relative;
            display: inline-block;
            /* 드롭다운 메뉴가 이름 텍스트 시작점에 맞춰지도록 추가 */
            text-align: left;
        }

        .dropdown-content {
            display: none;
            position: absolute;
            background-color: #f9f9f9;
            min-width: 160px;
            box-shadow: 0px 8px 16px 0px rgba(0,0,0,0.2);
            z-index: 1;
            margin-top: 5px;
            left: 0;
            max-height: 300px; /* 최대 높이 설정 */
            overflow-y: auto;   /* 세로 스크롤 추가 */
            white-space: nowrap; /* 내용이 길어질 경우 한 줄로 표시 */
        }

        .dropdown-content a,
        .dropdown-content form {
            color: black;
            padding: 12px 16px;
            text-decoration: none;
            display: block;
            text-align: center;
        }

        .dropdown-content form input[type="submit"] {
            background: none;
            border: none;
            text-decoration: none;
            color: black;
            cursor: pointer;
            padding: 0;
            font-size: inherit;
        }

        .dropdown-content a:hover,
        .dropdown-content form input[type="submit"]:hover {
            background-color: #f1f1f1;
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
    <div class="left-menu">
        <a>공지</a>
        <a href="${pageContext.request.contextPath}/recipeboard/list">분류</a>
        <a href="${pageContext.request.contextPath}/rankingboard/ranklist">랭킹</a>
    </div>
    <span class="center-logo">COOKHUB</span>
    <div>
        <!-- 로그인 상태 -->
        <sec:authorize access="isAuthenticated()">
            <div class="logged-in-menu">
                <div class="dropdown">
                    <a href="#" onclick="toggleDropdown(event)">
                        <sec:authentication property="principal.name" />님
                    </a>
                    <div class="dropdown-content" id="userDropdown">
                        <a href="${pageContext.request.contextPath}/member/detail">내 정보</a>
                        <form action="../auth/logout" method="post">
                            <input type="submit" value="로그아웃">
                            <input type="hidden" name="${_csrf.parameterName }" value="${_csrf.token }">
                        </form>
                    </div>
                </div>
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
<script>
    function toggleDropdown(event) {
        event.preventDefault();
        var dropdown = document.getElementById('userDropdown');
        if (dropdown.style.display === 'none' || dropdown.style.display === '') {
            dropdown.style.display = 'block';
            adjustDropdownPosition(dropdown);
        } else {
            dropdown.style.display = 'none';
        }
    }

    function adjustDropdownPosition(dropdown) {
        var rect = dropdown.getBoundingClientRect();
        var windowWidth = window.innerWidth || document.documentElement.clientWidth;

        // 드롭다운의 오른쪽 끝이 화면을 넘어가는지 확인
        if (rect.right > windowWidth) {
            // 넘어가면 드롭다운의 오른쪽 끝을 화면 오른쪽 끝에 맞춤
            dropdown.style.left = 'auto';
            dropdown.style.right = '0';

        } else {
            // 넘어가지 않으면 기존 위치 유지 (왼쪽 정렬)
            dropdown.style.left = '0';
            dropdown.style.right = 'auto';
        }
    }

    window.onclick = function(event) {
        if (!event.target.matches('.dropdown a')) {
            var dropdowns = document.getElementsByClassName("dropdown-content");
            for (var i = 0; i < dropdowns.length; i++) {
                var openDropdown = dropdowns[i];
                if (openDropdown.style.display === 'block') {
                    openDropdown.style.display = 'none';
                }
            }
        }
    };
</script>
</body>
</html>