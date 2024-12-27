<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Update Recipe</title>
    <style>
        .form-container {
            width: 50%;
            margin: 0 auto;
        }
        .form-container h1 {
            text-align: center;
        }
        .form-container form {
            display: flex;
            flex-direction: column;
        }
        .form-container label {
            margin-top: 10px;
            font-weight: bold;
        }
        .form-container input, .form-container textarea, .form-container select {
            margin-top: 5px;
            padding: 8px;
            font-size: 14px;
        }
        .form-container button {
            margin-top: 20px;
            padding: 10px;
            background-color: #4CAF50;
            color: white;
            border: none;
            cursor: pointer;
            font-size: 16px;
        }
        .form-container button:hover {
            background-color: #45a049;
        }
    </style>
</head>
<body>
    <div class="form-container">
        <h1>Update Recipe</h1>
        <form action="${pageContext.request.contextPath}/recipeboard/update/${recipeBoard.recipeBoardId}" method="post">
            <!-- Title -->
            <label for="recipeBoardTitle">Title:</label>
            <input type="text" id="recipeBoardTitle" name="recipeBoardTitle" value="${recipeBoard.recipeBoardTitle}" required>
            
            <!-- Content -->
            <label for="recipeBoardContent">Content:</label>
            <textarea id="recipeBoardContent" name="recipeBoardContent" rows="5" required>${recipeBoard.recipeBoardContent}</textarea>

            <!-- Types -->
            <label for="typeId">Type:</label>
            <select id="typeId" name="typeId" required>
                <c:forEach var="type" items="${typesList}">
                    <c:if test="${type.typeId != 1}">
                        <option value="${type.typeId}" ${type.typeId == recipeBoard.typeId ? 'selected' : ''}>${type.typeName}</option>
                    </c:if>
                </c:forEach>
            </select>

            <!-- Methods -->
            <label for="methodId">Method:</label>
            <select id="methodId" name="methodId" required>
                <c:forEach var="method" items="${methodsList}">
                    <c:if test="${method.methodId != 1}">
                        <option value="${method.methodId}" ${method.methodId == recipeBoard.methodId ? 'selected' : ''}>${method.methodName}</option>
                    </c:if>
                </c:forEach>
            </select>

            <!-- Situations -->
            <label for="situationId">Situation:</label>
            <select id="situationId" name="situationId" required>
                <c:forEach var="situation" items="${situationsList}">
                    <c:if test="${situation.situationId != 1}">
                        <option value="${situation.situationId}" ${situation.situationId == recipeBoard.situationId ? 'selected' : ''}>${situation.situationName}</option>
                    </c:if>
                </c:forEach>
            </select>

            <!-- Ingredients -->
            <label for="ingredientIds">Ingredients:</label>
            <c:forEach var="ingredient" items="${ingredientsList}">
                <c:if test="${ingredient.ingredientId != 1}">
                    <div>
                        <input type="checkbox" id="ingredient${ingredient.ingredientId}" name="ingredientIds" value="${ingredient.ingredientId}"
                            <c:if test="${recipeIngredientIds.contains(ingredient.ingredientId)}">checked</c:if>>
                        <label for="ingredient${ingredient.ingredientId}">${ingredient.ingredientName}</label>
                    </div>
                </c:if>
            </c:forEach>

            <!-- Submit Button -->
            <button type="submit">Update Recipe</button>
        </form>
    </div>
</body>
</html>
