package com.qkrwjdgus.queue;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MessageListener {

    @JmsListener(destination = "${aws.sqs.message}")
    public void request(String message) {

        log.info(message);

    }

}
