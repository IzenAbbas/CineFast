# CineFAST

**Latest commit:** [`b2def67`](https://github.com/IzenAbbas/CineFast/commit/b2def671054339761a43b3fec1607b9cd6e17821) — onboarding_rectangles.png issue resolved + Readme updated. (2026-05-20)

CineFAST is an Android movie-booking app built with Java, AndroidX, Material components, and Firebase. It guides users through a simple cinema experience: splash screen, onboarding, account creation or login, movie browsing, seat selection, snack ordering, and booking confirmation.

The project is organized as a single Android application module and includes a prebuilt APK artifact for quick installation and review.

---

## What the app does

CineFAST is designed to simulate a streamlined ticket-booking flow for a cinema app:

1. **Splash screen** introduces the app with a short animated logo.
2. **Onboarding** presents the first-time app entry screen.
3. **Authentication** supports email/password login, email sign-up, password reset, and Google sign-in.
4. **Home experience** shows movies and lets users move between home sections from the navigation drawer.
5. **Booking flow** covers movie selection, seat selection, snack selection, and ticket summary.
6. **Booking history** lets users review previous or last bookings.

---

## Key features

- Firebase Authentication for email/password and Google-based sign-in
- Firebase Realtime Database for saving user profile data after sign-up
- Firebase Firestore support in the app dependencies
- Movie browsing and paging/list screens
- Seat selection UI with booked/available/yours seat states
- Snack selection and cart-style flow
- Booking summary / ticket screen
- Navigation drawer for Home, My Bookings, Last Booking, and Logout
- Session handling so signed-in users can return to the main app faster
- Asset-backed movie data stored locally in `app/src/main/assets/movies.json`

---

## Screens and main classes

The most important activities and fragments are:

- `SplashActivity` — animated launch screen
- `OnboardingActivity` — first-run introduction
- `LoginActivity` — email/password login and Google sign-in
- `SignupActivity` — new user registration with email verification
- `MainActivity` — navigation-drawer host and authenticated app container
- `HomeFragment` — home dashboard and booking entry point
- `NowShowingFragment` — currently showing movies
- `ComingSoonFragment` — upcoming titles
- `SeatSelectionFragment` — seat booking interface
- `SnacksFragment` — snack ordering screen
- `MyBookingsFragment` — saved bookings list
- `TicketSummaryActivity` — booking confirmation / summary

Supporting packages include:

- `com.example.myapplication.movie` — movie model, adapters, repository, and image loading helpers
- `com.example.myapplication.data` — booking, snack, and database helper classes

---

## Repository structure

```text
Assignment1/
├─ app/
│  ├─ src/main/
│  │  ├─ java/com/example/myapplication/
│  │  │  ├─ activities, fragments, adapters, and session helpers
│  │  │  ├─ movie/                # movie-related UI and data classes
│  │  │  └─ data/                 # booking and snack data classes
│  │  ├─ assets/movies.json       # local movie catalogue data
│  │  ├─ res/                     # layouts, drawables, animations, menus, and values
│  │  └─ AndroidManifest.xml
│  ├─ google-services.json        # Firebase configuration
│  └─ build.gradle.kts
├─ build.gradle.kts               # root Gradle configuration
├─ settings.gradle.kts            # project/module setup
├─ gradle/                        # Gradle wrapper and version management
└─ apk/                          # prebuilt APK artifact in the repository
```

### Notable resources

- `app/src/main/res/layout/` — activity and fragment layouts
- `app/src/main/res/drawable/` — custom buttons, seat states, and icons
- `app/src/main/res/anim/` — splash logo animations
- `app/src/main/res/menu/drawer_menu.xml` — navigation drawer menu
- `app/src/main/res/values/strings.xml` — app text and user-facing messages

---

## Tech stack

- **Language:** Java
- **Build system:** Gradle Kotlin DSL
- **UI:** AndroidX, Material Components, ConstraintLayout, ViewPager2, RecyclerView
- **Backend services:** Firebase Auth, Realtime Database, Firestore, Analytics
- **Sign-in support:** Android Credential Manager + Google ID
- **Minimum SDK:** 24
- **Target SDK:** 36
- **Compile SDK:** 36

---

## Setup requirements

Before building the app, make sure you have:

- Android Studio installed
- Android SDK 24+ available locally
- A working internet connection for Gradle and Firebase dependencies
- A valid Firebase project connected through `app/google-services.json`
- A Google OAuth client configured for Google sign-in

> Tip: If you fork or clone this project, keep the Firebase configuration files in sync with your own Firebase project settings.

---

## How to run the project

### Option 1: Open in Android Studio

1. Open the root folder `Assignment1` in Android Studio.
2. Wait for Gradle sync to finish.
3. Select a device or emulator.
4. Click **Run**.

### Option 2: Build from the command line

From the project root:

```powershell
./gradlew assembleDebug
```

For a release build:

```powershell
./gradlew assembleRelease
```

If you are on Windows PowerShell and the wrapper script needs to be invoked directly, use:

```powershell
.\gradlew.bat assembleDebug
.\gradlew.bat assembleRelease
```

---

## APK artifact

A prebuilt APK is included with the repository for quick installation and testing.

- Expected location in the repository: `apk/CineFast.apk`
- If you build locally, Gradle will place APK outputs under `app/build/outputs/apk/`

### Important note about signing

The release build configuration in this project does not define a custom signing configuration. If you generate your own APK for distribution, sign it before sharing it outside of local testing.

---

## Firebase and authentication notes

- Login uses Firebase Authentication.
- Sign-up creates the account, stores the user profile in the Realtime Database, and sends a verification email.
- The login screen also supports Google sign-in through Credential Manager.
- Authenticated users are redirected into `MainActivity`; unauthenticated users are sent back to the login flow.

---

## Assets and sample media

The repository includes screenshots and sample media assets that can be used in documentation or demo pages:

- `app/my tickets.jpg`
- `app/order details.jpg`
- `app/Snacks and Drinks.jpg`

These files are useful if you want to extend this README with visuals of the booking flow.

---

## Contributing / extending

If you want to extend CineFAST, good next steps are:

- add more movie categories or filtering options
- connect snack orders to a remote backend
- improve validation and error handling
- add payment simulation or real payment integration
- add more screenshots to the README for a portfolio-style presentation

---

## License

No explicit license file is included in the repository. If you plan to reuse or publish the project, add a license that matches your intended usage.

