package org.project.trandit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
public class TranditApplication {

    public static void main(String[] args) {
        SpringApplication.run(TranditApplication.class, args);
    }

}
