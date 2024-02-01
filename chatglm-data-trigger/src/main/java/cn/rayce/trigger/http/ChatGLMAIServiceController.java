package cn.rayce.trigger.http;

import cn.bugstack.chatglm.model.ChatCompletionRequest;
import cn.bugstack.chatglm.model.ChatCompletionResponse;
import cn.bugstack.chatglm.model.Model;
import cn.bugstack.chatglm.model.Role;
import cn.bugstack.chatglm.session.Configuration;
import cn.bugstack.chatglm.session.OpenAiSession;
import cn.bugstack.chatglm.utils.BearerTokenUtils;
import cn.rayce.trigger.http.dto.ChatGLMRequestDTO;
import cn.rayce.types.exception.ChatGPTException;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Slf4j
@RestController()
@CrossOrigin("*")
@RequestMapping("/api/paas/v4/")
public class ChatGLMAIServiceController {

    @Autowired(required = false)
    private OpenAiSession openGlmAiSession;

    @Resource
    private ThreadPoolExecutor threadPoolExecutor;
    /*
    curl -X POST \
        http://localhost:8090/api/paas/v4/chat/completions \
        -H "Content-Type: application/json" \
        -d '{
          "model":"glm-3-turbo",
          "stream": "true",
          "messages": [
              {
                  "role": "user",
                  "content": "写个java冒泡排序"
              }
          ]
        }'

curl -X POST \
        -H "Authorization: Bearer 9a69187ff54806e6294ec93bb453f54b.NfL5iqpVNXnj2g7P" \
        -H "Content-Type: application/json" \
        -H "User-Agent: Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)" \
        -H "Accept: text/event-stream" \
        -d '{
        "top_p": 0.7,
        "sseFormat": "data",
        "temperature": 0.9,
        "incremental": true,
        "request_id": "xfg-1696992276607",
        "prompt": [
        {
        "role": "user",
        "content": "写个java冒泡排序"
        }
        ]
        }' \
  http://open.bigmodel.cn/api/paas/v4/chat/completions

curl -X POST \
        -H "Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiIsInNpZ25fdHlwZSI6IlNJR04ifQ.eyJhcGlfa2V5IjoiOWE2OTE4N2ZmNTQ4MDZlNjI5NGVjOTNiYjQ1M2Y1NGIiLCJleHAiOjE3MDY3NzAxMjMwODQsInRpbWVzdGFtcCI6MTcwNjc2ODMyMzA4NX0.edKgbou_FscD1u61HiY3tUWgyA6_32kjJFFaYg4ciKQ" \
        -H "Content-Type: application/json" \
        -H "User-Agent: Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)" \
        -H "Accept: text/event-stream" \
        -d '{
        "top_p": 0.7,
        "sseFormat": "data",
        "temperature": 0.9,
        "incremental": true,
        "request_id": "xfg-1696992276607",
        "prompt": [
        {
        "role": "user",
        "content": "写个java冒泡排序"
        }
        ]
        }' \
  http://open.bigmodel.cn/api/paas/v3/model-api/chatglm_lite/sse-invoke



        curl -X POST \
        http://localhost:8090/api/paas/v4/chat/completions \
        -H 'Content-Type: application/json;charset=utf-8' \
        -H 'Authorization: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiIsInNpZ25fdHlwZSI6IlNJR04ifQ.eyJhcGlfa2V5IjoiOWE2OTE4N2ZmNTQ4MDZlNjI5NGVjOTNiYjQ1M2Y1NGIiLCJleHAiOjE3MDY3NzAxMjMwODQsInRpbWVzdGFtcCI6MTcwNjc2ODMyMzA4NX0.edKgbou_FscD1u61HiY3tUWgyA6_32kjJFFaYg4ciKQ' \
        -d '{
        "messages": [
        {
        "content": "写一个java冒泡排序",
        "role": "user"
        }
        ],
        "model": "glm-3-turbo"
        }'
     */


