package live.smoothing.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteLocatorConfig {

    @Bean
    public RouteLocator myRoute(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("user-service",
                        p->p.path("/api/user").and()
                        .uri("lb://USER-SERVICE")
                )
                .route("auth-service",
                        p->p.path("/api/auth").and()
                        .uri("lb://SMOOTHING-AUTH"))
                .build();
    }
}