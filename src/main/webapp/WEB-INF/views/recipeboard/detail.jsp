<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>
<html>
<head>
<title>Recipe Detail</title>
<script>
	// Delete 확인 후 요청
	function confirmDelete(recipeBoardId) {
		if (confirm("Are you sure you want to delete this recipe?")) {
			location.href = '${pageContext.request.contextPath}/recipeboard/delete/'
					+ recipeBoardId;
		}
	}
</script>
</head>
<body>
	<h1>${recipeBoard.recipeBoardTitle}</h1>
	<p>Written by Member ID: ${recipeBoard.memberId}</p>

	<p>
		<span class="label">Content:</span> ${recipeBoard.recipeBoardContent}
	</p>
	<img
		src="${pageContext.request.contextPath}/recipeboard/thumbnail/${recipeBoard.recipeBoardId}"
		alt="Thumbnail" width="200" height="200">
	<p>
		<span class="label">Type:</span> ${typeName}
	</p>
	<p>
		<span class="label">Method:</span> ${methodName}
	</p>
	<p>
		<span class="label">Situation:</span> ${situationName}
	</p>

	<p>
		<span class="label">Ingredients:</span>
	</p>
	<ul>
		<c:forEach var="ingredient" items="${ingredients}">
			<li>${ingredient.ingredientName}</li>
		</c:forEach>
	</ul>

	<p>
		<span class="label">Average Rating:</span> ${recipeBoard.avgRating}
	</p>

	<!-- 버튼 영역 -->
	<div>
		<button
			onclick="location.href='${pageContext.request.contextPath}/recipeboard/update/${recipeBoard.recipeBoardId}'">
			Update</button>
		<form
			action="${pageContext.request.contextPath}/recipeboard/delete/${recipeBoard.recipeBoardId}"
			method="post" style="display: inline;">
			<button type="submit"
				onclick="return confirm('Are you sure you want to delete this recipe?');">Delete</button>
		</form>
		<button
			onclick="location.href='${pageContext.request.contextPath}/recipeboard/list'">Back
			to List</button>
	</div>

	<hr>
	<div style="text-align: center;">
		<div id="replies"></div>
	</div>

	<input type="hidden" id="recipeBoardId" value="${recipeBoard.recipeBoardId }">

	<script type="text/javascript">
		$(document)
				.ready(
						function() {
							getAllReply(); // 함수 호출

							$('#btnAdd').click(function() {
								var recipeBoardId = $('#recipeBoardId').val(); // 게시판 번호 데이터
								var memberId = $('#memberId').val(); // 작성자 데이터
								var replyContent = $('#replyContent').val(); // 댓글 내용
								// JS객체 생성
								var obj = {
									'recipeBoardId' : recipeBoardId, // 게시글 ID 전달
									'memberId' : memberId,
									'replyContent' : replyContent
								}
								console.log(obj);

								// $.ajax로 송수신
								$.ajax({
									type : 'POST', // 메서드 타입
									url : '/project/recipeboard/detail', // url
									headers : {// 헤더 정보
										'Content-Type' : 'application/json' // json content-type 설정   
									},
									data : JSON.stringify(obj), // JSON으로 변환
									success : function(result) { // 전송 성공 시 서버에서 result값 전송
										console.log(result);
										if (result == 1) {
											alert('댓글 입력 성공');
											getAllReply(); // 함수 호출
										} else {
											alert('댓글 입력 실패');
										}
									}
								});
							}); // end btn Add.click()

							// 게시판 댓글 전체 가져오기
							function getAllReply() {
								var recipeBoardId = $('#recipeBoardId').val();

								var url = '/project/recipeboard/all/'
										+ recipeBoardId;

								$
										.getJSON(
												url,
												function(data) {
													// data : 서버에서 전송 받은 list 데이터가 저장되어 있음.
													// getJSON()에서 json 데이터는
													// javascript object로 자동 parsing됨.
													console.log(data);

													var list = ''; // 댓글 데이터를 HTML에 표현할 문제열 변수

													// $(컬렉션).each() : 컬렉션 데이터를 반복문으로 꺼내는 함수
													$(data)
															.each(
																	function() {
																		// this : 컬렉션의 각 인덱스 데이터를 의미
																		console
																				.log(this);

																		// 전송된 replyDateCreated는 문자열 형태이므로 날짜 형태로 변환이 필요
																		var replyDateCreated = new Date(
																				this.replyDateCreated)

																		list += '<div class="reply_item">'
																				+ '<pre>'
																				+ '<input type="hidden" id="replyId" value="'+ this.replyId +'">'
																				+ this.memberId
																				+ '&nbsp;&nbsp;' // 공백
																				+ '<input type="text" id="replyContent" value="'+ this.replyContent +'">'
																				+ '&nbsp;&nbsp;'
																				+ replyDateCreated
																				+ '&nbsp;&nbsp;'
																				+ '<button class="btn_update" >수정</button>'
																				+ '<button class="btn_delete" >삭제</button>'
																				+ '</pre>'
																				+ '</div>';
																	}); // end each()

													$('#replies').html(list); // 저장된 데이터를 replies div 표현   
												} // end function()
										); // end getJSON()
							} // end getAllReply()
							// 수정 버튼을 클릭하면 선택된 댓글 수정
							$('#replies')
									.on(
											'click',
											'.reply_item .btn_update',
											function() {
												console.log(this);

												// 선택된 댓글의 replyId, replyContent 값을 저장
												// prevAll() : 선택된 노드 이전에 있는 모든 형제 노드를 접근
												var replyId = $(this).prevAll(
														'#replyId').val();
												var replyContent = $(this)
														.prevAll(
																'#replyContent')
														.val();
												console.log("선택된 댓글 번호 : "
														+ replyId
														+ ", 댓글 내용 : "
														+ replyContent);

												// ajax 요청
												$
														.ajax({
															type : 'PUT',
															url : '/project/recipeboard/'
																	+ replyId,
															headers : {
																'Content-Type' : 'application/json'
															},
															data : replyContent,
															success : function(
																	result) {
																console
																		.log(result);
																if (result == 1) {
																	alert('댓글 수정 성공!');
																	getAllReply();
																}
															}
														});

											}); // end replies.on()   

							// 삭제 버튼을 클릭하면 선택된 댓글 삭제
							$('#replies')
									.on(
											'click',
											'.reply_item .btn_delete',
											function() {
												console.log(this);
												var recipeBoardId = $(
														"#recipeBoardId").val(); // 게시판 번호 데이터
												var replyId = $(this).prevAll(
														'#replyId').val(); // 댓글 번호 데이터

												$
														.ajax({
															type : 'DELETE',
															url : '/project/recipeboard/'
																	+ replyId
																	+ '/'
																	+ recipeBoardId,
															headers : {
																'content-Type' : 'application/json'
															},
															success : function(
																	result) {
																console
																		.log(result);
																if (result == 1) {
																	alert('댓글 삭제 성공!');
																	getAllReply();
																}
															}
														});
											}); // end replies.on
						}); // end document()
	</script>

</body>
</html>


