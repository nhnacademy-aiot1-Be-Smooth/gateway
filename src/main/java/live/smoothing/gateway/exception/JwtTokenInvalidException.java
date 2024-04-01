package live.smoothing.gateway.exception;

import live.smoothing.common.exception.CommonException;
import org.springframework.http.HttpStatus;

/**
 * Jwt 토큰의 Signature 값이 틀릴 경우 발생하는 예외
 *
 * @author 박영준
 */
public class JwtTokenInvalidException extends CommonException {
    public JwtTokenInvalidException(HttpStatus status, String errorMessage) {
        super(status, errorMessage);
    }
}
