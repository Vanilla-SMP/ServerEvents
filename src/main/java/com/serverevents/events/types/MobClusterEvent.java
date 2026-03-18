package com.serverevents.events.types;

import com.serverevents.ServerEvents;
import com.serverevents.events.ServerEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.generator.structure.Structure;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.StructureSearchResult;

import java.util.*;

public class MobClusterEvent extends ServerEvent implements Listener {
    private final ServerEvents plugin;
    private Structure targetStructure;
    private EntityType targetMob;
    private BukkitTask spawnTask;
    private final Random random = new Random();

    private static final Map<Structure, String> STRUCTURE_NAMES = new LinkedHashMap<>();
    private static final Map<Structure, List<EntityType>> STRUCTURE_MOBS = new LinkedHashMap<>();

    static {
        STRUCTURE_NAMES.put(Structure.VILLAGE_PLAINS, "Деревня (равнины)");
        STRUCTURE_NAMES.put(Structure.VILLAGE_DESERT, "Деревня (пустыня)");
        STRUCTURE_NAMES.put(Structure.VILLAGE_SAVANNA, "Деревня (саванна)");
        STRUCTURE_NAMES.put(Structure.VILLAGE_SNOWY, "Деревня (снег)");
        STRUCTURE_NAMES.put(Structure.VILLAGE_TAIGA, "Деревня (тайга)");
        STRUCTURE_NAMES.put(Structure.PILLAGER_OUTPOST, "Застава разбойников");
        STRUCTURE_NAMES.put(Structure.MANSION, "Лесной особняк");
        STRUCTURE_NAMES.put(Structure.MONUMENT, "Океанский монумент");
        STRUCTURE_NAMES.put(Structure.STRONGHOLD, "Крепость");
        STRUCTURE_NAMES.put(Structure.FORTRESS, "Крепость Ада");
        STRUCTURE_NAMES.put(Structure.BASTION_REMNANT, "Бастионный остаток");
        STRUCTURE_NAMES.put(Structure.END_CITY, "Город Края");
        STRUCTURE_NAMES.put(Structure.ANCIENT_CITY, "Древний город");
        STRUCTURE_NAMES.put(Structure.DESERT_PYRAMID, "Пирамида пустыни");
        STRUCTURE_NAMES.put(Structure.JUNGLE_PYRAMID, "Храм джунглей");
        STRUCTURE_NAMES.put(Structure.SWAMP_HUT, "Хижина ведьмы");
        STRUCTURE_NAMES.put(Structure.IGLOO, "Иглу");

        STRUCTURE_MOBS.put(Structure.VILLAGE_PLAINS, Arrays.asList(EntityType.ZOMBIE, EntityType.PILLAGER, EntityType.VINDICATOR));
        STRUCTURE_MOBS.put(Structure.VILLAGE_DESERT, Arrays.asList(EntityType.HUSK, EntityType.PILLAGER, EntityType.ZOMBIE));
        STRUCTURE_MOBS.put(Structure.VILLAGE_SAVANNA, Arrays.asList(EntityType.ZOMBIE, EntityType.PILLAGER, EntityType.VINDICATOR));
        STRUCTURE_MOBS.put(Structure.VILLAGE_SNOWY, Arrays.asList(EntityType.STRAY, EntityType.ZOMBIE, EntityType.SKELETON));
        STRUCTURE_MOBS.put(Structure.VILLAGE_TAIGA, Arrays.asList(EntityType.ZOMBIE, EntityType.SKELETON, EntityType.STRAY));
        STRUCTURE_MOBS.put(Structure.PILLAGER_OUTPOST, Arrays.asList(EntityType.PILLAGER, EntityType.VINDICATOR, EntityType.EVOKER));
        STRUCTURE_MOBS.put(Structure.MANSION, Arrays.asList(EntityType.VINDICATOR, EntityType.EVOKER, EntityType.WITCH));
        STRUCTURE_MOBS.put(Structure.MONUMENT, Arrays.asList(EntityType.ELDER_GUARDIAN, EntityType.GUARDIAN, EntityType.DROWNED));
        STRUCTURE_MOBS.put(Structure.STRONGHOLD, Arrays.asList(EntityType.ZOMBIE, EntityType.SKELETON, EntityType.SILVERFISH));
        STRUCTURE_MOBS.put(Structure.FORTRESS, Arrays.asList(EntityType.BLAZE, EntityType.WITHER_SKELETON, EntityType.ZOMBIFIED_PIGLIN));
        STRUCTURE_MOBS.put(Structure.BASTION_REMNANT, Arrays.asList(EntityType.PIGLIN, EntityType.PIGLIN_BRUTE, EntityType.HOGLIN));
        STRUCTURE_MOBS.put(Structure.END_CITY, Arrays.asList(EntityType.ENDERMAN, EntityType.SHULKER));
        STRUCTURE_MOBS.put(Structure.ANCIENT_CITY, Arrays.asList(EntityType.WARDEN, EntityType.ZOMBIE, EntityType.SKELETON));
        STRUCTURE_MOBS.put(Structure.DESERT_PYRAMID, Arrays.asList(EntityType.HUSK, EntityType.SKELETON, EntityType.ZOMBIE));
        STRUCTURE_MOBS.put(Structure.JUNGLE_PYRAMID, Arrays.asList(EntityType.SPIDER, EntityType.ZOMBIE, EntityType.WITCH));
        STRUCTURE_MOBS.put(Structure.SWAMP_HUT, Arrays.asList(EntityType.WITCH, EntityType.SLIME, EntityType.ZOMBIE));
        STRUCTURE_MOBS.put(Structure.IGLOO, Arrays.asList(EntityType.STRAY, EntityType.SKELETON, EntityType.ZOMBIE));
    }

