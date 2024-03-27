package live.smoothing.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.SignatureException;
import live.smoothing.gateway.exception.AuthorizationNotFoundException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;

@Slf4j
@Getter
@Component
public class JwtAuthorizationHeaderGlobalFilter implements GlobalFilter, Ordered {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.token_prefix}")
    private String prefix;

    @Value("${jwt.login_url}")
    private String loginUrl;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        log.debug("jwt-validation-filter");
        ServerHttpRequest request = exchange.getRequest();

        if(!request.getHeaders().containsKey("X-NEED-AUTHENTICATION")) {
            return chain.filter(exchange);
        }

        if(request.getURI().toString().equals(loginUrl)) {
            return chain.filter(exchange);
        }

        if(!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            throw new AuthorizationNotFoundException(request.getURI().toString());
        }

        String accessToken = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);

        if(accessToken != null && accessToken.startsWith(prefix)) {
            accessToken = accessToken.substring(prefix.length());
        }

        log.debug("accessToken:{}", accessToken);

        Key key = new SecretKeySpec(Base64.getDecoder().decode(secret),
                SignatureAlgorithm.HS256.getJcaName());

        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();

            log.debug("claims:{}", claims);

            String userId = (String) claims.get("userId");
            if(userId == null) {
                log.error("JWT userId 없음");
                return Mono.error(new Exception("JWT userId 없음"));
            }

            ServerHttpRequest newRequest = request.mutate().header("X-USER-ID", userId).build();
            exchange.mutate().request(newRequest).build();

        } catch (ExpiredJwtException e) {
            log.warn("Expired jwt token", e);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED); //401
            return exchange.getResponse().setComplete();
        } catch (SignatureException e) {
            log.warn("Invalid jwt signature", e);
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN); //403
            return exchange.getResponse().setComplete();
        } catch (Exception e) {
            log.error("Invalid jwt token", e);
            exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR); //500
            return exchange.getResponse().setComplete();
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {

        return 0;
    }
}
