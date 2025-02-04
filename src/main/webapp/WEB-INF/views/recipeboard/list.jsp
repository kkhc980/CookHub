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
		
        .filters-container {
            display: flex;
            flex-direction: column;
            gap: 20px;
            margin-bottom: 20px;
        }

        .filter-group {
            display: flex;
            align-items: center;
        }

        .filter-title {
            font-weight: bold;
            margin-right: 10px;
        }

        .filter-button {
            margin: 0 5px;
            padding: 5px 10px;
            border: 1px solid #ddd;
            background-color: #f9f9f9;
            cursor: pointer;
        }

        .filter-button.active {
            background-color: #4caf50;
            color: white;
        }

        .recipe-list {
		    display: flex;
		    flex-wrap: wrap;
		    gap: 20px;
		    justify-content: center;
		    align-items: center;
		}

        .recipe-card {
	    width: 22%;
	    border: 1px solid #ddd;
	    border-radius: 5px;
	    overflow: hidden;
	    cursor: pointer;
	    background-color: #fff;
	    transition: transform 0.2s;
	    display: flex; /* 카드 내부를 Flexbox로 설정 */
	    flex-direction: column; /* 세로로 배치 */
	    align-items: center; /* 수평 중앙 정렬 */
	    justify-content: center; /* 수직 중앙 정렬 */
	}

        .recipe-card:hover {
            transform: scale(1.05);
        }

        .recipe-thumbnail {
		    width: 50%;
		    height: auto;
		    object-fit: cover;
		}

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

        .ingredient-button {
            margin: 0 5px;
            padding: 5px 10px;
            border: 1px solid #ddd;
            background-color: #f9f9f9;
            cursor: pointer;
        }

        .ingredient-button.active {
            background-color: #4caf50;
            color: white;
            border: 1px solid #4caf50;
        }

        .ingredient-button[disabled] {
            background-color: #ccc;
            cursor: not-allowed;
        }
        
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
    </style>
</head>
<body>
    <h1>Recipe Board</h1>
    
    <!-- Filters Section -->
    <div class="filters-container">
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
	
	<!-- Recipe List Section -->
	<div class="recipe-list">
	    <c:choose>
	        <c:when test="${not empty recipeList}">
	            <c:forEach var="recipe" items="${recipeList}">
	                <div class="recipe-card" onclick="location.href='${pageContext.request.contextPath}/recipeboard/detail/${recipe.recipeBoardId}'">
	                    <img class="recipe-thumbnail" src="${pageContext.request.contextPath}/uploads/${recipe.thumbnailPath}" alt="Thumbnail">
	                    <div class="recipe-info">
	                        <h3 class="recipe-title">${recipe.recipeBoardTitle}</h3>
	                        <p class="recipe-meta">
	                            조회수: ${recipe.viewCount} | 평점: ${recipe.avgRating}
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