    private static final Map<EntityType, String> MOB_NAMES = new HashMap<>();
    static {
        MOB_NAMES.put(EntityType.ZOMBIE, "Зомби");
        MOB_NAMES.put(EntityType.SKELETON, "Скелет");
        MOB_NAMES.put(EntityType.PILLAGER, "Разбойник");
        MOB_NAMES.put(EntityType.VINDICATOR, "Каратель");
        MOB_NAMES.put(EntityType.EVOKER, "Заклинатель");
        MOB_NAMES.put(EntityType.WITCH, "Ведьма");
        MOB_NAMES.put(EntityType.HUSK, "Иссушённый");
        MOB_NAMES.put(EntityType.STRAY, "Бродяга");
        MOB_NAMES.put(EntityType.GUARDIAN, "Страж");
        MOB_NAMES.put(EntityType.ELDER_GUARDIAN, "Старший страж");
        MOB_NAMES.put(EntityType.DROWNED, "Утопленник");
        MOB_NAMES.put(EntityType.SILVERFISH, "Чешуйница");
        MOB_NAMES.put(EntityType.BLAZE, "Огненный вихрь");
        MOB_NAMES.put(EntityType.WITHER_SKELETON, "Скелет иссушителя");
        MOB_NAMES.put(EntityType.ZOMBIFIED_PIGLIN, "Зомби-пиглин");
        MOB_NAMES.put(EntityType.PIGLIN, "Пиглин");
        MOB_NAMES.put(EntityType.PIGLIN_BRUTE, "Пиглин-громила");
        MOB_NAMES.put(EntityType.HOGLIN, "Хоглин");
        MOB_NAMES.put(EntityType.ENDERMAN, "Эндермен");
        MOB_NAMES.put(EntityType.SHULKER, "Шалкер");
        MOB_NAMES.put(EntityType.WARDEN, "Хранитель");
        MOB_NAMES.put(EntityType.SPIDER, "Паук");
        MOB_NAMES.put(EntityType.SLIME, "Слизень");
    }

    public MobClusterEvent(ServerEvents plugin) {
        super("Группировка");
        this.plugin = plugin;
    }

    @Override
    public void start() {
        List<Structure> structures = new ArrayList<>(STRUCTURE_MOBS.keySet());
        targetStructure = structures.get(random.nextInt(structures.size()));
        List<EntityType> mobs = STRUCTURE_MOBS.get(targetStructure);
        targetMob = mobs.get(random.nextInt(mobs.size()));

        String structureName = STRUCTURE_NAMES.getOrDefault(targetStructure, targetStructure.toString());
        String mobName = MOB_NAMES.getOrDefault(targetMob, targetMob.toString());

        bossBar = Bukkit.createBossBar(
            "§6Группировка §f| §e" + structureName + " §7— §f" + mobName,
            BarColor.YELLOW,
            BarStyle.SEGMENTED_6
        );
        bossBar.setProgress(1.0);
        Bukkit.getPluginManager().registerEvents(this, plugin);

        spawnTask = Bukkit.getScheduler().runTaskTimer(plugin, this::spawnNearPlayers, 100L, 100L);
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
        String structure = STRUCTURE_NAMES.getOrDefault(targetStructure, targetStructure != null ? targetStructure.toString() : "");
        String mob = targetMob != null ? MOB_NAMES.getOrDefault(targetMob, targetMob.toString()) : "";
        return "Структура: **" + structure + "**\nМоб: **" + mob + "**";
    }

    @Override
    public void onPlayerJoin(Player player) {
        if (bossBar != null && preferences != null && preferences.isEnabled(player.getUniqueId())) {
            bossBar.removePlayer(player);
            bossBar.addPlayer(player);
            player.playSound(player.getLocation(), Sound.ENTITY_RAVAGER_ROAR, 0.5f, 1.0f);
        }
    }

    private void spawnNearPlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            StructureSearchResult result = player.getWorld().locateNearestStructure(player.getLocation(), targetStructure, 8, false);
            if (result == null) continue;
            Location loc = result.getLocation();
            if (loc.distance(player.getLocation()) > 128) continue;
            Location spawnLoc = randomOffset(loc, 4, 16);
            if (!spawnLoc.getChunk().isLoaded()) continue;
            LivingEntity mob = (LivingEntity) player.getWorld().spawnEntity(spawnLoc, targetMob);
            mob.setRemoveWhenFarAway(true);
        }
    }

    private Location randomOffset(Location origin, int minR, int maxR) {
        double angle = random.nextDouble() * 2 * Math.PI;
        double dist = minR + random.nextInt(maxR - minR);
        return origin.clone().add(Math.cos(angle) * dist, 0, Math.sin(angle) * dist);
    }
}
