package com.example.rgrgit.service;

import com.example.rgrgit.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    @Autowired
    public JavaMailSender emailSender;

    public void send(String mailTo, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(mailTo);
        message.setSubject(subject);
        message.setText(text);
        this.emailSender.send(message);
    }

    public void sendGreetingMessage(User u) {
        send(u.getEmail(), "Успешная регистрация | Daily", "Здравствуйте, " + u.getUsername() + "! Вы успешно зарегистрировались. Для активации перейдите по ссылке http://localhost:8080/activate/" + u.getActivationCode());
    }
/*
	public void sendTestResult(Student t, String not) {
		send(t.getEmail(), "Система тестирования: результаты теста", "Здравствуйте! "+not);
	}
	public void sendRecoverMessage(Student u) {
		send(u.getEmail(), "Система тестирования: сменя пароля", "Здравствуйте! Для смены пароля проследуйте по ссылке: http://localhost:8080/recoverPassword/" + u.getActivationCode());
	}
	public void sendInvitation(Student u, Student t) {
		send(u.getEmail(), "Система тестирования: приглашение пройти тест", "Здравствуйте! Вы приглашены пройти тест '" + t.getName() + "'. Для прохождения проследуйте по ссылке: http://localhost:8080/take/" + t.getId());
	}
	*/
}
