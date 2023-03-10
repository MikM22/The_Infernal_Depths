package com.mikm.entities;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Intersector;
import com.mikm.input.GameInput;
import com.mikm.rendering.screens.Application;

public class Rope extends InanimateEntity {

    public Rope(float x, float y) {
        super(x, y);
    }

    @Override
    public void update() {
        if (Intersector.overlaps(Application.player.getHitbox(), getHitbox()) && GameInput.isTalkButtonJustPressed()) {
            Application.caveScreen.increaseFloor(-1);
        }
    }

    @Override
    public void draw(Batch batch) {
        batch.draw(Application.testTexture, x, y);
    }
}
