package live.smoothing.gateway.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

/**
 * Global Filter 들의 공통 속성 클래스
 *
 * @author 박영준
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties("global.filter")
public class GlobalFilterProperties {

    private Set<String> excludePath;
}
