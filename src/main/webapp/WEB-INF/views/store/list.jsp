<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>상품 목록</title>
    <style>
     body {
            font-family: Arial, sans-serif;
            margin: 20px;
            padding: 20px;
        }

        .product-list {
            display: flex;
            flex-wrap: wrap;
            gap: 20px;
            justify-content: center;
        }

        .product-card:hover {
            transform: scale(1.05);
        }
        
        .product-card {
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
		
		.product-image-container {
		    width: 200px;
		    height: 200px;
		    display: flex;
		    align-items: center;  /* 수직 중앙 정렬 */
		    justify-content: center; /* 수평 중앙 정렬 */
		    border: 1px solid #ddd;
		    overflow: hidden; /* 이미지 크기 초과 방지 */
		}
		
		.product-image {
		    max-width: 100%;  /* 컨테이너 크기에 맞춤 */
		    max-height: 100%; /* 컨테이너 크기에 맞춤 */
		    object-fit: contain; /* 비율 유지하며 꽉 차도록 */
		}
		
		.no-image-text {
		    display: none; /* 기본적으로 숨김 */
		    color: gray;
		    font-size: 14px;
		    text-align: center;
		    position: absolute;
		}

        .product-info {
            padding: 10px;
        }

        .product-title {
            font-size: 18px;
            margin: 5px 0;
        }

        .product-meta {
            color: #777;
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
        
		.purchase-button {
		    display: block;
		    width: 80%;
		    margin: 10px auto;
		    padding: 10px;
		    background-color: #ff5722;
		    color: white;
		    border: none;
		    border-radius: 5px;
		    cursor: pointer;
		    font-size: 14px;
		}
		
		.purchase-button:hover {
		    background-color: #e64a19;
		}

    </style>
</head>
<body>

    <h2>상품 목록</h2>

    <a href="${pageContext.request.contextPath}/store/register" class="register-btn">상품 등록</a>

    <!-- 상품 리스트 -->
    <div class="product-list">
        <c:choose>
            <c:when test="${not empty productList}">
				<c:forEach var="products" items="${productList}">
				    <div class="product-card">
				        <div class="product-image-container">
				            <img class="product-image" 
				                 src="${pageContext.request.contextPath}/uploads/${products.productImagePath}" 
				                 alt="상품 이미지"
				                 onerror="this.style.display='none'; this.nextElementSibling.style.display='block';">
				            <span class="no-image-text">이미지 없음</span>
				        </div>
				        <div class="product-info">
				            <h3 class="product-title">${products.productName}</h3>
				            <p class="product-meta">
				                가격: ${products.productPrice}원 | 재고: ${products.stock}
				            </p>
				            <button class="purchase-button" onclick="purchaseProduct('${products.productId}', '${products.productName}', ${products.productPrice})">구매하기</button>
				        </div>
				    </div>
				</c:forEach>

            </c:when>
            <c:otherwise>
                <p>등록된 상품이 없습니다.</p>
            </c:otherwise>
        </c:choose>
    </div>

<div class="pagination-container">
    <c:if test="${pageMaker.prev}">
        <a class="pagination-link" 
           href="?pageNum=${pageMaker.startNum - 1}&pageSize=${pagination.pageSize}">이전</a>
    </c:if>
    
    <c:forEach var="pageNum" begin="${pageMaker.startNum}" end="${pageMaker.endNum}">
        <a class="pagination-link ${pagination.pageNum == pageNum ? 'active' : ''}" 
           href="?pageNum=${pageNum}&pageSize=${pagination.pageSize}">${pageNum}</a>
    </c:forEach>

    <c:if test="${pageMaker.next}">
        <a class="pagination-link" 
           href="?pageNum=${pageMaker.endNum + 1}&pageSize=${pagination.pageSize}">다음</a>
    </c:if>
</div>

<script>
function purchaseProduct(productId, productName, productPrice) {
    let form = document.createElement("form");
    form.method = "POST"; // POST 사용 (보안 강화)
    form.action = '${pageContext.request.contextPath}/store/purchase';

    let csrfInput = document.createElement("input");
    csrfInput.type = "hidden";
    csrfInput.name = "${_csrf.parameterName}";
    csrfInput.value = "${_csrf.token}"; // CSRF 방어

    let input1 = document.createElement("input");
    input1.type = "hidden";
    input1.name = "productId";
    input1.value = productId;

    let input2 = document.createElement("input");
    input2.type = "hidden";
    input2.name = "productName";
    input2.value = productName;

    let input3 = document.createElement("input");
    input3.type = "hidden";
    input3.name = "productPrice";
    input3.value = productPrice;

    form.appendChild(csrfInput);
    form.appendChild(input1);
    form.appendChild(input2);
    form.appendChild(input3);
    document.body.appendChild(form);
    form.submit();
}
</script>
</body>
</html>
