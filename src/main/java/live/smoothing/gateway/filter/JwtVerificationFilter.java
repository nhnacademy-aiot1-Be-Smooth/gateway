package live.smoothing.gateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import live.smoothing.gateway.jwt.util.JwtCode;
import live.smoothing.gateway.jwt.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtVerificationFilter implements GlobalFilter, Ordered {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String accessToken = String.valueOf(exchange.getAttributes().getOrDefault("accessToken", null));
        String refreshToken = String.valueOf(exchange.getAttributes().getOrDefault("refreshToken", null));

        log.debug("[accessToken]: {}", accessToken);
        log.debug("[refreshToken]: {}", refreshToken);

        // TODO: exception 처리
        try {
            JwtCode jwtCode = jwtTokenProvider.validateToken(accessToken);

            if (jwtCode == JwtCode.INVALID) {
                throw new RuntimeException();
            }

            String userId = jwtTokenProvider.getUserId(accessToken);

            if (jwtCode == JwtCode.EXPIRED) {
                throw new RuntimeException();
            }

            exchange.getAttributes().put("userId", userId);
            return chain.filter(exchange);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
