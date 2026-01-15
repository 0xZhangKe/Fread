---
name: voyager2nav3
description: convert Voyager Screen to navigation 3
---

目前项目中使用了 Voyager(cafe.adriel.voyager:voyager-navigator) 作为导航框架，现在我希望将 Voyager 替换成 navigation3(androidx.navigation3:navigation3-runtime).
nav3我已经集成并且完成了部分代码，现在你需要帮我做一件事情，将所有的 Screen 替换成 一个 Composable 函数。

抽象类不要改， 只需要把继承自 cafe.adriel.voyager.core.screen.Screen 或者 com.zhangke.fread.common.page.BaseScreen 的类改成 Composable 函数即可。

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
然后跳转该页面的地方也需要同步修改,首先替换 LocalNavigator.currentOrThrow 为 LocalNavBackStack.currentOrThrow,然后通过 NavKey 跳转页面。

你需要按照模块来完成工作。

对于已经修改完成的类请不要再改。

你只应该修改 Screen 和 navigation3 相关的代码，其他的代码不要改，即使你觉得有问题也不要改。

不要修改任何 Tab 以及其直接引用的页面。

遇到不属于上述情况的页面请直接忽略，不要自己想办法解决。

不要求改任何嵌套的 Navigator 页面，遇到嵌套的情况直接跳过。

不要做任何超出我要求的事情。

