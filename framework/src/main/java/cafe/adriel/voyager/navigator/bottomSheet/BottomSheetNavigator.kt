//package cafe.adriel.voyager.navigator.bottomSheet
//
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.height
//import androidx.compose.material.ExperimentalMaterialApi
//import androidx.compose.material.MaterialTheme
//import androidx.compose.material.ModalBottomSheetDefaults
//import androidx.compose.material.ModalBottomSheetLayout
//import androidx.compose.material.ModalBottomSheetState
//import androidx.compose.material.ModalBottomSheetValue
//import androidx.compose.material.contentColorFor
//import androidx.compose.material.rememberModalBottomSheetState
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.CompositionLocalProvider
//import androidx.compose.runtime.ProvidableCompositionLocal
//import androidx.compose.runtime.currentCompositeKeyHash
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.rememberCoroutineScope
//import androidx.compose.runtime.staticCompositionLocalOf
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.Shape
//import androidx.compose.ui.unit.Dp
//import androidx.compose.ui.unit.dp
//import cafe.adriel.voyager.core.screen.Screen
//import cafe.adriel.voyager.core.stack.Stack
//import cafe.adriel.voyager.navigator.CurrentScreen
//import cafe.adriel.voyager.navigator.Navigator
//import cafe.adriel.voyager.navigator.bottomSheet.internal.BottomSheetNavigatorBackHandler
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.launch
//
//typealias BottomSheetNavigatorContent = @Composable (bottomSheetNavigator: BottomSheetNavigator) -> Unit
//
//val LocalBottomSheetNavigator: ProvidableCompositionLocal<BottomSheetNavigator> =
//    staticCompositionLocalOf { error("BottomSheetNavigator not initialized") }
//
//@ExperimentalMaterialApi
//@Composable
//fun BottomSheetNavigator(
//    modifier: Modifier = Modifier,
//    hideOnBackPress: Boolean = true,
//    scrimColor: Color = ModalBottomSheetDefaults.scrimColor,
//    sheetShape: Shape = MaterialTheme.shapes.large,
//    sheetElevation: Dp = ModalBottomSheetDefaults.Elevation,
//    sheetBackgroundColor: Color = MaterialTheme.colors.surface,
//    sheetContentColor: Color = contentColorFor(sheetBackgroundColor),
//    skipHalfExpanded: Boolean = true,
//    key: String = currentCompositeKeyHash.toString(36),
//    sheetContent: BottomSheetNavigatorContent = { CurrentScreen() },
//    content: BottomSheetNavigatorContent
//) {
//    var hideBottomSheet: (() -> Unit)? = null
//    val coroutineScope = rememberCoroutineScope()
//    val sheetState = rememberModalBottomSheetState(
//        initialValue = ModalBottomSheetValue.Hidden,
//        confirmValueChange = { state ->
//            when (state) {
//                ModalBottomSheetValue.Hidden -> {
//                    hideBottomSheet?.invoke()
//                    false
//                }
//
//                else -> true
//            }
//        },
//        skipHalfExpanded = skipHalfExpanded
//    )
//
//    Navigator(HiddenBottomSheetScreen, onBackPressed = null, key = key) { navigator ->
//        val bottomSheetNavigator = remember(navigator, sheetState, coroutineScope) {
//            BottomSheetNavigator(navigator, sheetState, coroutineScope)
//        }
//
//        hideBottomSheet = bottomSheetNavigator::hide
//
//        CompositionLocalProvider(LocalBottomSheetNavigator provides bottomSheetNavigator) {
//            ModalBottomSheetLayout(
//                modifier = modifier,
//                scrimColor = scrimColor,
//                sheetState = sheetState,
//                sheetShape = sheetShape,
//                sheetElevation = sheetElevation,
//                sheetBackgroundColor = sheetBackgroundColor,
//                sheetContentColor = sheetContentColor,
//                sheetContent = {
//                    BottomSheetNavigatorBackHandler(
//                        bottomSheetNavigator,
//                        sheetState,
//                        hideOnBackPress
//                    )
//                    sheetContent(bottomSheetNavigator)
//                },
//                content = {
//                    content(bottomSheetNavigator)
//                }
//            )
//        }
//    }
//}
//
//@OptIn(ExperimentalMaterialApi::class)
//class BottomSheetNavigator internal constructor(
//    private val navigator: Navigator,
//    private val sheetState: ModalBottomSheetState,
//    private val coroutineScope: CoroutineScope
//) : Stack<Screen> by navigator {
//
//    val isVisible: Boolean
//        get() = sheetState.isVisible
//
//    fun show(screen: Screen) {
//        coroutineScope.launch {
//            try {
//                replaceAll(screen)
//                sheetState.show()
//            } catch (e: Throwable) {
//                e.printStackTrace()
//            }
//        }
//    }
//
//    fun hide() {
//        coroutineScope.launch {
//            try {
//                sheetState.hide()
//                replaceAll(HiddenBottomSheetScreen)
//            } catch (e: Throwable) {
//                e.printStackTrace()
//            }
//        }
//    }
//
//    @Composable
//    fun saveableState(
//        key: String,
//        content: @Composable () -> Unit
//    ) {
//        navigator.saveableState(key, content = content)
//    }
//}
//
//private object HiddenBottomSheetScreen : Screen {
//
//    @Composable
//    override fun Content() {
//        Spacer(modifier = Modifier.height(1.dp))
//    }
//}
