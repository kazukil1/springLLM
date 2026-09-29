# SpringLLM

一个基于 Java 的 LLM / Agent 学习项目，通过多个独立模块逐步探索大语言模型的核心能力。

## 技术栈

- Java 21 + Spring Boot 3.5.6
- [Spring AI](https://docs.spring.io/spring-ai/reference/) 1.1.0 + [Spring AI Alibaba](https://github.com/alibaba/spring-ai-alibaba) 1.1.0.0
- [LangChain4J](https://docs.langchain4j.dev/) 1.8.0
- LLM Provider: [DashScope (通义千问)](https://dashscope.console.aliyun.com/) (qwen3.7-plus)

## 项目结构

```
springllm/
├── springai/        # Spring AI 基础用法
├── LangChain4J/     # LangChain4J 基础用法
├── FunctionCall/    # Function Calling（工具调用）
├── rag/             # RAG（检索增强生成）
└── pom.xml          # 父 POM，统一依赖版本管理
```

## 模块说明

### 1. springai — Spring AI 基础

学习 Spring AI 的核心 API，包括模型调用、会话记忆、提示词模板、结构化输出等。

| Controller | 路径前缀 | 功能 |
|---|---|---|
| `ChatModelController` | `/model` | 直接调用 DashScope ChatModel，同步/流式 |
| `ChatClientController` | `/client` | ChatClient 封装调用，支持 Advisor、System Prompt 覆盖 |
| `PromptTemplateController` | `/template` | Prompt 模板（String / 文件），变量替换 |
| `PromptEngineerController` | `/ai/prompt` | 预设角色的 Prompt Engineering |
| `ChatMemoryController` | `/memory` | 内存级多轮对话（MessageWindowChatMemory） |
| `JdbcChatMemoryController` | `/jdbcmemory` | 基于 JDBC 的持久化对话记忆 |
| `structureOutPutController` | `/structure` | 结构化输出（Bean / List），LLM → Java 对象 |
| `StreamController` | `/stream` | SSE / Flux 流式输出 |

### 2. LangChain4J — LangChain4J 基础

学习 LangChain4J 的低级 API，包括同步调用、流式回调、多轮对话记忆。

| Controller | 路径前缀 | 功能 |
|---|---|---|
| `LangChainLowLevelContorller` | `/langchain/low` | 低级 API 调用、StreamingChatResponseHandler 回调、Flux 桥接、多轮对话记忆 |

### 3. FunctionCall — 工具调用

学习 Spring AI 的 Function Calling 机制，让 LLM 调用外部工具完成任务。

| Controller | 路径前缀 | 功能 |
|---|---|---|
| `FunctionCallController` | `/function` | 基础 Function Call（时间查询工具） |
| `PddRefundController` | `/pdd/refund` | 模拟拼多多退款客服流程，结合 System Prompt + 多轮对话 + 工具调用 |

**已实现的 Tools：**

- `TimeTools` — 根据时区获取当前时间
- `OrderTools` — 模拟订单退款申请

### 4. RAG — 检索增强生成

学习 RAG 中的文档读取环节，基于策略模式支持多种文件格式的统一读取。

| Controller | 路径前缀 | 功能 |
|---|---|---|
| `RagReaderController` | `/rag` | 根据文件类型自动选择对应 Reader 读取文档 |

**支持的文档格式：**

| Strategy | 格式 |
|---|---|
| `PDFReaderStrategy` | PDF |
| `HTMLReaderStrategy` | HTML |
| `MarkdownReaderStrategy` | Markdown |
| `JsonReaderStrategy` | JSON |
| `TextReaderStrategy` | TXT |
| `TikaReaderStrategy` | Tika 兜底（其他格式） |

## 快速开始

### 环境要求

- JDK 21+
- Maven 3.9+
- MySQL 8.0+（springai / rag 模块的 JDBC ChatMemory 需要）

### 配置环境变量

复制项目根目录下的 `.env` 文件并填入你的 API Key：

```env
DASHSCOPE_API_KEY=your_dashscope_api_key
OPENAI_API_KEY=your_openai_compatible_api_key
LANGCHAIN4J_API_KEY=your_langchain4j_api_key
LANGCHAIN4J_STREAMING_API_KEY=your_langchain4j_streaming_api_key
XIAOMI_API_KEY=your_xiaomi_api_key
DB_USERNAME=root
DB_PASSWORD=your_password
```

> `.env` 已被 `.gitignore` 忽略，不会提交到仓库。

### 编译项目

```bash
mvn clean install -DskipTests
```

### 启动单个模块

```bash
# 启动 springai 模块（端口 8080）
cd springai && mvn spring-boot:run

# 启动 LangChain4J 模块（端口 8080）
cd LangChain4J && mvn spring-boot:run

# 启动 FunctionCall 模块（端口 8080）
cd FunctionCall && mvn spring-boot:run

# 启动 rag 模块（端口 8080）
cd rag && mvn spring-boot:run
```

> 各模块默认端口均为 8080，同时只能运行一个模块，或在 `application.yaml` 中修改端口。

### 接口示例

```bash
# Spring AI - 同步调用
curl "http://localhost:8080/model/call/string?message=你好"

# Spring AI - 流式调用
curl "http://localhost:8080/model/stream/string?message=你好"

# LangChain4J - 低级 API
curl "http://localhost:8080/langchain/low/hello"

# LangChain4J - 多轮对话记忆
curl "http://localhost:8080/langchain/low/memory"

# Function Call - 时间查询
curl "http://localhost:8080/function/chat?query=现在几点了"

# Function Call - 退款客服（新建对话）
curl "http://localhost:8080/pdd/refund/newChat?userId=123&orderId=456"

# RAG - 读取文档
curl "http://localhost:8080/rag/read?filePath=/path/to/your/document.pdf"
```

## 学习路线

```
Spring AI 基础
    ↓
LangChain4J 对比学习
    ↓
Function Calling（工具调用）
    ↓
RAG（检索增强生成）
    ↓
Multi-Agent（规划中）
```

## License

MIT
