<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>게시판 메인 페이지</title>
</head>
<body>
	<h1>게시판</h1>
	<!-- 글 작성 페이지 이동 버튼 -->
	<a href="register"><input type="button" value="글 작성"></a>
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
			<c:forEach var="RecipeBoardVO" items="${recipeBoardList }">
				<tr>
					<td>${RecipeBoardVO.recipeBoardId }</td>
					<td><a href="detail?recipeBoardId=${RecipeBoardVO.recipeBoardId }">
					${RecipeBoardVO.recipeBoardTitle }</a></td>
					<td>${RecipeBoardVO.memberId }</td>
					<!-- boardDateCreated 데이터 포멧 변경 -->
					<fmt:formatDate value="${RecipeBoardVO.recipeBoardDateCreated }"
						pattern="yyyy-MM-dd HH:mm:ss" var="recipeBoardDateCreated" />
					<td>${recipeBoardDateCreated }</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<ul>
		<!-- 이전 버튼 생성을 위한 조건문 -->
		<c:if test="${pageMaker.isPrev() }">
			<li><a href="list?pageNum=${pageMaker.startNum - 1}">이전</a></li>
		</c:if>
		<!-- 반복문으로 시작 번호부터 끝 번호까지 생성 -->
		<c:forEach begin="${pageMaker.startNum }"
			end="${pageMaker.endNum }" var="num">
			<li><a href="list?pageNum=${num }">${num }</a></li>
		</c:forEach>
		<!-- 다음 버튼 생성을 위한 조건문 -->
		<c:if test="${pageMaker.isNext() }">
			<li><a href="list?pageNum=${pageMaker.endNum + 1}">다음</a></li>
		</c:if>
	</ul>
</body>
</html>