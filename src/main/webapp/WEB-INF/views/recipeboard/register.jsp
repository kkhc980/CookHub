<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>

<!DOCTYPE html>
<html>
<head>
    <title>Register Recipe</title>
    <meta name="_csrf" content="${_csrf.token}" />
	<meta name="_csrf_header" content="${_csrf.headerName}" />
    <style>
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

        .thumbnail-preview {
            margin-top: 10px;
        }
        .thumbnail-preview img {
            max-width: 200px;
            max-height: 200px;
        }

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
        }
        .hashtag .remove-hashtag {
            margin-left: 5px;
            cursor: pointer;
            font-weight: bold;
            color: red;
        }
    </style>
    
    <script>
    document.addEventListener("DOMContentLoaded", function () {
        const hashtagInput = document.getElementById("hashtagInput");
        const hashtagList = document.getElementById("hashtagList");
        const hashtagsHiddenInput = document.getElementById("hashtagsHiddenInput");
/*         const registerForm = document.getElementById("registerForm");
        
        // 폼 제출 시 JavaScript로 전송 처리
        registerForm.addEventListener("submit", function (e) {
            e.preventDefault(); // 기본 폼 제출 방지

            const csrfToken = document.querySelector('meta[name="_csrf"]').content; // CSRF 토큰
            const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content; // CSRF 헤더 이름

            // Ajax 요청으로 폼 데이터를 전송하는 방법
            const formData = new FormData(registerForm); // 폼 데이터를 가져옴

            // CSRF 토큰을 요청 헤더에 추가
            const request = new XMLHttpRequest();
            request.open("POST", registerForm.action, true);
            request.setRequestHeader(csrfHeader, csrfToken); // 헤더에 CSRF 토큰 추가

            // 폼 데이터 전송
            request.onload = function () {
                if (request.status === 200) {
                    alert("폼이 성공적으로 제출되었습니다.");
                    window.location.href = registerForm.action.replace("register", "list");
                    // 성공적으로 제출된 후 처리 로직을 추가할 수 있습니다.
                } else {
                    alert("폼 제출에 실패했습니다.");
                    console.error("에러 상태 코드:", request.status);
                    console.error("응답 내용:", request.responseText);
                }
            };

            // 폼 데이터를 전송
            request.send(formData);
        });
 */
        // 해시태그 목록 저장용
        let hashtags = [];

        // 엔터 키 입력 시 해시태그 추가
        hashtagInput.addEventListener("keydown", function (e) {
            if (e.key === "Enter") {
                e.preventDefault(); // 폼 제출 방지
                const value = hashtagInput.value.trim(); // 공백 제거
                if (value && !hashtags.includes(value)) {
                    console.log("New hashtag entered:", value); // 디버깅: 입력값 확인
                    addHashtag(value);
                    hashtagInput.value = ""; // 입력 필드 초기화
                } else if (hashtags.includes(value)) {
                    console.log("Duplicate hashtag ignored:", value); // 중복 확인
                    alert("This hashtag is already added.");
                } else {
                    console.log("Invalid or empty hashtag ignored."); // 유효하지 않은 값
                }
            }
        });

        // 해시태그 추가 함수
        function addHashtag(value) {
		    console.log("Adding hashtag to UI:", value); // 디버깅용
		
		    // 해시태그 목록에 추가
		    hashtags.push(value);
		
		    // UI에 해시태그 추가
		    const hashtagElement = document.createElement("div");
		    hashtagElement.className = "hashtag";
		
		    // 해시태그 텍스트 추가
		    const hashtagText = document.createTextNode("#" + value); // 문자열 결합으로 해시태그 표시
		    hashtagElement.appendChild(hashtagText);
		
		    // X 버튼 추가
		    const removeButton = document.createElement("span");
		    removeButton.className = "remove-hashtag";
		    removeButton.textContent = "x";
		    removeButton.addEventListener("click", function () {
		        removeHashtag(value, hashtagElement);
		    });
		
		    hashtagElement.appendChild(removeButton);
		    hashtagList.appendChild(hashtagElement);
		
		    console.log("Hashtag added to UI element:", hashtagElement); // 디버깅용
		
		    // 히든 필드 업데이트
		    updateHiddenInput();
		}

        // 해시태그 제거 함수
        function removeHashtag(value, element) {
            console.log("Hashtag removed:", value); // 디버깅: 제거된 값 확인
            hashtags = hashtags.filter((tag) => tag !== value); // 목록에서 제거
            hashtagList.removeChild(element); // UI에서 제거

            // 히든 필드 업데이트
            updateHiddenInput();
        }

        // 히든 필드 업데이트
        function updateHiddenInput() {
            hashtagsHiddenInput.value = hashtags.join(","); // 콤마로 연결된 문자열 저장
            console.log("Updated hidden input value:", hashtagsHiddenInput.value); // 디버깅: 숨겨진 필드 값 확인
        }
    });

        function validateAndPreviewThumbnail(input) {
            const previewImage = document.getElementById('thumbnailPreview');
            const noThumbnailMessage = document.getElementById('noThumbnailMessage');
            const allowedExtensions = ['jpeg', 'jpg', 'png', 'gif'];

            if (input.files && input.files[0]) {
                const file = input.files[0];
                const fileName = file.name.toLowerCase();
                const fileExtension = fileName.split('.').pop();

                // 유효성 검사: 확장자 체크
                if (!allowedExtensions.includes(fileExtension)) {
                    alert('Invalid file type. Please upload an image (JPEG, PNG, GIF).');
                    input.value = ''; // 파일 입력 초기화
                    previewImage.style.display = 'none';
                    noThumbnailMessage.style.display = 'block';
                    return;
                }

                // 이미지 파일 미리보기
                const reader = new FileReader();
                reader.onload = function (e) {
                    previewImage.src = e.target.result;
                    previewImage.style.display = 'block';
                    noThumbnailMessage.style.display = 'none';
                };
                reader.readAsDataURL(file);
            } else {
                // 파일이 선택되지 않았을 경우 초기화
                previewImage.style.display = 'none';
                noThumbnailMessage.style.display = 'block';
            }
        }
    </script>
