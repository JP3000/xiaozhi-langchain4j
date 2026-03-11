# 智能医疗伴诊助手

## 📋 项目简介

智能医疗伴诊助手是一个基于大语言模型的虚拟医疗客服平台，提供智能导诊、健康咨询和在线挂号等核心医疗服务能力。系统通过自然语言交互，为用户提供24/7的医疗咨询服务，并支持多轮对话管理、医疗知识检索和智能业务办理。

## 🏗️ 技术架构

- **JDK 17** + **Spring Boot 3** + **LangChain4j**
- **MySQL** + **MyBatis-Plus** + **MongoDB** + **Pinecone**
- **阿里DashScope API** + **Spring WebFlux**

## ✨ 核心功能

- 💬 **智能医疗问答** - 基于通义千问的医疗咨询服务
- 🔍 **医疗知识RAG** - Pinecone向量库增强的知识检索能力
- 🗃️ **多轮对话管理** - MongoDB持久化会话上下文
- 🛠️ **工具调用** - Function Calling实现智能挂号业务
- ⚡ **流式输出** - WebFlux实时推送模型响应

## 📁 项目结构

```
src/main/java/com/learn/java/ai/langchain4j/
├── assistant/          # AI助手接口定义
│   ├── Assistant.java  # 基础助手接口
│   ├── MemoryChatAssistant.java    # 记忆聊天助手
│   ├── SeparateChatAssistant.java  # 独立会话助手
│   └── XiaozhiAgent.java           # 小智伴诊Agent
├── bean/               # 数据实体
│   ├── ChatForm.java   # 聊天表单
│   └── ChatMessages.java # 消息记录
├── config/             # 配置类
│   ├── EmbeddingStoreConfig.java   # 向量库配置
│   ├── MemoryChatAssistantConfig.java
│   ├── SeparateChatAssistantConfig.java
│   └── XiaozhiAgentConfig.java
├── controller/         # 控制器层
│   └── XiaozhiController.java
├── entity/             # 数据库实体
│   └── Appointment.java # 挂号预约实体
├── mapper/             # MyBatis数据访问层
│   └── AppointmentMapper.java
├── service/            # 业务逻辑层
│   ├── AppointmentService.java
│   └── impl/AppointmentServiceImpl.java
├── store/              # 存储实现
│   └── MongoChatMemoryStore.java   # MongoDB对话存储
├── tools/              # 工具函数
│   ├── AppointmentTools.java       # 挂号工具
│   └── CalculatorTools.java        # 计算器工具
└── XiaozhiApp.java     # 应用启动类
```

## 🚀 快速开始

### 前置要求

- JDK 17+
- MySQL 8.0+
- MongoDB 4.4+
- Pinecone账号（向量数据库）
- 阿里云DashScope API Key

### 配置步骤

1. **克隆项目**
```bash
git clone https://github.com/JP3000/xiaozhi-langchain4j.git
cd xiaozhi-langchain4j
```

2. **配置数据库**
```yaml
# application.properties
# web port
server.port=8080

#dashscope api key
langchain4j.community.dashscope.chat-model.api-key=${DASH_SCOPE_API_KEY}
langchain4j.community.dashscope.chat-model.model-name=qwen-max

# Integration of Alibaba Tongyi Qianwen - General Text Embedding v3
langchain4j.community.dashscope.embedding-model.api-key=${DASH_SCOPE_API_KEY}
langchain4j.community.dashscope.embedding-model.model-name=text-embedding-v3

#DeepSeek
langchain4j.open-ai.chat-model.base-url=https://api.deepseek.com
langchain4j.open-ai.chat-model.api-key=${DEEP_SEEK_API_KEY}
#DeepSeek-V3
langchain4j.open-ai.chat-model.model-name=deepseek-chat

langchain4j.open-ai.chat-model.log-requests=true
langchain4j.open-ai.chat-model.log-responses=true

#MongoDB
spring.data.mongodb.uri=mongodb://localhost:27017/chat_memory_db

#mysql
spring.datasource.url=jdbc:mysql://localhost:3306/guiguxiaozhi?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&useSSL=false
spring.datasource.username=r your_username
spring.datasource.password= your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# SQL
mybatis-plus.configuration.log-impl=org.apache.ibatis.logging.stdout.StdOutImpl

logging.level.root=info

# Pinecone
pinecone.api-key=${PINECONE_API_KEY}
```

