<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="https://code.jquery.com/ui/1.13.2/themes/base/jquery-ui.css">
    <title>Recipe Rankings</title>
    <style>
        body {
            font-family: Arial, sans-serif;
        }

        .ranking-container {
            margin: 20px auto;
            width: 80%;
            border-collapse: collapse;
        }

        .ranking-buttons {
            text-align: center;
            margin-bottom: 20px;
        }

        .ranking-buttons button {
            margin: 0 5px;
            padding: 10px 20px;
            border: 1px solid #ddd;
            background-color: #f9f9f9;
            cursor: pointer;
            font-size: 16px;
        }

        .ranking-buttons button:hover {
            background-color: #45a049;
            color: white;
        }

        .ranking-table {
            width: 100%;
            border-collapse: collapse;
            margin: 0 auto;
        }

        .ranking-table th, .ranking-table td {
            border: 1px solid #ddd;
            padding: 10px;
            text-align: left;
        }

        .ranking-table th {
            background-color: #f4f4f4;
        }

        .ranking-table td {
            text-align: center;
        }

        .ranking-table img {
            width: 50px;
            height: 50px;
            object-fit: cover;
        }

        .ranking-table tr:hover {
            background-color: #f1f1f1;
        }
    </style>
    <script>
        var contextPath = "${pageContext.request.contextPath}";

        window.onload = function() {
            fetchRankings('DAILY'); // 기본적으로 Daily Rankings를 표시합니다.
        };

        function fetchRankings(rankType) {
            console.log("Fetching rankings for type: " + rankType);
            fetch(contextPath + "/rankingboard/rankings?type=" + rankType, {
                method: 'GET',
                headers: {
                    'Accept': 'application/json'
                }
            })
            .then(function(response) {
                return response.json();
            })
            .then(function(data) {
                console.log("Received data:", data);
                updateRankingsView(data, rankType);
            })
            .catch(function(error) {
                console.error("Error fetching rankings:", error);
            });
        }

        function updateRankingsView(rankings, rankType) {
            console.log("Updating rankings view with data:", rankings);
            var container = document.getElementById("ranking-container");
            container.innerHTML = ""; // 기존 콘텐츠 초기화

            var viewCountColumnName = "View Count";
            if (rankType === "DAILY") {
                viewCountColumnName = "Daily View Count";
            } else if (rankType === "WEEKLY") {
                viewCountColumnName = "Weekly View Count";
            } else if (rankType === "MONTHLY") {
                viewCountColumnName = "Monthly View Count";
            }

            if (rankings.length > 0) {
                var tableHTML = 
                    "<table class='ranking-table'>" +
                        "<thead>" +
                            "<tr>" +
                                "<th>Rank</th>" +
                                "<th>Thumbnail</th>" +
                                "<th>Recipe Title</th>" +
                                "<th>View Count</th>" +
                                "<th>" + viewCountColumnName + "</th>" +
                            "</tr>" +
                        "</thead>" +
                        "<tbody>";

                rankings.forEach(function(recipe) {
                    tableHTML += 
                        "<tr onclick=\"location.href='" + contextPath + "/recipeboard/detail/" + recipe.recipeBoardId + "'\" style=\"cursor: pointer;\">" +
                            "<td>" + recipe.rankPosition + "</td>" +
                            "<td><img src='" + contextPath + "/uploads/" + recipe.thumbnailPath + "' alt='Thumbnail'></td>" +
                            "<td>" + recipe.recipeBoardTitle + "</td>" +
                            "<td>" + recipe.recipeBoardViewCount + "</td>" + // recipeBoard 테이블의 viewCount
                            "<td>" + recipe.viewCount + "</td>" + // Daily/Weekly/Monthly view count
                        "</tr>";
                });

                tableHTML += 
                        "</tbody>" +
                    "</table>";

                container.innerHTML = tableHTML;
            } else {
                container.innerHTML = "<p>No rankings available for this type.</p>";
            }
        }
    </script>
</head>
<body>
    <h1>Recipe Rankings</h1>

    <!-- 버튼 영역 -->
    <div class="ranking-buttons">
        <button type="button" onclick="fetchRankings('DAILY')">일일 조회 수 랭킹</button>
        <button type="button" onclick="fetchRankings('WEEKLY')">주간 조회 수 랭킹</button>
        <button type="button" onclick="fetchRankings('MONTHLY')">월간 조회 수 랭킹</button>
    </div>

    <!-- 랭킹 컨테이너 -->
    <div id="ranking-container"></div>
</body>
</html>
