<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html>
<head>
    <title>Recipe Board List</title>
</head>
<style>
    .filter-category {
        margin-bottom: 20px;
    }

    .filter-category h4 {
        margin-bottom: 10px;
        font-size: 16px;
        font-weight: bold;
    }

    .filter-category button {
        margin: 5px;
        padding: 10px 20px;
        font-size: 14px;
        border: 1px solid #ddd;
        background-color: #f9f9f9;
        cursor: pointer;
        border-radius: 5px;
        transition: background-color 0.3s;
    }

    .filter-category button:hover {
        background-color: #e0e0e0;
    }

    .filter-category button:active {
        background-color: #ccc;
    }

    .filter-category button.active {
        background-color: #4CAF50;
        color: white;
        border-color: #4CAF50;
    }
</style>


<body>
    <h1>Recipe Board</h1>
 <form id="filterForm" method="get" action="${pageContext.request.contextPath}/recipeboard/filter">
    <!-- 종류별 -->
    <div class="filter-category">
        <h4>종류별</h4>
        <button type="button" onclick="applyFilter('type', '')" class="${param.type == null || param.type == '' ? 'active' : ''}">전체</button>
        <c:forEach items="${typesList}" var="type">
            <c:if test="${type.typeId != 1}">
                <button type="button" onclick="applyFilter('type', '${type.typeId}')" class="${param.type == type.typeId ? 'active' : ''}">
                    ${type.typeName}
                </button>
            </c:if>
        </c:forEach>
    </div>

    <!-- 상황별 -->
    <div class="filter-category">
        <h4>상황별</h4>
        <button type="button" onclick="applyFilter('situation', '')" class="${param.situation == null || param.situation == '' ? 'active' : ''}">전체</button>
        <c:forEach items="${situationsList}" var="situation">
            <c:if test="${situation.situationId != 1}">
                <button type="button" onclick="applyFilter('situation', '${situation.situationId}')" class="${param.situation == situation.situationId ? 'active' : ''}">
                    ${situation.situationName}
                </button>
            </c:if>
        </c:forEach>
    </div>

    <!-- 재료별 -->
    <div class="filter-category">
        <h4>재료별</h4>
        <button type="button" onclick="applyFilter('ingredient', '')" class="${param.ingredient == null || param.ingredient == '' ? 'active' : ''}">전체</button>
        <c:forEach items="${ingredientsList}" var="ingredient">
            <c:if test="${ingredient.ingredientId != 1}">
                <button type="button" onclick="applyFilter('ingredient', '${ingredient.ingredientId}')" class="${param.ingredient == ingredient.ingredientId ? 'active' : ''}">
                    ${ingredient.ingredientName}
                </button>
            </c:if>
        </c:forEach>
    </div>

    <!-- 방법별 -->
    <div class="filter-category">
        <h4>방법별</h4>
        <button type="button" onclick="applyFilter('method', '')" class="${param.method == null || param.method == '' ? 'active' : ''}">전체</button>
        <c:forEach items="${methodsList}" var="method">
            <c:if test="${method.methodId != 1}">
                <button type="button" onclick="applyFilter('method', '${method.methodId}')" class="${param.method == method.methodId ? 'active' : ''}">
                    ${method.methodName}
                </button>
            </c:if>
        </c:forEach>
    </div>

    <!-- Hidden Fields -->
    <input type="hidden" id="type" name="type" value="${param.type}" />
    <input type="hidden" id="situation" name="situation" value="${param.situation}" />
    <input type="hidden" id="ingredient" name="ingredient" value="${param.ingredient}" />
    <input type="hidden" id="method" name="method" value="${param.method}" />

    <button type="submit" style="display:none;" id="submitFilter"></button>
</form>

<script>
    function applyFilter(category, value) {
        // Set the hidden field for the clicked category
        document.getElementById(category).value = value;

        // Automatically submit the form with updated filters
        document.getElementById('submitFilter').click();
    }
</script>

    <button onclick="location.href='${pageContext.request.contextPath}/recipeboard/register'">Register</button>
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
                    <td><fmt:formatDate value="${recipe.recipeBoardCreatedDate}" pattern="yyyy-MM-dd"/></td>
                    <td>${recipe.viewCount}</td>
                    <td>${recipe.avgRating}</td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</body>
</html>
