package hunre.edu.vn.backend.config;

import hunre.edu.vn.backend.serviceImpl.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService customUserDetailsService;

    public WebSecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            CustomUserDetailsService customUserDetailsService) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> {
                    authorize
                            .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/medicines/**").permitAll()
                            .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/appointments/**").permitAll()
                            .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/discounts/**").permitAll()
                            .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/doctor-services/**").permitAll()
                            .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/brands/**").permitAll()
                            .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/users/**").permitAll()
                            .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/services/**").permitAll()
                            .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/categories/**").permitAll()
                            .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/doctor-profiles/**").permitAll()
                            .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/medicine-categories/**").permitAll()
                            .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/medicine-media/**").permitAll()
                            .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/attributes/**").permitAll()
                            .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/reviews/**").permitAll()
                            .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/vouchers/**").permitAll()
                            .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/orders/**").permitAll()
                            .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/order-details/**").permitAll()
                            .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/service-bookings/**").permitAll()
                            .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/profile/**").permitAll()
                            .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/appointments/**").permitAll()
                            .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/patient-profiles/**").permitAll()
                            .requestMatchers(org.springframework.http.HttpMethod.GET,"/api/consultations/**").permitAll()
                            .requestMatchers("/api/auth/login").permitAll()
                            .requestMatchers( "/ws-consultation/**").authenticated()
                            .requestMatchers("/api/auth/forgot-password").permitAll()
                            .requestMatchers("/api/auth/reset-password").permitAll()
                            .requestMatchers("/api/auth/register").permitAll()
                            .requestMatchers("/api/auth/login/facebook").permitAll()
                            .requestMatchers("/api/auth/login/google").permitAll()
                            .requestMatchers("/v3/api-docs/**").permitAll()
                            .requestMatchers("/swagger-ui/**").permitAll()
                            .requestMatchers("/swagger-resources/**").permitAll()
                            .requestMatchers("/webjars/**").permitAll()

                            .anyRequest().authenticated();

                    // Thêm log để debug
                    System.out.println("Security configuration loaded successfully");
                })
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}