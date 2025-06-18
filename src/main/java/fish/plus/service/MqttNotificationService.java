package fish.plus.service;

import fish.plus.utils.MqttMessageSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * MQTT通知服务
 * 提供业务层面的MQTT消息发送功能
 */
@Slf4j
@Service
public class MqttNotificationService {

    /**
     * 发送用户消息
     * 
     * @param groupId  用户ID
     * @param message 消息内容
     */
    public void sendGroupMessage(Long groupId, String message) {
        String topic = "topic/" + groupId;
        MqttMessageSender.sendMessageToSubscribers(topic, message);
        log.info("用户消息已发送 - 用户ID: {}, 消息: {}", groupId, message);
    }

} 