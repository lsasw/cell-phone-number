package com.example.cellphonenumber.demos.service;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class Consumer {

    @RabbitListener(queues = "my.existing.queue") // "my.existing.queue"是一个已声明的队列名
    public void receiveSimpleMessage(String message) {
        System.out.println("Received: " + message);
    }

    // 使用@QueueBinding动态声明和绑定
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "my.dynamic.queue", durable = "true"), // 声明一个持久化队列
            exchange = @Exchange(name = "my.topic.exchange", type = ExchangeTypes.TOPIC), // 声明一个Topic交换机
            key = "user.notice.#" // 定义路由键（对于Topic交换机，支持通配符）
    ))
    public void receiveMessageFromDynamicQueue(String message) {
        // 处理消息逻辑
    }


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue("direct.queue"),
            exchange = @Exchange(name = "direct.exchange", type = ExchangeTypes.DIRECT),
            key = "routing.key" // 必须完全匹配此key的消息才会路由到此队列
    ))
    public void handleDirectMessage(String msg) {

    }
    // 主题模式 (Topic)：支持通配符的灵活路由，实现一对多。
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue("topic.queue"),
            exchange = @Exchange(name = "topic.exchange", type = ExchangeTypes.TOPIC),
            key = "order.*.success" // 匹配如 "order.payment.success"，但不匹配 "order.payment"
    ))
    public void handleTopicMessage(String msg) {

    }

    //广播模式 (Fanout)：将消息广播到所有绑定的队列。
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue, // 不指定name，会创建一个匿名、排他、自动删除的队列
            exchange = @Exchange(name = "fanout.exchange", type = ExchangeTypes.FANOUT)
            // Fanout交换机忽略key属性
    ))
    public void handleFanoutMessage1(String msg) {

    }

    // 另一个监听方法绑定到同一个Fanout交换机
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue,
            exchange = @Exchange(name = "fanout.exchange", type = ExchangeTypes.FANOUT)
    ))
    public void handleFanoutMessage2(String msg) {

    }

    //工作队列模式 (Work Queue)：一个队列多个消费者，实现负载均衡。
    // 消费者1
    @RabbitListener(queues = "work.queue")
    public void workConsumer1(String message) throws InterruptedException {
        Thread.sleep(100);
        System.out.println("Consumer 1: " + message);
    }

    // 消费者2
    @RabbitListener(queues = "work.queue")
    public void workConsumer2(String message) throws InterruptedException {
        Thread.sleep(300);
        System.out.println("Consumer 2: " + message);
    }



    @RabbitListener(queues = "ack.queue")
    public void handleMessageWithAck(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        try {
            // ... 处理业务逻辑
            channel.basicAck(tag, false); // 手动确认，第二个参数false表示不批量确认
        } catch (Exception e) {
            // 处理失败，拒绝消息并可选择是否重新入队
            channel.basicNack(tag, false, true);
        }
    }
}