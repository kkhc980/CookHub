<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Insert title here</title>
</head>
<body>
<p>상품명 : ${product.productName}</p>
<p>가격 : ${product.productPrice}원</p>
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
        <a class="btn_cart">장바구니 담기</a>
        <a class="btn_buy">바로구매</a>
    </div>
</div>
<script>
    // 수량 버튼 조작
    let quantity = $(".quantity_input").val();
    $(".plus_btn").on("click", function () {
        $(".quantity_input").val(++quantity);
    });
    $(".minus_btn").on("click", function () {
        if (quantity > 1) {
            $(".quantity_input").val(--quantity);
        }
    });

    // 장바구니 추가 버튼
    $(".btn_cart").on("click", function (e) {
        form.productCount = $(".quantity_input").val();
        $.ajax({
            url: '/cart/add', // 수정필요!!!
            type: 'POST',
            data: form,
            success: function (result) {
                cartAlert(result);
            }
        })
    });

    function cartAlert(result) {
        if (result == '0') {
            alert("장바구니에 추가를 하지 못하였습니다.");
        } else if (result == '1') {
            alert("장바구니에 추가되었습니다.");
        } else if (result == '2') {
            alert("장바구니에 이미 추가되어져 있습니다.");
        } else if (result == '5') {
            alert("로그인이 필요합니다.");
        }
    }

    // 바로구매 버튼
    $(".btn_buy").on("click", function () {
        let productCount = $(".quantity_input").val();
        $(".order_form").find("input[name='orders[0].productCount']").val(productCount);
        $(".order_form").submit();
    });
</script>
</body>
</html>