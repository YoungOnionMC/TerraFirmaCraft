/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant.fruit;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.IForgeBlockExtension;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.util.Helpers;

public class FruitTreeBranchBlock extends PipeBlock implements IForgeBlockExtension
{
    public static final IntegerProperty STAGE = TFCBlockStateProperties.STAGE_3;
    private final ExtendedProperties properties;

    public FruitTreeBranchBlock(ExtendedProperties properties)
    {
        super(0.25F, properties.properties());
        this.properties = properties;
        registerDefaultState(stateDefinition.any().setValue(NORTH, false).setValue(EAST, false).setValue(SOUTH, false).setValue(WEST, false).setValue(UP, false).setValue(DOWN, false));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return getStateForPlacement(context.getLevel(), context.getClickedPos());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN, STAGE);
    }

    public BlockState getStateForPlacement(BlockGetter level, BlockPos pos)
    {
        Block downBlock = level.getBlockState(pos.below()).getBlock();
        Block upBlock = level.getBlockState(pos.above()).getBlock();
        Block northBlock = level.getBlockState(pos.north()).getBlock();
        Block eastBlock = level.getBlockState(pos.east()).getBlock();
        Block southBlock = level.getBlockState(pos.south()).getBlock();
        Block westBlock = level.getBlockState(pos.west()).getBlock();
        return defaultBlockState()
            .setValue(DOWN, Helpers.isBlock(downBlock, TFCTags.Blocks.FRUIT_TREE_BRANCH) || Helpers.isBlock(downBlock, TFCTags.Blocks.BUSH_PLANTABLE_ON))
            .setValue(UP, Helpers.isBlock(upBlock, TFCTags.Blocks.FRUIT_TREE_BRANCH) || Helpers.isBlock(upBlock, TFCTags.Blocks.FRUIT_TREE_SAPLING))
            .setValue(NORTH, Helpers.isBlock(northBlock, TFCTags.Blocks.FRUIT_TREE_BRANCH))
            .setValue(EAST, Helpers.isBlock(eastBlock, TFCTags.Blocks.FRUIT_TREE_BRANCH))
            .setValue(SOUTH, Helpers.isBlock(southBlock, TFCTags.Blocks.FRUIT_TREE_BRANCH))
            .setValue(WEST, Helpers.isBlock(westBlock, TFCTags.Blocks.FRUIT_TREE_BRANCH));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos)
    {
        if (!state.canSurvive(level, currentPos))
        {
            level.scheduleTick(currentPos, this, 1);
            return state;
        }
        else
        {
            boolean flag = Helpers.isBlock(facingState, TFCTags.Blocks.FRUIT_TREE_BRANCH) || (facing == Direction.DOWN && Helpers.isBlock(facingState, TFCTags.Blocks.BUSH_PLANTABLE_ON) || (facing == Direction.UP && Helpers.isBlock(facingState, TFCTags.Blocks.FRUIT_TREE_SAPLING)));
            return state.setValue(PROPERTY_BY_DIRECTION.get(facing), flag);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        BlockState belowState = level.getBlockState(pos.below());
        for (Direction direction : Direction.Plane.HORIZONTAL)
        {
            BlockPos relativePos = pos.relative(direction);
            if (Helpers.isBlock(level.getBlockState(relativePos).getBlock(), TFCTags.Blocks.FRUIT_TREE_BRANCH))
            {
                Block below = level.getBlockState(relativePos.below()).getBlock();
                if (Helpers.isBlock(below, TFCTags.Blocks.FRUIT_TREE_BRANCH) || Helpers.isBlock(below, TFCTags.Blocks.BUSH_PLANTABLE_ON))
                {
                    return true;
                }
            }
        }
        Block block = belowState.getBlock();
        return Helpers.isBlock(block, TFCTags.Blocks.FRUIT_TREE_BRANCH) || Helpers.isBlock(block, TFCTags.Blocks.BUSH_PLANTABLE_ON);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void tick(BlockState state, ServerLevel level, BlockPos pos, Random rand)
    {
        if (!state.canSurvive(level, pos) && !level.isClientSide())
        {
            level.destroyBlock(pos, true);
        }
    }

    @Override
    public ExtendedProperties getExtendedProperties()
    {
        return properties;
    }
}
