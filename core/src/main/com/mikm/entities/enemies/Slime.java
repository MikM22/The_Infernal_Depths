package com.mikm.entities.enemies;

import com.mikm.entities.Entity;
import com.mikm.entities.animation.EntityActionSpritesheets;
import com.mikm.entities.enemies.states.DashBuildUpState;
import com.mikm.entities.enemies.states.DashingState;
import com.mikm.entities.enemies.states.StandingState;
import com.mikm.entities.enemies.states.WanderingState;

public class Slime extends Entity {
    public final float SPEED = 1;

    public Slime(int x, int y, EntityActionSpritesheets entityActionSpritesheets) {
        super(x, y, entityActionSpritesheets);
    }

    @Override
    public void createStates() {
        standingState = new StandingState(this, 1);
        walkingState = new WanderingState(this, 1);
        detectedPlayerState = new DashingState(this);
        dashBuildUpState = new DashBuildUpState(this);
        standingState.enter();
    }

    @Override
    public float getOriginX() {
        return getBounds().width/2f;
    }

    @Override
    public float getSpeed() {
        return 1;
    }

    @Override
    public int getMaxHp() {
        return 3;
    }
}