</head>
<body>
    <h1>Register a New Recipe</h1>
    <form id="registerForm" action="${pageContext.request.contextPath}/recipeboard/register" method="post" enctype="multipart/form-data">
        <sec:authorize access="isAuthenticated()">
		    <sec:authentication var="customUser" property="principal" />
		    <input type="hidden" id="memberId" name="memberId" value="${customUser.memberVO.memberId}">
		</sec:authorize>
        
        <!-- Title -->
        <label for="recipeBoardTitle">Title:</label>
        <input type="text" id="recipeBoardTitle" name="recipeBoardTitle" required>
        <br><br>

        <!-- Content -->
        <label for="recipeBoardContent">Content:</label>
        <textarea id="recipeBoardContent" name="recipeBoardContent" rows="5" cols="50" required></textarea>
        <br><br>

        <!-- Thumbnail Upload -->
        <label for="thumbnail">Thumbnail Image (Required):</label>
        <input type="file" id="thumbnail" name="thumbnail" accept="image/*" onchange="validateAndPreviewThumbnail(this)" required>
        <div class="thumbnail-preview">
            <p id="noThumbnailMessage" style="color: red;">No thumbnail selected. Please upload an image.</p>
            <img id="thumbnailPreview" src="#" alt="Thumbnail Preview" style="display: none;">
        </div>
        <br><br>

        <!-- Hashtags -->
        <div class="selection-container">
            <h3>Hashtags</h3>
            <input type="text" id="hashtagInput" placeholder="Enter hashtag and press Enter" />
            <div id="hashtagList" class="hashtag-container"></div>
            <!-- Hidden input to store hashtags as comma-separated values -->
            <input type="hidden" id="hashtagsHiddenInput" name="hashtags">
        </div>
        <br><br>

        <!-- Types (Single Selection) -->
        <div class="selection-container">
            <h3>종류별</h3>
            <ul>
                <c:forEach var="type" items="${typesList}">
                    <c:if test="${type.typeId != 1}">
                        <li>
                            <input type="radio" name="typeId" value="${type.typeId}" required>
                            ${type.typeName}
                        </li>
                    </c:if>
                </c:forEach>
            </ul>
        </div>

        <!-- Situations (Single Selection) -->
        <div class="selection-container">
            <h3>상황별</h3>
            <ul>
                <c:forEach var="situation" items="${situationsList}">
                    <c:if test="${situation.situationId != 1}">
                        <li>
                            <input type="radio" name="situationId" value="${situation.situationId}" required>
                            ${situation.situationName}
                        </li>
                    </c:if>
                </c:forEach>
            </ul>
        </div>

        <!-- Methods (Single Selection) -->
        <div class="selection-container">
            <h3>방법별</h3>
            <ul>
                <c:forEach var="method" items="${methodsList}">
                    <c:if test="${method.methodId != 1}">
                        <li>
                            <input type="radio" name="methodId" value="${method.methodId}" required>
                            ${method.methodName}
                        </li>
                    </c:if>
                </c:forEach>
            </ul>
        </div>

        <!-- Ingredients (Multiple Selection) -->
        <div class="selection-container">
            <h3>재료별</h3>
            <ul>
                <c:forEach var="ingredient" items="${ingredientsList}">
                    <c:if test="${ingredient.ingredientId != 1}">
                        <li>
                            <input type="checkbox" name="ingredientIds" value="${ingredient.ingredientId}">
                            ${ingredient.ingredientName}
                        </li>
                    </c:if>
                </c:forEach>
            </ul>
        </div>
		<input type="hidden" name="${_csrf.parameterName }" value="${_csrf.token }">
        <!-- Submit Button -->
        <button type="submit">Submit</button>
    </form>
</body>
</html>
