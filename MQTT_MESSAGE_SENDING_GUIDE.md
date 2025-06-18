# MQTT 消息发送功能使用指南

## 概述

本项目提供了完整的 MQTT 服务器功能，支持客户端订阅主题和服务器主动向订阅客户端发送消息。

## 核心功能

### 1. MqttServerHandler 类

`MqttServerHandler` 类处理客户端的 MQTT 连接、订阅、发布等操作，并提供了主动发送消息的静态方法：

#### 主要方法

```java
// 向订阅指定主题的客户端发送消息（默认QoS 0）
public static void sendMessageToSubscribers(String topic, String message)

// 向订阅指定主题的客户端发送消息（自定义QoS）
public static void sendMessageToSubscribers(String topic, String message, MqttQoS qos)

// 获取指定主题的订阅者数量
public static int getSubscriberCount(String topic)

// 检查指定主题是否有订阅者
public static boolean hasSubscribers(String topic)
```

### 2. MqttMessageSender 工具类

`MqttMessageSender` 是底层的消息发送工具类，负责实际的消息发送逻辑：

```java
// 发送消息到指定主题
public static void sendMessage(String topic, String payload, MqttQoS qos)

// 添加订阅者
public static void addSubscriber(String topic, ChannelHandlerContext context)

// 移除订阅者
public static void removeSubscriber(String topic, ChannelHandlerContext context)
```

## 使用方式

### 1. 直接调用静态方法

```java
// 发送简单消息
MqttServerHandler.sendMessageToSubscribers("test/topic", "Hello World!");

// 发送高优先级消息
MqttServerHandler.sendMessageToSubscribers("urgent/alert", "紧急通知", MqttQoS.EXACTLY_ONCE);

// 检查订阅者
if (MqttServerHandler.hasSubscribers("test/topic")) {
    int count = MqttServerHandler.getSubscriberCount("test/topic");
    System.out.println("主题有 " + count + " 个订阅者");
}
```

### 2. 通过 REST API 接口

#### 发送消息
```bash
# 发送简单消息
POST /mqtt/send?topic=test/topic&message=Hello World

# 发送带QoS的消息
POST /mqtt/send-with-qos?topic=test/topic&message=Hello World&qos=1
```

#### 查询订阅者信息
```bash
# 获取订阅者数量
GET /mqtt/subscribers/count?topic=test/topic

# 检查是否有订阅者
GET /mqtt/subscribers/exists?topic=test/topic
```

### 3. 通过服务类使用

```java
@Autowired
private MqttNotificationService mqttNotificationService;

// 发送系统通知
mqttNotificationService.sendSystemNotification("系统维护通知");

// 发送用户消息
mqttNotificationService.sendUserMessage("user123", "您有新的消息");

// 发送群组消息
mqttNotificationService.sendGroupMessage("group456", "群组公告");

// 发送设备状态更新
mqttNotificationService.sendDeviceStatusUpdate("device001", "online");
```

## 主题命名规范

建议使用以下主题命名规范：

- `system/notification` - 系统通知
- `user/{userId}/message` - 用户消息
- `group/{groupId}/message` - 群组消息
- `device/{deviceId}/status` - 设备状态
- `sensor/{sensorId}/data` - 传感器数据

## QoS 级别说明

- **QoS 0 (AT_MOST_ONCE)**: 最多一次，不保证消息到达
- **QoS 1 (AT_LEAST_ONCE)**: 至少一次，保证消息到达，可能重复
- **QoS 2 (EXACTLY_ONCE)**: 恰好一次，保证消息只到达一次

## 注意事项

1. **线程安全**: 所有方法都是线程安全的，可以在多线程环境中使用
2. **连接管理**: 系统会自动管理客户端连接，断开连接的客户端会自动从订阅列表中移除
3. **内存管理**: 使用 Netty 的 ByteBuf 进行内存管理，会自动释放资源
4. **错误处理**: 所有方法都包含适当的错误处理和日志记录

## 示例场景

### 场景1: 实时通知系统
```java
@Service
public class NotificationService {
    
    public void sendUserNotification(String userId, String message) {
        String topic = "user/" + userId + "/notification";
        MqttServerHandler.sendMessageToSubscribers(topic, message);
    }
    
    public void sendBroadcastNotification(String message) {
        MqttServerHandler.sendMessageToSubscribers("broadcast/notification", message);
    }
}
```

### 场景2: 设备监控系统
```java
@Service
public class DeviceMonitorService {
    
    public void updateDeviceStatus(String deviceId, String status) {
        String topic = "device/" + deviceId + "/status";
        String message = String.format("{\"deviceId\":\"%s\",\"status\":\"%s\",\"timestamp\":%d}", 
                                     deviceId, status, System.currentTimeMillis());
        MqttServerHandler.sendMessageToSubscribers(topic, message);
    }
}
```

### 场景3: 聊天系统
```java
@Service
public class ChatService {
    
    public void sendPrivateMessage(String fromUserId, String toUserId, String message) {
        String topic = "user/" + toUserId + "/private";
        String chatMessage = String.format("{\"from\":\"%s\",\"message\":\"%s\",\"timestamp\":%d}", 
                                         fromUserId, message, System.currentTimeMillis());
        MqttServerHandler.sendMessageToSubscribers(topic, chatMessage);
    }
    
    public void sendGroupMessage(String groupId, String fromUserId, String message) {
        String topic = "group/" + groupId + "/chat";
        String chatMessage = String.format("{\"from\":\"%s\",\"message\":\"%s\",\"timestamp\":%d}", 
                                         fromUserId, message, System.currentTimeMillis());
        MqttServerHandler.sendMessageToSubscribers(topic, chatMessage);
    }
}
```

## 故障排除

1. **消息发送失败**: 检查主题是否有订阅者
2. **客户端收不到消息**: 确认客户端已正确订阅主题
3. **连接断开**: 检查网络连接和客户端状态
4. **内存泄漏**: 确保正确释放 ByteBuf 资源

通过以上功能，您可以轻松实现服务器主动向 MQTT 客户端发送消息的需求。 