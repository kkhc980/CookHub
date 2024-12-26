<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html>
<head>
    <title>Update Recipe</title>
</head>
<body>
    <h1>Update Recipe</h1>
    <form action="${pageContext.request.contextPath}/recipeboard/update" method="post">
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
        <label for="type">Type:</label>
        <select id="type" name="typeId">
            <c:forEach var="type" items="${typesList}">
                <option value="${type.typeId}" 
                    <c:if test="${recipeBoard.typeId == type.typeId}">selected</c:if>>
                    ${type.typeName}
                </option>
            </c:forEach>
        </select>
        <br>

        <!-- Recipe Method -->
        <label for="method">Method:</label>
        <select id="method" name="methodId">
            <c:forEach var="method" items="${methodsList}">
                <option value="${method.methodId}" 
                    <c:if test="${recipeBoard.methodId == method.methodId}">selected</c:if>>
                    ${method.methodName}
                </option>
            </c:forEach>
        </select>
        <br>

        <!-- Recipe Situation -->
        <label for="situation">Situation:</label>
        <select id="situation" name="situationId">
            <c:forEach var="situation" items="${situationsList}">
                <option value="${situation.situationId}" 
                    <c:if test="${recipeBoard.situationId == situation.situationId}">selected</c:if>>
                    ${situation.situationName}
                </option>
            </c:forEach>
        </select>
        <br>

        <!-- Recipe Ingredients -->
        <label for="ingredients">Ingredients:</label>
        <c:forEach var="ingredient" items="${allIngredients}">
            <input type="checkbox" name="ingredientIds" value="${ingredient.ingredientId}" 
                <c:if test="${fn:contains(selectedIngredientIds, ingredient.ingredientId)}">checked</c:if>>
            ${ingredient.ingredientName}
        </c:forEach>
        <br>

        <!-- Submit Button -->
        <button type="submit">Update</button>
    </form>
</body>
</html>
