<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>주문 확인</title>
    <style>
        body {
            font-family: sans-serif;
            margin: 20px;
        }

        h2 {
            color: #333;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 20px;
        }

        th, td {
            border: 1px solid #ddd;
            padding: 8px;
            text-align: left;
        }

        th {
            background-color: #f2f2f2;
        }

        .div-total-payment {
            margin-bottom: 20px;
            font-weight: bold;
        }

        .purchase-button {
            background-color: #4CAF50;
            border: none;
            color: white;
            padding: 10px 20px;
            text-align: center;
            text-decoration: none;
            display: inline-block;
            font-size: 16px;
            cursor: pointer;
            border-radius: 5px;
        }

        .empty-message {
            color: red;
            font-weight: bold;
        }
    </style>
</head>
<body>

<h2>주문 확인</h2>

<p>주문자 : ${memberDTO.name}</p>

<c:if test="${not empty orderPageDTO.orders}">
    <table>
        <thead>
        <tr>
            <th>상품명</th>
            <th>수량</th>
            <th>가격</th>
            <th>총 가격</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="order" items="${orderPageDTO.orders}">
            <tr>
                <td>${order.productName}</td>
                <td>${order.productCount}</td>
                <td>${order.productPrice}</td>
                <td>${order.totalPrice}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

    <div class="div-total-payment">
        총 결제 금액:
        <c:set var="totalPayment" value="0"/>
        <c:forEach var="order" items="${orderPageDTO.orders}">
            <c:set var="totalPayment" value="${totalPayment + order.totalPrice}"/>
        </c:forEach>
            ${totalPayment} 원
    </div>

    <div>
        <button class="purchase-button" onclick="purchaseProduct()">구매하기</button>
    </div>
</c:if>

<c:if test="${empty orderPageDTO.orders}">
    <p class="empty-message">선택된 상품이 없습니다.</p>
</c:if>

<script>
    function purchaseProduct() {
        let form = document.createElement("form");
        form.method = "POST";
        form.action = '${pageContext.request.contextPath}/store/purchase';

        let csrfInput = document.createElement("input");
        csrfInput.type = "hidden";
        csrfInput.name = "${_csrf.parameterName}";
        csrfInput.value = "${_csrf.token}";
        form.appendChild(csrfInput);

        <c:forEach var="order" items="${orderPageDTO.orders}" varStatus="status">
        let productIdInput = document.createElement("input");
        productIdInput.type = "hidden";
        productIdInput.name = "orders[" + ${status.index} + "].productId";
        productIdInput.value = "${order.productId}";
        form.appendChild(productIdInput);

        let productCountInput = document.createElement("input");
        productCountInput.type = "hidden";
        productCountInput.name = "orders[" + ${status.index} + "].productCount";
        productIdInput.value = "${order.productCount}";
        form.appendChild(productCountInput);

        let productNameInput = document.createElement("input");
        productNameInput.type = "hidden";
        productNameInput.name = "orders[" + ${status.index} + "].productName";
        productNameInput.value = "${order.productName}";
        form.appendChild(productNameInput);

        let productPriceInput = document.createElement("input");
        productPriceInput.type = "hidden";
        productPriceInput.name = "orders[" + ${status.index} + "].productPrice";
        productPriceInput.value = "${order.productPrice}";
        form.appendChild(productPriceInput);
        </c:forEach>

        document.body.appendChild(form);
        form.submit();
    }
</script>

</body>
</html>