<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Signup</title>
    <meta name="_csrf" content="${_csrf.token}"/>
    <meta name="_csrf_header" content="${_csrf.headerName}"/>
    <script src="https://code.jquery.com/jquery-3.7.1.js"></script>
    <script type="text/javascript">
        let isEmailChecked = false; // 이메일 중복 확인 여부를 저장하는 변수

        $(document).ajaxSend(function (e, xhr, opt) {
            var token = $("meta[name='_csrf']").attr("content");
            var header = $("meta[name='_csrf_header']").attr("content");

            xhr.setRequestHeader(header, token);
        });

        function validate() {
            //전송 보내기전 (submit 버튼 클릭시) 입력값들이 유효한지 검사

            //암호와 암호 확인이 일치하는지 체크
            var pwd1 = document.getElementById("upwd1").value;
            var pwd2 = $('#upwd2').val();

            if (pwd1 !== pwd2) {
                alert("암호와 암호 확인이 일치하지 않습니다.\n" + "다시 입력하세요.");
                document.getElementById("upwd1").select();
                return false; //전송 안 함
            }

            // 이메일 중복 확인을 했는지 체크
            if (!isEmailChecked) {
                alert("이메일 중복 확인을 해주세요.");
                $('#email').focus();
                return false; //전송 안 함
            }

            return true; //전송함
        }

        //아이디 중복을 확인하기 위한 ajax 요청 처리용 함수
        function dupCheckId() {
            var email = $('#email').val().trim();
            if (email === '') {
                alert('이메일을 입력해주세요.');
                $('#email').focus();
                return false;
            }
            $.ajax({
                url: "../member/emailCheck",
                type: "post",
                data: {email: email},
                success: function (data) {
                    console.log("success : " + data);
                    if (data == 'ok') {
                        alert("사용 가능한 이메일입니다.");
                        $('#upwd1').focus();
                        isEmailChecked = true; // 중복 확인 완료
                    } else if (data == 'registered') {
                        alert("이미 가입된 이메일입니다.");
                        $('#email').select();
                        isEmailChecked = false; // 중복 확인 실패
                    } else if (data == 'pending') {
                        alert("이메일 인증 진행 중인 아이디입니다.");
                        $('#email').select();
                        isEmailChecked = false; // 중복 확인 실패
                    } else {
                        alert("오류가 발생했습니다. 다시 시도해주세요.");
                        isEmailChecked = false;
                    }
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    console.log("error : " + jqXHR.responseText + ", " + textStatus + ", " + errorThrown);
                    isEmailChecked = false; // 에러 발생 시 중복 확인 실패 처리
                }
            });
        }

        // 이메일 필드에 이벤트 리스너 추가 (이메일 변경 시 중복 확인 초기화)
        $('#email').on('input', function () {
            isEmailChecked = false; // 이메일 변경 시 중복 확인 초기화
        });
    </script>
</head>
<body>
<form action="../member/signup" method="post" onsubmit="return validate();">
    <table>
        <tr>
            <th>회원정보를 입력해 주세요. (* 표시는 필수입력 항목입니다)</th>
        </tr>
        <tr>
            <th>회원 정보</th>
        </tr>
        <tr>
            <th>* 이 름</th>
            <td><input type="text" name="name" id="name" required></td>
        </tr>
        <tr>
            <th>* 이메일</th>
            <td>
                <input type="text" name="email" id="email" required>

                <input type="button" value="중복확인" onclick="dupCheckId();">
            </td>
        </tr>
        <tr>
            <th>* 비밀번호</th>
            <td><input type="password" name="password" id="upwd1" required></td>
        </tr>
        <tr>
            <th>* 비밀번호 확인</th>
            <td><input type="password" id="upwd2" required></td>
        </tr>
        <tr>
            <th>전화번호</th>
            <td><input type="tel" name="phone" id="phone"></td>
        </tr>
        <tr>
            <td><input type="submit" value="가입하기"></td>
        </tr>
    </table>
    <!-- CSRF 토큰 -->
    <input type="hidden" name="${_csrf.parameterName }" value="${_csrf.token }">
</form>
</body>
</html>