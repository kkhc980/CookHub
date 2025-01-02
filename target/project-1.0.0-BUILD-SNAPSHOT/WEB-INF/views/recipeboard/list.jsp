<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html>
<head>
    <title>Recipe Board List</title>
    <style>
        .ingredient-button {
            margin: 5px;
            padding: 10px;
            border: 1px solid #ddd;
            background-color: #f5f5f5;
            cursor: pointer;
        }
        .ingredient-button.active {
            background-color: #4caf50;
            color: white;
            border: 1px solid #4caf50;
        }
        .ingredient-button[disabled] {
            background-color: #ccc;
            cursor: not-allowed;
        }
    </style>
</head>
<body>
    <h1>Recipe Board</h1>

	<div style="margin-bottom: 20px;">
        <a href="${pageContext.request.contextPath}/recipeboard/register" style="
            display: inline-block;
            padding: 10px 20px;
            background-color: #4caf50;
            color: white;
            text-decoration: none;
            border-radius: 5px;
            font-weight: bold;">
            Register New Recipe
        </a>
    </div>
    <!-- 필터 및 검색 -->
    <form id="searchForm" action="${pageContext.request.contextPath}/recipeboard/list" method="get">
        <input type="hidden" id="ingredientIdsInput" name="ingredientIds" value="${ingredientIdsStr}">

        <!-- Ingredients 필터 -->
        <h3>Select Ingredients:</h3>
        <c:forEach var="ingredient" items="${allIngredients}">
		    <button 
		        type="button" 
		        class="ingredient-button ${selectedIngredientIds.contains(ingredient.ingredientId) || (fn:length(selectedIngredientIds) == 0 && ingredient.ingredientId == 1) ? 'active' : ''}" 
		        data-id="${ingredient.ingredientId}">
		        ${ingredient.ingredientName}
		    </button>
		</c:forEach>

        <!-- Type 필터 -->
        <h3>Select Type:</h3>
        <c:forEach var="type" items="${allTypes}">
            <input type="radio" name="typeId" value="${type.typeId}" id="type_${type.typeId}" ${selectedTypeId == type.typeId ? 'checked' : ''}>
            <label for="type_${type.typeId}">${type.typeName}</label>
        </c:forEach>

        <!-- 추가 필터 -->
        <h3>Select Situation:</h3>
        <c:forEach var="situation" items="${allSituations}">
            <input type="radio" name="situationId" value="${situation.situationId}" id="situation_${situation.situationId}" ${selectedSituationId == situation.situationId ? 'checked' : ''}>
            <label for="situation_${situation.situationId}">${situation.situationName}</label>
        </c:forEach>

        <h3>Select Method:</h3>
        <c:forEach var="method" items="${allMethods}">
            <input type="radio" name="methodId" value="${method.methodId}" id="method_${method.methodId}" ${selectedMethodId == method.methodId ? 'checked' : ''}>
            <label for="method_${method.methodId}">${method.methodName}</label>
        </c:forEach>

        <button type="submit">Search</button>
    </form>

    <script>
    document.addEventListener("DOMContentLoaded", function () {
        const ingredientButtons = document.querySelectorAll(".ingredient-button");
        const ingredientIdsInput = document.getElementById("ingredientIdsInput");
        const selectedIngredients = new Set(ingredientIdsInput.value ? ingredientIdsInput.value.split(",") : ["1"]); // 기본 값 "1" (전체)

        // 버튼 상태 업데이트 함수
        function updateButtonState(button, activate) {
            if (activate) {
                button.classList.add("active");
                selectedIngredients.add(button.dataset.id);
            } else {
                button.classList.remove("active");
                selectedIngredients.delete(button.dataset.id);
            }
        }

        ingredientButtons.forEach(button => {
            // 기존 상태에 따라 버튼 활성화
            if (selectedIngredients.has(button.dataset.id)) {
                button.classList.add("active");
            }

            button.addEventListener("click", function () {
                if (button.dataset.id === "1") {
                    // "전체(All)" 버튼 클릭 시 다른 버튼 비활성화
                    ingredientButtons.forEach(btn => updateButtonState(btn, false));
                    updateButtonState(button, true);
                } else {
                    // 다른 버튼 클릭 시 "전체(All)" 비활성화
                    updateButtonState(button, !button.classList.contains("active"));
                    if (selectedIngredients.has("1")) selectedIngredients.delete("1");
                    document.querySelector("[data-id='1']").classList.remove("active");
                }
                ingredientIdsInput.value = Array.from(selectedIngredients).join(",");
            });
        });
    });
    </script>

    <!-- 게시글 목록 -->
    <table border="1">
        <thead>
            <tr>
            	<th>Thumbnail</th>
                <th>ID</th>
                <th>Title</th>
                <th>Content</th>
                <th>Created Date</th>
                <th>View Count</th>
                <th>Average Rating</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="recipe" items="${recipeList}">
                <tr onclick="location.href='${pageContext.request.contextPath}/recipeboard/detail/${recipe.recipeBoardId}'" style="cursor: pointer;">
                     <td>
                    <img src="${pageContext.request.contextPath}/uploads/${recipe.thumbnailPath}" alt="Thumbnail" width="100" height="100">
                	</td>
                    <td>${recipe.recipeBoardId}</td>
                    <td>${recipe.recipeBoardTitle}</td>
                    <td>${recipe.recipeBoardContent}</td>
                    <td>${recipe.recipeBoardCreatedDate}</td>
                    <td>${recipe.viewCount}</td>
                    <td>${recipe.avgRating}</td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

    <!-- 페이징 -->
    <div id="pagination">
        <c:if test="${pageMaker.prev}">
            <a href="?pageNum=${pageMaker.startNum - 1}&typeId=${selectedTypeId}&methodId=${selectedMethodId}&situationId=${selectedSituationId}&ingredientIds=${ingredientIdsStr}">
                Previous
            </a>
        </c:if>
        <c:forEach var="pageNum" begin="${pageMaker.startNum}" end="${pageMaker.endNum}">
            <a href="?pageNum=${pageNum}&typeId=${selectedTypeId}&methodId=${selectedMethodId}&situationId=${selectedSituationId}&ingredientIds=${ingredientIdsStr}"
               style="${pagination.pageNum == pageNum ? 'font-weight:bold;' : ''}">
                ${pageNum}
            </a>
        </c:forEach>
        <c:if test="${pageMaker.next}">
            <a href="?pageNum=${pageMaker.endNum + 1}&typeId=${selectedTypeId}&methodId=${selectedMethodId}&situationId=${selectedSituationId}&ingredientIds=${ingredientIdsStr}">
                Next
            </a>
        </c:if>
    </div>
</body>
</html>