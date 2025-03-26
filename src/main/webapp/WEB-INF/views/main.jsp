<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>CookHub</title>

	<style>
    /* 공통 스타일: 레시피 섹션 전체를 감싸는 컨테이너 */
	    .recipe-section, .notice-section {
	        max-width: 1200px; /* 최대 너비 제한 */
	        margin: 20px auto; /* 중앙 정렬 및 상하 여백 */
	        padding: 20px; /* 내부 여백 */
	        background-color: #fff; /* 배경색 */
	        border-radius: 10px; /* 테두리 둥글게 */
	        box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2); /* 그림자 효과: 기존보다 짙게 변경 */
	        border: 1px solid #eee; /* 연한 테두리 추가 (선택사항) 
	    }
		
		/* 섹션 제목 스타일 */
        .recipe-section .recipe-section h2, .notice-section h2 {
            font-size: 24px; /* 글자 크기 */
            margin-bottom: 15px; /* 아래 여백 */
            color: #333; /* 글자 색상 */
            margin: 0; /* 제목 기본 margin 제거 */
    		flex-grow: 1; /* 제목이 남은 공간을 모두 차지하도록 설정 */
        }
        
        .recipe-section .section-header {
		    display: flex;
		    align-items: center; /* 제목과 버튼을 세로축 중앙 정렬 */
		    margin-bottom: 10px; /* 제목과 카드 사이 간격 추가 */
		}
		
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
        
        /* 공지사항 아이템 스타일 */
    	.notice-section li {
        	padding: 8px 0;
        	border-bottom: 1px solid #f0f0f0; /* 구분선 추가 */
    	}
        
        /* 공지사항 링크 스타일 */
    	.notice-section a {
        	text-decoration: none; /* 밑줄 제거 */
        	color: #007bff; /* 파란색 링크 */
    	}
    	
    	.notice-section a:hover {
        	text-decoration: underline; /* 호버 시 밑줄 표시 */
    	}
        
        /* 레시피 아이템 목록 스타일 */
        .recipe-list {
            display: grid; /* 그리드 레이아웃 사용 */
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); /* 반응형 그리드: 최소 250px, 최대 1fr 크기의 컬럼 자동 생성 */
            gap: 20px; /* 아이템 간 간격 */
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
        
        /* 더보기 버튼 스타일 (선택 사항) */
		.more-button {
			background-color: #ff9900; /* 버튼 배경색 */
		    color: white; /* 버튼 글자색 */
		    padding: 8px 16px; /* 버튼 패딩 */
		    border: none; /* 테두리 제거 */
		    border-radius: 5px; /* 둥근 테두리 */
		    cursor: pointer; /* 마우스 오버 시 커서 변경 */
		    text-decoration: none; /* 링크 밑줄 제거 */
		    font-size: 14px; /* 글자 크기 */
		    white-space: nowrap; /* 텍스트가 줄바꿈되지 않도록 설정 */
		}
		
		.more-button-container {
		    display: flex;
		    align-items: center;
		    justify-content: space-between; /* 제목과 버튼을 양 끝으로 정렬 */
		}

		.more-button:hover {
			background-color: #e68900; /* 호버 시 배경색 변경 */
		}
        
    </style>
</head>
<body>
  <div class="notice-section">
    <h2>최신 공지</h2>
    <ul>
        <c:forEach var="notice" items="${latestNotices}">
            <li>
                <a href="noticeboard/detail/${notice.noticeBoardId}">${notice.noticeBoardTitle}</a> - 
                <fmt:formatDate value="${notice.noticeBoardCreatedDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
            </li>
        </c:forEach>
    </ul>
  </div> 
   
  <div class="recipe-section">
  <div class="more-button-container">
	<h2>인기 게시글</h2>
	<a href="${pageContext.request.contextPath}/recipeboard/list" class="more-button">더보기</a>
	</div>
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
                    <div class="recipe-meta">⭐ <span class="rating">${post.avgRating} </span></div>
                    <div class="recipe-meta">❤️ 좋아요: ${post.likeCount}</div>
                </div>
            </div>
            <c:set var="rank" value="${rank + 1}"/>
        </c:forEach>
    </div>
   </div> 
    
    <!-- 추천 레시피 추가 -->
    <c:if test="${not empty recommendedRecipes}">
    <div class="recipe-section">
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
       </div> 
    </c:if>
</body>
</html>