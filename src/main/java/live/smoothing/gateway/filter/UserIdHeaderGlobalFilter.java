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

@Slf4j
@Component
@RequiredArgsConstructor
public class UserIdHeaderGlobalFilter implements GlobalFilter, Ordered {

    private final GlobalFilterProperties properties;

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

    @Override
    public int getOrder() {

        return 3;
    }

    private boolean isExcludePath(String method, String path) {

        String excludePath = method + ":" + path;
        return properties.getExcludePath().contains(excludePath);
    }
}
