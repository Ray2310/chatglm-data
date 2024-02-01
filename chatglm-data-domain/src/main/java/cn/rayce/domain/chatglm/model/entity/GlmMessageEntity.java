package cn.rayce.domain.chatglm.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GlmMessageEntity {

    private String role;
    private String content;

}
