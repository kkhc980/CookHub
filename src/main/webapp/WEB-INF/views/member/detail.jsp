<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>detail</title>
    <script src="https://code.jquery.com/jquery-3.7.1.js"></script>
</head>
<body>
<p>이메일 : ${memberDTO.email }</p>
<p>회원 이름 : ${memberDTO.name }</p>
<p>전화 번호 : ${memberDTO.phone }</p>
<p>회원 등록일 : <fmt:formatDate value="${memberDTO.createdAt}" pattern="yyyy년 M월 d일 E요일"/></p>
<button id="updateMember">정보 수정</button>
<button id="deleteMember">회원 탈퇴</button>
<form id="deleteForm" action="delete" method="POST">
    <input type="hidden" name="${_csrf.parameterName }" value="${_csrf.token }">
</form>
<script type="text/javascript">
    $(document).ready(function () {

        $("#updateMember").click(function () {
            window.location.href = 'update'; // /member/update url로 이동
        }); // end click()

        $('#deleteMember').click(function () {
            if (confirm('탈퇴하시겠습니까?')) {
                var deleteForm = $("#deleteForm"); // form 객체 참조
                deleteForm.submit(); // form 전송
            }
        }); // end click()
    });
</script>
</body>
</html>
