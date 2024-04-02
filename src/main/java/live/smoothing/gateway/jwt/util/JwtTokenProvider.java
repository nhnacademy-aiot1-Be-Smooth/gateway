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


/**
 * Jwt 토큰과 관련된 유틸 클래스
 *
 * @author 박영준
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    private final ObjectMapper objectMapper;
    private final JwtParser jwtParser;

    /**
     * Jwt 토큰을 Base64로 decode 후 유저 아이디를 추출하는 메서드
     *
     * @param token 문자열로 되어 있는 Jwt 토큰
     * @return 사용자 ID
     * @throws JsonProcessingException Json 형식에 맞지 않으면 발생하는 예외
     */
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

    /**
     * Jwt 토큰의 유효성을 검증하는 메서드
     *
     * @param token 문자열로 되어 있는 Jwt 토큰
     * @return Jwt 검증 결과 상태
     */
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

