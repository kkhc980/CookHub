<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Enroll</title>
    <script src="https://code.jquery.com/jquery-3.7.1.js"></script>
    <script type="text/javascript">
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
                        alert("사용 가능한 아이디입니다.");
                        $('#upwd1').focus();
                    } else {
                        alert("이미 사용중인 아이디입니다.\n다시 입력하세요.");
                        $('#email').select();
                    }
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    console.log("error : " + jqXHR.responseText + ", " + textStatus + ", " + errorThrown);
                }
            });

        }
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
</form>
</body>
</html>