<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/security/tags"
	prefix="sec"%>
<!DOCTYPE html>
<html>
<head>
    <title>Update Recipe</title>
    <meta name="_csrf" content="${_csrf.token}"/>
    <meta name="_csrf_header" content="${_csrf.headerName}"/>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@400;700&display=swap" rel="stylesheet">
    <style>
        /* 스타일 (이전 코드와 동일)... */
        body {
            font-family: 'Noto Sans KR', sans-serif;
            margin: 0;
            padding: 20px;
            background-color: #f8f9fa;
        }

        h1 {
            text-align: center;
            margin-bottom: 40px;
            font-size: 2.5rem;
            color: #343a40;
        }

        .update-form {
		    max-width: 900px;
		    margin: 0 auto;
		    padding: 40px;
		    background-color: #fff;
		    border-radius: 10px;
		    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
		    border: 1px solid #e0e0e0;
		}

        .form-group {
            margin-bottom: 20px;
            display: flex;
            flex-direction: column;
        }

        .form-group label {
            font-weight: bold;
            margin-bottom: 8px;
            color: #495057;
        }

        .form-group input[type="text"], .form-group textarea, .form-group select,
        .form-group input[type="file"] {
            padding: 12px;
            border: 1px solid #ced4da;
            border-radius: 8px;
            font-size: 1rem;
            width: 100%;
            box-sizing: border-box;
        }

        .form-group input[type="text"]:focus, .form-group textarea:focus,
        .form-group select:focus {
            outline: none;
            border-color: #80bdff;
            box-shadow: 0 0 0 0.2rem rgba(0, 123, 255, .25);
        }

	    /* 기존 버튼 스타일을 recipe-button 클래스로 변경 */
	    .recipe-button {
	        background-color: #007bff;
	        color: white;
	        padding: 12px 25px;
	        border: none;
	        border-radius: 8px;
	        cursor: pointer;
	        font-size: 1.1rem;
	        transition: background-color 0.3s ease;
	        margin-top: 20px;
	    }
	
	    .recipe-button:hover {
	        background-color: #0056b3;
	    }
	
	    /* 삭제 버튼은 빨간색 유지 */
	    .recipe-button.delete {
	        background-color: #dc3545;
	    }
	
	    .recipe-button.delete:hover {
	        background-color: #c82333;
	    }

        /* 썸네일 이미지 미리보기 */
        .thumbnail-preview {
            margin-top: 15px;
            text-align: center;
        }

        .thumbnail-preview img {
            max-width: 200px;
            max-height: 200px;
            border: 2px solid #ddd;
            border-radius: 8px;
        }

        .noThumbnailMessage {
            color: red;
            text-align: center;
            font-size: 1rem;
            margin-top: 10px;
        }

        /* 해시태그 입력 및 리스트 */
        .hashtag-container {
            display: flex;
            align-items: center;
            flex-wrap: wrap;
            margin-top: 15px;
        }

        .hashtag {
            display: inline-flex;
            align-items: center;
            background-color: #f1f1f1;
            border-radius: 15px;
            padding: 5px 12px;
            margin: 5px;
            font-size: 1rem;
        }

        .hashtag .remove-hashtag {
            margin-left: 8px;
            cursor: pointer;
            font-weight: bold;
            color: red;
        }

        /* 선택 컨테이너 */
        .selection-container {
            margin-bottom: 25px;
            padding: 15px;
            border: 1px solid #dee2e6;
            border-radius: 8px;
            background-color: #f9f9f9;
        }

        .selection-container h3 {
            font-size: 1.2rem;
            margin-bottom: 10px;
            color: #495057;
            border-bottom: 1px solid #dee2e6;
            padding-bottom: 8px;
        }

        /* 재료 입력 */
        .ingredient-row {
            display: flex;
            align-items: center;
            margin-bottom: 15px;
        }

        .ingredient-row input {
            margin-right: 12px;
            padding: 10px;
            border-radius: 6px;
            border: 1px solid #ced4da;
        }

        .ingredient-row button {
            padding: 8px 15px;
            background-color: #dc3545;
            color: white;
            border-radius: 6px;
            cursor: pointer;
        }

        .ingredient-row button:hover {
            background-color: #c82333;
        }

        /* 조리 순서 입력 */
        .step-row {
            display: flex;
            align-items: center;
            margin-bottom: 15px;
            flex-wrap: wrap;
        }

        .step-row label {
            font-weight: bold;
            width: 100px;
        }

        .step-row textarea {
            padding: 10px;
            border: 1px solid #ced4da;
            border-radius: 6px;
            flex-grow: 1;
            font-size: 1rem;
        }

        .step-row button {
            padding: 8px 15px;
            background-color: #dc3545;
            color: white;
            border-radius: 6px;
            cursor: pointer;
        }

        .step-row button:hover {
            background-color: #c82333;
        }

        .step-preview {
            margin-top: 10px;
            text-align: center;
        }

        .step-preview img {
            max-width: 120px;
            max-height: 120px;
            border: 1px solid #ddd;
            border-radius: 6px;
            display: none;
        }

        /* 요리 정보 입력 스타일 */
        .recipe-info-container {
            display: flex;
            flex-wrap: wrap;
            margin-bottom: 25px;
            border: 1px solid #dee2e6;
            padding: 15px;
            border-radius: 8px;
            background-color: #f9f9f9;
        }

        .recipe-info-container .form-group {
            width: 30%;
            margin-right: 20px;
        }

        .recipe-info-container .form-group label {
            margin-bottom: 8px;
            font-weight: bold;
        }

        .recipe-info-container select {
            padding: 10px;
            border-radius: 6px;
            border: 1px solid #ced4da;
            font-size: 1rem;
            width: 100%;
            box-sizing: border-box;
        }

        @media (max-width: 768px) {
            .recipe-info-container .form-group {
                width: 100%;
                margin-bottom: 15px;
            }

            form {
                padding: 20px;
            }
        }
    </style>
