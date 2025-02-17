<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>상품 등록</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 20px;
            padding: 20px;
        }
        form {
            width: 400px;
            margin: auto;
        }
        label {
            display: block;
            margin-top: 10px;
        }
        input, textarea, button {
            width: 100%;
            padding: 8px;
            margin-top: 5px;
            box-sizing: border-box;
        }
        .checkbox-group {
            margin-top: 10px;
        }
        .checkbox-group input {
            width: auto;
        }
    </style>
    <script>
        function enforceSingleSelection(checkbox) {
            var checkboxes = document.getElementsByName("ingredient_id");
            checkboxes.forEach((item) => {
                if (item !== checkbox) item.checked = false;
            });
        }
    </script>
</head>
<body>

    <h2>상품 등록</h2>
    
    <form action="${pageContext.request.contextPath}/store/register" method="POST" enctype="multipart/form-data">
    <!-- CSRF 토큰 추가 -->
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
        
	    <label for="product_name">상품 이름</label>
	    <input type="text" id="product_name" name="productName" required>
	
	    <label for="product_price">상품 가격</label>
	    <input type="number" id="product_price" name="productPrice" required>
	
	    <label for="stock">재고 수량</label>
	    <input type="number" id="stock" name="stock" min="0" required>
	
	    <label for="product_description">상품 소개</label>
	    <textarea id="product_description" name="productDescription" rows="4" required></textarea>
	
	    <label for="product_image">상품 이미지</label>
	    <input type="file" id="product_image" name="productImage" accept="image/*" required>
    	
        <!-- 미리보기 이미지 -->
	    <div id="imagePreviewContainer" style="margin-top: 10px; display: none;">
	        <img id="imagePreview" src="" alt="미리보기" style="max-width: 200px; display: block;">
	    </div>
	    
        <div class="selection-container">
	        <h3>상품 카테고리</h3>
	        <select name="ingredientId" required>
	            <option value="">카테고리를 선택하세요</option>
	            <c:forEach var="ingredient" items="${ingredientsList}">
	                <option value="${ingredient.ingredientId}">${ingredient.ingredientName}</option>
	            </c:forEach>
	        </select>
	    </div>
	    
        <button type="submit">상품 등록</button>
    </form>

<script>
    document.addEventListener("DOMContentLoaded", function() {
        var fileInput = document.getElementById("product_image");
        if (fileInput) {
            fileInput.addEventListener("change", previewImage);
            console.log("파일 입력 이벤트 등록 완료!");
        } else {
            console.error("파일 입력 필드 찾을 수 없음!");
        }
    });

    function previewImage(event) {
        var input = event.target || event.srcElement;
        var preview = document.getElementById("imagePreview");
        var previewContainer = document.getElementById("imagePreviewContainer");

        console.log("파일 선택 이벤트 감지됨!");

        if (input.files && input.files[0]) {
            var reader = new FileReader();

            reader.onload = function(e) {
                console.log("이미지 데이터 로드됨:", e.target.result);

                preview.src = e.target.result;
                previewContainer.style.display = "block"; // 미리보기 컨테이너 보이기
            };

            reader.readAsDataURL(input.files[0]);
        } else {
            previewContainer.style.display = "none"; // 파일이 선택되지 않으면 숨기기
            console.warn("파일이 선택되지 않음!");
        }
    }
</script>

</body>
</html>
