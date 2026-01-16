# Kotlin Inject 依赖注入关系整理

## 1. 组件层级与入口
- Android 入口组件：`AndroidApplicationComponent`（`app-hosting/src/androidMain/...`），`@Component` + `@ApplicationScope`，由 `HostingApplication` 中 `AndroidApplicationComponent.create(this)` 创建。
- iOS 入口组件：`IosApplicationComponent`（`app-hosting/src/iosMain/...`），`@Component` + `@ApplicationScope`，由 `IosApplicationComponent.create(...)` 创建。
- Activity 级组件：
  - `AndroidActivityComponent`（`@ActivityScope`），通过 `@Component val applicationComponent: AndroidApplicationComponent` 作为父组件。
  - `IosActivityComponent`（`@ActivityScope`），通过 `@Component val applicationComponent: IosApplicationComponent` 作为父组件。
- 额外组件：
  - `BrowserBridgeDialogActivityComponent`（Android-only，`commonbiz/common`），用于 BrowserBridge Dialog 的 Activity 级依赖。

简图（逻辑关系）：

```
AndroidApplicationComponent (@ApplicationScope)
  └─ HostingApplicationComponent
      ├─ CommonComponent + CommonPlatformComponent
      ├─ SharedScreenModelModule + SharedScreenPlatformModule
      ├─ ExploreComponent
      ├─ FeedsComponent
      ├─ NotificationsComponent + NotificationsComponentPlatform
      ├─ ProfileComponent
      ├─ ActivityPubComponent + ActivityPubPlatformComponent
      ├─ RssComponent + RssPlatformComponent
      └─ BlueskyComponent + BlueskyPlatformComponent

AndroidActivityComponent (@ActivityScope)
  └─ parent: AndroidApplicationComponent

IosActivityComponent (@ActivityScope)
  └─ parent: IosApplicationComponent
```

## 2. 作用域
- `@ApplicationScope`：应用级单例（`commonbiz/common/src/commonMain/.../Scopes.kt`）。
- `@ActivityScope`：Activity 级对象，通常由 ActivityComponent 承载。

## 3. 模块级 DI 结构

### app-hosting（聚合层）
- `HostingApplicationComponent`（`app-hosting/src/commonMain/...`）
  - 聚合所有 Feature/Plugin/Common 模块组件（见上图）。
  - 将 `Set<IStatusProvider>` 组装为 `StatusProvider`（统一内容源入口）。
  - 注册 `MainViewModel`、`MainDrawerViewModel` 的 ViewModel Map。
- `AndroidApplicationComponent` / `IosApplicationComponent`
  - 平台基础依赖（`ApplicationContext`/`UIApplication`、`NSUserDefaults`）。
  - 提供 `ImageLoader`。
  - iOS 额外绑定 `KRouterStartup` 到 `ModuleStartup` 集合。
- `AndroidActivityComponent` / `IosActivityComponent`
  - 提供 Activity / UIViewController 与 `FreadApp` 容器。

### commonbiz/common
- `CommonComponent`（`commonbiz/common/src/commonMain/...`）
  - 提供 `StartupManager`、`DayNightHelper`、`FreadConfigManager`、`StatusProvider` 等核心单例。
  - 用 `KotlinInjectViewModelProviderFactory` 组装 ViewModel Map（`ViewModelCreator` + `ViewModelFactory`）。
  - 绑定 `CommonStartup`、`FreadConfigModuleStartup` 到 `ModuleStartup` 集合。
  - 注册全局 ViewModel（如 `UrlRedirectViewModel`、`SelectAccountForPublishViewModel`）。
- `CommonPlatformComponent`（expect/actual）
  - Android：Room DB、`FlowSettings`、`browserInterceptorSet`、`oauthHandler`，并把 `FeedsRepoModuleStartup`、`LanguageModuleStartup` 加入 `ModuleStartup` 集合。
  - iOS：Room + BundledSQLiteDriver，`FlowSettings` 使用 `NSUserDefaults`。
- `CommonActivityComponent` + `CommonActivityPlatformComponent`
  - 提供 `ActivityDayNightHelper`，并为 `SystemBrowserLauncher` 做平台绑定。

