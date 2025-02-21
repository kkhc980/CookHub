<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>

<html>
<head>
    <title>Admin Recipe Board List</title>
    <link rel="stylesheet" href="https://code.jquery.com/ui/1.13.2/themes/base/jquery-ui.css">
    <script src="https://code.jquery.com/jquery-3.6.4.min.js"></script>
    <script src="https://code.jquery.com/ui/1.13.2/jquery-ui.min.js"></script>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
            margin: 0;
            padding: 0;
        }

        .container {
            width: 80%;
            margin: 20px auto;
            background-color: #fff;
            padding: 20px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
        }

        h1 {
            color: #333;
            text-align: center;
            margin-bottom: 20px;
        }

        /* 검색창 스타일 */
        .search-container {
            text-align: center;
            margin-bottom: 20px;
        }

        .search-input {
            padding: 10px;
            width: 50%;
            border: 1px solid #ddd;
            border-radius: 5px;
            margin-right: 10px;
        }

        .search-button {
            padding: 10px 20px;
            background-color: #5cb85c;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            transition: background-color 0.3s;
        }

        .search-button:hover {
            background-color: #4cae4c;
        }

        /* 필터 스타일 */
        .filters-container {
            display: flex;
            flex-direction: column;
            margin-bottom: 20px;
        }

        .filter-group {
            margin-bottom: 10px;
        }

        .filter-title {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
            color: #555;
        }

        .filter-button,
        .ingredient-button {
            padding: 8px 12px;
            margin-right: 5px;
            margin-bottom: 5px;
            border: 1px solid #ddd;
            border-radius: 5px;
            background-color: #f9f9f9;
            color: #666;
            cursor: pointer;
            transition: background-color 0.3s, color 0.3s;
        }

        .filter-button:hover,
        .ingredient-button:hover {
            background-color: #e9e9e9;
        }

        .filter-button.active,
        .ingredient-button.active {
            background-color: #5bc0de;
            color: white;
            border-color: #46b8da;
        }

        /* 등록 버튼 스타일 */
        .register-button {
            display: inline-block;
            padding: 10px 20px;
            background-color: #5bc0de;
            color: white;
            text-decoration: none;
            border-radius: 5px;
            transition: background-color 0.3s;
            margin-bottom: 20px;
        }

        .register-button:hover {
            background-color: #46b8da;
        }

        /* 레시피 목록 스타일 */
        .recipe-list {
            display: flex;
            flex-wrap: wrap;
            justify-content: space-around;
        }

        .recipe-card {
            width: 200px;
            margin-bottom: 20px;
            border: 1px solid #ddd;
            border-radius: 5px;
            overflow: hidden;
            cursor: pointer;
            transition: transform 0.3s;
        }

        .recipe-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
        }

        .recipe-thumbnail {
            width: 100%;
            height: 150px;
            object-fit: cover;
        }

        .recipe-info {
            padding: 10px;
            text-align: center;
        }

        .recipe-title {
            font-size: 16px;
            margin-bottom: 5px;
            color: #333;
        }

        .recipe-meta {
            font-size: 12px;
            color: #777;
        }

        /* 페이지네이션 스타일 */
        .pagination-container {
            text-align: center;
            margin-top: 20px;
        }

        .pagination-link {
            display: inline-block;
            padding: 5px 10px;
            margin: 0 3px;
            border: 1px solid #ddd;
            border-radius: 3px;
            color: #555;
            text-decoration: none;
            transition: background-color 0.3s, color 0.3s;
        }

        .pagination-link:hover {
            background-color: #f0f0f0;
        }

        .pagination-link.active {
            background-color: #5cb85c;
            color: white;
            border-color: #4cae4c;
        }
    </style>
