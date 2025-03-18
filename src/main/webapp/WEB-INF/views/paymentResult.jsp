<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>결제 결과</title>
    <style>
        body { font-family: Arial, sans-serif; text-align: center; margin: 50px; }
        .container { max-width: 500px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 10px; }
        .success { color: green; }
        .fail { color: red; }
    </style>
</head>
<body>
<div class="container">
    <h2 class="${approveResponse != null ? 'success' : 'fail'}">${approveResponse != null ? "결제 성공" : "결제 실패"}</h2>

    <c:if test="${approveResponse != null}">
        <p><strong>🛍 상품명:</strong> ${approveResponse.item_name}</p>
        <p><strong>📦 수량:</strong> ${approveResponse.quantity}개</p>
        <p><strong>✅ 결제 승인 번호:</strong> ${approveResponse.aid}</p>
        <p><strong>💰 결제 금액:</strong> ${approveResponse.amount.total}원</p>
        <p><strong>💳 결제 수단:</strong> ${approveResponse.payment_method_type}</p>
        <p><strong>📅 결제 일시:</strong> ${approveResponse.approved_at}</p>
        <br>
        <a href="/project/store/list" onclick="clearSession()">🔙 돌아가기</a>
    </c:if>

    <c:if test="${approveResponse == null}">
        <p>❌ 결제가 실패하였습니다. 다시 시도해주세요.</p>
        <br>
        <a href="/project/store/list">🔙 돌아가기</a>
    </c:if>
</div>

<script>
    function clearSession() {
        fetch('/project/store/clearSession', { method: 'POST' })
            .then(response => {
                if (response.ok) {
                    window.location.href = '/project/store/list'; // 세션 삭제 후 페이지 이동
                } else {
                    console.error('세션 삭제 실패');
                }
            })
            .catch(error => console.error('Error:', error));
    }
</script>

</body>
</html>