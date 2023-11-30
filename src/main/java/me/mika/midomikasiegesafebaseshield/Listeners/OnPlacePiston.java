package me.mika.midomikasiegesafebaseshield.Listeners;

import me.mika.midomikasiegesafebaseshield.SiegeSafeBaseShield;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.io.File;
import java.util.*;

public class OnPlacePiston implements Listener {

    OnPlaceBlockInArea onPlaceBlockInArea = new OnPlaceBlockInArea(SiegeSafeBaseShield.getPlugin());
    private Block piston;
    private Set<String> allConfigOriBlock = new HashSet<>();

    @EventHandler
    public void onPlacePiston(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        if (e.getBlock().getType().toString().contains("PISTON")) {
            piston = e.getBlock();
        }else {
            return;
        }
        File file = new File(Bukkit.getServer().getPluginManager().getPlugin("SiegeSafeBaseShield").getDataFolder(), "config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        File PlayerSelectedAreaFile = new File(Bukkit.getServer().getPluginManager().getPlugin("SiegeSafeBaseShield").getDataFolder(), "PlayerSelectedAreaConfig.yml");
        FileConfiguration PlayerSelectedAreaConfig = YamlConfiguration.loadConfiguration(PlayerSelectedAreaFile);

        for (String mainKey : PlayerSelectedAreaConfig.getKeys(false)){
            if (!mainKey.equals(p.getName())) {
                for (String secondKey : PlayerSelectedAreaConfig.getConfigurationSection(mainKey).getKeys(false)) {
                    if (!secondKey.equals("Number-Of-Selected-Location")) {
                        for (String selectedAreaOriBlocks : PlayerSelectedAreaConfig.getConfigurationSection(mainKey).getConfigurationSection(secondKey).getConfigurationSection("areaInfo").getConfigurationSection("selectedAreaOriBlocks").getKeys(false)) {
                            allConfigOriBlock.add(selectedAreaOriBlocks);

                        }
                    }
                }
            }
        }

        Block blockAroundPiston;
        int x = 0;
        int y = -14;
        int z = -14;
        Material blockType = Material.BLUE_STAINED_GLASS;
        int count = 0;

        for ( x = -13; x < 14; x++) {
            y++;
            z++;
            if (!(x == 0 && y == 0 && z == 0)) {
                if (x < 14) {
//                    blockAroundPiston = piston.getRelative(x, 0, 0);
//                    blockAroundPiston.setType(blockType);
                    if (allConfigOriBlock.contains(setXYZ(piston, x, 0, 0))) {
                        e.setCancelled(true);

                    }
                }
                if (y < 14) {
//                    blockAroundPiston = piston.getRelative(0, y, 0);
//                    blockAroundPiston.setType(blockType);
                    if (allConfigOriBlock.contains(setXYZ(piston, 0, y, 0))) {
                        e.setCancelled(true);

                    }
                }
                if (z < 14){
//                    blockAroundPiston = piston.getRelative(0, 0, z);
//                    blockAroundPiston.setType(blockType);
                    if (allConfigOriBlock.contains(setXYZ(piston, 0, 0, z))) {
                        e.setCancelled(true);

                    }
                }
            }
        }

        if (e.isCancelled()){
            p.sendMessage(ChatColor.RED + "Piston cannot be placed within 13 blocks of another player's area.");
        }
    }

    @EventHandler
    public void OnPistonExtend(BlockPistonExtendEvent e){
        Block piston = e.getBlock();

        int x = 0;
        int y = -14;
        int z = -14;

        for ( x = -13; x < 14; x++) {
            y++;
            z++;
            if (!(x == 0 && y == 0 && z == 0)) {
                if (x < 14) {
                    if (allConfigOriBlock.contains(setXYZ(piston, x, 0, 0))) {
                        e.setCancelled(true);

                    }
                }
                if (y < 14) {
                    if (allConfigOriBlock.contains(setXYZ(piston, 0, y, 0))) {
                        e.setCancelled(true);

                    }
                }
                if (z < 14){
                    if (allConfigOriBlock.contains(setXYZ(piston, 0, 0, z))) {
                        e.setCancelled(true);

                    }
                }
            }
        }
    }

    public static String setXYZ(Block piston, int x, int y, int z){
        Block blockAroundPiston = piston.getRelative(x,y,z);
        String blockAroundPistonWorld = blockAroundPiston.getWorld().getName();
        Integer blockAroundPistonLocationX = blockAroundPiston.getX();
        Integer blockAroundPistonLocationY = blockAroundPiston.getY();
        Integer blockAroundPistonLocationZ = blockAroundPiston.getZ();
        String blockAroundPistonLocationString = blockAroundPistonWorld + ";" + blockAroundPistonLocationX + ";" + blockAroundPistonLocationY + ";" + blockAroundPistonLocationZ;

        return blockAroundPistonLocationString;
    }

}


