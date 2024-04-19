package live.smoothing.gateway.config;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import live.smoothing.gateway.jwt.prop.JwtProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

/**
 * Jwt Token 관련 설정 클래스
 *
 * @author 박영준
 */
@Configuration
@RequiredArgsConstructor
public class JwtConfig {

    private final JwtProperties jwtProperties;

    /**
     * Jwt Token Parser 객체 생성 메서드
     *
     * @return Jwt Token Parser 객체
     */
    @Bean
    public JwtParser jwtParser() {
        Key key = new SecretKeySpec(jwtProperties.getSecret().getBytes(), SignatureAlgorithm.HS256.getJcaName());
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build();
    }
}
