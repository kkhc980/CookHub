<%@ page language="java" contentType="text/html; charset=UTF-8"
   pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
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
   
   <link rel="stylesheet" 
  		href="${pageContext.request.contextPath }/resources/css/nestedreply.css"> <!-- CSS 링크 추가 -->
  		
   <link rel="stylesheet"
        href="${pageContext.request.contextPath }/resources/css/recipedetail.css">
   
   <link rel="stylesheet"
        href="${pageContext.request.contextPath }/resources/css/reviews.css">   
        
   <link rel="stylesheet"
        href="${pageContext.request.contextPath }/resources/css/review-edit.css"> 
        
   <link rel="stylesheet"
        href="${pageContext.request.contextPath }/resources/css/detail-components.css">
        
   <link rel="stylesheet"
        href="${pageContext.request.contextPath }/resources/css/follow.css">  
   
   <link rel="stylesheet"
        href="${pageContext.request.contextPath }/resources/css/reply.css">          		
   
   <!-- CSRF 토큰 추가 -->
   <meta name="_csrf" content="${_csrf.token}" />
   <meta name="_csrf_header" content="${_csrf.headerName}" />


<!-- 현재 로그인한 사용자 ID를 JavaScript에서 사용 가능하도록 전달 -->
    <sec:authorize access="isAuthenticated()">
        <sec:authentication var="customUser" property="principal"/>
        <meta name="member-id" content="${customUser.memberVO.memberId}">
    </sec:authorize>

<title>${recipeBoard.recipeBoardTitle }</title>

</head>
<body>

<div class="container">
   <h2>글 보기</h2>

   <div>
      <p>제목 :</p>
      <p>${recipeBoard.recipeBoardTitle }</p>
   </div>
	<div>
	    <p>Thumbnail:</p>
	    <div class="thumbnail-container">
	        <img class="thumbnail"
	             src="${pageContext.request.contextPath}/recipeboard/thumbnail/${recipeBoard.recipeBoardId}"
	             alt="Thumbnail"
	             onerror="this.style.display='none'; this.nextElementSibling.style.display='block';">
	        <span class="no-image-text">이미지 없음</span>
	    </div>
	</div>


<!-- 작성자 버튼 -->
<div class="author-info">
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
            	<h5 class="modal-title"></h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
                
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
   
   <div class="return-list-btn-container">
   <button class="return-list-btn" onclick="location.href='recipeboard/list'">글 목록</button>
   </div>
   
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

	<div class="likeContainer">
      <button id="like-button"> </button>
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
<div class="comment-review-container">

  <h2>댓글 (<span id="replyTotalCount">0</span>)</h2>

  <sec:authorize access="isAuthenticated()">
    <div>
      <sec:authentication var="customUser" property="principal" />
      <c:set var="loggedInMemberId" value="${customUser.memberVO.memberId}" />
    </div>
  </sec:authorize>

  <c:if test="${not empty loggedInMemberId}">
    <div class="input-area">
      <span id="loggedInMemberId"></span>
      <input type="hidden" id="memberId" value="${loggedInMemberId}">
      <textarea id="replyContent" maxlength="150" placeholder="댓글을 입력하세요"></textarea>
      <button id="btnAdd" class="add-button">등록</button>
    </div>
  </c:if>
  
  <div id="replies"></div>
  <div id="pagination"></div>
  <hr>

  <!-- Modal Structure -->
  <sec:authorize access="isAuthenticated()">
    <div id="nestedReplyModal" class="nestedReplyModal">
      <div class="nested-reply-modal-content">
        <span class="nested-close">×</span>
        <h5>답글</h5>
        <textarea id="nestedReplyContent" class="form-control" placeholder="답글 내용을 입력하세요."></textarea>
        <input type="hidden" id="parentReplyId">
        <input type="hidden" id="nestedReplyMemberId" value="${loggedInMemberId}">
        <button type="button" id="submitNestedReply">등록</button>
      </div>
    </div>
  </sec:authorize>
  
  <h2>리뷰 (<span id="reviewTotalCount">0</span>)</h2>

<c:if test="${not empty loggedInMemberId}">
 
    <div class="review-input-area">
    <div class="review-content-container">
    <div class="image-drop">drag - image</div>
    <div class="reviewAttachDTOImg-list"></div>
      <span id="loggedInReviewMemberId"></span>
      <input type="hidden" id="reviewMemberId" value="${loggedInMemberId}">
      <textarea id="recipeReviewContent" placeholder="리뷰 내용을 입력하세요"></textarea>
	  <button id="btnReviewAdd" class="review-add-button">등록</button>
      </div>
  	  	<span class="star-rating">
	        <input type="radio" name="reviewRating" id="star1" value="1"><label for="star1"></label>
	        <input type="radio" name="reviewRating" id="star2" value="2"><label for="star2"></label>
	        <input type="radio" name="reviewRating" id="star3" value="3"><label for="star3"></label>
	        <input type="radio" name="reviewRating" id="star4" value="4"><label for="star4"></label>
	        <input type="radio" name="reviewRating" id="star5" value="5"><label for="star5"></label>
	  	  </span>
	  </div>
  </c:if>

  <div id="reviews">
    <c:forEach var="review" items="${reviews}">
      <div class="recipeReview_item" data-review-id="${review.recipeReviewId}">
        <p class="recipeReviewContentDisplay">${review.recipeReviewContent}</p>
        <span class="starRatingDisplay">
          <c:forEach begin="1" end="${review.reviewRating}">
            <span style="color:gold;">⭐</span>
          </c:forEach>
        </span>
        <button class="btn_review_update" data-review-id="${review.recipeReviewId}">수정</button>
        <button class="btn_review_delete" data-review-id="${review.recipeReviewId}">삭제</button>
      </div>
    </c:forEach>
  </div>

  <script src="${pageContext.request.contextPath}/resources/js/image.js"></script>
  <div>
    <div id="reviewPagination"></div>
  </div>

  <hr>
  </div>
 
