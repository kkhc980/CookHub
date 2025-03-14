<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" integrity="sha512-9usAa10IRO0HhonpyAIVpjrylPvoDwiPUiKdWk5t3PyolY1cOd4DSE0Ga+ri4AuTroPR5aQvXU9xC6qOPnzFeg==" crossorigin="anonymous" referrerpolicy="no-referrer" />
<style>
/* 푸터 스타일 */
.footer {
    background-color: #333;
    color: white;
    padding: 30px 20px; /* 상하 패딩 증가 */
    text-align: center;
    margin-top: 30px; /* content와의 간격 증가 */
    box-shadow: 0px -2px 5px rgba(0, 0, 0, 0.2); /* 그림자 추가 */
}

.footer-container {
    display: flex;
    justify-content: space-around;
    align-items: flex-start; /* 상단 정렬 */
    flex-wrap: wrap;
    max-width: 1200px;
    margin: 0 auto;
}

.footer-info,
.footer-links,
.footer-social, /* 소셜 미디어 스타일 추가 */
.footer-copyright {
    margin-bottom: 20px; /* 간격 증가 */
    text-align: left; /* 왼쪽 정렬 */
}

.footer-info h3,
.footer-links h3,
.footer-social h3 {
    font-size: 1.2em; /* 제목 크기 조정 */
    margin-bottom: 10px;
    color: #ff9900; /* 제목 색상 변경 */
}

.footer-links ul,
.footer-social ul {
    list-style: none;
    padding: 0;
}

.footer-links li,
.footer-social li {
    margin-bottom: 8px; /* 간격 조정 */
}

.footer-links a,
.footer-social a {
    color: white;
    text-decoration: none;
    display: flex; /* 아이콘과 텍스트를 가로로 정렬 */
    align-items: center; /* 세로 가운데 정렬 */
}

.footer-links a:hover,
.footer-social a:hover {
    color: #ff9900;
}

.footer-links i,
.footer-social i {
    margin-right: 8px; /* 아이콘과 텍스트 간 간격 */
    width: 20px; /* 아이콘 너비 고정 */
    text-align: center; /* 아이콘 가운데 정렬 */
}

.footer-copyright p {
    font-size: 0.9em;
    color: #ccc; /* 저작권 텍스트 색상 변경 */
}

/* 반응형 디자인 */
@media (max-width: 768px) {
    .footer-container {
        flex-direction: column;
        align-items: center;
    }

    .footer-info,
    .footer-links,
    .footer-social,
    .footer-copyright {
        width: 100%;
        text-align: center;
    }
}
</style>
<div class="footer">
    <div class="footer-container">
        <div class="footer-info">
            <h3>CookHub</h3>
            <p>문의: COOKHUB@cookhub.com</p>
            <p>전화: 010-1234-5678</p>
        </div>

        <div class="footer-links">
            <h3>바로가기</h3>
            <ul>
                <li><a href="${pageContext.request.contextPath}/noticeboard/list"><i class="fas fa-bullhorn"></i> 공지사항</a></li>
                <li><a href="${pageContext.request.contextPath}/recipeboard/list"><i class="fas fa-utensils"></i> 레시피</a></li>
                <li><a href="${pageContext.request.contextPath}/store/list"><i class="fas fa-store"></i> 스토어</a></li>
<%--                 <li><a href="${pageContext.request.contextPath}/noticeboard/list"><i class="fas fa-question-circle"></i> FAQ</a></li> --%>
                <li><a href="#"><i class="fas fa-envelope"></i> 문의하기</a></li>
            </ul>
        </div>

        <div class="footer-social">
            <h3>소셜 미디어</h3>
            <ul>
                <li><a href="#" target="_blank"><i class="fab fa-facebook"></i> Facebook</a></li>
                <li><a href="#" target="_blank"><i class="fab fa-instagram"></i> Instagram</a></li>
                <li><a href="#" target="_blank"><i class="fab fa-twitter"></i> Twitter</a></li>
                <li><a href="https://www.notion.so/CookHub-2025-02-21-1a1be2b5006c80c7a371c7e3a094f357" target="_blank"><i class="fas fa-book"></i> Notion</a></li>
            </ul>
        </div>

        <div class="footer-copyright">
            <p>© 2025 CookHub. All rights reserved.</p>
        </div>
    </div>
</div>