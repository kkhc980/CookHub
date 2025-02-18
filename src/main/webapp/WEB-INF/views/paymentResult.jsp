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
        <h2 class="${not empty result ? 'success' : 'fail'}">${message}</h2>

        <c:if test="${not empty result}">
            <p><strong>✅ 결제 승인 번호:</strong> ${result.aid}</p>
            <p><strong>💰 결제 금액:</strong> ${result.amount.total}원</p>
            <p><strong>💳 결제 수단:</strong> ${result.payment_method_type}</p>
            <p><strong>📅 결제 일시:</strong> ${result.approved_at}</p>
            <br>
            <a href="/project/store/list">🔙 돌아가기</a>
        </c:if>

        <c:if test="${empty result}">
            <p>❌ 결제가 실패하였습니다. 다시 시도해주세요.</p>
            <br>
            <a href="/project/store/list">🔙 돌아가기</a>
        </c:if>
    </div>

</body>
</html>