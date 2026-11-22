# Skinmatics
[Documentation](https://fraynn.pages.dev/skinmatics/intro) | [Modrinth](https://modrinth.com/mod/skinmatics) | [GitHub](https://github.com/ProstStuff/Skinmatics)<br/>
Depends on **Utilitary** - [Modrinth](https://modrinth.com/mod/utilitary) | [GitHub](https://github.com/ProstStuff/Utilitary)

The official repository for Skinmatics Minecraft avatar mod.

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
    maven {
		name = "JitPack"
		url = uri('https://jitpack.io')
	}
}

dependencies {
    // In Common gradle
    compileOnly "com.github.ProstStuff:Utilitary:<version>+<minecraft_version>-common"
    compileOnly "com.github.ProstStuff:Skinmatics:<version>+<minecraft_version>-common"
    // In Fabric gradle
    implementation "com.github.ProstStuff:Utilitary:<version>+<minecraft_version>-fabric"
    implementation "com.github.ProstStuff:Skinmatics:<version>+<minecraft_version>-fabric"
    // In NeoForge gradle
    implementation "com.github.ProstStuff:Utilitary:<version>+<minecraft_version>-neoforge"
    implementation "com.github.ProstStuff:Skinmatics:<version>+<minecraft_version>-neoforge"
}
```

---

### Library Versions

| Skinmatics Version | Utilitary Version |
|-------------------:|:------------------|
|              2.0.X | 1.0.X             |
|              2.1.X | 1.1.X             |
|              2.2.X | 1.2.X             |