<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>주문 확인</title>
</head>
<body>

<h2>주문 확인</h2>

<p>주문자 ID: ${memberId}</p>

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

    <!-- 결제 금액 표시 -->
    <div>
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
    <p>선택된 상품이 없습니다.</p>
</c:if>

<script>
    function purchaseProduct() {
        // 1. 폼 생성
        let form = document.createElement("form");
        form.method = "POST";
        form.action = '${pageContext.request.contextPath}/store/purchase';

        // 2. CSRF 토큰 추가
        let csrfInput = document.createElement("input");
        csrfInput.type = "hidden";
        csrfInput.name = "${_csrf.parameterName}";
        csrfInput.value = "${_csrf.token}";
        form.appendChild(csrfInput);

        // 3. orderPageDTO.orders에 있는 각 상품 정보를 폼에 추가
        <c:forEach var="order" items="${orderPageDTO.orders}" varStatus="status">
        let productId = "${order.productId}";
        let productCount = "${order.productCount}";
        let productName = "${order.productName}";
        let productPrice = "${order.productPrice}";

        // productId
        let productIdInput = document.createElement("input");
        productIdInput.type = "hidden";
        productIdInput.name = "orders[" + ${status.index} + "].productId";
        productIdInput.value = productId;
        form.appendChild(productIdInput);

        // productCount
        let productCountInput = document.createElement("input");
        productCountInput.type = "hidden";
        productCountInput.name = "orders[" + ${status.index} + "].productCount";
        productCountInput.value = productCount;
        form.appendChild(productCountInput);

        // productName
        let productNameInput = document.createElement("input");
        productNameInput.type = "hidden";
        productNameInput.name = "orders[" + ${status.index} + "].productName";
        productNameInput.value = productName;
        form.appendChild(productNameInput);

        // productPrice
        let productPriceInput = document.createElement("input");
        productPriceInput.type = "hidden";
        productPriceInput.name = "orders[" + ${status.index} + "].productPrice";
        productPriceInput.value = productPrice;
        form.appendChild(productPriceInput);
        </c:forEach>

        // 4. 폼 제출
        document.body.appendChild(form);
        form.submit();
    }
</script>

</body>
</html>