package br.gov.agu.nutec.solluxapp.producer;

import br.gov.agu.nutec.solluxapp.dto.AudienciaDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AudienciaProducer {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.audiencia-pendente}")
    private String exchangeAudienciaPendente;

    public void enviarAudiencia(AudienciaDTO audienciaDTO){
        rabbitTemplate.convertAndSend(exchangeAudienciaPendente,"",audienciaDTO);
    }
}
