package fish.plus.controller;

import fish.plus.handler.MqttServerHandler;
import fish.plus.data.vo.Result;
import fish.plus.utils.MqttMessageSender;
import io.netty.handler.codec.mqtt.MqttQoS;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * MQTT消息发送控制器
 * 提供主动向订阅客户端发送消息的接口
 */
@Slf4j
@RestController
@RequestMapping("/mqtt")
public class MqttController {

    /**
     * 向指定主题发送消息
     * 
     * @param topic   主题名称
     * @param message 消息内容
     * @return 发送结果
     */
    @PostMapping("/send")
    public Result<String> sendMessage(@RequestParam String topic, @RequestParam String message) {
        try {
            // 检查是否有订阅者
            if (!MqttMessageSender.hasSubscribers(topic)) {
                return Result.fail(400, "主题 " + topic + " 没有订阅者");
            }

            // 发送消息
            MqttMessageSender.sendMessageToSubscribers(topic, message);
            
            log.info("成功向主题 {} 发送消息: {}", topic, message);
            return Result.ok("消息发送成功");
        } catch (Exception e) {
            log.error("发送MQTT消息失败", e);
            return Result.fail(500, "消息发送失败: " + e.getMessage());
        }
    }

    /**
     * 向指定主题发送消息（支持自定义QoS）
     * 
     * @param topic   主题名称
     * @param message 消息内容
     * @param qos     服务质量级别 (0, 1, 2)
     * @return 发送结果
     */
    @PostMapping("/send-with-qos")
    public Result<String> sendMessageWithQos(@RequestParam String topic, 
                                           @RequestParam String message, 
                                           @RequestParam(defaultValue = "0") int qos) {
        try {
            // 检查是否有订阅者
            if (!MqttMessageSender.hasSubscribers(topic)) {
                return Result.fail(400, "主题 " + topic + " 没有订阅者");
            }

            // 根据qos值获取对应的MqttQoS枚举
            MqttQoS mqttQoS;
            switch (qos) {
                case 0:
                    mqttQoS = MqttQoS.AT_MOST_ONCE;
                    break;
                case 1:
                    mqttQoS = MqttQoS.AT_LEAST_ONCE;
                    break;
                case 2:
                    mqttQoS = MqttQoS.EXACTLY_ONCE;
                    break;
                default:
                    return Result.fail(400, "无效的QoS值，必须是0、1或2");
            }

            // 发送消息
            MqttMessageSender.sendMessageToSubscribers(topic, message, mqttQoS);
            
            log.info("成功向主题 {} 发送消息: {} (QoS: {})", topic, message, qos);
            return Result.ok("消息发送成功");
        } catch (Exception e) {
            log.error("发送MQTT消息失败", e);
            return Result.fail(500, "消息发送失败: " + e.getMessage());
        }
    }

    /**
     * 获取指定主题的订阅者数量
     * 
     * @param topic 主题名称
     * @return 订阅者数量
     */
    @GetMapping("/subscribers/count")
    public Result<Integer> getSubscriberCount(@RequestParam String topic) {
        try {
            int count = MqttMessageSender.getSubscriberCount(topic);
            return Result.ok(count);
        } catch (Exception e) {
            log.error("获取订阅者数量失败", e);
            return Result.fail(500, "获取订阅者数量失败: " + e.getMessage());
        }
    }

    /**
     * 检查指定主题是否有订阅者
     * 
     * @param topic 主题名称
     * @return 是否有订阅者
     */
    @GetMapping("/subscribers/exists")
    public Result<Boolean> hasSubscribers(@RequestParam String topic) {
        try {
            boolean hasSubscribers = MqttMessageSender.hasSubscribers(topic);
            return Result.ok(hasSubscribers);
        } catch (Exception e) {
            log.error("检查订阅者失败", e);
            return Result.fail(500, "检查订阅者失败: " + e.getMessage());
        }
    }
} 