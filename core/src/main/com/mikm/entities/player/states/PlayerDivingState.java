package com.mikm.entities.player.states;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mikm.Vector2Int;
import com.mikm.ExtraMathUtils;
import com.mikm.entities.animation.AnimationManager;
import com.mikm.entities.animation.DirectionalAnimationSet;
import com.mikm.entities.player.PlayerAnimationNames;
import com.mikm.input.InputAxis;
import com.mikm.entities.player.Player;
import com.mikm.entities.State;

public class PlayerDivingState extends State {
    private final Player player;
    private Vector2 diveForce = new Vector2();
    private Vector2Int diveDirection = new Vector2Int();
    private float sinCounter;

    public PlayerDivingState(Player player) {
        super(player);
        this.player = player;
        DirectionalAnimationSet directionalAnimationSet = new DirectionalAnimationSet(.1f, Animation.PlayMode.NORMAL,
                player.spritesheets, 5, PlayerAnimationNames.DIVE_DOWN.ordinal());
        animationManager = new AnimationManager(player, directionalAnimationSet);
    }

    @Override
    public void enter() {
        super.enter();
        player.xVel = 0;
        player.yVel = 0;
        sinCounter = player.DIVE_STARTING_SIN_COUNT;

        diveForce = new Vector2(player.DIVE_SPEED * MathUtils.sin(sinCounter) * InputAxis.getHorizontalAxis(),
                player.DIVE_SPEED * MathUtils.sin(sinCounter) * InputAxis.getVerticalAxis());
        diveDirection = new Vector2Int(player.direction.x, player.direction.y);
        super.update();
    }

    @Override
    public void update() {
        player.xVel = diveForce.x;
        player.yVel = diveForce.y;
        setDiveForce();
    }

    @Override
    public void checkForStateTransition() {
        if (InputAxis.isDiveButtonPressed() && sinCounter > MathUtils.PI - player.DIVE_END_TIME_FRAME) {
            player.rollingState.enter();
        }
    }

    private void setDiveForce() {
        if (sinCounter < MathUtils.PI) {
            sinCounter += player.DIVE_FRICTION - (player.DIVE_FRICTION_SPEED * player.DIVE_FRICTION * sinCounter);
        } else {
            player.rollingState.enter();
            return;
        }
        if (sinCounter >= MathUtils.PI) {
            sinCounter = MathUtils.PI;
        }

        Vector2 normalizedDiveDirection = ExtraMathUtils.normalizeAndScale(diveDirection);
        diveForce = new Vector2(player.DIVE_SPEED * MathUtils.sin(sinCounter) * normalizedDiveDirection.x,
                player.DIVE_SPEED * MathUtils.sin(sinCounter) * normalizedDiveDirection.y);
    }
}
