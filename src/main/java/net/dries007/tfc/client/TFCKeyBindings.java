/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client;

import org.lwjgl.glfw.GLFW;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraftforge.client.settings.KeyConflictContext;

import static net.dries007.tfc.TerraFirmaCraft.MOD_NAME;

public class TFCKeyBindings
{
    public static final KeyMapping PLACE_BLOCK = new KeyMapping("tfc.key.place_block", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_V, MOD_NAME);
}
