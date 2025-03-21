<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>주문 확인</title>
    <script src="//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>
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
            text-align: center; /* 가운데 정렬 추가 */
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

        #sample6_postcode{
            width: 150px; /* 원하는 너비로 조정 */
            padding: 10px;
            margin-bottom: 5px;
            border: 1px solid #ddd;
            border-radius: 5px;
            box-sizing: border-box;
        }

        #sample6_address {
            width: 400px; /* 원하는 너비로 조정 */
            padding: 10px;
            margin-bottom: 5px;
            border: 1px solid #ddd;
            border-radius: 5px;
            box-sizing: border-box;
        }

        #sample6_detailAddress, #sample6_extraAddress {
            width: 300px; /* 상세주소, 참고항목 너비 조정 */
            padding: 10px;
            margin-bottom: 5px;
            border: 1px solid #ddd;
            border-radius: 5px;
            box-sizing: border-box;
        }

        #sample6_postcode + input[type="button"] {
            width: 100px;
            padding: 10px;
            margin-left: 5px;
            border: none;
            background-color: #007bff;
            color: white;
            border-radius: 5px;
            cursor: pointer;
        }

        #sample6_postcode + input[type="button"]:hover {
            background-color: #0056b3;
        }

        .daum-address-inputs {
            display: flex;
            align-items: center;
            margin-bottom: 15px;
        }

        .daum-address-inputs > input[type="text"] {
            flex-grow: 0; /* 남은 공간 채우지 않도록 변경 */
        }

        .daum-address-inputs > input[type="button"] {
            margin-left: 5px;
        }
    </style>
</head>
<body>

<h2>주문 확인</h2>

<p>주문자 : ${memberDTO.name}</p>

<div class="daum-address-inputs">
    <input type="text" id="sample6_postcode" placeholder="우편번호" readonly>
    <input type="button" onclick="sample6_execDaumPostcode()" value="우편번호 찾기">
</div>
<input type="text" id="sample6_address" placeholder="주소" readonly><br>
<input type="text" id="sample6_detailAddress" placeholder="상세주소"><br>
<input type="text" id="sample6_extraAddress" placeholder="참고항목">
<br><br>
<c:if test="${not empty orderPageDTO.orders}">
    <table>
        <thead>
        <tr>
            <th>상품명</th>
            <th>수량</th>
            <th>가격</th>
            <th>합계</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="order" items="${orderPageDTO.orders}">
            <tr>
                <td>${order.productName}</td>
                <td>${order.productCount}</td>
                <td><fmt:formatNumber value="${order.productPrice}" pattern="#,### 원"/></td>
                <td><fmt:formatNumber value="${order.totalPrice}" pattern="#,### 원"/></td>
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
        <fmt:formatNumber value="${totalPayment}" pattern="#,### 원"/>
    </div>

    <div>
        <button class="purchase-button" onclick="validateAndSubmit(${totalPayment})">구매하기</button>
    </div>
</c:if>

<c:if test="${empty orderPageDTO.orders}">
    <p class="empty-message">선택된 상품이 없습니다.</p>
</c:if>

<div id="paymentResultArea"></div>

