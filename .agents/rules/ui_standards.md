# AI Rules of Engagement: AdmiralBulldog Unified UI & Design System

These rules govern all UI development for the AdmiralBulldog Sounds application, specifically focusing on the *
*Hyper-Dark Design System** and best practices for Jetpack Compose Desktop.

## 1. Design System & Aesthetics (Hyper-Dark)

- **Theme Enforcement**: ALL application windows, dialogs, and components must be wrapped in `BulldogTheme`.
- **Exclusivity**: Light mode is deprecated. Do not implement toggles for light/dark mode. The system is permanently set
  to a premium dark aesthetic.
- **Color Palette**: Strictly use the Twitch-inspired palette defined in
  `com.github.mrbean355.admiralbulldog.ui.theme.Colors`:
    - Deep charcoal backgrounds (`MaterialTheme.colorScheme.background`).
    - Vibrant Purple (`Primary`) and Cyan (`Secondary`) for accents and interactive elements.
- **Typography**: Utilize the standard typographic scale defined in
  `com.github.mrbean355.admiralbulldog.ui.theme.Typography`. Avoid hardcoding font sizes; use `MaterialTheme.typography`
  styles.

## 2. Component Development & Layout

- **Reusable Components**: Check `com.github.mrbean355.admiralbulldog.ui.components` before creating new UI elements.
  Leverage established patterns:
    - `LabeledCheckbox`: Standardized row with a checkbox and label.
    - `NumericSpinner`: Standardized input for bounded integer values.
    - `CommonDialog`: Mandatory wrapper for all JDialog-based modal windows.
- **Layout Modifiers**: When surfacing `Modifier` overloads, always structure `modifier: Modifier = Modifier` as the
  first optional parameter, preceding any trailing lambda variables.
- **Import Hygiene**: All import statements **MUST** be placed at the absolute top of the file, immediately after the
  `package` declaration. Never place imports in the middle of a file (e.g., between functions).
- **No FQNs**: Avoid Fully Qualified Names in code; use explicit imports at the top.

## 3. Preview Standards

- **Mandatory Previews**: Every `@Composable` screen or component file must include a `private` `@Preview` function at
  the bottom of the file (before the window opener).
- **Theme Wrapping**: Previews **MUST** be wrapped in `BulldogTheme` and typically a `Surface` to ensure accurate visual
  representation:
    ```kotlin
    @Preview
    @Composable
    private fun MyComponentPreview() {
        BulldogTheme {
            Surface {
                MyComponent()
            }
        }
    }
    ```

## 4. Architectural Patterns

- **Decoupling**: Strictly avoid legacy TornadoFX / JavaFX data bindings (e.g., `Property<*>`). Use Kotlin `StateFlow`
  in ViewModels and `collectAsState()` in Composables.
- **File Structure (Co-location)**: Keep the `@Composable` screen, its sub-composables, and its previews in the same
  file.
- **Window Openers**: Entry point functions (e.g., `openMyScreen()`) must be at the virtual **bottom** of the file.
- **Teardowns**: Use `ComposeViewModel.requestWindowClose` for screen termination to ensure `onCleared()` lifecycle
  events trigger correctly.

## 5. OS & Window Integration

- **Window Sizing**: Prefer `frame.pack()` for intrinsic sizing rather than hardcoding static pixel dimensions for
  JFrames.
- **Interactivity**: Disable OS window scaling (`frame.isResizable = false`) for a consistent utility-app feel unless
  explicitly required.
- **Keyboard Shortcuts**: Anchor standard shortcuts globally. Specifically, `KeyEvent.VK_ESCAPE` should always map to a
  `WINDOW_CLOSING` event for modal dialogs.
