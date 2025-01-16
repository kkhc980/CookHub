<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html>
<head>
    <title>Update Recipe</title>
    <meta name="_csrf" content="${_csrf.token}" />
	<meta name="_csrf_header" content="${_csrf.headerName}" />
    <style>
    .thumbnail-preview {
        margin-top: 10px;
    }
    .thumbnail-preview img {
        max-width: 150px;
        max-height: 150px;
        border: 1px solid #ddd;
        padding: 5px;
        background-color: #f5f5f5;
    }

    .selection-container {
        margin-bottom: 20px;
    }
    .selection-container h3 {
        font-size: 18px;
        margin-bottom: 5px;
    }
    .selection-container ul {
        list-style: none;
        padding: 0;
        display: flex;
        flex-wrap: wrap;
    }
    .selection-container li {
        margin-right: 15px;
        cursor: pointer;
    }
    .selection-container li input {
        margin-right: 5px;
    }

    /* Hashtag 스타일 */
    .hashtag-container {
        display: flex;
        align-items: center;
        flex-wrap: wrap;
    }
    .hashtag {
        display: inline-flex;
        align-items: center;
        background-color: #f1f1f1;
        border-radius: 15px;
        padding: 5px 10px;
        margin: 5px;
        font-size: 14px;
        color: #333;
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        text-transform: lowercase;
    }
    .hashtag:hover {
        background-color: #e0e0e0; /* 호버 시 색상 변경 */
    }
    .hashtag .remove-hashtag {
        margin-left: 10px;
        cursor: pointer;
        font-weight: bold;
        color: red;
        font-size: 16px;
    }
    .hashtag .remove-hashtag:hover {
        color: darkred; /* 호버 시 색상 변경 */
    }

    /* Hashtag Input 스타일 */
    #hashtagInput {
        width: calc(100% - 20px);
        padding: 8px 10px;
        border: 1px solid #ccc;
        border-radius: 15px;
        font-size: 14px;
        margin-bottom: 10px;
        text-transform: lowercase;
    }
    #hashtagInput:focus {
        outline: none;
        border-color: #333;
        box-shadow: 0 0 5px rgba(0, 0, 0, 0.2);
    }
    </style>
    <script>
    document.addEventListener("DOMContentLoaded", function () {
        const hashtagInput = document.getElementById("hashtagInput");
        const hashtagList = document.getElementById("hashtagList");
        const hashtagsHiddenInput = document.getElementById("hashtagsHiddenInput");

        // 해시태그 초기화 (히든 필드 값 가져오기)
        let hashtags = hashtagsHiddenInput.value.split(',').filter(tag => tag.trim() !== "");

        // 해시태그 추가 처리
        hashtagInput.addEventListener("keydown", function (e) {
            if (e.key === "Enter") {
                e.preventDefault();
                const value = hashtagInput.value.trim().toLowerCase(); // 소문자로 변환
                if (value && !hashtags.includes(value)) {
                    addHashtag(value);
                    hashtagInput.value = ""; // 입력 필드 초기화
                } else if (hashtags.includes(value)) {
                    alert("This hashtag is already added.");
                }
            }
        });

        // 해시태그 추가 함수
        function addHashtag(value) {
            hashtags.push(value);
            const hashtagElement = document.createElement("div");
            hashtagElement.className = "hashtag";
            const hashtagText = document.createTextNode("#" + value);
            hashtagElement.appendChild(hashtagText);

            const removeButton = document.createElement("span");
            removeButton.className = "remove-hashtag";
            removeButton.textContent = "x";
            removeButton.addEventListener("click", function () {
                removeHashtag(value, hashtagElement);
            });

            hashtagElement.appendChild(removeButton);
            hashtagList.appendChild(hashtagElement);

            updateHiddenInput();
        }

        // 해시태그 제거 함수
		window.removeHashtag = function (value, element) {
		    // 배열에서 해시태그 제거
		    hashtags = hashtags.filter(tag => tag !== value);
		
		    // HTML에서 해당 해시태그 요소 제거
		    const parentElement = element.parentNode; // "hashtag" div
		    hashtagList.removeChild(parentElement);
		
		    // 히든 필드 업데이트
		    updateHiddenInput();
		};


        // 히든 필드 업데이트
        function updateHiddenInput() {
            hashtagsHiddenInput.value = hashtags.join(",");
        }
    });
	    
	    // 파일 선택 시 썸네일 미리보기
		function previewThumbnail(input) {
	        const previewImage = document.getElementById('thumbnailPreview');
	        const defaultThumbnail = document.getElementById('currentThumbnailPath').value; // 기존 썸네일 경로
	        const allowedExtensions = ['jpg', 'jpeg', 'png', 'gif']; // 허용할 이미지 확장자
	
	        if (input.files && input.files[0]) {
	            const file = input.files[0];
	            const fileName = file.name.toLowerCase();
	            const fileExtension = fileName.split('.').pop(); // 파일 확장자 추출
	
	            if (!allowedExtensions.includes(fileExtension)) {
	                alert("이미지 파일(jpg, jpeg, png, gif)만 업로드 가능합니다.");
	                input.value = ""; // 파일 입력 초기화
	                previewImage.src = defaultThumbnail; // 기존 썸네일 복원
	                return;
	            }
	
	            // 파일 미리보기
	            const reader = new FileReader();
	            reader.onload = function (e) {
	                previewImage.src = e.target.result; // 선택된 파일 표시
	            };
	            reader.readAsDataURL(file);
	        } else {
	            // 파일 선택 취소 시 기존 썸네일 복원
	            previewImage.src = defaultThumbnail;
	        }
	    }
    </script>
