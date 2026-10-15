# Sonar — working notes

A packet-sniffing damage tracker and companion tool for Realm of the Mad God. Java 17,
Gradle 7.6.4, Swing with fully custom-painted panes.

---

## READ THIS FIRST: it is all one project now

The product was Tomato, then Peach, then Sonar. On 2026-10-14 the rename was finished
and the RealmShark packet library, which had been a separate git worktree, was folded
into the main source tree. **Everything is `sonar.*` and builds in one step.**

| Package | What |
|---|---|
| `sonar` | `Sonar` (main), `AppWindow`, `ChatPings`, `SetupTool`, `OutOfMemoryDialog` |
| `sonar.ui` | every pane, theme and model - `SonarShell`, `SonarTheme`, `LiveView` and the rest |
| `sonar.backend` | live state (`GameData`), packet capture wiring, `Sniffer`, `BagSounds` |
| `sonar.game` | realm parsing, sounds, character stats |
| `sonar.packets` | the protocol: packet types, readers, the capture stack (was `packets`) |
| `sonar.assets` | game asset extraction (was `assets`) |
| `sonar.util` | shared helpers, including `PropertiesManager` (was `util`) |

Settings keys are `sonar.*`; `Settings.migrate()` renames leftover `peach.*` keys at
startup. Developer switches are `-Dsonar.demo`, `-Dsonar.shot`, `-Dsonar.monitor`,
`-Dsonar.selftest`.

One thing still says `peach`, correctly: `Settings.OLD`, the prefix being migrated
away from. It has to keep the old spelling to recognise the keys it renames. The accent
once named "Peach" became "Salmon" in 2.8.0, with the old name still accepted when read
back so nobody loses their choice.

### Provenance, and what must not be deleted

Sonar began as a fork of RealmShark (MIT) and absorbed its packet-capture and asset
code, which is most of `sonar.packets`, `sonar.assets` and `sonar.util`. That upstream
project is no longer developed and nothing is merged from it any more, so the names,
the branding and the separate build are all gone.

**The licence notices are not branding and must stay.** MIT permits all of the above -
renaming, selling, closing the source - on one condition: the copyright and permission
notice travels with every copy distributed. Four separate obligations ride along:

| Keep | Why |
|---|---|
| `LICENSE.md`, shipped in the release zip | the MIT condition itself; without it, distributing is infringement rather than licensed use |
| the 3 headers in `sonar\packets\packetcapture\sniff\ardikars` | Pcap Project, `SPDX: MIT OR Apache-2.0` |
| the 13 UnityPy credits in `sonar\assets\resextractor` | derived from UnityPy (MIT) |
| `META-INF\LICENSES\` inside the jar | Apache-2.0 for gson, flatbuffers and JNA, which requires notices be passed on |

None of that limits what Sonar can be sold as or called. It costs one file and some
comment headers. Do not "tidy" them away.

The `custom-realmshark` branch is kept, on `origin` too, as the record of the library
before it was folded in.

### If you are asked to rebrand again

- `Updater.CURRENT` and the repository URL in `Updater.java`
- window titles and the left rail in `SonarShell.java` / `LiveView.java`
- `README.md`, `INSTALL.md`, `CHANGELOG.md`, `tools
elease\INSTRUCTIONS.txt`
- `applicationName` in `build.gradle` and `rootProject.name` in `settings.gradle`, which
  between them drive the main class, the generated `Version.java` path and the jar name
- the exe/zip names in `tools\setup.ps1`, `tools\make-release.ps1`, `tools\deploy-test.ps1`
- `$appId` in `tools\setup.ps1` and `APP_ID` in `WindowsShell.java` - they must match, or a
  pinned shortcut stops lighting up when the window opens
- the icon filenames, and the paths to them in `Sonar.java` and `AppWindow.java`
- the settings-key prefix, by extending `Settings.migrate()` rather than replacing it
- the install folder names, then `setup.ps1 -LauncherOnly` to repoint the shortcuts

---

## Build and test

```powershell
.\gradlew.bat shadowJar                      # output lands in build\libs\Sonar.jar
Copy-Item build\libs\Sonar.jar run\Sonar.jar -Force
```

**The copy step is not optional.** Gradle writes to `build\libs\`; only `Setup.bat` copies to
`run\`. Forgetting it means every test runs the *previous* build — this has caused real
misdiagnoses. `tools\deploy-test.ps1` does the build and both copies in one go.

Self-test (54 assertions, must be 0 FAIL):

```powershell
cd run
java "-Dsonar.demo=1" "-Dsonar.selftest=1" "-Dsonar.shot=<path>" "-Dsonar.monitor=2" -jar Sonar.jar
```

- `-Dsonar.monitor=2` is **required** — test windows must never open on Jordan's primary screen.
- `-Dsonar.shot=<path>` makes the run exit on its own; without it the process hangs until killed.
- Prefer headless offscreen render harnesses (`-Djava.awt.headless=true`, call
  `pane.paintContent(g, width)` directly) over opening a real window.

## Releases

GitHub releases go out **only on minor bumps** (`x.Y.0`), never on patches. Patch work is
deployed to the test install at `E:\cyanb\Documents\Sonar Test` with `tools\deploy-test.ps1` for
Jordan to try first. Bump `Updater.CURRENT`, add a `CHANGELOG.md` section in plain language, then
release.

**`gh` wants `-R JustAutomate-AI/sonar-rotmg`** when run from anywhere but the repo root.

## House style

- Comments explain *why*, in plain prose, matching the surrounding density. No decorative headers.
- A figure Sonar cannot know is shown as a dash, never as zero, and never guessed.
- Verify against real data rather than reasoning about what the packets probably contain.
