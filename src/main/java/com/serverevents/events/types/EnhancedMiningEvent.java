package com.serverevents.events.types;

import com.serverevents.ServerEvents;
import com.serverevents.events.ServerEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Biome;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.*;

public class EnhancedMiningEvent extends ServerEvent implements Listener {

    private static final Set<Material> DOUBLE_DROP_MATERIALS = Set.of(
        Material.OAK_LOG, Material.BIRCH_LOG, Material.SPRUCE_LOG,
        Material.JUNGLE_LOG, Material.ACACIA_LOG, Material.DARK_OAK_LOG,
        Material.MANGROVE_LOG, Material.CHERRY_LOG,
        Material.DIRT, Material.GRASS_BLOCK, Material.COARSE_DIRT,
        Material.SAND, Material.RED_SAND, Material.GRAVEL,
        Material.SNOW_BLOCK, Material.CLAY,
        Material.STONE, Material.COBBLESTONE, Material.DEEPSLATE
    );

    private static final Set<Material> ORE_MATERIALS = Set.of(
        Material.COAL_ORE, Material.DEEPSLATE_COAL_ORE,
        Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE,
        Material.COPPER_ORE, Material.DEEPSLATE_COPPER_ORE,
        Material.GOLD_ORE, Material.DEEPSLATE_GOLD_ORE,
        Material.REDSTONE_ORE, Material.DEEPSLATE_REDSTONE_ORE,
        Material.LAPIS_ORE, Material.DEEPSLATE_LAPIS_ORE,
        Material.DIAMOND_ORE, Material.DEEPSLATE_DIAMOND_ORE,
        Material.EMERALD_ORE, Material.DEEPSLATE_EMERALD_ORE,
        Material.NETHER_QUARTZ_ORE, Material.NETHER_GOLD_ORE,
        Material.ANCIENT_DEBRIS
    );

    private static final Set<Biome> CAVE_BIOMES = Set.of(Biome.DRIPSTONE_CAVES, Biome.LUSH_CAVES, Biome.DEEP_DARK);

    private static final int ORE_SCAN_RADIUS = 5;
    private static final double ORE_REPLACE_CHANCE = 0.15;

    private static final Map<Biome, Material[]> BIOME_BLOCKS = new LinkedHashMap<>();
    private static final Map<Biome, String> BIOME_NAMES = new LinkedHashMap<>();
    private static final Map<Material, String> MATERIAL_NAMES = new LinkedHashMap<>();

