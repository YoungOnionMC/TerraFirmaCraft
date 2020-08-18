/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.surfacebuilder;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;

public class NormalSurfaceBuilder extends SurfaceBuilder<SurfaceBuilderConfig>
{
    public static final BlockState AIR = Blocks.AIR.getDefaultState();
    public static final BlockState SANDSTONE = Blocks.SANDSTONE.getDefaultState();

    public NormalSurfaceBuilder()
    {
        super(SurfaceBuilderConfig::deserialize);
    }

    @Override
    public void buildSurface(Random random, IChunk chunkIn, Biome biomeIn, int x, int z, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed, SurfaceBuilderConfig config)
    {
        BlockState topState;
        BlockState underState = config.getUnder();
        BlockPos.Mutable pos = new BlockPos.Mutable();
        int surfaceDepth = -1;
        int maxSurfaceDepth = (int) (noise / 3.0D + 3.0D + random.nextDouble() * 0.25D);
        int localX = x & 15;
        int localZ = z & 15;

        for (int y = startHeight; y >= 0; --y)
        {
            pos.setPos(localX, y, localZ);
            BlockState stateAt = chunkIn.getBlockState(pos);
            if (stateAt.isAir(chunkIn, pos))
            {
                // Reached air, reset surface depth
                surfaceDepth = -1;
            }
            else if (stateAt.getBlock() == defaultBlock.getBlock())
            {
                if (surfaceDepth == -1)
                {
                    // Reached surface. Place top state and switch to subsurface layers
                    surfaceDepth = maxSurfaceDepth;
                    if (maxSurfaceDepth <= 0)
                    {
                        topState = AIR;
                        underState = defaultBlock;
                    }
                    else if (y >= seaLevel)
                    {
                        topState = config.getTop();
                        underState = config.getUnder();
                    }
                    else
                    {
                        topState = underState = config.getUnderWaterMaterial();
                    }

                    chunkIn.setBlockState(pos, topState, false);
                }
                else if (surfaceDepth > 0)
                {
                    // Subsurface layers
                    surfaceDepth--;
                    chunkIn.setBlockState(pos, underState, false);
                    if (surfaceDepth == 0 && underState.getBlock() == Blocks.SAND && maxSurfaceDepth > 1)
                    {
                        surfaceDepth = random.nextInt(4) + Math.max(0, y - 63);
                        underState = SANDSTONE;
                    }
                }
            }
        }
    }
}
