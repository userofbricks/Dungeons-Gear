
package com.infamous.dungeons_gear.init;

import static com.infamous.dungeons_gear.DungeonsGear.MODID;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ParticleEventHandler {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onParticleFactory(ParticleFactoryRegisterEvent event){
        Minecraft.getInstance().particles.registerFactory(ParticleInit.ELECTRIC_SHOCK.get(), ElectricShockParticle.Factory::new);
        Minecraft.getInstance().particles.registerFactory(ParticleInit.SOUL.get(), SoulParticle.Factory::new);
    }
}
