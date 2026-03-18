package com.serverevents.events.types;

import com.serverevents.ServerEvents;
import com.serverevents.events.ServerEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Biome;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class MobSpawnBoostEvent extends ServerEvent implements Listener {
    private final ServerEvents plugin;
    private final Random random = new Random();

    private Biome targetBiome;
    private EntityType targetMob;
    private ResourceBonus targetResource;
    private BukkitTask spawnTask;

    private static final Map<Biome, BiomeProfile> BIOME_PROFILES = new LinkedHashMap<>();

    static {
        addProfile(Biome.PLAINS, "Равнины",
            Arrays.asList(EntityType.ZOMBIE, EntityType.SKELETON, EntityType.PILLAGER,
                EntityType.COW, EntityType.SHEEP, EntityType.HORSE),
            List.of(ore("Железная руда", Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE),
                ore("Угольная руда", Material.COAL_ORE, Material.DEEPSLATE_COAL_ORE),
                ore("Красная руда", Material.REDSTONE_ORE, Material.DEEPSLATE_REDSTONE_ORE)));

        addProfile(Biome.SUNFLOWER_PLAINS, "Подсолнечные равнины",
            Arrays.asList(EntityType.ZOMBIE, EntityType.SKELETON, EntityType.PILLAGER,
                EntityType.COW, EntityType.SHEEP, EntityType.BEE),
            List.of(ore("Железная руда", Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE),
                ore("Угольная руда", Material.COAL_ORE, Material.DEEPSLATE_COAL_ORE),
                ore("Красная руда", Material.REDSTONE_ORE, Material.DEEPSLATE_REDSTONE_ORE)));

        addProfile(Biome.FOREST, "Лес",
            Arrays.asList(EntityType.ZOMBIE, EntityType.SPIDER, EntityType.WITCH,
                EntityType.FOX, EntityType.BEE, EntityType.RABBIT),
            List.of(ore("Угольная руда", Material.COAL_ORE, Material.DEEPSLATE_COAL_ORE),
                ore("Железная руда", Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE),
                ore("Медная руда", Material.COPPER_ORE, Material.DEEPSLATE_COPPER_ORE)));

        addProfile(Biome.BIRCH_FOREST, "Берёзовый лес",
            Arrays.asList(EntityType.ZOMBIE, EntityType.SPIDER, EntityType.WITCH,
                EntityType.BEE, EntityType.RABBIT, EntityType.FOX),
            List.of(ore("Угольная руда", Material.COAL_ORE, Material.DEEPSLATE_COAL_ORE),
                ore("Железная руда", Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE),
                ore("Медная руда", Material.COPPER_ORE, Material.DEEPSLATE_COPPER_ORE)));

        addProfile(Biome.TAIGA, "Тайга",
            Arrays.asList(EntityType.SKELETON, EntityType.STRAY, EntityType.WOLF,
                EntityType.FOX, EntityType.RABBIT),
            List.of(ore("Железная руда", Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE),
                ore("Лазуритовая руда", Material.LAPIS_ORE, Material.DEEPSLATE_LAPIS_ORE),
                ore("Угольная руда", Material.COAL_ORE, Material.DEEPSLATE_COAL_ORE)));

        addProfile(Biome.OLD_GROWTH_PINE_TAIGA, "Старая тайга (сосна)",
            Arrays.asList(EntityType.SKELETON, EntityType.STRAY, EntityType.WOLF,
                EntityType.FOX, EntityType.RABBIT, EntityType.POLAR_BEAR),
            List.of(ore("Железная руда", Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE),
                ore("Лазуритовая руда", Material.LAPIS_ORE, Material.DEEPSLATE_LAPIS_ORE),
                ore("Угольная руда", Material.COAL_ORE, Material.DEEPSLATE_COAL_ORE)));

        addProfile(Biome.OLD_GROWTH_SPRUCE_TAIGA, "Старая тайга (ель)",
            Arrays.asList(EntityType.SKELETON, EntityType.STRAY, EntityType.WOLF,
                EntityType.FOX, EntityType.RABBIT, EntityType.POLAR_BEAR),
            List.of(ore("Железная руда", Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE),
                ore("Лазуритовая руда", Material.LAPIS_ORE, Material.DEEPSLATE_LAPIS_ORE),
                ore("Угольная руда", Material.COAL_ORE, Material.DEEPSLATE_COAL_ORE)));

        addProfile(Biome.DESERT, "Пустыня",
            Arrays.asList(EntityType.HUSK, EntityType.SKELETON, EntityType.PILLAGER,
                EntityType.CAMEL, EntityType.RABBIT),
            List.of(ore("Золотая руда", Material.GOLD_ORE, Material.DEEPSLATE_GOLD_ORE),
                ore("Красная руда", Material.REDSTONE_ORE, Material.DEEPSLATE_REDSTONE_ORE),
                ore("Железная руда", Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE)));

        addProfile(Biome.JUNGLE, "Джунгли",
            Arrays.asList(EntityType.SPIDER, EntityType.ZOMBIE, EntityType.WITCH,
                EntityType.PARROT, EntityType.OCELOT),
            List.of(ore("Медная руда", Material.COPPER_ORE, Material.DEEPSLATE_COPPER_ORE),
                ore("Лазуритовая руда", Material.LAPIS_ORE, Material.DEEPSLATE_LAPIS_ORE),
                ore("Алмазная руда", Material.DIAMOND_ORE, Material.DEEPSLATE_DIAMOND_ORE)));

        addProfile(Biome.BAMBOO_JUNGLE, "Бамбуковые джунгли",
            Arrays.asList(EntityType.SPIDER, EntityType.ZOMBIE, EntityType.WITCH,
                EntityType.PANDA, EntityType.PARROT, EntityType.OCELOT),
            List.of(ore("Медная руда", Material.COPPER_ORE, Material.DEEPSLATE_COPPER_ORE),
                ore("Лазуритовая руда", Material.LAPIS_ORE, Material.DEEPSLATE_LAPIS_ORE),
                ore("Алмазная руда", Material.DIAMOND_ORE, Material.DEEPSLATE_DIAMOND_ORE)));

        addProfile(Biome.SWAMP, "Болото",
            Arrays.asList(EntityType.WITCH, EntityType.SLIME, EntityType.ZOMBIE,
                EntityType.FROG, EntityType.TURTLE),
            List.of(ore("Медная руда", Material.COPPER_ORE, Material.DEEPSLATE_COPPER_ORE),
                ore("Красная руда", Material.REDSTONE_ORE, Material.DEEPSLATE_REDSTONE_ORE),
                ore("Угольная руда", Material.COAL_ORE, Material.DEEPSLATE_COAL_ORE)));

        addProfile(Biome.MANGROVE_SWAMP, "Мангровые болота",
            Arrays.asList(EntityType.WITCH, EntityType.SLIME, EntityType.ZOMBIE,
                EntityType.FROG, EntityType.TURTLE),
            List.of(ore("Медная руда", Material.COPPER_ORE, Material.DEEPSLATE_COPPER_ORE),
                ore("Красная руда", Material.REDSTONE_ORE, Material.DEEPSLATE_REDSTONE_ORE),
                ore("Угольная руда", Material.COAL_ORE, Material.DEEPSLATE_COAL_ORE)));

        addProfile(Biome.SAVANNA, "Саванна",
            Arrays.asList(EntityType.ZOMBIE, EntityType.PILLAGER, EntityType.VINDICATOR,
                EntityType.HORSE, EntityType.DONKEY, EntityType.LLAMA),
            List.of(ore("Железная руда", Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE),
                ore("Золотая руда", Material.GOLD_ORE, Material.DEEPSLATE_GOLD_ORE),
                ore("Изумрудная руда", Material.EMERALD_ORE, Material.DEEPSLATE_EMERALD_ORE)));

        addProfile(Biome.SAVANNA_PLATEAU, "Плато саванны",
            Arrays.asList(EntityType.ZOMBIE, EntityType.PILLAGER, EntityType.VINDICATOR,
                EntityType.HORSE, EntityType.LLAMA),
            List.of(ore("Железная руда", Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE),
                ore("Золотая руда", Material.GOLD_ORE, Material.DEEPSLATE_GOLD_ORE),
                ore("Изумрудная руда", Material.EMERALD_ORE, Material.DEEPSLATE_EMERALD_ORE)));

        addProfile(Biome.BADLANDS, "Пустошь",
            Arrays.asList(EntityType.ZOMBIE, EntityType.SKELETON, EntityType.PILLAGER,
                EntityType.ARMADILLO, EntityType.HORSE),
            List.of(ore("Золотая руда", Material.GOLD_ORE, Material.DEEPSLATE_GOLD_ORE),
                ore("Медная руда", Material.COPPER_ORE, Material.DEEPSLATE_COPPER_ORE),
                ore("Алмазная руда", Material.DIAMOND_ORE, Material.DEEPSLATE_DIAMOND_ORE)));

        addProfile(Biome.ERODED_BADLANDS, "Эродированная пустошь",
            Arrays.asList(EntityType.ZOMBIE, EntityType.SKELETON, EntityType.PILLAGER,
                EntityType.ARMADILLO, EntityType.HORSE),
            List.of(ore("Золотая руда", Material.GOLD_ORE, Material.DEEPSLATE_GOLD_ORE),
                ore("Медная руда", Material.COPPER_ORE, Material.DEEPSLATE_COPPER_ORE),
                ore("Алмазная руда", Material.DIAMOND_ORE, Material.DEEPSLATE_DIAMOND_ORE)));

        addProfile(Biome.SNOWY_PLAINS, "Заснеженные равнины",
            Arrays.asList(EntityType.STRAY, EntityType.SKELETON, EntityType.POLAR_BEAR,
                EntityType.RABBIT, EntityType.COW),
            List.of(ore("Железная руда", Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE),
                ore("Угольная руда", Material.COAL_ORE, Material.DEEPSLATE_COAL_ORE),
                ore("Изумрудная руда", Material.EMERALD_ORE, Material.DEEPSLATE_EMERALD_ORE)));

        addProfile(Biome.SNOWY_TAIGA, "Заснеженная тайга",
            Arrays.asList(EntityType.STRAY, EntityType.SKELETON, EntityType.WOLF,
                EntityType.POLAR_BEAR, EntityType.FOX, EntityType.RABBIT),
            List.of(ore("Железная руда", Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE),
                ore("Угольная руда", Material.COAL_ORE, Material.DEEPSLATE_COAL_ORE),
                ore("Изумрудная руда", Material.EMERALD_ORE, Material.DEEPSLATE_EMERALD_ORE)));

        addProfile(Biome.FROZEN_PEAKS, "Заледенелые вершины",
            Arrays.asList(EntityType.STRAY, EntityType.SKELETON, EntityType.POLAR_BEAR,
                EntityType.GOAT),
            List.of(ore("Железная руда", Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE),
                ore("Угольная руда", Material.COAL_ORE, Material.DEEPSLATE_COAL_ORE),
                ore("Изумрудная руда", Material.EMERALD_ORE, Material.DEEPSLATE_EMERALD_ORE)));

        addProfile(Biome.JAGGED_PEAKS, "Зубчатые вершины",
            Arrays.asList(EntityType.STRAY, EntityType.SKELETON,
                EntityType.GOAT),
            List.of(ore("Железная руда", Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE),
                ore("Угольная руда", Material.COAL_ORE, Material.DEEPSLATE_COAL_ORE),
                ore("Изумрудная руда", Material.EMERALD_ORE, Material.DEEPSLATE_EMERALD_ORE)));

        addProfile(Biome.STONY_PEAKS, "Скалистые вершины",
            Arrays.asList(EntityType.SKELETON, EntityType.CREEPER,
                EntityType.GOAT),
            List.of(ore("Железная руда", Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE),
                ore("Угольная руда", Material.COAL_ORE, Material.DEEPSLATE_COAL_ORE),
                ore("Изумрудная руда", Material.EMERALD_ORE, Material.DEEPSLATE_EMERALD_ORE)));

        addProfile(Biome.MEADOW, "Луг",
            Arrays.asList(EntityType.ZOMBIE, EntityType.SKELETON, EntityType.PILLAGER,
                EntityType.DONKEY, EntityType.BEE, EntityType.SHEEP),
            List.of(ore("Железная руда", Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE),
                ore("Угольная руда", Material.COAL_ORE, Material.DEEPSLATE_COAL_ORE),
                ore("Медная руда", Material.COPPER_ORE, Material.DEEPSLATE_COPPER_ORE)));

        addProfile(Biome.MUSHROOM_FIELDS, "Грибные поля",
            Arrays.asList(EntityType.ZOMBIE, EntityType.SKELETON,
                EntityType.MOOSHROOM),
            List.of(ore("Железная руда", Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE),
                ore("Угольная руда", Material.COAL_ORE, Material.DEEPSLATE_COAL_ORE),
                ore("Медная руда", Material.COPPER_ORE, Material.DEEPSLATE_COPPER_ORE)));

        addProfile(Biome.DRIPSTONE_CAVES, "Пещеры капельника",
            Arrays.asList(EntityType.DROWNED, EntityType.ZOMBIE, EntityType.SKELETON,
                EntityType.BAT),
            List.of(ore("Медная руда", Material.COPPER_ORE, Material.DEEPSLATE_COPPER_ORE),
                ore("Железная руда", Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE),
                ore("Угольная руда", Material.COAL_ORE, Material.DEEPSLATE_COAL_ORE)));

        addProfile(Biome.LUSH_CAVES, "Пышные пещеры",
            Arrays.asList(EntityType.ZOMBIE, EntityType.SKELETON, EntityType.SPIDER,
                EntityType.AXOLOTL, EntityType.GLOW_SQUID),
            List.of(ore("Медная руда", Material.COPPER_ORE, Material.DEEPSLATE_COPPER_ORE),
                ore("Золотая руда", Material.GOLD_ORE, Material.DEEPSLATE_GOLD_ORE),
                ore("Алмазная руда", Material.DIAMOND_ORE, Material.DEEPSLATE_DIAMOND_ORE)));

        addProfile(Biome.DEEP_DARK, "Глубокая тьма",
            Arrays.asList(EntityType.ZOMBIE, EntityType.SKELETON, EntityType.CREEPER,
                EntityType.WARDEN),
            List.of(ore("Алмазная руда", Material.DIAMOND_ORE, Material.DEEPSLATE_DIAMOND_ORE),
                ore("Красная руда", Material.REDSTONE_ORE, Material.DEEPSLATE_REDSTONE_ORE),
                ore("Лазуритовая руда", Material.LAPIS_ORE, Material.DEEPSLATE_LAPIS_ORE)));

        addProfile(Biome.NETHER_WASTES, "Адские пустоши",
            Arrays.asList(EntityType.ZOMBIFIED_PIGLIN, EntityType.GHAST, EntityType.MAGMA_CUBE,
                EntityType.STRIDER),
            List.of(single("Кварцевая руда Незера", Material.NETHER_QUARTZ_ORE),
                single("Золотая руда Незера", Material.NETHER_GOLD_ORE),
                single("Древние обломки", Material.ANCIENT_DEBRIS)));

        addProfile(Biome.SOUL_SAND_VALLEY, "Долина душ",
            Arrays.asList(EntityType.SKELETON, EntityType.GHAST, EntityType.ENDERMAN,
                EntityType.STRIDER),
            List.of(single("Кварцевая руда Незера", Material.NETHER_QUARTZ_ORE),
                single("Золотая руда Незера", Material.NETHER_GOLD_ORE),
                single("Древние обломки", Material.ANCIENT_DEBRIS)));

        addProfile(Biome.CRIMSON_FOREST, "Багровый лес",
            Arrays.asList(EntityType.HOGLIN, EntityType.PIGLIN, EntityType.ZOMBIFIED_PIGLIN,
                EntityType.STRIDER),
            List.of(single("Кварцевая руда Незера", Material.NETHER_QUARTZ_ORE),
                single("Золотая руда Незера", Material.NETHER_GOLD_ORE),
                single("Древние обломки", Material.ANCIENT_DEBRIS)));

        addProfile(Biome.WARPED_FOREST, "Искаженный лес",
            Arrays.asList(EntityType.ENDERMAN, EntityType.STRIDER, EntityType.ZOMBIFIED_PIGLIN),
            List.of(single("Кварцевая руда Незера", Material.NETHER_QUARTZ_ORE),
                single("Золотая руда Незера", Material.NETHER_GOLD_ORE),
                single("Древние обломки", Material.ANCIENT_DEBRIS)));

        addProfile(Biome.BASALT_DELTAS, "Базальтовые дельты",
            Arrays.asList(EntityType.MAGMA_CUBE, EntityType.GHAST, EntityType.ZOMBIFIED_PIGLIN,
                EntityType.STRIDER),
            List.of(single("Кварцевая руда Незера", Material.NETHER_QUARTZ_ORE),
                single("Золотая руда Незера", Material.NETHER_GOLD_ORE),
                single("Древние обломки", Material.ANCIENT_DEBRIS)));
    }

    public MobSpawnBoostEvent(ServerEvents plugin) {
        super("Биомный всплеск");
        this.plugin = plugin;
    }

    @Override
    public void start() {
        List<Biome> biomes = new ArrayList<>(BIOME_PROFILES.keySet());
        targetBiome = biomes.get(random.nextInt(biomes.size()));
        BiomeProfile profile = BIOME_PROFILES.get(targetBiome);
        targetMob = profile.mobs.get(random.nextInt(profile.mobs.size()));
        targetResource = profile.resources.get(random.nextInt(profile.resources.size()));
        String themeColor = getBiomeThemeColor(targetBiome);

        bossBar = Bukkit.createBossBar(
            themeColor + "Всплеск | " + profile.name + " §f— " + targetResource.name,
            getBiomeBarColor(targetBiome),
            BarStyle.SEGMENTED_6
        );
        bossBar.setProgress(1.0);
        Bukkit.getPluginManager().registerEvents(this, plugin);

        spawnTask = Bukkit.getScheduler().runTaskTimer(plugin, this::spawnNearPlayers, 80L, 80L);
    }

    @Override
    public void stop() {
        if (bossBar != null) bossBar.removeAll();
        if (spawnTask != null) spawnTask.cancel();
        HandlerList.unregisterAll(this);
    }

    @Override
    public void tick() {}

    @Override
    public String getDiscordDescription() {
        BiomeProfile profile = targetBiome != null ? BIOME_PROFILES.get(targetBiome) : null;
        String biome = profile != null ? profile.name : (targetBiome != null ? targetBiome.toString() : "");
        String resource = targetResource != null ? targetResource.name : "";
        return "Биом: **" + biome + "**\nРесурс: **" + resource + "**";
    }

    @Override
    public void onPlayerJoin(Player player) {
        if (bossBar != null && preferences != null && preferences.isEnabled(player.getUniqueId())) {
            bossBar.removePlayer(player);
            bossBar.addPlayer(player);
            player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 0.5f, 1.0f);
        }
    }

    private void spawnNearPlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getWorld().getBiome(player.getLocation()) != targetBiome) continue;
            Location loc = randomOffset(player.getLocation(), 12, 24);
            if (!loc.getChunk().isLoaded()) continue;
            LivingEntity mob = (LivingEntity) player.getWorld().spawnEntity(loc, targetMob);
            mob.setRemoveWhenFarAway(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (targetBiome == null || targetResource == null) return;

        org.bukkit.block.Block block = event.getBlock();
        if (block.getBiome() != targetBiome) return;
        if (!targetResource.blocks.contains(block.getType())) return;

        ItemStack tool = event.getPlayer().getInventory().getItemInMainHand();
        Collection<ItemStack> baseDrops = block.getDrops(tool, event.getPlayer());
        event.setDropItems(false);

        for (ItemStack drop : baseDrops) {
            ItemStack boosted = drop.clone();
            boosted.setAmount(drop.getAmount() * 2);
            block.getWorld().dropItemNaturally(block.getLocation(), boosted);
        }
    }

    private Location randomOffset(Location origin, int minR, int maxR) {
        double angle = random.nextDouble() * 2 * Math.PI;
        double dist = minR + random.nextInt(maxR - minR);
        return origin.clone().add(Math.cos(angle) * dist, 0, Math.sin(angle) * dist);
    }

    private String getBiomeThemeColor(Biome biome) {
        if (biome == Biome.DESERT || biome == Biome.BADLANDS || biome == Biome.ERODED_BADLANDS
            || biome == Biome.SAVANNA || biome == Biome.SAVANNA_PLATEAU) return "§6";
        if (biome == Biome.FOREST || biome == Biome.BIRCH_FOREST || biome == Biome.TAIGA
            || biome == Biome.OLD_GROWTH_PINE_TAIGA || biome == Biome.OLD_GROWTH_SPRUCE_TAIGA) return "§2";
        if (biome == Biome.JUNGLE || biome == Biome.BAMBOO_JUNGLE || biome == Biome.SWAMP
            || biome == Biome.MANGROVE_SWAMP || biome == Biome.LUSH_CAVES) return "§a";
        if (biome == Biome.DRIPSTONE_CAVES || biome == Biome.PLAINS || biome == Biome.SUNFLOWER_PLAINS
            || biome == Biome.MEADOW || biome == Biome.MUSHROOM_FIELDS) return "§e";
        if (biome == Biome.DEEP_DARK) return "§8";
        if (biome == Biome.NETHER_WASTES || biome == Biome.CRIMSON_FOREST || biome == Biome.BASALT_DELTAS) return "§c";
        if (biome == Biome.SOUL_SAND_VALLEY || biome == Biome.WARPED_FOREST) return "§3";
        if (biome == Biome.SNOWY_PLAINS || biome == Biome.SNOWY_TAIGA || biome == Biome.FROZEN_PEAKS
            || biome == Biome.JAGGED_PEAKS || biome == Biome.STONY_PEAKS) return "§b";
        return "§d";
    }

    private BarColor getBiomeBarColor(Biome biome) {
        if (biome == Biome.DESERT || biome == Biome.BADLANDS || biome == Biome.ERODED_BADLANDS
            || biome == Biome.SAVANNA || biome == Biome.SAVANNA_PLATEAU) return BarColor.YELLOW;
        if (biome == Biome.FOREST || biome == Biome.BIRCH_FOREST || biome == Biome.TAIGA
            || biome == Biome.OLD_GROWTH_PINE_TAIGA || biome == Biome.OLD_GROWTH_SPRUCE_TAIGA
            || biome == Biome.JUNGLE || biome == Biome.BAMBOO_JUNGLE || biome == Biome.SWAMP
            || biome == Biome.MANGROVE_SWAMP || biome == Biome.LUSH_CAVES || biome == Biome.PLAINS
            || biome == Biome.SUNFLOWER_PLAINS || biome == Biome.MEADOW || biome == Biome.MUSHROOM_FIELDS) return BarColor.GREEN;
        if (biome == Biome.DRIPSTONE_CAVES || biome == Biome.SOUL_SAND_VALLEY || biome == Biome.WARPED_FOREST
            || biome == Biome.SNOWY_PLAINS || biome == Biome.SNOWY_TAIGA || biome == Biome.FROZEN_PEAKS
            || biome == Biome.JAGGED_PEAKS || biome == Biome.STONY_PEAKS) return BarColor.BLUE;
        if (biome == Biome.NETHER_WASTES || biome == Biome.CRIMSON_FOREST || biome == Biome.BASALT_DELTAS
            || biome == Biome.DEEP_DARK) return BarColor.RED;
        return BarColor.PURPLE;
    }

    private static ResourceBonus ore(String name, Material normal, Material deepslate) {
        return new ResourceBonus(name, Set.of(normal, deepslate));
    }

    private static ResourceBonus single(String name, Material material) {
        return new ResourceBonus(name, Set.of(material));
    }

    private static void addProfile(Biome biome, String name, List<EntityType> mobs, List<ResourceBonus> resources) {
        BIOME_PROFILES.put(biome, new BiomeProfile(name, mobs, resources));
    }

    private static final class BiomeProfile {
        private final String name;
        private final List<EntityType> mobs;
        private final List<ResourceBonus> resources;

        private BiomeProfile(String name, List<EntityType> mobs, List<ResourceBonus> resources) {
            this.name = name;
            this.mobs = mobs;
            this.resources = resources;
        }
    }

    private static final class ResourceBonus {
        private final String name;
        private final Set<Material> blocks;

        private ResourceBonus(String name, Set<Material> blocks) {
            this.name = name;
            this.blocks = blocks;
        }
    }
}
