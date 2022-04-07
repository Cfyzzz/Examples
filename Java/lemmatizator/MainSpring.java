package ru.nedovizin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(YAMLConfig.class)
public class MainSpring {
    public static void main(String[] args) {
        SpringApplication.run(MainSpring.class, args);
    }
}
