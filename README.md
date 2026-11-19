# ZodiacHue

**ZodiacHue** is a Kotlin Android app that combines a daily zodiac **Color of the Day** guide, curated outfit inspiration, and a personal reflection diary inside a cohesive dark cosmic UI.

## Features

- **Accounts & Session Persistence**: Sign up with automatic zodiac sign detection from your birth date (including leap-year validation), SHA-256 password hashing, password strength feedback, "Keep me signed in" auto-login, and password updates.
- **Color of the Day & Zodiac Explorer**: Personalized greeting, color swatch & copyable hex code, uncropped zodiac infographic card, structured **Mood Influence / Aesthetic to Wear / Colors to Avoid** breakdown, daily zodiac reading, and a bottom-sheet **Explore Zodiac Hues** switcher to preview all 12 signs.
- **Outfit Inspiration Moodboard**: Preview outfit aesthetics fullscreen and bookmark looks per user to your profile gallery.
- **Personal Diary**: Create, view, edit, search, share, and delete mood-tagged diary entries with timestamps, word count, and one-tap quote/reading reflection prompts.
- **Profile & Settings**: Editable name and birth date (auto-updating zodiac sign), persistent profile photo picker, live note/outfit stats, and safe logout.

## Tech Stack

- **Language**: Kotlin (`compileSdk = 35`, `minSdk = 24`, `targetSdk = 34`)
- **UI**: AndroidX AppCompat, Material Components, RecyclerView, CardView, BottomSheetDialog
- **Persistence & Media**: `SharedPreferences` + `Gson`, `Glide`

## Running Locally

1. Open `C:\Users\Dillan\ZodiacHue` in Android Studio (or configure `sdk.dir` in `local.properties`).
2. Run unit tests and build the debug APK:
   ```bash
   ./gradlew testDebugUnitTest assembleDebug
   ```
