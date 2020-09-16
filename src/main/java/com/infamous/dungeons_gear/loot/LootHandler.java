package com.infamous.dungeons_gear.loot;

import static com.infamous.dungeons_gear.items.ArmorList.commonLeatherArmorMap;
import static com.infamous.dungeons_gear.items.ArmorList.commonMetalArmorMap;
import static com.infamous.dungeons_gear.items.ArmorList.uniqueLeatherArmorMap;
import static com.infamous.dungeons_gear.items.ArmorList.uniqueMetalArmorMap;
import static com.infamous.dungeons_gear.items.ArtifactList.artifactMap;
import static com.infamous.dungeons_gear.items.RangedWeaponList.commonRangedWeaponMap;
import static com.infamous.dungeons_gear.items.RangedWeaponList.uniqueRangedWeaponMap;
import static com.infamous.dungeons_gear.items.WeaponList.commonWeaponMap;
import static com.infamous.dungeons_gear.items.WeaponList.uniqueWeaponMap;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.infamous.dungeons_gear.DungeonsGear;
import com.infamous.dungeons_gear.config.DungeonsGearConfig;
import com.infamous.dungeons_gear.items.ArtifactList;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.merchant.IReputationType;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.merchant.villager.VillagerTrades;
import net.minecraft.entity.merchant.villager.WanderingTraderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.common.BasicTrade;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.event.village.WandererTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = DungeonsGear.MODID)
public class LootHandler {

    @SubscribeEvent
    public static void onWandererTrades(WandererTradesEvent event){
        if(!DungeonsGearConfig.COMMON.ENABLE_VILLAGER_TRADES.get()) return;

        List<VillagerTrades.ITrade> genericTrades = event.getGenericTrades();
        List<VillagerTrades.ITrade> rareTrades = event.getRareTrades();

        moveTradesToDifferentGroup(rareTrades, genericTrades);

        for(Item item : ArtifactList.artifactMap.keySet()){
            ResourceLocation resourceLocation = ArtifactList.artifactMap.get(item);
            Item artifact = ForgeRegistries.ITEMS.getValue(resourceLocation);
            ItemStack artifactStack = new ItemStack(artifact);
            BasicTrade trade = new BasicTrade(24, artifactStack, 3, 30);
            rareTrades.add(trade);
        }
    }

    @SubscribeEvent
    public static void onVillagerTrades(VillagerTradesEvent event){
        if(!DungeonsGearConfig.COMMON.ENABLE_VILLAGER_TRADES.get()) return;

        if(event.getType() == VillagerProfession.WEAPONSMITH){
            Int2ObjectMap<List<VillagerTrades.ITrade>> weaponsmithTrades = event.getTrades();

            // Moving the Level 4 "Diamond for Emerald" Trade down to Level 3
            VillagerTrades.ITrade diamondForEmeraldTrade = weaponsmithTrades.get(4).get(0);
            weaponsmithTrades.get(3).add(diamondForEmeraldTrade);
            weaponsmithTrades.get(4).remove(0);

            addCommonAndUniqueTrades(weaponsmithTrades, commonWeaponMap, uniqueWeaponMap);
        }
        if(event.getType() == VillagerProfession.FLETCHER){
            Int2ObjectMap<List<VillagerTrades.ITrade>> fletcherTrades = event.getTrades();

            // Move higher level stuff to lower trade levels
            VillagerTrades.ITrade emeraldForFeatherTrade = fletcherTrades.get(4).get(0);
            VillagerTrades.ITrade emeraldForTripwireHookTrade = fletcherTrades.get(5).get(0);
            VillagerTrades.ITrade emeraldAndArrowForTippedArrowTrade = fletcherTrades.get(5).get(2);
            fletcherTrades.get(2).add(emeraldForFeatherTrade);
            fletcherTrades.get(3).add(emeraldForTripwireHookTrade);
            fletcherTrades.get(3).add(emeraldAndArrowForTippedArrowTrade);
            fletcherTrades.get(4).remove(0);
            fletcherTrades.get(5).remove(0);
            fletcherTrades.get(5).remove(1); // since the 1st trade was already removed, the 3rd trade is now the second

            //moveTradesToDifferentGroup(fletcherTrades.get(4), fletcherTrades.get(2));
            //moveTradesToDifferentGroup(fletcherTrades.get(5),  fletcherTrades.get(3));

            addCommonAndUniqueTrades(fletcherTrades, commonRangedWeaponMap, uniqueRangedWeaponMap);
        }

        if(event.getType() == VillagerProfession.ARMORER){
            Int2ObjectMap<List<VillagerTrades.ITrade>> armorerTrades = event.getTrades();
            addCommonAndUniqueTrades(armorerTrades, commonMetalArmorMap, uniqueMetalArmorMap);
        }

        if(event.getType() == VillagerProfession.LEATHERWORKER){
            Int2ObjectMap<List<VillagerTrades.ITrade>> leatherworkerTrades = event.getTrades();

            // Move higher level stuff to lower trade levels
            moveTradesToDifferentGroup(leatherworkerTrades.get(4), leatherworkerTrades.get(2));
            moveTradesToDifferentGroup(leatherworkerTrades.get(5),  leatherworkerTrades.get(3));

            addCommonAndUniqueTrades(leatherworkerTrades, commonLeatherArmorMap, uniqueLeatherArmorMap);
        }
    }

