package com.dishcovery.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Random;

@Service
public class MailSendService {

    private int size;

    @Autowired
    private JavaMailSender mailSender;

    //인증키 생성
    private String getKey(int size) {
        this.size = size;
        return getAuthCode();
    }

    //인증코드 난수 발생
    private String getAuthCode() {
        Random random = new Random();
        StringBuffer buffer = new StringBuffer();
        int num = 0;

        while (buffer.length() < size) {
            num = random.nextInt(10);
            buffer.append(num);
        }

        return buffer.toString();
    }

    //인증메일 보내기
    public String sendAuthMail(String email) {
        //6자리 난수 인증번호 생성
        String authKey = getKey(6);

        //인증메일 보내기
        MimeMessage mail = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mail, true, "UTF-8");
            String mailContent = "<html><head><meta charset='UTF-8'></head><body>" +
                    "<h1>[이메일 인증]</h1><br><p>아래 링크를 클릭하시면 이메일 인증이 완료됩니다.</p>"
                    + "<a href='http://localhost:8080/project/member/signUpConfirm?email="
                    + email + "&authKey=" + authKey + "' target='_blank'>이메일 인증 확인</a>" +
                    "</body></html>";

            helper.setSubject("회원가입 이메일 인증");
            helper.setText(mailContent, true); // 두 번째 인자를 true 로 설정하면 HTML 메일로 처리
            helper.setTo(email);
            mailSender.send(mail);
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return authKey;
    }
}
