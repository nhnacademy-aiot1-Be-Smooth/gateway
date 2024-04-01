package live.smoothing.gateway.filter;

import live.smoothing.gateway.config.GlobalFilterProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * User ID 헤더를 추가하는 Global Filter
 *
 * @Author 신민석
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserIdHeaderGlobalFilter implements GlobalFilter, Ordered {

    private final GlobalFilterProperties properties;

    /**
     * UserId를 헤더에 추가하는 메서드
     *
     * @param exchange ServerWebExchange
     * @param chain GatewayFilterChain
     * @return User ID를 헤더에 추가하고 다음 필터로 이동
     *
     * @Author 신민석
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        if (isExcludePath(request.getMethodValue(), request.getPath().value())) {
            return chain.filter(exchange);
        }

        String userId = String.valueOf(exchange.getAttributes().get("userId"));

        exchange.mutate().request(builder -> {
            builder.header("X-USER-ID", userId);
        });

        return chain.filter(exchange);
    }

    /**
     * 필터의 순서를 3으로 지정
     *
     * @Author 신민석
     */
    @Override
    public int getOrder() {

        return 3;
    }

    /**
     * 제외 경로인지 확인
     *
     * @param method 메서드 이름
     * @param path 경로
     * @return 제외 경로인 경우 true, 아닌 경우 false
     *
     * @Author 신민석
     */
    private boolean isExcludePath(String method, String path) {

        String excludePath = method + ":" + path;
        return properties.getExcludePath().contains(excludePath);
    }
}
