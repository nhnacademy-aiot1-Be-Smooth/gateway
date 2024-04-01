package live.smoothing.gateway.jwt.util;

/**
 * Jwt 검증 후 결과 상태를 나타내는 클래스
 *
 * @author 박영준
 */
public enum JwtCode {
    INVALID,
    EXPIRED,
    ACCESS
}
