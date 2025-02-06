<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html>
<head>
    <title>CookHub</title>
    <script src="https://code.jquery.com/jquery-3.6.4.min.js"></script>
    <script src="https://code.jquery.com/ui/1.13.2/jquery-ui.min.js"></script>
    <style>
        .navbar {
            display: flex;
            justify-content: space-between;
            align-items: center;
            background-color: #333;
            padding: 10px 20px;
            color: white;
        }

        .navbar .left-menu {
            display: flex;
            gap: 20px;
        }

        .navbar .center-logo {
            font-size: 24px;
            font-weight: bold;
            color: #ff9900;
        }

        .navbar a {
            color: white;
            text-decoration: none;
        }

        .navbar a:hover {
            color: #ff9900;
        }

        .search-container {
            display: flex;
            justify-content: center;
            align-items: center;
            padding: 20px;
            background-color: #f9f9f9;
            box-shadow: 0px 2px 5px rgba(0, 0, 0, 0.1);
        }

        .search-container input {
            width: 300px;
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 5px;
            font-size: 14px;
        }

        .search-container button {
            padding: 10px 20px;
            background-color: #ff9900;
            border: none;
            color: white;
            border-radius: 5px;
            cursor: pointer;
            margin-left: 10px;
        }

        .search-container button:hover {
            background-color: #e68a00;
        }

        .register-text-button {
            padding: 10px 15px;
            font-size: 14px;
            font-weight: bold;
            color: white;
            background-color: #ff9900;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            transition: background-color 0.2s;
        }

        .register-text-button:hover {
            background-color: #e68a00;
        }

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
            box-shadow: 0px 8px 16px 0px rgba(0, 0, 0, 0.2);
            z-index: 1;
            margin-top: 5px;
            left: 0;
            max-height: 300px; /* 최대 높이 설정 */
            overflow-y: auto; /* 세로 스크롤 추가 */
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
<!-- 네비게이션 바 -->
<div class="navbar">
    <div class="left-menu">
        <a href="${pageContext.request.contextPath}/noticeboard/list">공지</a>
        <a href="${pageContext.request.contextPath}/recipeboard/list">분류</a>
        <a href="${pageContext.request.contextPath}/rankingboard/ranklist">랭킹</a>
    </div>
    <div class="center-logo">
        <a href="${pageContext.request.contextPath}/recipeboard/list" style="text-decoration: none; color: #ff9900;">CookHub</a>
    </div>

    <div>
        <sec:authorize access="isAuthenticated()">
            <div class="logged-in-menu">
                <div class="dropdown">
                    <a href="#" onclick="toggleDropdown(event)">
                        <sec:authentication property="principal.name"/>님
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
        <sec:authorize access="isAnonymous()">
            <a href="../auth/login">로그인</a>
            <a href="../member/signup">회원가입</a>
        </sec:authorize>
    </div>
</div>

<!-- 해시태그 검색 -->
<div class="search-container">
    <form method="GET" action="${pageContext.request.contextPath}/recipeboard/list" id="searchForm">
        <input
                type="text"
                name="hashtag"
                value="${param.hashtag}"
                placeholder="Search..."
                class="search-input">
        <input type="hidden" name="pageNum" value="1"> <!-- 검색 시 항상 첫 페이지로 이동 -->
        <button type="submit" class="search-button">🔍 Search</button>
    </form>

    <!-- 등록 버튼 -->
    <sec:authorize access="isAuthenticated()">
        <sec:csrfInput/>
        <a href="${pageContext.request.contextPath}/recipeboard/register" class="register-text-button">등록</a>
    </sec:authorize>

    <sec:authorize access="isAnonymous()">
        <button
                type="button"
                class="register-text-button"
                onclick="redirectToLogin()">
            등록
        </button>
    </sec:authorize>
</div>


<!-- 페이지별 콘텐츠 -->
<div class="content">
    <jsp:include page="${pageContent}"/>
</div>

<script>
    $(document).ready(function () {
        $(".search-input").autocomplete({
            source: function (request, response) {
                $.ajax({
                    url: "${pageContext.request.contextPath}/autocomplete",
                    type: "GET",
                    data: {
                        q: request.term // 입력된 검색어를 전송
                    },
                    success: function (data) {
                        response(data); // 결과를 autocomplete에 전달
                    },
                    error: function (xhr) {
                        console.error("Error fetching autocomplete suggestions:", xhr);
                    }
                });
            },
            minLength: 1, // 최소 몇 글자 입력 후 동작할지 설정
            select: function (event, ui) {
                // 선택한 데이터를 검색창에 입력
                $(".search-input").val(ui.item.value);
                return false; // 자동으로 폼이 제출되지 않도록 방지
            }
        });
    });

    function redirectToLogin() {
        alert("로그인이 필요한 서비스입니다.");
        window.location.href = "${pageContext.request.contextPath}/auth/login";
    }

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

    window.onclick = function (event) {
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
