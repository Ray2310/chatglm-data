package cn.rayce.types.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description 模型对象
 */
@Getter
@AllArgsConstructor
public enum ChatGPTModel {

    /** gpt-3.5-turbo */
    GPT_3_5_TURBO("gpt-3.5-turbo"),

    ;
    private final String code;

}
