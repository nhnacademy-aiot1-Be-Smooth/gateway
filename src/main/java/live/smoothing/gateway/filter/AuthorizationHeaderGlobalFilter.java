package live.smoothing.gateway.filter;

import live.smoothing.gateway.config.GlobalFilterProperties;
import live.smoothing.gateway.exception.AuthorizationNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthorizationHeaderGlobalFilter implements GlobalFilter, Ordered {

    private static final String TOKEN_TYPE = "Bearer";

    private final GlobalFilterProperties properties;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        if (isExcludePath(request.getMethodValue(), request.getPath().value())) {
            return chain.filter(exchange);
        }

        String accessToken = Optional.ofNullable(exchange.getRequest().getHeaders().getFirst("smoothing-accessToken"))
                .map(token -> token.substring(TOKEN_TYPE.length()+1))
                .orElse(null);

        if (Objects.isNull(accessToken)) {
            throw new AuthorizationNotFoundException(HttpStatus.UNAUTHORIZED, "Access Token 헤더를 찾지 못했습니다.");
        }

        exchange.getAttributes().put("accessToken", accessToken);
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
