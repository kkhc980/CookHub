<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>회원 정보</title>
    <script src="https://code.jquery.com/jquery-3.7.1.js"></script>
    <style>
        body {
            font-family: sans-serif;
            display: flex;
            flex-direction: column;
            min-height: 100vh;
            margin: 0;
            background-color: #f4f4f4;
        }

        .container {
            flex: 1;
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .member-info-container {
            width: 400px;
            padding: 30px;
            background-color: white;
            border-radius: 8px;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
        }

        .member-info-container h2 {
            text-align: center;
            margin-bottom: 25px;
            color: #333;
        }

        .member-info-container p {
            margin-bottom: 10px;
        }

        .member-info-container strong {
            font-weight: bold;
        }

        .member-info-container button {
            padding: 10px 15px;
            border: none;
            background-color: #007bff;
            color: white;
            border-radius: 5px;
            cursor: pointer;
            margin-right: 10px;
        }

        .member-info-container .button-container {
            display: flex;
            justify-content: center; /* 버튼 그룹을 가운데 정렬 */
            margin-top: 20px;
        }

        .member-info-container button {
            padding: 10px 15px;
            border: none;
            background-color: #007bff;
            color: white;
            border-radius: 5px;
            cursor: pointer;
            margin: 0 5px; /* 버튼 사이 간격 추가 */
        }

        .member-info-container button:last-child {
            margin-right: 0;
            background-color: #dc3545;
        }

        .member-info-container button:hover {
            background-color: #0056b3;
        }

        .member-info-container button:last-child:hover {
            background-color: #c82333;
        }
    </style>
    <script type="text/javascript">
        $(document).ready(function () {
            $("#updateMember").click(function () {
                window.location.href = 'update';
            });

            $('#deleteMember').click(function () {
                if (confirm('탈퇴하시겠습니까?')) {
                    var deleteForm = $("#deleteForm");
                    deleteForm.submit();
                }
            });
        });
    </script>
</head>
<body>
<div class="container">
    <div class="member-info-container">
        <h2>회원 정보</h2>
        <p><strong>이메일:</strong> ${memberDTO.email }</p>
        <p><strong>회원 이름:</strong> ${memberDTO.name }</p>
        <p><strong>전화 번호:</strong> ${memberDTO.phone }</p>
        <p><strong>회원 등록일:</strong> <fmt:formatDate value="${memberDTO.createdAt}" pattern="yyyy년 M월 d일 E요일"/></p>
        <div class="button-container">
            <button id="updateMember">정보 수정</button>
            <button id="deleteMember">회원 탈퇴</button>
            <form id="deleteForm" action="delete" method="POST">
                <input type="hidden" name="${_csrf.parameterName }" value="${_csrf.token }">
            </form>
        </div>
    </div>
</div>
</body>
</html>