</head>
<body>
<h1>레시피 수정</h1>
<form class="update-form" action="${pageContext.request.contextPath}/recipeboard/update" method="post" enctype="multipart/form-data">
    <input type="hidden" name="recipeBoardId" value="${recipeBoard.recipeBoardId}">

    <div class="form-group">
        <label for="recipeBoardTitle">레시피 제목</label>
        <input type="text" id="recipeBoardTitle" name="recipeBoardTitle" value="${recipeBoard.recipeBoardTitle}" required>
    </div>

    <div class="form-group">
        <label for="recipeBoardContent">레시피 설명</label>
        <textarea id="recipeBoardContent" name="recipeBoardContent" rows="3" required>${recipeBoard.recipeBoardContent}</textarea>
    </div>

    <div class="form-group">
        <label for="recipeTip">요리 팁</label>
        <textarea id="recipeTip" name="recipeTip" rows="3">${recipeBoard.recipeTip}</textarea>
    </div>

    <div class="form-group">
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
		</div>

    <div class="selection-container">
        <h3>종류별</h3>
        <select name="typeId" required>
            <c:forEach var="type" items="${typesList}">
                <c:if test="${type.typeId != 1}">
                    <option value="${type.typeId}" ${recipeBoard.typeId eq type.typeId ? 'selected' : ''}>${type.typeName}</option>
                </c:if>
            </c:forEach>
        </select>
    </div>

    <div class="selection-container">
        <h3>상황별</h3>
        <select name="situationId" required>
            <c:forEach var="situation" items="${situationsList}">
                <c:if test="${situation.situationId != 1}">
                    <option value="${situation.situationId}" ${recipeBoard.situationId eq situation.situationId ? 'selected' : ''}>${situation.situationName}</option>
                </c:if>
            </c:forEach>
        </select>
    </div>

    <div class="selection-container">
        <h3>방법별</h3>
        <select name="methodId" required>
            <c:forEach var="method" items="${methodsList}">
                <c:if test="${method.methodId != 1}">
                    <option value="${method.methodId}" ${recipeBoard.methodId eq method.methodId ? 'selected' : ''}>${method.methodName}</option>
                </c:if>
            </c:forEach>
        </select>
    </div>

   <div class="selection-container">
    <h3>재료별</h3>
    <ul>
        <%-- 재료 목록을 반복하여 체크박스 생성 --%>
      <c:forEach var="ingredient" items="${ingredientsList}" varStatus="status">
    <c:if test="${ingredient.ingredientId != 1}">
        <li>
            <input type="checkbox" name="ingredientIds" value="${ingredient.ingredientId}"
                ${ingredientChecked[status.index] ? 'checked' : ''}>
            ${ingredient.ingredientName}
        </li>
    </c:if>
</c:forEach>
    </ul>
</div>

   <div class="form-group">
        <label for="hashtags">해시태그</label>
