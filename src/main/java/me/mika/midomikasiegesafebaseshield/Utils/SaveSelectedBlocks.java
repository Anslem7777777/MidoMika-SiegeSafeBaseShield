package me.mika.midomikasiegesafebaseshield.Utils;

import me.mika.midomikasiegesafebaseshield.Listeners.SelectArea;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class SaveSelectedBlocks {

    // 恢复选择区域的原始方块类型
    public static void saveSelectedBlocks(Location minLocation, Location maxLocation, Player player) {
        World world = minLocation.getWorld();
        int minX = minLocation.getBlockX();
        int minY = minLocation.getBlockY();
        int minZ = minLocation.getBlockZ();
        int maxX = maxLocation.getBlockX();
        int maxY = maxLocation.getBlockY();
        int maxZ = maxLocation.getBlockZ();

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Location location = new Location(world, x, y, z);
                    Block block = location.getBlock();
                    if (SelectArea.blocksReplaceBySelectedBlocks.containsKey(location)) {
                        SelectArea.selectedBlocks.put(location, block.getType());
//                        Material originalType = SelectArea2.blocksReplaceBySelectedBlocks.get(location);
//                        block.setType(originalType);
                    }
                }
            }
        }
    }

}
