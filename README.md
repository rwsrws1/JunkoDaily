# JunkoDaily

**JunkoDaily** 是一款基于 Jetpack Compose 与 Modern Android Development (MAD) 最佳实践构建的现代日常习惯追踪与生活记录 Android 应用程序。

项目采用了高度模块化（Multi-module）架构设计，基于最新的 AndroidX Navigation 3、Jetpack Compose Adaptive Layout、Hilt、Room、DataStore 以及 Kotlin Coroutines/Flow 等技术栈构建。

---

## 目录

- [架构与模块说明](#架构与模块说明)
- [核心功能](#核心功能)
- [技术栈](#技术栈)
- [设计系统](#设计系统)
- [快速开始](#快速开始)

---

## 架构与模块说明

项目参照 Now in Android 架构推荐，采用高度模块化与分层架构，并通过 `build-logic`（Gradle Convention Plugins）统一管理构建逻辑与依赖配置。

```
JunkoDaily
├── app                     # 应用入口与全局导航装配
├── build-logic             # 自定义 Gradle Convention Plugins
├── arithmetic_lib          # 纯 Kotlin 基础算法与数据结构实现库
├── core                    # 核心基础组件与数据层
│   ├── common              # 通用工具（如 SoundManager 音效管理）
│   ├── model               # 领域模型（RoutineCard, RoutineDailyLog 等）
│   ├── database            # Room 数据库持久化及 DAO 实现
│   ├── data                # Repository 数据仓库层
│   ├── datastore           # Preferences DataStore 本地首选项存储
│   ├── network             # 网络请求与网络状态监控
│   ├── navigation          # 基于 Navigation 3 的类型安全导航状态与路由控制器
│   └── designsystem        # Material 3 主题、图标、预设样式与自定义卡片组件
└── feature                 # 业务功能模块
    ├── routine             # 习惯管理与日常打卡核心功能
    ├── chart               # 打卡数据统计与月度/年度可视化图表
    ├── spend               # 支出/活动展示与详情模块
    ├── auth                # 用户认证与登录框架
    └── Introduction        # 引导与介绍展示模块
```

---

## 核心功能

1. **日常习惯与打卡管理 (Routine Tracker)**
   - **自定义习惯卡片**：支持创建习惯卡片，自定义卡片文案、背景颜色、预设几何形状及卡片图案。
   - **习惯打卡与记录**：支持按日期打卡与完成状态切换，集成刮刮卡交互与粒子爆炸等动画和音效反馈。
   - **日历与时间管理**：支持切换不同日期，查看历史打卡日志与进度。

2. **数据统计与可视化 (Charts & Analytics)**
   - **月度统计卡片 (`MonthChartCard`)**：展示单项习惯按月的打卡频次与完成趋势。
   - **年度热力图与汇总 (`YearChartCard`)**：直观反映全年习惯坚持情况与打卡记录。

3. **开支与日常记录 (Spend & Activity Logging)**
   - **响应式卡片网格**：支持开支/消费记录展示与多级详情切换。

4. **通用算法支持 (`arithmetic_lib`)**
   - 包含冒泡、选择、插入、快排、归并、堆排序等经典算法，以及二叉树遍历等 Kotlin 基础实现。

---

## 技术栈

- **开发语言**：Kotlin (100%)
- **UI 框架**：Jetpack Compose + Material 3 (Expressive & Adaptive UI)
- **导航组件**：AndroidX Navigation 3 (`androidx.navigation3`)
- **依赖注入**：Hilt (Dagger Hilt)
- **本地数据库**：Room Database (配合 KSP 与 Flow)
- **键值存储**：Jetpack DataStore Preferences
- **网络与图片加载**：Retrofit 2 + OkHttp 3 + Coil 3
- **媒体与异步**：Jetpack Media3 + Kotlin Coroutines & Flow
- **构建系统**：Gradle (Kotlin DSL + Version Catalogs + Convention Plugins)

---

## 设计系统

项目在 `core:designsystem` 模块中沉淀了丰富的现代化设计资源与交互组件：
- **个性化配色与形状**：内置 `PresetColorList`、`PresetShape` 及 `PresetImage` 配置。
- **自定义卡片样式**：支持翻转卡 (`FlipCard`)、深度翻转 (`DepthFlipCard`)、涂鸦边框 (`ScribbleStrokeCard`)、卷角卡片 (`CurlingCard`)、扭曲形变 (`TwistMorphCard`) 等趣味视觉样式。

---

## 快速开始

### 环境要求
- **Android Studio**: Ladybug / Jellyfish 或更新版本
- **JDK**: 17 / 21
- **Min SDK**: 26 (Android 8.0+)
- **Compile SDK**: 35+

### 构建与运行
1. 克隆仓库：
   ```bash
   git clone <repository-url>
   cd JunkoDaily
   ```
2. 用 Android Studio 打开 `JunkoDaily` 根目录。
3. 等待 Gradle Sync 完成后，选择 `:app` 模块并运行。
