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

/**
 * Authorization 헤더를 검증하는 Global Filter
 *
 * @Author 신민석
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthorizationHeaderGlobalFilter implements GlobalFilter, Ordered {

    private static final String TOKEN_TYPE = "Bearer";

    private final GlobalFilterProperties properties;

    /**
     * Gateway Filter
     *
     * @param exchange ServerWebExchange
     * @param chain GatewayFilterChain
     * @return 헤더에 Access Token이 없을 경우 예외를 발생, Access Token이 존재할 경우 다음 필터로 이동
     *
     * @Author 신민석
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        if (isExcludePath(request.getMethodValue(), request.getPath().value())) {
            return chain.filter(exchange);
        }

        String accessTokenHeaderValue = exchange.getRequest().getHeaders().getFirst("Authorization");

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

    /**
     * Filter 의 동작순서를 1로 지정
     *
     * @Author 신민석
     */
    @Override
    public int getOrder() {

        return 1;
    }

    /**
     * 제외 경로인지 확인
     *
     * @param method
     * @param path
     * @return
     *
     * @Author 신민석
     */
    private boolean isExcludePath(String method, String path) {

        String excludePath = method + ":" + path;
        return properties.getExcludePath().contains(excludePath);
    }
}
