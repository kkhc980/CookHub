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
            margin: 20px;
        }

        .ranking-container {
            margin: 20px auto;
            width: 80%;
        }

        .ranking-buttons-wrapper {
            display: flex;
            align-items: center;
            justify-content: space-between;
            margin-bottom: 20px;
        }

		.ranking-main-buttons {
		            display: flex;
		            gap: 10px;
		}
        
        .ranking-main-button {
            padding: 10px 20px;
            border: 1px solid #ddd;
            background-color: #f9f9f9;
            cursor: pointer;
            font-size: 16px;
        }

        .ranking-main-button:hover, .ranking-main-button.active {
            background-color: #45a049;
            color: white;
        }

        .ranking-buttons {
            display: flex;
            gap: 10px;
        }

        .ranking-buttons button {
            padding: 10px 20px;
            border: 1px solid #ddd;
            background-color: #f9f9f9;
            cursor: pointer;
            font-size: 16px;
        }

        .ranking-buttons button:hover, .ranking-buttons button.active {
            background-color: #45a049;
            color: white;
        }

        .ranking-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }

        .ranking-table th, .ranking-table td {
            border: 1px solid #ddd;
            padding: 12px;
            text-align: center;
            vertical-align: middle;
        }

        .ranking-table th {
            background-color: #f4f4f4;
            font-weight: bold;
        }

        .ranking-table img {
            width: 80px;
            height: 80px;
            object-fit: cover;
            border-radius: 5px;
        }
    </style>
    <script>
        var contextPath = "${pageContext.request.contextPath}";
        var currentMode = "VIEWS"; // "VIEWS" (조회수 랭킹) 또는 "TOTAL" (통합 랭킹)

        window.onload = function() {
            fetchRankings("DAILY"); // 기본적으로 Daily Rankings 표시
        };

        function switchRankingMode(mode) {
            currentMode = mode;
            document.querySelectorAll(".ranking-main-button").forEach(button => {
                button.classList.remove("active");
            });
            document.querySelector(".ranking-main-button[data-mode='" + mode + "']").classList.add("active");
            
            var rankingButtons = document.getElementById("ranking-buttons");
            if (mode === "VIEWS") {
                rankingButtons.innerHTML = `
                    <button type="button" data-type="DAILY" onclick="fetchRankings('DAILY')" class="active">일간</button>
                    <button type="button" data-type="WEEKLY" onclick="fetchRankings('WEEKLY')">주간</button>
                    <button type="button" data-type="MONTHLY" onclick="fetchRankings('MONTHLY')">월간</button>
                `;
                fetchRankings("DAILY");
            } else {
                rankingButtons.innerHTML = `
                    <button type="button" data-type="TOTAL_DAILY" onclick="fetchRankings('TOTAL_DAILY')" class="active">일간</button>
                    <button type="button" data-type="TOTAL_WEEKLY" onclick="fetchRankings('TOTAL_WEEKLY')">주간</button>
                    <button type="button" data-type="TOTAL_MONTHLY" onclick="fetchRankings('TOTAL_MONTHLY')">월간</button>
                `;
                fetchRankings("TOTAL_DAILY");
            }
        }

        function fetchRankings(rankType) {
            console.log("Fetching rankings for type: " + rankType);

            var buttons = document.querySelectorAll(".ranking-buttons button");
            buttons.forEach(function(button) {
                if (button.getAttribute("data-type") === rankType) {
                    button.classList.add("active");
                } else {
                    button.classList.remove("active");
                }
            });

            var container = document.getElementById("ranking-container");
            container.innerHTML = "<p class='loading'>Loading rankings...</p>"; // 로딩 상태 표시

            fetch(contextPath + "/rankingboard/rankings?type=" + rankType, {
                method: "GET",
                headers: {
                    "Accept": "application/json"
                }
            })
            .then(function(response) {
                console.log("Response Status:", response.status);
                if (!response.ok) {
                    return response.text().then(function(text) {
                        throw new Error("Server Error: " + text);
                    });
                }
                return response.json();
            })
            .then(function(data) {
                console.log("Received data:", data);
                updateRankingsView(data, rankType);
            })
            .catch(function(error) {
                console.error("Error fetching rankings:", error);
                container.innerHTML = "<p class='no-data'>Error fetching data. Please check the server.</p>";
            });
        }
        
        function updateRankingsView(rankings, rankType) {
            var container = document.getElementById("ranking-container");
            container.innerHTML = ""; // 기존 콘텐츠 초기화

            var viewCountColumnName = currentMode === "VIEWS" ? "View Count" : "Total Score";

            if (currentMode === "VIEWS") {
                if (rankType === "DAILY") {
                    viewCountColumnName = "Daily View Count";
                } else if (rankType === "WEEKLY") {
                    viewCountColumnName = "Weekly View Count";
                } else if (rankType === "MONTHLY") {
                    viewCountColumnName = "Monthly View Count";
                }
            } else { // TOTAL 모드일 경우
                viewCountColumnName = "Total Score";
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

                rankings.slice(0, 10).forEach(function(recipe, index) {  // ✅ 최대 10개까지만 표시
                    let fixedRank = index + 1;  // ✅ 1부터 10까지 고정 랭킹 설정
                    let formattedScore = recipe.totalScore ? parseFloat(recipe.totalScore).toFixed(2) : "0.00"; 

                    tableHTML += 
                        "<tr onclick=\"location.href='" + contextPath + "/recipeboard/detail/" + recipe.recipeBoardId + "'\" style=\"cursor: pointer;\">" +
                            "<td>" + fixedRank + "</td>" +  // ✅ JavaScript에서 1부터 10까지 자동 지정
                            "<td><img src='" + contextPath + "/uploads/" + recipe.thumbnailPath + "' alt='Thumbnail'></td>" +
                            "<td>" + recipe.recipeBoardTitle + "</td>" +
                            "<td>" + recipe.recipeBoardViewCount + "</td>";

                    if (currentMode === "VIEWS") {
                        tableHTML += "<td>" + recipe.viewCount + "</td>";
                    } else {
                        tableHTML += "<td>" + formattedScore + "</td>";
                    }

                    tableHTML += "</tr>";
                });

                tableHTML += "</tbody></table>";
                container.innerHTML = tableHTML;
            } else {
                container.innerHTML = "<p class='no-data'>No rankings available for this type.</p>";
            }
        }


    </script>
</head>
<body>
    <h1>Recipe Rankings</h1>


    <!-- 버튼 영역 -->
    <div class="ranking-buttons-wrapper">
        <div class="ranking-main-buttons">
            <button class="ranking-main-button active" data-mode="VIEWS" onclick="switchRankingMode('VIEWS')">조회 수 랭킹</button>
            <button class="ranking-main-button" data-mode="TOTAL" onclick="switchRankingMode('TOTAL')">통합 랭킹</button>
        </div>
        <div class="ranking-buttons" id="ranking-buttons">
            <button type="button" data-type="DAILY" onclick="fetchRankings('DAILY')" class="active">일간</button>
            <button type="button" data-type="WEEKLY" onclick="fetchRankings('WEEKLY')">주간</button>
            <button type="button" data-type="MONTHLY" onclick="fetchRankings('MONTHLY')">월간</button>
        </div>
    </div>

    <!-- 랭킹 컨테이너 -->
    <div id="ranking-container"></div>
</body>
</html>
