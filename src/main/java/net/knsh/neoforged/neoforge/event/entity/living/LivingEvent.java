/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */
package net.knsh.neoforged.neoforge.event.entity.living;

import net.knsh.neoforged.neoforge.event.entity.EntityEvent;
import net.minecraft.world.entity.LivingEntity;

public class LivingEvent extends EntityEvent {
    private final LivingEntity livingEntity;

    public LivingEvent(LivingEntity entity) {
        super(entity);
        livingEntity = entity;
    }

    @Override
    public LivingEntity getEntity() {
        return livingEntity;
    }
}
