<h1 align="center">This API is currently under heavy development, unstable, and doesn't feature every event in Forge</h1>
<h1>Forge Event API Port</h1>
A <b>partial</b> port of the (Neo)Forge Event API that uses the Fabric Event API
<b><br>Not affilated with the NeoForged project or the Forge Config API Port, both of which this library is based upon!</b>

### Currently added built-in events
<details>

  ### Common
  
  | Event | Ported | Info |
  | :---- | :----: | :----- |
  | BlockEvent.BreakEvent | ✅
  | LivingChangeTargetEvent | ✅
  | LivingDamageEvent | ✅
  | LivingDeathEvent | ✅
  | LivingExperienceDropEvent | ✅
  | LivingTickEvent | ✅
  | MobEffectEvent | ✅ | All Sub-events added

  ### Client
  | Event | Ported | Reason |
  | :---- | :----: | :----- |
  | RenderLevelStageEvent | ✅
  
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
