package me.mika.midomikasiegesafebaseshield.Utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Set;

public class ChangeSelectedBlock {
    public static void ChangeBorderToGold(Player p){
        File file = new File(Bukkit.getServer().getPluginManager().getPlugin("SiegeSafeBaseShield").getDataFolder(), "config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        File PlayerSelectedAreaFile = new File(Bukkit.getServer().getPluginManager().getPlugin("SiegeSafeBaseShield").getDataFolder(), "PlayerSelectedAreaConfig.yml");
        FileConfiguration PlayerSelectedAreaConfig = YamlConfiguration.loadConfiguration(PlayerSelectedAreaFile);

        int count = config.getInt(p.getName() + " Count");
        String secondKey = "Number-Of-Selected-Location";

        ConfigurationSection selectionMainKeySection = PlayerSelectedAreaConfig.getConfigurationSection(p.getName());
        if (selectionMainKeySection != null) {
            selectionMainKeySection.getKeys(false).stream().forEach(secondKeys -> {
                ConfigurationSection selectedBlocks = PlayerSelectedAreaConfig.getConfigurationSection(p.getName() + "." + secondKeys + ".areaInfo" + ".selectedBlocks");
                if (selectedBlocks != null) {
                    Set<String> keys = selectedBlocks.getKeys(false);
                    for (String key : keys) {
                        String locationKey = key;
                        String materialName = PlayerSelectedAreaConfig.getString(p.getName() + "." + secondKeys + ".areaInfo" + ".selectedBlocks." + locationKey);
                        String[] locationData = locationKey.split(";");
                        World worldName = Bukkit.getWorld(locationData[0]);
                        int x = Integer.parseInt(locationData[1]);
                        int y = Integer.parseInt(locationData[2]);
                        int z = Integer.parseInt(locationData[3]);
                        if (worldName != null) {
                            Location location = new Location(worldName, x, y, z);
                            Material material = Material.valueOf(materialName);
                            Block block = location.getBlock();
                            block.setType(material);

//                        // 在给定位置的上方生成粒子效果
//                        p.spawnParticle(Particle.GLOW, location.add(0.5,1,0.5), 100, 0, 0, 0);
//                        // 在给定位置的下方生成粒子效果
//                        p.spawnParticle(Particle.GLOW, location.add(0,-1,0), 100, 0, 0, 0);

                        } else {
                            // 处理无效的世界名称
                            // 这里可以添加适当的处理逻辑，比如发送错误消息给玩家等
                        }
                    }
                }
            });
        }
    }

    public static void ChangeBorderToOri(Player p){
        File file = new File(Bukkit.getServer().getPluginManager().getPlugin("SiegeSafeBaseShield").getDataFolder(), "config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        File PlayerSelectedAreaFile = new File(Bukkit.getServer().getPluginManager().getPlugin("SiegeSafeBaseShield").getDataFolder(), "PlayerSelectedAreaConfig.yml");
        FileConfiguration PlayerSelectedAreaConfig = YamlConfiguration.loadConfiguration(PlayerSelectedAreaFile);

        int count = config.getInt(p.getName() + " Count");
        String secondKey = "Number-Of-Selected-Location";

        ConfigurationSection selectionMainKeySection = PlayerSelectedAreaConfig.getConfigurationSection(p.getName());
        if (selectionMainKeySection != null) {
            selectionMainKeySection.getKeys(false).stream().forEach(secondKeys -> {
                ConfigurationSection blocksReplaceBySelectedBlocks = PlayerSelectedAreaConfig.getConfigurationSection(p.getName() + "." + secondKeys + ".areaInfo" + ".blocksReplaceBySelectedBlocks");
                if (blocksReplaceBySelectedBlocks != null) {
                    Set<String> keys = blocksReplaceBySelectedBlocks.getKeys(false);
                    for (String key : keys) {
                        String locationKey = key;
                        String materialName = PlayerSelectedAreaConfig.getString(p.getName() + "." + secondKeys + ".areaInfo" + ".blocksReplaceBySelectedBlocks." + locationKey);
                        String[] locationData = locationKey.split(";");
                        World worldName = Bukkit.getWorld(locationData[0]);
                        int x = Integer.parseInt(locationData[1]);
                        int y = Integer.parseInt(locationData[2]);
                        int z = Integer.parseInt(locationData[3]);
                        if (worldName != null) {
                            Location location = new Location(worldName, x, y, z);

                            Material material = Material.valueOf(materialName);
                            Block block = location.getBlock();
                            block.setType(material);
                        } else {
                            // 处理无效的世界名称
                            // 这里可以添加适当的处理逻辑，比如发送错误消息给玩家等
                        }
                    }
                }
            });
        }
    }
}
