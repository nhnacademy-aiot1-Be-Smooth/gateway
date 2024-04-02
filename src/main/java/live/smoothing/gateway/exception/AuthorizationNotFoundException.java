package live.smoothing.gateway.exception;

import live.smoothing.common.exception.CommonException;
import org.springframework.http.HttpStatus;

/**
 * Jwt Token을 담은 헤더를 찾지 못했을 경우 발생하는 예외
 *
 * @author 박영준
 */
public class AuthorizationNotFoundException extends CommonException {
    public AuthorizationNotFoundException(HttpStatus status, String errorMessage) {
        super(status, errorMessage);
    }
}