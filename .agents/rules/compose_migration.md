# AI Rules of Engagement: Jetpack Compose Desktop Migration

When assisting in the ongoing architectural migration from TornadoFX to Jetpack Compose Desktop for the AdmiralBulldog
Sounds application, the AI must strictly adhere to the following established rules and conventions:

## 1. Architectural & Layout Design

- **Window Rendering**: Do not statically constrain `JFrame` window sizes. Always leverage `frame.pack()` inside AWT
  bindings so Jetpack Compose Desktop implicitly fits intrinsic widths/heights natively.
- **Window Rigidity**: Generally disable manual OS window scaling (`frame.isResizable = false`) for consistent,
  application-controlled component bounds.
- **TornadoFX Decoupling**: Completely purge TornadoFX data bindings (e.g. `Property<*>` bindings or `FXEvent`) from
  standard ViewModels. Rely exclusively on pure Kotlin `StateFlow` infrastructures bridged to Jetpack Compose state
  collection bindings.

## 2. Compose Best Practices

- **Preview Engineering**: You must **always** append an `@Preview` environment block directly below any established
  `@Composable` screens or widgets.
- **Preview Isolation**: You must **always** explicitly mark `@Preview` functions as `private` to avoid polluting the
  underlying Kotlin context scope.
- **Layout Modifiers**: When surfacing `Modifier` overloads, always structure `modifier: Modifier = Modifier` directly
  preceding any trailing lambda variables (e.g., `onConfirmClick: () -> Unit`). This ensures standard Kotlin compiler
  lambda targeting conventions remain pristine.
- **Explicit Imports**: Never leverage Fully Qualified Names (FQNs) natively within method blocks (e.g.,
  `androidx.compose.ui.Modifier`). Cleanly aggregate missing dependencies via explicit file imports at the top of the
  compilation script.
- **Reusable Extractions**: Extrapolate any deeply reusable Jetpack Compose widgets (like form toggles or common UI
  inputs) outside of screen modules natively into the common `com.github.mrbean355.admiralbulldog.ui.components`
  package.

## 3. UI Component Structure

- **Code Co-location**: When swapping legacy TornadoFX screens for Compose screens, completely overwrite the target
  script with the explicit rendering components. Keep the `@Composable` screen definitions and the `openScreen()`
  invocation hooks squashed within the **exact same file** (e.g., `FeedbackScreen.kt`). *Do not* split Compose modules
  into standalone `.kt` scripts alongside legacy `.kt` files.
- **Window Openers**: Logic for launching the screen (e.g., `openSettingsScreen()`) should always be placed at the
  absolute bottom of the file, after all `@Composable` functions and previews.
- **Progress Dialogs**: Do not rely on legacy TornadoFX progress window overlays that block the entire OS instance.
  Natively route loading states directly to the underlying Compose Screen UI using tools like
  `CircularProgressIndicator` disabled triggers instead.

## 4. Hardware Dispatches & OS Callbacks

- **Thread Sandboxing**: With `kotlinx-coroutines-javafx` eliminated from constraints, **never** employ
  `Dispatchers.Main` natively on the KMP configuration. If UI propagation requires explicit thread hopping out of
  default asynchronous behaviors, utilize the `JavaFxDispatcher` natively mapped natively inside `AppViewModel.kt`.
- **Native Keyboard Listening**: Hook universal desktop shortcuts intuitively natively inside framework bridges.
  Specifically, anchor `KeyEvent.VK_ESCAPE` to natively trigger an emulated `WINDOW_CLOSING` dispatch via AWT's native
  `KeyboardFocusManager`.
- **Window Teardowns**: Connect screen termination commands straight to the abstracted
  `ComposeViewModel.requestWindowClose` `MutableSharedFlow` collector rather than trying to explicitly manage
  independent JFrame disposes locally in Compose trees. Ensure `viewModel.onCleared()` fires reliably during window
  garbage collection.
