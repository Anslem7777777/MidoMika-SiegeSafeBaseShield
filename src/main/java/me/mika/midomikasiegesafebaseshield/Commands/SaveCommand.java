package me.mika.midomikasiegesafebaseshield.Commands;

import me.mika.midomikasiegesafebaseshield.SiegeSafeBaseShield;
import me.mika.midomikasiegesafebaseshield.Listeners.SelectArea;
import me.mika.midomikasiegesafebaseshield.Utils.SaveDataToConfig;
import me.mika.midomikasiegesafebaseshield.Utils.SaveSelectedBlocks;
import me.mika.midomikasiegesafebaseshield.Utils.SetRegionBorderToGoldBlocks;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import static me.mika.midomikasiegesafebaseshield.Utils.SaveDataToConfig.areaBlockLimit;
import static me.mika.midomikasiegesafebaseshield.Utils.SaveDataToConfig.blockCount;

public class SaveCommand extends SubCommands{
    SiegeSafeBaseShield plugin;
    public SaveCommand(SiegeSafeBaseShield plugin) {
        this.plugin = plugin;
    }

    // 创建SelectArea对象
    SelectArea SelectArea = new SelectArea(plugin);

    // 使用SelectArea对象访问selection变量
    Map<Player, Location[]> playerSelections = SelectArea.playerSelections;
    Map<Location, Material> blocksReplaceBySelectedBlocks = SelectArea.blocksReplaceBySelectedBlocks;
    Map<Location, Material> selectedBlocks = SelectArea.selectedBlocks;
    Map<Location, Material> selectedAreaOriBlocks = SelectArea.selectedAreaOriBlocks;
    Location[] selection = SelectArea.selection;
    public Location minLocation;
    public Location maxLocation;
    String finalSecondKeyAndNumber = SelectArea.finalSecondKeyAndNumber;
    int count = SelectArea.count;
    String secondKeyAndNumber = SelectArea.secondKeyAndNumber;
    String secondKey = SelectArea.secondKey;
    ArrayList<String> nameValueList = new ArrayList<>();

    @Override
    public String getName() {
        return "save";
    }

    @Override
    public String getDescription() {
        return "Set a SSBase";
    }

    @Override
    public String getSyntax() {
        return "/ssbs save <your SSBase name>";
    }

