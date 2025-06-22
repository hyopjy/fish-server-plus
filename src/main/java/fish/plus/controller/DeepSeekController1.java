package fish.plus.controller;

import io.github.pigmesh.ai.deepseek.core.DeepSeekClient;
import io.github.pigmesh.ai.deepseek.core.chat.ChatCompletionModel;
import io.github.pigmesh.ai.deepseek.core.chat.ChatCompletionRequest;
import io.github.pigmesh.ai.deepseek.core.chat.ChatCompletionResponse;
import io.github.pigmesh.ai.deepseek.core.chat.ResponseFormatType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class DeepSeekController1 {
    @Autowired
    private DeepSeekClient deepSeekClient;

    @GetMapping(value = "/chat/advanced", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatCompletionResponse> chatAdvanced(String prompt) {
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                // 模型选择，支持 DEEPSEEK_CHAT、DEEPSEEK_REASONER 等
                .model(ChatCompletionModel.DEEPSEEK_REASONER)
                // 添加用户消息
                .addUserMessage(prompt)
                // 添加助手消息，用于多轮对话
                .addAssistantMessage("上轮结果")
                // 添加系统消息，用于设置角色和行为
                .addSystemMessage("你是一个专业的助手")
                // 设置最大生成 token 数，默认 2048
                .maxTokens(1000)
                // 设置响应格式，支持 JSON 结构化输出
                .responseFormat(ResponseFormatType.JSON_OBJECT) // 可选
                // function calling
                // .tools(...) // 可选
                .build();

        return deepSeekClient.chatFluxCompletion(request);
    }

    @GetMapping(value = "/sync/chat")
    public ChatCompletionResponse syncChat(String prompt, String model) {
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                // 根据渠道模型名称动态修改这个参数
                .model(model)
                .addUserMessage(prompt).build();

        return deepSeekClient.chatCompletion(request).execute();
    }
}