<script>
    function validateAndSubmit(totalPayment) {
        if (!document.getElementById("sample6_postcode").value) {
            alert("주소 입력을 해주세요.");
            return;
        }
        submitOrder(totalPayment);
    }

    function submitOrder(totalPayment) {
        let form = document.createElement("form");
        form.method = "POST";
        form.action = "${pageContext.request.contextPath}/store/purchase?totalPayment=" + totalPayment;

        let productIdInput = null;
        let productCountInput = null;
        let productNameInput = null;
        let productPriceInput = null;
        let totalPriceInput = null;

        <c:forEach var="order" items="${orderPageDTO.orders}" varStatus="status">
        productIdInput = document.createElement("input");
        productIdInput.type = "hidden";
        productIdInput.name = "orders[${status.index}].productId";
        productIdInput.value = "${order.productId}";
        form.appendChild(productIdInput);

        productCountInput = document.createElement("input");
        productCountInput.type = "hidden";
        productCountInput.name = "orders[${status.index}].productCount";
        productCountInput.value = "${order.productCount}";
        form.appendChild(productCountInput);

        productNameInput = document.createElement("input");
        productNameInput.type = "hidden";
        productNameInput.name = "orders[${status.index}].productName";
        productNameInput.value = "${order.productName}";
        form.appendChild(productNameInput);

        productPriceInput = document.createElement("input");
        productPriceInput.type = "hidden";
        productPriceInput.name = "orders[${status.index}].productPrice";
        productPriceInput.value = "${order.productPrice}";
        form.appendChild(productPriceInput);

        totalPriceInput = document.createElement("input");
        totalPriceInput.type = "hidden";
        totalPriceInput.name = "orders[${status.index}].totalPrice";
        totalPriceInput.value = "${order.totalPrice}";
        form.appendChild(totalPriceInput);
        </c:forEach>

        // 우편번호 및 주소 정보 추가
        let postcodeInput = document.createElement("input");
        postcodeInput.type = "hidden";
        postcodeInput.name = "postcode";
        postcodeInput.value = document.getElementById("sample6_postcode").value;
        form.appendChild(postcodeInput);

        let addressInput = document.createElement("input");
        addressInput.type = "hidden";
        addressInput.name = "address";
        addressInput.value = document.getElementById("sample6_address").value.trim() + " " +
            document.getElementById("sample6_detailAddress").value.trim() + " " +
            document.getElementById("sample6_extraAddress").value.trim();
        form.appendChild(addressInput);

        let csrfInput = document.createElement("input");
        csrfInput.type = "hidden";
        csrfInput.name = "${_csrf.parameterName}";
        csrfInput.value = "${_csrf.token}";
        form.appendChild(csrfInput);

        // 팝업 창 열기
        let popupWidth = 500;
        let popupHeight = 600;
        let popupX = (window.screen.width / 2) - (popupWidth / 2);
        let popupY = (window.screen.height / 2) - (popupHeight / 2);
        // 팝업 창 열기 및 이름 저장
        let popup = window.open("", "kakaopayPopup", "width=" + popupWidth + ", height=" + popupHeight + ", left=" + popupX + ", top=" + popupY);

        // 폼 전송 대상 설정
        form.target = "kakaopayPopup";

        // 폼을 body에 추가하고 전송
        document.body.appendChild(form);
        form.submit();
    }

    // 결제 결과를 받아 처리하는 함수
    window.addEventListener("message", function(event) {
        if (event.origin === "${pageContext.request.contextPath}") {
            let result = event.data;
            if (result.success) {
                // 결제 성공 시 paymentResult.jsp 로 이동
                window.location.href = "${pageContext.request.contextPath}/store/paymentResult";
            } else {
                document.getElementById("paymentResultArea").innerHTML = "❌ 결제가 실패하였습니다. 다시 시도해주세요.";
            }
        }
    });

    // 우편번호
    function sample6_execDaumPostcode() {
        new daum.Postcode({
            oncomplete: function(data) {
                // 팝업에서 검색결과 항목을 클릭했을때 실행할 코드를 작성하는 부분.

                // 각 주소의 노출 규칙에 따라 주소를 조합한다.
                // 내려오는 변수가 값이 없는 경우엔 공백('')값을 가지므로, 이를 참고하여 분기 한다.
                var addr = ''; // 주소 변수
                var extraAddr = ''; // 참고항목 변수

                //사용자가 선택한 주소 타입에 따라 해당 주소 값을 가져온다.
                if (data.userSelectedType === 'R') { // 사용자가 도로명 주소를 선택했을 경우
                    addr = data.roadAddress;
                } else { // 사용자가 지번 주소를 선택했을 경우(J)
                    addr = data.jibunAddress;
                }

                // 사용자가 선택한 주소가 도로명 타입일때 참고항목을 조합한다.
                if(data.userSelectedType === 'R'){
                    // 법정동명이 있을 경우 추가한다. (법정리는 제외)
                    // 법정동의 경우 마지막 문자가 "동/로/가"로 끝난다.
                    if(data.bname !== '' && /[동|로|가]$/g.test(data.bname)){
                        extraAddr += data.bname;
                    }
                    // 건물명이 있고, 공동주택일 경우 추가한다.
                    if(data.buildingName !== '' && data.apartment === 'Y'){
                        extraAddr += (extraAddr !== '' ? ', ' + data.buildingName : data.buildingName);
                    }
                    // 표시할 참고항목이 있을 경우, 괄호까지 추가한 최종 문자열을 만든다.
                    if(extraAddr !== ''){
                        extraAddr = ' (' + extraAddr + ')';
                    }
                    // 조합된 참고항목을 해당 필드에 넣는다.
                    document.getElementById("sample6_extraAddress").value = extraAddr;

                } else {
                    document.getElementById("sample6_extraAddress").value = '';
                }

                // 우편번호와 주소 정보를 해당 필드에 넣는다.
                document.getElementById('sample6_postcode').value = data.zonecode;
                document.getElementById("sample6_address").value = addr;
                // 커서를 상세주소 필드로 이동한다.
                document.getElementById("sample6_detailAddress").focus();
            }
        }).open();
    }
</script>

</body>
</html>