    static {
        BIOME_BLOCKS.put(Biome.PLAINS, new Material[]{Material.OAK_LOG, Material.WHEAT, Material.IRON_ORE, Material.COAL_ORE});
        BIOME_BLOCKS.put(Biome.SUNFLOWER_PLAINS, new Material[]{Material.OAK_LOG, Material.IRON_ORE, Material.COPPER_ORE, Material.COAL_ORE});
        BIOME_BLOCKS.put(Biome.FOREST, new Material[]{Material.OAK_LOG, Material.BIRCH_LOG, Material.IRON_ORE, Material.COAL_ORE});
        BIOME_BLOCKS.put(Biome.FLOWER_FOREST, new Material[]{Material.OAK_LOG, Material.BIRCH_LOG, Material.IRON_ORE, Material.COPPER_ORE});
        BIOME_BLOCKS.put(Biome.BIRCH_FOREST, new Material[]{Material.BIRCH_LOG, Material.OAK_LOG, Material.IRON_ORE, Material.COAL_ORE});
        BIOME_BLOCKS.put(Biome.OLD_GROWTH_BIRCH_FOREST, new Material[]{Material.BIRCH_LOG, Material.IRON_ORE, Material.COPPER_ORE, Material.COAL_ORE});
        BIOME_BLOCKS.put(Biome.DARK_FOREST, new Material[]{Material.DARK_OAK_LOG, Material.OAK_LOG, Material.IRON_ORE, Material.COAL_ORE});
        BIOME_BLOCKS.put(Biome.TAIGA, new Material[]{Material.SPRUCE_LOG, Material.SNOW_BLOCK, Material.IRON_ORE, Material.COAL_ORE});
        BIOME_BLOCKS.put(Biome.OLD_GROWTH_SPRUCE_TAIGA, new Material[]{Material.SPRUCE_LOG, Material.IRON_ORE, Material.COPPER_ORE, Material.COAL_ORE});
        BIOME_BLOCKS.put(Biome.OLD_GROWTH_PINE_TAIGA, new Material[]{Material.SPRUCE_LOG, Material.IRON_ORE, Material.COAL_ORE, Material.COPPER_ORE});
        BIOME_BLOCKS.put(Biome.SNOWY_TAIGA, new Material[]{Material.SPRUCE_LOG, Material.SNOW_BLOCK, Material.IRON_ORE, Material.COAL_ORE});
        BIOME_BLOCKS.put(Biome.SNOWY_PLAINS, new Material[]{Material.SNOW_BLOCK, Material.IRON_ORE, Material.COAL_ORE, Material.COPPER_ORE});
        BIOME_BLOCKS.put(Biome.SNOWY_SLOPES, new Material[]{Material.SNOW_BLOCK, Material.IRON_ORE, Material.EMERALD_ORE, Material.COAL_ORE});
        BIOME_BLOCKS.put(Biome.SNOWY_BEACH, new Material[]{Material.SNOW_BLOCK, Material.SAND, Material.IRON_ORE, Material.COAL_ORE});
        BIOME_BLOCKS.put(Biome.DESERT, new Material[]{Material.SAND, Material.SANDSTONE, Material.GOLD_ORE, Material.COAL_ORE});
        BIOME_BLOCKS.put(Biome.JUNGLE, new Material[]{Material.JUNGLE_LOG, Material.BAMBOO, Material.IRON_ORE, Material.GOLD_ORE});
        BIOME_BLOCKS.put(Biome.SPARSE_JUNGLE, new Material[]{Material.JUNGLE_LOG, Material.IRON_ORE, Material.COPPER_ORE, Material.COAL_ORE});
        BIOME_BLOCKS.put(Biome.BAMBOO_JUNGLE, new Material[]{Material.JUNGLE_LOG, Material.BAMBOO, Material.IRON_ORE, Material.COPPER_ORE});
        BIOME_BLOCKS.put(Biome.SWAMP, new Material[]{Material.OAK_LOG, Material.CLAY, Material.IRON_ORE, Material.COPPER_ORE});
        BIOME_BLOCKS.put(Biome.MANGROVE_SWAMP, new Material[]{Material.MANGROVE_LOG, Material.CLAY, Material.IRON_ORE, Material.COPPER_ORE});
        BIOME_BLOCKS.put(Biome.SAVANNA, new Material[]{Material.ACACIA_LOG, Material.IRON_ORE, Material.GOLD_ORE, Material.COAL_ORE});
        BIOME_BLOCKS.put(Biome.SAVANNA_PLATEAU, new Material[]{Material.ACACIA_LOG, Material.IRON_ORE, Material.GOLD_ORE, Material.EMERALD_ORE});
        BIOME_BLOCKS.put(Biome.WINDSWEPT_SAVANNA, new Material[]{Material.ACACIA_LOG, Material.IRON_ORE, Material.GOLD_ORE, Material.COAL_ORE});
        BIOME_BLOCKS.put(Biome.BADLANDS, new Material[]{Material.RED_SAND, Material.TERRACOTTA, Material.GOLD_ORE, Material.DEEPSLATE_GOLD_ORE});
        BIOME_BLOCKS.put(Biome.WOODED_BADLANDS, new Material[]{Material.RED_SAND, Material.ACACIA_LOG, Material.GOLD_ORE, Material.COPPER_ORE});
        BIOME_BLOCKS.put(Biome.ERODED_BADLANDS, new Material[]{Material.RED_SAND, Material.TERRACOTTA, Material.GOLD_ORE, Material.COPPER_ORE});
        BIOME_BLOCKS.put(Biome.MEADOW, new Material[]{Material.OAK_LOG, Material.IRON_ORE, Material.COPPER_ORE, Material.EMERALD_ORE});
        BIOME_BLOCKS.put(Biome.CHERRY_GROVE, new Material[]{Material.CHERRY_LOG, Material.IRON_ORE, Material.COPPER_ORE, Material.COAL_ORE});
        BIOME_BLOCKS.put(Biome.GROVE, new Material[]{Material.SPRUCE_LOG, Material.SNOW_BLOCK, Material.IRON_ORE, Material.EMERALD_ORE});
        BIOME_BLOCKS.put(Biome.STONY_PEAKS, new Material[]{Material.STONE, Material.IRON_ORE, Material.COAL_ORE, Material.EMERALD_ORE});
        BIOME_BLOCKS.put(Biome.JAGGED_PEAKS, new Material[]{Material.STONE, Material.IRON_ORE, Material.EMERALD_ORE, Material.COAL_ORE});
        BIOME_BLOCKS.put(Biome.FROZEN_PEAKS, new Material[]{Material.SNOW_BLOCK, Material.IRON_ORE, Material.EMERALD_ORE, Material.COAL_ORE});
        BIOME_BLOCKS.put(Biome.STONY_SHORE, new Material[]{Material.STONE, Material.GRAVEL, Material.IRON_ORE, Material.COAL_ORE});
        BIOME_BLOCKS.put(Biome.WINDSWEPT_HILLS, new Material[]{Material.STONE, Material.IRON_ORE, Material.COAL_ORE, Material.EMERALD_ORE});
        BIOME_BLOCKS.put(Biome.WINDSWEPT_FOREST, new Material[]{Material.OAK_LOG, Material.IRON_ORE, Material.COAL_ORE, Material.EMERALD_ORE});
        BIOME_BLOCKS.put(Biome.WINDSWEPT_GRAVELLY_HILLS, new Material[]{Material.GRAVEL, Material.IRON_ORE, Material.COAL_ORE, Material.EMERALD_ORE});
        BIOME_BLOCKS.put(Biome.DRIPSTONE_CAVES, new Material[]{Material.COPPER_ORE, Material.IRON_ORE, Material.DEEPSLATE_COPPER_ORE, Material.DEEPSLATE_IRON_ORE});
        BIOME_BLOCKS.put(Biome.LUSH_CAVES, new Material[]{Material.IRON_ORE, Material.COPPER_ORE, Material.DEEPSLATE_IRON_ORE, Material.DEEPSLATE_COPPER_ORE});
        BIOME_BLOCKS.put(Biome.DEEP_DARK, new Material[]{Material.DIAMOND_ORE, Material.DEEPSLATE_DIAMOND_ORE, Material.DEEPSLATE_IRON_ORE, Material.DEEPSLATE_GOLD_ORE});

        BIOME_NAMES.put(Biome.PLAINS, "Plains");
        BIOME_NAMES.put(Biome.SUNFLOWER_PLAINS, "Sunflower Plains");
        BIOME_NAMES.put(Biome.FOREST, "Forest");
        BIOME_NAMES.put(Biome.FLOWER_FOREST, "Flower Forest");
        BIOME_NAMES.put(Biome.BIRCH_FOREST, "Birch Forest");
        BIOME_NAMES.put(Biome.OLD_GROWTH_BIRCH_FOREST, "Old Growth Birch Forest");
        BIOME_NAMES.put(Biome.DARK_FOREST, "Dark Forest");
        BIOME_NAMES.put(Biome.TAIGA, "Taiga");
        BIOME_NAMES.put(Biome.OLD_GROWTH_SPRUCE_TAIGA, "Old Growth Spruce Taiga");
        BIOME_NAMES.put(Biome.OLD_GROWTH_PINE_TAIGA, "Old Growth Pine Taiga");
        BIOME_NAMES.put(Biome.SNOWY_TAIGA, "Snowy Taiga");
        BIOME_NAMES.put(Biome.SNOWY_PLAINS, "Snowy Plains");
        BIOME_NAMES.put(Biome.SNOWY_SLOPES, "Snowy Slopes");
        BIOME_NAMES.put(Biome.SNOWY_BEACH, "Snowy Beach");
        BIOME_NAMES.put(Biome.DESERT, "Desert");
        BIOME_NAMES.put(Biome.JUNGLE, "Jungle");
        BIOME_NAMES.put(Biome.SPARSE_JUNGLE, "Sparse Jungle");
        BIOME_NAMES.put(Biome.BAMBOO_JUNGLE, "Bamboo Jungle");
        BIOME_NAMES.put(Biome.SWAMP, "Swamp");
        BIOME_NAMES.put(Biome.MANGROVE_SWAMP, "Mangrove Swamp");
        BIOME_NAMES.put(Biome.SAVANNA, "Savanna");
        BIOME_NAMES.put(Biome.SAVANNA_PLATEAU, "Savanna Plateau");
        BIOME_NAMES.put(Biome.WINDSWEPT_SAVANNA, "Windswept Savanna");
        BIOME_NAMES.put(Biome.BADLANDS, "Badlands");
        BIOME_NAMES.put(Biome.WOODED_BADLANDS, "Wooded Badlands");
        BIOME_NAMES.put(Biome.ERODED_BADLANDS, "Eroded Badlands");
        BIOME_NAMES.put(Biome.MEADOW, "Meadow");
        BIOME_NAMES.put(Biome.CHERRY_GROVE, "Cherry Grove");
        BIOME_NAMES.put(Biome.GROVE, "Grove");
        BIOME_NAMES.put(Biome.STONY_PEAKS, "Stony Peaks");
        BIOME_NAMES.put(Biome.JAGGED_PEAKS, "Jagged Peaks");
        BIOME_NAMES.put(Biome.FROZEN_PEAKS, "Frozen Peaks");
        BIOME_NAMES.put(Biome.STONY_SHORE, "Stony Shore");
        BIOME_NAMES.put(Biome.WINDSWEPT_HILLS, "Windswept Hills");
        BIOME_NAMES.put(Biome.WINDSWEPT_FOREST, "Windswept Forest");
        BIOME_NAMES.put(Biome.WINDSWEPT_GRAVELLY_HILLS, "Windswept Gravelly Hills");
        BIOME_NAMES.put(Biome.DRIPSTONE_CAVES, "Dripstone Caves");
        BIOME_NAMES.put(Biome.LUSH_CAVES, "Lush Caves");
        BIOME_NAMES.put(Biome.DEEP_DARK, "Deep Dark");

        MATERIAL_NAMES.put(Material.OAK_LOG, "Oak Log");
        MATERIAL_NAMES.put(Material.BIRCH_LOG, "Birch Log");
        MATERIAL_NAMES.put(Material.SPRUCE_LOG, "Spruce Log");
        MATERIAL_NAMES.put(Material.JUNGLE_LOG, "Jungle Log");
        MATERIAL_NAMES.put(Material.ACACIA_LOG, "Acacia Log");
        MATERIAL_NAMES.put(Material.DARK_OAK_LOG, "Dark Oak Log");
        MATERIAL_NAMES.put(Material.MANGROVE_LOG, "Mangrove Log");
        MATERIAL_NAMES.put(Material.CHERRY_LOG, "Cherry Log");
        MATERIAL_NAMES.put(Material.WHEAT, "Wheat");
        MATERIAL_NAMES.put(Material.BAMBOO, "Bamboo");
        MATERIAL_NAMES.put(Material.DIRT, "Dirt");
        MATERIAL_NAMES.put(Material.GRASS_BLOCK, "Grass Block");
        MATERIAL_NAMES.put(Material.COARSE_DIRT, "Coarse Dirt");
        MATERIAL_NAMES.put(Material.SNOW_BLOCK, "Snow Block");
        MATERIAL_NAMES.put(Material.SAND, "Sand");
        MATERIAL_NAMES.put(Material.RED_SAND, "Red Sand");
        MATERIAL_NAMES.put(Material.SANDSTONE, "Sandstone");
        MATERIAL_NAMES.put(Material.GRAVEL, "Gravel");
        MATERIAL_NAMES.put(Material.CLAY, "Clay");
        MATERIAL_NAMES.put(Material.STONE, "Stone");
        MATERIAL_NAMES.put(Material.COBBLESTONE, "Cobblestone");
        MATERIAL_NAMES.put(Material.DEEPSLATE, "Deepslate");
        MATERIAL_NAMES.put(Material.TERRACOTTA, "Terracotta");
        MATERIAL_NAMES.put(Material.COAL_ORE, "Coal Ore");
        MATERIAL_NAMES.put(Material.DEEPSLATE_COAL_ORE, "Deepslate Coal Ore");
        MATERIAL_NAMES.put(Material.IRON_ORE, "Iron Ore");
        MATERIAL_NAMES.put(Material.DEEPSLATE_IRON_ORE, "Deepslate Iron Ore");
        MATERIAL_NAMES.put(Material.COPPER_ORE, "Copper Ore");
        MATERIAL_NAMES.put(Material.DEEPSLATE_COPPER_ORE, "Deepslate Copper Ore");
        MATERIAL_NAMES.put(Material.GOLD_ORE, "Gold Ore");
        MATERIAL_NAMES.put(Material.DEEPSLATE_GOLD_ORE, "Deepslate Gold Ore");
        MATERIAL_NAMES.put(Material.REDSTONE_ORE, "Redstone Ore");
        MATERIAL_NAMES.put(Material.DEEPSLATE_REDSTONE_ORE, "Deepslate Redstone Ore");
        MATERIAL_NAMES.put(Material.LAPIS_ORE, "Lapis Ore");
        MATERIAL_NAMES.put(Material.DEEPSLATE_LAPIS_ORE, "Deepslate Lapis Ore");
        MATERIAL_NAMES.put(Material.DIAMOND_ORE, "Diamond Ore");
        MATERIAL_NAMES.put(Material.DEEPSLATE_DIAMOND_ORE, "Deepslate Diamond Ore");
        MATERIAL_NAMES.put(Material.EMERALD_ORE, "Emerald Ore");
        MATERIAL_NAMES.put(Material.DEEPSLATE_EMERALD_ORE, "Deepslate Emerald Ore");
    }

