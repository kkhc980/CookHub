<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Recipe Detail</title>
    <script>
        // Delete 확인 후 요청
        function confirmDelete(recipeBoardId) {
            if (confirm("Are you sure you want to delete this recipe?")) {
                location.href = '${pageContext.request.contextPath}/recipeboard/delete/' + recipeBoardId;
            }
        }
    </script>
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

    <!-- 버튼 영역 -->
    <div>
        <button onclick="location.href='${pageContext.request.contextPath}/recipeboard/update/${recipeBoard.recipeBoardId}'">
            Update
        </button>
        <form action="${pageContext.request.contextPath}/recipeboard/delete/${recipeBoard.recipeBoardId}" method="post" style="display:inline;">
    	<button type="submit" onclick="return confirm('Are you sure you want to delete this recipe?');">Delete</button>
		</form>
        <button onclick="location.href='${pageContext.request.contextPath}/recipeboard/list'">Back to List</button>
    </div>
</body>
</html>
