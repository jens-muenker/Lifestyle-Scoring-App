# Lifestyle Scoring App (Android)

Eine Android-App zur schnellen und fehlerarmen Punkteberechnung für das Kartenspiel **Lifestyle**.

## Download

- Google Play: [Lifestyle Scoring App](https://play.google.com/store/apps/details?id=com.frosch2010.lifestyle_scoring_app&gl=DE)

## Features

- Karten per Kamera erfassen (Text Recognition / OCR)
- Punkte automatisch berechnen statt manuell zählen
- Mehrere Spieler in einer Runde unterstützen
- Reduzierte Fehlerquote bei der Auswertung

## Tech Stack

- **Plattform:** Android (minSdk 24, targetSdk 37, compileSdk 37)
- **Sprache & Build:** Kotlin, Gradle (Kotlin DSL)
- **Architektur:** MVVM mit klarer Layer-Trennung (UI, Domain, Data)
- **DI:** Hilt
- **Kamera/OCR:** CameraX, Google ML Kit Text Recognition
- **Tests:** JUnit4, Robolectric, Espresso, Mockito
- **Code Coverage:** JaCoCo

## Projektstruktur

- `app/` - Android-App-Modul
- `docs/` - Architektur-/Migrationsdokumentation
- `privacy_policy/` - Datenschutzerklärung (DE/EN)

## Lizenz

Dieses Projekt steht unter der [Apache License 2.0](LICENSE).
