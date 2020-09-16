package com.infamous.dungeons_gear.utilties;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

public class AxisAlignedBBUtils {

	   public static AxisAlignedBB func_241549_a_(Vec3d p_241549_0_) {
	      return new AxisAlignedBB(p_241549_0_.x, p_241549_0_.y, p_241549_0_.z, p_241549_0_.x + 1.0D, p_241549_0_.y + 1.0D, p_241549_0_.z + 1.0D);
	   }
}