<!-- 입력 필드 -->
		    <input type="text" id="hashtagInput" placeholder="Enter hashtag and press Enter(max 20 chars) and press Enter" maxlength="20" />
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

    <div class="selection-container">
        <h3>재료 입력</h3>
        <div id="ingredientInputs">
            <c:forEach var="ingredientDetail" items="${ingredientDetails}" varStatus="status">
                <div class="ingredient-row">
                    <input type="text" name="ingredientDetails[${status.index}].ingredientName" value="${ingredientDetail.ingredientName}" required>
                    <input type="text" name="ingredientDetails[${status.index}].ingredientAmount" value="${ingredientDetail.ingredientAmount}" required>
                    <input type="text" name="ingredientDetails[${status.index}].ingredientUnit" value="${ingredientDetail.ingredientUnit}" required>
                    <input type="text" name="ingredientDetails[${status.index}].ingredientNote" value="${ingredientDetail.ingredientNote}">
                    <button type="button" class="recipe-button delete remove-ingredient">삭제</button>

                </div>
            </c:forEach>
        </div>
        <button type="button" id="addIngredient" class="recipe-button">재료 추가</button>

    </div>

    <div class="selection-container">
        <h3>조리 순서</h3>
        <div id="stepInputs">
            <c:forEach var="step" items="${steps}" varStatus="status">
                <div class="step-row">
                    <label>Step ${status.index + 1}</label>
                    <textarea name="stepDescription[${status.index}]" required>${step.stepDescription}</textarea>
                    <input type="file" name="stepImage[${status.index}]" accept="image/*">
                    <input type="number" name="stepOrder[${status.index}]" value="${step.stepOrder}" style="width: 80px;">
                    <button type="button" class="recipe-button delete remove-step">삭제</button>

                    <div class="step-preview">
                        <img src="${step.stepImageUrl != null ? step.stepImageUrl : '#'}" alt="Step Preview" style="display: ${step.stepImageUrl != null ? 'block' : 'none'};">
                    </div>
                </div>
            </c:forEach>
        </div>
        <button type="button" id="addStep" class="recipe-button">조리 순서 추가</button>

    </div>

    <div class="recipe-info-container">
        <div class="form-group">
            <label for="time">시간</label>
            <select id="time" name="time" required>
                <option value="10" ${recipeBoard.time eq 10 ? 'selected' : ''}>10분</option>
                <option value="20" ${recipeBoard.time eq 20 ? 'selected' : ''}>20분</option>
                <option value="30" ${recipeBoard.time eq 30 ? 'selected' : ''}>30분</option>
                <option value="40" ${recipeBoard.time eq 40 ? 'selected' : ''}>40분</option>
                <option value="50" ${recipeBoard.time eq 50 ? 'selected' : ''}>50분</option>
                <option value="60" ${recipeBoard.time eq 60 ? 'selected' : ''}>60분</option>
            </select>
        </div>
        <div class="form-group">
            <label for="difficulty">난이도</label>
            <select id="difficulty" name="difficulty" required>
                <option value="아주쉬움" ${recipeBoard.difficulty eq '아주쉬움' ? 'selected' : ''}>아주 쉬움</option>
                <option value="쉬움" ${recipeBoard.difficulty eq '쉬움' ? 'selected' : ''}>쉬움</option>
                <option value="보통" ${recipeBoard.difficulty eq '보통' ? 'selected' : ''}>보통</option>
                <option value="어려움" ${recipeBoard.difficulty eq '어려움' ? 'selected' : ''}>어려움</option>
                <option value="매우어려움" ${recipeBoard.difficulty eq '매우어려움' ? 'selected' : ''}>매우 어려움</option>
            </select>
        </div>
        <div class="form-group">
            <label for="servings">인분</label>
            <select id="servings" name="servings" required>
                <option value="1" ${recipeBoard.servings eq 1 ? 'selected' : ''}>1인분</option>
                <option value="2" ${recipeBoard.servings eq 2 ? 'selected' : ''}>2인분</option>
                <option value="3" ${recipeBoard.servings eq 3 ? 'selected' : ''}>3인분</option>
                <option value="4" ${recipeBoard.servings eq 4 ? 'selected' : ''}>4인분</option>
                <option value="5" ${recipeBoard.servings eq 5 ? 'selected' : ''}>5인분</option>
                <option value="6" ${recipeBoard.servings eq 6 ? 'selected' : ''}>6인분</option>
                <option value="7" ${recipeBoard.servings eq 7 ? 'selected' : ''}>7인분</option>
                <option value="8" ${recipeBoard.servings eq 8 ? 'selected' : ''}>8인분</option>
                <option value="9" ${recipeBoard.servings eq 9 ? 'selected' : ''}>9인분</option>
                <option value="10" ${recipeBoard.servings eq 10 ? 'selected' : ''}>10인분</option>
            </select>
        </div>
    </div>

    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
    <button type="submit" class="recipe-button">수정</button>

