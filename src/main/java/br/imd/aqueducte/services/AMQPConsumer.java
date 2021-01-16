package br.imd.aqueducte.services;

import org.springframework.amqp.core.Message;
import org.springframework.stereotype.Component;

@Component
public interface AMQPConsumer {
    void consumer(Message message);
}
