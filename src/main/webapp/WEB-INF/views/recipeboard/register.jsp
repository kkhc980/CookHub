<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>레시피 등록</title>

</head>
<body>
	<h2>레시피 등록</h2>
	<form action="register" method="POST">
		<!-- input 태그의 name은 boardVO의 멤버 변수 이름과 동일하게 작성 -->
		<!-- 레시피 제목 -->
		<label for="recipeBoardTitle">레시피 제목</label> <input type="text"
			id="recipeBoardTitle" name="recipeBoardTitle" required>

		<!-- 레시피 내용 -->
		<label for="recipeBoardContent">레시피 내용</label>
		<textarea id="recipeBoardContent" name="recipeBoardContent" required></textarea>

		<!-- 작성자 ID -->
		<label for="memberId">작성자 ID</label> <input type="text" id="memberId"
			name="memberId" required readonly>

		<!-- 조회수 -->
		<!--  <label for="viewCount">조회수</label>
            <input type="number" id="viewCount" name="viewCount" value="0" required> -->

		<!-- 댓글수 -->
		<!--    <label for="replyCount">댓글수</label>
            <input type="number" id="replyCount" name="replyCount" value="0" required> -->

		<!-- 레시피 타입 -->
		<fieldset>
			<legend>
				<label for="typeId">레시피 타입</label>
			</legend>
			<div class="checkbox-group">
				<c:forEach var="type" items="${typesList}">
					<input type="checkbox" name="typeId" value="${type.typeId}"
						id="type${type.typeId}">
					<label for="type${type.typeId}">${type.typeName}</label>
					<br>
				</c:forEach>
			</div>
		</fieldset>

		<fieldset>
			<legend>
				<label for="ingredientId">재료</label>
			</legend>
			<div class="checkbox-group">
				<c:forEach var="ingredient" items="${ingredientsList}">
					<input type="checkbox" name="ingredientId[]"
						value="${ingredient.ingredientId}"
						id="ingredient${ingredient.ingredientId}">
					<label for="ingredient${ingredient.ingredientId}">${ingredient.ingredientName}</label>
					<br>
				</c:forEach>
			</div>
		</fieldset>

		<fieldset>
			<legend>
				<label for="methodId">조리 방법</label>
			</legend>
			<div class="checkbox-group">
				<c:forEach var="method" items="${methodsList}">
					<input type="checkbox" name="methodId[]" value="${method.methodId}"
						id="method${method.methodId}">
					<label for="method${method.methodId}">${method.methodName}</label>
					<br>
				</c:forEach>
			</div>
		</fieldset>

		<fieldset>
			<legend>
				<label for="situationId">상황</label>
			</legend>
			<div class="checkbox-group">
				<c:forEach var="situation" items="${situationsList}">
					<input type="checkbox" name="situationId[]"
						value="${situation.situationId}"
						id="situation${situation.situationId}">
					<label for="situation${situation.situationId}">${situation.situationName}</label>
					<br>
				</c:forEach>
			</div>
		</fieldset>

		<button type="submit">등록</button>
	</form>
</body>
</html>
