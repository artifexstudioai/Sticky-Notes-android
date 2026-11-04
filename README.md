# Sticky Notes — Android app

This is the native Android project exported from Google AI Studio. It uses Kotlin, Jetpack Compose, Room, and Android App Widgets.

## Build an APK with GitHub Actions

The included workflow is `.github/workflows/android-debug-apk.yml`. It builds a **debug APK** whenever you push changes to `main` or `master`, and can also be started manually.

### 1. Create a GitHub repository

1. On GitHub, create a new repository (for example, `sticky-notes-android`).
2. Extract the contents of this ZIP on your computer or phone.
3. Upload the extracted project contents to the repository's root. Make sure `.github/workflows/android-debug-apk.yml` is included. Do not upload the ZIP as a single file.
4. Commit the files to the `main` branch.

### 2. Run the build

1. Open the repository on GitHub.
2. Select **Actions**.
3. Choose **Build Sticky Notes Android APK**.
4. If the workflow has not run automatically, select **Run workflow** and confirm.
5. Wait for the workflow to finish with a green check mark.

### 3. Download the APK

1. Open the completed workflow run.
2. Scroll to **Artifacts** at the bottom.
3. Download **Sticky-Notes-debug-apk**.
4. Extract the downloaded artifact ZIP. It contains `app-debug.apk`.

### 4. Install on an Android phone

1. Transfer `app-debug.apk` to your phone, or download it directly from the GitHub Actions artifact.
2. Open the APK file using the Files app.
3. If Android asks, allow your browser or Files app to **Install unknown apps**.
4. Tap **Install**, then open Sticky Notes.

## Debug signing / keystore

The app's debug build is configured to use a project-root file named `debug.keystore` with the standard Android debug alias and passwords. The GitHub Actions workflow generates that file at build time using `keytool`; it is intentionally ignored by Git and is not committed to the repository.

This is a **debug signing key**, not a Play Store release key. The workflow creates a fresh debug key for each clean GitHub-hosted run. That is suitable for testing and installing the APK, but a later APK signed with a different debug key may not install as an update over the previous one. Uninstall the old test build first (this removes its app data), or preserve and securely manage one debug keystore if you need seamless test updates.

## Notes about API keys

The project includes `.env.example` for the optional Gemini API key. Do not commit a real API key to the repository. If the app's AI features require it, add the key as a GitHub Actions repository secret and adjust the workflow to create `.env` from that secret before the Gradle build. A normal debug build does not require committing `.env`.

## Build details

- Gradle: 9.3.1
- Java: 17
- Android SDK platform: API 36 extension 1
- Gradle task: `assembleDebug`
- APK output: `app/build/outputs/apk/debug/app-debug.apk`

The GitHub Actions workflow builds the APK; it does not publish the app to Google Play.
