package mekanism.common.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import mekanism.api.Coord4D;
import mekanism.common.Mekanism;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.ReflectionHelper.UnableToFindMethodException;

public final class MinerUtils {

    public static final List<Block> specialSilkIDs = Arrays.asList(Blocks.ICE, Blocks.CHORUS_FLOWER);

    private static Method getSilkTouchDrop;

    static {
        try {
            getSilkTouchDrop = ReflectionHelper.findMethod(Block.class, "getSilkTouchDrop", "func_180643_i", BlockState.class);
        } catch (UnableToFindMethodException e) {
            Mekanism.logger.error("Unable to find method Block.getSilkTouchDrop");
        }
    }

    public static List<ItemStack> getDrops(World world, Coord4D coord, boolean silk, BlockPos minerPosition) {
        BlockState state = coord.getBlockState(world);
        Block block = state.getBlock();
        PlayerEntity fakePlayer = Mekanism.proxy.getDummyPlayer((ServerWorld) world, minerPosition).get();
        if (block.isAir(state, world, coord.getPos())) {
            return Collections.emptyList();
        }

        //TODO: I believe the shulker box is actually properly handled now, but if not we need to add back the specialized logic
        if (silk && (block.canSilkHarvest(world, coord.getPos(), state, fakePlayer) || specialSilkIDs.contains(block))) {
            Object it = null;
            if (getSilkTouchDrop != null) {
                try {
                    it = getSilkTouchDrop.invoke(block, state);
                } catch (InvocationTargetException | IllegalAccessException e) {
                    Mekanism.logger.error("Block.getSilkTouchDrop errored", e);
                }
            }
            NonNullList<ItemStack> ret = NonNullList.create();
            if (it instanceof ItemStack && !((ItemStack) it).isEmpty()) {
                ret.add((ItemStack) it);
            } else {
                //silk touch drop is empty or failed to call/find getSilkTouchDrop method
                // Fallback to grabbing an itemblock
                Item item = block.asItem();
                if (item != Items.AIR) {
                    ret.add(new ItemStack(item));
                }
            }
            if (ret.size() > 0) {
                ForgeEventFactory.fireBlockHarvesting(ret, world, coord.getPos(), state, 0, 1.0F, true, fakePlayer);
                return ret;
            }
        } else {
            @SuppressWarnings("deprecation")//needed for backwards compatibility
                  List<ItemStack> blockDrops = block.getDrops(world, coord.getPos(), state, 0);
            if (blockDrops.size() > 0) {
                ForgeEventFactory.fireBlockHarvesting(blockDrops, world, coord.getPos(), state, 0, 1.0F, false, fakePlayer);
            } else if (block == Blocks.CHORUS_FLOWER) {
                //Chorus flower returns AIR for itemDropped... and for silkTouchDrop.
                blockDrops.add(new ItemStack(Blocks.CHORUS_FLOWER));
            }
            return blockDrops;
        }

        return Collections.emptyList();
    }
}