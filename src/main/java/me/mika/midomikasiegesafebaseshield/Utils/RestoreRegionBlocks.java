package me.mika.midomikasiegesafebaseshield.Utils;

import me.mika.midomikasiegesafebaseshield.Listeners.OnPlayerItemHeld;
import me.mika.midomikasiegesafebaseshield.Listeners.SelectArea;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class RestoreRegionBlocks {

    // 恢复选择区域的原始方块类型
    public static void restoreRegionBlocks(Location minLocation, Location maxLocation, Player player) {
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
                        Material originalType = SelectArea.blocksReplaceBySelectedBlocks.get(location);
                        block.setType(originalType);
                    }
                }
            }
        }
    }

    public static void restoreBLockBeforeBreak(FileConfiguration  PlayerSelectedAreaConfig, Player p, String selectedMainKey, String selectedSecondKey){
        for (String mainKey : PlayerSelectedAreaConfig.getKeys(false)) {
            if (mainKey.equals(selectedMainKey)) {
                for (String secondKey : PlayerSelectedAreaConfig.getConfigurationSection(mainKey).getKeys(false)) {
                    if (!secondKey.equals("Number-Of-Selected-Location") && secondKey.equals(selectedSecondKey)) {
                        for (String blocksReplaceBySelectedBlocksLocation : PlayerSelectedAreaConfig.getConfigurationSection(mainKey + "." + secondKey + ".areaInfo" + ".blocksReplaceBySelectedBlocks").getKeys(true)) {
                            Material blocksReplaceBySelectedBlocksType = Material.valueOf(PlayerSelectedAreaConfig.getString(mainKey + "." + secondKey + ".areaInfo" + ".blocksReplaceBySelectedBlocks" + "." + blocksReplaceBySelectedBlocksLocation));
                            String[] splitLocationParts = blocksReplaceBySelectedBlocksLocation.split(";");
                            World world = Bukkit.getWorld(splitLocationParts[0]);
                            int x = Integer.parseInt(splitLocationParts[1]);
                            int y = Integer.parseInt(splitLocationParts[2]);
                            int z = Integer.parseInt(splitLocationParts[3]);
                            Location location = new Location(world, x, y, z);
                            Block block = location.getBlock();
//                            block.setType(blocksReplaceBySelectedBlocksType);
                            OnPlayerItemHeld.playerIsGoldBorderBooleanMap.put(p.getUniqueId(), false);

                        }
                    }
                }
            }
        }

    }

}
