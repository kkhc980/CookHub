<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>CookHub</title>

	<style>
        /* 컨테이너 - 한 줄에 5개씩 출력 */
        .recipe-container {
            display: grid;
            grid-template-columns: repeat(5, 1fr); /* 5개씩 한 줄 */
            gap: 20px;
            padding: 20px;
            justify-items: center; /* 아이템 가운데 정렬 */
            max-width: 1200px; /* 전체 너비 제한 */
            margin: 0 auto; /* 중앙 정렬 */
            padding: 20px; /* 좌우 공백 */
        }

        /* 카드 스타일 */
        .recipe-card {
            cursor: pointer;
            width: 220px;
            border-radius: 10px;
            background: white;
            box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
            overflow: hidden;
            transition: transform 0.2s ease-in-out;
            text-align: center;
            position: relative;
        }

        .recipe-card:hover {
            transform: translateY(-5px);
        }

        /* 순위 스타일 (제대로 표시되도록 수정) */
        .rank-label {
            position: absolute;
            top: 5px;
            left: 5px;
            background-color: #ff4500;
            color: white;
            padding: 5px 10px;
            font-size: 14px;
            font-weight: bold;
            border-radius: 5px;
            z-index: 10;
        }

        /* 이미지 스타일 */
        .recipe-thumbnail-container {
            position: relative;
            width: 100%;
            height: 150px;
            background-color: #f0f0f0;
            display: flex;
            align-items: center;
            justify-content: center;
            overflow: hidden;
        }

        .recipe-thumbnail {
            width: 100%;
            height: 100%;
            object-fit: cover;
            display: block;
        }

        /* 이미지 없을 경우 (기본적으로 숨김) */
        .no-image-text {
            position: absolute;
            color: gray;
            font-size: 14px;
            display: none;
        }

        /* 이미지가 없을 경우 텍스트 표시 */
        .recipe-thumbnail.error {
            display: none;
        }

        .recipe-thumbnail.error + .no-image-text {
            display: block;
        }

        /* 카드 내용 */
        .recipe-info {
            padding: 10px;
            font-size: 14px;
        }

        .recipe-title {
            font-weight: bold;
            margin-bottom: 5px;
        }

        .recipe-meta {
            font-size: 12px;
            color: #666;
            display: flex;
            align-items: center;
            gap: 5px;
            justify-content: center;
        }

        /* 평점 스타일 */
        .rating {
            font-size: 12px;
            color: #f4c542;
            font-weight: bold;
        }
    </style>
</head>
<body>
    <h2>최신 공지</h2>
    <ul>
        <c:forEach var="notice" items="${latestNotices}">
            <li>
                <a href="noticeboard/detail/${notice.noticeBoardId}">${notice.noticeBoardTitle}</a> - ${notice.noticeBoardCreatedDate}
            </li>
        </c:forEach>
    </ul>
    
	<h2>인기 게시글</h2>
    <div class="recipe-container">
        <c:set var="rank" value="1"/>
        <c:forEach var="post" items="${topPosts}">
            <div class="recipe-card" onclick="location.href='${pageContext.request.contextPath}/recipeboard/detail/${post.recipeBoardId}'">
                
                <!-- 순위 (고정 숫자로 표시) -->
                <div class="rank-label">${rank}위</div>

                <!-- 썸네일 -->
                <div class="recipe-thumbnail-container">
                    <img class="recipe-thumbnail" 
                         src="${pageContext.request.contextPath}/uploads/${post.thumbnailPath}" 
                         alt="Thumbnail"
                         onerror="this.classList.add('error'); this.nextElementSibling.style.display='block';">
                    <span class="no-image-text">이미지 없음</span>
                </div>

                <!-- 게시글 정보 -->
                <div class="recipe-info">
                    <div class="recipe-title">${post.recipeBoardTitle}</div>
                    
                    <div class="recipe-meta">👤 작성자: ${post.memberId}</div>
                    <div class="recipe-meta">👁️ 조회수: ${post.viewCount}</div>
                    <div class="recipe-meta">⭐ <span class="rating">${post.avgRating}</span></div>
                    <div class="recipe-meta">❤️ 좋아요: ${post.likeCount}</div>
                </div>
            </div>
            <c:set var="rank" value="${rank + 1}"/>
        </c:forEach>
    </div>
    
    <!-- 추천 레시피 추가 -->
    <c:if test="${not empty recommendedRecipes}">
        <h2>💡 맞춤 추천 레시피</h2>
        <div class="recipe-container">
            <c:forEach var="recipe" items="${recommendedRecipes}">
			    <div class="recipe-card" onclick="location.href='${pageContext.request.contextPath}/recipeboard/detail/${recipe.recipe_board_id}'">
			        
			        <!-- 썸네일 -->
			        <div class="recipe-thumbnail-container">
			            <img class="recipe-thumbnail" 
			                 src="${pageContext.request.contextPath}/uploads/${recipe.thumbnail_path}" 
			                 alt="Thumbnail"
			                 onerror="this.classList.add('error'); this.nextElementSibling.style.display='block';">
			            <span class="no-image-text">이미지 없음</span>
			        </div>
			
			        <!-- 게시글 정보 -->
			        <div class="recipe-info">
			            <div class="recipe-title">${recipe.recipe_board_title}</div>
			            <div class="recipe-meta">👤 작성자: ${recipe.member_id}</div>
			            <div class="recipe-meta">👁️ 조회수: ${recipe.view_count}</div>
			            <div class="recipe-meta">⭐ <span class="rating">${recipe.avg_rating}</span></div>
			            <div class="recipe-meta">❤️ 좋아요: ${recipe.like_count}</div>
			            
			            <!-- 가중치, 유사도, 최종 점수 (hidden 처리) -->
			            <input type="hidden" value="${recipe.weight_score}">
			            <input type="hidden" value="${recipe.cosine_similarity}">
			            <input type="hidden" value="${recipe.final_score}">
			        </div>
			    </div>
			</c:forEach>
        </div>
    </c:if>
</body>
</html>