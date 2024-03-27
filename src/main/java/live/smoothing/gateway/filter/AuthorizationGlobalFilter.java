package live.smoothing.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Slf4j
@Component
public class AuthorizationGlobalFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        log.debug("authorization-header-filter");

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
}
