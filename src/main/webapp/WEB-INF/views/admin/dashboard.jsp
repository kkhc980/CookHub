<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="content">
    <h2>관리자 대시보드</h2>

    <div class="dashboard-summary">
        <h3>요약 정보</h3>
        <p>총 회원 수: ${memberCount}</p>
        <p>총 레시피 수: ${recipeCount}</p>
        <!-- 기타 요약 정보 추가 -->
    </div>

    <div class="dashboard-links">
        <h3>빠른 링크</h3>
        <ul>
            <li><a href="${pageContext.request.contextPath}/admin/members/list">회원 관리</a></li>
            <li><a href="${pageContext.request.contextPath}/admin/recipeboard">레시피 관리</a></li>
            <!-- 기타 빠른 링크 추가 -->
        </ul>
    </div>

    <!-- 추가적인 대시보드 컨텐츠 (통계 차트, 최신 활동 내역 등) -->

</div>