</div>
  
   <script type="text/javascript">
   
      $(document)
            .ready(
                  function() {
                    var contextRoot = '/project';
                     // CSRF 토큰 설정
                     var csrfToken = $('meta[name="_csrf"]').attr('content');
                     var csrfHeader = $('meta[name="_csrf_header"]').attr('content');
                     
                     getAllReply(); // reply 함수 호출
                     getAllRecipeReview(); // review 함수 호출
                      
                     // AJAX 전역 설정: 모든 요청에 CSRF 토큰 추가
                     $.ajaxSetup({
                         beforeSend: function (xhr) {
                             xhr.setRequestHeader(csrfHeader, csrfToken); // CSRF 토큰을 헤더에 추가
                         }
                     });
                     
                     var isLoggedIn = false;
                     
                    // 좋아요 초기 상태 가져오기
                     function loadLikeStatus() {
                         var recipeBoardId = $('#recipeBoardId').val(); // 게시글 ID
                         $.get(contextRoot + '/recipeboard/' + recipeBoardId + '/like-count', function (response) {
                             $('#like-count').text(response.likeCount); // 좋아요 개수 업데이트
                         });
                         
                      // 로그인한 사용자의 좋아요 여부 확인
                         $.get(contextRoot + '/recipeboard/' + recipeBoardId + '/like-status', function(response) {
                            isLoggedIn = true; // 로그인 확인
                            if (response.liked) {
                               $('#like-button').addClass('liked');
                            } else {
                               $('#like-button').removeClass('liked');
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
                     $('#like-button').click(function () {
                var recipeBoardId = $('#recipeBoardId').val(); // 게시글 ID
                $.ajax({
                    type: 'POST',
                    url: contextRoot + '/recipeboard/' + recipeBoardId + '/like', // 좋아요 토글 API
                    success: function (response) {
                        if (response.message === "로그인이 필요한 서비스입니다.") {
                            alert(response.message); // 로그인 필요 메시지 표시
                        } else {
                            if (response.liked) {
                                $('#like-button').addClass('liked'); // 버튼 텍스트 변경
                                alert('좋아요가 설정되었습니다.');
                            } else {
                                $('#like-button').removeClass('liked'); // 버튼 텍스트 변경
                                alert('좋아요가 취소되었습니다.');
                            }
                        }
                        $('#like-count').text(response.likeCount); // 좋아요 개수 업데이트
                    }
                });
            });
                     
                     loadLikeStatus();                                        
                          
                     $('#btnAdd').click(function() {
                         var recipeBoardId = $('#recipeBoardId').val(); // 게시판 번호 데이터
                         var memberId = $('#memberId').val();
                         var replyContent = $('#replyContent').val(); // 댓글 내용
                         
                         if (!memberId || memberId.trim() === "") {
                             alert("로그인이 필요한 서비스입니다.");
                             return;
                         }
                         
                         if (!replyContent.trim()) {
                             alert("댓글을 입력해주세요.");
                             return;
                         }
                         
                         // JS객체 생성
                         var obj = {
                            'recipeBoardId' : recipeBoardId, // 게시글 ID 전달
                            'memberId' : memberId,
                            'replyContent' : replyContent
                         }
                         
                         // $.ajax로 송수신
                         $.ajax({
                            type : 'POST', // 메서드 타입
                            url : '/project/recipeboard/replies/detail', // url
                            headers : {// 헤더 정보
                              'Content-Type' : 'application/json' // json content-type 설정 
                            },
                             data : JSON.stringify(obj), // JSON으로 변환 
                            success : function(result) { // 전송 성공 시 서버에서 result값 전송
                               console.log(result);
                               if (result == 1) {
                                  alert('댓글 입력 성공');
                                  getAllReply(1); // 함수 호출
                               } else {
                                  alert('댓글 입력 실패');
                               }
                            }
                        
                         });
                      }); // end btn Add.click()
                  
                      
                      $('#btnReviewAdd').click(function() {
                          var recipeBoardId = $('#recipeBoardId').val();
                          var memberId = $('#reviewMemberId').val();
                          var recipeReviewContent = $('#recipeReviewContent').val();

                          // 입력된 별점 값을 반전 (1 -> 5, 2 -> 4, ...)
                          var reviewRating = $("input[name='reviewRating']:checked").val();
                          reviewRating = 6 - parseInt(reviewRating, 10); // RTL에 따른 별점 값 반전
                     
                          if (!memberId || memberId.trim() === "") {
                              alert("로그인이 필요한 서비스입니다.");
                              return;
                          }

                          if (!recipeReviewContent.trim()) {
                              alert("리뷰 내용을 입력해주세요.");
                              return;
                          }
                          
                          if (!reviewRating) {
                              alert('0점 이외의 별점을 입력하세요');
                              return;
                          }                                           
                          
                          // hidden input에서 reviewAttachDTO 값 가져오기
                          var reviewAttachDTOs = [];
                          $("input[type='hidden'][name='reviewAttachDTO']").each(function() {
                              var attachData = JSON.parse($(this).val()); // JSON 파싱
                              
                           	  // 이미지 정보 중복 등록 방지
                              if (!reviewAttachDTOs.some(dto => dto.attachChgName === attachData.attachChgName)) {
                            	  reviewAttachDTOs.push(attachData);
                              }
                          });
                          
                          var obj = {
                              'recipeBoardId': recipeBoardId,
                              'memberId': memberId,
                              'recipeReviewContent': recipeReviewContent,
                              'reviewRating': reviewRating,
                              'reviewAttachList': reviewAttachDTOs
                           
                          };
                          
                          if (reviewAttachDTOs.length > 0) {
                              obj.reviewAttachList = reviewAttachDTOs;
                          } else {
                              obj.reviewAttachList = [];
                          }
                          
                          
                          console.log("전송 데이터:", obj);

                          $.ajax({
                              type: 'POST',
                              url: '/project/recipeboard/reviews/detail',
                              headers: { 'Content-Type': 'application/json' },
                              data: JSON.stringify(obj),
                              success: function(result) {
                                  console.log(result);
                                  if (result == 1) {
                                      alert('리뷰 입력 성공');
                                      getAllRecipeReview();
                                      
                                  } else {
                                      alert('리뷰 입력 실패');
                                  }
                              }
                          });
                      });
                     
                      function escapeHtml(text) {
                           return text
                               .replace(/&/g, "&amp;")
                               .replace(/</g, "&lt;")
                               .replace(/>/g, "&gt;")
                               .replace(/"/g, "&quot;")
                               .replace(/'/g, "&#039;");
                       }
                      
                      
                  // 게시판 댓글 전체 가져오기
                     function getAllReply(pageNum = 1) {
                       var recipeBoardId = $('#recipeBoardId').val();
                       var url = '/project/recipeboard/all/' + recipeBoardId + '?pageNum=' + pageNum;
                       

                       $.getJSON(url, function(data) {

                         
                         if (data.pagination) {
                        	    console.log("replyTotalCount 값:", data.pagination.replyTotalCount);
                        	} else {
                        	    console.warn("pagination 데이터가 존재하지 않음!");
                        	}
						
                         var currentUserId = data.currentUserId; // ✅ 백엔드에서 받은 로그인한 사용자 ID
                         console.log("현재 로그인한 사용자 ID:", currentUserId); // ✅ 로그인한 사용자 ID 확인
                         
                      		// ✅ 기존 댓글을 비운 후 새로 채움 (덮어쓰기)
                         $('#replies').empty();
                         
                         var list = '';

                         $.each(data.replies, function() {
                        	 
                         if (currentUserId === this.memberId) {
                	           console.log("✅ 수정/삭제 버튼 표시 - 리뷰 ID:", this.recipeReviewId);
                	        } else {
                	           console.log("❌ 수정/삭제 버튼 숨김 - 리뷰 ID:", this.recipeReviewId);
                	        }	 
                   		 var replyDateCreated = new Date(this.replyDateCreated);                   		
	                   	 var formattedDate = replyDateCreated.toLocaleString("ko-KR", { 
	                   		    year: "numeric", 
	                   		    month: "2-digit", 
	                   		    day: "2-digit", 
	                   		 	hourCycle: "h23",  // ✅ 24시간 형식 강제 적용
	                   		    hour: "2-digit", 
	                   		    minute: "2-digit", 
	                   		    second: "2-digit" 
	                   		})
							.replace(/\. /g, '-')  // "2025. 03. 18. 10:36:48" → "2025-03-18-10:36:48"
							.replace(/-(\d{2}):/, ' $1:');  // ✅ 날짜와 시간 사이의 `-`을 공백으로 변경


							list += '<div class="reply_item" data-reply-id="' + this.replyId + '">' +
							'<pre>' +
						    '<input type="hidden" class="replyId" value="' + this.replyId + '">' +
						    this.memberId +
						    '  ' +
						    '<span class="replyContentDisplay">' + escapeHtml(this.replyContent) + '</span>' +
						    '  ' +
						    formattedDate +
						    '  '; 
						    
						   // ✅ 현재 로그인한 사용자와 댓글 작성자가 동일한 경우에만 수정/삭제 버튼 출력
							if (currentUserId != null && currentUserId == this.memberId) {
							    console.log("✅ 수정/삭제 버튼 표시 - 댓글 ID:", this.replyId);
							    list += '<div class="reply_buttons" data-reply-id="' + this.replyId + '">' +
							        '<button class="btn_update" data-reply-id="' + this.replyId + '">수정</button>' +
							        '<button class="btn_delete" data-reply-id="' + this.replyId + '">삭제</button>' +
							        '</div>'; // ✅ 닫는 div 추가
							}
						   
							// ✅ 답글 버튼은 로그인한 사용자(ROLE_MEMBER)만 보이도록 처리
				            if (currentUserId != null) {
				                list += '<button class="btn_reply" data-reply-id="' + this.replyId + '">답글</button>';
				            }
						   
								list += '</pre>' +
			                	'<div class="nested_replies" id="nested_replies_' + this.replyId + '"></div>' + 
			                	'</div>'; 
			                	
			                	getAllNestedReply(this.replyId); // ✅ 대댓글도 같이 불러오기
			       			});
                         
                         $('#replies').html(list);
                         
                      	// ✅ 댓글 총 개수 표시 (replyTotalCount 사용)
                        if ($('#replyTotalCount').length) {
                        	 console.log("replyTotalCount 요소 발견! 업데이트 시도...");
                             $('#replyTotalCount').text(data.pagination.replyTotalCount);
                        } else {
                            console.warn("replyTotalCount 요소를 찾을 수 없습니다.");
                        }
                         
                     	// ✅ 페이지네이션 업데이트
                        updatePagination(data.pagination);
                         
                      	// ✅ 페이지네이션 UI 업데이트
                        function updatePagination(pageMaker) {
						    if (!pageMaker) {
						        console.warn("페이지네이션 데이터가 없습니다.");
						        return;
						    }
							
						    console.log("페이지네이션 업데이트 실행됨!", pageMaker);
						    
						    var paginationHtml = '';
						
						    if (pageMaker.prev) {
						        paginationHtml += '<a href="#" class="page-link" data-page="' + (pageMaker.startNum - 1) + '">◀ 이전</a> ';
						    }
						
						    for (var i = pageMaker.startNum; i <= pageMaker.endNum; i++) {
						        paginationHtml += '<a href="#" class="page-link" data-page="' + i + '">' + i + '</a> ';
						    }
						
						    if (pageMaker.next) {
						        paginationHtml += '<a href="#" class="page-link" data-page="' + (pageMaker.endNum + 1) + '">다음 ▶</a>';
						    }
						    
						    console.log("생성된 paginationHtml:", paginationHtml)
							
						    if ($('#pagination').length) {
						    $('#pagination').html(paginationHtml);
						    } else {
						        console.warn("pagination 요소를 찾을 수 없습니다.");
						    }
						    
                          	 // ✅ 페이지 번호 클릭 시 기본 이벤트 제거 & AJAX 호출
                             $('.page-link').on('click', function(event) {
                                 event.preventDefault(); // 기본 이벤트 제거 (페이지 이동 방지)
                                 var pageNum = $(this).data('page');
                                 getAllReply(pageNum); // AJAX로 댓글 불러오기
                             });
                         }
                         
                       
                         // 수정 버튼 클릭 이벤트
                         $('#replies').on('click', '.btn_update', function() {
                           var replyId = $(this).data('reply-id'); // data 속성에서 replyId 가져오기
                           var replyItem = $('.reply_item[data-reply-id="' + replyId + '"]'); // 해당 댓글 요소 가져오기
                           var replyContentSpan = replyItem.find('.replyContentDisplay');
                           var currentContent = replyContentSpan.text().trim(); // 기존 텍스트 내용 가져오기
                                                                         
                          // span 요소를 text input으로 변경
                           replyContentSpan.replaceWith('<input type="text" class="replyContentInput" data-reply-id="' + replyId + '" value="' + currentContent + '">');
                                                  
                           // 수정 완료 버튼으로 변경
                           $(this).replaceWith('<button class="btn_update_complete" data-reply-id="' + replyId + '">수정 완료</button>');

                           
                           // 수정 완료 버튼 클릭 이벤트
                             $('#replies').off('click', '.btn_update_complete').on('click', '.btn_update_complete', function() {
                              
                               var replyId = $(this).data('reply-id');
                               var replyItem = $('.reply_item[data-reply-id="' + replyId + '"]'); // 다시 댓글 요소 찾기
                               var replyContentInput = replyItem.find('.replyContentInput');
                               var updatedReplyContent = replyContentInput.val().trim();
                               
                                 console.log("replyId : " + replyId + ", 수정할 내용 : " + updatedReplyContent);

                                  $.ajax({
                                       url: '/project/recipeboard/replies/' + replyId,
                                         type: 'PUT',
                                         dataType: "json",
                                         ContentType: "application/json",
                                         data: JSON.stringify({
                                             replyContent: updatedReplyContent
                                         }),                                                                                            
                                           success: function(response) {
                                            
                                            $('.replyContentDisplay[data-reply-id="' + replyId + '"]').text(response.replyContent); // ✅ 수정된 내용만 삽입  
                                              
                                             if (response == 1) {
                                                 alert('댓글 수정 성공!');
                                                 
                                              // 기존 input을 다시 span으로 변경하여 수정 내용 반영
                                              replyContentInput.replaceWith('<span class="replyContentDisplay">' + updatedReplyContent + '</span>');

                                                // "수정 완료" 버튼을 다시 "수정" 버튼으로 변경
                                              replyItem.find('.btn_update_complete').replaceWith('<button class="btn_update" data-reply-id="' + replyId + '">수정</button>');
                                              
                                             }
                                           },
                                         error: function(xhr, status, error) {
                                          console.error("댓글 수정 실패 :", error);
                                         }
                                     });
                                 
                                });
                              
                             
                         });
                       });
                     }   
                  // 전역 함수로 설정
                  window.getAllReply = getAllReply
                  
                  $(document).ready(function() {
                	    getAllReply(1); // 페이지 로딩 시 첫 페이지 댓글 가져오기
                	});
                     
                  // 삭제 버튼을 클릭하면 선택된 댓글 삭제
               $('#replies').on('click', '.btn_delete', function() {
                      var replyId = $(this).closest('.reply_item').find('.replyId').val(); // ✅ 올바르게 선택
                      var recipeBoardId = $("#recipeBoardId").val(); // 게시판 ID 가져오기

                if (!replyId || !recipeBoardId) {
                    console.error("삭제할 댓글 ID 또는 게시판 ID를 찾을 수 없음.");
                    return;
                   }

                   console.log("삭제 요청:", replyId, recipeBoardId);

             $.ajax({
                 type: 'DELETE',
                 dataType: "json",
                 url: '/project/recipeboard/replies/' + replyId + '/' + recipeBoardId,
                 headers: {
                     'Content-Type': 'application/json'
                 },
                    success: function(result) {
                  if (result == 1) {
                      alert('댓글 삭제 성공!');
                      getAllReply();
                  }
              },
                    error: function(xhr, status, error) {
                        console.error("댓글 삭제 실패:", error);
                    }
                });
                                 }); // end replies.on
                                 
                                 // 리뷰 전체 불러오기
                                 function getAllRecipeReview(pageNum = 1) {
                                    var recipeBoardId = $('#recipeBoardId').val();
                                    var url = '/project/recipeboard/allReviews/' + recipeBoardId + '?pageNum=' + pageNum;
                                          
                                    
                                    $.getJSON(url, function(data) {
                                    			console.log("백엔드에서 받은 데이터:", data); // ✅ 전체 데이터 확인        
		                                        if (data.pagination) {
		                                       	    console.log("reviewTotalCount 값:", data.pagination.reviewTotalCount);
		                                       	} else {
		                                       	    console.warn("pagination 데이터가 존재하지 않음!");
		                                       	}
                                    			
		                                        var currentUserId = data.currentUserId; // ✅ 백엔드에서 받은 로그인한 사용자 ID
		                                        console.log("현재 로그인한 사용자 ID:", currentUserId); // ✅ 로그인한 사용자 ID 확인
		                                        var list = '';
                                                
                                                
                                           $(data.recipeReviews).each(function() {
                                        	   		 console.log("각 리뷰의 작성자 ID:", this.memberId); // ✅ 각 리뷰의 작성자 ID 확인
                                        	   		 
                                        	   		 if (currentUserId === this.memberId) {
                                        	             console.log("✅ 수정/삭제 버튼 표시 - 리뷰 ID:", this.recipeReviewId);
                                        	         } else {
                                        	             console.log("❌ 수정/삭제 버튼 숨김 - 리뷰 ID:", this.recipeReviewId);
                                        	         }
                                        	   		 
                                        	   		 console.log(this); // 각 리뷰 객체 출력
                                        	   		 var reviewAttachList = this.reviewAttachList || []; // 기본값으로 빈 배열 설정
                                        	   		 var recipeReviewDateCreated = new Date(this.recipeReviewDateCreated)
                                        	   		                  		
                            	                   	 var reviewFormattedDate = recipeReviewDateCreated.toLocaleString("ko-KR", { 
                            	                   		    year: "numeric", 
                            	                   		    month: "2-digit", 
                            	                   		    day: "2-digit", 
                            	                   		 	hourCycle: "h23",  // ✅ 24시간 형식 강제 적용
                            	                   		    hour: "2-digit", 
                            	                   		    minute: "2-digit", 
                            	                   		    second: "2-digit" 
                            	                   		})
                            							.replace(/\. /g, '-')  // "2025. 03. 18. 10:36:48" → "2025-03-18-10:36:48"
                            							.replace(/-(\d{2}):/, ' $1:');  // ✅ 날짜와 시간 사이의 `-`을 공백으로 변경

                                        	   		 
                                                      
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
                                                      if (this.reviewAttachList && this.reviewAttachList.length > 0) {
                                                          this.reviewAttachList.forEach(function(reviewAttach) {
                                                              var imageUrl = "/project/image/display?attachPath=" + encodeURIComponent(reviewAttach.attachPath) + 
                                                                             "&attachChgName=" + encodeURIComponent(reviewAttach.attachChgName) + 
                                                                             "&attachExtension=" + encodeURIComponent(reviewAttach.attachExtension);
                                                              
                                                              imageHTML += '<div class="image_item" data-chgName="'+ reviewAttach.attachChgName +'">'
                                                                        + '<a href="' + imageUrl + '" target="_blank">'
                                                                        + '<img width="100px" height="100px" src="' + imageUrl + '" />'
                                                                        + '</a>'                                                                         
                                                                        + '</div>';
                                                          });
                                                      }
                                                      
                                                   
                                                      
                                                      // 기존 리스트 초기화 후 새로운 리뷰 추가
                                                      list += '<div class="review_item" data-recipeReview-id="' + this.recipeReviewId + '">'
                                                      + '<pre>'
                                                      + '<input type="hidden" id="recipeReviewId" value="' + this.recipeReviewId + '">'
                                                      + '<div class="review-header">'
                                                      + '<div class="image-upload">'
                                                      + imageHTML // 이미지 HTML 코드 삽입
                                                      + '</div>'
                                                      + '<div class="review-info-container">' // 회원 정보 및 별점 컨테이너
                                                      + '<div class="review-info">'
                                                      + '<span class="memberId">' + this. memberId + '</span> <br>'
                                                      + reviewFormattedDate
                                                      + '</div>'
                                                      + '<span class="starRatingDisplay" data-recipeReview-id="' + this.recipeReviewId + '">' + starRatingHTML + '</span>'
                                                      + '</div>'
                                                      + '</div>'
                                                      + '<div class = "review-content">'
                                                      + '<span class="recipeReviewContentDisplay" data-recipeReview-id="' + this.recipeReviewId + '">' + this.recipeReviewContent + '</span>'
                                                      + '</div>'    
                                                      
                                                      if(currentUserId != null && currentUserId == this.memberId) {
                                                   	  console.log("✅ 수정/삭제 버튼 표시 - 리뷰 ID:", this.recipeReviewId);
                                                      list += '<div class="review-buttons">'
                                                      + '<button class="btn_review_update" data-review-id="' + this.recipeReviewId + '">수정</button>'
                                                      + '<button class="btn_review_delete" data-review-id="' + this.recipeReviewId + '">삭제</button>'                                                     
                                                      + '</div>'                                                    
                                                      + '</pre>'                                                      
                                                      + '</div>';                                                                                    
                                                      } else {
                                                          console.log("❌ 수정/삭제 버튼 숨김 - 리뷰 ID:", this.recipeReviewId);
                                                      }
                                                      
                                                      list += '</pre></div>';
                                                      
                                                   // ✅ 리뷰 수정 모달 추가 (초기 숨김 상태)
                                                      list += '<div class="editReviewModal modal" id="editReviewModal_' + this.recipeReviewId + '" style="display: none;">'
                                                            + '<div class="modal-content">'
                                                            + '<span class="close">&times;</span>'
                                                            + '<h2>리뷰 수정</h2>'
                                                            
                                                            + '<div class="edit-content-area">'
                                                            + '<div class="image-upload">'
                                                            + '<div class="image-drop">drag - image</div>'
                                                            + '<div class="show-image-list" id="imageList_' + this.recipeReviewId + '"></div>'
                                                            + '<div class="reviewAttachDTOImg-list" id="reviewAttachDTOImgList_' + this.recipeReviewId + '"></div>'
                                                            + '</div>'
                                                            
                                                            // ✅ 수정할 리뷰 내용 입력란 (id 추가)
                                                            + '<textarea id="editReviewContent_' + this.recipeReviewId + '" class="editReviewContent">'
                                                            + this.recipeReviewContent + '</textarea>'
                                                            + '<button class="btnEditComplete" data-review-id="' + this.recipeReviewId + '">수정</button>'
                                                            + '</div>'
                                                           
                                                            + '<div class="star-rating">'
                                                            + '<input type="radio" name="reviewRating_' + this.recipeReviewId + '" id="star5_' + this.recipeReviewId + '" value="5"><label for="star5_' + this.recipeReviewId + '"></label>'
                                                            + '<input type="radio" name="reviewRating_' + this.recipeReviewId + '" id="star4_' + this.recipeReviewId + '" value="4"><label for="star4_' + this.recipeReviewId + '"></label>'
                                                            + '<input type="radio" name="reviewRating_' + this.recipeReviewId + '" id="star3_' + this.recipeReviewId + '" value="3"><label for="star3_' + this.recipeReviewId + '"></label>'
                                                            + '<input type="radio" name="reviewRating_' + this.recipeReviewId + '" id="star2_' + this.recipeReviewId + '" value="2"><label for="star2_' + this.recipeReviewId + '"></label>'
                                                            + '<input type="radio" name="reviewRating_' + this.recipeReviewId + '" id="star1_' + this.recipeReviewId + '" value="1"><label for="star1_' + this.recipeReviewId + '"></label>'
                                                            + '</div>'
                                                            + '</div></div>';
                                                           
                                                      list += '</pre></div>';    
                                                   });
                                           
                                                $('#reviews').html(list);
                                                
                                             // ✅ 댓글 총 개수 표시 (reviewTotalCount 사용)
                                                if ($('#reviewTotalCount').length) {
                                                	 console.log("reviewTotalCount 요소 발견! 업데이트 시도...");
                                                     $('#reviewTotalCount').text(data.pagination.reviewTotalCount);
                                                } else {
                                                    console.warn("reviewTotalCount 요소를 찾을 수 없습니다.");
                                                }
                                                 
                                             	// ✅ 페이지네이션 업데이트
                                                updateReviewPagination(data.pagination);
                                                
                                             // ✅ 페이지네이션 UI 업데이트
                                                function updateReviewPagination(pageMaker) {
                        						    if (!pageMaker) {
                        						        console.warn("페이지네이션 데이터가 없습니다.");
                        						        return;
                        						    }
                        							
                        						    console.log("페이지네이션 업데이트 실행됨!2", pageMaker);
                        						    
                        						    var paginationHtml = '';
                        						
                        						    if (pageMaker.prev) {
                        						        paginationHtml += '<a href="#" class="page-link" data-page="' + (pageMaker.startNum - 1) + '">◀ 이전</a> ';
                        						    }
                        						
                        						    for (var i = pageMaker.startNum; i <= pageMaker.endNum; i++) {
                        						        paginationHtml += '<a href="#" class="page-link" data-page="' + i + '">' + i + '</a> ';
                        						    }
                        						
                        						    if (pageMaker.next) {
                        						        paginationHtml += '<a href="#" class="page-link" data-page="' + (pageMaker.endNum + 1) + '">다음 ▶</a>';
                        						    }
                        						    
                        						    console.log("생성된 paginationHtml:", paginationHtml)
                        							
                        						    if ($('#reviewPagination').length) {
                        						    $('#reviewPagination').html(paginationHtml);
                        						    } else {
                        						        console.warn("pagination 요소를 찾을 수 없습니다.");
                        						    }
                        						    
                                                  	 // ✅ 페이지 번호 클릭 시 기본 이벤트 제거 & AJAX 호출
                                                     $('.page-link').on('click', function(event) {
                                                         event.preventDefault(); // 기본 이벤트 제거 (페이지 이동 방지)
                                                         var pageNum = $(this).data('page');
                                                         getAllRecipeReview(pageNum); // AJAX로 댓글 불러오기
                                                     });
                                                 }
                                                
                                             });
                                   
                                    
                                     
                                 }      
                                 
                                 
                                 $(document).ready(function() {
                                                var selectedReviewId = null; // 수정할 리뷰 ID 저장
                                                var selectedFiles = []; // 드래그 드롭된 이미지 파일 저장
                                                               
                                 // 수정 버튼을 클릭하면 선택된 리뷰 수정
                                 $('#reviews').on('click', '.btn_review_update', function() {
                                	 			                           
                                                var selectedReviewId = $(this).data('review-id'); // ✅ 속성을 확실하게 가져오기
                                               
                                                console.log("선택한 리뷰 ID:", selectedReviewId); // ✅    값이 들어오는지 디버깅
                                                
                                                if (!selectedReviewId || String(selectedReviewId).trim() === "") {
                                                      alert("리뷰 ID를 찾을 수 없습니다.");
                                                      return;
                                                  }
                                                
                                                var reviewModal = $('#editReviewModal_' + selectedReviewId);
                                                console.log("🔹 해당 모달 요소:", reviewModal); // ✅ 모달이 존재하는지 확인
                                                var reviewItem = $('.recipeReview_item[data-review-id="' + selectedReviewId + '"]');
                                                var currentContent = reviewItem.find('.recipeReviewContentDisplay').text();
                                                var currentRating = reviewItem.find('.starRatingDisplay span[style="color:gold;"]').length;
                                                 
                                                
                                                // 기존 값 모달에 채우기
                                                 $('#editReviewContent_' + selectedReviewId).val(currentContent);
                                                 $('input[name="reviewRating_' + selectedReviewId + '"]').prop('checked', false); // 초기화
                                                 $('input[name="reviewRating_' + selectedReviewId + '"][value="' + currentRating + '"]').prop('checked', true);
                                                                                    
                                                   // 기존 이미지 미리보기 초기화
                                                 $('#imagePreview').empty();
                                                 selectedFiles = []; // 선택한 파일 목록 초기화
                                           
                                                 
                                              // 해당 리뷰 아래로 모달 이동 후 표시
                                                 reviewItem.after(reviewModal);
                                                 reviewModal.show();
                                                 reviewModal.css("display", "block");
                                                 
                                                  
                                             });
                                             
                                             // 모달 닫기 버튼
                                            $(document).on('click', '.close', function() {
    											console.log("✅ 모달 닫기 버튼 클릭됨!"); // ✅ 디버깅용 로그 출력

    											var reviewModal = $(this).closest('.editReviewModal'); // ✅ 올바른 클래스명 사용
    											console.log("🔹 닫힐 모달 요소:", reviewModal);

								    reviewModal.hide(); // ✅ 모달 닫기
								});
                                                                                                  
                                                // 수정 완료 버튼 클릭 이벤트
                                                $(document).on('click', '.btnEditComplete', function() {
                                                console.log("✅ '수정 완료' 버튼 클릭됨!");	
                                                	
                                                var selectedReviewId = $(this).attr('data-review-id'); // ✅ 수정할 리뷰 ID 가져오기
                                                var recipeBoardId = $('#recipeBoardId').val(); // ✅ 현재 게시판 ID 가져오기	
                                                var memberId = $('#reviewMemberId').val(); // ✅ 로그인한 사용자 ID 가져오기

                                                   
                                                console.log("🔹 선택한 리뷰 ID:", selectedReviewId);
                                                console.log("🔹 게시판 ID:", recipeBoardId);
                                                console.log("🔹 회원 ID:", memberId);
                                                
                                                if (!selectedReviewId) {
                                                        alert("수정할 리뷰가 없습니다.");
                                                        return;
                                                                                                      
                                                    }
                                                
                                                	var contentElement = $('#editReviewContent_' + selectedReviewId);
                                                	var updatedContent = contentElement.val().trim();
                                                	
                                                	console.log("🔹 수정된 내용:", updatedContent);
                                                	
                                                	// ✅ 수정할 별점 가져오기 (존재 여부 확인)
                                                    var ratingElement = $('input[name="reviewRating_' + selectedReviewId + '"]:checked');
                                                    console.log("🔹 별점 요소:", ratingElement); // ✅ 존재하는지 확인
                                                	
                                                    var updatedRating = ratingElement.val();
                                                    console.log("🔹 수정된 별점:", updatedRating);
                                                                                                        
                                                    if (!updatedContent) {
                                                        alert("리뷰 내용을 입력하세요.");
                                                        return;
                                                    }
                                                    
                                                    // ✅ 수정할 별점 가져오기 (존재 여부 확인)
                                                    var ratingElement = $('input[name="reviewRating_' + selectedReviewId + '"]:checked');
                                                    console.log("🔹 수정된 별점:", updatedRating);


                                                    if (ratingElement.length === 0) {
                                                        alert("❌ 별점 입력란을 찾을 수 없습니다.");
                                                        return;
                                                    }
                                                    
                                                    
                                                    if (!updatedRating) {
                                                        alert("별점을 선택하세요.");
                                                        return;
                                                    }
                                                                                                                                                         
                                                    // hidden input에서 reviewAttachDTO 값 가져오기
                                                    var updateReviewAttachDTOs = [];
                                                    $("input[type='hidden'][name='reviewAttachDTO']").each(function() {
                                                        var attachData = JSON.parse($(this).val()); // JSON 파싱
                                                        
                                                        // 이미지 정보 중복 등록 방지
                                                        if (!updateReviewAttachDTOs.some(dto => dto.attachChgName === attachData.attachChgName)) {
                                                      	  updateReviewAttachDTOs.push(attachData);
                                                        }
                                                    });
                                                    
                                                    var reviewData = {
                                                            'recipeReviewId': selectedReviewId,
                                                            'recipeBoardId': recipeBoardId,
                                                            'memberId': memberId,
                                                            'recipeReviewContent': updatedContent,
                                                            'reviewRating': updatedRating
                                                        };
                                                    
                                                    if (updateReviewAttachDTOs.length > 0) {
                                                        reviewData.reviewAttachList = updateReviewAttachDTOs;
                                                    } else {
                                                        reviewData.reviewAttachList = [];
                                                    }


                                                        console.log("🟢 JSON 데이터 확인:", JSON.stringify(reviewData));
                                                           
                                                // ajax 요청
                                                $.ajax({ 
                                                       type : 'PUT',
                                                        url : '/project/recipeboard/reviews/' + selectedReviewId, // 'update' 요청으로 변경
                                                        contentType: 'application/json; charset=UTF-8', // ✅ JSON 형식으로 전송
                                                        data: JSON.stringify(reviewData),
                                                        success : function(result) {
                                                            if (result == 1) {
                                                            	alert('✅ 리뷰 수정 성공!');
                                                                $('#editReviewModal_' + selectedReviewId).hide();
                                                                getAllRecipeReview(); // ✅ 리뷰 목록 새로고침
                                                            } else {
                                                                alert("❌ 리뷰 수정 실패");
                                                            }
                                                         },
                                                         error: function () {
                                                        	 alert("리뷰 수정 중 오류 발생")
                                                         	}                                               
                                                         });
                                                      
                                             });  
                                                
                                             }); // end review.on()   
                                 +
                              // 삭제 버튼을 클릭하면 선택된 리뷰 삭제
                                 $('#reviews')
                                       .on(
                                             'click',
                                             '.review_item .btn_review_delete',
                                             function() {
                                                console.log(this);
                                                var recipeBoardId = $(
                                                      "#recipeBoardId").val(); // 게시판 번호 데이터
                                                var recipeReviewId = // ✅ data 속성에서 직접 가져오기 
                                                //	$(this).prevAll('#recipeReviewId').val(); // 댓글 번호 데이터
                                                	  $(this).closest('.review_item').find('#recipeReviewId').val();
                                                
                                                      console.log("삭제할 리뷰 ID:", recipeReviewId);
                                                      console.log("삭제할 게시판 ID:", recipeBoardId);

                                                      if (!recipeReviewId) {
                                                          alert("삭제할 리뷰 ID가 없습니다.");
                                                          return;
                                                      }


                                                $
                                                      .ajax({
                                                         type : 'DELETE',
                                                         dataType: "json",
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
   
   <script src="${pageContext.request.contextPath }/resources/js/nestedreply.js"></script>
   
</body>
</html>