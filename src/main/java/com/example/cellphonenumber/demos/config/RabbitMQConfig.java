package com.example.cellphonenumber.demos.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    // 交换机名称
    public static final String DIRECT_EXCHANGE_NAME = "direct_exchange";
    public static final String TOPIC_EXCHANGE_NAME = "topic_exchange";
    public static final String FANOUT_EXCHANGE_NAME = "fanout_exchange";

    // 队列名称
    public static final String DIRECT_QUEUE_NAME = "direct_queue";
    public static final String TOPIC_QUEUE_NAME = "topic_queue";
    public static final String FANOUT_QUEUE_NAME = "fanout_queue";

    // 路由键
    public static final String DIRECT_ROUTING_KEY = "direct_routing_key";
    public static final String TOPIC_ROUTING_KEY = "topic_routing_key";

    // 交换机
    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(DIRECT_EXCHANGE_NAME);
    }

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(TOPIC_EXCHANGE_NAME);
    }

    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(FANOUT_EXCHANGE_NAME);
    }

    // 队列
    @Bean
    public Queue directQueue() {
        return new Queue(DIRECT_QUEUE_NAME);
    }

    @Bean
    public Queue topicQueue() {
        return new Queue(TOPIC_QUEUE_NAME);
    }

    @Bean
    public Queue fanoutQueue() {
        return new Queue(FANOUT_QUEUE_NAME);
    }

    // 绑定
    @Bean
    public Binding directBinding() {
        return BindingBuilder.bind(directQueue()).to(directExchange()).with(DIRECT_ROUTING_KEY);
    }

    @Bean
    public Binding topicBinding() {
        return BindingBuilder.bind(topicQueue()).to(topicExchange()).with(TOPIC_ROUTING_KEY);
    }

    @Bean
    public Binding fanoutBinding() {
        return BindingBuilder.bind(fanoutQueue()).to(fanoutExchange());
    }

    public static final String USER_REGISTRATION_EXCHANGE = "user.registration.exchange";
    public static final String USER_NOTIFICATION_QUEUE = "user.notification.queue";
    public static final String USER_ROUTING_KEY = "user.registered";

    // Dead Letter Queue (DLQ) 相关配置
    public static final String DLQ_QUEUE = "user.dlq.queue";
    public static final String DLX_EXCHANGE = "dlx.exchange";
    public static final String DLQ_ROUTING_KEY = "dlq.user";

    // ========== 主交换机与队列 ==========
    /**
     * 创建用户注册交换机
     * 该交换机用于路由用户注册相关的消息，使用DirectExchange类型
     * DirectExchange会将消息精确路由到绑定键与路由键完全匹配的队列
     * 
     * @return 配置好的DirectExchange对象
     */
    @Bean
    public DirectExchange userRegistrationExchange() {
        // 返回了一个名为"user.registration.exchange"的DirectExchange对象，并设置为持久化，并且设置为自动创建
        return new DirectExchange(USER_REGISTRATION_EXCHANGE, true, false);
    }

    /**
     * 创建用户通知队列
     * 该队列用于接收用户注册成功的消息，并配置了死信交换机和路由键
     * 当消息在该队列中变为死信时，会被转发到死信交换机
     * 
     * @return 配置好的持久化队列对象
     */
    @Bean
    public Queue userNotificationQueue() {
        return QueueBuilder.durable(USER_NOTIFICATION_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    public Binding bindingUserNotification() {
        return BindingBuilder.bind(userNotificationQueue())
                             .to(userRegistrationExchange())
                             .with(USER_ROUTING_KEY);
    }

    // ========== 死信交换机与队列 ==========
    @Bean
    public TopicExchange dlxExchange() {
        return new TopicExchange(DLX_EXCHANGE, true, false);
    }

    @Bean
    public Queue dlqQueue() {
        return QueueBuilder.durable(DLQ_QUEUE).build();
    }

    @Bean
    public Binding dlqBinding() {
        return BindingBuilder.bind(dlqQueue()).to(dlxExchange()).with("#"); // 匹配所有 routing key
    }

    // ========== 消息序列化器 ==========
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }


    @Bean
    public RabbitListenerContainerFactory<?> rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        // 设置最大并发数
        factory.setConcurrentConsumers(5);
        factory.setMaxConcurrentConsumers(10);
        // 设置每次消费的消息数量
        factory.setPrefetchCount(10);
        return factory;
    }
}