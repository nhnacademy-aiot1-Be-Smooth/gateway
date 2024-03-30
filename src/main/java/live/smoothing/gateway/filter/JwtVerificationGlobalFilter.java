package live.smoothing.gateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.jsonwebtoken.MalformedJwtException;
import live.smoothing.gateway.config.GlobalFilterProperties;
import live.smoothing.gateway.exception.InvalidJsonFormatException;
import live.smoothing.gateway.exception.JwtTokenInvalidException;
import live.smoothing.gateway.jwt.util.JwtCode;
import live.smoothing.gateway.jwt.util.JwtTokenProvider;
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

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtVerificationGlobalFilter implements GlobalFilter, Ordered {

    private final JwtTokenProvider jwtTokenProvider;
    private final GlobalFilterProperties properties;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        if (isExcludePath(request.getMethodValue(), request.getPath().value())) {
            return chain.filter(exchange);
        }

        String accessToken = String.valueOf(exchange.getAttributes().get("accessToken"));

        try {
            JwtCode jwtCode = jwtTokenProvider.validateToken(accessToken);

            if (jwtCode == JwtCode.INVALID) {
                throw new JwtTokenInvalidException(HttpStatus.UNAUTHORIZED, "유효하지 않은 Jwt 토큰 입니다.");
            }

            String userId = jwtTokenProvider.getUserId(accessToken);
            exchange.getAttributes().put("userId", userId);
            return chain.filter(exchange);

        } catch (JsonProcessingException e) {
            throw new InvalidJsonFormatException(HttpStatus.BAD_REQUEST, "Jwt 토큰의 json 포맷이 잘못 되었습니다.");
        } catch (MalformedJwtException e) {
            throw new JwtTokenInvalidException(HttpStatus.UNAUTHORIZED, "유효하지 않은 Jwt 토큰 입니다.");
        }
    }

    @Override
    public int getOrder() {
        return 2;
    }

    private boolean isExcludePath(String method, String path) {

        String excludePath = method + ":" + path;
        return properties.getExcludePath().contains(excludePath);
    }
}
