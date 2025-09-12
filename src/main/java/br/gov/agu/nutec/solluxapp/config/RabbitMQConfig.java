package br.gov.agu.nutec.solluxapp.config;

import org.springframework.amqp.core.FanoutExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {




@Bean
    public FanoutExchange exchange() {
        return new FanoutExchange("planilhaExchange");
}



}
