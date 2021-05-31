package io.github.loovien.locked;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DistributedLockedApplication {

    public static void main(String[] args) {
        SpringApplication.run(DistributedLockedApplication.class, args);
    }

}
