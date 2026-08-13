# BaseFrame

Android 能力演示工作台 —— 使用 Kotlin、View/XML 与 Jetpack Compose 组织的 Android Demo 集合，覆盖浮层交互、布局动效、状态管理、设备能力及 WebView 安全基座；同时提供 MVVM + UDF、Hilt、导航、数据源和组件封装的参考实现。

## 模块结构

```
BaseFrame/
├── app/            主应用：Demo 目录、详情页、UDF 示例、深链与本地偏好
├── anylayer/       浮层组件库（Dialog / Toast / Notification / Guide / Popup）
└── module_web/     安全 WebView 基座（Policy / Client / Pool）
```

首页以卡片目录的形式组织所有 Demo，分为四个类别：

- **浮层与反馈**：Dialog、AnyLayer、Popup、Notification
- **布局与交互**：MotionLayout、CoordinatorLayout、OverScrollView、Flow
- **状态与架构**：Scoped Storage、CountDown、DSL Usage、Behavior
- **设备与媒体**：Camera

目录元数据定义在 `app/src/main/java/com/robin/baseframe/ui/home/DemoCatalog.kt`。每个可见 Demo 使用稳定 ID、标题、摘要、分类、动作与可用性状态描述；新增 Demo 应同时补充目录条目、导航动作、资源文案与对应测试。

## 技术栈

- **语言/UI**：Kotlin、Jetpack Compose（新页面）+ View/XML（存量页面），Navigation Component
- **架构**：MVVM + UDF（`BaseViewModel<UiState, UiEvent, UiEffect>`），详见 `docs/architecture.md`
- **依赖注入**：Hilt
- **异步**：Kotlin Coroutines / Flow
- **数据示例**：内置确定性 Fake DataSource，覆盖加载、成功、空数据、失败和重试状态；预留远程数据源接入点
- **本地体验**：Preferences DataStore（收藏/最近访问）、内部深链 `baseframe://demo/{demoId}`
- **WebView**：HTTPS 域名白名单策略、默认拒绝 JS/Bridge/定位/文件选择、统一 `WebViewPool`
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

# 执行 App 与 module_web 的 JVM 测试
./gradlew :app:testDebugUnitTest :module_web:testDebugUnitTest

# 执行静态检查（当前存在历史 lint 问题，详见 docs/test-matrix.md）
./gradlew :app:lintDebug

# 编译 Release 产物
./gradlew :app:assembleRelease :module_web:assembleRelease :anylayer:assembleRelease

# 安装到已连接设备/模拟器
./gradlew :app:installDebug
```

也可以直接用 Android Studio 打开根目录，选择 `app` 运行配置启动。

## 质量与安全基线

- 首页 AIDL Service 绑定以 View 生命周期为边界，离开页面时会注销 callback 并解绑。
- WebView 只允许显式配置的 HTTPS Origin；默认不加载不可信导航，也不开放 JavaScript、JS Bridge、地理位置或文件选择。
- WebView SSL 错误始终取消，release 构建不开放 WebContents debugging。
- 示例数据默认离线可运行，不会请求占位网络地址。
- Deep link 仅接受已登记的 Demo：`baseframe://demo/{demoId}`；未知或格式错误链接安全停留在目录页。
- 自动化与真机验收范围请参考本地 `docs/test-matrix.md`（该目录按本地工作文档维护，默认已忽略）。

## 本地文档

`docs/` 用于本地路线图、审查报告和测试矩阵，当前已被 `.gitignore` 忽略。需要团队共享时，请通过约定渠道同步；README 不依赖其中文档作为构建或运行前置条件。

## 架构速览

```
UI (Compose/XML) ──onEvent──► ViewModel ──► UseCase ──► Repository ──► DataSource
      ▲                                                                    │
      └────────────────── uiState (StateFlow) ◄── Result<T> ◄─────────────┘
```

新页面遵循单向数据流：View 只读取 `uiState`、发送 `onEvent`；一次性动作（Toast、导航）通过 `effect` 下发。存量页面在完成迁移前继续使用 `LegacyViewModel` / `LegacyBaseFragment` 兼容层（已标记 `@Deprecated`，新代码禁止使用）。
