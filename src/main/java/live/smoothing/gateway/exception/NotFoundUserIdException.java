package live.smoothing.gateway.exception;

import live.smoothing.common.exception.CommonException;
import org.springframework.http.HttpStatus;

public class NotFoundUserIdException extends CommonException {
    public NotFoundUserIdException(HttpStatus status, String errorMessage) {
        super(status, errorMessage);
    }
}
