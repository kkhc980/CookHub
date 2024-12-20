<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Recipe Detail</title>
    <style>
        .button-container {
            margin-top: 20px;
        }
        .button-container button {
            margin-right: 10px;
            padding: 10px 15px;
            font-size: 14px;
            cursor: pointer;
        }
    </style>
</head>
<body>
    <h1>${recipeBoard.recipeBoardTitle}</h1>
    <p>Written by Member ID: ${recipeBoard.memberId}</p>

    <p><span class="label">Content:</span> ${recipeBoard.recipeBoardContent}</p>
    <p><span class="label">Type:</span> ${typeName}</p>
    <p><span class="label">Method:</span> ${methodName}</p>
    <p><span class="label">Situation:</span> ${situationName}</p>

    <p><span class="label">Ingredients:</span></p>
    <ul>
        <c:forEach var="ingredient" items="${ingredients}">
            <li>${ingredient.ingredientName}</li>
        </c:forEach>
    </ul>

    <p><span class="label">Average Rating:</span> ${recipeBoard.avgRating}</p>

    <!-- Buttons -->
    <div class="button-container">
	    <!-- Update Button -->
	    <button onclick="location.href='${pageContext.request.contextPath}/recipeboard/update/${recipeBoard.recipeBoardId}'">Update</button>
	    
	    <!-- Delete Button -->
	    <form action="${pageContext.request.contextPath}/recipeboard/delete/${recipeBoard.recipeBoardId}" method="post" style="display: inline;">
	        <button type="submit" onclick="return confirm('Are you sure you want to delete this recipe?');">Delete</button>
	    </form>
	</div>


    <!-- Back Button -->
    <button onclick="location.href='${pageContext.request.contextPath}/recipeboard/list'">Back to List</button>
</body>
</html>
