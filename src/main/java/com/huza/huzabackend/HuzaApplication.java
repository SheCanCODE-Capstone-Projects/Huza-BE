package huza.huzabackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HuzaApplication {
    public static void main(String[] args) {
        SpringApplication.run(HuzaApplication.class, args);
    }
}