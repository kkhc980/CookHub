<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>회원 정보 수정</title>
    <meta name="_csrf" content="${_csrf.token}"/>
    <meta name="_csrf_header" content="${_csrf.headerName}"/>
    <script src="https://code.jquery.com/jquery-3.7.1.js"></script>
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            margin: 0;
            padding: 20px;
            background-color: #f8f9fa;
        }

        .container {
            width: 90%;
            max-width: 500px;
            margin: 20px auto;
            background-color: #ffffff;
            border-radius: 10px;
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
            padding: 30px;
        }

        .form-group {
            margin-bottom: 25px;
        }

        .form-label {
            display: block;
            margin-bottom: 8px;
            font-weight: 600;
            color: #333;
        }

        .form-input {
            width: calc(100% - 22px);
            padding: 12px;
            border: 1px solid #ced4da;
            border-radius: 6px;
            margin-bottom: 5px;
            box-sizing: border-box;
            font-size: 16px;
        }

        .error-message {
            color: #dc3545;
            font-size: 0.9em;
            margin-top: 5px;
            display: block;
        }

        .submit-button {
            width: 100%;
            padding: 12px 20px;
            background-color: #007bff;
            color: white;
            border: none;
            border-radius: 6px;
            cursor: pointer;
            font-size: 16px;
            transition: background-color 0.3s ease;
        }

        .submit-button:hover {
            background-color: #0056b3;
        }
    </style>
</head>
<body>
<div class="container">
    <sec:authentication property="principal" var="user"/>
    <form id="updateForm" action="../member/update" method="POST">
        <div class="form-group">
            <label for="email" class="form-label">이메일</label>
            <div>${memberDTO.email}</div>
            <input type="hidden" name="email" value="${memberDTO.email}">
        </div>
        <div class="form-group">
            <label for="password" class="form-label">비밀번호</label>
            <input type="password" id="password" name="password" title="비밀번호" maxlength="16"
                   placeholder="비밀번호를 입력하세요" class="form-input">
            <span id="pwMsg" class="error-message"></span>
        </div>
        <div class="form-group">
            <label for="pwConfirm" class="form-label">비밀번호 재확인</label>
            <input type="password" id="pwConfirm" title="비밀번호 확인" maxlength="16" placeholder="비밀번호를 다시 입력하세요"
                   class="form-input">
            <span id="pwConfirmMsg" class="error-message"></span>
        </div>
        <div class="form-group">
            <label for="userName" class="form-label">이름</label>
            <input type="text" id="userName" name="name" title="이름" maxlength="10" value="${memberDTO.name}"
                   placeholder="이름을 입력하세요" class="form-input">
            <span id="nameMsg" class="error-message"></span>
        </div>
        <div class="form-group">
            <label for="phone" class="form-label">전화번호</label>
            <input type="text" id="phone" name="phone" title="전화번호" value="${memberDTO.phone}"
                   placeholder="전화번호를 입력하세요" class="form-input">
            <span id="phoneMsg" class="error-message"></span>
        </div>
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
        <button type="button" id="btnModify" class="submit-button">제출</button>
    </form>
</div>
<script type="text/javascript">
    $(document).ready(function () {
        var pwFlag = false; // memberPw 유효성 변수
        var pwConfirmFlag = false; // pwConfirm 유효성 변수
        var nameFlag = false; // memberName 유효성 변수
        var phoneFlag = true;

        // 비밀번호 유효성 검사
        $('#password').blur(function () {
            var memberPw = $('#password')
                .val();
            var pwRegExp = /^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$^&*-]).{8,16}$/;
            // 8 ~ 16 사이의 대문자, 소문자, 숫자, 특수문자를 1개 이상 포함하는 정규 표현식

            if (memberPw === '') { // 비밀번호가 입력되지 않은 경우
                $('#pwMsg').html('필수 입력입니다.');
                $('#pwMsg').css('color', 'red');
                pwFlag = false; // 유효성 false
                return;
            }

            if (!pwRegExp.test(memberPw)) { // 입력한 비밀번호와 정규표현식이 일치하지 않는 경우
                pwFlag = false; // 유효성 false
                $('#pwMsg').html('8~16자 영문 대/소문자, 숫자, 특수문자를 모두 포함하세요');
                $('#pwMsg').css('color', 'red');
            } else {
                $('#pwMsg').html('가능한 비밀번호입니다.');
                $('#pwMsg').css('color', 'green');
                pwFlag = true; // 유효성 true
            }
        }); // end memberPw.blur()

        // 비밀번호 확인 유효성 검사
        $('#pwConfirm').blur(function () {
            var memberPw = $('#password').val();
            var pwConfirm = $('#pwConfirm').val();

            // 비밀번호 확인을 입력하지 않은 경우
            if (pwConfirm === '') {
                $('#pwConfirmMsg').html('필수 입력입니다.');
                $('#pwConfirmMsg').css('color', 'red');
                pwConfirmFlag = false; // 유효성 false

                return;
            }

            // 입력한 비밀번호와 비밀번호 확인이 일치하는 경우
            if (memberPw === pwConfirm) {
                $('#pwConfirmMsg').html('비밀번호가 일치합니다.');
                $('#pwConfirmMsg').css('color', 'green');
                pwConfirmFlag = true; // 유효성 true

            } else {
                $('#pwConfirmMsg').html('비밀번호가 일치하지 않습니다.');
                $('#pwConfirmMsg').css('color', 'red');
                pwConfirmFlag = false; // 유효성 false
            }
        });

        // 이름 유효성 검사
        $('#userName').blur(function () {
            var memberName = $('#userName').val(); // 입력한 데이터 값

            if (memberName.trim() === '') { // 이름이 입력되지 않았을 경우
                $('#nameMsg').html('필수 입력입니다.');
                $('#nameMsg').css('color', 'red');
                nameFlag = false; // 유효성 false
                return;
            } else {
                $('#nameMsg').html('');
                nameFlag = true; // 유효성 true
            }
        });

        // 전화번호 유효성 검사 (input 이벤트로 변경)
        $('#phone').on('input', function () {
            var phoneNumber = $('#phone').val(); // 입력한 데이터 값
            var phoneRegex = /^010-\d{4}-\d{4}$/;

            if (!phoneRegex.test(phoneNumber) && phoneNumber.length > 0) {
                $('#phoneMsg').html('잘못된 전화번호 형식입니다.');
                $('#phoneMsg').css('color', 'red');
                phoneFlag = false;
            } else if (phoneNumber.length === 0) {
                $('#phoneMsg').html('필수 입력입니다.');
                $('#phoneMsg').css('color', 'red');
                phoneFlag = false;
            } else {
                $('#phoneMsg').html('');
                phoneFlag = true;
            }
        });

        // 회원 정보 form 데이터 전송
        $('#btnModify').click(function () {
            if (pwFlag && pwConfirmFlag && nameFlag && phoneFlag) { // 입력된 데이터가 모두 유효한 경우
                $('#updateForm').submit(); // form 전송 실행
            }
        });
    });
</script>
</body>
</html>