    @Override
    public void perform(Player player, String[] args) {
        File file = new File(Bukkit.getServer().getPluginManager().getPlugin("SiegeSafeBaseShield").getDataFolder(), "config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        File PlayerSelectedAreaFile = new File(Bukkit.getServer().getPluginManager().getPlugin("SiegeSafeBaseShield").getDataFolder(), "PlayerSelectedAreaConfig.yml");
        FileConfiguration PlayerSelectedAreaConfig = YamlConfiguration.loadConfiguration(PlayerSelectedAreaFile);

        count = 1;
        secondKey = "Number-Of-Selected-Location";
        secondKeyAndNumber = secondKey + count;

        //TODO - 如果areaName不等于null就存放到arrayList里，准备调用
        ConfigurationSection selectionMainKeySection = PlayerSelectedAreaConfig.getConfigurationSection(player.getName());
        if (selectionMainKeySection.equals(null)) {
//            player.sendMessage(ChatColor.YELLOW + "NULL");
        }
        selectionMainKeySection.getKeys(false).stream().forEach(secondKeys -> {

            if (!secondKeys.equals(null)) {
                String nameValue = PlayerSelectedAreaConfig.getString(player.getName() + "." + secondKeys + ".areaInfo" + ".name");
                nameValueList.add(nameValue);
            }

        });
        //TODO - 如果areaName不等于null就存放到arrayList里，准备调用
        if (args.length > 1) {
            //使用 StringBuilder 构建
            StringBuilder builder = new StringBuilder();
            for (int i = 1; i < args.length; i++){
                builder.append(args[i]).append(" ");

            }
            String areaName = builder.toString().trim();

            if (!(nameValueList.contains(areaName)) || nameValueList.equals(null)) {
                selection = playerSelections.getOrDefault(player, new Location[2]);

                if (selection[0] != null && selection[1] != null) {
                    minLocation = new Location(
                            player.getWorld(),
                            Math.min(selection[0].getX(), selection[1].getX()),
                            Math.min(selection[0].getY(), selection[1].getY()),
                            Math.min(selection[0].getZ(), selection[1].getZ())
                    );
                    maxLocation = new Location(
                            player.getWorld(),
                            Math.max(selection[0].getX(), selection[1].getX()),
                            Math.max(selection[0].getY(), selection[1].getY()),
                            Math.max(selection[0].getZ(), selection[1].getZ())
                    );

                    if ((!PlayerSelectedAreaConfig.contains(player.getName() + "." + secondKeyAndNumber)) && selection[0] != null && selection[1] != null) {
                        finalSecondKeyAndNumber = secondKeyAndNumber;
                        SaveDataToConfig.saveSelectedAreaBlock(minLocation, maxLocation, player, finalSecondKeyAndNumber, areaName);

                        // 暂时将选中区域的边框设定为黄金块
                        SetRegionBorderToGoldBlocks.setRegionBorderToGoldBlocks(minLocation, maxLocation, player);

                        SaveSelectedBlocks.saveSelectedBlocks(minLocation, maxLocation, player);
                        SaveDataToConfig.saveDataToConfig(player, finalSecondKeyAndNumber);

                        if (blockCount > areaBlockLimit) {

                        } else if (blockCount < areaBlockLimit) {
                            sendActionBarMessage(player, ChatColor.GREEN + "Saved " + "'" + areaName + "'");

                            //save selected area to config
                            config.set(player.getName() + " Count", count);
//                                PlayerSelectedAreaConfig.set(p.getName() + "." + secondKeyAndNumber, null);
                            try {
                                config.save(file);
                            } catch (IOException error) {
                                System.out.println("Save File Error");
                            }
                        }

                        blockCount = 0;
                        selectedBlocks.clear();
                        blocksReplaceBySelectedBlocks.clear();
                        selectedAreaOriBlocks.clear();
                        selection[0] = null;
                        selection[1] = null;

                        // 清除选择并重置数据
                        playerSelections.remove(player);

                    } else if ((PlayerSelectedAreaConfig.contains(player.getName() + "." + secondKeyAndNumber)) && selection[0] != null && selection[1] != null) {
                        count = config.getInt(player.getName() + " Count");

                        count++;
                        config.set(player.getName() + " Count", count);
                        secondKeyAndNumber = secondKey + count;

                        finalSecondKeyAndNumber = secondKeyAndNumber;
                        SaveDataToConfig.saveSelectedAreaBlock(minLocation, maxLocation, player, finalSecondKeyAndNumber, areaName);

                        // 暂时将选中区域的边框设定为黄金块
                        SetRegionBorderToGoldBlocks.setRegionBorderToGoldBlocks(minLocation, maxLocation, player);

                        SaveSelectedBlocks.saveSelectedBlocks(minLocation, maxLocation, player);
                        SaveDataToConfig.saveDataToConfig(player, finalSecondKeyAndNumber);

                        if (blockCount > areaBlockLimit) {

                        } else if (blockCount < areaBlockLimit) {
                            sendActionBarMessage(player, ChatColor.GREEN + "Saved " + "'" + areaName + "'");

                            try {
                                config.save(file);
                            } catch (IOException error) {
                                System.out.println("Save File Error");
                            }
                        }


                        selectedBlocks.clear();
                        blocksReplaceBySelectedBlocks.clear();
                        selectedAreaOriBlocks.clear();
                        selection[0] = null;
                        selection[1] = null;

                        // 清除选择并重置数据
                        playerSelections.remove(player);
                        blockCount = 0;

                    }
                } else {
                    sendActionBarMessage(player, ChatColor.RED + "You need to select an area before save!");
                }
            } else if (nameValueList.contains(areaName)) {
                sendActionBarMessage(player, ChatColor.RED + "The area name has been repeated, please change.");
            }
        } else {
            sendActionBarMessage(player, ChatColor.RED + "Example : /bsg save [name]");

        }
        nameValueList.clear();
    }

    private void sendActionBarMessage(Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
    }
}
