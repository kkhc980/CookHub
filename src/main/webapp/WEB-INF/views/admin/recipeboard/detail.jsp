<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Recipe Detail</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@400;700&display=swap" rel="stylesheet">
    <style>
        body {
            font-family: 'Noto Sans KR', sans-serif;
            background-color: #f8f9fa;
            margin: 0;
            padding: 20px;
        }
        .container {
            max-width: 800px;
            margin: 0 auto;
            padding: 30px;
            background-color: #fff;
            border-radius: 8px;
            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
        }
        .detail-container > div {
            margin-bottom: 20px;
        }
        .detail-container .section-title {
            font-size: 1.3rem;
            margin-bottom: 15px;
            color: #343a40;
            border-bottom: 1px solid #dee2e6;
            padding-bottom: 5px;
        }
        .detail-container .info-item {
            margin-bottom: 5px;
            color: #495057;
            display: flex;
            align-items: center;
        }
        .detail-container .info-item .label {
            font-weight: bold;
            margin-right: 5px;
            min-width: 60px;
        }
        .detail-container textarea {
            width: 100%;
            padding: 10px;
            border: 1px solid #ced4da;
            border-radius: 4px;
            font-size: 1rem;
            box-sizing: border-box;
            margin-top: 5px;
            resize: vertical;
            font-family: 'Noto Sans KR', sans-serif;
            color: #495057;
            outline: none;
        }
        .detail-container ul {
            list-style: none;
            padding: 0;
        }
       .detail-container ul li {
            margin-bottom: 0px;
            color: #495057;
        }
        .detail-container img {
            max-width: 100%;
            max-height: 300px;
            display: block;
            margin: 10px auto;
        }
        .hashtags {
            margin-top: 20px;
        }
        .hashtags span {
            display: inline-block;
            background-color: #f1f1f1;
            padding: 5px 10px;
            margin: 5px;
            border-radius: 15px;
            font-size: 14px;
            color: #333;
        }
        .step-row {
            margin-bottom: 20px;
            border: 1px solid #dee2e6;
            padding: 15px;
            border-radius: 8px;
        }
        .step-row .step-label {
            font-weight: bold;
            margin-bottom: 10px;
            font-size: 1.1rem;
            display: block;
        }
        .step-row textarea {
            width: 100%;
            padding: 10px;
            border: 1px solid #ced4da;
            border-radius: 4px;
            margin-bottom: 10px;
            font-size: 1rem;
            resize: vertical;
            font-family: 'Noto Sans KR', sans-serif;
            color: #495057;
            outline: none;
        }
        .step-row img {
            max-width: 100%;
            max-height: 100px;
            display: block;
            margin: 10px auto;
        }
        .ingredient-detail-row {
            margin-bottom: 10px;
            padding: 10px;
            border: 1px solid #dee2e6;
            border-radius: 4px;
        }
        .ingredient-detail-row .label {
            font-weight: bold;
            margin-right: 5px;
        }
        .ingredient-detail-row span {
            color: #495057;
        }
        /* 카테고리 및 요리 정보 컨테이너 */
        .info-container {
            display: flex;
            flex-wrap: wrap;
            margin-bottom: 20px;
            border: 1px solid #dee2e6;
            padding: 15px;
            border-radius: 8px;
            background-color: #f9f9f9;
        }
        .info-container .category-section,
        .info-container .recipe-info-section {
            width: 48%; /* 좌우로 나뉘도록 */
            margin-bottom: 15px;
            box-sizing: border-box; /* 패딩, 테두리 포함 너비 */
            padding: 10px;
        }
         .info-container .category-section ul,
        .info-container .category-section ul li {
            margin-bottom: 0px;
            margin-left: 0px;
            padding-left: 0px;
        }
        .info-container .category-section:last-child,
        .info-container .recipe-info-section:last-child{
            margin-right: 0;
        }
        @media (max-width: 768px) {
            .info-container .category-section,
            .info-container .recipe-info-section {
                width: 100%;
            }
        }
    </style>
