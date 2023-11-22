package com.example.micro_b.configs;

import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class EmailConfig {

    public static Properties getProperties(){
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.user", "fernorte04@gmail.com");
        properties.put("mail.smtp.password", "nabl wmqi yqfd xmcx");
        return properties;
    }

}
