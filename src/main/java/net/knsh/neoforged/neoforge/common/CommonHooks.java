package net.knsh.neoforged.neoforge.common;

import com.google.common.collect.Lists;
import net.knsh.neoforged.accessors.ForgeBlockState;
import net.knsh.neoforged.accessors.ForgeLevel;
import net.knsh.neoforged.bus.api.ForgeEvent;
import net.knsh.neoforged.neoforge.common.util.BlockSnapshot;
import net.knsh.neoforged.neoforge.event.EventHooks;
import net.knsh.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.knsh.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.knsh.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.knsh.neoforged.neoforge.event.entity.living.LivingEvent;
import net.knsh.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.knsh.neoforged.neoforge.event.level.BlockEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CommonHooks {
    public static boolean isCorrectToolForDrops(@NotNull BlockState state, @NotNull Player player) {
        if (!state.requiresCorrectToolForDrops())
            return EventHooks.doPlayerHarvestCheck(player, state, true);

        return player.hasCorrectToolForDrops(state);
    }

    public static LivingChangeTargetEvent onLivingChangeTarget(LivingEntity entity, LivingEntity originalTarget, LivingChangeTargetEvent.ILivingTargetType targetType) {
        LivingChangeTargetEvent event = new LivingChangeTargetEvent(entity, originalTarget, targetType);
        event = LivingChangeTargetEvent.EVENT.invoker().onLivingChangeTarget(event);
        return event;
    }

    public static LivingDamageEvent onLivingDamage(LivingEntity entity, DamageSource src, float amount) {
        LivingDamageEvent event = new LivingDamageEvent(entity, src, amount);
        event = LivingDamageEvent.EVENT.invoker().onLivingDamage(event);
        return event;
    }

    public static Boolean onLivingDeath(LivingEntity entity, DamageSource src) {
        LivingDeathEvent event = new LivingDeathEvent(entity, src);
        return LivingDeathEvent.EVENT.invoker().onLivingDeath(event).isCanceled();
    }

    public static InteractionResult onItemRightClick(Player player, InteractionHand hand) {
        PlayerInteractEvent.RightClickItem evt = new PlayerInteractEvent.RightClickItem(player, hand);
        evt = PlayerInteractEvent.RightClickItem.EVENT.invoker().onRightClickItem(evt);
        return evt.isCanceled() ? evt.getCancellationResult() : null;
    }

    public static boolean onLivingTick(LivingEntity entity) {
        return LivingEvent.LivingTickEvent.EVENT.invoker().onLivingTickEvent(new LivingEvent.LivingTickEvent(entity)).isCanceled();
    }

    public static int onBlockBreakEvent(Level level, GameType gameType, ServerPlayer entityPlayer, BlockPos pos) {
        // Logic from tryHarvestBlock for pre-canceling the event
        boolean preCancelEvent = false;
        ItemStack itemstack = entityPlayer.getMainHandItem();
        if (!itemstack.isEmpty() && !itemstack.getItem().canAttackBlock(level.getBlockState(pos), level, pos, entityPlayer)) {
            preCancelEvent = true;
        }

        if (gameType.isBlockPlacingRestricted()) {
            if (gameType == GameType.SPECTATOR)
                preCancelEvent = true;

            if (!entityPlayer.mayBuild()) {
                if (itemstack.isEmpty() || !itemstack.hasAdventureModeBreakTagForBlock(level.registryAccess().registryOrThrow(Registries.BLOCK), new BlockInWorld(level, pos, false)))
                    preCancelEvent = true;
            }
        }

        // Tell client the block is gone immediately then process events
        if (level.getBlockEntity(pos) == null) {
            entityPlayer.connection.send(new ClientboundBlockUpdatePacket(pos, level.getFluidState(pos).createLegacyBlock()));
        }

        // Post the block break event
        BlockState state = level.getBlockState(pos);
        BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(level, pos, state, entityPlayer);
        event.setCanceled(preCancelEvent);
        event = BlockEvent.BreakEvent.EVENT.invoker().onBreakEvent(event);

        // Handle if the event is canceled
        if (event.isCanceled()) {
            // Let the client know the block still exists
            entityPlayer.connection.send(new ClientboundBlockUpdatePacket(level, pos));

            // Update any tile entity data for this block
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity != null) {
                Packet<?> pkt = blockEntity.getUpdatePacket();
                if (pkt != null) {
                    entityPlayer.connection.send(pkt);
                }
            }
        }
        return event.isCanceled() ? -1 : 0;
    }

    public static void dropXpForBlock(BlockState state, ServerLevel level, BlockPos pos, ItemStack stack) {
        int fortuneLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_FORTUNE, stack);
        int silkTouchLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, stack);
        int exp = ((ForgeBlockState) state).getExpDrop(level, level.random, pos, fortuneLevel, silkTouchLevel);
        if (exp > 0)
            state.getBlock().popExperience(level, pos, exp);
    }

    public static boolean onCropsGrowPre(Level level, BlockPos pos, BlockState state, boolean def) {
        BlockEvent.CropGrowEvent.Pre ev = new BlockEvent.CropGrowEvent.Pre(level, pos, state);
        ev = BlockEvent.CropGrowEvent.Pre.EVENT.invoker().onEvent(ev);
        return (ev.getResult() == ForgeEvent.Result.ALLOW || (ev.getResult() == ForgeEvent.Result.DEFAULT && def));
    }

    public static void onCropsGrowPost(Level level, BlockPos pos, BlockState state) {
        BlockEvent.CropGrowEvent.Post.EVENT.invoker().onEvent(new BlockEvent.CropGrowEvent.Post(level, pos, state, level.getBlockState(pos)));
    }

    public static boolean onFarmlandTrample(Level level, BlockPos pos, BlockState state, float fallDistance, Entity entity) {
        BlockEvent.FarmlandTrampleEvent event = new BlockEvent.FarmlandTrampleEvent(level, pos, state, fallDistance, entity);
        event = BlockEvent.FarmlandTrampleEvent.EVENT.invoker().onEvent(event);
        return !event.isCanceled();
    }

    public static InteractionResult onPlaceItemIntoWorld(@NotNull UseOnContext context) {
        ItemStack itemstack = context.getItemInHand();
        Level level = context.getLevel();

        Player player = context.getPlayer();
        if (player != null && !player.getAbilities().mayBuild && !itemstack.hasAdventureModePlaceTagForBlock(level.registryAccess().registryOrThrow(Registries.BLOCK), new BlockInWorld(level, context.getClickedPos(), false)))
            return InteractionResult.PASS;

        // handle all placement events here
        Item item = itemstack.getItem();
        int size = itemstack.getCount();
        CompoundTag nbt = null;
        if (itemstack.getTag() != null)
            nbt = itemstack.getTag().copy();

        if (!(itemstack.getItem() instanceof BucketItem)) // if not bucket
            ((ForgeLevel) level).setCaptureBlockSnapshots(true);

        ItemStack copy = itemstack.copy();
        InteractionResult ret = itemstack.getItem().useOn(context);
        if (itemstack.isEmpty())
            //EventHooks.onPlayerDestroyItem(player, copy, context.getHand());

        ((ForgeLevel) level).setCaptureBlockSnapshots(false);

        if (ret.consumesAction()) {
            // save new item data
            int newSize = itemstack.getCount();
            CompoundTag newNBT = null;
            if (itemstack.getTag() != null) {
                newNBT = itemstack.getTag().copy();
            }
            @SuppressWarnings("unchecked")
            List<BlockSnapshot> blockSnapshots = (List<BlockSnapshot>) ((ForgeLevel)level).getCapturedBlockSnapshots().clone();
            ((ForgeLevel)level).getCapturedBlockSnapshots().clear();

            // make sure to set pre-placement item data for event
            itemstack.setCount(size);
            itemstack.setTag(nbt);

            Direction side = context.getClickedFace();

            boolean eventResult = false;
            if (blockSnapshots.size() > 1) {
                eventResult = EventHooks.onMultiBlockPlace(player, blockSnapshots, side);
            } else if (blockSnapshots.size() == 1) {
                eventResult = EventHooks.onBlockPlace(player, blockSnapshots.get(0), side);
            }

            if (eventResult) {
                ret = InteractionResult.FAIL; // cancel placement
                // revert back all captured blocks
                for (BlockSnapshot blocksnapshot : Lists.reverse(blockSnapshots)) {
                    ((ForgeLevel) level).setRestoringBlockSnapshots(true);
                    blocksnapshot.restore(true, false);
                    ((ForgeLevel) level).setRestoringBlockSnapshots(false);
                }
            } else {
                // Change the stack to its new content
                itemstack.setCount(newSize);
                itemstack.setTag(newNBT);

                for (BlockSnapshot snap : blockSnapshots) {
                    int updateFlag = snap.getFlag();
                    BlockState oldBlock = snap.getReplacedBlock();
                    BlockState newBlock = level.getBlockState(snap.getPos());
                    newBlock.onPlace(level, snap.getPos(), oldBlock, false);

                    //level.markAndNotifyBlock(snap.getPos(), level.getChunkAt(snap.getPos()), oldBlock, newBlock, updateFlag, 512);
                }
                if (player != null)
                    player.awardStat(Stats.ITEM_USED.get(item));
            }
        }
        ((ForgeLevel) level).getCapturedBlockSnapshots().clear();

        return ret;
    }
}
