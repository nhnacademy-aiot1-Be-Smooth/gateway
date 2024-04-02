package live.smoothing.gateway.exception;

import live.smoothing.common.exception.CommonException;
import org.springframework.http.HttpStatus;

/**
 * Jwt 토큰 Payload 내부에 'userId' 필드 값이 없을 경우 발생하는 예외
 *
 * @author 박영준
 */
public class NotFoundUserIdException extends CommonException {
    public NotFoundUserIdException(HttpStatus status, String errorMessage) {
        super(status, errorMessage);
    }
}
