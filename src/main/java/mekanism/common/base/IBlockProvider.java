package mekanism.common.base;

import javax.annotation.Nonnull;
import mekanism.common.block.interfaces.IBlockDisableable;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public interface IBlockProvider extends IItemProvider {

    @Nonnull
    Block getBlock();

    default boolean blockMatches(ItemStack otherStack) {
        Item item = otherStack.getItem();
        return item instanceof BlockItem && blockMatches(((BlockItem) item).getBlock());
    }

    default boolean blockMatches(Block other) {
        return getBlock() == other;
    }

    @Override
    default ResourceLocation getRegistryName() {
        //Make sure to use the block's registry name in case it somehow doesn't match
        return getBlock().getRegistryName();
    }

    @Override
    default String getTranslationKey() {
        return getBlock().getTranslationKey();
    }

    default boolean isEnabled() {
        Block block = getBlock();
        return !(block instanceof IBlockDisableable) || ((IBlockDisableable) block).isEnabled();
    }

    default boolean canBeDisabled() {
        Block block = getBlock();
        return block instanceof IBlockDisableable;
    }
}