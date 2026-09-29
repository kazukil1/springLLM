package com.kaziki.springai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LangChainApplication {

    public static void main(String[] args) {
        SpringApplication.run(LangChainApplication.class, args);
        System.out.println("http://localhost:8080/");
    }
}