    // 需要先运行， 然后将token赋值给脚本的token
    public static void main(String[] args) {
        // 1. 配置文件
        Configuration configuration = new Configuration();
        configuration.setApiHost("https://open.bigmodel.cn/");
        configuration.setApiSecretKey("9a69187ff54806e6294ec93bb453f54b.NfL5iqpVNXnj2g7P");
        // 2. 获取Token
        String token = BearerTokenUtils.getToken(configuration.getApiKey(), configuration.getApiSecret());
        log.info("2. 运行 test_curl 获取 token：{}", token);
    }


    @RequestMapping(value = "chat/completions", method = RequestMethod.POST)
    public ResponseBodyEmitter completionsStream(@RequestBody ChatGLMRequestDTO request,@RequestHeader("Authorization") String token, HttpServletResponse response) {
        log.info("流式问答请求开始，使用模型：{} 请求信息：{}", request.getModel(), JSON.toJSONString(request.getMessages()));
        try {
            // 1. 基础配置；流式输出、编码、禁用缓存
            response.setContentType("text/event-stream");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Cache-Control", "no-cache");

            // 2. 异步处理 HTTP 响应处理类
            ResponseBodyEmitter emitter = new ResponseBodyEmitter(3 * 60 * 1000L);
            emitter.onCompletion(() -> {
                log.info("流式问答请求完成，使用模型：{}", request.getModel());
            });
            emitter.onError(throwable -> log.error("流式问答请求异常，使用模型：{}", request.getModel(), throwable));

            // 3.1 构建请求参数
            List<ChatCompletionRequest.Prompt> prompts = request.getMessages().stream()
                    .map(entity -> ChatCompletionRequest.Prompt.builder()
                            .role(Role.user.getCode())
                            .content(entity.getContent())
                            .build()).collect(Collectors.toList());
            // 使用builder模式构建失败
//            ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
//                    .model(Model.GLM_3_5_TURBO)
//                    .stream(true)
//                    .prompt(prompts)
//                    .build();

//            ChatCompletionRequest chatCompletionRequest  = ChatCompletionRequest.builder()
//                    .model(Model.GLM_4)
//                    .stream(true)
//                    .messages(prompts)
//                    .topP(0.7F)
//                    .maxTokens(2048)
//                    .temperature(0.9F)
//                    .doSample(true)
//                    .requestId("xfg-1706771794861") // 这里懒得设置，但是builder模式必须需要， 所以就随便设置一个
//                    .build();
//            System.out.println();
//            System.out.println(chatCompletionRequest);
            ChatCompletionRequest chatCompletionRequest = new ChatCompletionRequest();
            chatCompletionRequest.setModel(Model.GLM_4); // GLM_3_5_TURBO、GLM_4
            chatCompletionRequest.setMessages(prompts);

            // 3.2 请求应答
            openGlmAiSession.completions(chatCompletionRequest, new EventSourceListener() {

                @Override
                public void onEvent(@NotNull EventSource eventSource, @Nullable String id, @Nullable String type, @NotNull String data) {
                    if ("[DONE]".equals(data)) {
                        log.info("[输出结束] Tokens {}", JSON.toJSONString(data));
                        return;
                    }
                    ChatCompletionResponse chatCompletionResponse = JSON.parseObject(data, ChatCompletionResponse.class);
                    List<ChatCompletionResponse.Choice> choices = chatCompletionResponse.getChoices();
                    for (ChatCompletionResponse.Choice choice : choices) {
                        ChatCompletionResponse.Delta delta = choice.getDelta();
                        String finishReason = choice.getFinishReason();
                        if (StringUtils.isNoneBlank(finishReason) && "stop".equals(finishReason)) {
                            emitter.complete();
                            break;
                        }
                        try {
                            emitter.send(delta.getContent());
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }

                }
                @Override
                public void onClosed(@NotNull EventSource eventSource) {
                    log.info("对话完成");
                }
            });
            return emitter;
        } catch (Exception e) {
            log.error("流式应答，请求模型：{} 发生异常", request.getModel(), e);
            throw new ChatGPTException(e.getMessage());
        }
    }


}