</form>

<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
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

document.addEventListener("DOMContentLoaded", function () {
    // 재료 입력 관련 요소
    const ingredientInputs = document.getElementById("ingredientInputs");
    const addIngredientButton = document.getElementById("addIngredient");
    let ingredientCounter = ingredientInputs.querySelectorAll('.ingredient-row').length;

    // 재료 추가 함수
    function addIngredientRow() {
        const newRow = document.createElement("div");
        newRow.className = "ingredient-row";
        const index = ingredientInputs.querySelectorAll('.ingredient-row').length;
        newRow.innerHTML = `
            <input type="text" name="ingredientName[${index}]" placeholder="예) 돼지고기" required>
            <input type="text" name="ingredientAmount[${index}]" placeholder="예) 10(수량)" required>
            <input type="text" name="ingredientUnit[${index}]" placeholder="예) g,ml(단위)" required>
            <input type="text" name="ingredientNote[${index}]" placeholder="예) (비고)">
            <button type="button" class="remove-ingredient">삭제</button>
        `;
        ingredientInputs.appendChild(newRow);
    }

    // 이벤트 위임: 삭제 버튼
    ingredientInputs.addEventListener('click', function (event) {
        if (event.target.classList.contains('remove-ingredient')) {
            event.target.closest('.ingredient-row').remove();
        }
    });

    // 재료 추가 버튼 클릭 이벤트
    addIngredientButton.addEventListener("click", addIngredientRow);
});

document.addEventListener("DOMContentLoaded", function () {
    // 조리 순서 입력 관련 요소
    const stepInputs = document.getElementById("stepInputs");
    const addStepButton = document.getElementById("addStep");
    let stepCounter = 2;

    // 조리 순서 추가 버튼 클릭 이벤트 핸들러
    addStepButton.addEventListener("click", function () {
        const newRow = document.createElement("div");
        newRow.className = "step-row";
        newRow.innerHTML = `
            <label>Step ${stepCounter}</label>
            <textarea name="stepDescription" placeholder="예) 소고기는 기름기를 떼어내고 적당한 크기로 썰어주세요." required></textarea>
            <input type="file" name="stepImage" accept="image/*" >
            <input type="number" name="stepOrder" value="${stepCounter}" style="width: 80px;">
            <button type="button" class="remove-step">삭제</button>
            <div class="step-preview">
                <img src="#" alt="Step Preview" style="display: none;">
            </div>
        `;

        stepInputs.appendChild(newRow);

        // 이미지 미리보기
        const fileInput = newRow.querySelector('input[type="file"]');
        const previewImage = newRow.querySelector('.step-preview img');

        fileInput.addEventListener('change', function () {
            if (fileInput.files && fileInput.files[0]) {
                const reader = new FileReader();
                reader.onload = function (e) {
                    previewImage.src = e.target.result;
                    previewImage.style.display = 'block';
                };
                reader.readAsDataURL(fileInput.files[0]);
            } else {
                previewImage.src = '';
                previewImage.style.display = 'none';
            }

        });
        // 삭제 버튼에 이벤트 리스너 추가
        const removeButton = newRow.querySelector('.remove-step');
        removeButton.addEventListener('click', function () {
            newRow.remove();
        });
        stepCounter++;
    });
    // 이벤트 위임: 삭제 버튼
    stepInputs.addEventListener('click', function (event) {
        if (event.target.classList.contains('remove-step')) {
            event.target.closest('.step-row').remove();
        }
    });
    // 파일 변경 시 미리보기 업데이트 이벤트
    stepInputs.addEventListener('change', function (event) {
        if (event.target.matches('input[type="file"]')) {
            const fileInput = event.target;
            const previewImage = fileInput.closest('.step-row').querySelector('.step-preview img');
            if (fileInput.files && fileInput.files[0]) {
                const reader = new FileReader();
                reader.onload = function (e) {
                    previewImage.src = e.target.result;
                    previewImage.style.display = 'block';
                };
                reader.readAsDataURL(fileInput.files[0]);
            } else {
                previewImage.src = '';
                previewImage.style.display = 'none';
            }
        }
    });
});
</script>
</body>
</html>