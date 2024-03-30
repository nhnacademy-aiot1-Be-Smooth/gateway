package live.smoothing.gateway.jwt.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.security.SignatureException;
import live.smoothing.gateway.exception.NotFoundUserIdException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Objects;


@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    private final ObjectMapper objectMapper;
    private final JwtParser jwtParser;

    public String getUserId(String token) throws JsonProcessingException {
        String[] chunks = token.split("\\.");
        String payload = new String(Base64.getUrlDecoder().decode(chunks[1]));
        JsonNode id = objectMapper.readTree(payload).findValue("id");

        // TODO : exception 처리
        if (Objects.isNull(id)) {
            throw new NotFoundUserIdException(HttpStatus.BAD_REQUEST, "Jwt 토큰 내에서 userId를 찾지 못했습니다.");
        }

        return id.asText();
    }

    public JwtCode validateToken(String token) {
        try {
            jwtParser.parseClaimsJws(token);
            return JwtCode.ACCESS;
        } catch (ExpiredJwtException e) {
            return JwtCode.EXPIRED;
        } catch (SignatureException e) {
            return JwtCode.INVALID;
        }
    }

}

