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

/**
 * Jwt 토큰 검증 클래스
 *
 * @author 박영준
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtVerificationGlobalFilter implements GlobalFilter, Ordered {

    private final JwtTokenProvider jwtTokenProvider;
    private final GlobalFilterProperties properties;

    /**
     * Jwt 토큰의 유효성을 검증하고 유요한 토큰일 경우 사용자 ID를 추출하여 요청 속성에 저장한다.
     * 토큰이 유효하지 않거나 예외가 발생하면 적절한 예외를 던진다.
     *
     * @param exchange 비동기 웹 요청과 응답을 모두 포함하는 객체
     * @param chain 게이트웨이 필터 체인
     * @return 필터 체인의 다음 필터로 이어지거나, 필터링이 완료된 후 최종 응답을 나타내는 Mono<Void> 객체
     */
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
