<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html>
<head>
    <title>Recipe Board List</title>
</head>
<body>
    <h1>Recipe Board</h1>

    <!-- Filter Buttons -->
    <div class="filter-container">
        <h3>종류별</h3>
        <ul>
            <c:forEach var="type" items="${typesList}">
                <li>
                    <a href="${pageContext.request.contextPath}/recipeboard/filter?typeId=${type.typeId}&situationId=${selectedSituationId}&methodId=${selectedMethodId}&ingredientId=${selectedIngredientId}">
                        ${type.typeName}
                    </a>
                </li>
            </c:forEach>
        </ul>
    </div>

    <div class="filter-container">
        <h3>상황별</h3>
        <ul>
            <c:forEach var="situation" items="${situationsList}">
                <li>
                    <a href="${pageContext.request.contextPath}/recipeboard/filter?typeId=${selectedTypeId}&situationId=${situation.situationId}&methodId=${selectedMethodId}&ingredientId=${selectedIngredientId}">
                        ${situation.situationName}
                    </a>
                </li>
            </c:forEach>
        </ul>
    </div>

    <div class="filter-container">
        <h3>방법별</h3>
        <ul>
            <c:forEach var="method" items="${methodsList}">
                <li>
                    <a href="${pageContext.request.contextPath}/recipeboard/filter?typeId=${selectedTypeId}&situationId=${selectedSituationId}&methodId=${method.methodId}&ingredientId=${selectedIngredientId}">
                        ${method.methodName}
                    </a>
                </li>
            </c:forEach>
        </ul>
    </div>

  <div class="filter-container">
    <h3>재료별</h3>
    <ul>
        <c:forEach var="ingredient" items="${ingredientsList}">
            <li>
                <!-- 현재 ingredientId가 선택된 상태인지 확인 -->
                <c:set var="selectedIngredients" value="${selectedIngredientIds != null ? selectedIngredientIds : []}" />
                <c:choose>
                    <c:when test="${selectedIngredients.contains(ingredient.ingredientId)}">
                        <!-- 이미 선택된 경우: 선택 해제 -->
                        <a href="${pageContext.request.contextPath}/recipeboard/filter?typeId=${selectedTypeId}&situationId=${selectedSituationId}&methodId=${selectedMethodId}&ingredientIds=${fn:join(selectedIngredients, ',')}">
                            (선택 해제) ${ingredient.ingredientName}
                        </a>
                    </c:when>
                    <c:otherwise>
                        <!-- 선택되지 않은 경우: 선택 추가 -->
                        <a href="${pageContext.request.contextPath}/recipeboard/filter?typeId=${selectedTypeId}&situationId=${selectedSituationId}&methodId=${selectedMethodId}&ingredientIds=${fn:join(selectedIngredients, ',')},${ingredient.ingredientId}">
                            ${ingredient.ingredientName}
                        </a>
                    </c:otherwise>
                </c:choose>
            </li>
        </c:forEach>
    </ul>
</div>

    <div class="button-container">
        <button onclick="location.href='${pageContext.request.contextPath}/recipeboard/register'">Register</button>
    </div>

    <!-- Recipe Board List -->
    <table>
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
                    <td><fmt:formatDate value="${recipe.recipeBoardCreatedDate}" pattern="yyyy-MM-dd" /></td>
                    <td>${recipe.viewCount}</td>
                    <td>${recipe.avgRating}</td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</body>
</html>