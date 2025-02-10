<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html>
<head>
    <title>Update Recipe</title>
    <!-- 스타일 및 메타 정보는 이전과 동일하게 유지 -->
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

        form {
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

        button[type="submit"], button[type="button"] {
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

        button[type="submit"]:hover, button[type="button"]:hover {
            background-color: #0056b3;
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
<form action="${pageContext.request.contextPath}/recipeboard/update" method="post" enctype="multipart/form-data">
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
        <label for="thumbnail">썸네일 이미지</label>
        <input type="file" id="thumbnail" name="thumbnail" accept="image/*" onchange="validateAndPreviewThumbnail(this)">
        <div class="thumbnail-preview">
            <img id="thumbnailPreview" src="
            <c:choose>
                <c:when test="${not empty recipeBoard.thumbnailPath}">
                    <c:url value="/upload/${recipeBoard.thumbnailPath}"/>
                </c:when>
                <c:otherwise>#</c:otherwise>
            </c:choose>" alt="Thumbnail Preview" style="display: ${not empty recipeBoard.thumbnailPath ? 'block' : 'none'};">
        </div>
        <p id="noThumbnailMessage" class="noThumbnailMessage" style="display: ${recipeBoard.thumbnailPath != null ? 'none' : 'block'}">
            No thumbnail selected.
        </p>
    </div>

    <div class="selection-container">
        <h3>종류별</h3>
        <select name="typeId" required>
            <c:forEach var="type" items="${typesList}">
                <c:if test="${type.typeId != 1}">
                    <option value="${type.typeId}" ${recipeBoard.typeId == type.typeId ? 'selected' : ''}>${type.typeName}</option>
                </c:if>
            </c:forEach>
        </select>
    </div>

    <div class="selection-container">
        <h3>상황별</h3>
        <select name="situationId" required>
            <c:forEach var="situation" items="${situationsList}">
                <c:if test="${situation.situationId != 1}">
                    <option value="${situation.situationId}" ${recipeBoard.situationId == situation.situationId ? 'selected' : ''}>${situation.situationName}</option>
                </c:if>
            </c:forEach>
        </select>
    </div>

    <div class="selection-container">
        <h3>방법별</h3>
        <select name="methodId" required>
            <c:forEach var="method" items="${methodsList}">
                <c:if test="${method.methodId != 1}">
                    <option value="${method.methodId}" ${recipeBoard.methodId == method.methodId ? 'selected' : ''}>${method.methodName}</option>
                </c:if>
            </c:forEach>
        </select>
    </div>

    <div class="selection-container">
        <h3>재료별</h3>
        <ul>
            <%-- 재료 목록을 반복하여 체크박스 생성 --%>
            <c:forEach var="ingredient" items="${ingredientsList}">
                <c:if test="${ingredient.ingredientId != 1}">
                    <li>
                        <input type="checkbox" name="ingredientIds" value="${ingredient.ingredientId}"
                                <c:if test="${fn:contains(recipeDetailVO.recipeBoard.ingredientIds, ingredient.ingredientId)}">checked</c:if>>
                        ${ingredient.ingredientName}
                    </li>
                </c:if>
            </c:forEach>
        </ul>
    </div>

   <div class="form-group">
        <label for="hashtags">해시태그</label>
        <div id="hashtagInputs">
            <c:forEach var="hashtag" items="${recipeBoard.hashtags}" varStatus="status">
                <div class="input-group mb-1">
                    <input type="text" class="form-control" name="hashtags[${status.index}].hashtagName"
                           value="${hashtag.hashtagName}"/>
                    <div class="input-group-append">
                        <button class="btn btn-outline-danger remove-hashtag" type="button">삭제</button>
                    </div>
                </div>
            </c:forEach>
             <input type="text" id="hashtagInput" placeholder="해시태그를 입력하세요. (엔터로 추가)">
        </div>
        <button id="addHashtag" class="btn btn-secondary" type="button">해시태그 추가</button>
    </div>

    <div class="selection-container">
        <h3>재료 입력</h3>
        <div id="ingredientInputs">
            <c:forEach var="ingredientDetail" items="${ingredientDetails}" varStatus="status">
                <div class="ingredient-row">
                    <input type="text" name="ingredientName[${status.index}]" value="${ingredientDetail.ingredientName}" required>
                    <input type="text" name="ingredientAmount[${status.index}]" value="${ingredientDetail.ingredientAmount}" required>
                    <input type="text" name="ingredientUnit[${status.index}]" value="${ingredientDetail.ingredientUnit}" required>
                    <input type="text" name="ingredientNote[${status.index}]" value="${ingredientDetail.ingredientNote}">
                    <button type="button" class="remove-ingredient">삭제</button>
                </div>
            </c:forEach>
        </div>
        <button type="button" id="addIngredient">재료 추가</button>
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
                    <button type="button" class="remove-step">삭제</button>
                    <div class="step-preview">
                        <img src="${step.stepImageUrl != null ? step.stepImageUrl : '#'}" alt="Step Preview" style="display: ${step.stepImageUrl != null ? 'block' : 'none'};">
                    </div>
                </div>
            </c:forEach>
        </div>
        <button type="button" id="addStep">조리 순서 추가</button>
    </div>

    <div class="recipe-info-container">
        <div class="form-group">
            <label for="time">시간</label>
            <select id="time" name="time" required>
                <option value="10" ${recipeBoard.time == 10 ? 'selected' : ''}>10분</option>
                <option value="20" ${recipeBoard.time == 20 ? 'selected' : ''}>20분</option>
                <option value="30" ${recipeBoard.time == 30 ? 'selected' : ''}>30분</option>
                <option value="40" ${recipeBoard.time == 40 ? 'selected' : ''}>40분</option>
                <option value="50" ${recipeBoard.time == 50 ? 'selected' : ''}>50분</option>
                <option value="60" ${recipeBoard.time == 60 ? 'selected' : ''}>60분</option>
            </select>
        </div>
        <div class="form-group">
            <label for="difficulty">난이도</label>
            <select id="difficulty" name="difficulty" required>
                <option value="아주쉬움" ${recipeBoard.difficulty == '아주쉬움' ? 'selected' : ''}>아주 쉬움</option>
                <option value="쉬움" ${recipeBoard.difficulty == '쉬움' ? 'selected' : ''}>쉬움</option>
                <option value="보통" ${recipeBoard.difficulty == '보통' ? 'selected' : ''}>보통</option>
                <option value="어려움" ${recipeBoard.difficulty == '어려움' ? 'selected' : ''}>어려움</option>
                <option value="매우어려움" ${recipeBoard.difficulty == '매우어려움' ? 'selected' : ''}>매우 어려움</option>
            </select>
        </div>
        <div class="form-group">
            <label for="servings">인분</label>
            <select id="servings" name="servings" required>
                <option value="1" ${recipeBoard.servings == 1 ? 'selected' : ''}>1인분</option>
                <option value="2" ${recipeBoard.servings == 2 ? 'selected' : ''}>2인분</option>
                <option value="3" ${recipeBoard.servings == 3 ? 'selected' : ''}>3인분</option>
                <option value="4" ${recipeBoard.servings == 4 ? 'selected' : ''}>4인분</option>
                <option value="5" ${recipeBoard.servings == 5 ? 'selected' : ''}>5인분</option>
                <option value="6" ${recipeBoard.servings == 6 ? 'selected' : ''}>6인분</option>
                <option value="7" ${recipeBoard.servings == 7 ? 'selected' : ''}>7인분</option>
                <option value="8" ${recipeBoard.servings == 8 ? 'selected' : ''}>8인분</option>
                <option value="9" ${recipeBoard.servings == 9 ? 'selected' : ''}>9인분</option>
                <option value="10" ${recipeBoard.servings == 10 ? 'selected' : ''}>10인분</option>
            </select>
        </div>
    </div>

    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
    <button type="submit">수정</button>
</form>

<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script>
    $(document).ready(function () {
         // 해시태그 관련 JavaScript
        const hashtagInput = document.getElementById("hashtagInput");
        const hashtagInputsContainer = document.getElementById("hashtagInputs");
        const initialHashtags = ${JSON.stringify(recipeBoard.hashtags)}; // 초기 해시태그 목록

        // 초기 해시태그 표시
        initialHashtags.forEach((hashtag, index) => {
            addHashtagInput(hashtag.hashtagName, index);
        });

        // 해시태그 추가 함수
        function addHashtagInput(hashtagName = "", index = null) {
            const hashtagIndex = index !== null ? index : $("#hashtagInputs .input-group").length;
            const newHashtagInput = `
                <div class="input-group mb-1">
                    <input type="text" class="form-control" name="hashtags[${hashtagIndex}].hashtagName" value="${hashtagName}" />
                    <div class="input-group-append">
                        <button class="btn btn-outline-danger remove-hashtag" type="button">삭제</button>
                    </div>
                </div>
            `;
            $("#hashtagInputs").append(newHashtagInput);
        }

        // 해시태그 추가 버튼 클릭 이벤트
        $("#addHashtag").click(function () {
            addHashtagInput();
        });

        // 해시태그 삭제 버튼 클릭 이벤트 (동적 생성 요소에 대한 이벤트 위임)
        $("#hashtagInputs").on('click', '.remove-hashtag', function () {
            $(this).closest('.input-group').remove();
        });
                // 해시태그 입력 필드에서 엔터 키 입력 시 해시태그 추가
        hashtagInput.addEventListener("keydown", function (e) {
            if (e.key === "Enter") {
                e.preventDefault(); // 폼 제출 방지
                const value = hashtagInput.value.trim(); // 입력 값에서 공백 제거

                if (value) {
                    // 새 해시태그 입력 필드 추가
                    addHashtagInput(value);

                    // 입력 필드 초기화
                    hashtagInput.value = "";
                }
            }
        });
    });

    function validateAndPreviewThumbnail(input) {
        const previewImage = document.getElementById('thumbnailPreview');
        const noThumbnailMessage = document.getElementById('noThumbnailMessage');
        const allowedExtensions = ['jpeg', 'jpg', 'png', 'gif'];

        if (input.files && input.files[0]) {
            const file = input.files[0];
            const fileName = file.name.toLowerCase();
            const fileExtension = fileName.split('.').pop();

            if (!allowedExtensions.includes(fileExtension)) {
                alert('Invalid file type. Please upload an image (JPEG, PNG, GIF).');
                input.value = '';
                previewImage.style.display = 'none';
                noThumbnailMessage.style.display = 'block';
                return;
            }
            const reader = new FileReader();
            reader.onload = function (e) {
                previewImage.src = e.target.result;
                previewImage.style.display = 'block';
                noThumbnailMessage.style.display = 'none';
            };
            reader.readAsDataURL(file);
        } else {
            const existingThumbnail = "${recipeBoard.thumbnailPath}";
            if (existingThumbnail && existingThumbnail !== "") {
                previewImage.src = "${pageContext.request.contextPath}/upload/" + existingThumbnail;
                previewImage.style.display = 'block';
                noThumbnailMessage.style.display = 'none';

            } else {
                previewImage.style.display = 'none';
                noThumbnailMessage.style.display = 'block';
            }
        }
    }

    // 재료 관련 JavaScript
    const ingredientInputs = document.getElementById("ingredientInputs");
    const addIngredientButton = document.getElementById("addIngredient");
    const initialIngredientDetails = ${JSON.stringify(ingredientDetails)};

    function addIngredientRow(ingredient = {}, index = null) {
        const newRow = document.createElement("div");
        newRow.className = "ingredient-row";
        let ingredientIndex = index !== null ? index : ingredientInputs.querySelectorAll('.ingredient-row').length;

        newRow.innerHTML = `
            <input type="text" name="ingredientName[${ingredientIndex}]" value="${ingredient.ingredientName || ''}" required>
            <input type="text" name="ingredientAmount[${ingredientIndex}]" value="${ingredient.ingredientAmount || ''}" required>
            <input type="text" name="ingredientUnit[${ingredientIndex}]" value="${ingredient.ingredientUnit || ''}" required>
            <input type="text" name="ingredientNote[${ingredientIndex}]" value="${ingredient.ingredientNote || ''}">
            <button type="button" class="remove-ingredient">삭제</button>
        `;
        ingredientInputs.appendChild(newRow);
    }

    initialIngredientDetails.forEach((ingredient, index) => {
        addIngredientRow(ingredient, index);
    });

    // 이벤트 위임을 사용하여 동적으로 생성된 삭제 버튼 처리
    ingredientInputs.addEventListener('click', function (event) {
        if (event.target.classList.contains('remove-ingredient')) {
            event.target.closest('.ingredient-row').remove();
        }
    });

    addIngredientButton.addEventListener("click", addIngredientRow);

    // 조리 순서 관련 JavaScript
    const stepInputs = document.getElementById("stepInputs");
    const addStepButton = document.getElementById("addStep");
    const initialSteps = ${JSON.stringify(steps)};

    function addStepRow(step = {}, index = null) {
        const stepIndex = index !== null ? index : stepInputs.querySelectorAll('.step-row').length;
        const newRow = document.createElement("div");
        newRow.className = "step-row";

        newRow.innerHTML = `
            <label>Step ${stepIndex + 1}</label>
            <textarea name="stepDescription[${stepIndex}]" required>${step.stepDescription || ''}</textarea>
            <input type="file" name="stepImage[${stepIndex}]" accept="image/*">
            <input type="number" name="stepOrder[${stepIndex}]" value="${step.stepOrder}" style="width: 80px;">
            <button type="button" class="remove-step">삭제</button>
            <div class="step-preview">
                <img src="${step.stepImageUrl ? step.stepImageUrl : '#'}" alt="Step Preview" style="display: ${step.stepImageUrl ? 'block' : 'none'};">
            </div>
        `;
        stepInputs.appendChild(newRow);

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
                previewImage.style.display = 'none';
            }
        });
    }

    initialSteps.forEach((step, index) => {
        addStepRow(step, index);
    });

    function updateStepNumbers() {
        const stepRows = stepInputs.querySelectorAll('.step-row');
        stepRows.forEach((row, index) => {
            row.querySelector('label').textContent = `Step ${index + 1}`;
            row.querySelector('input[name^="stepOrder"]').value = index + 1;
        });
    }

    // 이벤트 위임을 사용하여 동적으로 생성된 삭제 버튼 처리
    stepInputs.addEventListener('click', function (event) {
        if (event.target.classList.contains('remove-step')) {
            event.target.closest('.step-row').remove();
            updateStepNumbers();
        }
    });

    addStepButton.addEventListener("click", addStepRow);

    // 폼 제출 처리
   document.querySelector("form").addEventListener("submit", function (e) {
        e.preventDefault(); // 기본 폼 제출 방지

        const form = this;
        const formData = new FormData(form); // 폼 데이터 객체 생성

        // 해시태그 필드 값 설정
        const hashtagRows = document.querySelectorAll("#hashtagInputs .input-group");
        hashtagRows.forEach((row, index) => {
            const hashtagName = row.querySelector("input[type='text']").value;
            formData.append(`hashtags[${index}].hashtagName`, hashtagName);
        });

        // AJAX 요청을 위한 설정
        const xhr = new XMLHttpRequest();
        xhr.open("POST", form.action, true);

        // CSRF 토큰 설정 (필요한 경우)
        const csrfToken = document.querySelector('meta[name="_csrf"]').content;
        const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;
        xhr.setRequestHeader(csrfHeader, csrfToken);

        xhr.onload = function () {
            if (xhr.status >= 200 && xhr.status < 300) { // 성공적인 응답
                console.log('Form successfully submitted');
                window.location.href = "${pageContext.request.contextPath}/recipeboard/detail/${recipeBoard.recipeBoardId}";
            } else {
                console.error('Form submission failed with status:', xhr.status);
            }
        };

        xhr.onerror = function () {
            console.error('Form submission failed');
        };

        // 폼 데이터 전송
        xhr.send(formData);
    });
</script>
</body>
</html>