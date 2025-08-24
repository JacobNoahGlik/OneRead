# OneRead
One‑time, expiring secrets microservice in Kotlin

```
OneRead/
├─ settings.gradle.kts
├─ build.gradle.kts # root (conventions, detekt, spotless)
├─ .gitignore
├─ Dockerfile
├─ .editorconfig
├─ .detekt.yml
├─ .github/workflows/ci.yml
├─ server/
│ ├─ build.gradle.kts
│ └─ src/
│ ├─ main/kotlin/com/secretdrop/
│ │ ├─ Server.kt
│ │ ├─ Config.kt
│ │ ├─ crypto/Crypto.kt
│ │ ├─ db/DatabaseFactory.kt
│ │ ├─ db/Secrets.kt
│ │ └─ routes/SecretRoutes.kt
│ └─ main/resources/logback.xml
│ └─ test/kotlin/com/secretdrop/
│ ├─ SecretRoutesTest.kt
│ └─ CryptoTest.kt
└─ client/
├─ build.gradle.kts
└─ src/main/kotlin/com/secretdrop/Client.kt
```
