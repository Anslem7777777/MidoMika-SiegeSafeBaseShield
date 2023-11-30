package me.mika.midomikasiegesafebaseshield.Commands;

import me.mika.midomikasiegesafebaseshield.Utils.RefreshPlayerSelectedAreaConfig;
import me.mika.midomikasiegesafebaseshield.Utils.RestoreRegionBlocks;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class DeleteCommand extends SubCommands {
    ArrayList<String> finalSecondKeyAndNumberList = new ArrayList<>();

    int secondKeyCount = 0;

    String refreshFinalSecondKeyAndNumber;

    @Override
    public String getName() {
        return "delete";
    }

    @Override
    public String getDescription() {
        return "Delete a SSBase";
    }

    @Override
    public String getSyntax() {
        return "/ssbs delete <your SSBase name>";
    }

    @Override
    public void perform(Player player, String[] args) {
        if (args.length > 1) {
            //使用 StringBuilder 构建
            StringBuilder builder = new StringBuilder();
            for (int i = 1; i < args.length; i++){
                builder.append(args[i]).append(" ");

            }
            String areaName = builder.toString().trim();

            File file = new File(Bukkit.getServer().getPluginManager().getPlugin("SiegeSafeBaseShield").getDataFolder(), "config.yml");
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            File PlayerSelectedAreaFile = new File(Bukkit.getServer().getPluginManager().getPlugin("SiegeSafeBaseShield").getDataFolder(), "PlayerSelectedAreaConfig.yml");
            FileConfiguration PlayerSelectedAreaConfig = YamlConfiguration.loadConfiguration(PlayerSelectedAreaFile);
            Boolean deletedSelectedBlock = false;

            for (String mainKey : PlayerSelectedAreaConfig.getKeys(false)){
                if (player.getName().equals(mainKey)){
                    for (String secondKey : PlayerSelectedAreaConfig.getConfigurationSection(mainKey).getKeys(false)){
                        if (!secondKey.equals("Number-Of-Selected-Location")){
                            String configAreaName = PlayerSelectedAreaConfig.getString(mainKey + "." + secondKey + ".areaInfo" + ".name");
                            if (areaName.equals(configAreaName)){
                                RestoreRegionBlocks.restoreBLockBeforeBreak(PlayerSelectedAreaConfig, player, mainKey, secondKey);

                                // 删除被选择的 Number-Of-Selected-Location 子节点
                                PlayerSelectedAreaConfig.set(mainKey + "." + secondKey, null);

                                //refresh player limit
                                Integer playerLimit = config.getInt(mainKey + " Count");
                                config.set(mainKey + " Count", playerLimit - 1);

                                ConfigurationSection selectionMainKeySection = PlayerSelectedAreaConfig.getConfigurationSection(player.getName());

                                //整理secondKey
                                for (String secondKeys2 : selectionMainKeySection.getKeys(false)){
                                    if (!secondKeys2.equals("Number-Of-Selected-Location")) {
                                        secondKeyCount++;
                                        refreshFinalSecondKeyAndNumber = "Number-Of-Selected-Location" + secondKeyCount;
                                        RefreshPlayerSelectedAreaConfig.RefreshSelectionConfig(player, PlayerSelectedAreaConfig, config, secondKeys2, refreshFinalSecondKeyAndNumber);

                                    } else if (secondKeys2.equals("Number-Of-Selected-Location")) {
//                                        player.sendMessage(ChatColor.RED + "Error: Save error!");

                                    }
                                }
                                secondKeyCount = 0;

                                // 保存 YML 文件
                                try {
                                    config.save(file);
                                    PlayerSelectedAreaConfig.save(PlayerSelectedAreaFile);
                                    player.sendMessage(ChatColor.GREEN + "Area has been deleted.");
                                } catch (IOException error) {
                                    System.out.println("Save File Error: " + error);
                                }

                                deletedSelectedBlock = true;
                                break;
                            }else {

                            }
                        }
                    }
                }
            }
            if (!deletedSelectedBlock) {
                player.sendMessage(ChatColor.RED + "Area Name Not Found!");
            }

            //------------------------------------BEFORE--------------------------------------------//
//            if (!(PlayerSelectedAreaConfig.getConfigurationSection(player.getName()) == null)) {
//                ConfigurationSection selectionMainKeySection = PlayerSelectedAreaConfig.getConfigurationSection(player.getName());
//
//                //get record count from config
//                int limit = config.getInt("player-area-limit");
//                AtomicInteger runCount = new AtomicInteger(1);
//
//                for(String secondKeys : selectionMainKeySection.getKeys(false)){
//                    finalSecondKeyAndNumberList.add(secondKeys);
//
//                }
//
//                for (String secondKeys : finalSecondKeyAndNumberList){
//                    String nameValue = PlayerSelectedAreaConfig.getString(player.getName() + "." + secondKeys + ".areaInfo" + ".name");
//
//                    if (areaName.equals(nameValue)) {
//                        String deleteObject = player.getName() + "." + secondKeys;
//                        ConfigurationSection blocksReplaceBySelectedBlocks = PlayerSelectedAreaConfig.getConfigurationSection(player.getName() + "." + secondKeys + ".areaInfo" + ".blocksReplaceBySelectedBlocks");
//
//                        if (blocksReplaceBySelectedBlocks != null) {
//                            Set<String> keys = blocksReplaceBySelectedBlocks.getKeys(false);
//
//                            for (String key : keys) {
//                                String locationKey = key;
//                                String materialName = PlayerSelectedAreaConfig.getString(player.getName() + "." + secondKeys + ".areaInfo" + ".blocksReplaceBySelectedBlocks." + locationKey);
//
//                                String[] locationData = locationKey.split(";");
//                                World worldName =   Bukkit.getWorld(locationData[0]);
//                                int x = Integer.parseInt(locationData[1]);
//                                int y = Integer.parseInt(locationData[2]);
//                                int z = Integer.parseInt(locationData[3]);
//
//                                if (worldName != null) {
//                                    Location location = new Location(worldName, x, y, z);
//
//                                    Material material = Material.valueOf(materialName);
//                                    Block block = location.getBlock();
//                                    block.setType(material);
//                                } else {
//                                    // 处理无效的世界名称
//                                    // 这里可以添加适当的处理逻辑，比如发送错误消息给玩家等
//                                }
//                            }
//                        }
//
//                        // 删除被选择的 Number-Of-Selected-Location 子节点
//                        PlayerSelectedAreaConfig.set(deleteObject, null);
//
//                        //整理secondKey
//                        for (String secondKeys2 : selectionMainKeySection.getKeys(false)){
//                            if (!secondKeys2.equals("Number-Of-Selected-Location")) {
//                                secondKeyCount++;
//                                refreshFinalSecondKeyAndNumber = secondKey + secondKeyCount;
//                                RefreshPlayerSelectedAreaConfig.RefreshSelectionConfig(player, PlayerSelectedAreaConfig, config, secondKeys2, refreshFinalSecondKeyAndNumber);
//
//                            } else if (secondKeys2.equals("Number-Of-Selected-Location")) {
//                                player.sendMessage(ChatColor.RED + "Error: Save error!");
//
//                            }
//
//                        };
//                        secondKeyCount = 0;
//
//                        // 保存 YML 文件
//                        try {
//                            PlayerSelectedAreaConfig.save(PlayerSelectedAreaFile);
//                            player.sendMessage(ChatColor.GREEN + "Saved!");
//                        } catch (IOException error) {
//                            player.sendMessage("Save File Error: " + error);
//                        }
//
//                    } else if (!areaName.equals(nameValue)) {
//                        player.sendMessage(ChatColor.GOLD + "areaName: " + ChatColor.AQUA + areaName);
//                        player.sendMessage(ChatColor.GOLD + "nameValue: " + ChatColor.AQUA + nameValue);
//                        player.sendMessage(ChatColor.RED + "Area Name Not Found!");
//
//                    }
//                }
//
//                //Refresh Player Limit
//                finalSecondKeyAndNumberList.clear();
//
//                for (String secondKeys2 : selectionMainKeySection.getKeys(false)){
//                    finalSecondKeyAndNumberList.add(secondKeys2);
//
//                }
//
//                config.set(player.getName() +" Count", finalSecondKeyAndNumberList.size() - 1);
//                try {
//                    config.save(file);
//                    player.sendMessage(ChatColor.GREEN + "Saved!");
//                } catch (IOException error) {
//                    player.sendMessage("Save File Error: " + error);
//                }
//
//                finalSecondKeyAndNumberList.clear();
//                secondKeyCount = 0;
//                //Refresh Player Limit
//
//            } else if (PlayerSelectedAreaConfig.getConfigurationSection(player.getName()) == null) {
//                player.sendMessage(ChatColor.RED + "Record Not Found!");
//
//            }
//        } else {
//            player.sendMessage(ChatColor.RED + "Example : /areadelete [name]");
//
        }else {
            player.sendMessage(ChatColor.RED + "Please select an area name to delete");

        }
    }
}
