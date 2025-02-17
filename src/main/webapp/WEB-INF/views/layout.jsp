<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html>
<head>
    <title>CookHub</title>
    <script src="https://code.jquery.com/jquery-3.6.4.min.js"></script>
    <script src="https://code.jquery.com/ui/1.13.2/jquery-ui.min.js"></script>

   <meta name="_csrf" content="${_csrf.token}" />
   <meta name="_csrf_header" content="${_csrf.headerName}" />

    <!-- 로그인한 사용자 ID를 안전하게 전달 -->
    <sec:authorize access="isAuthenticated()">
        <sec:authentication var="customUser" property="principal" />
        <meta name="logged-in-user-id" content="${customUser.memberVO.memberId}">
    </sec:authorize>

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

        .notification-container {
            position: relative;
            display: inline-block;
            margin-right: 20px;
        }

        #notificationButton {
            background: none;
            border: none;
            font-size: 20px;
            cursor: pointer;
            position: relative;
            color: white;
        }

        .badge {
            background: red;
            color: white;
            border-radius: 50%;
            padding: 3px 8px;
            font-size: 12px;
            position: absolute;
            top: -5px;
            right: -5px;
            display: none;
        }

        .notification-popup {
            display: none;
            position: absolute;
            right: 0;
            top: 30px;
            width: 280px;
            background: white;
            box-shadow: 0px 2px 5px rgba(0, 0, 0, 0.2);
            border-radius: 5px;
            z-index: 1000;
            max-height: 350px;
            overflow-y: auto;
            border: 1px solid #ccc;
        }

        .notification-header {
            display: flex;
            justify-content: space-between;
            padding: 12px;
            background: #f5f5f5;
            border-bottom: 1px solid #ddd;
            font-weight: bold;
            font-size: 16px;
            color: #000; /* 글씨 검은색 */
        }

        .notification-footer {
            text-align: center;
            padding: 12px;
            background: #f5f5f5;
            border-top: 1px solid #ddd;
            color: #000; /* 글씨 검은색 */
        }

        #notificationList {
            list-style: none;
            padding: 0;
            margin: 0;
        }

        #notificationList li {
            padding: 12px;
            border-bottom: 1px solid #ddd;
            font-size: 14px;
            cursor: pointer;
            transition: background-color 0.2s;
            color: #000; /* 글씨 검은색 */
            background: #ffffff; /* 기본 배경 흰색 */
        }

        /* 읽지 않은 알림 */
        #notificationList li.unread {
            background: #ffffff !important; /* 배경 흰색 */
            font-weight: bold;
            color: #000000 !important; /* 글씨 검은색 */
        }

        #notificationList li:hover {
            background: #f0f0f0;
        }

        /* '모두 읽음' 버튼 스타일 */
        .notification-footer button {
            background: #000;
            border: 1px solid #000;
            color: #fff;
            padding: 8px 12px;
            font-size: 14px;
            cursor: pointer;
            border-radius: 4px;
        }

        .notification-footer button:hover {
            background: #444;
        }

        /* 닫기 버튼 */
        .notification-header button {
            background: none;
            border: none;
            font-size: 16px;
            cursor: pointer;
            color: #000; /* 글씨 검은색 */
        }

        .notification-header button:hover {
            background: #ddd;
        }

        .user-notification-container {
            display: flex;
            align-items: center;
            gap: 10px; /* 아이콘과 텍스트 사이 간격 조절 */
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

    <div class="user-notification-container">
        <sec:authorize access="isAuthenticated()">
            <div class="notification-container">
                <button id="notificationButton">
                    🔔 <span id="unreadCount" class="badge"></span>
                </button>
                <div id="notificationPopup" class="notification-popup">
                    <div class="notification-header">
                        <span>📢 알림</span>
                        <button onclick="closeNotificationPopup()">✖</button>
                    </div>
                    <ul id="notificationList"></ul>
                    <div class="notification-footer">
                        <button onclick="markAllAsRead()">✅ 모두 읽음</button>
                    </div>
                </div>
            </div>

            <div class="logged-in-menu">
                <div class="dropdown">
                    <a href="#" onclick="toggleDropdown(event)">
                        <sec:authentication property="principal.name"/>님
                    </a>
                    <div class="dropdown-content" id="userDropdown">
                        <a href="${pageContext.request.contextPath}/member/detail">내 정보</a>
                        <!-- 어드민 페이지로 가는 링크 내정보 아래 뜨도록 함
                        	 ROLE_ADMIN에게만 보이도록 -->
                        <sec:authorize access="hasRole('ROLE_ADMIN')">
                            <a href="${pageContext.request.contextPath}/admin/recipeboard">관리자 페이지</a>
                        </sec:authorize>
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
            <a href="../auth/login">로그인</a> &nbsp;
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
    &nbsp;
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

    $(document).ready(function () {
        var memberId = $("meta[name='logged-in-user-id']").attr("content");
        var contextPath = "${pageContext.request.contextPath}";

        if (!memberId) {
            console.log("🚨 로그인되지 않음. 알림 기능 비활성화.");
            return;
        }

        // 🔔 최신 알림 로드
        function loadNotifications() {
            $.ajax({
                url: contextPath + "/notifications/unread/" + memberId,
                type: "GET",
                success: function (notifications) {
                    var notificationList = $("#notificationList");
                    notificationList.empty();
                    var unreadCount = 0;

                    notifications.forEach(function (notification) {
                        var listItem = $("<li>").text(notification.message);
                        listItem.attr("data-id", notification.notificationId);
                        if (!notification.isRead) {
                            listItem.addClass("unread");
                            unreadCount++;
                        }
                        notificationList.append(listItem);
                    });

                    // 🔴 안 읽은 알림 개수 업데이트
                    if (unreadCount > 0) {
                        $("#unreadCount").text(unreadCount).show();
                    } else {
                        $("#unreadCount").hide();
                    }
                },
                error: function (xhr) {
                    console.error("🔴 알림 가져오기 실패:", xhr);
                }
            });
        }

        // ✅ "모두 읽음" 기능
        function markAllAsRead() {
            var csrfToken = $('meta[name="_csrf"]').attr('content');
            var csrfHeader = $('meta[name="_csrf_header"]').attr('content');

            $.ajax({
                url: contextPath + "/notifications/readAll/" + memberId,
                type: "POST",
                beforeSend: function (xhr) {
                    xhr.setRequestHeader(csrfHeader, csrfToken);
                },
                success: function () {
                    console.log("✅ 모든 알림을 읽음으로 변경");
                    $("#notificationList li").removeClass("unread");
                    $("#unreadCount").hide();
                    loadNotifications(); // 새로고침하여 최신 데이터 가져오기
                },
                error: function (xhr) {
                    console.error("🔴 알림 읽음 처리 실패:", xhr);
                }
            });
        }

        // 🔔 알림 버튼 클릭 이벤트
        $("#notificationButton").click(function (event) {
            event.stopPropagation();
            $("#notificationPopup").toggle();
            loadNotifications();
        });

        // 📌 팝업 닫기 이벤트
        window.onclick = function (event) {
            if (!event.target.matches("#notificationButton")) {
                $("#notificationPopup").hide();
            }
        };

     // ✅ 알림 클릭 시 읽음 처리 (CSRF 토큰 추가)
        $("#notificationList").on("click", "li", function () {
            var notificationId = $(this).data("id");
            var csrfToken = $("meta[name='_csrf']").attr("content"); // 메타 태그에서 CSRF 토큰 가져오기
            var csrfHeader = $("meta[name='_csrf_header']").attr("content"); // CSRF 헤더 이름 가져오기

            $.ajax({
                url: contextPath + "/notifications/read/" + notificationId,
                type: "POST",
                beforeSend: function(xhr) {
                    xhr.setRequestHeader(csrfHeader, csrfToken); // CSRF 헤더 추가
                },
                success: function () {
                    $(this).removeClass("unread");
                    loadNotifications();
                }
            });
        });


        // 초기 알림 데이터 로드
        loadNotifications();
    });
</script>
</body>
</html>