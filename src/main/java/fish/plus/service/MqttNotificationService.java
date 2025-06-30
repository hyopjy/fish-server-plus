package fish.plus.service;

import com.alibaba.fastjson.JSONObject;
import fish.plus.data.dto.MessageContentDTO;
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
     */
    public void sendGroupRodeoMessage(Long groupId) {
        String topic = "topic/" + groupId;
        MessageContentDTO dto = new MessageContentDTO();
        dto.setMessageType("RODEO_INIT");
        dto.setGroupId(groupId);
        MqttMessageSender.sendMessageToSubscribers(topic, JSONObject.toJSONString(dto));
        log.info("用户消息已发送 - 用户ID: {}, 消息: {}", groupId, JSONObject.toJSONString(dto));
    }

} 