</head>
<body>
<div class="container">
    <h2 class="section-title">레시피 상세 정보 (관리자)</h2>

    <div class="detail-container">
        <div>
            <h3 class="section-title">제목</h3>
            <p>${recipe.recipeBoardTitle}</p>
        </div>

        <div>
            <h3 class="section-title">작성자 정보</h3>
            <div class="info-item">
                <span class="label">작성자 :</span>
                <span>${recipe.memberId}</span>
            </div>
            <fmt:formatDate value="${recipe.recipeBoardCreatedDate}" pattern="yyyy-MM-dd HH:mm:ss" var="recipeBoardCreatedDate"/>
            <div class="info-item">
                <span class="label">작성일 :</span>
                <span>${recipeBoardCreatedDate}</span>
            </div>
        </div>

        <div>
            <h3 class="section-title">썸네일 이미지</h3>
            <c:if test="${not empty recipe.thumbnailPath}">
                <img src="${pageContext.request.contextPath}/uploads/${recipe.thumbnailPath}" alt="썸네일 이미지">
            </c:if>
            <c:if test="${empty recipe.thumbnailPath}">
                <p>썸네일 이미지가 없습니다.</p>
            </c:if>
        </div>

        <div>
            <h3 class="section-title">내용</h3>
            <textarea rows="10" readonly>${recipe.recipeBoardContent}</textarea>
        </div>
        <div>
            <h3 class="section-title">요리 팁</h3>
            <textarea rows="5" readonly>${recipe.recipeTip}</textarea>
        </div>

        <!-- 카테고리 및 요리 정보 표시 -->
        <div class="info-container">
            <div class="category-section">
                <h3 class="section-title">카테고리</h3>
                <div class="info-item">
                    <span class="label">종류:</span>
                    <span>${typeName}</span>
                </div>
                <div class="info-item">
                    <span class="label">방법:</span>
                    <span>${methodName}</span>
                </div>
                <div class="info-item">
                    <span class="label">상황:</span>
                    <span>${situationName}</span>
                </div>
            </div>
            
        	  <div class="category-section">
            <h3 class="section-title">재료</h3>
            <ul>
             <c:forEach var="ingredient" items="${ingredients}">
              <div class="info-item">
               <li>${ingredient.ingredientName}</li>
              </div>
             </c:forEach>
            </ul>
           </div>

            <div class="recipe-info-section">
                <h3 class="section-title">요리 정보</h3>
                <div class="info-item">
                    <span class="label">인분:</span>
                    <span>${recipe.servings}</span>  <!-- recipeBoard -> recipe 로 변경 -->
                </div>
                <div class="info-item">
                    <span class="label">시간:</span>
                    <span>${recipe.time}</span> <!-- recipeBoard -> recipe 로 변경 -->
                </div>
                <div class="info-item">
                    <span class="label">난이도:</span>
                    <span>${recipe.difficulty}</span> <!-- recipeBoard -> recipe 로 변경 -->
                </div>
            </div>
        </div>

        <div>
            <h3 class="section-title">재료 상세 정보</h3>
            <c:forEach var="ingredientDetail" items="${ingredientDetails}">
                <div class="ingredient-detail-row">
                    <span class="label">이름:</span> <span>${ingredientDetail.ingredientName}</span>
                    <span class="label">수량:</span> <span>${ingredientDetail.ingredientAmount}</span>
                    <span class="label">단위:</span> <span>${ingredientDetail.ingredientUnit}</span>
                    <c:if test="${not empty ingredientDetail.ingredientNote}">
                        <span class="label">비고:</span> <span>${ingredientDetail.ingredientNote}</span>
                    </c:if>
                </div>
            </c:forEach>
        </div>

        <!-- 해시태그 표시 -->
        <div class="hashtags">
            <h3 class="section-title">해시태그</h3>
            <c:forEach var="hashtag" items="${hashtagNames}">
                <span>#${hashtag}</span>  <!-- hashtag.hashtagName -> hashtag 로 변경 -->
            </c:forEach>
        </div>

        <!-- 요리 단계 표시 -->
        <div>
            <h3 class="section-title">요리 순서</h3>
            <c:forEach var="step" items="${steps}" varStatus="status">
                <div class="step-row">
                    <label class="step-label">Step ${status.index + 1}</label>
                    <textarea rows="3" readonly>${step.stepDescription}</textarea>
                    <c:if test="${not empty step.stepImageUrl}">
                        <img
                                src="${pageContext.request.contextPath}/uploads/${step.stepImageUrl}"
                                alt="요리 단계 이미지" />
                    </c:if>
                </div>
            </c:forEach>
        </div>

        <div>
            <a href="${pageContext.request.contextPath}/admin/recipeboard/${recipe.recipeBoardId}/edit" class="btn btn-primary">수정</a>
            <form action="${pageContext.request.contextPath}/admin/recipeboard/${recipe.recipeBoardId}/delete" method="POST" style="display:inline;">
                <button type="submit" class="btn btn-danger">삭제</button>
            </form>
            <a href="${pageContext.request.contextPath}/admin/recipeboard" class="btn btn-secondary">목록</a>
        </div>
    </div>
</div>

<script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.5.3/dist/umd/popper.min.js"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
</body>
</html>