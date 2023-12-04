package me.mika.midomikasiegesafebaseshield.Listeners;

import me.mika.midomikasiegesafebaseshield.Utils.BaseHealthBar;
import me.mika.midomikasiegesafebaseshield.Utils.RefreshPlayerSelectedAreaConfig;
import me.mika.midomikasiegesafebaseshield.Utils.RestoreRegionBlocks;
import me.mika.midomikasiegesafebaseshield.SiegeSafeBaseShield;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.TNTPrimeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static me.mika.midomikasiegesafebaseshield.Listeners.OnBreakSelectedArea.playerAreaGetBreakBlockTime;

public class OnTNTBreakSelectedArea implements Listener {
    SiegeSafeBaseShield plugin;
    public OnTNTBreakSelectedArea(SiegeSafeBaseShield plugin) {
        this.plugin = plugin;
    }
    private Double hardnessValue;
    private Double repairHardnessValue;
    private LocalDateTime breakBlockTime;
    private String mainKeyAndNumber;
    private Integer  secondKeyCount = 0;
    private String refreshFinalSecondKeyAndNumber;
    private static Double explodeTotalHardness = 0.0;
    private static Entity whoActiveTNT;
    private Set<String> selectedAreaOriBlocks;
    private String secondKeyName = "Number-Of-Selected-Location";
    private HashMap<String, String> allRecordBlock = new HashMap<>();
    private List<String> stringExplodeBlockLocation = new ArrayList<>();
    public static HashMap<Player, LocalDateTime> playerBreakBlockTime = new HashMap<>();
    public static int mainKeyCount = 0;
    private String areaPlayerName;
    private String areaSecondKey;

    @EventHandler
    public void onPlayerExplodeTNT(TNTPrimeEvent e) {
        if (e.getPrimingEntity() != null && !e.getPrimingEntity().toString().equals("CraftTNTPrimed")) {
            whoActiveTNT = e.getPrimingEntity();
            boolean isBossBarCreated = OnBreakSelectedArea.playerIsBossBarCreatedMap.getOrDefault(whoActiveTNT.getUniqueId(), false);
            OnBreakSelectedArea.playerIsBossBarCreatedMap.put(whoActiveTNT.getUniqueId(), isBossBarCreated);
            File file = new File(Bukkit.getServer().getPluginManager().getPlugin("SiegeSafeBaseShield").getDataFolder(), "config.yml");
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            File PlayerSelectedAreaFile = new File(Bukkit.getServer().getPluginManager().getPlugin("SiegeSafeBaseShield").getDataFolder(), "PlayerSelectedAreaConfig.yml");
            FileConfiguration PlayerSelectedAreaConfig = YamlConfiguration.loadConfiguration(PlayerSelectedAreaFile);
            ConfigurationSection selectionMainKeySection = PlayerSelectedAreaConfig.getConfigurationSection(whoActiveTNT.getName());

            Boolean selfConfigBlock = false;
            Boolean allConfigBlock = false;

            Block litBlock = e.getBlock();
            String litBlockType = litBlock.getType().toString();
            String litBlockWorld = litBlock.getWorld().getName();
            int litBlockX = litBlock.getX();
            int litBlockY = litBlock.getY();
            int litBlockZ = litBlock.getZ();
            String litBlockLocationString = litBlockWorld + ";" + litBlockX + ";" + litBlockY + ";" + litBlockZ;

            if (litBlockType.equals("TNT")) {
                //TODO - 检查全部的selection 的 block有没有被选中
                int recordCount = 0;
                for (String mainKey : PlayerSelectedAreaConfig.getKeys(false)) {
                    for (String secondKey : PlayerSelectedAreaConfig.getConfigurationSection(mainKey).getKeys(false)) {
                        if (!secondKey.equals("Number-Of-Selected-Location")) {
                            recordCount++;
                            String mainKeyAndNumber = mainKey + ":" + recordCount;
                            selectedAreaOriBlocks = PlayerSelectedAreaConfig.getConfigurationSection(mainKey + "." + secondKey + "." + ".areaInfo" + ".selectedAreaOriBlocks").getKeys(false);
                            Double selectedAreaOriBlocksHardness = PlayerSelectedAreaConfig.getDouble(mainKey + "." + secondKey + "." + ".areaInfo" + ".totalBlockHardness");
                            Double selectedAreaOriRepairBlocksHardness = PlayerSelectedAreaConfig.getDouble(mainKey + "." + secondKey + "." + ".areaInfo" + ".repairTotalBlockHardness");

                            allRecordBlock.put(mainKeyAndNumber, selectedAreaOriBlocks + "totalBlockHardness=" + selectedAreaOriBlocksHardness + "=repairTotalBlockHardness=" + selectedAreaOriRepairBlocksHardness);

                        }
                    }
                    recordCount = 0;
                }

                //检查是不是在config里的数据，用于排除不是normal block
                if (String.valueOf(allRecordBlock.values().toString().contains(litBlockLocationString)).equals("true")) {
                    allConfigBlock = true;

                } else {

                }

                //TODO - 检查全部的selection 的 block有没有被选中


                //TODO - 检查自己的selection 的 block有没有被选中
                int secondKeysCount2 = selectionMainKeySection.getKeys(false).size();

                for (int i = 1; i < secondKeysCount2; i++) {
                    String mainKeyAndNumber = whoActiveTNT.getName() + ":" + i;
                    //检查是不是config里跟玩家名字相符的数据，用于从allConfigBlock中分离
                    if (String.valueOf(allRecordBlock.get(mainKeyAndNumber).contains(litBlockLocationString)).equals("true")) {
                        selfConfigBlock = true;
                        break;

                    } else {

                    }
                }
                allRecordBlock.clear();
                //TODO - 检查自己的selection 的 block有没有被选中
            }

            if (allConfigBlock == true && selfConfigBlock == true) {
                //自己的Block
                e.setCancelled(false);

            } else if (allConfigBlock == true && selfConfigBlock == false) {
                //其他player的block
                e.setCancelled(true);

            }
        }else {

        }
    }

