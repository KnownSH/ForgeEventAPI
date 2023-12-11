/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */
package net.neoforged.neoforge.event.entity;

import net.neoforged.bus.api.ForgeEvent;
import net.minecraft.core.SectionPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;

public abstract class EntityEvent extends ForgeEvent {
    private final Entity entity;

    public EntityEvent(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }

    public static class EntityConstructing extends EntityEvent {
        public EntityConstructing(Entity entity) {
            super(entity);
        }
    }

    public static class EnteringSection extends EntityEvent {

        private final long packedOldPos;
        private final long packedNewPos;

        public EnteringSection(Entity entity, long packedOldPos, long packedNewPos) {
            super(entity);
            this.packedOldPos = packedOldPos;
            this.packedNewPos = packedNewPos;
        }

        /**
         * A packed version of the old section's position. This is to be used with the various methods in {@link net.minecraft.core.SectionPos},
         * such as {@link net.minecraft.core.SectionPos#of(long)} or {@link net.minecraft.core.SectionPos#x(long)} to avoid allocation.
         *
         * @return the packed position of the old section
         */
        public long getPackedOldPos() {
            return packedOldPos;
        }

        /**
         * A packed version of the new section's position. This is to be used with the various methods in {@link net.minecraft.core.SectionPos},
         * such as {@link net.minecraft.core.SectionPos#of(long)} or {@link net.minecraft.core.SectionPos#x(long)} to avoid allocation.
         *
         * @return the packed position of the new section
         */
        public long getPackedNewPos() {
            return packedNewPos;
        }

        /**
         * @return the position of the old section
         */
        public SectionPos getOldPos() {
            return SectionPos.of(packedOldPos);
        }

        /**
         * @return the position of the new section
         */
        public SectionPos getNewPos() {
            return SectionPos.of(packedNewPos);
        }

        /**
         * Whether the chunk has changed as part of this event. If this method returns false, only the Y position of the
         * section has changed.
         */
        public boolean didChunkChange() {
            return SectionPos.x(packedOldPos) != SectionPos.x(packedNewPos) || SectionPos.z(packedOldPos) != SectionPos.z(packedNewPos);
        }

    }

    public static class Size extends EntityEvent {
        private final Pose pose;
        private final EntityDimensions oldSize;
        private EntityDimensions newSize;
        private final float oldEyeHeight;
        private float newEyeHeight;

        public Size(Entity entity, Pose pose, EntityDimensions size, float defaultEyeHeight) {
            this(entity, pose, size, size, defaultEyeHeight, defaultEyeHeight);
        }

        public Size(Entity entity, Pose pose, EntityDimensions oldSize, EntityDimensions newSize, float oldEyeHeight, float newEyeHeight) {
            super(entity);
            this.pose = pose;
            this.oldSize = oldSize;
            this.newSize = newSize;
            this.oldEyeHeight = oldEyeHeight;
            this.newEyeHeight = newEyeHeight;
        }

        public Pose getPose() {
            return pose;
        }

        public EntityDimensions getOldSize() {
            return oldSize;
        }

        public EntityDimensions getNewSize() {
            return newSize;
        }

        public void setNewSize(EntityDimensions size) {
            setNewSize(size, false);
        }

        /**
         * Set the new size of the entity. Set updateEyeHeight to true to also update the eye height according to the new size.
         */
        public void setNewSize(EntityDimensions size, boolean updateEyeHeight) {
            this.newSize = size;
            if (updateEyeHeight) {
                //this.newEyeHeight = this.getEntity().getEyeHeightAccess(this.getPose(), this.newSize);
            }
        }

        public float getOldEyeHeight() {
            return oldEyeHeight;
        }

        public float getNewEyeHeight() {
            return newEyeHeight;
        }

        public void setNewEyeHeight(float newHeight) {
            this.newEyeHeight = newHeight;
        }
    }
}