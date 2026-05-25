# AGENTS.md

This file is the operating manual for AI coding agents working in the Fread repository.
Keep instructions concrete, executable, and specific to this codebase.

## Project Overview

Fread is a Kotlin Multiplatform decentralized microblogging client for Mastodon, Bluesky, and RSS. The main implementation is Kotlin/Compose Multiplatform with Android and iOS targets.

Important modules:

- `app`: Android application wrapper, signing, packaging, launcher resources, and APK renaming.
- `iosApp`: Swift/iOS host project for the shared Compose app.
- `app-hosting`: shared app shell, root navigation, Koin startup, platform app entry points, and `FreadKit` framework output for iOS.
- `framework`: shared infrastructure utilities, Compose helpers, HTTP/client primitives, permissions, parsing, persistence helpers, and platform `expect`/`actual` abstractions.
- `bizframework/status-provider`: status-provider contracts, account abstractions, rich text parsing, status models, publishing/search interfaces, and protocol-facing types.
- `commonbiz/common`, `commonbiz/analytics`, `commonbiz/status-ui`, `commonbiz/sharedscreen`: shared business logic, analytics abstractions, reusable status UI, and shared screens.
- `feature/*`: user-facing feature modules such as feeds, explore, profile, and notifications.
- `plugins/activitypub-app`, `plugins/bluesky`, `plugins/rss`: protocol implementations integrated through status-provider contracts.
- `plugins/Fread-Firebase`: optional Firebase/push/review integration. It is included only when the module exists and `-PdisableFirebase=true` is not passed.
- `localization`: Compose Multiplatform resources and localization helpers.
- `thirds/*`: vendored rich text libraries. Treat these as third-party code and avoid edits unless the task is specifically about them.
- `build-logic`: included Gradle build containing Fread convention plugins. Prefer using or extending existing convention plugins over duplicating Gradle setup in modules.

## Architecture Rules

- Keep protocol-specific behavior in its plugin module. Do not leak Mastodon/ActivityPub, Bluesky, RSS, or Firebase implementation details into generic `framework` or `commonbiz` modules unless the abstraction is intentionally shared.
- Put cross-provider status contracts and models in `bizframework/status-provider`; put reusable UI rendering in `commonbiz/status-ui`; put feature workflows in `feature/*`.
- Use `app-hosting` for composition of the app shell, root navigation, and top-level dependency graph. Keep platform launch wrappers thin in `app` and `iosApp`.
- Prefer `commonMain` for shared behavior. Add `expect` declarations in `commonMain` and `actual` implementations in `androidMain`/`iosMain` only for real platform differences.
- Keep build configuration centralized in `build-logic` and `gradle/libs.versions.toml`. Avoid one-off Gradle logic unless it is truly module-specific.
- Do not add broad dependencies to foundational modules casually. First check whether the dependency belongs in a feature or plugin module.
- Preserve the optional Firebase design: code must still build with `-PdisableFirebase=true` when the changed area does not require Firebase.

## Kotlin And Compose Style

- Use Kotlin idioms already present in the repo: immutable `data class` UI state, sealed interfaces/classes for state variants, extension functions for small reusable operations, and explicit constructor injection.
- Compose functions should be small, state-hoisted where practical, and named with PascalCase when they emit UI.
- Use `remember`, `rememberSaveable`, `derivedStateOf`, `LaunchedEffect`, and `DisposableEffect` deliberately. Avoid hidden mutable state that survives longer than the composable it belongs to.
- Keep ViewModels free of direct platform UI dependencies. Use injected collaborators, `viewModelScope`, and existing helpers such as `launchInViewModel` where appropriate.
- Follow existing formatting: 4-space indentation, trailing commas in multiline calls/classes where local code uses them, and explicit named parameters for readability in Compose calls.
- Prefer existing framework helpers before adding new utilities.
- For user-visible text, use localization resources from `localization/src/commonMain/composeResources` and `LocalizedString` patterns instead of hardcoded strings.
- For UI changes, check Android and iOS source sets for platform-specific implementations before assuming common code is enough.

## Dependency Injection And Navigation

- Use Koin modules following existing `val <name>Module = module { ... }` patterns.
- For platform-specific bindings, keep the common declaration as `expect fun Module.createPlatformModule()` and implement Android/iOS `actual` functions in the matching source set.
- Register new feature/plugin dependencies in the owning module first, then wire them through `app-hosting` only when they must participate in app startup.
- Respect existing Navigation 3 and KRouter patterns. If adding generated KRouter pieces, ensure KSP is configured for all needed KMP targets with `kspAll(...)` and `configureCommonMainKsp()` where applicable.

## Gradle And Dependencies

- Use the version catalog in `gradle/libs.versions.toml` for external dependencies.
- Use existing convention plugins:
  - `fread.android.application` for Android app modules.
  - `fread.kmp.library` and `fread.project.framework.kmp` for KMP library modules.
  - `fread.project.feature.kmp` for feature KMP modules.
  - `fread.compose.multiplatform` for Compose Multiplatform setup.
- If adding or updating dependencies, update the version catalog and relevant lock/config files together.
- Do not commit generated build output from `.gradle/`, `build/`, `.kotlin/`, or module build directories.
- Do not edit Gradle wrapper files unless the task is specifically a Gradle upgrade.

## Testing Expectations

- Add or update focused tests for non-trivial logic changes, especially parsers, URL handling, rich text, status-provider behavior, and feed/RSS transformations.
- Existing test locations include `commonTest`, `androidUnitTest`, and `androidInstrumentedTest`. Place new tests in the same source set as the code path being validated.
- Prefer assertions against complete objects or meaningful rendered/state outputs rather than many field-by-field checks when that improves failure clarity.
- For Compose UI behavior, add focused state/unit coverage when possible and manually verify affected screens if automated UI coverage is not practical.
- Do not run compile/build commands after every task by default. Only run the narrowest relevant test/build command when verification is necessary because the change is risky, broad, requested by the user, or likely to fail without compiler feedback. If verification is skipped or blocked, state that clearly.

## Sensitive Files And Boundaries

- Never create, print, modify, or commit real secrets, signing material, tokens, or production credentials.
- Treat these files as sensitive if present: `keystore.properties`, `*.jks`, `local.properties`, `app/google-services.json`, and `iosApp/iosApp/GoogleService-Info.plist`.
- Do not edit generated or local-tooling directories: `.gradle/`, `.kotlin/`, `build/`, `.idea/`, module build directories, and Gradle caches.
- Do not change package/application IDs, signing config, release versioning, CI release behavior, or store metadata unless the task asks for it.
- Ask before adding new network endpoints, analytics events, push-notification behavior, or persistent schema changes.

## Git And Change Hygiene

- Keep patches scoped to the requested behavior and nearby tests/docs.
- Preserve user changes already present in the worktree. Do not revert unrelated modifications.
- Do not make broad formatting-only changes across the repository.
- When moving files or changing public APIs, update imports, DI registrations, tests, and platform source sets in the same change.
- Summaries should include modified files, verification commands, and any known follow-up work.

## Documentation Notes

- Keep README-facing changes concise and user-oriented.
- Architecture or developer docs should describe actual module responsibilities and commands, not aspirational structure.
- If documenting commands, include exact Gradle invocations and any required flags such as `-PdisableFirebase=true`.

## Context Compaction

When compressing agent context, preserve in priority order:

1. Architecture decisions. Never summarize these away.
2. Modified files and their key changes.
3. Current verification status, pass or fail.
4. Open TODOs and rollback notes.
5. Tool outputs. Keep only pass/fail unless specific output is needed.
