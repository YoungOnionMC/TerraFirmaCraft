/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.cave;

import java.util.Random;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import com.mojang.serialization.Codec;

public class LargeCaveSpikesFeature extends CaveSpikesFeature
{
    public LargeCaveSpikesFeature(Codec<NoneFeatureConfiguration> codec)
    {
        super(codec);
    }

    /**
     * Much larger spikes, calls to the smaller spikes on the outsides
     */
    public void place(WorldGenLevel worldIn, BlockPos pos, BlockState spike, BlockState raw, Direction direction, Random rand)
    {
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        int height = 6 + rand.nextInt(11);
        int radius = 2 + rand.nextInt(1);
        int maxHeightReached = 0;
        for (int y = -3; y <= height; y++)
        {
            float radiusSquared = radius * (1 - 1.5f * Math.abs(y) / height);
            if (radiusSquared < 0)
            {
                continue;
            }
            radiusSquared *= radiusSquared;
            for (int x = -radius; x <= radius; x++)
            {
                for (int z = -radius; z <= radius; z++)
                {
                    mutablePos.set(pos).move(x, y * direction.getStepY(), z);
                    float actualRadius = ((x * x) + (z * z)) / radiusSquared;
                    if (actualRadius < 0.7)
                    {
                        // Fill in actual blocks
                        replaceBlockWithoutFluid(worldIn, mutablePos, raw);
                        if (x == 0 && z == 0)
                        {
                            maxHeightReached = y;
                        }
                    }
                    else if (actualRadius < 0.85 && rand.nextBoolean())
                    {
                        // Only fill in if continuing downwards
                        if (worldIn.getBlockState(mutablePos.offset(0, -direction.getStepY(), 0)) == raw)
                        {
                            replaceBlockWithoutFluid(worldIn, mutablePos, raw);
                        }
                    }
                    else if (actualRadius < 1 && rand.nextInt(3) == 0 && y > 0)
                    {
                        placeSmallSpike(worldIn, mutablePos, spike, raw, direction, rand);
                    }
                }
            }
        }
        mutablePos.set(pos).move(direction, maxHeightReached - 1);
        placeSmallSpike(worldIn, mutablePos, spike, raw, direction, rand, 1.0f);
    }
}