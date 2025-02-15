/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.PacketDistributor;

import com.mojang.blaze3d.vertex.PoseStack;
import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.client.screen.button.PlayerInventoryTabButton;
import net.dries007.tfc.common.capabilities.food.Nutrient;
import net.dries007.tfc.common.capabilities.food.NutritionData;
import net.dries007.tfc.common.capabilities.food.TFCFoodData;
import net.dries007.tfc.common.container.Container;
import net.dries007.tfc.network.PacketHandler;
import net.dries007.tfc.network.SwitchInventoryTabPacket;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class NutritionScreen extends TFCContainerScreen<Container>
{
    public static final ResourceLocation TEXTURE = new ResourceLocation(MOD_ID, "textures/gui/player_nutrition.png");

    public NutritionScreen(Container container, Inventory playerInventory, Component name)
    {
        super(container, playerInventory, name, TEXTURE);
    }

    @Override
    public void init()
    {
        super.init();
        addRenderableWidget(new PlayerInventoryTabButton(leftPos, topPos, 176, 4, 20, 22, 128, 0, 1, 3, 0, 0, button -> {
            playerInventory.player.containerMenu = playerInventory.player.inventoryMenu;
            Minecraft.getInstance().setScreen(new InventoryScreen(playerInventory.player));
            PacketHandler.send(PacketDistributor.SERVER.noArg(), new SwitchInventoryTabPacket(SwitchInventoryTabPacket.Type.INVENTORY));
        }));
        addRenderableWidget(new PlayerInventoryTabButton(leftPos, topPos, 176, 27, 20, 22, 128, 0, 1, 3, 32, 0, SwitchInventoryTabPacket.Type.CALENDAR));
        addRenderableWidget(new PlayerInventoryTabButton(leftPos, topPos, 176 - 3, 50, 20 + 3, 22, 128 + 20, 0, 1, 3, 64, 0, SwitchInventoryTabPacket.Type.NUTRITION));
        addRenderableWidget(new PlayerInventoryTabButton(leftPos, topPos, 176, 73, 20, 22, 128, 0, 1, 3, 96, 0, SwitchInventoryTabPacket.Type.CLIMATE));
    }

    @Override
    protected void renderBg(PoseStack stack, float partialTicks, int mouseX, int mouseY)
    {
        super.renderBg(stack, partialTicks, mouseX, mouseY);

        final Player player = ClientHelpers.getPlayer();
        if (player != null && player.getFoodData() instanceof TFCFoodData data)
        {
            final NutritionData nutrition = data.getNutrition();
            for (Nutrient nutrient : Nutrient.VALUES)
            {
                final int width = (int) (nutrition.getNutrient(nutrient) * 50);
                blit(stack, leftPos + 118, topPos + 21 + 13 * nutrient.ordinal(), 176, 0, width, 5);
            }
        }
    }

    @Override
    protected void renderLabels(PoseStack stack, int mouseX, int mouseY)
    {
        super.renderLabels(stack, mouseX, mouseY);

        for (Nutrient nutrient : Nutrient.VALUES)
        {
            final Component text = Helpers.translateEnum(nutrient).withStyle(nutrient.getColor());
            font.draw(stack, text, 112 - font.width(text), 19 + 13 * nutrient.ordinal(), 0x404040);
        }
    }
}