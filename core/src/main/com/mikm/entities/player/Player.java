package com.mikm.entities.player;


import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.mikm.Vector2Int;
import com.mikm.entities.Entity;
import com.mikm.entities.player.states.PlayerDivingState;
import com.mikm.entities.player.states.PlayerRollingState;
import com.mikm.entities.player.states.PlayerWalkingState;
import com.mikm.entities.player.states.PlayerStandingState;
import com.mikm.rendering.screens.GameScreen;

import java.util.ArrayList;

public class Player extends Entity {
    public static final int playerWidthPixels = 32, playerHeightPixels = 32;
    public final float speed = 2;
    private final boolean noClip = false;

    public final float diveSpeed = 6;
    public final float diveFriction = .3f;
    public final float diveFrictionSpeed = .317f;
    public final float diveStartingSinCount = 1;
    public final float diveEndTimeFrame = 0.2f;

    public final float rollSpeed = 4;
    public final float rollStartingSinCount = 0;
    public final float rollFriction = .3f;
    public final float rollFrictionSpeed = .317f;
    public final float rollEndingTime = 0.35f;
    public final float rollJumpSpeed = .25f;
    public final float rollJumpHeight = 12f;

    public Group group;
    private PlayerHeldItem playerHeldItem;
    private PlayerBackItem playerBackItem;

    public PlayerStandingState standingState;
    public PlayerWalkingState walkingState;
    public PlayerDivingState divingState;
    public PlayerRollingState rollingState;

    public Player(int x, int y, ArrayList<TextureRegion[]> spritesheets) {
        super(x, y, spritesheets);

        createStates();

        standingState.enter();
        createGroup();
    }

    public void setScreen(GameScreen screen) {
        this.screen = screen;
        screen.stage.addActor(group);
    }

    @Override
    public void update() {
        currentState.update();
        currentState.handleInput();
        checkWallCollisions();
        if (InputAxis.isMoving()) {
            direction = new Vector2Int(InputAxis.getHorizontalAxisInt(), InputAxis.getVerticalAxisInt());
        }
        x += xVel;
        y += yVel;
    }

    @Override
    public void render(Batch batch) {
        currentState.animationSet.draw(batch);
    }

    private void createGroup() {
        group = new Group();
        playerBackItem = new PlayerBackItem();
        playerHeldItem = new PlayerHeldItem();
        group.addActor(this);
        group.addActor(playerBackItem);
        group.addActor(playerHeldItem);
    }

    @Override
    public Rectangle getBounds() {
        if (noClip) {
            return new Rectangle(0, 0, 0,0);
        }
        return new Rectangle(x+8, y+9, 16, 15);
    }

    @Override
    public Rectangle getFullBounds() {
        return new Rectangle(x, y, playerWidthPixels, playerHeightPixels);
    }

    private void createStates() {
        walkingState = new PlayerWalkingState(this);
        divingState = new PlayerDivingState(this);
        standingState = new PlayerStandingState(this);
        rollingState = new PlayerRollingState(this);
    }
}
