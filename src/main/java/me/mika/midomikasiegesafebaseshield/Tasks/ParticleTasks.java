package me.mika.midomikasiegesafebaseshield.Tasks;

import me.mika.midomikasiegesafebaseshield.Listeners.OnPlayerItemHeld;
import me.mika.midomikasiegesafebaseshield.SiegeSafeBaseShield;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;

public class ParticleTasks extends BukkitRunnable {

    private final SiegeSafeBaseShield plugin;
    private final Player player;

    public ParticleTasks(SiegeSafeBaseShield plugin, Player p) {
        this.plugin = plugin;
        this.player = p;
    }

    @Override
    public void run() {

        File file = new File(Bukkit.getServer().getPluginManager().getPlugin("SiegeSafeBaseShield").getDataFolder(), "config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        File playerSelectedAreaFile = new File(Bukkit.getServer().getPluginManager().getPlugin("SiegeSafeBaseShield").getDataFolder(), "PlayerSelectedAreaConfig.yml");
        FileConfiguration playerSelectedAreaConfig = YamlConfiguration.loadConfiguration(playerSelectedAreaFile);
//        Player p = OnPlayerItemHeld.onHotbarChangePlayer;
        if (player != null) {
            if (OnPlayerItemHeld.playerIsGoldBorderBooleanMap != null) {
                if (OnPlayerItemHeld.playerIsGoldBorderBooleanMap.get(player.getUniqueId())) {
                    if (playerSelectedAreaConfig != null) {
                        outerForLoop:
                        for (String mainKey : playerSelectedAreaConfig.getKeys(false)) {
                            if (mainKey.equals(player.getName())) {
                                for (String secondKey : playerSelectedAreaConfig.getConfigurationSection(mainKey).getKeys(false)) {
                                    if (!secondKey.equals("Number-Of-Selected-Location")) {
                                        for (String key : playerSelectedAreaConfig.getConfigurationSection(mainKey + "." + secondKey + "." + ".areaInfo" + ".selectedBlocks").getKeys(false)) {
                                            String[] coordinates = key.split(";");
                                            // 从字符串数组中解析出坐标信息
                                            if (coordinates.length == 4) {
                                                String worldName = coordinates[0];
                                                double x = Double.parseDouble(coordinates[1]);
                                                double y = Double.parseDouble(coordinates[2]);
                                                double z = Double.parseDouble(coordinates[3]);

                                                // 根据解析的坐标信息创建 Location
                                                Location location = new Location(Bukkit.getWorld(worldName), x, y, z);

                                                // 在每个位置生成粒子效果
                                                // 使用 Particle.REDSTONE 创建有颜色的粒子效果
                                                Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(255, 215, 0), 1f);

                                                // 在位置生成带颜色的粒子效果
                                                player.spawnParticle(Particle.REDSTONE, location.add(0.5, 1, 0.5), 1, 0, 0, 0, 0, dustOptions);

                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void start() {
        // 启动任务
        BukkitTask task = runTaskTimer(plugin, 0, 5); // 以5 ticks为周期执行任务

    }

}

//    // 在需要的地方调用
//    startParticleTask(player);
