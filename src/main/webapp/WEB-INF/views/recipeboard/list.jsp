<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

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
    <script>
	    document.addEventListener("DOMContentLoaded", function () {
	        const selectedIngredients = new Set();
	        const allButton = document.querySelector(".ingredient-button[data-id='1']");
	        const buttons = document.querySelectorAll(".ingredient-button:not([data-id='1'])");
	
	        // "전체" 버튼 클릭 시 다른 버튼 선택 해제
	        allButton.addEventListener("click", function () {
	            if (allButton.classList.contains("active")) {
	                allButton.classList.remove("active");
	                selectedIngredients.delete("1");
	            } else {
	                allButton.classList.add("active");
	                selectedIngredients.clear();
	                selectedIngredients.add("1");
	
	                // 다른 버튼 선택 해제
	                buttons.forEach(button => button.classList.remove("active"));
	            }
	        });
	
	        // 개별 버튼 클릭 시 "전체" 버튼 선택 해제
	        buttons.forEach(button => {
	            button.addEventListener("click", function () {
	                if (button.classList.contains("active")) {
	                    button.classList.remove("active");
	                    selectedIngredients.delete(button.dataset.id);
	                } else {
	                    button.classList.add("active");
	                    selectedIngredients.add(button.dataset.id);
	                }
	
	                // "전체" 버튼 선택 해제
	                if (allButton.classList.contains("active")) {
	                    allButton.classList.remove("active");
	                    selectedIngredients.delete("1");
	                }
	            });
	        });
	
	        // 검색 폼 제출 시 선택된 재료 ID 전송
	        document.getElementById("searchForm").addEventListener("submit", function (event) {
	            const ingredientInput = document.getElementById("ingredientIdsInput");
	            ingredientInput.value = Array.from(selectedIngredients).join(",");
	        });
	    });
	</script>
</head>
<body>
    <h1>Recipe Board</h1>


	<!-- 등록 버튼 -->
    <button class="register-button" onclick="location.href='${pageContext.request.contextPath}/recipeboard/register'">
        Register
    </button>
    <!-- 검색 폼 -->
  <form id="searchForm" action="${pageContext.request.contextPath}/recipeboard/list" method="get">
    <input type="hidden" id="ingredientIdsInput" name="ingredientIds">

    <!-- Types 필터 -->
    <h3>Select Type:</h3>
    <c:forEach var="type" items="${allTypes}">
        <input type="radio" name="typeId" value="${type.typeId}" id="type_${type.typeId}">
        <label for="type_${type.typeId}">${type.typeName}</label>
    </c:forEach>


    <!-- Methods 필터 -->
    <h3>Select Method:</h3>
    <c:forEach var="method" items="${allMethods}">
        <input type="radio" name="methodId" value="${method.methodId}" id="method_${method.methodId}">
        <label for="method_${method.methodId}">${method.methodName}</label>
    </c:forEach>
    
    <!-- Situations 필터 -->
    <h3>Select Situation:</h3>
    <c:forEach var="situation" items="${allSituations}">
        <input type="radio" name="situationId" value="${situation.situationId}" id="situation_${situation.situationId}">
        <label for="situation_${situation.situationId}">${situation.situationName}</label>
    </c:forEach>


    <!-- Ingredients 필터 -->
    <h3>Select Ingredients:</h3>
    <c:forEach var="ingredient" items="${allIngredients}">
        <button type="button" class="ingredient-button" data-id="${ingredient.ingredientId}">
            ${ingredient.ingredientName}
        </button>
    </c:forEach>

    <button type="submit">Search</button>
</form>

    <!-- 게시글 목록 -->
    <table border="1">
        <thead>
            <tr>
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
                <tr>
                    <td>${recipe.recipeBoardId}</td>
                    <td>
                        <a href="${pageContext.request.contextPath}/recipeboard/detail/${recipe.recipeBoardId}">
                            ${recipe.recipeBoardTitle}
                        </a>
                    </td>
                    <td>${recipe.recipeBoardContent}</td>
                    <td>${recipe.recipeBoardCreatedDate}</td>
                    <td>${recipe.viewCount}</td>
                    <td>${recipe.avgRating}</td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

    <!-- 페이징 -->
    <div>
    <c:if test="${pageMaker.prev}">
        <a href="${pageContext.request.contextPath}/recipeboard/list?pageNum=${pageMaker.startNum - 1}
            &ingredientIds=${param.ingredientIds}
            &typeId=${param.typeId}
            &methodId=${param.methodId}
            &situationId=${param.situationId}">
            Previous
        </a>
    </c:if>

    <c:forEach var="pageNum" begin="${pageMaker.startNum}" end="${pageMaker.endNum}">
        <a href="${pageContext.request.contextPath}/recipeboard/list?pageNum=${pageNum}
            &ingredientIds=${param.ingredientIds}
            &typeId=${param.typeId}
            &methodId=${param.methodId}
            &situationId=${param.situationId}"
           style="${pagination.pageNum == pageNum ? 'font-weight:bold;' : ''}">
            ${pageNum}
        </a>
    </c:forEach>

    <c:if test="${pageMaker.next}">
        <a href="${pageContext.request.contextPath}/recipeboard/list?pageNum=${pageMaker.endNum + 1}
            &ingredientIds=${param.ingredientIds}
            &typeId=${param.typeId}
            &methodId=${param.methodId}
            &situationId=${param.situationId}">
            Next
        </a>
    </c:if>
</div>
</body>
</html>
