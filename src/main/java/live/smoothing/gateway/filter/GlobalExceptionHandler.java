package live.smoothing.gateway.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import live.smoothing.common.dto.ErrorResponse;
import live.smoothing.common.exception.CommonException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.Hints;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ErrorResponse errorResponse;
        String path = exchange.getRequest().getPath().value();

        if (ex instanceof CommonException) {
            CommonException commonException = (CommonException) ex;
            errorResponse = commonException.toEntity(path);
        } else {
            errorResponse = ErrorResponse.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .errorMessage("서버 내부 오류 발생")
                    .path(path)
                    .build();
        }

        ServerHttpResponse response = exchange.getResponse();
        return response.writeWith(
                    new Jackson2JsonEncoder(objectMapper).encode(
                            Mono.just(errorResponse),
                            response.bufferFactory(),
                            ResolvableType.forInstance(errorResponse),
                            MediaType.APPLICATION_JSON,
                            Hints.from(Hints.LOG_PREFIX_HINT, exchange.getLogPrefix())
                    )
        );
    }
}