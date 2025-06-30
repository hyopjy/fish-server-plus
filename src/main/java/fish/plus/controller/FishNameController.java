package fish.plus.controller;

import fish.plus.data.dto.FishInfo;
import fish.plus.data.vo.Result;
import io.github.pigmesh.ai.deepseek.core.DeepSeekClient;
import io.github.pigmesh.ai.deepseek.core.chat.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;

@RestController
public class FishNameController {

    // 优化后的提示词模板 [3][6]
    private static final String PROMPT_TEMPLATE =
            "作为渔获命名专家，生成8个虚构渔获名称及其故事性描述。要求：\n" +
                    "1. 名称要求（海洋/陆地各占50%）：\n" +
                    "   - 中文名2-4字，兼具文化底蕴和趣味性（参考《山海经》风格）\n" +
                    "   - 示例：幻光锦鲤、冰原剑齿鳕\n" +
                    "2. 描述要求（含三个要素）：\n" +
                    "   - 栖息环境特征（如：'栖于北极冰层下的发光洞穴'）\n" +
                    "   - 特殊习性（如：'月圆之夜会跃出水面吸收月光'）\n" +
                    "   - 传奇故事（如：'渔民视其为风暴预警使者'）\n" +
                    "3. 直接输出标准JSON数组格式：[{\"fishName\":\"\",\"fishDesc\":\"\"}]\n" +
                    "4. 禁止解释性文字，仅返回JSON";

    @Autowired
    private DeepSeekClient deepSeekClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // 渔获生成专用接口
    @GetMapping("/generate/fish-names")
    @ResponseBody
    public Result<List<FishInfo>> generateFishNames() {
        try {
            ChatCompletionRequest request = ChatCompletionRequest.builder()
                    .model(ChatCompletionModel.DEEPSEEK_REASONER)
                    .addSystemMessage("你是精通生物命名的神秘学家，擅长创作融合自然与奇幻元素的渔获故事") // 角色强化 [3]
                    .addUserMessage(PROMPT_TEMPLATE)
                    .responseFormat(ResponseFormatType.JSON_OBJECT) // 强制JSON输出 [1][7]
                    .temperature(0.85)  // 提高创造性 [6]
                    .maxTokens(1200)     // 保证完整输出
                    .build();

            ChatCompletionResponse response = deepSeekClient.chatCompletion(request).execute();
            String jsonOutput = extractJsonString(response);

            // JSON解析与验证
            List<FishInfo> fishList = objectMapper.readValue(
                    jsonOutput,
                    new TypeReference<List<FishInfo>>(){}
            );

            return Result.ok(fishList);

        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail(500, "");
        }
    }

    // 提取纯净JSON（核心改进）
    private String extractJsonString(ChatCompletionResponse response) {
        String rawContent = response.choices().get(0).message().content();

        // 双重过滤确保获得有效JSON
        int jsonStart = rawContent.indexOf("[");
        int jsonEnd = rawContent.lastIndexOf("]") + 1;

        return (jsonStart != -1 && jsonEnd != -1) ?
                rawContent.substring(jsonStart, jsonEnd) :
                rawContent;
    }


}