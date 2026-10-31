# Skinmatics

The official repository for Skinmatics.

## Authors

- [Fraynn](https://fraynn.pages.dev/):
  - Programmer: ProstStuff
  - Artist: Freyya (CallMeFreyya)

---

## [License](./LICENSE.md)

- Code: **MIT License**
- Assets: **All Rights Reserved** unless explicitly stated

---

## Maven

```gradle
repositories {
    exclusiveContent {
        forRepository {
            maven {
                name = "Modrinth"
                url = "https://api.modrinth.com/maven"
            }
        }

        filter {
            includeGroup "maven.modrinth"
        }
    }
}

dependencies {
    implementation "maven.modrinth:utilitary:<version>"
    implementation "maven.modrinth:skinmatics:<version>"
}
```

### Library Versions

Minor versions are excluded (2.0 = 2.0.X; 2.1 = 2.1.X)

| Skinmatics Version | Utilitary Version |
|-------------------:|:------------------|
|                2.0 | 1.0               |
|                2.1 | 1.1               |