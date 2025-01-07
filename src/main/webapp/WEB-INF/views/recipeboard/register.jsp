<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Register Recipe</title>
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
    </style>
    
    <script>
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
    <form action="${pageContext.request.contextPath}/recipeboard/register" method="post" enctype="multipart/form-data">
        <input type="hidden" id="memberId" name="memberId" value="1">
        
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

        <!-- Submit Button -->
        <button type="submit">Submit</button>
    </form>
</body>
</html>
