# 基础镜像
FROM openjdk:8-jre-slim
# 作者
MAINTAINER xiaofuge
# 配置
ENV PARAMS=""
# 时区
ENV TZ=PRC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
# 添加应用
ADD target/chatgpt-data-app.jar /chatgpt-data-app.jar
## 在镜像运行为容器后执行的命令
ENTRYPOINT ["sh","-c","java -jar $JAVA_OPTS /chatgpt-data-app.jar $PARAMS"]