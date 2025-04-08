package hunre.edu.vn.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
@SpringBootApplication
@ComponentScan(basePackages = {
        "hunre.edu.vn.backend",
        "hunre.edu.vn.backend.mapper",
        "hunre.edu.vn.backend.repository",
        "hunre.edu.vn.backend.service",
        "hunre.edu.vn.backend.controller"
})
@EntityScan(basePackages = "hunre.edu.vn.backend.entity")
@EnableJpaRepositories(basePackages = "hunre.edu.vn.backend.repository")
public class BackEndApplication {
    public static void main(String[] args) {
        SpringApplication.run(BackEndApplication.class, args);
    }
}