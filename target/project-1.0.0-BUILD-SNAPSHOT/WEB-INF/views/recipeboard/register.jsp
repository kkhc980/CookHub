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
    </style>
</head>
<body>
    <h1>Register a New Recipe</h1>
    <form action="${pageContext.request.contextPath}/recipeboard/register" method="post" enctype="multipart/form-data">
        <!-- Title -->
        <label for="memberId">Member ID:</label>
        <input type="text" id="memberId" name="memberId" required>
        <br><br>
        
        <label for="recipeBoardTitle">Title:</label>
        <input type="text" id="recipeBoardTitle" name="recipeBoardTitle" required>
        <br><br>

        <!-- Content -->
        <label for="recipeBoardContent">Content:</label>
        <textarea id="recipeBoardContent" name="recipeBoardContent" rows="5" cols="50" required></textarea>
        <br><br>
        
        <!-- Thumbnail Upload -->
	    <label for="thumbnail">Thumbnail Image:</label>
	    <input type="file" id="thumbnail" name="thumbnail" accept="image/*" required>
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
