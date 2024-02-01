package cn.rayce.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "chatglm.sdk.config", ignoreInvalidFields = true)
public class ChatGLMSDKConfigProperties {
    /** 状态；open = 开启、close 关闭 */
    private boolean enabled;
    /** 转发地址 */
    private String apiHost;
    /** 可以申请 sk-*** */
    private String apiSecretKey;

}
