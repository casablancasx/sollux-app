package br.gov.agu.nutec.solluxapp.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {


    @Value("${rabbitmq.exchange.audiencia-pendente}")
    private String exchangeAudienciaPendente;

    @Value("${rabbitmq.queue.sollux-ms-contestacao}")
    private String filaAudienciaPendenteMsConstestacao;

    @Value("${rabbitmq.queue.sollux-ms-pauta}")
    private String filaAudienciaPendenteMsPace;


    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory){
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public ApplicationListener<ApplicationReadyEvent> inicializarRabbitAdmin(RabbitAdmin rabbitAdmin){
        return event -> rabbitAdmin.initialize();
    }

    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }


    @Bean
    public FanoutExchange fanoutExchange(){
        return ExchangeBuilder.fanoutExchange(exchangeAudienciaPendente).build();
    }


    @Bean
    public Queue filaAudienciaPendenteMsContestacao(){
        return QueueBuilder.durable(filaAudienciaPendenteMsConstestacao).build();
    }

    @Bean
    public Queue filaAudienciaPendenteMsPauta(){
        return QueueBuilder.durable(filaAudienciaPendenteMsPace).build();
    }

    @Bean
    public Binding bindingFilaAudienciaPendenteMsContestacao(FanoutExchange fanoutExchange, Queue filaAudienciaPendenteMsContestacao){
        return BindingBuilder.bind(filaAudienciaPendenteMsContestacao).to(fanoutExchange);
    }

    @Bean
    public Binding bindingFilaAudienciaPendenteMsPace(FanoutExchange fanoutExchange, Queue filaAudienciaPendenteMsPauta){
        return BindingBuilder.bind(filaAudienciaPendenteMsPauta).to(fanoutExchange);
    }


}
