package live.smoothing.gateway.config;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import live.smoothing.gateway.jwt.prop.JwtProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class JwtConfig {

    private final JwtProperties jwtProperties;

    @Bean
    public JwtParser jwtParser() {
        return Jwts.parserBuilder()
                .setSigningKey(jwtProperties.getSecret().getBytes())
                .build();
    }
}