</head>
<body>
<div class="container">
    <h1>Admin Recipe Board</h1>

    <!-- 검색창 및 버튼 -->
    <div class="search-container">
        <form method="GET" action="${pageContext.request.contextPath}/admin/recipeboard" id="searchForm">
            <input
                    type="text"
                    name="hashtag"
                    value="${param.hashtag}"
                    placeholder="Enter hashtags to search..."
                    class="search-input"
                    id="searchInput">
            <input type="hidden" name="pageNum" value="1">
            <button type="submit" class="search-button">Search</button>
        </form>
    </div>

    <!-- Filters Section -->
    <div class="filters-container">
        <!-- Type Filter -->
        <div class="filter-group">
            <span class="filter-title">종류별</span>
            <c:forEach var="type" items="${allTypes}">
                <button type="button"
                        class="filter-button ${selectedTypeId == type.typeId ? 'active' : ''}"
                        onclick="applyFilter('typeId', '${type.typeId}')">${type.typeName}</button>
            </c:forEach>
        </div>

        <!-- Situation Filter -->
        <div class="filter-group">
            <span class="filter-title">상황별</span>
            <c:forEach var="situation" items="${allSituations}">
                <button type="button"
                        class="filter-button ${selectedSituationId == situation.situationId ? 'active' : ''}"
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
                <button type="button"
                        class="filter-button ${selectedMethodId == method.methodId ? 'active' : ''}"
                        onclick="applyFilter('methodId', '${method.methodId}')">${method.methodName}</button>
            </c:forEach>
        </div>
    </div>

  <%--   <!-- 등록 버튼 -->
    <a href="${pageContext.request.contextPath}/admin/recipeboard/register" class="register-button">등록</a>
     --%>

    <!-- Recipe List Section -->
    <div class="recipe-list">
        <c:forEach var="recipe" items="${recipeList}">
            <div class="recipe-card"
                 onclick="location.href='${pageContext.request.contextPath}/admin/recipeboard/${recipe.recipeBoardId}'">
                <img class="recipe-thumbnail"
                     src="${pageContext.request.contextPath}/uploads/${recipe.thumbnailPath}" alt="Thumbnail">
                <div class="recipe-info">
                    <h3 class="recipe-title">${recipe.recipeBoardTitle}</h3>
                    <p class="recipe-meta">
                        조회수: ${recipe.viewCount} | 평점: ${recipe.avgRating}
                    </p>
                </div>
            </div>
        </c:forEach>
    </div>

    <!-- Pagination Section -->
    <div class="pagination-container">
        <c:if test="${pageMaker.prev}">
            <c:url var="prevPageUrl" value="/admin/recipeboard">
                <c:param name="pageNum" value="${pageMaker.startPage - 1}"/>
                <c:param name="typeId" value="${selectedTypeId}"/>
                <c:param name="situationId" value="${selectedSituationId}"/>
                <c:param name="methodId" value="${selectedMethodId}"/>
                <c:param name="ingredientIds" value="${ingredientIdsStr}"/>
                <c:param name="hashtag" value="${hashtag}"/>
            </c:url>
            <a class="pagination-link" href="${prevPageUrl}">이전</a>
        </c:if>
        <c:forEach var="num" begin="${pageMaker.startPage}" end="${pageMaker.endPage}">
            <c:url var="pageUrl" value="/admin/recipeboard">
                <c:param name="pageNum" value="${num}"/>
                <c:param name="typeId" value="${selectedTypeId}"/>
                <c:param name="situationId" value="${selectedSituationId}"/>
                <c:param name="methodId" value="${selectedMethodId}"/>
                <c:param name="ingredientIds" value="${ingredientIdsStr}"/>
                <c:param name="hashtag" value="${hashtag}"/>
            </c:url>
            <a class="pagination-link ${pagination.pageNum == num ? 'active' : ''}" href="${pageUrl}">${num}</a>
        </c:forEach>
        <c:if test="${pageMaker.next}">
            <c:url var="nextPageUrl" value="/admin/recipeboard">
                <c:param name="pageNum" value="${pageMaker.endPage + 1}"/>
                <c:param name="typeId" value="${selectedTypeId}"/>
                <c:param name="situationId" value="${selectedSituationId}"/>
                <c:param name="methodId" value="${selectedMethodId}"/>
                <c:param name="ingredientIds" value="${ingredientIdsStr}"/>
                <c:param name="hashtag" value="${hashtag}"/>
            </c:url>
            <a class="pagination-link" href="${nextPageUrl}">다음</a>
        </c:if>
    </div>
</div>

<script>
    function applyFilter(key, value) {
        const urlParams = new URLSearchParams(window.location.search);
        urlParams.set(key, value);
        urlParams.set("pageNum", "1");
        urlParams.delete("hashtag");
        window.location.href = "${pageContext.request.contextPath}/admin/recipeboard?" + urlParams.toString();
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
                const urlParams = new URLSearchParams();
                urlParams.set("ingredientIds", ingredientIdsInput.value);
                urlParams.set("pageNum", "1");
                urlParams.delete("hashtag");

                window.location.href = "${pageContext.request.contextPath}/admin/recipeboard?" + urlParams.toString();
            });
        });

        // autocomplete
        $("#searchInput").autocomplete({
            source: function (request, response) {
                $.ajax({
                    url: "${pageContext.request.contextPath}/autocomplete",
                    type: "GET",
                    data: {q: request.term},
                    success: function (data) {
                        response(data);
                    },
                    error: function (xhr) {
                        console.error("Error fetching autocomplete suggestions:", xhr);
                    }
                });
            },
            minLength: 1,
            select: function (event, ui) {
                $("#searchInput").val(ui.item.value);
                $("#searchForm").submit();
            }
        });

        // 검색어 입력 후 폼 제출 시
        document.getElementById("searchForm").addEventListener("submit", function (event) {
            event.preventDefault();
            const hashtag = document.getElementById("searchInput").value;
            const urlParams = new URLSearchParams();
            urlParams.set("hashtag", hashtag);
            urlParams.set("pageNum", 1);
            window.location.href = "${pageContext.request.contextPath}/admin/recipeboard?" + urlParams.toString();
        });
    });
</script>

</body>
</html>