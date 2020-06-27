/*
 * Copyright (c) 2020 Taner Sener
 *
 * This file is part of MobileFFmpeg.
 *
 * MobileFFmpeg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MobileFFmpeg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MobileFFmpeg.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.arthenica.mobileffmpeg;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Surface;

import com.arthenica.mobileffmpeg.player.AudioManager;
import com.arthenica.mobileffmpeg.player.ControllerManager;
import com.arthenica.mobileffmpeg.player.PlayerManager;
import com.arthenica.mobileffmpeg.player.sdl.SDLActivity;
import com.arthenica.mobileffmpeg.player.sdl.SDLAudioManager;
import com.arthenica.mobileffmpeg.player.sdl.SDLControllerManager;

import java.io.IOException;
import java.io.InputStream;

public class FFplay {

    private static final boolean enabled;

    static {

        /* MOUSE AND TOUCH IS THE SAME DEVICE */
        separateMouseAndTouch = false;

        /* FFPLAY DEPENDS ON SDL. CHECK WHETHER IT IS ENABLED OR NOT */
        if (Config.getExternalLibraries().contains("sdl2")) {
            init();

            /* FFPLAY METHODS ARE ENABLED ONLY IF INITIALIZATION COMPLETES SUCCESSFULLY */
            enabled = true;
        } else {
            enabled = false;
        }
    }

    /**
     * <p>Synchronously executes FFplay with arguments provided.
     *
     * @param arguments FFplay command options/arguments as string array
     * @return zero on successful execution, 255 on user cancel and non-zero on error
     */
    public static int execute(final String[] arguments) {
        if (enabled) {
            return nativePlayerRun(arguments);
        } else {
            throw new RuntimeException("sdl not found. FFplay requires sdl to run.");
        }
    }

    static void init() {

        // ENABLE SDL FIRST
        nativeSDLInit();

        // COMPLETE OTHER COMPONENTS AFTER
        nativePlayerInit();
        nativeAudioInit();
        nativeControllerInit();
    }

    public static String playerNativeGetHint(final String name) {
        return nativePlayerNativeGetHint(name);
    }

    public static void inputSetComposingText(final String text, final int newCursorPosition) {
        nativeInputSetComposingText(text, newCursorPosition);
    }

    public static void inputGenerateScancodeForUnichar(final char c) {
        nativeInputGenerateScancodeForUnichar(c);
    }

    public static void inputCommitText(String text, int newCursorPosition) {
        nativeInputCommitText(text, newCursorPosition);
    }

    public static void playerNativeSetenv(final String name, final String value) {
        nativePlayerNativeSetenv(name, value);
    }

    public static void playerOnDropFile(final String filename) {
        nativePlayerOnDropFile(filename);
    }

    public static void playerNativeLowMemory() {
        nativePlayerNativeLowMemory();
    }

    public static void playerNativeQuit() {
        nativePlayerNativeQuit();
    }

    public static void playerNativePause() {
        nativePlayerNativePause();
    }

    public static void playerNativeResume() {
        nativePlayerNativeResume();
    }

    public static void playerOnKeyDown(final int keyCode) {
        nativePlayerOnKeyDown(keyCode);
    }

    public static void playerOnKeyUp(final int keyCode) {
        nativePlayerOnKeyUp(keyCode);
    }

    public static void playerOnKeyboardFocusLost() {
        nativePlayerOnKeyboardFocusLost();
    }

    public static void playerOnClipboardChanged() {
        nativePlayerOnClipboardChanged();
    }

    public static void playerOnSurfaceChanged() {
        nativePlayerOnSurfaceChanged();
    }

    public static void playerOnSurfaceDestroyed() {
        nativePlayerOnSurfaceDestroyed();
    }

    public static void playerOnMouse(final int button, final int action, final float x, final float y) {
        nativePlayerOnMouse(button, action, x, y);
    }

    public static int controllerAddHaptic(final int deviceId, final String name) {
        return nativeControllerAddHaptic(deviceId, name);
    }

    public static int controllerRemoveHaptic(final int deviceId) {
        return nativeControllerRemoveHaptic(deviceId);
    }

    public static int controllerAddJoystick(final int deviceId, final String name, final String desc, final int isAccelerometer, final int nButtons, final int nAxes, final int nHats, final int nBalls) {
        return nativeControllerAddJoystick(deviceId, name, desc, isAccelerometer, nButtons, nAxes, nHats, nBalls);
    }

    public static int controllerRemoveJoystick(final int deviceId) {
        return nativeControllerRemoveJoystick(deviceId);
    }

    public static void controllerOnJoy(final int deviceId, final int axis, final float value) {
        nativeControllerOnJoy(deviceId, axis, value);
    }

    public static void controllerOnHat(final int deviceId, final int hatId, final int x, final int y) {
        nativeControllerOnHat(deviceId, hatId, x, y);
    }

    public static void playerOnResize(final int x, final int y, final int format, final float rate) {
        nativePlayerOnResize(x, y, format, rate);
    }

    public static int controllerOnPadDown(final int deviceId, final int keycode) {
        return nativeControllerOnPadDown(deviceId, keycode);
    }

    public static int controllerOnPadUp(final int deviceId, final int keycode) {
        return nativeControllerOnPadUp(deviceId, keycode);
    }

    public static void playerOnTouch(final int touchDevId, final int pointerFingerId, final int action, final float x, final float y, final float p) {
        nativePlayerOnTouch(touchDevId, pointerFingerId, action, x, y, p);
    }

    public static void playerOnAccel(final float x, final float y, final float z) {
        nativePlayerOnAccel(x, y, z);
    }

    public static void reset() {
        setActivityContext(null);

        //@TODO refactor initialise methods
        SDLActivity.initialize();
        SDLAudioManager.initialize();
        SDLControllerManager.initialize();
    }

    //@TODO we should not save the activity context
    private static Context activityContext;

    public static Context getActivityContext() {
        return activityContext;
    }

    public static void setActivityContext(Context context) {
        activityContext = context;
    }

    private static boolean separateMouseAndTouch;

    public static boolean isSeparateMouseAndTouch() {
        return separateMouseAndTouch;
    }

    public static void setSeparateMouseAndTouch(boolean separateMouseAndTouch) {
        FFplay.separateMouseAndTouch = separateMouseAndTouch;
    }

    private static AudioManager audioManager;

    private static ControllerManager controllerManager;

    private static PlayerManager playerManager;

    public static void setAudioManager(final AudioManager newAudioManager) {
        audioManager = newAudioManager;
    }

    public static void setControllerManager(final ControllerManager newControllerManager) {
        controllerManager = newControllerManager;
    }

    public static void setPlayerManager(PlayerManager newPlayerManager) {
        playerManager = newPlayerManager;
    }

    static int audioOpen(final int sampleRate, final boolean is16Bit, final boolean isStereo, final int desiredFrames) {
        if (audioManager != null) {
            return audioManager.audioOpen(sampleRate, is16Bit, isStereo, desiredFrames);
        } else {
            return -1;
        }
    }

    static void audioWriteShortBuffer(final short[] buffer) {
        if (audioManager != null) {
            audioManager.audioWriteShortBuffer(buffer);
        }
    }

    static void audioWriteByteBuffer(final byte[] buffer) {
        if (audioManager != null) {
            audioManager.audioWriteByteBuffer(buffer);
        }
    }

    static int captureOpen(final int sampleRate, final boolean is16Bit, final boolean isStereo, final int desiredFrames) {
        if (audioManager != null) {
            return audioManager.captureOpen(sampleRate, is16Bit, isStereo, desiredFrames);
        } else {
            return -1;
        }
    }

    static int captureReadShortBuffer(final short[] buffer, final boolean blocking) {
        if (audioManager != null) {
            return audioManager.captureReadShortBuffer(buffer, blocking);
        } else {
            return -1;
        }
    }

    static int captureReadByteBuffer(final byte[] buffer, final boolean blocking) {
        if (audioManager != null) {
            return audioManager.captureReadByteBuffer(buffer, blocking);
        } else {
            return -1;
        }
    }

    static void audioClose() {
        if (audioManager != null) {
            audioManager.audioClose();
        }
    }

    static void captureClose() {
        if (audioManager != null) {
            audioManager.captureClose();
        }
    }

    static void pollInputDevices() {
        if (controllerManager != null) {
            controllerManager.pollInputDevices();
        }
    }

    static void pollHapticDevices() {
        if (controllerManager != null) {
            controllerManager.pollHapticDevices();
        }
    }

    static void hapticRun(final int deviceId, final int length) {
        if (controllerManager != null) {
            controllerManager.hapticRun(deviceId, length);
        }
    }

    static boolean setActivityTitle(final String title) {
        if (playerManager != null) {
            return playerManager.setActivityTitle(title);
        } else {
            return true;
        }
    }

    static void setWindowStyle(final boolean fullScreen) {
        if (playerManager != null) {
            playerManager.setWindowStyle(fullScreen);
        }
    }

    static void setOrientation(final int w, final int h, final boolean resizable, final String hint) {
        if (playerManager != null) {
            playerManager.setOrientation(w, h, resizable, hint);
        }
    }

    static boolean isScreenKeyboardShown() {
        if (playerManager != null) {
            return playerManager.isScreenKeyboardShown();
        } else {
            return false;
        }
    }

    static boolean sendMessage(final int command, final int param) {
        if (playerManager != null) {
            return playerManager.sendMessage(command, param);
        } else {
            return false;
        }
    }

    static Context getContext() {
        if (playerManager != null) {
            return playerManager.getContext();
        } else {
            return null;
        }
    }

    static boolean isAndroidTV() {
        if (playerManager != null) {
            return playerManager.isAndroidTV();
        } else {
            return false;
        }
    }

    static DisplayMetrics getDisplayDPI() {
        if (playerManager != null) {
            return playerManager.getDisplayDPI();
        } else {
            return null;
        }
    }

    static boolean getManifestEnvironmentVariables() {
        if (playerManager != null) {
            return playerManager.getManifestEnvironmentVariables();
        } else {
            return false;
        }
    }

    static boolean showTextInput(final int x, final int y, final int w, final int h) {
        if (playerManager != null) {
            return playerManager.showTextInput(x, y, w, h);
        } else {
            return false;
        }
    }

    static Surface getNativeSurface() {
        if (playerManager != null) {
            return playerManager.getNativeSurface();
        } else {
            return null;
        }
    }

    static int[] inputGetInputDeviceIds(final int sources) {
        if (playerManager != null) {
            return playerManager.inputGetInputDeviceIds(sources);
        } else {
            return new int[0];
        }
    }

    static boolean clipboardHasText() {
        if (playerManager != null) {
            return playerManager.clipboardHasText();
        } else {
            return false;
        }
    }

    static String clipboardGetText() {
        if (playerManager != null) {
            return playerManager.clipboardGetText();
        } else {
            return "";
        }
    }

    static void clipboardSetText(final String string) {
        if (playerManager != null) {
            playerManager.clipboardSetText(string);
        }
    }

    static InputStream openAPKExpansionInputStream(final String fileName) throws IOException {
        if (playerManager != null) {
            return playerManager.openAPKExpansionInputStream(fileName);
        } else {
            return null;
        }
    }

    static int messageBoxShowMessageBox(final int flags, final String title, final String message, final int[] buttonFlags, final int[] buttonIds, final String[] buttonTexts, final int[] colors) {
        if (playerManager != null) {
            return playerManager.messageBoxShowMessageBox(flags, title, message, buttonFlags, buttonIds, buttonTexts, colors);
        } else {
            return -1;
        }
    }

    /**
     * <p>Initializes SDL for FFplay. It must be called before other SDL functions.
     */
    native static void nativeSDLInit();

    native static int nativePlayerInit();

    /**
     * <p>Synchronously executes FFplay natively with arguments provided.
     *
     * @param arguments FFplay command options/arguments as string array
     * @return zero on successful execution, 255 on user cancel and non-zero on error
     */
    native static int nativePlayerRun(final String[] arguments);

    native static void nativePlayerNativeLowMemory();

    native static void nativePlayerNativeQuit();

    native static void nativePlayerNativePause();

    native static void nativePlayerNativeResume();

    native static void nativePlayerOnDropFile(final String filename);

    native static void nativePlayerOnResize(final int x, final int y, final int format, final float rate);

    native static void nativePlayerOnKeyDown(final int keyCode);

    native static void nativePlayerOnKeyUp(final int keyCode);

    native static void nativePlayerOnKeyboardFocusLost();

    native static void nativePlayerOnMouse(final int button, final int action, final float x, final float y);

    native static void nativePlayerOnTouch(final int touchDevId, final int pointerFingerId, final int action, final float x, final float y, final float p);

    native static void nativePlayerOnAccel(final float x, final float y, final float z);

    native static void nativePlayerOnClipboardChanged();

    native static void nativePlayerOnSurfaceChanged();

    native static void nativePlayerOnSurfaceDestroyed();

    native static String nativePlayerNativeGetHint(final String name);

    native static void nativePlayerNativeSetenv(final String name, final String value);

    native static int nativeAudioInit();

    native static int nativeControllerInit();

    native static int nativeControllerAddJoystick(final int deviceId, final String name, final String desc, final int isAccelerometer, final int nButtons, final int nAxes, final int nHats, final int nBalls);

    native static int nativeControllerRemoveJoystick(final int deviceId);

    native static int nativeControllerAddHaptic(final int deviceId, final String name);

    native static int nativeControllerRemoveHaptic(final int deviceId);

    native static int nativeControllerOnPadDown(final int deviceId, final int keycode);

    native static int nativeControllerOnPadUp(final int deviceId, final int keycode);

    native static void nativeControllerOnJoy(final int deviceId, final int axis, final float value);

    native static void nativeControllerOnHat(final int deviceId, final int hatId, final int x, final int y);

    native static void nativeInputCommitText(final String text, final int newCursorPosition);

    native static void nativeInputGenerateScancodeForUnichar(final char c);

    native static void nativeInputSetComposingText(final String text, final int newCursorPosition);

}