package cn.rayce.trigger.http.dto;

import cn.rayce.types.enums.ChatGLMModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatGLMRequestDTO {

    /** 默认模型 */
    private String model = ChatGLMModel.GLM_3_5_TURBO.getCode();

    /** 问题描述 */
    private List<GlmMessageEntity> messages;

}
