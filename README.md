# BaseFrame

Android 能力演示工作台 —— 一个用 MVVM + UDF 架构组织的 Kotlin/Compose 项目，收录了浮层交互、布局动效、状态管理和设备能力等一系列可运行的 Demo，同时也是 Android 基础框架（网络层、DI、导航、WebView 封装等）的参考实现。

## 模块结构

```
BaseFrame/
├── app/            主应用：Demo 目录首页、各能力详情页、DI、网络与架构基类
├── anylayer/       浮层组件库（Dialog / Toast / Notification / Guide / Popup）
└── module_web/     WebView 封装模块（BaseWebView、WebViewPool、下载拦截等）
```

首页以卡片目录的形式组织所有 Demo，分为四个类别：

- **浮层与反馈**：Dialog、AnyLayer、Popup、Notification
- **布局与交互**：MotionLayout、CoordinatorLayout、OverScrollView、Flow
- **状态与架构**：Scoped Storage、CountDown、DSL Usage、Behavior
- **设备与媒体**：Camera

目录数据模型定义在 `app/src/main/java/com/robin/baseframe/ui/home/DemoCatalog.kt`，新增 Demo 只需在此追加一项，无需改动首页布局或监听逻辑。

## 技术栈

- **语言/UI**：Kotlin、Jetpack Compose（新页面）+ View/XML（存量页面），Navigation Component
- **架构**：MVVM + UDF（`BaseViewModel<UiState, UiEvent, UiEffect>`），详见 `docs/architecture.md`
- **依赖注入**：Hilt
- **异步**：Kotlin Coroutines / Flow
- **网络**：Retrofit + Gson
- **设备能力**：CameraX、Zxing（二维码）
- **图片加载**：Glide、Coil

## 环境要求

| 项目 | 版本 |
|------|------|
| compileSdk / targetSdk | 34 |
| minSdk | 23 |
| JDK | 17 |
| Kotlin | 1.9.10 |
| Android Gradle Plugin | 8.4.2 |
| Hilt | 2.48 |

## 快速开始

```bash
# 编译 Debug 包
./gradlew :app:assembleDebug

# 仅编译 Kotlin（快速验证改动是否通过编译）
./gradlew :app:compileDebugKotlin

# 运行 JVM 单元测试
./gradlew :app:test

# 安装到已连接设备/模拟器
./gradlew :app:installDebug
```

也可以直接用 Android Studio 打开根目录，选择 `app` 运行配置启动。

## 项目文档

详细文档集中在 [`docs/`](docs) 目录：

| 文档 | 内容 |
|------|------|
| [architecture.md](docs/architecture.md) | MVVM + UDF 架构总览、分层职责、关键类说明 |
| [guide-mvvm-udf.md](docs/guide-mvvm-udf.md) | 新建一个 UDF 页面的 5 步开发指南与规范 checklist |
| [migration-guide.md](docs/migration-guide.md) | 存量 LiveData/MVVM 代码迁移到 UDF 的对照表与进度 |
| [di-setup.md](docs/di-setup.md) | Hilt 依赖注入配置、作用域与常见问题 |
| [ui-optimization-plan.md](docs/ui-optimization-plan.md) | 首页目录化与详情页 UI 一致性改造计划，含分阶段实施记录 |
| [report.md](docs/report.md) | 全仓代码审查报告（权限、安全、内存/生命周期、代码质量） |

> `docs/` 未纳入版本控制（见 `.gitignore`），是本地持续维护的工作文档，仓库克隆后需要单独同步。

## 架构速览

```
UI (Compose/XML) ──onEvent──► ViewModel ──► UseCase ──► Repository ──► DataSource
      ▲                                                                    │
      └────────────────── uiState (StateFlow) ◄── Result<T> ◄─────────────┘
```

新页面遵循单向数据流：View 只读取 `uiState`、发送 `onEvent`；一次性动作（Toast、导航）通过 `effect` 下发。存量页面在完成迁移前继续使用 `LegacyViewModel` / `LegacyBaseFragment` 兼容层（已标记 `@Deprecated`，新代码禁止使用）。
