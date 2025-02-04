package com.dishcovery.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class MailSendService {

    @Autowired
    private JavaMailSender mailSender;

    //인증메일 보내기
    @Async // 비동기 처리 및 스레드 풀 지정
    public void sendAuthMail(String email, String authKey) {

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
        // return authKey;
    }

    //인증코드 메일 보내기
    public void sendVerificationCode(String email, String verificationCode) {
        MimeMessage mail = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mail, true, "UTF-8");
            String mailContent = "<html><head><meta charset='UTF-8'></head><body>" +
                    "<h1>[인증번호]</h1><br><p>인증번호는 다음과 같습니다. <br>" + verificationCode + "</p>"
                    + "<br>해당 인증번호를 인증번호 확인란에 기입하여 주세요."
                    + "</body></html>";

            helper.setSubject("비밀번호 찾기 인증번호");
            helper.setText(mailContent, true); // 두 번째 인자를 true 로 설정하면 HTML 메일로 처리
            helper.setTo(email);
            mailSender.send(mail);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    //임시비밀번호 보내기
    @Async
    public void sendTemporaryPassword(String email, String tempPw){
        //인증메일 보내기
        MimeMessage mail = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mail, true, "UTF-8");
            String mailContent = "<html><head><meta charset='UTF-8'></head><body>" +
                    "<h1>임시 비밀번호</h1><br><p>아래 임시 비밀번호로 로그인 하신 후 비밀번호를 변경하시길 바랍니다.</p><br>" +
                    tempPw + "</body></html>";

            helper.setSubject("임시 비밀번호");
            helper.setText(mailContent, true); // 두 번째 인자를 true 로 설정하면 HTML 메일로 처리
            helper.setTo(email);
            mailSender.send(mail);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
