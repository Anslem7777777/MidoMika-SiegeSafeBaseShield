package me.mika.midomikasiegesafebaseshield.Utils;

import me.mika.midomikasiegesafebaseshield.Listeners.SelectArea;
import me.mika.midomikasiegesafebaseshield.SiegeSafeBaseShield;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

public class SaveDataToConfig {
    SiegeSafeBaseShield plugin;
    public SaveDataToConfig(SiegeSafeBaseShield plugin) {
        this.plugin = plugin;
    }
    public static String locationToString(Location location) {
        return location.getWorld().getName() + ";" + location.getBlockX() + ";" + location.getBlockY() + ";" + location.getBlockZ();
    }
    static File file = new File(Bukkit.getServer().getPluginManager().getPlugin("SiegeSafeBaseShield").getDataFolder(), "config.yml");
    static FileConfiguration config = YamlConfiguration.loadConfiguration(file);
    public static int areaBlockLimit = config.getInt("area-block-limit");
    public static Integer blockCount = 0;

    public static void saveDataToConfig(Player player, String finalSecondKeyAndNumber) {
        File file = new File(Bukkit.getServer().getPluginManager().getPlugin("SiegeSafeBaseShield").getDataFolder(), "config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        File PlayerSelectedAreaFile = new File(Bukkit.getServer().getPluginManager().getPlugin("SiegeSafeBaseShield").getDataFolder(), "PlayerSelectedAreaConfig.yml");
        FileConfiguration PlayerSelectedAreaConfig = YamlConfiguration.loadConfiguration(PlayerSelectedAreaFile);
        String playerName = player.getName();

        if (blockCount > areaBlockLimit) {

        } else if (blockCount < areaBlockLimit) {

            // 保存playerSelections
            for (Map.Entry<Player, Location[]> entry : SelectArea.playerSelections.entrySet()) {
                Location[] selection = entry.getValue();

                if (selection[0] != null) {
                    String minLocation = locationToString(selection[0]);
                    PlayerSelectedAreaConfig.set(playerName + "." + finalSecondKeyAndNumber + ".areaInfo" + ".playerSelections" + ".minLocation", minLocation);
                }

                if (selection[1] != null) {
                    String maxLocation = locationToString(selection[1]);
                    PlayerSelectedAreaConfig.set(playerName + "." + finalSecondKeyAndNumber + ".areaInfo" + ".playerSelections" + ".maxLocation", maxLocation);
                }
            }

            // 保存blocksReplaceBySelectedBlocks
            for (Map.Entry<Location, Material> entry : SelectArea.blocksReplaceBySelectedBlocks.entrySet()) {
                Location location = entry.getKey();
                Material material = entry.getValue();
                String locationKey = locationToString(location);
                PlayerSelectedAreaConfig.set(playerName + "." + finalSecondKeyAndNumber + ".areaInfo" + ".blocksReplaceBySelectedBlocks." + locationKey, material.name());
            }

            // 保存selectedBlocks
            for (Map.Entry<Location, Material> entry : SelectArea.selectedBlocks.entrySet()) {
                Location location = entry.getKey();
                Material material = entry.getValue();
                String locationKey = locationToString(location);
                PlayerSelectedAreaConfig.set(playerName + "." + finalSecondKeyAndNumber + ".areaInfo" + ".selectedBlocks." + locationKey, material.name());
            }

            try {
                PlayerSelectedAreaConfig.save(PlayerSelectedAreaFile);
            } catch (IOException error) {
                System.out.println("Save File Error");
            }

        }
    }

    public static void refreshHardness(Location minLocation, Location maxLocation, String mainKey, String secondKey){
        Player p = Bukkit.getPlayer(mainKey);
        File PlayerSelectedAreaFile = new File(Bukkit.getServer().getPluginManager().getPlugin("SiegeSafeBaseShield").getDataFolder(), "PlayerSelectedAreaConfig.yml");
        FileConfiguration PlayerSelectedAreaConfig = YamlConfiguration.loadConfiguration(PlayerSelectedAreaFile);

        World world = minLocation.getWorld();
        int minX = minLocation.getBlockX();
        int minY = minLocation.getBlockY();
        int minZ = minLocation.getBlockZ();
        int maxX = maxLocation.getBlockX();
        int maxY = maxLocation.getBlockY();
        int maxZ = maxLocation.getBlockZ();

        //block硬度的逻辑
        Double totalAreaBlockHardness = 0.0;
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Location location = new Location(world, x, y, z);
                    Block block = location.getBlock();
                    PersistentDataContainer dataContainer = block.getWorld().getPersistentDataContainer();
                    Material blockType = block.getType();
                    double blockHardness = blockType.getHardness();
                    totalAreaBlockHardness += blockHardness;


                }
            }
        }
        if (totalAreaBlockHardness > 1) {
            PlayerSelectedAreaConfig.set(mainKey + "." + secondKey + ".areaInfo" + ".totalBlockHardness", totalAreaBlockHardness);
            PlayerSelectedAreaConfig.set(mainKey + "." + secondKey + ".areaInfo" + ".repairTotalBlockHardness", totalAreaBlockHardness);
        } else {
            PlayerSelectedAreaConfig.set(mainKey + "." + secondKey + ".areaInfo" + ".totalBlockHardness", 1);
            PlayerSelectedAreaConfig.set(mainKey + "." + secondKey + ".areaInfo" + ".repairTotalBlockHardness", 1);
        }

        // 保存配置文件
        try {
            PlayerSelectedAreaConfig.save(PlayerSelectedAreaFile);
        } catch (IOException error) {
            error.printStackTrace();
        }
    }
    public static void saveSelectedAreaBlock(Location minLocation, Location maxLocation, Player player, String finalSecondKeyAndNumber, String areaName) {
        File PlayerSelectedAreaFile = new File(Bukkit.getServer().getPluginManager().getPlugin("SiegeSafeBaseShield").getDataFolder(), "PlayerSelectedAreaConfig.yml");
        FileConfiguration PlayerSelectedAreaConfig = YamlConfiguration.loadConfiguration(PlayerSelectedAreaFile);

        String playerName = player.getName();

        World world = minLocation.getWorld();
        int minX = minLocation.getBlockX();
        int minY = minLocation.getBlockY();
        int minZ = minLocation.getBlockZ();
        int maxX = maxLocation.getBlockX();
        int maxY = maxLocation.getBlockY();
        int maxZ = maxLocation.getBlockZ();

        //block硬度的逻辑
        Double totalAreaBlockHardness = 0.0;
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Location location = new Location(world, x, y, z);
                    Block block = location.getBlock();
                    PersistentDataContainer dataContainer = block.getWorld().getPersistentDataContainer();
                    Material blockType = block.getType();
                    double blockHardness = blockType.getHardness();
                    totalAreaBlockHardness += blockHardness;

                   blockCount++;

                    SelectArea.selectedAreaOriBlocks.put(location, blockType);
                }
            }
        }

        if (blockCount > areaBlockLimit) {

        } else if (blockCount < areaBlockLimit) {
            // 保存areaName
            PlayerSelectedAreaConfig.set(playerName  + "." + finalSecondKeyAndNumber + ".areaInfo" + ".name", areaName);

            // 将blockTypes保存到配置文件
            for (Map.Entry<Location, Material> entry : SelectArea.selectedAreaOriBlocks.entrySet()) {
                Location location = entry.getKey();
                Material material = entry.getValue();

                String locationKey = SaveDataToConfig.locationToString(location);
                PlayerSelectedAreaConfig.set(playerName  + "." + finalSecondKeyAndNumber + ".areaInfo" + ".selectedAreaOriBlocks." + locationKey, material.name());
            }
            PlayerSelectedAreaConfig.set(playerName + "." + finalSecondKeyAndNumber + ".areaInfo" + ".totalBlockHardness", totalAreaBlockHardness);
            PlayerSelectedAreaConfig.set(playerName + "." + finalSecondKeyAndNumber + ".areaInfo" + ".repairTotalBlockHardness", totalAreaBlockHardness);
            PlayerSelectedAreaConfig.set(playerName + "." + finalSecondKeyAndNumber + ".areaInfo" + ".breakBlockTime", LocalDateTime.now().toString());

            // 保存配置文件
            try {
                PlayerSelectedAreaConfig.save(PlayerSelectedAreaFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        SelectArea.selectedAreaOriBlocks.clear();
    }

}
