package hunre.edu.vn.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConsultationConfig {
    @Value("${app.base-video-call-url}")
    private String baseVideoCallUrl;

    @Bean
    public String baseVideoCallUrl() {
        return baseVideoCallUrl;
    }
}
