package com.huza.huzabackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.huza.huzabackend","com.huza.huzabackend.controller","com.huza.huzabackend.service","com.huza.huzabackend.repository" })
public class HuzaApplication {

    public static void main(String[] args) {
        SpringApplication.run(HuzaApplication.class, args);
    }

}
