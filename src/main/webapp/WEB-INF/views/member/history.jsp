<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>구매 내역</title>
    <script src="https://code.jquery.com/jquery-3.7.1.js"></script>
    <style>
        .detail-row {
            display: none;
        }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            margin: 20px;
        }

        h2 {
            color: #333;
            text-align: center;
            margin-bottom: 20px;
        }

        #orderTable {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 20px;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
        }

        #orderTable th, #orderTable td {
            border: 1px solid #ddd;
            padding: 10px;
            text-align: left;
        }

        #orderTable th {
            background-color: #f2f2f2;
            font-weight: bold;
        }

        #orderTable tr:nth-child(even) {
            background-color: #f9f9f9;
        }

        .order-row {
            cursor: pointer;
            transition: background-color 0.3s;
        }

        .order-row:hover {
            background-color: #e0f7fa;
        }

        .detail-row {
            display: none;
            background-color: #f0f0f0;
        }

        .detail-row table {
            width: 95%;
            margin: 10px auto;
            border-collapse: collapse;
        }

        .detail-row table th, .detail-row table td {
            border: 1px solid #ccc;
            padding: 8px;
            text-align: left;
        }

        .detail-row table th {
            background-color: #e8e8e8;
            font-weight: bold;
        }

        .detail-row table tr:nth-child(even) {
            background-color: #f2f2f2;
        }

        .address-info { /* 배송지 정보 표시를 위한 스타일 추가 */
            margin-top: 10px;
            padding: 10px;
            background-color: #e6f7ff;
            border: 1px solid #add8e6;
            border-radius: 5px;
        }
        .no-orders {
            text-align: center;
            padding: 20px;
            font-size: 1.2em;
            color: #777;
        }
    </style>
</head>
<body>
<h2>구매 내역</h2>

<c:choose>
    <c:when test="${empty list}">
        <div class="no-orders">구매 내역이 없습니다.</div>
    </c:when>
    <c:otherwise>
        <table id="orderTable">
            <thead>
            <tr>
                <th>주문 ID</th>
                <th>주문 날짜</th>
                <th>총 금액</th>
                <th>주문 상품 이름</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="order" items="${list}">
                <tr class="order-row" data-order-id="${order.orderId}" data-order-date="${order.orderDate}" data-address="${order.address}">
                    <td>${order.orderId}</td>
                    <td>${order.orderDate}</td>
                    <td><fmt:formatNumber value="${order.totalAmount}" pattern="#,###원"/></td>
                    <td>${order.orderProductName}</td>
                </tr>
                <tr class="detail-row" data-order-id="${order.orderId}" data-order-date="${order.orderDate}">
                    <td colspan="4">
                        <div class="address-info">
                            <strong>배송지:</strong> <span class="address"></span>
                        </div>
                        <table>
                            <thead>
                            <tr>
                                <th>상품 이름</th>
                                <th>상품 가격</th>
                                <th>상품 수량</th>
                                <th>상품 총 가격</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach var="detail" items="${order.orderDetails}">
                                <tr>
                                    <td>${detail.productName}</td>
                                    <td><fmt:formatNumber value="${detail.productPrice}" pattern="#,###원"/></td>
                                    <td>${detail.productCount}</td>
                                    <td><fmt:formatNumber value="${detail.productTotalPrice}" pattern="#,###원"/></td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>

        <script>
            $(document).ready(function() {
                $(".order-row").click(function() {
                    const orderId = $(this).data("order-id");
                    const orderDate = $(this).data("order-date");
                    const address = $(this).data("address"); // 배송지 정보 가져오기
                    const detailRow = $(".detail-row[data-order-id='" + orderId + "'][data-order-date='" + orderDate + "']");
                    detailRow.find(".address").text(address); // 배송지 정보 설정
                    detailRow.toggle();
                });
            });
        </script>
    </c:otherwise>
</c:choose>
</body>
</html>