    @EventHandler
    public void onTNTBreak(EntityExplodeEvent e) {
        File file = new File(Bukkit.getServer().getPluginManager().getPlugin("SiegeSafeBaseShield").getDataFolder(), "config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        File PlayerSelectedAreaFile = new File(Bukkit.getServer().getPluginManager().getPlugin("SiegeSafeBaseShield").getDataFolder(), "PlayerSelectedAreaConfig.yml");
        FileConfiguration PlayerSelectedAreaConfig = YamlConfiguration.loadConfiguration(PlayerSelectedAreaFile);
        if (whoActiveTNT != null && !whoActiveTNT.toString().equals("CraftTNTPrimed")) {
            ConfigurationSection selectionMainKeySection = PlayerSelectedAreaConfig.getConfigurationSection(whoActiveTNT.getName());
            HashMap<String, String> selectedBlockList = new HashMap<>();
            Boolean selfConfigBlock = false;
            Boolean allConfigBlock = false;
            int areaLimit = config.getInt("player-area-limit");
            breakBlockTime = LocalDateTime.now();

            //把被TNT破坏的Block重新排列成我想要的world x y z
            for (Block explodeBlock : e.blockList()) {
                Location explodeBlockLocation = explodeBlock.getLocation();
                int explodeBlockX = explodeBlock.getLocation().getBlockX();
                int explodeBlockY = explodeBlock.getLocation().getBlockY();
                int explodeBlockZ = explodeBlock.getLocation().getBlockZ();
                String explodeBlockType = explodeBlock.getType().toString();
                Double explodedBlockHardness = (double) explodeBlock.getType().getHardness();

                //用forloop一个个获取被爆破的block的hardness并加到explodeTotalHardness中
                if (explodeTotalHardness <= 0) {
                    explodeTotalHardness = explodedBlockHardness;

                } else if (explodeTotalHardness > 0) {
                    explodeTotalHardness = explodeTotalHardness + explodedBlockHardness;

                }

                //列子：world;-26;72;42, world;-28;72;42, world;-27;72;41, world;-26;72;41, world;-28;72;43, world;-27;72;43, world;-28;72;41, world;-26;72;43
                stringExplodeBlockLocation.add(explodeBlockLocation.getWorld().getName() + ";" + explodeBlockX + ";" + explodeBlockY + ";" + explodeBlockZ);
            }

            for (String mainKey : PlayerSelectedAreaConfig.getKeys(false)) {
                for (String secondKey : PlayerSelectedAreaConfig.getConfigurationSection(mainKey).getKeys(false)) {
                    if (!secondKey.equals("Number-Of-Selected-Location")) {
                        mainKeyCount++;
                        try {
                            selectedAreaOriBlocks = PlayerSelectedAreaConfig.getConfigurationSection(mainKey).getConfigurationSection(secondKey).getConfigurationSection("areaInfo").getConfigurationSection("selectedAreaOriBlocks").getKeys(false);
                        } catch (Exception error) {
                            System.out.println(error.toString());
                        }

                        //mainkey 玩家名字加上forloop次数准备放入hashmap
                        mainKeyAndNumber = mainKey + ":" + mainKeyCount;
                        //获取玩家area所有数据里的totalBlockHardness
                        Double selectedAreaOriBlocksHardness = PlayerSelectedAreaConfig.getDouble(mainKey + "." + secondKey + ".areaInfo" + ".totalBlockHardness");
                        Double selectedAreaOriRepairBlocksHardness = PlayerSelectedAreaConfig.getDouble(mainKey + "." + secondKey + ".areaInfo" + ".repairTotalBlockHardness");

                        //列子：Ansalem2608:1=Ansalem2608 - Number-Of-Selected-Location3 - [world;-39;59;39, world;-39;59;40, world;-38;59;39, world;-38;59;40]BlockHardness=70=repairTotalBlockHardness=70
                        allRecordBlock.put(mainKeyAndNumber, mainKey + " - " + secondKey + " - " + selectedAreaOriBlocks + "BlockHardness=" + selectedAreaOriBlocksHardness + "=repairTotalBlockHardness=" + selectedAreaOriRepairBlocksHardness);

                    }
                }
                mainKeyCount = 0;
            }

            //检查是不是在config里的数据，用于排除不是normal block
            for (String explodeBlockString : stringExplodeBlockLocation) {
                if (String.valueOf(allRecordBlock.values().toString().contains(explodeBlockString)).equals("true")) {
                    allConfigBlock = true;

                } else {
                }

                int secondKeysCount = selectionMainKeySection.getKeys(false).size();
                for (int i = 1; i < secondKeysCount; i++) {
                    String mainKeyAndNumber = whoActiveTNT.getName() + ":" + i;

                    //检查是不是config里跟玩家名字相符的数据，用于从allConfigBlock中分离
                    if (String.valueOf(allRecordBlock.get(mainKeyAndNumber).contains(explodeBlockString)).equals("true")) {
                        selfConfigBlock = true;
                        break;

                    } else {
                    }
                }
            }

            //把allRecordBlock中的key拆开 （Ansalem2608:1）
            for (String key : allRecordBlock.keySet()) {
                //从列子：world;-26;72;42, world;-28;72;42, world;-27;72;41中拆开获取
                for (String explodeBlockString : stringExplodeBlockLocation) {
                    if (allRecordBlock.get(key).contains(explodeBlockString)) {
                        String blockExplodeListValue = allRecordBlock.get(key);

                        // 步骤1：将字符串分割成两部分 BlockHardness=4拆开变成，
                        // BlockHardness = splitHardnessParts[0]，4 = splitHardnessParts[1]
                        String[] splitplayerNameParts = blockExplodeListValue.split(" - ");
                        areaPlayerName = splitplayerNameParts[0];
                        areaSecondKey = splitplayerNameParts[1];

                        //列子：{Ansalem2608=Ansalem2608 - Number-Of-Selected-Location3 -
                        // [world;-39;59;39, world;-39;59;40, world;-38;59;39, world;-38;59;40]BlockHardness=70=repairTotalBlockHardness=70
                        selectedBlockList.put(areaPlayerName, allRecordBlock.get(key));

                        //如果selectedBlockList的数据里面有符合explodeBlockString的数据，就把BlockHardness拆开备用
                        if (selectedBlockList.get(areaPlayerName).contains(explodeBlockString)) {
                            String blockBreakingListValue = selectedBlockList.get(areaPlayerName);

                            // 步骤1：将字符串分割成两部分 BlockHardness=4拆开变成，
                            // BlockHardness = splitHardnessParts[0]，4 = splitHardnessParts[1]
                            String[] splitHardnessParts = blockBreakingListValue.split("=");
                            hardnessValue = Double.parseDouble(splitHardnessParts[1]);
                            repairHardnessValue = Double.parseDouble(splitHardnessParts[3]);

                        }
                    }
                }
            }

            if (allConfigBlock == true && selfConfigBlock == true) {
                //自己的block
                e.setCancelled(true);

            } else if (allConfigBlock == true && selfConfigBlock == false) {
                //其他player的block
                e.setCancelled(true);

                //如果 hardnessValue 大于等于0，就设置config里的 totalBlockHardness 数值为 hardnessValue 剪掉所有被炸到的block的hardness（explodeTotalHardness）
                if (hardnessValue >= 0 && !(explodeTotalHardness > hardnessValue)) {
                    LocalDateTime now = LocalDateTime.now();
                    //把爆破当下的时间记录到config中等待触发
                    PlayerSelectedAreaConfig.set(areaPlayerName + "." + areaSecondKey + ".areaInfo" + ".breakBlockTime", breakBlockTime.toString());
                    String areaName = PlayerSelectedAreaConfig.getString(areaPlayerName + "." + areaSecondKey + ".areaInfo" + ".name");
                    OnBreakSelectedArea.playerBreakBlockTime.put((Player) whoActiveTNT, breakBlockTime);
                    if (!OnBreakSelectedArea.playerIsBossBarCreatedMap.get(whoActiveTNT.getUniqueId())) {
                        OnBreakSelectedArea.baseHealthBar = new BaseHealthBar(plugin, (Player) whoActiveTNT);
                        OnBreakSelectedArea.baseHealthBar.showBossBar(areaPlayerName);
                        OnBreakSelectedArea.playerIsBossBarCreatedMap.put(whoActiveTNT.getUniqueId(), true);

                    }
                    PlayerSelectedAreaConfig.set(areaPlayerName + "." + areaSecondKey + ".areaInfo" + ".totalBlockHardness", hardnessValue - explodeTotalHardness);
                    hardnessValue = (Double) PlayerSelectedAreaConfig.getDouble(areaPlayerName + "." + areaSecondKey + ".areaInfo" + ".totalBlockHardness");
                    double bossBarProgress = hardnessValue / repairHardnessValue;
                    OnBreakSelectedArea.baseHealthBar = new BaseHealthBar(plugin, (Player) whoActiveTNT);
                    OnBreakSelectedArea.baseHealthBar.updateBossBars(hardnessValue, repairHardnessValue);
                    ((Player) whoActiveTNT).playSound(whoActiveTNT.getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.1f, 1.0f);
                    ((Player) whoActiveTNT).playSound(whoActiveTNT.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1f, 1.0f);

                    try {

                        if (playerAreaGetBreakBlockTime.toString().equals("{}")) {
                            playerAreaGetBreakBlockTime.put(Bukkit.getPlayer(areaPlayerName), now);
                            Bukkit.getPlayer(areaPlayerName).sendMessage(ChatColor.YELLOW + "Caution! Your area " + ChatColor.GREEN + areaName + ChatColor.YELLOW + " has been attacked!");

                        }
                        Duration duration = Duration.between(playerAreaGetBreakBlockTime.get(Bukkit.getPlayer(areaPlayerName)), now);
                        long finalTime = duration.toSeconds();

                        if (finalTime >= 5) {
                            if (bossBarProgress < 1 && bossBarProgress > 0.75) {
                                Bukkit.getPlayer(areaPlayerName).sendMessage(ChatColor.YELLOW + "Caution! Your area " + ChatColor.GREEN + areaName + ChatColor.YELLOW + " has been attacked!");
                                playerAreaGetBreakBlockTime.put(Bukkit.getPlayer(areaPlayerName), now);

                            } else if (bossBarProgress < 0.75 && bossBarProgress > 0.5) {
                                Bukkit.getPlayer(areaPlayerName).sendMessage(ChatColor.YELLOW + "Caution! Your area " + ChatColor.GREEN + areaName + ChatColor.YELLOW + " has been attacked and has only" + ChatColor.RED + " 75% health" + ChatColor.YELLOW + " remaining");
                                playerAreaGetBreakBlockTime.put(Bukkit.getPlayer(areaPlayerName), now);

                            } else if (bossBarProgress < 0.5 && bossBarProgress > 0.25) {
                                Bukkit.getPlayer(areaPlayerName).sendMessage(ChatColor.YELLOW + "Caution! Your area " + ChatColor.GREEN + areaName + ChatColor.YELLOW + " has been attacked and has only" + ChatColor.RED + " 50% health" + ChatColor.YELLOW + " remaining");
                                playerAreaGetBreakBlockTime.put(Bukkit.getPlayer(areaPlayerName), now);

                            } else if (bossBarProgress < 0.25 && bossBarProgress > 0) {
                                Bukkit.getPlayer(areaPlayerName).sendMessage(ChatColor.YELLOW + "Caution! Your area " + ChatColor.GREEN + areaName + ChatColor.YELLOW + " has been attacked and has only" + ChatColor.RED + " 25% health" + ChatColor.YELLOW + " remaining");
                                playerAreaGetBreakBlockTime.put(Bukkit.getPlayer(areaPlayerName), now);

                            }
                        }
                    } catch (Exception error) {
                        playerAreaGetBreakBlockTime.clear();
                    }

                    //如果 hardnessValue 小于0，就会恢复相关block到原本block（防止破坏的是gold border block）
                    //然后玩家接收 Break!!! 的消息和破坏声并把bossbar移除
                } else if (hardnessValue < 0 || explodeTotalHardness > hardnessValue) {
//                RestoreRegionBlocks.restoreBLockBeforeBreak(PlayerSelectedAreaConfig, p, areaPlayerName, areaSecondKey);
                    String areaName = PlayerSelectedAreaConfig.getString(areaPlayerName + "." + areaSecondKey + ".areaInfo" + ".name");
                    if (Bukkit.getPlayer(areaPlayerName) != null) {
                        Bukkit.getPlayer(areaPlayerName).sendMessage(ChatColor.DARK_RED + "Warning: your area " + ChatColor.GREEN + areaName + ChatColor.DARK_RED + " has been break!!");
                    }
                    SiegeSafeBaseShield.areaUnderAttack = false;
                    whoActiveTNT.sendMessage(ChatColor.GOLD + "Successfully Breakthrough the shield!");
                    int count = config.getInt(areaPlayerName + " Count");
                    count--;
                    config.set(areaPlayerName + " Count", count);
                    ((Player) whoActiveTNT).playSound(whoActiveTNT.getLocation(), Sound.ENTITY_IRON_GOLEM_DAMAGE, 2.0f, 1.0f);
                    ((Player) whoActiveTNT).playSound(whoActiveTNT.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1f, 1.0f);
                    try {
                        OnBreakSelectedArea.baseHealthBar = new BaseHealthBar(plugin, (Player) whoActiveTNT);
                        OnBreakSelectedArea.baseHealthBar.hideBossBar();
                        OnBreakSelectedArea.playerIsBossBarCreatedMap.put(whoActiveTNT.getUniqueId(), false);

                    } catch (NullPointerException error) {
                        //一次性炸break， bossbar 会等于null 所以用exception
                    }

                    // 删除被选择的 Number-Of-Selected-Location 子节点
                    PlayerSelectedAreaConfig.set(areaPlayerName + "." + areaSecondKey, null);

                    //整理secondKey
                    for (String secondKeys : PlayerSelectedAreaConfig.getConfigurationSection(areaPlayerName).getKeys(false)) {
                        if (!secondKeys.equals("Number-Of-Selected-Location")) {
                            secondKeyCount++;
                            refreshFinalSecondKeyAndNumber = secondKeys;
                            RefreshPlayerSelectedAreaConfig.RefreshSelectionBreakConfig(areaPlayerName, PlayerSelectedAreaConfig, config, secondKeys, refreshFinalSecondKeyAndNumber);

                        } else if (secondKeys.equals("Number-Of-Selected-Location")) {

                        }
                    }
                    secondKeyCount = 0;

                }
            }

            // 保存配置文件
            try {
                config.save(file);
                PlayerSelectedAreaConfig.save(PlayerSelectedAreaFile);
            } catch (IOException error) {
                error.printStackTrace();
            }

            stringExplodeBlockLocation.clear();
            selectedBlockList.clear();
            allRecordBlock.clear();
            explodeTotalHardness = 0.0;
        }else {
            e.setCancelled(true);
        }
    }
}
