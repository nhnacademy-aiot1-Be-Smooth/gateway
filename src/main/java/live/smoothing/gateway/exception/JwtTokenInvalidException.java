package live.smoothing.gateway.exception;

import live.smoothing.common.exception.CommonException;
import org.springframework.http.HttpStatus;

public class JwtTokenInvalidException extends CommonException {
    public JwtTokenInvalidException(HttpStatus status, String errorMessage) {
        super(status, errorMessage);
    }
}
