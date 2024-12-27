<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Update</title>
    <script src="https://code.jquery.com/jquery-3.7.1.js" />
</head>
<body>
<table>
    <tr>
        <th>회원정보를 입력해 주세요. (* 표시는 필수입력 항목입니다)</th>
    </tr>
    <tr>
        <th>회원 정보</th>
    </tr>
    <tr>
        <th>* 이 름</th>
        <td><input type="text" name="name" id="name" value="${memberVO.name}" readonly></td>
    </tr>
    <tr>
        <th>* 이메일</th>
        <td>
            <input type="email" name="email" id="email" value="${memberVO.email}" readonly>
        </td>
    </tr>
    <tr>
        <th>전화번호</th>
        <td><input type="tel" name="phone" id="phone" value="${memberVO.phone}" readonly></td>
    </tr>
    <tr>
        <td><button onclick="">회원정보 수정</button>&nbsp;&nbsp;<button onclick="">회원 탈퇴</button></td>
    </tr>
</table>
</body>
</html>
