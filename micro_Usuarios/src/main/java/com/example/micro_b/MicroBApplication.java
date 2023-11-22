package com.example.micro_b;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.example.micro_b")
public class MicroBApplication {

    public static void main(String[] args) {
        SpringApplication.run(MicroBApplication.class, args);
    }

}
