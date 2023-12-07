package me.mika.midomikasiegesafebaseshield.Listeners;

import me.mika.midomikasiegesafebaseshield.SiegeSafeBaseShield;
import me.mika.midomikasiegesafebaseshield.Utils.CheckSelectArea;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class SelectArea implements Listener {

    SiegeSafeBaseShield plugin;

    public SelectArea(SiegeSafeBaseShield plugin) {
        this.plugin = plugin;
    }

    public static Map<Player, Location[]> playerSelections = new HashMap<>();
    public static Map<Location, Material> blocksReplaceBySelectedBlocks = new HashMap<>();
    public static Map<Location, Material> selectedBlocks = new HashMap<>();
    public static Map<Location, Material> selectedAreaOriBlocks = new HashMap<>();
    Set<String> selectedAreaOriBlocks2;
    public String finalSecondKeyAndNumber;
    public int count;
    public String secondKeyAndNumber;
    public static Location[] selection;
    public String secondKey;
    private HashMap<String, String> allRecordBlock = new HashMap<>();
    public static int mainKeyCount = 0;


    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Block clickedBlock = e.getClickedBlock();
        String playerName = player.getName();
        Block finalClickedBlock= (clickedBlock != null) ? clickedBlock : e.getClickedBlock();

        if (clickedBlock != null  && !clickedBlock.getType().equals(Material.BEDROCK)) {
            //以下全部都是config用的Variable
            File file = new File(Bukkit.getServer().getPluginManager().getPlugin("SiegeSafeBaseShield").getDataFolder(), "config.yml");
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            File PlayerSelectedAreaFile = new File(Bukkit.getServer().getPluginManager().getPlugin("SiegeSafeBaseShield").getDataFolder(), "PlayerSelectedAreaConfig.yml");
            FileConfiguration PlayerSelectedAreaConfig = YamlConfiguration.loadConfiguration(PlayerSelectedAreaFile);
            count = 1;
            secondKey = "Number-Of-Selected-Location";
            secondKeyAndNumber = secondKey + count;
            int limit = config.getInt("player-area-limit");
            //以上全部都是config用的Variable

            //以下全部从config中获取value并存入allRecordBlock
            for (String mainKey : PlayerSelectedAreaConfig.getKeys(false)) {
                for (String secondKey : PlayerSelectedAreaConfig.getConfigurationSection(mainKey).getKeys(false)) {
                    if (!secondKey.equals("Number-Of-Selected-Location")) {
                        mainKeyCount++;
                        try {
                            selectedAreaOriBlocks2 = PlayerSelectedAreaConfig.getConfigurationSection(mainKey).getConfigurationSection(secondKey).getConfigurationSection("areaInfo").getConfigurationSection("selectedAreaOriBlocks").getKeys(false);
                        } catch (Exception error) {
                            player.sendMessage(error.toString());
                        }
                        //mainkey 玩家名字加上forloop次数准备放入hashmap
                        String mainKeyAndNumber = mainKey + ":" + mainKeyCount;
                        //获取玩家area所有数据里的totalBlockHardness
                        int selectedAreaOriBlocksHardness = PlayerSelectedAreaConfig.getInt(mainKey + "." + secondKey + ".areaInfo" + ".totalBlockHardness");
                        int selectedAreaOriRepairBlocksHardness = PlayerSelectedAreaConfig.getInt(mainKey + "." + secondKey + ".areaInfo" + ".repairTotalBlockHardness");

                        //列子：Ansalem2608:1=Ansalem2608 - Number-Of-Selected-Location3 - [world;-39;59;39, world;-39;59;40, world;-38;59;39, world;-38;59;40]BlockHardness=70=repairTotalBlockHardness=70
                        allRecordBlock.put(mainKeyAndNumber, mainKey + " - " + secondKey + " - " + selectedAreaOriBlocks2 + "BlockHardness=" + selectedAreaOriBlocksHardness + "=repairTotalBlockHardness=" + selectedAreaOriRepairBlocksHardness);

                    }
                }
                mainKeyCount = 0;
            }
            //以上全部从config中获取value并存入allRecordBlock

            // 检查玩家config数据，和手持物品是否为魔杖（例如stick）
            if ((!PlayerSelectedAreaConfig.contains(player.getName() + "." + secondKeyAndNumber)) && (e.getItem() != null && e.getItem().getType() == Material.STICK)) {
                if (clickedBlock != null) {
                    Location clickedLocation = clickedBlock.getLocation();
                    selection = playerSelections.getOrDefault(player, new Location[2]);
                    // 第一次点击，设置第一个点
                    if (selection[0] == null && e.getAction() == Action.LEFT_CLICK_BLOCK) {
                        e.setCancelled(true);
                        selection[0] = clickedLocation;
                        CheckSelectArea.checkLeftClickSetSelectArea(allRecordBlock, player);

                    }
                    // 第二次点击，设置第二个点并计算区域
                    else if (selection[0] != null && selection[1] == null && e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                        selection[1] = clickedLocation;
                        CheckSelectArea.checkRightClickSetSelectArea(allRecordBlock, player);
                        CheckSelectArea.checkSelectArea(allRecordBlock, player);

                    } else if (selection[0] != null && e.getAction() == Action.LEFT_CLICK_BLOCK) {
                        e.setCancelled(true);
                        selection[0] = clickedLocation;
                        CheckSelectArea.checkLeftClickResetSelectArea(allRecordBlock, player);

                    } else if (selection[1] != null && e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                        selection[1] = clickedLocation;
                        CheckSelectArea.checkRightClickResetSelectArea(allRecordBlock, player);
                        CheckSelectArea.checkSelectArea(allRecordBlock, player);

                    } else if (selection[0] == null && selection[1] == null && e.getAction() == Action.RIGHT_CLICK_BLOCK) {
//                        player.sendMessage(ChatColor.RED + "Please set the first point.");
                        sendActionBarMessage(player, ChatColor.RED + "Please set the first point.");

                    }
                    // 保存玩家的选择区域(需要）
                    playerSelections.put(player, selection);
                }

                // 检查玩家config数据，手持物品是否为魔杖（例如stick）
            } else if ((PlayerSelectedAreaConfig.contains(player.getName() + "." + secondKeyAndNumber)) && (e.getItem() != null && e.getItem().getType() == Material.STICK)) {
                //这个code需要运行两次因为要储存selection 1 和 2 放count在这里会++X2
                count = config.getInt(playerName + " Count");
                if (limit > count) {
                    if (clickedBlock != null) {
                        Location clickedLocation = clickedBlock.getLocation();
                        selection = playerSelections.getOrDefault(player, new Location[2]);
                        // 第一次点击，设置第一个点
                        if (selection[0] == null && e.getAction() == Action.LEFT_CLICK_BLOCK) {
                            e.setCancelled(true);
                            selection[0] = clickedLocation;
                            CheckSelectArea.checkLeftClickSetSelectArea(allRecordBlock, player);

                        }
                        // 第二次点击，设置第二个点并计算区域
                        else if (selection[0] != null && selection[1] == null && e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                            selection[1] = clickedLocation;
                            CheckSelectArea.checkRightClickSetSelectArea(allRecordBlock, player);
                            CheckSelectArea.checkSelectArea(allRecordBlock, player);

                        } else if (selection[0] != null && e.getAction() == Action.LEFT_CLICK_BLOCK) {
                            e.setCancelled(true);
                            selection[0] = clickedLocation;
                            CheckSelectArea.checkLeftClickResetSelectArea(allRecordBlock, player);

                        } else if (selection[1] != null && e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                            selection[1] = clickedLocation;
                            CheckSelectArea.checkRightClickResetSelectArea(allRecordBlock, player);
                            CheckSelectArea.checkSelectArea(allRecordBlock, player);

                        } else if (selection[0] == null && selection[1] == null && e.getAction() == Action.RIGHT_CLICK_BLOCK) {
//                            player.sendMessage(ChatColor.RED + "请先设置第一个点");
                            sendActionBarMessage(player, ChatColor.RED + "Please set the first point.");

                        }

                        // 保存玩家的选择区域
                        playerSelections.put(player, selection);
                    }
                } else if (count >= limit) {
                    e.setCancelled(true);
                    sendActionBarMessage(player, ChatColor.RED + "Exceeded the limit.");

                }
            }
            allRecordBlock.clear();
        }

    }

    private void sendActionBarMessage(Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
    }
}


