package live.smoothing.gateway.exception;

import live.smoothing.common.exception.CommonException;
import org.springframework.http.HttpStatus;

public class InvalidJsonFormatException extends CommonException {
    public InvalidJsonFormatException(HttpStatus status, String errorMessage) {
        super(status, errorMessage);
    }
}
