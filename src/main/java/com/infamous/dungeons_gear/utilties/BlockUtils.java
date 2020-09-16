package com.infamous.dungeons_gear.utilties;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;

public class BlockUtils {
    public static boolean isIn(BlockState state, Block tagIn) {
       return state.getBlock() == tagIn;
    }
}