### commonbiz/sharedscreen
- `SharedScreenModelModule`
  - 注册共享页 ViewModel（`StatusContextViewModel`、`RssBlogDetailViewModel`、`MultiAccountPublishingViewModel`、`SelectAccountOpenStatusViewModel`）。
  - 提供 `ModuleScreenVisitor`，依赖 `IFeedsScreenVisitor` + `IProfileScreenVisitor`（来自 Feeds/Profile 模块）。
- `SharedScreenPlatformModule`
  - 提供 `SelectedAccountPublishingDatabase`（平台路径差异）。

### feature/feeds
- `FeedsComponent`
  - 注册 Feeds 管理相关 ViewModel（`ContentHome`、`MixedContent`、`Add/Edit/Import` 等）。
  - 提供 `IFeedsScreenVisitor`（被 SharedScreenModelModule 使用）。

### feature/profile
- `ProfileComponent`
  - 注册 `ProfileHomeViewModel`、`SettingScreenModel`、`AboutViewModel`。
  - 提供 `IProfileScreenVisitor`（被 SharedScreenModelModule 使用）。

### feature/explore
- `ExploreComponent`
  - 注册 Explore/搜索相关 ViewModel（Home、Search、SearchAuthor/Status/Hashtag/Platform 等）。

### feature/notifications
- `NotificationsComponent`
  - 注册通知页 ViewModel（Home/Container）。
- `NotificationsComponentPlatform`
  - 提供 `NotificationsDatabase`（平台 Room/SQLite 实现差异）。

### plugins/activitypub-app
- `ActivityPubComponent`
  - 提供 `ActivityPubLoggedAccountRepo`。
  - 将 `ActivityPubProvider` 注入 `Set<IStatusProvider>`。
  - 将 `ActivityPubUrlInterceptor` 注入 `Set<BrowserInterceptor>`。
  - 绑定 `ActivityPubStartup` 到 `ModuleStartup` 集合。
  - 注册 ActivityPub 相关 ViewModel（账号、内容流、列表、过滤器、用户、搜索、趋势等）。
- `ActivityPubPlatformComponent`
  - 提供 ActivityPub 相关数据库。
  - Android：额外提供 Push 数据库、`PushInfoRepo`、`PushNotificationManager`。
  - iOS：Room + BundledSQLiteDriver。

### plugins/rss
- `RssComponent`
  - 将 `RssStatusProvider` 注入 `Set<IStatusProvider>`。
  - 注册 `RssSourceViewModel`。
- `RssPlatformComponent`
  - 提供 `RssDatabases` 与 `RssParser`（Android 使用 OkHttp，iOS 使用 NSURLSession）。

### plugins/bluesky
- `BlueskyComponent`
  - 将 `BlueskyProvider` 注入 `Set<IStatusProvider>`。
  - 将 `BskyUrlInterceptor` 注入 `Set<BrowserInterceptor>`。
  - 绑定 `BskyStartup` 到 `ModuleStartup` 集合。
  - 注册 Bluesky 相关 ViewModel（home、feeds、user、publish、search 等）。
- `BlueskyPlatformComponent`
  - 提供 `BlueskyLoggedAccountDatabase`（平台 Room/SQLite 实现差异）。

## 4. 跨模块依赖关系汇总
- `HostingApplicationComponent` 统一聚合各模块的 `@Provides`、`@IntoSet`、`@IntoMap` 产物。
- `StatusProvider` 依赖 `Set<IStatusProvider>`：由 ActivityPub / Rss / Bluesky 三个插件模块贡献。
- `BrowserInterceptor` 集合在 Android 平台通过 ActivityPub/Bluesky 注入，用于统一 URL 拦截与跳转。
- `ModuleStartup` 集合由 Common + 平台模块 + ActivityPub + Bluesky + iOS 的 `KRouterStartup` 共同贡献，`StartupManager.initialize()` 统一触发。
- `SharedScreenModelModule` 依赖 Feeds/Profile 模块的 `IFeedsScreenVisitor`、`IProfileScreenVisitor` 来构建 `ModuleScreenVisitor`。
- ViewModel Map 汇总在 `CommonComponent`，由各模块以 `@IntoMap` 方式注册，最终由 `ViewModelProvider.Factory` 统一提供。

## 5. 组件访问方式（Android）
- `HostingApplication` 实现 `ApplicationComponentProvider`，并设置 `commonComponentProvider` 全局入口。
- `Context.component` -> `AndroidApplicationComponent`
- `Context.commonComponent` -> `CommonComponent`
- `Context.activityPubComponent` -> `ActivityPubComponent`
