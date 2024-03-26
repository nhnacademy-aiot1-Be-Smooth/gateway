package live.smoothing.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

// std: RestTemplate은 동기/블로킹 작업을 수행하는데 사용되는데, WebClient는 비동기/넌블로킹 작업을 수행하는데 사용된다.
// std: WebClient는 Spring WebFlux에서 제공하는 비동기 HTTP 클라이언트이다.
// std: WebClient는 기본적으로 Reactor의 Mono와 Flux를 사용하여 비동기 작업을 수행한다.
// std: X-USER-ID 헤더가 존재한다고 가정하고 있음. 헤더가 없을 경우 NullPointerException 잠재 가능성

// Todo: 어떤 상황에서 'X-USER-AUTHORITY' 헤더를 추가해야 하는지 알아보기
@Slf4j
@Component
public class AuthorizationGlobalFilter implements GlobalFilter, Ordered {
    private final String serviceName = "user-service";
    private final String url = "http://" + serviceName + "/api/user/{userId}";
    private final WebClient webClient;

    public AuthorizationGlobalFilter(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.debug("authorization-header-filter");

        ServerHttpRequest request = exchange.getRequest();

        if(!request.getHeaders().containsKey("X-NEED-AUTHENTICATION")) {
            return chain.filter(exchange);
        }

        String userId = request.getHeaders().get("X-USER-ID").get(0);

        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder.build(userId))
                .exchangeToMono(clientResponse -> clientResponse.bodyToMono(Object.class))
                .flatMap(response -> {
                    exchange.mutate().request(builder -> {
                        builder.header("X-USER-AUTHORITY", String.valueOf(response));
                    });
                    return chain.filter(exchange);
                });
    }

    @Override
    public int getOrder() {

        return 1;
    }
}