    private final ServerEvents plugin;
    private Material targetBlock;
    private Biome targetBiome;

    public EnhancedMiningEvent(ServerEvents plugin) {
        super("Enhanced Mining");
        this.plugin = plugin;
    }

    @Override
    public void start() {
        List<Biome> biomes = new ArrayList<>(BIOME_BLOCKS.keySet());
        targetBiome = biomes.get(new Random().nextInt(biomes.size()));
        Material[] blocks = BIOME_BLOCKS.get(targetBiome);
        targetBlock = blocks[new Random().nextInt(blocks.length)];

        String biomeName = BIOME_NAMES.getOrDefault(targetBiome, targetBiome.toString());
        String materialName = MATERIAL_NAMES.getOrDefault(targetBlock, targetBlock.name());

        bossBar = Bukkit.createBossBar(
            "§aEnhanced Mining §f| " + biomeName + " — " + materialName + " §7(ores boosted nearby)",
            BarColor.GREEN,
            BarStyle.SOLID
        );

        bossBar.setProgress(1.0);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void stop() {
        if (bossBar != null) bossBar.removeAll();
        HandlerList.unregisterAll(this);
    }

    @Override
    public void tick() {}

    @Override
    public String getDiscordDescription() {
        String biome = BIOME_NAMES.getOrDefault(targetBiome, targetBiome != null ? targetBiome.toString() : "");
        String material = MATERIAL_NAMES.getOrDefault(targetBlock, targetBlock != null ? targetBlock.name() : "");
        return "Biome: **" + biome + "**\nResource: **" + material + "** — ore veins are denser in this biome!";
    }

    @Override
    public void onPlayerJoin(Player player) {
        if (bossBar != null && preferences != null && preferences.isEnabled(player.getUniqueId())) {
            bossBar.removePlayer(player);
            bossBar.addPlayer(player);
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.5f);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        org.bukkit.block.Block broken = event.getBlock();
        if (broken.getType() != targetBlock) return;

        Biome effectiveBiome = CAVE_BIOMES.contains(targetBiome)
            ? broken.getBiome()
            : getSurfaceBiome(broken);

        if (effectiveBiome != targetBiome) return;

        if (ORE_MATERIALS.contains(targetBlock)) {
            boostNearbyOres(broken, targetBlock);
        } else if (DOUBLE_DROP_MATERIALS.contains(targetBlock)) {
            doubleDrops(event);
        }
    }

    private Biome getSurfaceBiome(org.bukkit.block.Block block) {
        org.bukkit.World world = block.getWorld();
        int x = block.getX(), z = block.getZ();
        int maxY = world.getMaxHeight();
        for (int y = maxY - 1; y > block.getY(); y--) {
            Biome biome = world.getBiome(x, y, z);
            if (!CAVE_BIOMES.contains(biome)) return biome;
        }
        return block.getBiome();
    }

    private void doubleDrops(BlockBreakEvent event) {
        org.bukkit.block.Block block = event.getBlock();
        event.setDropItems(true);
        block.getDrops(event.getPlayer().getInventory().getItemInMainHand()).forEach(drop -> {
            drop.setAmount(drop.getAmount() * 2);
            block.getWorld().dropItemNaturally(block.getLocation(), drop);
        });
    }

    private void boostNearbyOres(org.bukkit.block.Block origin, Material oreType) {
        Material deepslateVariant = getDeepslateVariant(oreType);
        Random rng = new Random();
        int r = ORE_SCAN_RADIUS;

        for (int dx = -r; dx <= r; dx++) {
            for (int dy = -r; dy <= r; dy++) {
                for (int dz = -r; dz <= r; dz++) {
                    if (dx == 0 && dy == 0 && dz == 0) continue;
                    org.bukkit.block.Block nearby = origin.getRelative(dx, dy, dz);
                    Material type = nearby.getType();
                    if ((type == Material.STONE || type == Material.DEEPSLATE || type == Material.TUFF) && rng.nextDouble() < ORE_REPLACE_CHANCE) {
                        Material replacement = (type == Material.DEEPSLATE || type == Material.TUFF) && deepslateVariant != null
                            ? deepslateVariant
                            : oreType;
                        nearby.setType(replacement);
                    }
                }
            }
        }
    }

    private Material getDeepslateVariant(Material ore) {
        return switch (ore) {
            case COAL_ORE -> Material.DEEPSLATE_COAL_ORE;
            case IRON_ORE -> Material.DEEPSLATE_IRON_ORE;
            case COPPER_ORE -> Material.DEEPSLATE_COPPER_ORE;
            case GOLD_ORE -> Material.DEEPSLATE_GOLD_ORE;
            case REDSTONE_ORE -> Material.DEEPSLATE_REDSTONE_ORE;
            case LAPIS_ORE -> Material.DEEPSLATE_LAPIS_ORE;
            case DIAMOND_ORE -> Material.DEEPSLATE_DIAMOND_ORE;
            case EMERALD_ORE -> Material.DEEPSLATE_EMERALD_ORE;
            default -> null;
        };
    }
}
