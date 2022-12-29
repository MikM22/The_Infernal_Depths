package com.mikm.entities.player.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mikm.entities.player.ANIMATIONS;
import com.mikm.entities.player.InputAxis;
import com.mikm.entities.player.Player;

public class DivingState extends State {
    private Vector2 diveForce = new Vector2();
    private Vector2 diveDirection = new Vector2();
    private float sinCounter;

    public DivingState(Player player) {
        super(player);
    }

    @Override
    public void enter() {
        super.enter();
        player.xVel = 0;
        player.yVel = 0;
        animationTime = 0;
        sinCounter = player.diveStartingSinCount;

        diveForce = new Vector2(player.diveSpeed * MathUtils.sin(sinCounter) * InputAxis.getHorizontalAxis() * InputAxis.movementVectorNormalizationMultiplier(),
                player.diveSpeed * MathUtils.sin(sinCounter) * InputAxis.getVerticalAxis() * InputAxis.movementVectorNormalizationMultiplier());
        diveDirection = new Vector2(player.direction.x, player.direction.y);
    }

    @Override
    void createAnimations() {
        for (int i = 0; i < 8; i++) {
            int indexOfAnimation = i + ANIMATIONS.Character_RollDown.ordinal();
            animations.add(new Animation<>(.07f, player.spritesheets.get(indexOfAnimation)));
            animations.get(i).setPlayMode(Animation.PlayMode.NORMAL);
        }
    }


    @Override
    public void update() {
        super.update();
        player.xVel = diveForce.x;
        player.yVel = diveForce.y;
        setDiveForce();
    }

    @Override
    public void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.E) && sinCounter > MathUtils.PI - player.diveEndTimeFrame) {
            player.walkingState.enter();
        }
    }

    private void setDiveForce() {
        if (sinCounter < MathUtils.PI) {
            sinCounter += player.diveFriction - (player.diveFrictionSpeed * player.diveFriction * sinCounter);
        } else {
            player.walkingState.enter();
            return;
        }
        if (sinCounter >= MathUtils.PI) {
            sinCounter = MathUtils.PI;
        }
        diveForce = new Vector2(player.diveSpeed * MathUtils.sin(sinCounter) * diveDirection.x * InputAxis.movementVectorNormalizationMultiplier(),
                player.diveSpeed * MathUtils.sin(sinCounter) * diveDirection.y * InputAxis.movementVectorNormalizationMultiplier());
    }
}
