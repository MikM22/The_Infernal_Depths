package com.mikm.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mikm.Vector2Int;
import com.mikm.ExtraMathUtils;
import com.mikm.rendering.Camera;
import com.mikm.rendering.screens.Application;

import static com.mikm.input.InputRaw.controllerMapping;

public class GameInput {

    private static float lastControllerLeftStickAngle;

    public static float getHorizontalAxis() {
        Vector2 normalizedMovementVector = ExtraMathUtils.normalizeAndScale(movementVector());
        return normalizedMovementVector.x;
    }

    public static float getVerticalAxis() {
        Vector2 normalizedMovementVector = ExtraMathUtils.normalizeAndScale(movementVector());
        return normalizedMovementVector.y;
    }

    public static int getHorizontalAxisInt() {
        if (InputRaw.usingController) {
            return InputRaw.controllerXAxisInt();
        }
        return keyboardHorizontalAxisInt();
    }

    public static int getVerticalAxisInt() {
        if (InputRaw.usingController) {
            return InputRaw.controllerYAxisInt();
        }
        return keyboardVerticalAxisInt();
    }

    private static int keyboardHorizontalAxisInt() {
        if (InputRaw.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            return 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            return -1;
        }
        return 0;
    }

    private static int keyboardVerticalAxisInt() {
        if (InputRaw.isKeyPressed(Input.Keys.W) || InputRaw.isKeyPressed(Input.Keys.UP)) {
            return 1;
        }
        if (InputRaw.isKeyPressed(Input.Keys.S) || InputRaw.isKeyPressed(Input.Keys.DOWN)) {
            return -1;
        }
        return 0;
    }

    private static Vector2 movementVector() {
        if (InputRaw.usingController) {
            return new Vector2(InputRaw.controllerXAxis(), InputRaw.controllerYAxis());
        }
        return new Vector2(keyboardHorizontalAxisInt(), keyboardVerticalAxisInt());
    }

    public static boolean isMoving() {
        return getHorizontalAxisInt() != 0 || getVerticalAxisInt() != 0;
    }

    public static boolean isDiveButtonJustPressed() {
        if (InputRaw.usingController) {
            return InputRaw.isControllerButtonJustPressed(controllerMapping.buttonA);
        }
        return Gdx.input.isKeyJustPressed(Input.Keys.SPACE);
    }

    public static boolean isAttackButtonPressed() {
        if (InputRaw.usingController) {
            return InputRaw.isControllerButtonPressed(controllerMapping.buttonR2);
        }
        return Gdx.input.isTouched();
    }

    public static boolean isSwitchButtonJustPressed() {
        if (InputRaw.usingController) {
            return InputRaw.isControllerButtonJustPressed(controllerMapping.buttonB);
        }
        return InputRaw.isKeyJustPressed(Input.Keys.Q);
    }

    public static boolean isTalkButtonJustPressed() {
        return isDiveButtonJustPressed();
    }

    public static Vector2Int getAttackingDirectionInt() {
        if (InputRaw.usingController) {
            return controllerAxisToDirectionVectorInt();
        }
        return mousePositionToDirectionVectorInt();
    }

    public static Vector2 getAttackingVector() {
        if (InputRaw.usingController) {
            if (isControllerRightStickMoving()) {
                return new Vector2(InputRaw.controllerXAxisRight(), InputRaw.controllerYAxisRight()).scl(.5f);
            }
            return Vector2.Zero;
        }
        final float pixelsPerOneDistance = 400;
        return new Vector2(mousePosRelativeToPlayer().x/pixelsPerOneDistance, mousePosRelativeToPlayer().y/pixelsPerOneDistance);
    }

    public static Vector2 mousePosRelativeToPlayer() {
        return new Vector2(InputRaw.mouseXPosition() + Camera.x - Application.player.getCenteredPosition().x - Gdx.graphics.getWidth()/2f*Camera.VIEWPORT_ZOOM,
                InputRaw.mouseYPosition() + Camera.y - Application.player.getCenteredPosition().y - Gdx.graphics.getHeight()/2f * Camera.VIEWPORT_ZOOM);
    }

    public static float getAttackingAngle() {
        if (InputRaw.usingController) {
            if (isControllerRightStickMoving()) {
                return controllerRightStickAngle();
            } else if (isControllerLeftStickMoving()) {
                float controllerAngle = controllerLeftStickAngle();
                lastControllerLeftStickAngle = controllerAngle;
                return controllerAngle;
            } else {
                return lastControllerLeftStickAngle;
            }
        }
        return mouseAngle();
    }


    private static float mouseAngle() {
        return MathUtils.atan2(mousePosRelativeToPlayer().y, mousePosRelativeToPlayer().x);
    }

    private static boolean isControllerLeftStickMoving() {
        return InputRaw.controllerXAxis() != 0 || InputRaw.controllerYAxis() != 0;
    }

    private static boolean isControllerRightStickMoving() {
        return InputRaw.controllerXAxisRight() != 0 || InputRaw.controllerYAxisRight() != 0;
    }

    private static float controllerLeftStickAngle() {
        return MathUtils.atan2(InputRaw.controllerYAxis(), InputRaw.controllerXAxis());
    }

    private static float controllerRightStickAngle() {
        return MathUtils.atan2(InputRaw.controllerYAxisRight(), InputRaw.controllerXAxisRight());
    }

    private static Vector2Int controllerAxisToDirectionVectorInt() {
        return angleToDirectionVectorInt(getAttackingAngle());
    }

    private static Vector2Int mousePositionToDirectionVectorInt() {
        return angleToDirectionVectorInt(mouseAngle());
    }

    private static Vector2Int angleToDirectionVectorInt(float radians) {
        return new Vector2Int(MathUtils.round(MathUtils.cos(radians)), MathUtils.round(MathUtils.sin(radians)));
    }
}
