<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>

<!DOCTYPE html>
<html>
<head>
    <title>Recipe Board List</title>
    <link rel="stylesheet" href="https://code.jquery.com/ui/1.13.2/themes/base/jquery-ui.css">
    <script src="https://code.jquery.com/jquery-3.6.4.min.js"></script>
    <script src="https://code.jquery.com/ui/1.13.2/jquery-ui.min.js"></script>
 
    <style>
	body {
	    font-family: Arial, sans-serif;
	}
	
	.no-results {
	    text-align: center;
	    margin-top: 50px;
	    font-size: 18px;
	    color: #555;
	}
	
	/* 닫기 버튼 스타일 */
	.filter-toggle {
	    text-align: center;
	    font-size: 14px;
	    color: #2c8c3a;
	    cursor: pointer;
	    padding: 10px 0;
	    border-top: 1px solid #ddd;
	}
	
	.filter-toggle:hover {
	    font-weight: bold;
	}
	
	/* 필터 컨테이너 */
	.filters-container {
	    transition: max-height 0.3s ease-in-out, opacity 0.3s ease-in-out;
	    overflow: hidden;
	}
	
	
	/* 필터 그룹 */
	.filter-group {
	    display: flex;
	    align-items: center;
	    flex-wrap: nowrap;
	    gap: 1px;  /* 여백을 늘리기 위해 10px에서 20px로 수정 */
	    border-top: 1px solid #ddd;
	    overflow-x: auto; /* 가로 스크롤 활성화 */
	    white-space: nowrap; /* 텍스트 줄바꿈 방지 */
	    padding-bottom: 10px; /* 스크롤바 영역 확보 */
	}
	
	/* 필터 제목 */
	.filter-title {
	    font-weight: bold;
	    color: #2c8c3a; /* 초록색 */
	    margin-right: 10px;
	    min-width: 80px;
	}
	
	/* 필터 버튼 */
	.filter-button, .ingredient-button {
	    border: 1px solid #ddd;
	    background-color: #fff;
	    color: #333;
	    padding: 5px 12px;
	    border-radius: 20px; /* 둥글게 */
	    cursor: pointer;
	    font-size: 14px;
	    transition: background-color 0.2s ease-in-out;
	    margin: 10px 0.01px;
	    white-space: nowrap; /* 버튼 내 텍스트 줄바꿈 방지 */
	}
	
	/* 전체(ALL) 버튼 */
	.filter-button:first-child, .ingredient-button:first-child {
	    background-color: #2c8c3a; /* 초록색 */
	    color: white;
	    font-weight: bold;
	    border: none;
	}
	
	/* 활성화된 버튼 */
	.filter-button.active, .ingredient-button.active {
	    background-color: #2c8c3a;
	    color: white;
	    border: none;
	}
	
	/* 버튼 호버 효과 */
	.filter-button:hover, .ingredient-button:hover {
	    background-color: #f0f0f0;
	}
	
	/* 레시피 리스트 */
	.recipe-list {
	    display: flex;
	    flex-wrap: wrap;
	    gap: 20px;
	    justify-content: center;
	}
	
	/* 레시피 카드 */
	.recipe-card {
	    width: 22%;
	    border: 1px solid #ddd;
	    border-radius: 5px;
	    overflow: hidden;
	    background-color: #fff;
	    cursor: pointer;
	    text-align: center;
	    transition: transform 0.2s;
	    display: flex;
	    flex-direction: column;
	    align-items: center; /* 카드 내부 요소 중앙 정렬 */
	}
	
	.recipe-card:hover {
	    transform: scale(1.05);
	}
	
	/* 레시피 이미지 */
	.recipe-thumbnail {
	    width: 50%;
	    height: auto%;
	    object-fit: cover;
	}
	
	/* 이미지 없음 텍스트 컨테이너 */
	.recipe-thumbnail-container {
	    width: 100%;
	    height: 200px; /* 고정된 높이로 설정 */
	    display: flex;
	    align-items: center; /* 수직 중앙 정렬 */
	    justify-content: center; /* 수평 중앙 정렬 */
	    border: 1px solid #ddd;
	    overflow: hidden; /* 이미지 크기 초과 방지 */
	    position: relative;
	    background-color: #f0f0f0; /* 이미지를 불러오지 못했을 때 배경색 */
	}
	
	/* 이미지 없음 텍스트 스타일 */
	.no-image-text {
	    display: none; /* 기본적으로 숨김 */
	    color: gray;
	    font-size: 14px;
	    text-align: center;
	    position: absolute;
	    top: 50%;
	    left: 50%;
	    transform: translate(-50%, -50%); /* 중앙 정렬 */
	}


	/* 레시피 정보 */
	.recipe-info {
	    padding: 10px;
	}
	
	.recipe-title {
	    font-size: 18px;
	    margin: 5px 0;
	}
	
	.recipe-meta {
	    color: #777;
	}
	
	/* 페이지네이션 */
	.pagination-container {
	    display: flex;
	    justify-content: center;
	    margin-top: 20px;
	}
	
	.pagination-link {
	    margin: 0 5px;
	    padding: 5px 10px;
	    border: 1px solid #ddd;
	    text-decoration: none;
	    color: #333;
	}
	
	.pagination-link.active {
	    font-weight: bold;
	    background-color: #4caf50;
	    color: white;
	}
	
	/* 재료 필터 버튼 */
	.ingredient-button[disabled] {
	    background-color: #ccc;
	    cursor: not-allowed;
	}
	
	/* 등록 버튼 */
	.register-button {
	    display: block;
	    margin: 20px auto;
	    padding: 10px 20px;
	    font-size: 16px;
	    background-color: #4caf50;
	    color: white;
	    border: none;
	    border-radius: 5px;
	    cursor: pointer;
	    text-align: center;
	    text-decoration: none;
	}
	
	.register-button:hover {
	    background-color: #45a049;
	}
	
	/* 정렬 버튼 */
	.sort-container {
	    display: flex;
	    justify-content: flex-end;
	    margin-bottom: 20px;
	}
	
	.sort-button {
	    padding: 5px 15px;
	    border: 1px solid #ddd;
	    background-color: #f9f9f9;
	    cursor: pointer;
	    margin-left: 10px;
	}
	
	.sort-button.active {
	    background-color: #4caf50;
	    color: white;
	}
	</style>
