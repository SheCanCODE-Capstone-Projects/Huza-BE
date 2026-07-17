package com.huza.huzabackend.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.StandardEnvironment;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class DotenvEnvironmentPostProcessorTest {

    @Test
    void loadsDotenvPropertiesIntoEnvironment() throws IOException {
        Path tempDir = Files.createTempDirectory("dotenv-test");
        Path envFile = tempDir.resolve(".env");
        Files.writeString(envFile, "DB_URL=jdbc:postgresql://localhost:5432/huza\n"
                + "DB_USERNAME=postgres\n"
                + "DB_PASSWORD=admin\n");

        String originalUserDir = System.getProperty("user.dir");
        System.setProperty("user.dir", tempDir.toString());

        try {
            StandardEnvironment environment = new StandardEnvironment();
            EnvironmentPostProcessor postProcessor = new DotenvEnvironmentPostProcessor();
            postProcessor.postProcessEnvironment(environment, new SpringApplication());

            assertThat(environment.getProperty("DB_URL"))
                    .isEqualTo("jdbc:postgresql://localhost:5432/huza");
            assertThat(environment.getProperty("DB_USERNAME"))
                    .isEqualTo("postgres");
            assertThat(environment.getProperty("DB_PASSWORD"))
                    .isEqualTo("admin");
        } finally {
            if (originalUserDir != null) {
                System.setProperty("user.dir", originalUserDir);
            } else {
                System.clearProperty("user.dir");
            }
        }
    }
}
