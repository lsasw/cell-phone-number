package com.example.cellphonenumber;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class CellPhoneNumberApplication {

    public static void main(String[] args) {
        SpringApplication.run(CellPhoneNumberApplication.class, args);
    }

}