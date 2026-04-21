# AdmiralBulldog Sounds: Manual Testing Checklist

Following the 100% native Jetpack Compose migration, please use this checklist to verify that all features and UI components are functioning correctly.

---

## 🚀 Startup & Initialization
- [ ] **First-time Run**: Delete `config.json` and verify the **Installation Wizard** appears.
- [ ] **Dota Path Selection**: Verify the new native **FileDialog** correctly selects the Dota 2 folder.
- [ ] **Java Version Check**: (Optional) Run with an older Java version to verify the legacy `JOptionPane` appears.
- [ ] **OS Distribution Check**: Verify no distribution mismatch error appears on your current OS.
- [ ] **Splash/Loading**: Verify the loading screen appears during sound bite sync.

## 🏠 Main Screen
- [ ] **UI Scaling**: Verify the window is correctly sized ("packed") and centered on startup.
- [ ] **Tray Integration**: Verify "Minimize to Tray" works (app disappears from taskbar, remains in tray).
- [ ] **Tray Menu**: Verify "Show" restores the window and "Close" exits the app.
- [ ] **Quick Links**: Click the version number to verify it opens the latest GitHub release in your browser.

## 🔊 Sound Triggers & Management
- [ ] **Manage Sounds**: Open the sound management screen and verify the grid of checkboxes works.
- [ ] **Sound Customisation**: Click a trigger (e.g., "Kill") and verify:
    - [ ] Chance slider works.
    - [ ] Playback speed sliders work.
    - [ ] "Test" button plays the sound at a random speed within the range.
    - [ ] "Choose Sounds" opens the selection dialog.
- [ ] **Sound Board**: Add sounds to the board and verify they can be played manually.
- [ ] **Sound Combos**: Create a sound combo, test it, and assign it to a trigger.

## 🤖 Discord Bot
- [ ] **Token Validation**: Enter a token and verify the status dot changes (Neutral -> Loading -> Good/Bad).
- [ ] **Bot Invite**: Verify the "Invite bot" link opens the correct URL.
- [ ] **Discord Triggers**: Toggle "Play through Discord" for a trigger and verify it works in a voice channel.
- [ ] **Sound Board**: Click a sound on the Discord sound board and verify it plays in the voice channel.

## 🛠️ Dota Mods
- [ ] **Risk Disclaimer**: Verify the "Accept Risk" dialog appears and correctly saves your choice.
- [ ] **Mod Installation**: Select a mod and click "Download". Verify the progress overlay appears.
- [ ] **Launch Options**: Verify the "Enable" button shows the dialog with the `-language bulldog` instruction.

## ⏳ Roshan Timer
- [ ] **UI States**: Verify the timer shows "Waiting for Roshan" or the death/respawn times correctly.
- [ ] **Turbo Mode**: Toggle the checkbox and verify the respawn timings adjust accordingly.

## ⚙️ Settings & Updates
- [ ] **Check for Updates**: Click "Check Now" for App/Sounds/Mods and verify the "Up to date" or "Update available" dialogs appear.
- [ ] **Master Volume**: Change the master volume and verify it affects all locally played sounds.
- [ ] **Feedback**: Open the feedback screen, fill it out, and verify the submission dialog.

---

## ⚠️ Edge Cases & Robustness
- [ ] **No Internet**: Start the app without internet and verify it handles update check failures gracefully (showing SadKek dialogs).
- [ ] **Invalid Dota Path**: Change the dota path in `config.json` to something invalid and verify the app asks to re-run the wizard.
- [ ] **Window Focus**: Verify that ESCAPE closes sub-windows/dialogs where expected.
- [ ] **Multi-Monitor**: Verify windows center correctly on the primary monitor.
- [ ] **Crash Handling**: (Developer Only) Trigger a test exception to verify the native **Uncaught Exception** dialog appears and points to a log file.
