package me.mika.midomikasiegesafebaseshield.Utils;

import me.mika.midomikasiegesafebaseshield.Listeners.OnPlayerItemHeld;
import me.mika.midomikasiegesafebaseshield.Listeners.SelectArea;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import static me.mika.midomikasiegesafebaseshield.Utils.SaveDataToConfig.areaBlockLimit;
import static me.mika.midomikasiegesafebaseshield.Utils.SaveDataToConfig.blockCount;

public class SetRegionBorderToGoldBlocks {

    // 将选择区域的边框设定为黄金块
    public static void setRegionBorderToGoldBlocks(Location minLocation, Location maxLocation, Player player) {
        World world = minLocation.getWorld();
        int minX = minLocation.getBlockX();
        int minY = minLocation.getBlockY();
        int minZ = minLocation.getBlockZ();
        int maxX = maxLocation.getBlockX();
        int maxY = maxLocation.getBlockY();
        int maxZ = maxLocation.getBlockZ();



        if (blockCount > areaBlockLimit) {
            player.sendMessage(ChatColor.RED + "The size of the area cannot exceed " + areaBlockLimit);

        } else if (blockCount < areaBlockLimit) {
            for (int x = minX; x <= maxX; x++) {
                //我现在的视角 loop 左到右
                for (int y = minY; y <= maxY; y++) {
                    //我现在的视角 loop 高到低
                    for (int z = minZ; z <= maxZ; z++) {
                        //我现在的视角 loop 上到下
                        // 边框条件判断
                        boolean border1 = ((x == maxX && z == minZ) || (x != maxX && z == minZ && (y == minY || y == maxY)) || (x == maxX && z != minZ && (y == minY || y == maxY)));
                        boolean border2 = ((x == minX && z == maxZ) || (x == minX && z != maxZ && (y == minY || y == maxY)) || (x != minX && z == maxZ && (y == minY || y == maxY)));
                        boolean border3 = ((x == minX && z == minZ) || (x == minX && z != minZ && (y == minY || y == maxY)) || (x != minX && z == minZ && (y == minY || y == maxY)));
                        boolean border4 = ((x == maxX && z == maxZ) || (x != minX && z == minZ && (y == minY || y == maxY)) || (x == maxX && z != maxZ && (y == minY || y == maxY)));
                        if (border1 || border2 || border3 || border4) {
                            Location location = new Location(world, x, y, z);
                            Block block = location.getBlock();
                            SelectArea.blocksReplaceBySelectedBlocks.put(location, block.getType());
//                            block.setType(Material.GOLD_BLOCK);
                            OnPlayerItemHeld.playerIsGoldBorderBooleanMap.put(player.getUniqueId(), true);
                        }
                    }
                }
            }
        }
    }
}
