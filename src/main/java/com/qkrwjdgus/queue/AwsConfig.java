package com.qkrwjdgus.queue;

import com.amazon.sqs.javamessaging.ProviderConfiguration;
import com.amazon.sqs.javamessaging.SQSConnection;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.support.destination.DynamicDestinationResolver;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Session;

@Configuration
@EnableJms
public class AwsConfig {

    @Value("${aws.sqs.access-key}")
    private String awsSqsAccessKey;

    @Value("${aws.sqs.secret-key}")
    private String awsSqsSecretKey;

    @Value("${aws.sqs.regions}")
    private String awsSqsRegions;

    @Bean
    public BasicAWSCredentials basicAWSCredentials() {
        return new BasicAWSCredentials(awsSqsAccessKey, awsSqsSecretKey);
    }

    @Bean
    public AmazonSQS amazonSQS() {
        return AmazonSQSClientBuilder.standard().withRegion(awsSqsRegions).withCredentials(new AWSStaticCredentialsProvider(basicAWSCredentials()))
                .build();
    }

    @Bean
    public SQSConnection sqsConnection(@Qualifier("amazonSQS") AmazonSQS amazonSQS) throws JMSException {
        return new SQSConnectionFactory(new ProviderConfiguration(), amazonSQS).createConnection();
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        return new SQSConnectionFactory(new ProviderConfiguration(), amazonSQS());
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();

        factory.setConnectionFactory(connectionFactory());
        factory.setDestinationResolver(new DynamicDestinationResolver());
        factory.setConcurrency("3-10");
        factory.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
        return factory;
    }

}