</head>
<body>
    <h1>Update Recipe</h1>
    <form action="${pageContext.request.contextPath}/recipeboard/update" method="post" enctype="multipart/form-data">
    	<!-- CSRF 토큰 추가 -->
        <input type="hidden" name="recipeBoardId" value="${recipeBoard.recipeBoardId}">

        <!-- Recipe Title -->
        <label for="title">Title:</label>
        <input type="text" id="title" name="recipeBoardTitle" value="${recipeBoard.recipeBoardTitle}" required>
        <br>

        <!-- Recipe Content -->
        <label for="content">Content:</label>
        <textarea id="content" name="recipeBoardContent" required>${recipeBoard.recipeBoardContent}</textarea>
        <br>

        <!-- Recipe Type -->
        <div class="selection-container">
            <h3>Type:</h3>
            <ul>
                <c:forEach var="type" items="${typesList}">
                <c:if test="${type.typeId != 1}">
                    <li>
                        <input type="radio" name="typeId" value="${type.typeId}" 
                            <c:if test="${recipeBoard.typeId == type.typeId}">checked</c:if>> 
                        ${type.typeName}
                    </li>
				</c:if>
                </c:forEach>
            </ul>
        </div>

        <!-- Recipe Method -->
        <div class="selection-container">
            <h3>Method:</h3>
            <ul>
                <c:forEach var="method" items="${methodsList}">
                <c:if test="${method.methodId != 1}">
                    <li>
                        <input type="radio" name="methodId" value="${method.methodId}" 
                            <c:if test="${recipeBoard.methodId == method.methodId}">checked</c:if>> 
                        ${method.methodName}
                    </li>
                </c:if>
                </c:forEach>          
            </ul>
        </div>

        <!-- Recipe Situation -->
        <div class="selection-container">
            <h3>Situation:</h3>
            <ul>
                <c:forEach var="situation" items="${situationsList}">
                <c:if test="${situation.situationId != 1}">
                    <li>
                        <input type="radio" name="situationId" value="${situation.situationId}" 
                            <c:if test="${recipeBoard.situationId == situation.situationId}">checked</c:if>> 
                        ${situation.situationName}
                    </li>
                </c:if>
                </c:forEach>
            </ul>
        </div>

        <!-- Recipe Ingredients -->
        <div class="selection-container">
            <h3>Ingredients:</h3>
            <ul>
                <c:forEach var="ingredient" items="${ingredientsList}">
                <c:if test="${ingredient.ingredientId != 1}">
				    <li>
				        <input type="checkbox" name="ingredientIds" value="${ingredient.ingredientId}"
				            <c:if test="${selectedIngredientIds.contains(ingredient.ingredientId)}">checked</c:if>>
				        ${ingredient.ingredientName}
				    </li>
				</c:if>
				</c:forEach>
            </ul>
        </div>
        
		<!-- Hashtags -->
		<div class="selection-container">
		    <h3>Hashtags</h3>
		    <!-- 입력 필드 -->
		    <input type="text" id="hashtagInput" placeholder="Enter hashtag and press Enter" />
		    <!-- 해시태그 목록 -->
		    <div id="hashtagList" class="hashtag-container">
			    <c:forEach var="hashtag" items="${hashtags}">
			        <div class="hashtag">
			            #${fn:toLowerCase(hashtag.hashtagName)} <!-- 소문자로 출력 -->
			            <span class="remove-hashtag" onclick="removeHashtag('${fn:toLowerCase(hashtag.hashtagName)}', this)">x</span>
			        </div>
			    </c:forEach>
			</div>
		    <!-- Hidden input to store hashtags as comma-separated values -->
		    <input type="hidden" id="hashtagsHiddenInput" name="hashtags" 
		           value="<c:forEach var='hashtag' items='${hashtags}'>${fn:toLowerCase(hashtag.hashtagName)},</c:forEach>">
		</div>

        <!-- Thumbnail Upload -->
		<label for="thumbnail">Thumbnail:</label>
		<div class="thumbnail-preview">
		    <!-- 기존 썸네일 표시 -->
		    <img id="thumbnailPreview" 
		         src="${pageContext.request.contextPath}/recipeboard/thumbnail/${recipeBoard.recipeBoardId}" 
		         alt="Current Thumbnail" 
		         style="max-width: 200px; max-height: 200px;">
		</div>
		<br>
		<!-- 파일 입력 -->
		<input type="file" id="thumbnail" name="thumbnail" accept="image/*" onchange="previewThumbnail(this)">
		<input type="hidden" id="currentThumbnailPath" name="currentThumbnailPath" 
		       value="${pageContext.request.contextPath}/recipeboard/thumbnail/${recipeBoard.recipeBoardId}">
		<br><br>

        <!-- Submit Button -->
        <input type="hidden" name="${_csrf.parameterName }" value="${_csrf.token }">
        <button type="submit">Update</button>
    </form>
</body>
</html>