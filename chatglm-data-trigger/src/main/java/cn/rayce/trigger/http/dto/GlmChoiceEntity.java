package cn.rayce.trigger.http.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 */
@Data
public class GlmChoiceEntity {

    private Long index; // 结果下标
    @JsonProperty("finish_reason")
    private String finishReason; // 模型推理终止的原因。stop代表推理自然结束或触发停止词。/ tool_calls 代表模型命中函数。/ length代表到达 tokens 长度上限。
    private GlmMessageEntity delta; //模型增量返回的文本信息



}
