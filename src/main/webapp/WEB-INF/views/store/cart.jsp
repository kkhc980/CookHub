<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>장바구니</title>
</head>
<body>

<h2>장바구니</h2>

<c:if test="${not empty message}">
    <p>${message}</p>
</c:if>

<c:if test="${not empty cart}">
    <table>
        <thead>
        <tr>
            <th>상품명</th>
            <th>가격</th>
            <th>수량</th>
            <th>총 가격</th>
            <th></th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="item" items="${cart}">
            <tr>
                <td>${item.productName}</td>
                <td>${item.productPrice}</td>
                <td>${item.productCount}</td>
                <td>${item.totalPrice}</td>
                <td><a href="../store/cart/delete/${item.productId}">삭제</a></td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</c:if>
<a href="../store/list">계속 쇼핑하기</a>
</body>
</html>