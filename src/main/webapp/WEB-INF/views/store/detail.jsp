<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>상품 상세 정보</title>
    <script src="https://code.jquery.com/jquery-3.7.1.js"></script>
    <style>
        body {
            font-family: sans-serif;
            margin: 20px;
        }

        .product-image {
            max-width: 100%;
            max-height: 300px;
            margin-bottom: 20px;
        }

        .product-info p {
            margin: 10px 0;
        }

        .button {
            display: flex;
            flex-direction: column;
            align-items: flex-start;
            margin-top: 20px;
        }

        .button_quantity {
            display: flex;
            align-items: center;
            margin-bottom: 10px;
        }

        .quantity_input {
            width: 50px;
            text-align: center;
            padding: 5px;
            margin: 0 10px;
            border: 1px solid #ddd;
        }

        .minus_btn, .plus_btn {
            background-color: #f0f0f0;
            border: 1px solid #ddd;
            padding: 5px 10px;
            cursor: pointer;
        }

        .btn_cart, .purchase-button {
            background-color: #4CAF50;
            border: none;
            color: white;
            padding: 10px 20px;
            text-align: center;
            text-decoration: none;
            display: inline-block;
            font-size: 16px;
            cursor: pointer;
            border-radius: 5px;
            margin: 5px;
        }

        .btn_cart {
            background-color: #008CBA;
        }
    </style>
</head>
<body>
<img src="${pageContext.request.contextPath}/uploads/${product.productImagePath}"
     alt="상품 이미지"
     onerror="이미지 없음"
     class="product-image">
<div class="product-info">
    <p>상품명 : ${product.productName}</p>
    <p>가격 : ${product.productPrice}원</p>
    <p>총 가격 : <span id="total-price">${product.productPrice}</span>원</p>
    <p>재고 : ${product.stock}개</p>
</div>
<div class="button">
    <div class="button_quantity">
        주문수량
        <span>
            <button class="minus_btn">-</button>
        </span>
        <input type="text" class="quantity_input" value="1">
        <span>
          <button class="plus_btn">+</button>
      </span>
    </div>
    <div class="button_set">
        <button class="btn_cart">장바구니</button>
        <button class="purchase-button"
                onclick="purchaseProduct('${product.productId}', '${product.productName}', ${product.productPrice})">
            구매하기
        </button>
    </div>
</div>
<script src="https://code.jquery.com/jquery-3.7.1.js"></script>
<script>
    //초기 가격 설정
    let stock = ${product.stock};
    let productPrice = ${product.productPrice};
    let quantity = $(".quantity_input").val();
    updateTotalPrice();

    // 수량 증가 버튼
    $(".plus_btn").on("click", function () {
        if (quantity < stock) {
            $(".quantity_input").val(++quantity);
            updateTotalPrice();
        } else {
            alert("재고량이 부족합니다.");
        }
    });

    // 수량 감소 버튼
    $(".minus_btn").on("click", function () {
        if (quantity > 1) {
            $(".quantity_input").val(--quantity);
            updateTotalPrice();
        }
    });

    // 입력값 제한 및 가격 업데이트
    $(".quantity_input").on("input", function () {
        let value = $(this).val();
        if (!/^[0-9]*$/.test(value)) {
            alert("숫자만 입력하세요.");
            $(this).val(value.replace(/[^0-9]/g, ''));
        }

        quantity = parseInt($(this).val()) || 1;

        if (quantity > stock) {
            alert("재고량이 부족합니다.");
            quantity = stock;
        }
        if (quantity < 1) {
            quantity = 1;
        }
        $(this).val(quantity);
        updateTotalPrice();
    });

    // 총 가격 업데이트 함수
    function updateTotalPrice() {
        let totalPrice = productPrice * quantity;
        $("#total-price").text(totalPrice);
    }

    // 장바구니 버튼
    $(".btn_cart").on("click", function () {
        let productCount = $(".quantity_input").val();

        if (productCount > stock) {
            alert("재고량이 부족합니다.");
            return;
        }

        $.ajax({
            type: "POST",
            url: "../cart/add/${product.productId}",
            data: {productCount: productCount},
            success: function (response) {
                // response.body를 사용하여 true/false 값 확인
                if (response.body) {
                    alert("장바구니에 추가되었습니다!");
                    window.location.href = "../cart";
                } else {
                    alert("이미 장바구니에 있는 상품입니다.");
                }
            },
            error: function (error) {
                alert("오류가 발생했습니다.");
            }
        });
    });

    // 바로구매 버튼
    function purchaseProduct(productId, productName) {
        let productCount = $(".quantity_input").val();
        if (productCount > stock) {
            alert("재고량이 부족합니다.");
            return;
        }

        let totalPrice = productPrice * productCount;

        let form = document.createElement("form");
        form.method = "POST";
        form.action = '${pageContext.request.contextPath}/store/purchase';

        let csrfInput = document.createElement("input");
        csrfInput.type = "hidden";
        csrfInput.name = "${_csrf.parameterName}";
        csrfInput.value = "${_csrf.token}";

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

        let input4 = document.createElement("input");
        input4.type = "hidden";
        input4.name = "productCount";
        input4.value = productCount;

        form.appendChild(csrfInput);
        form.appendChild(input1);
        form.appendChild(input2);
        form.appendChild(input3);
        form.appendChild(input4);
        document.body.appendChild(form);
        form.submit();
    }

    // 숫자만 입력하도록 제한 및 재고량 체크
    $(".quantity_input").on("input", function () {
        let value = $(this).val();
        if (!/^[0-9]*$/.test(value)) { // 정규식으로 숫자만 허용
            alert("숫자만 입력하세요.");
            $(this).val(value.replace(/[^0-9]/g, '')); // 숫자 이외의 문자 제거
        }

        quantity = parseInt($(this).val()) || 1; // 현재 quantity 업데이트

        if (quantity > stock) {
            alert("재고량이 부족합니다.");
            $(this).val(stock);
            quantity = stock;
        }

        if (quantity < 1) {
            quantity = 1;
            $(this).val(1);
        }

    });
</script>
</body>
</html>