package live.smoothing.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 요청에 대한 라우팅 클래스
 *
 * @author 박영준
 */
@Configuration
public class RouteLocatorConfig {

    /**
     * 라우팅을 담당 객체 설정 및 생성 메서드
     *
     * @param builder Spring Cloud Gateway에서 라우팅을 구성하기 위한 빌더 클래스
     * @return 라우팅 담당 객체
     */
    @Bean
    public RouteLocator myRoute(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("user-service",
                        p->p.path("/api/user/**").and()
                        .uri("lb://USER-SERVICE")
                )
                .route("auth-service",
                        p->p.path("/api/auth/**").and()
                        .uri("lb://AUTH-SERVICE")
                )
                .route("sensor-data-service",
                        p->p.path("/api/sensor/**").and()
                        .uri("lb://SENSOR-DATA-SERVICE")
                ).route("device-service",
                        p->p.path("/api/device/**").and()
                                .uri("lb://DEVICE-SERVICE")
                ) .route("ai-service",
                        p->p.path("/api/ai/**").and()
                                .uri("lb://AI-SERVICE"))
                .build();
    }
}