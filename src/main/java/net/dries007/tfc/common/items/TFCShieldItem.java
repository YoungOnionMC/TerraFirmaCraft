/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.Tier;

public class TFCShieldItem extends ShieldItem
{
    private final Tier tier;

    public TFCShieldItem(Tier tier, Properties builder)
    {
        super(builder.defaultDurability(tier.getUses()));
        this.tier = tier;
    }

    @Override
    public int getEnchantmentValue()
    {
        return tier.getEnchantmentValue();
    }

    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair)
    {
        return tier.getRepairIngredient().test(repair);
    }
}