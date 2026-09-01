# Skinmatics

## About
An avatar customization mod focusing in improving Minecraft's own player model than changing it for a new one.
Features include:

### Skin, Cape, And Elytra Texture Switching
Allow the change of the player's skin, cape, and elytra texture without relying on
Mojang's own avatar authentication.

Note: Skinmatics is a client-only mod. Your Skinmatics avatar won't be synced to others and vice versa.

### Time-Frame-Like Animated Textures

### Emissive Textures
Easily add custom glowing textures to your skin, cape, and elytra!

### Dynamic Eyes
Immerse your characters with dynamic eyes.
The eyes follow where you look,
and reacts different when using an item.
Animated and emissive textures are supported to!

### Paper Doll
Bedrock-like paper doll with extra configurations,
view your character without going to third-person-view!

### Dynamic Light Support **[EXPERIMENTAL]**
Want your character to emit light?</br>
Well Skinmatics provide just that!

LambDynamicLight is required to use this feature.
Otherwise, it remains unfunctional.

### PBR Support **[EXPERIMENTAL]**
Emissive texture too strong?</br>
Want to see how rough and metallic your character is?</br>
Well Skinmatics also provide that.

Iris Shaders with a PBR-supported shader is required to use this feature.
Otherwise, it remains unfunctional.

## Information

### Dependency
Fabric API and Utilitary 1.0.0 or above is required for Skinmatics to run.

### Those Before Skinmatics 2.0.0
A lot of change has been made, in-fact it's an almost full-blown rework on almost every aspect. Please note that:
- Texture remain the same as 1.0.0
- 'profile' is **deprecated**, replaced by '**avatar**'
- The profile directory at `minecraft/config/skinmatics/profiles` is deprecated, all files under it remain untouched
  - The avatar directory at `minecraft/config/skinmatics/avatars`
- The structure of the **config and avatar files** have severely been changed with Utilitary 1.0.0 config system
  - Your profile is **deprecated** and **will not be loaded** even if you moved them to the avatar directory
  - Config file is now `.json5` instead of `json`, meaning Skinmatics **won't read the old file and create a new one**

## FAQ

1. Can I use Skinmatics on a server?</br>
Yes, Skinmatics is a client-side mod,
you should still be able to connect to any servers even with/without Skinmatics.
Please note that Skinmatics is a **client-only mod, do not put this in a server**.


2. Will Skinmatics be backported to [Minecraft version]?</br>
We don't have plan for backports. However, we're open for a community-managed
version of Skinmatics, and is looking for a maintainer.


3. Will Skinmatics be available on Forge/NeoForge?</br>
We don't have plan to port to NeoForge or Forge (for older versions),
as it would be a hassle rewriting and maintaining everything in one go.
As we said in question 2,
we're open for a community-managed version of Skinmatics,
and looking for a maintainer for it.</br></br>
If you're interested in maintaining other Minecraft versions or mod loader,
or anything related with Skinmatics development,
please DM me in Discord -ProstStuff


4. Can I include Skinmatics in a modpack?</br>
Yes, you can include it in a modpack. Provide credit and project link (e.g. Modrinth) as needed.