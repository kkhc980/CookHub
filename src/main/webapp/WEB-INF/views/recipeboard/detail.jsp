<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://www.springframework.org/security/tags"
	prefix="sec"%>

<!DOCTYPE html>
<html>
<head>
	<!-- jquery 라이브러리 import -->
	<base href="${pageContext.request.contextPath}/">
	<script src="https://code.jquery.com/jquery-3.7.1.js"></script>
	<meta charset="UTF-8">
	
	
	<!-- css 파일 불러오기 -->
	<link rel="stylesheet"
		href="${pageContext.request.contextPath }/resources/css/image.css">
	
	<!-- CSRF 토큰 추가 -->
	<meta name="_csrf" content="${_csrf.token}" />
	<meta name="_csrf_header" content="${_csrf.headerName}" />

    <!-- 현재 로그인한 사용자 ID를 JavaScript에서 사용 가능하도록 전달 -->
    <sec:authorize access="isAuthenticated()">
        <sec:authentication var="customUser" property="principal"/>
        <meta name="member-id" content="${customUser.memberVO.memberId}">
    </sec:authorize>
    
    
<title>${recipeBoard.recipeBoardTitle }</title>

<style>
.star-rating {
	display: inline-block;
	direction: rtl; /* 별을 오른쪽부터 채우도록 설정 */
	font-size: 20px; /* 별 크기 */
	color: lightgray;
}

.star-rating input[type="radio"] {
	display: none;
}

.star-rating label {
	cursor: pointer;
}

.star-rating label:before {
	content: '★';
	display: inline-block;
	transition: color 0.2s;
}

.star-rating input[type="radio"]:checked ~ label:before {
	color: gold;
}

.star-rating label:hover:before, .star-rating label:hover ~ label:before
	{
	color: gold;
}

.hashtags {
	margin-top: 20px;
}

.hashtag-button {
	display: inline-block;
	background-color: #4CAF50;
	color: white;
	border: none;
	padding: 7px 15px;
	margin: 5px;
	border-radius: 15px;
	font-size: 14px;
	cursor: pointer;
}

.hashtag-button:hover {
	background-color: #45a049;
}

.thumbnail {
	max-width: 200px;
	max-height: 200px;
	border: 1px solid #ddd;
	border-radius: 5px;
	object-fit: cover;
}
</style>

</head>
<body>
	<h2>글 보기</h2>

	<div>
		<p>제목 :</p>
		<p>${recipeBoard.recipeBoardTitle }</p>
	</div>
	<div>
		<p>Thumbnail:</p>
		<img
			src="${pageContext.request.contextPath}/recipeboard/thumbnail/${recipeBoard.recipeBoardId}"
			alt="Thumbnail" class="thumbnail">
	</div>

<!-- 작성자 버튼 -->
<div>
    <p>작성자 :</p>
    <button class="btn btn-link p-0 follow-btn" data-member-id="${recipeBoard.memberId}">
        ${recipeBoard.memberId}
    </button>
</div>

<!-- 팔로우 모달 창 -->
<div id="followPopup" class="modal fade" tabindex="-1" role="dialog">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
                <h5 class="modal-title">팔로우 정보</h5>
            </div>
            <div class="modal-body">
                <p><strong>작성자 ID:</strong> <span id="popupMemberId"></span></p>
                <button id="followActionBtn" class="btn btn-primary">팔로우</button>
            </div>
        </div>
    </div>
</div>

<!-- jQuery 및 Bootstrap JS 로드 -->
<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.5.2/dist/js/bootstrap.min.js"></script>

