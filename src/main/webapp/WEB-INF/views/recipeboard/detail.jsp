<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
<!-- jquery 라이브러리 import -->
<base href="${pageContext.request.contextPath}/">
<script src="https://code.jquery.com/jquery-3.7.1.js"></script>
<meta charset="UTF-8">
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
  
  .star-rating label:hover:before,
  .star-rating label:hover ~ label:before {
    color: gold;
  }

  .hashtags {
    margin-top: 20px;
  }
  .hashtags span {
    display: inline-block;
    background-color: #f1f1f1;
    padding: 5px 10px;
    margin: 5px;
    border-radius: 15px;
    font-size: 14px;
    color: #333;
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
		<p>작성자 : ${recipeBoard.memberId }</p>
		<!-- boardDateCreated 데이터 포멧 변경 -->
		<fmt:formatDate value="${recipeBoard.recipeBoardCreatedDate }"
			pattern="yyyy-MM-dd HH:mm:ss" var="recipeBoardCreatedDate" />
		<p>작성일 : ${recipeBoardCreatedDate }</p>
	</div>
	
	<div>
	    <c:if test="${recipeBoard.thumbnailPath != null}">
	        <!-- 썸네일 이미지 경로를 사용해 이미지를 표시 -->
	        <img 
	            src="${pageContext.request.contextPath}/uploads/${recipeBoard.thumbnailPath}" 
	            alt="썸네일 이미지" 
	            style="max-width: 300px; max-height: 300px;">
	    </c:if>
	    <c:if test="${recipeBoard.thumbnailPath == null}">
	        <p>썸네일 이미지가 없습니다.</p>
	    </c:if>
	</div>
	
	<div>
		<textarea rows="20" cols="120" readonly>${recipeBoard.recipeBoardContent }</textarea>
	</div>

	<div>
		<p>타입 : ${typeName}</p>
	</div>
	
	<div>
		<p>방법 : ${methodName}</p>
	</div>
	
	<div>
		<p>상황 : ${situationName}</p>
	</div>
	
	<div>
		<p>재료 :</p>
		<ul>
			<c:forEach var="ingredient" items="${ingredients}">
				<li>${ingredient.ingredientName}</li>
			</c:forEach>
		</ul>
	</div>

    <!-- 해시태그 표시 -->
	<div class="hashtags">
		<h3>Hashtags:</h3>
		<c:forEach var="hashtag" items="${hashtags}">
			<span>#${hashtag.hashtagName}</span>
		</c:forEach>
	</div>
	
	<div>
		<p>첨부 이미지 :</p>
		<c:forEach var="attach" items="${attachList}">
			<img
				src="${pageContext.request.contextPath}${attach.recipeBoardPath}"
				alt="첨부 이미지" style="max-width: 300px;">
			<br>
		</c:forEach>
	</div>
	<button onclick="location.href='recipeboard/list'">글 목록</button>
	<button
		onclick="location.href='recipeboard/update/${recipeBoard.recipeBoardId}'">글
		수정</button>
	<button type="button" id="deleteBoard">글 삭제</button>
	<form id="deleteForm"
		action="recipeboard/delete/${recipeBoard.recipeBoardId}" method="POST">
		<input type="hidden" name="recipeBoardId"
			value="${recipeBoard.recipeBoardId}">
	</form>
	
	<script type="text/javascript">
        $(document).ready(function(){
            $('#deleteBoard').click(function(){
                if(confirm('삭제하시겠습니까?')) {
                    $('#deleteForm').submit(); // form 데이터 전송
                }
            });
        }); // end document
    </script>
	
	<input type="hidden" id="recipeBoardId"
		value="${recipeBoard.recipeBoardId }">
	
	<div style="text-align: center;">
		<input type="text" id="memberId"> <input type="text"
			id="replyContent">
		<button id="btnAdd">댓글 작성</button>
	</div>

	<hr>
	<div style="text-align: center;">
		<div id="replies"></div>
	</div>

</body>
</html>
