<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>공지 게시판</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 20px;
            background-color: #f4f4f4;
        }

        h1 {
            text-align: center;
            color: #333;
            margin-bottom: 20px;
        }

        hr {
            border: 1px solid #ddd;
            margin-bottom: 20px;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 20px;
            background-color: #fff;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
        }

        th, td {
            padding: 12px 15px;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }

        th {
            background-color: #007bff;
            color: white;
            font-weight: bold;
        }

        tbody tr:hover {
            background-color: #f5f5f5;
        }

        a {
            color: #007bff;
            text-decoration: none;
        }

        a:hover {
            text-decoration: underline;
        }

        ul {
            list-style: none;
            padding: 0;
            text-align: center;
        }

        ul li {
            display: inline;
            margin: 0 5px;
        }

        ul li a {
            display: inline-block;
            padding: 8px 12px;
            background-color: #f0f0f0;
            border: 1px solid #ccc;
            border-radius: 4px;
            color: #333;
        }

        ul li a:hover {
            background-color: #ddd;
        }

        .button-container {
            text-align: right;
            margin-bottom: 10px;
        }

        .button-container a {
            display: inline-block;
            padding: 10px 15px;
            background-color: #28a745;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            text-decoration: none;
            font-size: 14px;
        }

        .button-container a:hover {
            background-color: #218838;
        }

        .pagination-container {
            text-align: center;
            margin-top: 20px;
        }

        .pagination-container ul {
            display: inline-block;
        }

        .pagination-container ul li {
            display: inline;
            margin: 0 5px;
        }

        .pagination-container ul li a {
            display: inline-block;
            padding: 8px 12px;
            background-color: #007bff;
            color: white;
            text-decoration: none;
            border-radius: 4px;
            transition: background-color 0.3s ease;
        }

        .pagination-container ul li a:hover {
            background-color: #0056b3;
        }

        .pagination-container ul li.active a {
            background-color: #0056b3;
            font-weight: bold;
        }
    </style>
</head>
<body>
<h1>공지 게시판</h1>

<!-- 글 작성 페이지 이동 버튼 -->
<div class="button-container">
    <sec:authorize access="hasRole('ROLE_ADMIN')">
        <a href="${pageContext.request.contextPath}/noticeboard/register">글 작성</a>
    </sec:authorize>
</div>
<hr>
<table>
    <thead>
    <tr>
        <th style="width: 60px">번호</th>
        <th style="width: 700px">제목</th>
        <th style="width: 120px">작성자</th>
        <th style="width: 100px">작성일</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="NoticeBoardVO" items="${noticeBoardList }">
        <tr>
            <td>${NoticeBoardVO.noticeBoardId }</td>
            <td>
                <a href="${pageContext.request.contextPath}/noticeboard/detail/${NoticeBoardVO.noticeBoardId }">
                        ${NoticeBoardVO.noticeBoardTitle }
                </a>
            </td>
            <td>${NoticeBoardVO.memberId }</td>
            <!-- boardDateCreated 데이터 포멧 변경 -->
            <fmt:formatDate value="${NoticeBoardVO.noticeBoardCreatedDate }"
                            pattern="yyyy-MM-dd HH:mm:ss" var="noticeBoardDateCreated"/>
            <td>${noticeBoardDateCreated }</td>
        </tr>
    </c:forEach>
    </tbody>
</table>

<!-- 페이지네이션 -->
<div class="pagination-container">
    <ul>
        <!-- 이전 버튼 생성을 위한 조건문 -->
        <c:if test="${pageMaker.isPrev() }">
            <li>
                <a href="${pageContext.request.contextPath}/noticeboard/list?pageNum=${pageMaker.startNum - 1}">이전</a>
            </li>
        </c:if>

        <!-- 반복문으로 시작 번호부터 끝 번호까지 생성 -->
        <c:forEach begin="${pageMaker.startNum }" end="${pageMaker.endNum }" var="num">
            <li ${pageNum == num ? 'class="active"' : ''}>
                <a href="${pageContext.request.contextPath}/noticeboard/list?pageNum=${num}">${num}</a>
            </li>
        </c:forEach>

        <!-- 다음 버튼 생성을 위한 조건문 -->
        <c:if test="${pageMaker.isNext() }">
            <li>
                <a href="${pageContext.request.contextPath}/noticeboard/list?pageNum=${pageMaker.endNum + 1}">다음</a>
            </li>
        </c:if>
    </ul>
</div>
</body>
</html>