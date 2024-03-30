package live.smoothing.gateway.filter;

import live.smoothing.gateway.config.GlobalFilterProperties;
import live.smoothing.gateway.exception.AuthorizationNotFoundException;
import live.smoothing.gateway.exception.JwtTokenInvalidException;
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

        String accessTokenHeaderValue = exchange.getRequest().getHeaders().getFirst("smoothing-accessToken");

        if (Objects.isNull(accessTokenHeaderValue)) {
            throw new AuthorizationNotFoundException(HttpStatus.UNAUTHORIZED, "Access Token 헤더를 찾지 못했습니다.");
        }

        if (accessTokenHeaderValue.length() <= TOKEN_TYPE.length()) {
            throw new JwtTokenInvalidException(HttpStatus.UNAUTHORIZED, "Access Token 값이 유효하지 않습니다.");
        }

        String accessToken = accessTokenHeaderValue.substring(TOKEN_TYPE.length()+1);
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
