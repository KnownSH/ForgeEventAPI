<h1 align="center">This API is currently under heavy development, unstable, and doesn't feature every event in Forge</h1>
<img src="https://cdn.discordapp.com/attachments/650570958343634969/1182596427432595466/FEAPIP-logo.png?ex=658545bf&is=6572d0bf&hm=3eaa2a44ea3ba39e4754c6205cc7b41fe6382068d740bbed5628bb1ceb63be11" aligh="center">Forge Event API Port</href>
A <b>partial</b> port of the (Neo)Forge Event API that uses the Fabric Event API.
<b><br>Not affilated with the NeoForged project or the Forge Config API Port, both of which this library is based upon!</b>
<br>

### This API is very [Mixin intensive](https://github.com/KnownSH/forgeeventapi/tree/main/src/main/java/net/knsh/neoforged/mixin), leading to possible mod conflicts, I will try and fix these as they happen.

### Installing Via Gradle
<details>

  Forge Event API Port uses the jitpack Maven repositiory
  ```gradle
  repositories {
    maven { url = "https://jitpack.io" }
  }
  ```

  If you want to include FEAPI as a Jar-in-jar
  ```gradle
  dependencies {
    include modApi("com.github.knownsh:forgeeventapi:VERSION")
  }
  ```
</details>

### Currently added built-in events
<details>

  ### Common

  | Emoji | Meaning |
  | :---: | :------ |
  | ✅ | Fully Added |
  | ✴️ | Partially Added |
  | ❌ | Not added |

  The primary reason why some events are not ported to fabric (yet) is primarly due to how they require large overwrites of Minecraft source code, which is very likely to cause compatibility with other mods to completely break.
  
  | Event | Ported | Sub-events | Reason |
  | :---- | :----: | :-----: | :-----: |
  | BlockEvent | ✴️ | ✴️ | **BlockToolModificationEvent**, **CreateFluidSourceEvent** not added due to intrusiveness, possible full rewrite eventually.
  | CustomizeGuiOverlayEvent | ✅ | ✴️ | **Chat** event not added due to intrusiveness.
  | ExplosionEvent | ✅ | ✅
  | LivingEvent | ✅ | ✅
  | LivingChangeTargetEvent | ✅
  | LivingDamageEvent | ✅
  | LivingDeathEvent | ✅
  | LivingExperienceDropEvent | ✅
  | LivingTickEvent | ✅
  | MobEffectEvent | ✅ | ✅
  | MobSpawnEvent | ✅ | ✅

  ### Client
  | Event | Ported | Sub-events | Reason |
  | :---- | :----: | :-----: | :-----: |
  | RenderLevelStageEvent | ✅ | ✅
  
</details>

### FAQ
<details>

  #### How does this work?
  
  The API essentially adapts the NeoForge Bus library to make the SubscribeEvent annotation system compatible with the Fabric Event API. This was achieved by creating a modified version of the NeoForge Bus library, specifically tailored to seamlessly integrate with Fabric's event system. This allows you to use the familiar SubscribeEvent annotation system in the context of the Fabric Event API.
  <br><br>
  #### Why was this created?
  
  When you're porting a mod from Forge to Fabric, you'll notice that Fabric's event system doesn't have all the events that Forge provides. This means you have to do additional work using mixins to replicate similar functionalities that were present in Forge.

  Moreover, in Fabric's event handling API, the events often send immutable parameters. This becomes a challenge when you need to make adjustments to certain values. For example, if you want to change the amount of experience a player gets when mining a pre-existing ore. This difference in how events are handled can complicate the process of adapting a mod to Fabric.
  
  
</details>
