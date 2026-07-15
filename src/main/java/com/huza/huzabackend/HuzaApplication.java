package huza.huzabackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.example.auth","com.example.auth.controller","com.example.auth.service","com.example.auth.repository" })
public class HuzaApplication {

    public static void main(String[] args) {
        SpringApplication.run(HuzaApplication.class, args);
    }

}