</head>
<body>
    
    <!-- Filters Section -->
    <div class="filters-container" id="filtersContainer">
        <!-- Type Filter -->
        <div class="filter-group">
            <span class="filter-title">종류별</span>
            <c:forEach var="type" items="${allTypes}">
                <button type="button" class="filter-button ${selectedTypeId == type.typeId ? 'active' : ''}" 
                        onclick="applyFilter('typeId', '${type.typeId}')">${type.typeName}</button>
            </c:forEach>
        </div>

        <!-- Situation Filter -->
        <div class="filter-group">
            <span class="filter-title">상황별</span>
            <c:forEach var="situation" items="${allSituations}">
                <button type="button" class="filter-button ${selectedSituationId == situation.situationId ? 'active' : ''}" 
                        onclick="applyFilter('situationId', '${situation.situationId}')">${situation.situationName}</button>
            </c:forEach>
        </div>

        <!-- Ingredient Filter -->
        <div class="filter-group">
            <span class="filter-title">재료별</span>
            <input type="hidden" id="ingredientIdsInput" name="ingredientIds" value="${ingredientIdsStr}">
            <c:forEach var="ingredient" items="${allIngredients}">
                <button 
                    type="button" 
                    class="ingredient-button ${selectedIngredientIds.contains(ingredient.ingredientId) || (fn:length(selectedIngredientIds) == 0 && ingredient.ingredientId == 1) ? 'active' : ''}" 
                    data-id="${ingredient.ingredientId}">
                    ${ingredient.ingredientName}
                </button>
            </c:forEach>
        </div>

        <!-- Method Filter -->
        <div class="filter-group">
            <span class="filter-title">방법별</span>
            <c:forEach var="method" items="${allMethods}">
                <button type="button" class="filter-button ${selectedMethodId == method.methodId ? 'active' : ''}" 
                        onclick="applyFilter('methodId', '${method.methodId}')">${method.methodName}</button>
            </c:forEach>
        </div>
    </div>
	<div class="filter-toggle" id="filterToggle">
	    카테고리 닫기 ▲
	</div>
	<!-- 정렬 버튼 추가 -->
	<p>총 <span style="color: green; font-size: 32px;">${totalRecipeBoards}</span> 개의 레시피가 있습니다.</p>

	<div class="sort-container">
	    <button type="button" class="sort-button ${empty param.sort || param.sort == 'latest' ? 'active' : ''}" 
	            onclick="applySort('latest')">
	        최신순
	    </button>
	    <button type="button" class="sort-button ${param.sort == 'view' ? 'active' : ''}" 
	            onclick="applySort('view')">
	        조회순
	    </button>
	    <button type="button" class="sort-button ${param.sort == 'rating' ? 'active' : ''}" 
	            onclick="applySort('rating')">
	        평점순
	    </button>
	    <button type="button" class="sort-button ${param.sort == 'like' ? 'active' : ''}" 
	            onclick="applySort('like')">
	        좋아요순
	    </button>
	</div>
        
	<!-- 레시피 리스트 섹션 -->
	<div class="recipe-list">
	    <c:choose>
	        <c:when test="${not empty recipeList}">
	            <c:forEach var="recipe" items="${recipeList}">
	                <div class="recipe-card" onclick="location.href='${pageContext.request.contextPath}/recipeboard/detail/${recipe.recipeBoardId}'">
	                    <div class="recipe-thumbnail-container">
	                        <img class="recipe-thumbnail" 
	                             src="${pageContext.request.contextPath}/uploads/${recipe.thumbnailPath}" 
	                             alt="Thumbnail"
	                             onerror="this.style.display='none'; this.nextElementSibling.style.display='block';">
	                        <span class="no-image-text">이미지 없음</span>
	                    </div>
	                    <div class="recipe-info">
	                        <h3 class="recipe-title">${recipe.recipeBoardTitle}</h3>
	                        <p class="recipe-meta">
	                            조회수: ${recipe.viewCount} | 평점: ${recipe.avgRating} | ❤️: ${recipe.likeCount}
	                        </p>
	                    </div>
	                </div>
	            </c:forEach>
	        </c:when>
	        <c:otherwise>
	            <div class="no-results">
	                <h3>검색 결과가 없습니다</h3>
	                <p>- 일반적인 검색어로 다시 검색해 보세요</p>
	                <p>- 검색어의 단어 수를 줄이거나, 다른 검색어로 검색해 보세요.</p>
	            </div>
	        </c:otherwise>
	    </c:choose>
	</div>


    <!-- Pagination Section -->
	<div class="pagination-container">
	    <c:if test="${pageMaker.prev}">
	        <a class="pagination-link" 
	           href="?pageNum=${pageMaker.startNum - 1}&typeId=${selectedTypeId}&situationId=${selectedSituationId}&methodId=${selectedMethodId}&ingredientIds=${ingredientIdsStr}&hashtag=${fn:escapeXml(param.hashtag)}">
	            이전
	        </a>
	    </c:if>
	    <c:forEach var="pageNum" begin="${pageMaker.startNum}" end="${pageMaker.endNum}">
	        <a class="pagination-link ${pagination.pageNum == pageNum ? 'active' : ''}" 
	           href="?pageNum=${pageNum}&typeId=${selectedTypeId}&situationId=${selectedSituationId}&methodId=${selectedMethodId}&ingredientIds=${ingredientIdsStr}&hashtag=${fn:escapeXml(param.hashtag)}">
	            ${pageNum}
	        </a>
	    </c:forEach>
	    <c:if test="${pageMaker.next}">
	        <a class="pagination-link" 
	           href="?pageNum=${pageMaker.endNum + 1}&typeId=${selectedTypeId}&situationId=${selectedSituationId}&methodId=${selectedMethodId}&ingredientIds=${ingredientIdsStr}&hashtag=${fn:escapeXml(param.hashtag)}">
	            다음
	        </a>
	    </c:if>
	</div>

    <script>
	    function redirectToLogin() {
	        alert("로그인이 필요한 서비스입니다.");
	        window.location.href = "${pageContext.request.contextPath}/auth/login";
	    }    
	    
        function applySort(value) {
            const urlParams = new URLSearchParams(window.location.search);
            urlParams.set("sort", value);
            urlParams.set("pageNum", "1"); // 페이지 초기화
            window.location.search = urlParams.toString();
        }
	    
    	function applyFilter(key, value) {
            const urlParams = new URLSearchParams(window.location.search);
            urlParams.set(key, value);
            urlParams.set("pageNum", "1");
            
            urlParams.delete("hashtag");
            window.location.search = urlParams.toString();
        }
    	
        document.addEventListener("DOMContentLoaded", function () {
            const ingredientButtons = document.querySelectorAll(".ingredient-button");
            const ingredientIdsInput = document.getElementById("ingredientIdsInput");
            const selectedIngredients = new Set(ingredientIdsInput.value ? ingredientIdsInput.value.split(",") : ["1"]); // 기본 값 "1" (전체)

			// URL에서 success 파라미터 확인
			const urlParams = new URLSearchParams(window.location.search);
			const success = urlParams.get('success');

			if (success === 'true') {
				alert('로그인 성공!');
				// URL에서 success 파라미터 제거 (새로고침 시 중복 알림 방지)
				urlParams.delete('success');
				const newUrl = window.location.pathname + '?' + urlParams.toString();
				window.history.replaceState({}, document.title, newUrl); // URL 업데이트
			}

            // 버튼 상태 업데이트 함수
            function updateButtonState(button, activate) {
                if (activate) {
                    button.classList.add("active");
                    selectedIngredients.add(button.dataset.id);
                } else {
                    button.classList.remove("active");
                    selectedIngredients.delete(button.dataset.id);
                }
            }

            function checkAndActivateAll() {
                const otherButtonsActive = Array.from(ingredientButtons).some(
                    button => button.dataset.id !== "1" && selectedIngredients.has(button.dataset.id)
                );
                if (!otherButtonsActive) {
                    updateButtonState(document.querySelector("[data-id='1']"), true); // "전체(All)" 버튼 활성화
                }
            }

            ingredientButtons.forEach(button => {
                // 기존 상태에 따라 버튼 활성화
                if (selectedIngredients.has(button.dataset.id)) {
                    button.classList.add("active");
                }

                button.addEventListener("click", function () {
                    if (button.dataset.id === "1") {
                        // "전체(All)" 버튼 클릭 시 다른 버튼 비활성화
                        ingredientButtons.forEach(btn => updateButtonState(btn, false));
                        updateButtonState(button, true);
                    } else {
                        // 다른 버튼 클릭 시 "전체(All)" 비활성화
                        updateButtonState(button, !button.classList.contains("active"));
                        if (selectedIngredients.has("1")) selectedIngredients.delete("1");
                        document.querySelector("[data-id='1']").classList.remove("active");
                    }

                    checkAndActivateAll();

                    ingredientIdsInput.value = Array.from(selectedIngredients).join(",");

                    // 실시간으로 필터링 적용
                    const urlParams = new URLSearchParams(window.location.search);
                    urlParams.set("ingredientIds", ingredientIdsInput.value);

                    // 필터 변경 시 pageNum을 항상 1로 초기화
                    urlParams.set("pageNum", "1");
                    urlParams.delete("hashtag");

                    window.location.search = urlParams.toString();
                });
            });
        });
    </script>
    
    <script>
    document.getElementById("filterToggle").addEventListener("click", function() {
        var filters = document.getElementById("filtersContainer");

        if (filters.style.display === "none" || filters.style.display === "") {
            // 필터 열기
            filters.style.display = "block";
            filters.style.opacity = "1";
            this.innerHTML = "카테고리 닫기 ▲";
        } else {
            // 필터 닫기
            filters.style.display = "none";
            filters.style.opacity = "0";
            this.innerHTML = "카테고리 열기 ▼";
        }
    });
    </script>
    <script>
	    $(document).ready(function () {
	        // 자동완성 적용
	        $(".search-input").autocomplete({
	            source: function (request, response) {
	                $.ajax({
	                    url: "${pageContext.request.contextPath}/autocomplete", // Spring Controller의 URL
	                    type: "GET",
	                    data: {
	                        q: request.term // 입력된 검색어
	                    },
	                    success: function (data) {
	                        response(data); // 결과를 autocomplete에 전달
	                    },
	                    error: function (xhr) {
	                        console.error("Error fetching autocomplete suggestions:", xhr);
	                    }
	                });
	            },
	            minLength: 1, // 최소 몇 글자 입력 후 동작할지 설정
	            select: function (event, ui) {
	                $(".search-input").val(ui.item.value); // 선택한 항목을 입력창에 삽입
	                $("#searchForm").submit(); // 자동으로 폼 제출
	            }
	        });
	    });
	</script>

</body>
</html>