3. **配置API密钥**
```properties
# 阿里DashScope配置
langchain4j.dash-scope.chat-model.api-key=your_api_key
langchain4j.dash-scope.chat-model.model-name=qwen-max

# Pinecone配置
pinecone.api-key=your_pinecone_key
pinecone.environment=your_environment
```

## 📚 核心功能详解

### 1. AI服务集成

基于LangChain4j的`@AiService`注解，规范化系统Prompt和问诊流程：

```java
@AiService(
        wiringMode = EXPLICIT,
        chatModel = "qwenChatModel",
        chatMemoryProvider = "chatMemoryProviderXiaozhi",
        tools = "appointmentTools",
//        contentRetriever = "contentRetrieverXiaozhi"
        contentRetriever = "contentRetrieverXiaozhiPincone"
)
public interface XiaozhiAgent {

    @SystemMessage(fromResource = "zhaozhi-prompt-template.txt")
    String chat(@MemoryId Long memoryId, @UserMessage String userMessage);
}

```

### 2. RAG知识检索

使用Pinecone向量库存储医疗知识，通过相似度阈值控制检索质量：

```java
@Bean
    public EmbeddingStore<TextSegment> embeddingStore() {
        // 创建向量存储
        EmbeddingStore<TextSegment> embeddingStore = PineconeEmbeddingStore.builder()
                .apiKey(pineconeApiKey)
                .index("xiaozhi-index")// 如果指定的索引不存在，将创建一个新的索引
                .nameSpace("xiaozhi-namespace") // 如果指定的名称空间不存在，将创建一个新的名称空间
                .createIndex(PineconeServerlessIndexConfig.builder()
                        .cloud("AWS") // 指定索引部署在 AWS 云服务上。
                        .region("us-east-1") // 指定索引所在的 AWS 区域为 us-east-1。
                        .dimension(embeddingModel.dimension()) // 指定索引的向量维度，该维度与 embeddedModel 生成的向量维度相同。
                        .build())
                .build();

        return embeddingStore;
    }
```

### 3. 多轮对话管理

基于MongoDB实现会话上下文的持久化存储：

```java
@Component
public class MongoChatMemoryStore implements ChatMemoryStore {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        Criteria criteria = Criteria.where("memoryId").is(memoryId);
        Query query = new Query(criteria);

        ChatMessages chatMessages = mongoTemplate.findOne(query, ChatMessages.class);
        if (chatMessages == null) {
            return new LinkedList<>();
        }
        String contentJson = chatMessages.getContent();
        return ChatMessageDeserializer.messagesFromJson(contentJson);
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> list) {
        Criteria criteria = Criteria.where("memoryId").is(memoryId);
        Query query = new Query(criteria);
        Update update = new Update();

        update.set("content", ChatMessageSerializer.messagesToJson(list));

        //修改或新增
        mongoTemplate.upsert(query, update, ChatMessages.class);
    }

    @Override
    public void deleteMessages(Object memoryId) {
        Criteria criteria = Criteria.where("memoryId").is(memoryId);
        Query query = new Query(criteria);
        mongoTemplate.remove(query, ChatMessages.class);
    }
}
```

### 4. 流式输出

使用WebFlux实现模型响应的流式推送：

```java
@Operation(summary = "对话")
@PostMapping(value = "/chat", produces = "text/stream;charset=utf-8")
public Flux<String> chat(@RequestBody ChatForm chatForm)  {
    return xiaozhiAgent.chat(chatForm.getMemoryId(), chatForm.getMessage());
}
```

