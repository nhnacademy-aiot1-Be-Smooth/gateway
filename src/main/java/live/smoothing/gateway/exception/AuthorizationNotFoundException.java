package live.smoothing.gateway.exception;

import live.smoothing.common.exception.CommonException;
import org.springframework.http.HttpStatus;

public class AuthorizationNotFoundException extends CommonException {
    public AuthorizationNotFoundException(HttpStatus status, String errorMessage) {
        super(status, errorMessage);
    }
}

