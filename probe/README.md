# probe

One-off diagnostic programs, kept because each one was written to answer a
question that came back later: how a sprite sheet is cut, where the overlay
thinks the minimap is, what a theme looks like at every accent.

They are deliberately **outside** the Gradle source set. Nothing here ships in
`Sonar.jar`, nothing here is compiled by `Setup.bat`, and a broken probe can
never break a build. They are compiled by hand, against a jar that already
exists.

## Running one

Build the app first, so `run\Sonar.jar` is current, then:

```powershell
cd E:\cyanb\.peach-workshop
& "C:\Program Files\Eclipse Adoptium\jdk-17.0.20.8-hotspot\bin\javac.exe" `
    -cp run\Sonar.jar -d probe probe\sonar\ui\ThemeShot.java
cd run
& "C:\Program Files\Eclipse Adoptium\jdk-17.0.20.8-hotspot\bin\java.exe" `
    -cp "Sonar.jar;..\probe" sonar.ui.ThemeShot
```

JDK 17, not the 25 that `java` resolves to by default — the same rule the rest
of the project follows.

A probe that opens a window takes `-Dsonar.monitor=2` like the app does, so it
lands on the secondary screen instead of over whatever is on the main one.

The `.class` files these produce are gitignored; only the sources are kept.

## What is here

| Probe | Answers |
|---|---|
| `ClampProbe` | how the Shatters overlay clamps its view against a map edge |
| `CoverProbe` | which game objects a mark would cover up |
| `HeaderShot` | renders the window header on its own |
| `KeyProbe` | what the game's key bindings map to on this machine |
| `LostShot` | how a lost character draws on the Characters page |
| `OryxRun` | replays an Oryx fight through the damage model |
| `OverlayShot` | renders the Shatters overlay without the game running |
| `SheetProbe` | how a sprite sheet is laid out |
| `SkinProbe` | which skin a class falls back to |
| `SpriteCut` | where one sprite sits inside its sheet |
| `SpriteProbe` | whether a sprite id resolves to a picture |
| `TestModeShot` | the overlay's test marks |
| `TexProbe` | raw texture lookups |
| `SettingsShot` | the Settings page, rendered offscreen |
| `ThemeShot` | every theme and accent, side by side |
| `TotalProbe` | totals the damage model produces for a known fight |