    private static void addCommonAndUniqueTrades(Int2ObjectMap<List<VillagerTrades.ITrade>> villagerTrades, Map<Item, ResourceLocation> commonMap, Map<Item, ResourceLocation> uniqueMap) {
        for(Item item : commonMap.keySet()){
            ResourceLocation resourceLocation = commonMap.get(item);
            Item weapon = ForgeRegistries.ITEMS.getValue(resourceLocation);
            ItemStack weaponStack = new ItemStack(weapon);
            TradeUtils.EnchantedItemForEmeraldsTrade trade = new TradeUtils.EnchantedItemForEmeraldsTrade(weapon, 12, 3, 15,0.2F);

            villagerTrades.get(4).add(trade);
        }
        for(Item item : uniqueMap.keySet()){
            ResourceLocation resourceLocation = uniqueMap.get(item);
            Item weapon = ForgeRegistries.ITEMS.getValue(resourceLocation);
            ItemStack weaponStack = new ItemStack(weapon);
            TradeUtils.EnchantedItemForEmeraldsTrade trade = new TradeUtils.EnchantedItemForEmeraldsTrade(weapon, 24, 3, 30,0.2F);

            villagerTrades.get(5).add(trade);
        }
    }

    private static void moveTradesToDifferentGroup(List<VillagerTrades.ITrade> oldTradesGroup, List<VillagerTrades.ITrade> newTradesGroup) {
        Iterator<VillagerTrades.ITrade> it = oldTradesGroup.iterator();
        while (it.hasNext()) {
            VillagerTrades.ITrade currentTrade = it.next();
            if (currentTrade != null) {
                newTradesGroup.add(currentTrade);
                it.remove();
            }
        }
    }

    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event){
        if(!DungeonsGearConfig.COMMON.ENABLE_DUNGEONS_GEAR_LOOT.get()) return;

        // SUPER RARE
        if(event.getName().toString().equals("minecraft:chests/woodland_mansion")){
            //DungeonsGear.LOGGER.info("Handled the Woodland Mansion's loot table!");
            LootTable table = event.getTable();
            addSuperRareLootTable(table);

        }
        if(event.getName().toString().equals("minecraft:chests/buried_treasure")){
            //DungeonsGear.LOGGER.info("Handled the Buried Treasure's loot table!");
            LootTable table = event.getTable();
            addSuperRareLootTable(table);

        }

        // RARE
        if(event.getName().toString().contains("minecraft:chests/stronghold")){
            //DungeonsGear.LOGGER.info("Handled the Stronghold's loot tables!");
            LootTable table = event.getTable();
            addRareLootTable(table);

        }
        if(event.getName().toString().contains("minecraft:chests/underwater_ruin")){
            //DungeonsGear.LOGGER.info("Handled the Ocean Ruin's loot tables!");
            LootTable table = event.getTable();
            addRareLootTable(table);

        }
        if(event.getName().toString().equals("minecraft:chests/ruined_portal")){
            //DungeonsGear.LOGGER.info("Handled the Ruined Portal's loot table!");
            LootTable table = event.getTable();
            addRareLootTable(table);

        }
        if(event.getName().toString().equals("minecraft:chests/pillager_outpost")){
            //DungeonsGear.LOGGER.info("Handled the Pillager Outpost's loot table!");
            LootTable table = event.getTable();
            addRareLootTable(table);

        }
        if(event.getName().toString().equals("minecraft:chests/simple_dungeon")){
            //DungeonsGear.LOGGER.info("Handled the Dungeon's loot table!");
            LootTable table = event.getTable();
            addRareLootTable(table);

        }
        if(event.getName().toString().equals("minecraft:chests/end_city_treasure")){
            //DungeonsGear.LOGGER.info("Handled the End City's loot table!");
            LootTable table = event.getTable();
            addRareLootTable(table);

        }
        if(event.getName().toString().equals("minecraft:chests/igloo_chest")){
            //DungeonsGear.LOGGER.info("Handled the Igloo's loot table!");
            LootTable table = event.getTable();
            addRareLootTable(table);

        }

        // UNCOMMON
        if(event.getName().toString().equals("minecraft:chests/jungle_temple")){
            //DungeonsGear.LOGGER.info("Handled the Jungle Temple's loot table!");
            LootTable table = event.getTable();
            addUncommonLootTable(table);

        }
        if(event.getName().toString().equals("minecraft:chests/nether_bridge")){
            //DungeonsGear.LOGGER.info("Handled the Nether Fortress's loot table!");
            LootTable table = event.getTable();
            addUncommonLootTable(table);

        }
        if(event.getName().toString().contains("minecraft:chests/bastion")){
            //DungeonsGear.LOGGER.info("Handled the Bastion's loot tables!");
            LootTable table = event.getTable();
            addUncommonLootTable(table);

        }

        //COMMON
        if(event.getName().toString().equals("minecraft:chests/abandoned_mineshaft")){
            //DungeonsGear.LOGGER.info("Handled the Mineshaft's loot table!");
            LootTable table = event.getTable();
            addCommonLootTable(table);

        }
        if(event.getName().toString().contains("minecraft:chests/shipwreck")){
            //DungeonsGear.LOGGER.info("Handled the Shipwreck's loot tables!");
            LootTable table = event.getTable();
            addCommonLootTable(table);

        }
        if(event.getName().toString().equals("minecraft:chests/desert_pyramid")){
            //DungeonsGear.LOGGER.info("Handled the Desert Pyramid's loot table!");
            LootTable table = event.getTable();
            addCommonLootTable(table);

        }
        if(event.getName().toString().equals("minecraft:chests/village/village_weaponsmith")){
            //DungeonsGear.LOGGER.info("Handled the Weaponsmith's Chest loot table!");
            LootTable table = event.getTable();
            addCommonMeeleWeaponLootTable(table);

        }
        if(event.getName().toString().equals("minecraft:chests/village/village_fletcher")){
            //DungeonsGear.LOGGER.info("Handled the Fletcher's Chest loot table!");
            LootTable table = event.getTable();
            addCommonRangedWeaponLootTable(table);

        }
        if(event.getName().toString().equals("minecraft:chests/village/village_armorer")){
            //DungeonsGear.LOGGER.info("Handled the Weaponsmith's Chest loot table!");
            LootTable table = event.getTable();
            addCommonMetalArmorLootTable(table);

        }
        if(event.getName().toString().equals("minecraft:chests/village/village_leatherworker")){
            //DungeonsGear.LOGGER.info("Handled the Fletcher's Chest loot table!");
            LootTable table = event.getTable();
            addCommonLeatherArmorLootTable(table);

        }
        if(event.getName().toString().equals("minecraft:gameplay/hero_of_the_village/weaponsmith_gift")){
            //DungeonsGear.LOGGER.info("Handled the Weaponsmith's Gift loot table!");
            LootTable table = event.getTable();
            addCommonMeeleWeaponLootTable(table);

        }
        if(event.getName().toString().equals("minecraft:gameplay/hero_of_the_village/fletcher_gift")){
            //DungeonsGear.LOGGER.info("Handled the Fletcher's Gift loot table!");
            LootTable table = event.getTable();
            addCommonRangedWeaponLootTable(table);

        }
        if(event.getName().toString().equals("minecraft:gameplay/hero_of_the_village/armorer_gift")){
            //DungeonsGear.LOGGER.info("Handled the Armorer's Gift loot table!");
            LootTable table = event.getTable();
            addCommonMetalArmorLootTable(table);

        }
        if(event.getName().toString().equals("minecraft:gameplay/hero_of_the_village/leatherworker_gift")){
            //DungeonsGear.LOGGER.info("Handled the Leatherworker's Gift loot table!");
            LootTable table = event.getTable();
            addCommonLeatherArmorLootTable(table);

        }
    }

    private static void addCommonMeeleWeaponLootTable(LootTable table){
        Collection<ResourceLocation> commonWeapons = commonWeaponMap.values();
        Collection<ResourceLocation> uniqueWeapons = uniqueWeaponMap.values();
        Collection<ResourceLocation> artifacts = artifactMap.values();

        LootUtils.myAddItemsToTable(table, commonWeapons, 1, DungeonsGearConfig.COMMON.COMMON_WEAPON_COMMON_LOOT.get().floatValue(), "common_weapons");
        LootUtils.myAddItemsToTable(table, uniqueWeapons, 1, DungeonsGearConfig.COMMON.UNIQUE_WEAPON_COMMON_LOOT.get().floatValue(), "unique_weapons");
        LootUtils.myAddItemsToTable(table, artifacts,  1, DungeonsGearConfig.COMMON.ARTIFACT_COMMON_LOOT.get().floatValue(), "artifacts");
    }
    private static void addCommonRangedWeaponLootTable(LootTable table){
        Collection<ResourceLocation> commonRangedWeapons = commonRangedWeaponMap.values();
        Collection<ResourceLocation> uniqueRangedWeapons = uniqueRangedWeaponMap.values();
        Collection<ResourceLocation> artifacts = artifactMap.values();

        LootUtils.myAddItemsToTable(table, commonRangedWeapons,  1, DungeonsGearConfig.COMMON.COMMON_WEAPON_COMMON_LOOT.get().floatValue(), "common_ranged_weapons");
        LootUtils.myAddItemsToTable(table, uniqueRangedWeapons,  1, DungeonsGearConfig.COMMON.UNIQUE_WEAPON_COMMON_LOOT.get().floatValue(), "unique_ranged_weapons");
        LootUtils.myAddItemsToTable(table, artifacts,  1, DungeonsGearConfig.COMMON.ARTIFACT_COMMON_LOOT.get().floatValue(), "artifacts");
    }

    private static void addCommonMetalArmorLootTable(LootTable table){
        Collection<ResourceLocation> commonWeapons = commonMetalArmorMap.values();
        Collection<ResourceLocation> uniqueWeapons = uniqueMetalArmorMap.values();
        Collection<ResourceLocation> artifacts = artifactMap.values();

        LootUtils.myAddItemsToTable(table, commonWeapons,  1, DungeonsGearConfig.COMMON.COMMON_ARMOR_COMMON_LOOT.get().floatValue(), "common_metal_armor");
        LootUtils.myAddItemsToTable(table, uniqueWeapons,  1, DungeonsGearConfig.COMMON.UNIQUE_ARMOR_COMMON_LOOT.get().floatValue(), "unique_metal_armor");
        LootUtils.myAddItemsToTable(table, artifacts,  1, DungeonsGearConfig.COMMON.ARTIFACT_COMMON_LOOT.get().floatValue(), "artifacts");
    }
    private static void addCommonLeatherArmorLootTable(LootTable table){
        Collection<ResourceLocation> commonRangedWeapons = commonLeatherArmorMap.values();
        Collection<ResourceLocation> uniqueRangedWeapons = uniqueLeatherArmorMap.values();
        Collection<ResourceLocation> artifacts = artifactMap.values();

        LootUtils.myAddItemsToTable(table, commonRangedWeapons,  1, DungeonsGearConfig.COMMON.COMMON_ARMOR_COMMON_LOOT.get().floatValue(), "common_leather_armor");
        LootUtils.myAddItemsToTable(table, uniqueRangedWeapons,  1, DungeonsGearConfig.COMMON.UNIQUE_ARMOR_COMMON_LOOT.get().floatValue(), "unique_leather_armor");
        LootUtils.myAddItemsToTable(table, artifacts,  1, DungeonsGearConfig.COMMON.ARTIFACT_COMMON_LOOT.get().floatValue(), "artifacts");
    }

    private static void addCommonLootTable(LootTable table){
        Collection<ResourceLocation> commonWeaponCollectionCombined = getCommonWeaponCollection();
        Collection<ResourceLocation> uniqueWeaponCollectionCombined = getUniqueWeaponCollection();
        Collection<ResourceLocation> commonArmorCollectionCombined = getCommonArmorCollection();
        Collection<ResourceLocation> uniqueArmorCollectionCombined = getUniqueArmorCollection();

        Collection<ResourceLocation> artifacts = artifactMap.values();

        LootUtils.myAddItemsToTable(table, commonWeaponCollectionCombined,  1, DungeonsGearConfig.COMMON.COMMON_WEAPON_COMMON_LOOT.get().floatValue(), "common_weapons");
        LootUtils.myAddItemsToTable(table, uniqueWeaponCollectionCombined,  1, DungeonsGearConfig.COMMON.UNIQUE_WEAPON_COMMON_LOOT.get().floatValue(), "unique_weapons");
        LootUtils.myAddItemsToTable(table, commonArmorCollectionCombined,  1, DungeonsGearConfig.COMMON.COMMON_ARMOR_COMMON_LOOT.get().floatValue(), "common_armor");
        LootUtils.myAddItemsToTable(table, uniqueArmorCollectionCombined,  1, DungeonsGearConfig.COMMON.UNIQUE_ARMOR_COMMON_LOOT.get().floatValue(), "unique_armor");
        LootUtils.myAddItemsToTable(table, artifacts,  1, DungeonsGearConfig.COMMON.ARTIFACT_COMMON_LOOT.get().floatValue(), "artifacts");
    }

    private static void addUncommonLootTable(LootTable table){
        Collection<ResourceLocation> commonWeaponCollectionCombined = getCommonWeaponCollection();
        Collection<ResourceLocation> uniqueWeaponCollectionCombined = getUniqueWeaponCollection();
        Collection<ResourceLocation> commonArmorCollectionCombined = getCommonArmorCollection();
        Collection<ResourceLocation> uniqueArmorCollectionCombined = getUniqueArmorCollection();

        Collection<ResourceLocation> artifacts = artifactMap.values();

        LootUtils.myAddItemsToTable(table, commonWeaponCollectionCombined,  1, DungeonsGearConfig.COMMON.COMMON_WEAPON_UNCOMMON_LOOT.get().floatValue(), "common_weapons");
        LootUtils.myAddItemsToTable(table, uniqueWeaponCollectionCombined,  1, DungeonsGearConfig.COMMON.UNIQUE_WEAPON_UNCOMMON_LOOT.get().floatValue(), "unique_weapons");
        LootUtils.myAddItemsToTable(table, commonArmorCollectionCombined,  1, DungeonsGearConfig.COMMON.COMMON_ARMOR_UNCOMMON_LOOT.get().floatValue(), "common_armor");
        LootUtils.myAddItemsToTable(table, uniqueArmorCollectionCombined,  1, DungeonsGearConfig.COMMON.UNIQUE_ARMOR_UNCOMMON_LOOT.get().floatValue(), "unique_armor");
        LootUtils.myAddItemsToTable(table, artifacts,  1, DungeonsGearConfig.COMMON.ARTIFACT_UNCOMMON_LOOT.get().floatValue(), "artifacts");
    }

    private static void addRareLootTable(LootTable table){
        Collection<ResourceLocation> commonWeaponCollectionCombined = getCommonWeaponCollection();
        Collection<ResourceLocation> uniqueWeaponCollectionCombined = getUniqueWeaponCollection();
        Collection<ResourceLocation> commonArmorCollectionCombined = getCommonArmorCollection();
        Collection<ResourceLocation> uniqueArmorCollectionCombined = getUniqueArmorCollection();

        Collection<ResourceLocation> artifacts = artifactMap.values();

        LootUtils.myAddItemsToTable(table, commonWeaponCollectionCombined,  1, DungeonsGearConfig.COMMON.COMMON_WEAPON_RARE_LOOT.get().floatValue(), "common_weapons");
        LootUtils.myAddItemsToTable(table, uniqueWeaponCollectionCombined,  1, DungeonsGearConfig.COMMON.UNIQUE_WEAPON_RARE_LOOT.get().floatValue(), "unique_weapons");
        LootUtils.myAddItemsToTable(table, commonArmorCollectionCombined,  1, DungeonsGearConfig.COMMON.COMMON_ARMOR_RARE_LOOT.get().floatValue(), "common_armor");
        LootUtils.myAddItemsToTable(table, uniqueArmorCollectionCombined,  1, DungeonsGearConfig.COMMON.UNIQUE_ARMOR_RARE_LOOT.get().floatValue(), "unique_armor");
        LootUtils.myAddItemsToTable(table, artifacts,  1, DungeonsGearConfig.COMMON.ARTIFACT_RARE_LOOT.get().floatValue(), "artifacts");
    }

    private static void addSuperRareLootTable(LootTable table){
        Collection<ResourceLocation> commonWeaponCollectionCombined = getCommonWeaponCollection();
        Collection<ResourceLocation> uniqueWeaponCollectionCombined = getUniqueWeaponCollection();
        Collection<ResourceLocation> commonArmorCollectionCombined = getCommonArmorCollection();
        Collection<ResourceLocation> uniqueArmorCollectionCombined = getUniqueArmorCollection();

        Collection<ResourceLocation> artifacts = artifactMap.values();

        LootUtils.myAddItemsToTable(table, commonWeaponCollectionCombined,  1, DungeonsGearConfig.COMMON.COMMON_WEAPON_SUPER_RARE_LOOT.get().floatValue(), "common_weapons");
        LootUtils.myAddItemsToTable(table, uniqueWeaponCollectionCombined,  1, DungeonsGearConfig.COMMON.UNIQUE_WEAPON_SUPER_RARE_LOOT.get().floatValue(), "unique_weapons");
        LootUtils.myAddItemsToTable(table, commonArmorCollectionCombined,  1, DungeonsGearConfig.COMMON.COMMON_ARMOR_SUPER_RARE_LOOT.get().floatValue(), "common_armor");
        LootUtils.myAddItemsToTable(table, uniqueArmorCollectionCombined,  1, DungeonsGearConfig.COMMON.UNIQUE_ARMOR_SUPER_RARE_LOOT.get().floatValue(), "unique_armor");
        LootUtils.myAddItemsToTable(table, artifacts,  1, DungeonsGearConfig.COMMON.ARTIFACT_SUPER_RARE_LOOT.get().floatValue(), "artifacts");
    }

    private static Collection<ResourceLocation> getUniqueArmorCollection() {
        Collection<ResourceLocation> uniqueLeatherArmor = uniqueLeatherArmorMap.values();
        Collection<ResourceLocation> uniqueMetalArmor = uniqueMetalArmorMap.values();
        Iterable<ResourceLocation> combinedUniqueArmor = Iterables.unmodifiableIterable(
                Iterables.concat(uniqueMetalArmor, uniqueLeatherArmor));
        return Lists.newArrayList(combinedUniqueArmor);
    }

    private static Collection<ResourceLocation> getCommonArmorCollection() {
        Collection<ResourceLocation> commonLeatherArmor = commonLeatherArmorMap.values();
        Collection<ResourceLocation> commonMetalArmor = commonMetalArmorMap.values();
        Iterable<ResourceLocation> combinedCommonArmor = Iterables.unmodifiableIterable(
                Iterables.concat(commonMetalArmor, commonLeatherArmor));
        return Lists.newArrayList(combinedCommonArmor);
    }

    private static Collection<ResourceLocation> getUniqueWeaponCollection() {
        Collection<ResourceLocation> uniqueWeapons = uniqueWeaponMap.values();
        Collection<ResourceLocation> uniqueRangedWeapons = uniqueRangedWeaponMap.values();
        Iterable<ResourceLocation> combinedUniqueWeapons = Iterables.unmodifiableIterable(
                Iterables.concat(uniqueWeapons, uniqueRangedWeapons));
        return Lists.newArrayList(combinedUniqueWeapons);
    }
    private static Collection<ResourceLocation> getCommonWeaponCollection() {
        Collection<ResourceLocation> commonWeapons = commonWeaponMap.values();
        Collection<ResourceLocation> commonRangedWeapons = commonRangedWeaponMap.values();
        Iterable<ResourceLocation> combinedCommonWeapons = Iterables.unmodifiableIterable(
                Iterables.concat(commonWeapons, commonRangedWeapons));
        return Lists.newArrayList(combinedCommonWeapons);
    }

    @SubscribeEvent
    public static void onSalvageItem(PlayerInteractEvent.EntityInteract event){
        if(!DungeonsGearConfig.COMMON.ENABLE_SALVAGING.get()) return;
        Entity entity = event.getTarget();
        PlayerEntity playerEntity = event.getPlayer();
        if(entity instanceof VillagerEntity){
            VillagerEntity villagerEntity = (VillagerEntity) entity;
            if(villagerEntity.getVillagerData().getProfession() == VillagerProfession.WEAPONSMITH){
                if(playerEntity.isSneaking()){
                    ItemStack interactStack = playerEntity.getHeldItem(event.getHand());
                    if(commonWeaponMap.containsKey(interactStack.getItem())){
                        handleSalvageTrade(playerEntity, villagerEntity, interactStack, false);
                    }
                    else if(uniqueWeaponMap.containsKey(interactStack.getItem())){
                        handleSalvageTrade(playerEntity, villagerEntity, interactStack, true);
                    }
                }
            }
            if(villagerEntity.getVillagerData().getProfession() == VillagerProfession.FLETCHER){
                if(playerEntity.isSneaking()){
                    ItemStack interactStack = playerEntity.getHeldItem(event.getHand());
                    if(commonRangedWeaponMap.containsKey(interactStack.getItem())){
                        handleSalvageTrade(playerEntity, villagerEntity, interactStack, false);
                    }
                    else if(uniqueRangedWeaponMap.containsKey(interactStack.getItem())){
                        handleSalvageTrade(playerEntity, villagerEntity, interactStack, true);
                    }
                }
            }
            if(villagerEntity.getVillagerData().getProfession() == VillagerProfession.ARMORER){
                if(playerEntity.isSneaking()){
                    ItemStack interactStack = playerEntity.getHeldItem(event.getHand());
                    if(commonMetalArmorMap.containsKey(interactStack.getItem())){
                        handleSalvageTrade(playerEntity, villagerEntity, interactStack, false);
                    }
                    else if(uniqueMetalArmorMap.containsKey(interactStack.getItem())){
                        handleSalvageTrade(playerEntity, villagerEntity, interactStack, true);
                    }
                }
            }
            if(villagerEntity.getVillagerData().getProfession() == VillagerProfession.LEATHERWORKER){
                if(playerEntity.isSneaking()){
                    ItemStack interactStack = playerEntity.getHeldItem(event.getHand());
                    if(commonLeatherArmorMap.containsKey(interactStack.getItem())){
                        handleSalvageTrade(playerEntity, villagerEntity, interactStack, false);
                    }
                    else if(uniqueLeatherArmorMap.containsKey(interactStack.getItem())){
                        handleSalvageTrade(playerEntity, villagerEntity, interactStack, true);
                    }
                }
            }
        }
        if(entity instanceof WanderingTraderEntity){
            WanderingTraderEntity wanderingTraderEntity = (WanderingTraderEntity) entity;
            if(playerEntity.isSneaking()){
                ItemStack interactStack = playerEntity.getHeldItem(event.getHand());
                if(artifactMap.containsKey(interactStack.getItem())){
                    handleSalvageTrade(playerEntity, wanderingTraderEntity, interactStack, true);
                }
            }
        }
    }

    private static void handleSalvageTrade(PlayerEntity playerEntity, AbstractVillagerEntity abstractVillagerEntity, ItemStack interactStack, boolean rareItem) {
        double maxDamage = interactStack.getMaxDamage() * 1.0D;
        DungeonsGear.LOGGER.info("Max damage: " + maxDamage);
        double currentDamage = maxDamage - interactStack.getDamage() * 1.0D;
        DungeonsGear.LOGGER.info("Current damage: " + currentDamage);

        int emeraldReward = 6;
        int damagePenalty = 1;
        if(rareItem){
            emeraldReward *= 2;
            damagePenalty *= 2;
        }

        double damagePercent = currentDamage / maxDamage;
        DungeonsGear.LOGGER.info("Current damage percent: " + damagePercent);
        if (damagePercent < 1.0D){
            DungeonsGear.LOGGER.info("Less than 100% durability penalty!");
            emeraldReward -= damagePenalty;
        }
        if (damagePercent < 0.75D){
            DungeonsGear.LOGGER.info("Less than 75% durability penalty!");
            emeraldReward -= damagePenalty;
        }
        if (damagePercent < 0.5D){
            DungeonsGear.LOGGER.info("Less than 50% durability penalty!");
            emeraldReward -= damagePenalty;
        }
        if (damagePercent < 0.25D){
            DungeonsGear.LOGGER.info("Less than 25% durability penalty!");
            emeraldReward -= damagePenalty;
        }

        interactStack.shrink(1);

        if(abstractVillagerEntity instanceof VillagerEntity){
            VillagerEntity villagerEntity = (VillagerEntity)abstractVillagerEntity;
            villagerEntity.world.playSound((PlayerEntity) null, abstractVillagerEntity.getPosX(), abstractVillagerEntity.getPosY(), abstractVillagerEntity.getPosZ(), SoundEvents.ENTITY_VILLAGER_YES, SoundCategory.PLAYERS, 64.0F, 1.0F);
            villagerEntity.updateReputation(IReputationType.TRADE, playerEntity);
        }
        else if(abstractVillagerEntity instanceof WanderingTraderEntity){
            WanderingTraderEntity wanderingTraderEntity = (WanderingTraderEntity)abstractVillagerEntity;
            wanderingTraderEntity.world.playSound((PlayerEntity) null, wanderingTraderEntity.getPosX(), wanderingTraderEntity.getPosY(), wanderingTraderEntity.getPosZ(), SoundEvents.ENTITY_WANDERING_TRADER_YES, SoundCategory.PLAYERS, 64.0F, 1.0F);
        }

        playerEntity.addItemStackToInventory(new ItemStack(Items.EMERALD, emeraldReward));
        playerEntity.giveExperiencePoints(emeraldReward);
    }
}
