<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>접근 제한</title>
</head>
<body>
    <!-- SPRING_SECURITY_403_EXCEPTION.getMessage() : Spring Security에서 전달된 403 예외 메시지를 출력 -->
    <h2>${SPRING_SECURITY_403_EXCEPTION.getMessage()}</h2>
    
    <!-- msg : 사용자가 직접 설정한 메시지를 출력 -->
    <h2>${msg}</h2>
</body>
</html>