<script>
$(document).ready(function() {
    console.log("📌 페이지 로드됨");

    var contextPath = "${pageContext.request.contextPath}";

    // ✅ 현재 로그인한 사용자 ID 가져오기
    var currentUserId = $("meta[name='member-id']").attr("content");

    if (!currentUserId || currentUserId === "null" || currentUserId === "") {
        console.log("⚠️ 로그인되지 않은 사용자. 팔로우 기능 비활성화.");
        return;
    }
    console.log("📌 현재 로그인된 사용자 ID:", currentUserId);

    let followingId = null;

    // CSRF 토큰 설정
    var csrfToken = $('meta[name="_csrf"]').attr('content');
    var csrfHeader = $('meta[name="_csrf_header"]').attr('content');

    $.ajaxSetup({
        beforeSend: function(xhr) {
            xhr.setRequestHeader(csrfHeader, csrfToken);
        }
    });

    // 팔로우 버튼 클릭 시 팝업 띄우기
    $(document).on("click", ".follow-btn", function() {
        followingId = $(this).data("member-id");
        $("#popupMemberId").text(followingId);
        checkFollowingStatus(currentUserId, followingId);
        $("#followPopup").modal("show");
    });

    // 팔로우/언팔로우 버튼 클릭 이벤트
    $(document).on("click", "#followActionBtn", function() {
        if ($(this).text() === "팔로우") {
            followUser(currentUserId, followingId);
        } else {
            unfollowUser(currentUserId, followingId);
        }
    });

    // ✅ 팔로우 여부 확인 (GET 요청)
    function checkFollowingStatus(followerId, followingId) {
        $.ajax({
            url: contextPath + "/follow/is-following/" + followingId,
            type: "GET",
            success: function(response) {
                if (response) {
                    $("#followActionBtn").text("언팔로우").removeClass("btn-primary").addClass("btn-danger");
                } else {
                    $("#followActionBtn").text("팔로우").removeClass("btn-danger").addClass("btn-primary");
                }
            },
            error: function(xhr, status, error) {
                console.error("❌ 팔로우 상태 확인 실패:", error);
            }
        });
    }

 // ✅ 팔로우 요청 (POST 요청) + 알림 추가
    function followUser(followerId, followingId) {
        $.ajax({
            url: contextPath + "/follow/" + followingId,
            type: "POST",
            success: function() {
                $("#followActionBtn").text("언팔로우").removeClass("btn-primary").addClass("btn-danger");
                console.log("✅ 팔로우 성공");

                // 팔로우 성공 시 알림 생성 요청
                sendFollowNotification(followerId, followingId);
            },
            error: function(xhr, status, error) {
                console.error("❌ 팔로우 요청 실패:", error);
            }
        });
    }

    // ✅ 언팔로우 요청 (DELETE 요청) - URL 파라미터로 followerId 전달
    function unfollowUser(followerId, followingId) {
        $.ajax({
            url: contextPath + "/follow/" + followingId + "?followerId=" + followerId, // 🔹 followerId를 URL 파라미터로 전달
            type: "DELETE",
            success: function() {
                $("#followActionBtn").text("팔로우").removeClass("btn-danger").addClass("btn-primary");
                console.log("✅ 언팔로우 성공");

                // 언팔로우 시 팔로우 알림 삭제
                deleteFollowNotification(followerId, followingId);
            },
            error: function(xhr, status, error) {
                console.error("❌ 언팔로우 요청 실패:", error);
            }
        });
    }


    // ✅ 팔로우 알림 보내기 (POST 요청)
    function sendFollowNotification(senderId, receiverId) {
        $.ajax({
            url: contextPath + "/notifications/follow",
            type: "POST",
            data: { senderId: senderId, receiverId: receiverId }
        });
    }

    // ✅ 팔로우 알림 삭제 (DELETE 요청) - URL 파라미터로 senderId 전달
    function deleteFollowNotification(senderId, receiverId) {
        $.ajax({
            url: contextPath + "/notifications/follow?senderId=" + senderId + "&receiverId=" + receiverId, // 🔹 URL 파라미터로 전달
            type: "DELETE"
        });
    }

});
</script>




		<!-- boardDateCreated 데이터 포멧 변경 -->
	<div>
		<fmt:formatDate value="${recipeBoard.recipeBoardCreatedDate }"
			pattern="yyyy-MM-dd HH:mm:ss" var="recipeBoardCreatedDate" />
		<p>작성일 : ${recipeBoardCreatedDate }</p>
	</div>
	<div>
		<textarea rows="20" cols="120" readonly>${recipeBoard.recipeBoardContent }</textarea>
	</div>

	<div class="info-container">
		<div class="category-section">
			<h3 class="section-title">카테고리</h3>
			<div class="info-item">
				<span class="label">종류:</span> <span>${typeName}</span>
			</div>
			<div class="info-item">
				<span class="label">방법:</span> <span>${methodName}</span>
			</div>
			<div class="info-item">
				<span class="label">상황:</span> <span>${situationName}</span>
			</div>
			<div class="info-item">
				<span class="label">재료:</span>
				<ul>
					<c:forEach var="ingredient" items="${ingredients}">
						<li>${ingredient.ingredientName}</li>
					</c:forEach>
				</ul>
			</div>
		</div>
		<div class="recipe-info-section">
			<h3 class="section-title">요리 정보</h3>
			<div class="info-item">
				<span class="label">인분:</span> <span>${recipeBoard.servings}</span>
			</div>
			<div class="info-item">
				<span class="label">시간:</span> <span>${recipeBoard.time}</span>
			</div>
			<div class="info-item">
				<span class="label">난이도:</span> <span>${recipeBoard.difficulty}</span>
			</div>
		</div>
	</div>
	<div>
		<h3 class="section-title">재료 상세 정보</h3>
		<c:forEach var="ingredientDetail" items="${ingredientDetails}">
			<div class="ingredient-detail-row">
				<span class="label">이름:</span> <span>${ingredientDetail.ingredientName}</span>
				<span class="label">수량:</span> <span>${ingredientDetail.ingredientAmount}</span>
				<span class="label">단위:</span> <span>${ingredientDetail.ingredientUnit}</span>
				<c:if test="${not empty ingredientDetail.ingredientNote}">
					<span class="label">비고:</span>
					<span>${ingredientDetail.ingredientNote}</span>
				</c:if>
			</div>
		</c:forEach>
	</div>
	<!-- 해시태그 표시 -->
	<div class="hashtags">
		<h3>Hashtags:</h3>
		<c:forEach var="hashtag" items="${hashtags}">
			<button class="hashtag-button" data-hashtag="${hashtag.hashtagName}">
				#${hashtag.hashtagName}</button>
		</c:forEach>
	</div>
	<div>
		<h3 class="section-title">요리 순서</h3>
		<c:forEach var="step" items="${steps}" varStatus="status">
			<div class="step-row">
				<label class="step-label">Step ${status.index + 1}</label>
				<textarea rows="3" readonly>${step.stepDescription}</textarea>
				<c:if test="${step.stepImageUrl != null}">
					<img
						src="${pageContext.request.contextPath}/uploads/${step.stepImageUrl}"
						alt="요리 단계 이미지" />
				</c:if>
			</div>
		</c:forEach>
	</div>
	<button onclick="location.href='recipeboard/list'">글 목록</button>

	<!-- 글 수정/삭제 버튼 -->
	<sec:authorize access="isAuthenticated()">
		<sec:authentication var="customUser" property="principal" />
		<c:if test="${recipeBoard.memberId == customUser.memberVO.memberId}">
			<button
				onclick="location.href='recipeboard/update/${recipeBoard.recipeBoardId}'">글
				수정</button>
			<button type="button" id="deleteBoard">글 삭제</button>
		</c:if>
	</sec:authorize>

	<div>
		<button id="like-button">좋아요</button>
		<span id="like-count">0</span>
	</div>

	<form id="deleteForm"
		action="recipeboard/delete/${recipeBoard.recipeBoardId}" method="POST">

		<input type="hidden" name="${_csrf.parameterName}"
			value="${_csrf.token}"> <input type="hidden"
			name="recipeBoardId" value="${recipeBoard.recipeBoardId}">
	</form>

	<script type="text/javascript">
		$(document)
				.ready(
						function() {
							$('#deleteBoard').click(function() {
								if (confirm('삭제하시겠습니까?')) {
									$('#deleteForm').submit(); // form 데이터 전송
								}
							});

							$(".hashtag-button")
									.click(
											function() {
												var hashtag = $(this).data(
														"hashtag");
												var url = "${pageContext.request.contextPath}/recipeboard/list?hashtag="
														+ encodeURIComponent(hashtag);
												window.location.href = url;
											});
						}); // end document
	</script>



	<input type="hidden" id="recipeBoardId"
		value="${recipeBoard.recipeBoardId }">

	<h2>댓글</h2>
	<sec:authorize access="isAuthenticated()">
		<div style="text-align: left;">
			<sec:authentication var="customUser" property="principal" />
			<c:set var="loggedInMemberId" value="${customUser.memberVO.memberId}" />
		</div>
	</sec:authorize>

	<div style="text-align: left;">
		<!-- memberId를 입력하는 대신 span 태그로 표시 -->
		<span id="loggedInMemberId">${loggedInMemberId}</span>
		<!-- hidden 필드로 memberId 전송 -->
		<input type="hidden" id="memberId" value="${loggedInMemberId}">

		<input type="text" id="replyContent" maxlength="150"
			placeholder="댓글을 입력하세요">
		<button id="btnAdd">댓글 작성</button>
	</div>

	<hr>
	<div id="replies"></div>
	<hr>

	<h2>리뷰</h2>


	<div style="text-align: left;">
		<span id="loggedInReviewMemberId">${loggedInMemberId}</span> <input
			type="hidden" id="reviewMemberId" value="${loggedInMemberId}">
		<input type="text" id="recipeReviewContent" placeholder="리뷰 내용을 입력하세요">

		<span class="star-rating"> <input type="radio"
			name="reviewRating" id="star1" value="1"><label for="star1"></label>
			<input type="radio" name="reviewRating" id="star2" value="2"><label
			for="star2"></label> <input type="radio" name="reviewRating"
			id="star3" value="3"><label for="star3"></label> <input
			type="radio" name="reviewRating" id="star4" value="4"><label
			for="star4"></label> <input type="radio" name="reviewRating"
			id="star5" value="5"><label for="star5"></label>
		</span>

		<button id="btnReviewAdd">리뷰 작성</button>
	</div>

	<hr>
	<div id="reviews"></div>

	<div class="image-upload">

		<div class="image-drop">drag - image</div>
	</div>

	<div class="reviewAttachDTOImg-list"></div>

	<button id="btnReviewAdd">리뷰 작성</button>

	<script src="${pageContext.request.contextPath }/resources/js/image.js"></script>


	<hr>
	<div style="text-align: left;">
		<div id="reviews"></div>
	</div>

	<script type="text/javascript">
		$(document)
				.ready(
						function() {
							var contextRoot = '/project';
							// CSRF 토큰 설정
							var csrfToken = $('meta[name="_csrf"]').attr(
									'content');
							var csrfHeader = $('meta[name="_csrf_header"]')
									.attr('content');

							getAllReply(); // reply 함수 호출
							getAllRecipeReview(); // review 함수 호출

							// AJAX 전역 설정: 모든 요청에 CSRF 토큰 추가
							$
									.ajaxSetup({
										beforeSend : function(xhr) {
											xhr.setRequestHeader(csrfHeader,
													csrfToken); // CSRF 토큰을 헤더에 추가
										}
									});

							var isLoggedIn = false;

							// 좋아요 초기 상태 가져오기
							function loadLikeStatus() {
								var recipeBoardId = $('#recipeBoardId').val(); // 게시글 ID
								$.get(contextRoot + '/recipeboard/'
										+ recipeBoardId + '/like-count',
										function(response) {
											$('#like-count').text(
													response.likeCount); // 좋아요 개수 업데이트
										});

								// 로그인한 사용자의 좋아요 여부 확인
								$.get(
										contextRoot + '/recipeboard/'
												+ recipeBoardId
												+ '/like-status',
										function(response) {
											isLoggedIn = true; // 로그인 확인
											if (response.liked) {
												$('#like-button')
														.text('좋아요 취소');
											} else {
												$('#like-button').text('좋아요');
											}
										}).fail(function(xhr) {
									if (xhr.status === 403) {
										console.warn('로그인이 필요합니다.'); // 경고만 출력, 네트워크 에러 발생 안함
										isLoggedIn = false;
										$('#like-button').text('로그인 후 사용 가능');
									}
								});
							}

							// 좋아요 버튼 클릭 이벤트
							$('#like-button')
									.click(
											function() {
												var recipeBoardId = $(
														'#recipeBoardId').val(); // 게시글 ID
												$
														.ajax({
															type : 'POST',
															url : contextRoot
																	+ '/recipeboard/'
																	+ recipeBoardId
																	+ '/like', // 좋아요 토글 API
															success : function(
																	response) {
																if (response.message === "로그인이 필요한 서비스입니다.") {
																	alert(response.message); // 로그인 필요 메시지 표시
																} else {
																	if (response.liked) {
																		$(
																				'#like-button')
																				.text(
																						'좋아요 취소'); // 버튼 텍스트 변경
																		alert('좋아요가 설정되었습니다.');
																	} else {
																		$(
																				'#like-button')
																				.text(
																						'좋아요'); // 버튼 텍스트 변경
																		alert('좋아요가 취소되었습니다.');
																	}
																}
																$('#like-count')
																		.text(
																				response.likeCount); // 좋아요 개수 업데이트
															}
														});
											});

							loadLikeStatus();

							$('#btnAdd')
									.click(
											function() {
												var recipeBoardId = $(
														'#recipeBoardId').val(); // 게시판 번호 데이터
												var memberId = $('#memberId')
														.val();
												var replyContent = $(
														'#replyContent').val(); // 댓글 내용
												// JS객체 생성
												var obj = {
													'recipeBoardId' : recipeBoardId, // 게시글 ID 전달
													'memberId' : memberId,
													'replyContent' : replyContent
												}

												// $.ajax로 송수신
												$
														.ajax({
															type : 'POST', // 메서드 타입
															url : '/project/recipeboard/replies/detail', // url
															headers : {// 헤더 정보
																'Content-Type' : 'application/json' // json content-type 설정 
															},
															data : JSON
																	.stringify(obj), // JSON으로 변환 
															success : function(
																	result) { // 전송 성공 시 서버에서 result값 전송
																console
																		.log(result);
																if (result == 1) {
																	alert('댓글 입력 성공');
																	getAllReply(); // 함수 호출
																} else {
																	alert('댓글 입력 실패');
																}
															}

														});
											}); // end btn Add.click()

							$('#btnReviewAdd')
									.click(
											function() {
												var recipeBoardId = $(
														'#recipeBoardId').val();
												var memberId = $(
														'#reviewMemberId')
														.val();
												var recipeReviewContent = $(
														'#recipeReviewContent')
														.val();

												// 입력된 별점 값을 반전 (1 -> 5, 2 -> 4, ...)
												var reviewRating = $(
														"input[name='reviewRating']:checked")
														.val();
												reviewRating = 6 - parseInt(
														reviewRating, 10); // RTL에 따른 별점 값 반전

												if (!reviewRating) {
													alert('0점 이외의 별점을 입력하세요');
													return;
												}

												// hidden input에서 reviewAttachDTO 값 가져오기
												var reviewAttachDTOs = [];
												$(
														"input[type='hidden'][name='reviewAttachDTO']")
														.each(
																function() {
																	var attachData = JSON
																			.parse($(
																					this)
																					.val()); // JSON 파싱
																	reviewAttachDTOs
																			.push(attachData);
																});

												var obj = {
													'recipeBoardId' : recipeBoardId,
													'memberId' : memberId,
													'recipeReviewContent' : recipeReviewContent,
													'reviewRating' : reviewRating

												};

												if (reviewAttachDTOs.length > 0) {
													obj.reviewAttachList = reviewAttachDTOs;
												} else {
													obj.reviewAttachList = [];
												}

												console.log("전송 데이터:", obj);

												$
														.ajax({
															type : 'POST',
															url : '/project/recipeboard/reviews/detail',
															headers : {
																'Content-Type' : 'application/json'
															},
															data : JSON
																	.stringify(obj),
															success : function(
																	result) {
																console
																		.log(result);
																if (result == 1) {
																	alert('리뷰 입력 성공');
																	getAllRecipeReview();

																} else {
																	alert('리뷰 입력 실패');
																}
															}
														});
											});

							// 게시판 댓글 전체 가져오기
							function getAllReply() {
								var recipeBoardId = $('#recipeBoardId').val();
								var url = '/project/recipeboard/all/'
										+ recipeBoardId;

								$
										.getJSON(
												url,
												function(data) {
													console.log(data);

													var list = '';

													$(data)
															.each(
																	function() {
																		console
																				.log(this);
																		var replyDateCreated = new Date(
																				this.replyDateCreated);

																		list += '<div class="reply_item">'
																				+ '<pre>'
																				+ '<input type="hidden" id="replyId" value="' + this.replyId + '">'
																				+ this.memberId
																				+ '  '
																				+ '<span class="replyContentDisplay" data-reply-id="' + this.replyId + '">'
																				+ this.replyContent
																				+ '</span>'
																				+ // 텍스트로 출력, data 속성 추가
																				'  '
																				+ replyDateCreated
																				+ '  '
																				+ '<button class="btn_update" data-reply-id="' + this.replyId + '">수정</button>'
																				+ // data 속성 추가
																				'<button class="btn_delete" data-reply-id="' + this.replyId + '">삭제</button>'
																				+ // data 속성 추가
																				'</pre>'
																				+ '</div>';
																	});

													$('#replies').html(list);

													// 수정 버튼 클릭 이벤트
													$('#replies')
															.on(
																	'click',
																	'.btn_update',
																	function() {
																		var replyId = $(
																				this)
																				.data(
																						'reply-id'); // data 속성에서 replyId 가져오기
																		var replyContentSpan = $('.replyContentDisplay[data-reply-id="'
																				+ replyId
																				+ '"]'); // 수정할 span 요소 선택
																		var currentContent = replyContentSpan
																				.text(); // 기존 텍스트 내용 가져오기

																		// span 요소를 text input으로 변경
																		replyContentSpan
																				.replaceWith('<input type="text" class="replyContentInput" data-reply-id="' + replyId + '" value="' + currentContent + '">');

																		// 수정 완료 버튼으로 변경
																		$(this)
																				.replaceWith(
																						'<button class="btn_update_complete" data-reply-id="' + replyId + '">수정 완료</button>');

																		// 수정 완료 버튼 클릭 이벤트
																		$(
																				'#replies')
																				.off(
																						'click',
																						'.btn_update_complete')
																				.on(
																						'click',
																						'.btn_update_complete',
																						function() {

																							var replyId = $(
																									this)
																									.data(
																											'reply-id');
																							var replyContentInput = $('.replyContentInput[data-reply-id="'
																									+ replyId
																									+ '"]');
																							var updatedReplyContent = replyContentInput
																									.val();

																							console
																									.log("replyId : "
																											+ replyId
																											+ ", 수정할 내용 : "
																											+ updatedReplyContent);

																							$
																									.ajax({
																										url : '/project/recipeboard/replies/'
																												+ replyId,
																										type : 'PUT',
																										dataType : "json",
																										ContentType : 'application/json',
																										data : JSON
																												.stringify({
																													replyId : replyId,
																													replyContent : updatedReplyContent
																												}),
																										success : function(
																												result) {
																											console
																													.log(result);
																											if (result == 1) {
																												alert('댓글 수정 성공!');
																												getAllReply();
																											}
																										},
																										error : function(
																												xhr,
																												status,
																												error) {
																											console
																													.error(
																															"댓글 수정 실패 :",
																															error);
																										}
																									});

																						});

																	});
												});
							}

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
															dataType : "json",
															url : '/project/recipeboard/replies/'
																	+ replyId
																	+ '/'
																	+ recipeBoardId,
															headers : {
																'Content-Type' : 'application/json'
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

							// 리뷰 전체 불러오기
							function getAllRecipeReview() {
								var recipeBoardId = $('#recipeBoardId').val();
								var url = '/project/recipeboard/allReviews/'
										+ recipeBoardId;
								$
										.getJSON(
												url,
												function(data) {
													console
															.log("리뷰 데이터:",
																	data);
													var list = '';
													$(data)
															.each(
																	function() {
																		console
																				.log(
																						"별점 값:",
																						this.reviewRating);
																		recipeReviewDateCreated = new Date(
																				this.recipeReviewDateCreated)

																		var starRatingHTML = '';
																		for (let i = 1; i <= 5; i++) {
																			if (i <= this.reviewRating) {
																				starRatingHTML += '<span style="color:gold;">★</span>'; // 채워진 별
																			} else {
																				starRatingHTML += '<span style="color:lightgray;">★</span>'; // 빈 별
																			}
																		}

																		// 이미지 표시 HTML 생성
																		var imageHTML = '';
																		if (this.reviewAttachList
																				&& this.reviewAttachList.length > 0) {
																			this.reviewAttachList
																					.forEach(function(
																							reviewAttach) {
																						var imageUrl = "/project/image/display?attachPath="
																								+ encodeURIComponent(reviewAttach.attachPath)
																								+ "&attachChgName="
																								+ encodeURIComponent(reviewAttach.attachChgName)
																								+ "&attachExtension="
																								+ encodeURIComponent(reviewAttach.attachExtension);

																						imageHTML += '<div class="image_item" data-chgName="'+ reviewAttach.attachChgName +'">'
																								+ '<a href="' + imageUrl + '" target="_blank">'
																								+ '<img width="100px" height="100px" src="' + imageUrl + '" />'
																								+ '</a>'
																								+ '</div>';
																					});
																		}

																		list += '<div class="review_item">'
																				+ '<pre>'
																				+ '<input type="hidden" id="recipeReviewId" value="' + this.recipeReviewId + '">'
																				+ this.memberId
																				+ '&nbsp;&nbsp;'
																				+ '<input type="text" id="recipeReviewContent" value="' + this.recipeReviewContent + '">'
																				+ '&nbsp;&nbsp;'
																				+ '<br>'
																				+ '<span style="font-size: 1.2em;">'
																				+ starRatingHTML
																				+ '</span>' // 별점 추가
																				+ '&nbsp;&nbsp;'
																				+ recipeReviewDateCreated
																				+ '&nbsp;&nbsp;'
																				//          + '<button class="btn_review_update" >수정</button>'
																				+ '<button class="btn_review_delete" >삭제</button>'

																		// 이미지가 있는 경우만 추가
																		if (imageHTML !== '') {
																			list += '<div class="review_images image-list">'
																					+ imageHTML
																					+ '</div>';
																		}

																		list += '</pre>'
																				+ '</div>';

																	});
													$('#reviews').html(list);
												});

							}

							// 수정 버튼을 클릭하면 선택된 댓글 수정
							$('#reviews')
									.on(
											'click',
											'.review_item .btn_review_update',
											function() {
												console.log(this);

												// 선택된 리뷰의 replyId, replyContent 값을 저장
												// prevAll() : 선택된 노드 이전에 있는 모든 형제 노드를 접근
												var recipeReviewId = $(this)
														.prevAll(
																'#recipeReviewId')
														.val();
												var recipeReviewContent = $(
														this).prevAll(
														'#recipeReviewContent')
														.val();
												console.log("선택된 리뷰 번호 : "
														+ recipeReviewId
														+ ", 댓글 내용 : "
														+ recipeReviewContent
														+ ", 리뷰 별점 : "
														+ reviewRating);

												// ajax 요청
												$
														.ajax({
															type : 'PUT',
															dataType : "json",
															url : '/project/recipeboard/reviews/'
																	+ recipeReviewId,
															headers : {
																'Content-Type' : 'application/json'
															},
															data : JSON
																	.stringify(obj),
															success : function(
																	result) {
																console
																		.log(result);
																if (result == 1) {
																	alert('리뷰 수정 성공!');
																	getAllRecipeReview();
																}
															}
														});

											}); // end review.on()   

							// 삭제 버튼을 클릭하면 선택된 리뷰 삭제
							$('#reviews')
									.on(
											'click',
											'.review_item .btn_review_delete',
											function() {
												console.log(this);
												var recipeBoardId = $(
														"#recipeBoardId").val(); // 게시판 번호 데이터
												var recipeReviewId = $(this)
														.prevAll(
																'#recipeReviewId')
														.val(); // 댓글 번호 데이터

												$
														.ajax({
															type : 'DELETE',
															dataType : "json",
															url : '/project/recipeboard/reviews/'
																	+ recipeReviewId
																	+ '/'
																	+ recipeBoardId,
															headers : {
																'Content-Type' : 'application/json'
															},
															success : function(
																	result) {
																console
																		.log(result);
																if (result == 1) {
																	alert('리뷰 삭제 성공!');
																	getAllRecipeReview();
																}
															}
														});
											}); // end reviews.on
						}); // end document()
	</script>
</body>
</html>