package fish.plus.handler;

import fish.plus.utils.MqttMessageSender;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.mqtt.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
@ChannelHandler.Sharable
public class MqttServerHandler extends SimpleChannelInboundHandler<MqttMessage> {

    // 记录每个主题的订阅者
//    private final Map<String, Set<ChannelHandlerContext>> topicSubscribers = new HashMap<>();

    private MqttMessageSender mqttMessageSender;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MqttMessage msg) {
        if (msg instanceof MqttConnectMessage) {
            handleConnect(ctx, (MqttConnectMessage) msg);
        } else if (msg instanceof MqttPublishMessage) {
            handlePublish(ctx, (MqttPublishMessage) msg);
        } else if (msg instanceof MqttSubscribeMessage) {
            handleSubscribe(ctx, (MqttSubscribeMessage) msg);
        } else if (msg instanceof MqttUnsubscribeMessage) {
            handleUnsubscribe(ctx, (MqttUnsubscribeMessage) msg);
        }
    }

    private void handleConnect(ChannelHandlerContext ctx, MqttConnectMessage msg) {
        MqttFixedHeader header = new MqttFixedHeader(
                MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0);
        MqttConnAckVariableHeader ackHeader = new MqttConnAckVariableHeader(
                MqttConnectReturnCode.CONNECTION_ACCEPTED, true);
        ctx.writeAndFlush(new MqttConnAckMessage(header, ackHeader));
    }

    private void handlePublish(ChannelHandlerContext ctx, MqttPublishMessage msg) {
        String topic = msg.variableHeader().topicName();
        ByteBuf payload = msg.payload();
        log.info("收到主题[{}]数据: {}", topic, payload.toString(StandardCharsets.UTF_8));

        // 广播消息给所有订阅了该主题的客户端
        broadcastMessage(topic, payload);
    }

    private void handleSubscribe(ChannelHandlerContext ctx, MqttSubscribeMessage msg) {
        MqttFixedHeader fixedHeader = new MqttFixedHeader(MqttMessageType.SUBACK, false, MqttQoS.AT_MOST_ONCE, false, 0);

        // 使用 from 方法创建 MqttMessageIdVariableHeader 实例
        MqttMessageIdVariableHeader variableHeader = MqttMessageIdVariableHeader.from(msg.variableHeader().messageId());

        MqttSubAckMessage subAckMessage = new MqttSubAckMessage(fixedHeader, variableHeader, new MqttSubAckPayload(new int[]{MqttQoS.AT_MOST_ONCE.value()}));
        ctx.writeAndFlush(subAckMessage);

        // 记录订阅关系
        for (MqttTopicSubscription subscription : msg.payload().topicSubscriptions()) {
            String topic = subscription.topicName();
            MqttMessageSender.addSubscriber(topic, ctx);
            log.info("客户端已订阅主题: {}", topic);
        }
    }

    private void handleUnsubscribe(ChannelHandlerContext ctx, MqttUnsubscribeMessage msg) {
        MqttFixedHeader fixedHeader = new MqttFixedHeader(MqttMessageType.UNSUBACK, false, MqttQoS.AT_MOST_ONCE, false, 0);

        // 使用 from 方法创建 MqttMessageIdVariableHeader 实例
        MqttMessageIdVariableHeader variableHeader = MqttMessageIdVariableHeader.from(msg.variableHeader().messageId());

        MqttUnsubAckMessage unsubAckMessage = new MqttUnsubAckMessage(fixedHeader, variableHeader);
        ctx.writeAndFlush(unsubAckMessage);

        // 移除订阅关系
        for (String topic : msg.payload().topics()) {
            MqttMessageSender.removeSubscriber(topic, ctx);
//            topicSubscribers.computeIfPresent(topic, (k, contexts) -> {
//                contexts.remove(ctx);
//                log.info("客户端已取消订阅主题: {}", topic);
//                return contexts.isEmpty() ? null : contexts;
//            });
        }
    }

    private void broadcastMessage(String topic, ByteBuf payload) {
        Set<ChannelHandlerContext> subscribers = MqttMessageSender.getTopic(topic);
        if (subscribers != null) {
            MqttFixedHeader fixedHeader = new MqttFixedHeader(MqttMessageType.PUBLISH, false, MqttQoS.AT_MOST_ONCE, false, payload.readableBytes());
            MqttPublishMessage publishMessage = new MqttPublishMessage(fixedHeader, new MqttPublishVariableHeader(topic, 0), payload.copy());

            for (ChannelHandlerContext subscriberCtx : subscribers) {
                subscriberCtx.writeAndFlush(publishMessage.retain());
            }
        }
    }
}