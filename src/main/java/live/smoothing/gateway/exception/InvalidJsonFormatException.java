package live.smoothing.gateway.exception;

import live.smoothing.common.exception.CommonException;
import org.springframework.http.HttpStatus;

/**
 * Jwt 토큰을 Json으로 변환하는 과정 중 발생하는 예외
 *
 * @author 박영준
 */
public class InvalidJsonFormatException extends CommonException {
    public InvalidJsonFormatException(HttpStatus status, String errorMessage) {
        super(status, errorMessage);
    }
}
