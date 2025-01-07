<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html>
<head>
    <title>Update Recipe</title>
    <style>
        .thumbnail-preview {
            margin-top: 10px;
        }
        .thumbnail-preview img {
            max-width: 150px; /* 적당한 크기로 이미지 미리보기 */
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
    </style>
    <script>
        // 파일 선택 시 썸네일 미리보기
        function previewThumbnail(input) {
            const previewImage = document.getElementById('thumbnailPreview');
            if (input.files && input.files[0]) {
                const reader = new FileReader();
                reader.onload = function (e) {
                    previewImage.src = e.target.result;
                };
                reader.readAsDataURL(input.files[0]);
            }
        }
    </script>
</head>
<body>
    <h1>Update Recipe</h1>
    <form action="${pageContext.request.contextPath}/recipeboard/update" method="post" enctype="multipart/form-data">
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

        <!-- Thumbnail Upload -->
        <label for="thumbnail">Thumbnail:</label>
        <div class="thumbnail-preview">
            <!-- 현재 썸네일 미리보기 -->
            <img id="thumbnailPreview" 
                 src="${pageContext.request.contextPath}/recipeboard/thumbnail/${recipeBoard.recipeBoardId}" 
                 alt="Current Thumbnail">
        </div>
        <br>
        <input type="file" id="thumbnail" name="thumbnail" accept="image/*" onchange="previewThumbnail(this)" required>
        <br><br>

        <!-- Submit Button -->
        <button type="submit">Update</button>
    </form>
</body>
</html>
