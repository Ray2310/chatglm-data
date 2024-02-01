package cn.rayce.domain.chatglm.model.aggregates;

import cn.rayce.domain.chatglm.model.entity.GlmMessageEntity;
import cn.rayce.types.enums.ChatGLMModel;
import cn.rayce.types.enums.ChatGPTModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatGlmProcessAggregate {

    /** 默认模型 */
    private String model = ChatGLMModel.GLM_3_5_TURBO.getCode();
    /** 问题描述 */
    private List<GlmMessageEntity> messages;



}
