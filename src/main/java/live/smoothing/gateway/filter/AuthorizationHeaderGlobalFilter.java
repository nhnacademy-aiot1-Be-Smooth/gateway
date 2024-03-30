package live.smoothing.gateway.filter;

import live.smoothing.gateway.config.GlobalFilterProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthorizationHeaderGlobalFilter implements GlobalFilter, Ordered {

    private final GlobalFilterProperties properties;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        if (isExcludePath(request.getMethodValue(), request.getPath().value())) {
            return chain.filter(exchange);
        }

        request.getHeaders().get(HttpHeaders.COOKIE);

        String accessToken = Optional.ofNullable(exchange.getRequest().getHeaders().getFirst("X-ACCESS-TOKEN"))
                .map(token -> token.substring(7))
                .orElse("");

        String refreshToken = Optional.ofNullable(exchange.getRequest().getHeaders().getFirst("X-REFRESH-TOKEN"))
                .map(token -> token.substring(7))
                .orElse("");

        exchange.getAttributes().put("accessToken", accessToken);
        exchange.getAttributes().put("refreshToken", refreshToken);

        return chain.filter(exchange);

    }

    @Override
    public int getOrder() {

        return 1;
    }

    private boolean isExcludePath(String method, String path) {

        String excludePath = method + ":" + path;
        return properties.getExcludePath().contains(excludePath);
    }
}
