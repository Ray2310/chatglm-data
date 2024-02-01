package cn.rayce.config;

import cn.bugstack.chatglm.session.OpenAiSession;
import cn.bugstack.chatglm.session.OpenAiSessionFactory;
import cn.bugstack.chatglm.session.defaults.DefaultOpenAiSessionFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
@EnableConfigurationProperties(ChatGLMSDKConfigProperties.class)
public class ChatGLMSDKConfig {

    @Bean
    public OpenAiSession openAiSession(ChatGLMSDKConfigProperties configProperties){
        cn.bugstack.chatglm.session.Configuration configuration = new cn.bugstack.chatglm.session.Configuration();
        configuration.setApiHost(configProperties.getApiHost());
        configuration.setApiSecretKey(configProperties.getApiSecretKey());
        OpenAiSessionFactory factory = new DefaultOpenAiSessionFactory(configuration);
        return factory.openSession();
    }
}
