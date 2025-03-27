<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>비밀번호 찾기</title>
    <script src="https://code.jquery.com/jquery-3.7.1.js"></script>
    <meta name="_csrf" content="${_csrf.token}"/>
    <meta name="_csrf_header" content="${_csrf.headerName}"/>
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            margin: 0;
            padding: 0;
            display: flex;
            flex-direction: column;
            min-height: 100vh;
            background-color: #f4f7f9;
        }

        .content {
            flex: 1;
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .form-container {
            width: 500px;
            padding: 40px;
            border-radius: 12px;
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.08);
            background-color: white;
        }

        .input-group {
            display: flex;
            align-items: center;
            margin-bottom: 25px;
        }

        .input-group input, .input-group select, .input-group button {
            padding: 14px;
            border: 1px solid #ced4da;
            border-radius: 6px;
            font-size: 16px;
        }

        .input-group input {
            flex: 1;
        }

        .input-group select {
            width: 150px;
            margin-left: 10px;
        }

        .input-group button {
            background-color: #007bff;
            color: white;
            cursor: pointer;
            margin-left: 10px;
            border: none;
        }

        .mail-check-box {
            margin-top: 20px;
            display: flex;
            align-items: center;
        }

        .mail-check-box input {
            flex: 1;
            padding: 14px;
            border: 1px solid #ced4da;
            border-radius: 6px;
            font-size: 16px;
            margin-right: 10px;
        }

        .mail-check-box button {
            background-color: #28a745;
            color: white;
            cursor: pointer;
            padding: 14px;
            border: none;
            border-radius: 6px;
            font-size: 16px;
        }

        .custom-email-input {
            display: none;
        }

        #mail-check-warn {
            display: block;
            margin-top: 10px;
            font-size: 14px;
            color: #dc3545;
        }

        h2 {
            text-align: center;
            margin-bottom: 30px;
            color: #343a40;
        }

        label {
            display: block;
            margin-bottom: 10px;
            font-weight: 600;
            color: #495057;
        }

        .at-symbol {
            margin: 0 10px;
            font-size: 18px;
            color: #495057;
        }
    </style>
</head>
<body>
<div class="content">
    <div class="form-container">
        <h2>비밀번호 찾기</h2>
        <div class="form-group email-form">
            <div class="input-group">
                <input type="text" class="form-control" name="userEmail1" id="userEmail1" placeholder="이메일">
                &nbsp;&nbsp;@
                <select class="form-control" name="userEmail2" id="userEmail2">
                    <option value="naver.com">naver.com</option>
                    <option value="daum.net">daum.net</option>
                    <option value="gmail.com">gmail.com</option>
                    <option value="hanmail.com">hanmail.com</option>
                    <option value="yahoo.co.kr">yahoo.co.kr</option>
                    <option value="custom">직접 입력</option>
                </select>
                <input type="text" class="form-control custom-email-input" id="customEmailInput" placeholder="직접 입력">
                <button type="button" class="btn btn-primary" id="mail-Check-Btn">발급</button>
            </div>
            <div class="mail-check-box">
                <input class="form-control mail-check-input" placeholder="인증번호 6자리를 입력해주세요!" disabled="disabled"
                       maxlength="6">
                <button type="button" class="btn btn-primary" id="mail-Check-Submit-Btn" disabled="disabled">확인</button>
            </div>
            <span id="mail-check-warn"></span>
        </div>
    </div>
</div>
<script>
    $(document).ajaxSend(function (e, xhr, opt) {
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");

        xhr.setRequestHeader(header, token);
    });

    $('#userEmail2').change(function () {
        const selectedValue = $(this).val();
        const customInput = $('.custom-email-input');
        const formContainer = $('.form-container'); // form-container 요소를 선택합니다.

        if (selectedValue === 'custom') {
            customInput.show();
            customInput.focus();
            formContainer.css('width', '750px'); // form-container의 너비를 늘립니다.
        } else {
            customInput.hide();
            formContainer.css('width', '500px'); // form-container의 너비를 원래대로 되돌립니다.
        }
    });

    $('#mail-Check-Btn').click(function () {
        let email;
        if ($('#userEmail2').val() === 'custom') {
            email = $('#userEmail1').val() + "@" + $('#customEmailInput').val();
        } else {
            email = $('#userEmail1').val() + "@" + $('#userEmail2').val();
        }

        const checkInput = $('.mail-check-input');
        const checkSubmitBtn = $('#mail-Check-Submit-Btn');

        $.ajax({
            type: 'get',
            url: "../member/mailCheck?email=" + email,
            success: function (response) { // 응답 데이터를 받음
                const data = JSON.parse(response); // JSON 문자열을 객체로 파싱

                if (data.result === "success") {
                    checkInput.attr('disabled', false);
                    checkSubmitBtn.attr('disabled', false);
                    checkInput.data('code', data.authKey);
                    alert('인증번호가 전송되었습니다.');
                } else if (data.result === "not_found") {
                    alert('회원가입된 메일이 아닙니다.');
                } else {
                    alert("메일 전송에 실패했습니다.");
                }
            },
            error: function (xhr, status, error) {
                console.error("AJAX request failed", status, error);
                alert("서버와의 통신 중 오류가 발생했습니다.");
            }
        });
    });

    // 인증번호 비교
    $('#mail-Check-Submit-Btn').click(function () {
        const inputCode = $('.mail-check-input').val();
        const code = $('.mail-check-input').data('code');
        const $resultMsg = $('#mail-check-warn');
        let email;

        if ($('#userEmail2').val() === 'custom') {
            email = $('#userEmail1').val() + "@" + $('#customEmailInput').val();
        } else {
            email = $('#userEmail1').val() + "@" + $('#userEmail2').val();
        }

        if (inputCode === code) {
            $.ajax({
                type: 'post',
                url: "../member/sendTempPassword",
                data: {email: email},
                success: function (data) {
                    if (data === "success") {
                        alert('임시 비밀번호가 발급되었습니다. 로그인 화면으로 이동합니다.');
                        window.location.href = "../auth/login";
                    } else {
                        alert('임시 비밀번호 발급에 실패했습니다.');
                    }
                },
                error: function (xhr, status, error) {
                    console.error("AJAX request failed", status, error);
                    alert("서버와의 통신 중 오류가 발생했습니다.");
                }
            });
            $resultMsg.html('인증번호가 일치합니다.');
            $resultMsg.css('color', 'green');
            $('#mail-Check-Btn').attr('disabled', true);
            $('#userEmail1').attr('readonly', true);
            $('#userEmail2').attr('disabled', true);
            $('#customEmailInput').attr('readonly', true);
        } else {
            $resultMsg.html('인증번호가 불일치 합니다. 다시 확인해주세요!.');
            $resultMsg.css('color', 'red');
        }
    });
</script>
</body>
</html>