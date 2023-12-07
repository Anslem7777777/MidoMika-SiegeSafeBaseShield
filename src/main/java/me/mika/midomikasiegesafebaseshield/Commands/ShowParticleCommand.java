package me.mika.midomikasiegesafebaseshield.Commands;
import me.mika.midomikasiegesafebaseshield.Tasks.ParticleTrail;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class ShowParticleCommand extends SubCommands{
    @Override
    public String getName() {
        return "show";
    }

    @Override
    public String getDescription() {
        return "show SSBase particle";
    }

    @Override
    public String getSyntax() {
        return "/ssbs show";
    }

    @Override
    public void perform(Player player, String[] args) {

        if (args.length > 0){
            File file = new File(Bukkit.getServer().getPluginManager().getPlugin("SiegeSafeBaseShield").getDataFolder(), "config.yml");
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            File PlayerSelectedAreaFile = new File(Bukkit.getServer().getPluginManager().getPlugin("SiegeSafeBaseShield").getDataFolder(), "PlayerSelectedAreaConfig.yml");
            FileConfiguration PlayerSelectedAreaConfig = YamlConfiguration.loadConfiguration(PlayerSelectedAreaFile);

            StringBuilder builder = new StringBuilder();
            for (int i = 1; i < args.length; i++){
                builder.append(args[i]).append(" ");

            }
            String areaName = builder.toString().trim();

            for (String mainKey : PlayerSelectedAreaConfig.getKeys(false)) {
                if (mainKey.equals(player.getName())) {
                    for (String secondKey : PlayerSelectedAreaConfig.getConfigurationSection(mainKey).getKeys(false)) {
                        if (!secondKey.equalsIgnoreCase("Number-Of-Selected-Location")) {
                            String configAreaName = PlayerSelectedAreaConfig.getString(mainKey + "." + secondKey + ".areaInfo.name");
                            if (configAreaName.equals(areaName)) {
                                for (String key : PlayerSelectedAreaConfig.getConfigurationSection(mainKey + "." + secondKey + ".areaInfo.selectedBlocks").getKeys(false)) {
                                    String[] coordinates = key.split(";");
                                    // 从字符串数组中解析出坐标信息
                                    if (coordinates.length == 4) {
                                        String worldName = coordinates[0];
                                        double x = Double.parseDouble(coordinates[1]);
                                        double y = Double.parseDouble(coordinates[2]);
                                        double z = Double.parseDouble(coordinates[3]);

                                        // 根据解析的坐标信息创建 Location
                                        Location location = new Location(Bukkit.getWorld(worldName), x, y, z);

                                        // 在玩家位置到目标位置之间生成粒子
                                        generateParticleTrail(player.getLocation(), location, player);
                                    }
                                }
                            }
                        }
                    }
                }
            }

//            for (String mainKey : PlayerSelectedAreaConfig.getKeys(false)){
//                if (mainKey.equals(player.getName())){
//                    for (String secondKey : PlayerSelectedAreaConfig.getConfigurationSection(mainKey).getKeys(false)){
//                        if (!secondKey.equalsIgnoreCase("Number-Of-Selected-Location")){
//                            String configAreaName = PlayerSelectedAreaConfig.getString(mainKey + "." + secondKey + ".areaInfo.name");
//                            if (configAreaName.equals(areaName)){
//                                for (String key : PlayerSelectedAreaConfig.getConfigurationSection(mainKey + "." + secondKey + ".areaInfo.selectedBlocks").getKeys(false)) {
//                                    String[] coordinates = key.split(";");
//                                    // 从字符串数组中解析出坐标信息
//                                    if (coordinates.length == 4) {
//                                        String worldName = coordinates[0];
//                                        double x = Double.parseDouble(coordinates[1]);
//                                        double y = Double.parseDouble(coordinates[2]);
//                                        double z = Double.parseDouble(coordinates[3]);
//
//                                        // 根据解析的坐标信息创建 Location
//                                        Location location = new Location(Bukkit.getWorld(worldName), x, y, z);
//
//                                        // 在每个位置生成粒子效果
//                                        // 使用 Particle.REDSTONE 创建有颜色的粒子效果
//                                        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(0,255,255), 1.5f);
//
//                                        // 在位置生成带颜色的粒子效果
//                                        player.spawnParticle(Particle.REDSTONE, location.add(0.5, 1, 0.5), 1, 0, 0, 0, 0, dustOptions);
//
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }

        }
    }
    // 插值生成粒子效果的方法
    private void generateParticleTrail(Location startLocation, Location endLocation, Player player) {
        int particleCount = 10; // 你可以调整生成的粒子数量

        double deltaX = (endLocation.getX() - startLocation.getX()) / particleCount;
        double deltaY = (endLocation.getY() - startLocation.getY()) / particleCount;
        double deltaZ = (endLocation.getZ() - startLocation.getZ()) / particleCount;

        for (int i = 0; i < particleCount; i++) {
            double currentX = startLocation.getX() + i * deltaX;
            double currentY = startLocation.getY() + i * deltaY;
            double currentZ = startLocation.getZ() + i * deltaZ;

            Location particleLocation = new Location(startLocation.getWorld(), currentX, currentY, currentZ);

            // 生成粒子效果
            Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(0, 255, 255), 1.5f);
            player.spawnParticle(Particle.REDSTONE, particleLocation.add(0.5, 1, 0.5), 1, 0, 0, 0, 0, dustOptions);
        }
    }
}
