package live.smoothing.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Slf4j
@Component
public class UserIdHeaderFilter implements GlobalFilter, Ordered {

    @Override
    public int getOrder() {

        return 3;
    }


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("UserIdHeaderFilter");

        String userId = String.valueOf(exchange.getAttributes().getOrDefault("userId", null));

        // TODO : exception 처리
        if (Objects.isNull(userId)) {
            throw new RuntimeException();
        }

        exchange.mutate().request(builder -> {
            builder.header("X-USER-ID", userId);
        });

        return chain.filter(exchange);
    }
}
