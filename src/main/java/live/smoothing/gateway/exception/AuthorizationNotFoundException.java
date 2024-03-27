package live.smoothing.gateway.exception;

public class AuthorizationNotFoundException extends RuntimeException {

    public AuthorizationNotFoundException(String message) {

        super(message + ": Authorization not found in header");
    }
}

