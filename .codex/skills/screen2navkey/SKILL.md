---
name: screen2navkey
description: convert Voyager Screen to navigation 3
---

## 任务背景
目前项目中使用了 Voyager(cafe.adriel.voyager:voyager-navigator) 作为导航框架，现在我希望将 Voyager 替换成 navigation3(androidx.navigation3:navigation3-runtime).

## 任务内容
目前 nav3 我已经集成并且完成了部分代码的重构，现在你需要帮我做一件事情，将一些 Screen 替换成 一个 Composable 函数 + NavKey。

你的目的是把继承自 cafe.adriel.voyager.core.screen.Screen 或者 com.zhangke.fread.common.page.BaseScreen 的类改成 androix.navigaton3 的一个 NavKey + 对应的 Composable 函数。

比如现在有这样的一个 Screen：
```kotlin
class ProfileScreen : BaseScreen() {

    @Composable
    override fun Content() {
        super.Content()
        val viewModel = getViewModel<ProfileHomeViewModel>()
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
        ) {
            Text(text = "Profile Screen")
        }
    }
}

```
那么你需要改成如下方式，并且新增一个 NavKey:
```kotlin

object ProfileScreenKey: NavKey

@Composable
fun ProfileScreen(viewModel: ProfileHomeViewModel){
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background),
    ) {
        Text(text = "Profile Screen")
    }
}
```
但如果这个页面有参数，那么 key 也应该带一个参数：
```kotlin
data class DetailScreenKey(val itemId: String) : NavKey

@Composable
fun DetailScreen(viewModel: DetailViewModel){
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background),
    ) {
        Text(text = "Detail Screen for item: $itemId")
    }
}
```

然后你需要把这个新增的 NavKey 注册到当前模块的 NavEntryProvider 中，比如：
```kotlin
class ProfileNavEntryProvider : NavEntryProvider {

    override fun EntryProviderScope<NavKey>.build() {
        entry<ProfileScreenKey> {
            ProfileScreen(koinViewModel())
        }
        entry<CreatePlanScreenNavKey> { key ->
            // with parameters
            CreatePlanScreen(koinViewModel { parametersOf(key.lexicon) })
        }
    }

    override fun PolymorphicModuleBuilder<NavKey>.polymorph() {
        subclass(ProfileScreenKey::class)
        subclass(CreatePlanScreenNavKey::class)
    }
}
```

## 工作流程
你需要 Follow 以下工作流程：
1. 首先找到给定模块中所有符合如下条件的 Screen：
    a. 继承自 cafe.adriel.voyager.core.screen.Screen 或者 com.zhangke.fread.common.page.BaseScreen
    b. 不包含任何嵌套 **Navigator**
2. 将这些符合条件的 Screen 列出并输出到控制台
3. 逐个重构这些 Screen
4. 对于每个 Screen，首先创建该 Screen 的 NavKey，比如给 ProfileScreen 创建一个 ProfileScreenNavKey.
5. 将 ProfileScreen 改为 @Composable 函数。
6. 对于使用了 navigationResult 的地方请保持不动，不要试图修改相关的代码，即使有编译报错也不用管，保留原样。
7. 将 ProfileScreenNavKey 以及这个 @Composable 函数 注册到该模块的 NavEntryProvider 中。
8. 找到这个 Screen 的相关引用，并将跳转处改为这个 Screen 的 NavKey
9. 结束这个 Screen 重构并进入下一个 Screen。
10. 直到所有重构完所有满足条件的 Screen。

## 绝对禁止
一下内容为绝对禁止修改的规则：
1. 对于已经修改完成的类请不要再改
2. 你只应该修改 Screen 和 navigation3 相关的代码，其他的代码不要改，即使你觉得有问题也不要改
3. 不要做任何超出我要求的事情
4. 遇到不属于上述情况的页面请直接忽略，不要自己想办法解决
5. 不要求改任何嵌套的 Navigator 页面，遇到嵌套的情况直接跳过
6. 不要修改任何已经使用 navigation3 的页面
7. 不要通过代码引用的方式找某个页面的引用并且试图修改其引用点
8. 不要修改任何超出要求的代码

