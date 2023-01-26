package antifraud.configuration;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.context.annotation.PropertySource;

import javax.annotation.PostConstruct;

@ConstructorBinding
@Value
@Slf4j
@ConfigurationProperties("transaction")
@PropertySource("classpath:transaction.properties")
public class TransactionProperties {
    int allowedAmount;
    int manualProcessingAmount;

    @PostConstruct
    void init(){
        log.info("transaction.allowed-amount: {}",allowedAmount);
        log.info("transaction.manual-processing-amount: {}",manualProcessingAmount);
    }
}
