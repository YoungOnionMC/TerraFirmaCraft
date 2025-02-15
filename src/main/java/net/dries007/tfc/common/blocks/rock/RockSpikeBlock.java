/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.rock;

import java.util.Locale;
import java.util.Random;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.fluids.FluidProperty;
import net.dries007.tfc.common.fluids.IFluidLoggable;
import net.dries007.tfc.common.recipes.CollapseRecipe;
import net.dries007.tfc.util.Helpers;

@SuppressWarnings("deprecation")
public class RockSpikeBlock extends Block implements IFluidLoggable, IFallableBlock
{
    public static final EnumProperty<Part> PART = TFCBlockStateProperties.ROCK_SPIKE_PART;
    public static final FluidProperty FLUID = TFCBlockStateProperties.WATER_AND_LAVA;

    public static final VoxelShape BASE_SHAPE = box(2, 0, 2, 14, 16, 14);
    public static final VoxelShape MIDDLE_SHAPE = box(4, 0, 4, 12, 16, 12);
    public static final VoxelShape TIP_SHAPE = box(6, 0, 6, 10, 16, 10);

    public RockSpikeBlock(Properties properties)
    {
        super(properties);

        registerDefaultState(stateDefinition.any().setValue(PART, Part.BASE).setValue(getFluidProperty(), getFluidProperty().keyFor(Fluids.EMPTY)));
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving)
    {
        level.scheduleTick(pos, this, 1);
    }

    @Override
    public FluidState getFluidState(BlockState state)
    {
        return IFluidLoggable.super.getFluidState(state);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
    {
        return switch (state.getValue(PART))
            {
                case BASE -> BASE_SHAPE;
                case MIDDLE -> MIDDLE_SHAPE;
                default -> TIP_SHAPE;
            };
    }

    @Override
    @SuppressWarnings("deprecation")
    public void tick(BlockState state, ServerLevel level, BlockPos pos, Random rand)
    {
        // Check support from above or below
        BlockPos belowPos = pos.below();
        BlockState belowState = level.getBlockState(belowPos);
        if (belowState.getBlock() == this && belowState.getValue(PART).isLargerThan(state.getValue(PART)))
        {
            // Larger spike below. Tick that to ensure it is supported
            level.scheduleTick(belowPos, this, 1);
            return;
        }
        else if (belowState.isFaceSturdy(level, belowPos, Direction.UP))
        {
            // Full block below, this is supported
            return;
        }

        // No support below, try above
        BlockPos abovePos = pos.above();
        BlockState aboveState = level.getBlockState(abovePos);
        if (aboveState.getBlock() == this && aboveState.getValue(PART).isLargerThan(state.getValue(PART)))
        {
            // Larger spike above. Tick to ensure that it is supported
            level.scheduleTick(abovePos, this, 1);
            return;
        }
        else if (aboveState.isFaceSturdy(level, abovePos, Direction.DOWN))
        {
            // Full block above, this is supported
            return;
        }

        // No support, so either collapse, or break
        if (Helpers.isBlock(this, TFCTags.Blocks.CAN_COLLAPSE) && CollapseRecipe.collapseBlock(level, pos, state))
        {
            level.playSound(null, pos, TFCSounds.ROCK_SLIDE_SHORT.get(), SoundSource.BLOCKS, 0.8f, 1.0f);
        }
        else
        {
            level.destroyBlock(pos, true);
        }
    }

    @Override
    public void onceFinishedFalling(Level worldIn, BlockPos pos, FallingBlockEntity fallingBlock)
    {
        // todo: better shatter sound
        worldIn.destroyBlock(pos, false);
        worldIn.playSound(null, pos, TFCSounds.ROCK_SLIDE_SHORT.get(), SoundSource.BLOCKS, 0.8f, 2.0f);
    }

    @Override
    public FluidProperty getFluidProperty()
    {
        return FLUID;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(PART, getFluidProperty());
    }

    public enum Part implements StringRepresentable
    {
        BASE, MIDDLE, TIP;

        private final String serializedName;

        Part()
        {
            serializedName = name().toLowerCase(Locale.ROOT);
        }

        @Override
        public String getSerializedName()
        {
            return serializedName;
        }

        public boolean isLargerThan(Part other)
        {
            return this.ordinal() <= other.ordinal();
        }
    }
}