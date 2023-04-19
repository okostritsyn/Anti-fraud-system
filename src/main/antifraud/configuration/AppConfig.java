package antifraud.configuration;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@PropertySource("classpath:transaction.properties")
@ConfigurationPropertiesScan
@Configuration
public class AppConfig {
}
