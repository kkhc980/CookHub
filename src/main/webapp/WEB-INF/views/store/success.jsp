<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>카카오페이 결제 성공</title>
  <script>
    window.onload = function() {
      if (window.opener) {
        // 부모 창의 URL을 paymentResult.jsp로 변경
        window.opener.location.href = "${pageContext.request.contextPath}/store/paymentResult";
        // 현재 창 닫기
        window.close();
      } else {
        alert("결제 성공!");
        window.location.href = "${pageContext.request.contextPath}/store/paymentResult"; // 부모창이 없을 경우 직접 이동
      }
    };
  </script>
</head>
<body>
</body>
</html>