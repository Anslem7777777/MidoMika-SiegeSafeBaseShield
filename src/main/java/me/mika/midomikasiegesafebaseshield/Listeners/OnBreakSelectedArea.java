package me.mika.midomikasiegesafebaseshield.Listeners;

import me.mika.midomikasiegesafebaseshield.Utils.BaseHealthBar;
import me.mika.midomikasiegesafebaseshield.Utils.RefreshPlayerSelectedAreaConfig;
import me.mika.midomikasiegesafebaseshield.Utils.RestoreRegionBlocks;
import me.mika.midomikasiegesafebaseshield.Utils.SaveDataToConfig;
import me.mika.midomikasiegesafebaseshield.SiegeSafeBaseShield;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class OnBreakSelectedArea implements Listener {


    SiegeSafeBaseShield plugin;

    public OnBreakSelectedArea(SiegeSafeBaseShield plugin) {
        this.plugin = plugin;
    }

    private Double hardnessValue;
    private Double repairHardnessValue;
    private String blockBreakingList;
    private LocalDateTime breakBlockTime;
    private String mainKeyAndNumber;
    private Integer secondKeyCount = 0;
    private String secondKeyString = "Number-Of-Selected-Location";
    private String refreshFinalSecondKeyAndNumber;
    public static BossBar bossBar;
    private Boolean isBossBarCreated = false;
    public static HashMap<UUID, Boolean> playerIsBossBarCreatedMap = new HashMap<>();
    private static Set<String> selectedAreaOriBlocks;
    private String secondKeyName = "Number-Of-Selected-Location";
    private HashMap<String, String> allRecordBlock = new HashMap<>();
    public static HashMap<Player, LocalDateTime> playerBreakBlockTime = new HashMap<>();
    public static HashMap<Player, LocalDateTime> playerAreaGetBreakBlockTime = new HashMap<>();
    private Location selfFinalMinLocation;
    private Location selfFinalMaxLocation;
    public static BaseHealthBar baseHealthBar;


    @EventHandler
    public void onPlayerBreak(BlockBreakEvent e) {
        //TODO - Setup Variable
        Player p = e.getPlayer();
        boolean isBossBarCreated = playerIsBossBarCreatedMap.getOrDefault(p.getUniqueId(), false);
        playerIsBossBarCreatedMap.put(p.getUniqueId(), isBossBarCreated);
        String playerName = p.getDisplayName();
        Block interactedBlock = e.getBlock();
        Double breakBlockHardness = (double) interactedBlock.getType().getHardness();

        Location playerBreakBlockLocation = interactedBlock.getLocation();
        int playerBreakBlockLocationX = interactedBlock.getLocation().getBlockX();
        int playerBreakBlockLocationY = interactedBlock.getLocation().getBlockY();
        int playerBreakBlockLocationZ = interactedBlock.getLocation().getBlockZ();
        String playerBreakBlockType = interactedBlock.getType().toString();
        String stringPlayerBreakBlockLocation = playerBreakBlockLocation.getWorld().getName() + ";" + playerBreakBlockLocationX + ";" + playerBreakBlockLocationY + ";" + playerBreakBlockLocationZ;

        File file = new File(Bukkit.getServer().getPluginManager().getPlugin("SiegeSafeBaseShield").getDataFolder(), "config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        File PlayerSelectedAreaFile = new File(Bukkit.getServer().getPluginManager().getPlugin("SiegeSafeBaseShield").getDataFolder(), "PlayerSelectedAreaConfig.yml");
        FileConfiguration PlayerSelectedAreaConfig = YamlConfiguration.loadConfiguration(PlayerSelectedAreaFile);
        ConfigurationSection selectionMainKeySection = PlayerSelectedAreaConfig.getConfigurationSection(p.getName());

        Boolean selfConfigBlock = false;
        Boolean allConfigBlock = false;
        HashMap<String, String> selectedBlockList = new HashMap<>();
        String blockBreak = stringPlayerBreakBlockLocation;//"world;-42;59;40";

        // 获取当前时间
        breakBlockTime = LocalDateTime.now();
        // 定义日期时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // 格式化日期时间为字符串
        String formattedTime = breakBlockTime.format(formatter);
        //TODO - Setup Variable

//        String blockBreak = "world;-35;59;40";

        //TODO - 检查全部的selection 的 block有没有被选中
        int recordCount = 0;
        for (String mainKey : PlayerSelectedAreaConfig.getKeys(false)) {
            for (String secondKey : PlayerSelectedAreaConfig.getConfigurationSection(mainKey).getKeys(false)) {
                if (!secondKey.equals("Number-Of-Selected-Location")) {
                    recordCount++;
                    String mainKeyAndNumber = mainKey + ":" + recordCount;
                    selectedAreaOriBlocks = PlayerSelectedAreaConfig.getConfigurationSection(mainKey + "." + secondKey + ".areaInfo" + ".selectedAreaOriBlocks").getKeys(false);
                    Double selectedAreaOriBlocksHardness = PlayerSelectedAreaConfig.getDouble(mainKey + "." + secondKey + ".areaInfo" + ".totalBlockHardness");
                    Double selectedAreaOriRepairBlocksHardness = PlayerSelectedAreaConfig.getDouble(mainKey + "." + secondKey + ".areaInfo" + ".repairTotalBlockHardness");

                    allRecordBlock.put(mainKeyAndNumber, selectedAreaOriBlocks + "totalBlockHardness=" + selectedAreaOriBlocksHardness + "=repairTotalBlockHardness=" + selectedAreaOriRepairBlocksHardness);
                }
            }
            recordCount = 0;
        }
        selectedAreaOriBlocks.clear();


        //检查是不是在config里的数据，用于排除不是normal block
        if (String.valueOf(allRecordBlock.values().toString().contains(stringPlayerBreakBlockLocation)).equals("true")) {
            allConfigBlock = true;

        } else {

        }

        //TODO - 检查全部的selection 的 block有没有被选中


        //TODO - 检查自己的selection 的 block有没有被选中
        int secondKeysCount2 = selectionMainKeySection.getKeys(false).size();

        for (int i = 1; i < secondKeysCount2; i++) {
            String mainKeyAndNumber = p.getName() + ":" + i;

            //检查是不是config里跟玩家名字相符的数据，用于从allConfigBlock中分离
            if (allRecordBlock.get(mainKeyAndNumber) != null) {
                if (String.valueOf(allRecordBlock.get(mainKeyAndNumber).contains(stringPlayerBreakBlockLocation)).equals("true")) {
                    selfConfigBlock = true;
                    break;

                } else {

                }
            }

        }

        allRecordBlock.clear();
        //TODO - 检查自己的selection 的 block有没有被选中


        //TODO - 检查是自己的block还是其他player还是normal block

        if (allConfigBlock == true && selfConfigBlock == true) {
            //自己的block
            if (!SiegeSafeBaseShield.areaUnderAttack) {
                for (String selfMainKey : PlayerSelectedAreaConfig.getKeys(false)) {
                    if (selfMainKey.equals(p.getName())) {
                        for (String selfSecondKey : PlayerSelectedAreaConfig.getConfigurationSection(selfMainKey).getKeys(false)) {
                            if (!selfSecondKey.equals("Number-Of-Selected-Location")) {
                                Set<String> selfSelectedAreaOriBlocks = PlayerSelectedAreaConfig.getConfigurationSection(selfMainKey).getConfigurationSection(selfSecondKey).getConfigurationSection("areaInfo").getConfigurationSection("selectedAreaOriBlocks").getKeys(false);
                                Set<String> selfBlocksReplaceBySelectedBlocks = PlayerSelectedAreaConfig.getConfigurationSection(selfMainKey).getConfigurationSection(selfSecondKey).getConfigurationSection("areaInfo").getConfigurationSection("blocksReplaceBySelectedBlocks").getKeys(false);

                                String selfMinLocation = PlayerSelectedAreaConfig.getString(selfMainKey + "." + selfSecondKey + ".areaInfo.playerSelections.minLocation");
                                String selfMaxLocation = PlayerSelectedAreaConfig.getString(selfMainKey + "." + selfSecondKey + ".areaInfo.playerSelections.maxLocation");

                                String[] splitselfMinLocation = selfMinLocation.split(";");
                                World selfWorldLocation = Bukkit.getWorld(splitselfMinLocation[0]);
                                Double selfMinLocationX = Double.valueOf(splitselfMinLocation[1]);
                                Double selfMinLocationY = Double.valueOf(splitselfMinLocation[2]);
                                Double selfMinLocationZ = Double.valueOf(splitselfMinLocation[3]);
                                String[] splitselfMaxLocation = selfMaxLocation.split(";");
                                Double selfMaxLocationX = Double.valueOf(splitselfMaxLocation[1]);
                                Double selfMaxLocationY = Double.valueOf(splitselfMaxLocation[2]);
                                Double selfMaxLocationZ = Double.valueOf(splitselfMaxLocation[3]);

                                selfFinalMinLocation = new Location(
                                        selfWorldLocation,
                                        Math.min(selfMinLocationX, selfMaxLocationX),
                                        Math.min(selfMinLocationY, selfMaxLocationY),
                                        Math.min(selfMinLocationZ, selfMaxLocationZ)
                                );
                                selfFinalMaxLocation = new Location(
                                        selfWorldLocation,
                                        Math.max(selfMinLocationX, selfMaxLocationX),
                                        Math.max(selfMinLocationY, selfMaxLocationY),
                                        Math.max(selfMinLocationZ, selfMaxLocationZ)
                                );

                                if (selfSelectedAreaOriBlocks.contains(stringPlayerBreakBlockLocation) && selfBlocksReplaceBySelectedBlocks.contains(stringPlayerBreakBlockLocation)) {
                                    PlayerSelectedAreaConfig.set(selfMainKey + "." + selfSecondKey + ".areaInfo" + ".selectedAreaOriBlocks." + stringPlayerBreakBlockLocation, "AIR");
                                    PlayerSelectedAreaConfig.set(selfMainKey + "." + selfSecondKey + ".areaInfo" + ".blocksReplaceBySelectedBlocks." + stringPlayerBreakBlockLocation, "AIR");

                                    //AsyncTask 可以让异步任务在后台运行
                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            SaveDataToConfig.refreshHardness(selfFinalMinLocation, selfFinalMaxLocation, selfMainKey, selfSecondKey);
                                        }
                                    }.runTaskAsynchronously(plugin);
                                    break;

                                } else if (selfSelectedAreaOriBlocks.contains(stringPlayerBreakBlockLocation)) {
                                    PlayerSelectedAreaConfig.set(selfMainKey + "." + selfSecondKey + ".areaInfo" + ".selectedAreaOriBlocks." + stringPlayerBreakBlockLocation, "AIR");

                                    //AsyncTask 可以让异步任务在后台运行
                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            SaveDataToConfig.refreshHardness(selfFinalMinLocation, selfFinalMaxLocation, selfMainKey, selfSecondKey);
                                        }
                                    }.runTaskAsynchronously(plugin);
                                    break;

                                } else if (selfBlocksReplaceBySelectedBlocks.contains(stringPlayerBreakBlockLocation)) {
                                    PlayerSelectedAreaConfig.set(selfMainKey + "." + selfSecondKey + ".areaInfo" + ".blocksReplaceBySelectedBlocks." + stringPlayerBreakBlockLocation, "AIR");

                                    //AsyncTask 可以让异步任务在后台运行
                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            SaveDataToConfig.refreshHardness(selfFinalMinLocation, selfFinalMaxLocation, selfMainKey, selfSecondKey);
                                        }
                                    }.runTaskAsynchronously(plugin);
                                    break;

                                }
                            }
                        }
                    }
                }
                // 保存配置文件
                try {
                    PlayerSelectedAreaConfig.save(PlayerSelectedAreaFile);
                } catch (IOException error) {
                    error.printStackTrace();
                }
            } else {
                p.sendMessage(ChatColor.RED + "Your area is currently under attack. You cannot edit any blocks.");
                e.setCancelled(true);
            }


        } else if (allConfigBlock == true && selfConfigBlock == false) {
            //其他player的block
            e.setCancelled(true);
            SiegeSafeBaseShield.areaUnderAttack = true;
            p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.1f, 1.0f);

            for (String mainKey : PlayerSelectedAreaConfig.getKeys(false)) {
                if (!mainKey.equals(p.getName())) {
                    for (String secondKey : PlayerSelectedAreaConfig.getConfigurationSection(mainKey).getKeys(false)) {
                        if (!secondKey.equals("Number-Of-Selected-Location")) {
                            recordCount++;
                            String mainKeyAndNumber = mainKey + ":" + recordCount;
                            try {
                                selectedAreaOriBlocks = PlayerSelectedAreaConfig.getConfigurationSection(mainKey + "." + secondKey + ".areaInfo" + ".selectedAreaOriBlocks").getKeys(false);
                            }catch (Exception error){

                            }
                            Double selectedAreaOriBlocksHardness = PlayerSelectedAreaConfig.getDouble(mainKey + "." + secondKey + ".areaInfo" + ".totalBlockHardness");

                            allRecordBlock.put(mainKeyAndNumber, mainKey + " - " + secondKey + " - " + selectedAreaOriBlocks.toString() + "BlockHardness=" + selectedAreaOriBlocksHardness);
                            selectedAreaOriBlocks.clear();

                            if (allRecordBlock.get(mainKeyAndNumber).contains(blockBreak)) {
                                selectedBlockList.put(mainKey, allRecordBlock.get(mainKeyAndNumber));

                                //split hardness and save to "hardnessValue"
                                String blockBreakingList2 = selectedBlockList.get(mainKey);
                                // 步骤1：将字符串分割成两部分
                                String[] splitValueParts = blockBreakingList2.split(",");
                                String[] splitHardnessParts = blockBreakingList2.split("=");

                                hardnessValue = Double.parseDouble(splitHardnessParts[1]);

                                PlayerSelectedAreaConfig.set(mainKey + "." + secondKey + ".areaInfo" + ".breakBlockTime", breakBlockTime.toString());

                                if (hardnessValue >= 0 && !(breakBlockHardness > hardnessValue)) {
                                    LocalDateTime now = LocalDateTime.now();
                                    repairHardnessValue = PlayerSelectedAreaConfig.getDouble(mainKey + "." + secondKey + ".areaInfo" + ".repairTotalBlockHardness");
                                    PlayerSelectedAreaConfig.set(mainKey + "." + secondKey + ".areaInfo" + ".totalBlockHardness", hardnessValue - breakBlockHardness);
                                    hardnessValue = PlayerSelectedAreaConfig.getDouble(mainKey + "." + secondKey + ".areaInfo" + ".totalBlockHardness");
                                    String areaName = PlayerSelectedAreaConfig.getString(mainKey + "." + secondKey + ".areaInfo" + ".name");

                                    double bossBarProgress = hardnessValue / repairHardnessValue;

                                    if (!playerIsBossBarCreatedMap.get(p.getUniqueId())) {
                                        baseHealthBar = new BaseHealthBar(plugin, p);
                                        baseHealthBar.showBossBar(mainKey);
                                        playerIsBossBarCreatedMap.put(p.getUniqueId(), true);

                                    }
                                    baseHealthBar = new BaseHealthBar(plugin, p);
                                    baseHealthBar.updateBossBars(hardnessValue, repairHardnessValue);

                                    try {
                                        if (playerAreaGetBreakBlockTime.toString().equals("{}")) {
                                            playerAreaGetBreakBlockTime.put(Bukkit.getPlayer(mainKey), now);
                                            Bukkit.getPlayer(mainKey).sendMessage(ChatColor.YELLOW + "Caution! Your area " + ChatColor.GREEN + areaName + ChatColor.YELLOW + " has been attacked!");

                                        }
                                        Duration duration = Duration.between(playerAreaGetBreakBlockTime.get(Bukkit.getPlayer(mainKey)), now);
                                        long finalTime = duration.toSeconds();

                                        if (finalTime >= 5) {
                                            if (bossBarProgress < 1 && bossBarProgress > 0.75) {
                                                Bukkit.getPlayer(mainKey).sendMessage(ChatColor.YELLOW + "Caution! Your area " + ChatColor.GREEN + areaName + ChatColor.YELLOW + " has been attacked!");
                                                playerAreaGetBreakBlockTime.put(Bukkit.getPlayer(mainKey), now);

                                            } else if (bossBarProgress < 0.75 && bossBarProgress > 0.5) {
                                                Bukkit.getPlayer(mainKey).sendMessage(ChatColor.YELLOW + "Caution! Your area " + ChatColor.GREEN + areaName + ChatColor.YELLOW + " has been attacked and has only" + ChatColor.RED + " 75% health" + ChatColor.YELLOW + " remaining");
                                                playerAreaGetBreakBlockTime.put(Bukkit.getPlayer(mainKey), now);

                                            } else if (bossBarProgress < 0.5 && bossBarProgress > 0.25) {
                                                Bukkit.getPlayer(mainKey).sendMessage(ChatColor.YELLOW + "Caution! Your area " + ChatColor.GREEN + areaName + ChatColor.YELLOW + " has been attacked and has only" + ChatColor.RED + " 50% health" + ChatColor.YELLOW + " remaining");
                                                playerAreaGetBreakBlockTime.put(Bukkit.getPlayer(mainKey), now);

                                            } else if (bossBarProgress < 0.25 && bossBarProgress > 0) {
                                                Bukkit.getPlayer(mainKey).sendMessage(ChatColor.YELLOW + "Caution! Your area " + ChatColor.GREEN + areaName + ChatColor.YELLOW + " has been attacked and has only" + ChatColor.RED + " 25% health" + ChatColor.YELLOW + " remaining");
                                                playerAreaGetBreakBlockTime.put(Bukkit.getPlayer(mainKey), now);

                                            }
                                        }
                                    } catch (Exception error) {
                                        playerAreaGetBreakBlockTime.clear();
                                    }
                                } else if (hardnessValue < 0 || breakBlockHardness > hardnessValue) {
//                                    RestoreRegionBlocks.restoreBLockBeforeBreak(PlayerSelectedAreaConfig, p, mainKey, secondKeyAndNumber);
                                    String areaName = PlayerSelectedAreaConfig.getString(mainKey + "." + secondKey + ".areaInfo" + ".name");
                                    SiegeSafeBaseShield.areaUnderAttack = false;
                                    p.sendMessage(ChatColor.GOLD + "Successfully Breakthrough the shield!");
                                    int count = config.getInt(mainKey + " Count");
                                    count--;
                                    config.set(mainKey + " Count", count);
                                    p.playSound(p.getLocation(), Sound.ENTITY_IRON_GOLEM_DAMAGE, 2.0f, 1.0f);

                                    baseHealthBar = new BaseHealthBar(plugin, p);
                                    baseHealthBar.hideBossBar();

                                    playerIsBossBarCreatedMap.put(p.getUniqueId(), false);

                                    // 删除被选择的 Number-Of-Selected-Location 子节点
                                    PlayerSelectedAreaConfig.set(mainKey + "." + secondKey, null);

                                    try {
                                        selectedAreaOriBlocks = PlayerSelectedAreaConfig.getConfigurationSection(mainKey + "." + secondKey + ".areaInfo" + ".selectedAreaOriBlocks").getKeys(false);
                                    }catch (Exception error){

                                    }


                                    //整理secondKey
                                    for (String secondKeys : PlayerSelectedAreaConfig.getConfigurationSection(mainKey).getKeys(false)) {
                                        if (!secondKeys.equals("Number-Of-Selected-Location")) {
                                            secondKeyCount++;
                                            refreshFinalSecondKeyAndNumber = secondKeyString + secondKeyCount;
                                            RefreshPlayerSelectedAreaConfig.RefreshSelectionBreakConfig(mainKey, PlayerSelectedAreaConfig, config, secondKeys, refreshFinalSecondKeyAndNumber);

                                        }
                                    }
                                    secondKeyCount = 0;
                                    if (Bukkit.getPlayer(mainKey) != null) {
                                        Bukkit.getPlayer(mainKey).sendMessage(ChatColor.DARK_RED + "Warning: your area " + ChatColor.GREEN + areaName + ChatColor.DARK_RED + " has been break!!");
                                    }
                                }

                                // 保存配置文件
                                try {
                                    config.save(file);
                                    PlayerSelectedAreaConfig.save(PlayerSelectedAreaFile);
                                } catch (IOException error) {
                                    error.printStackTrace();
                                }
                            }
                        }
                    }
                    selectedAreaOriBlocks.clear();
                }
            }

            LocalDateTime currentTime = LocalDateTime.now();
            playerBreakBlockTime.put(p, currentTime);

            allRecordBlock.clear();
            selectedBlockList.clear();
            recordCount = 0;


//            PlayerSelectedAreaConfig.getKeys(false).stream().forEach(mainKey -> {
//
//                ConfigurationSection selectionAllMainKeySection = PlayerSelectedAreaConfig.getConfigurationSection(mainKey);
//                int secondKeysCount3 = selectionAllMainKeySection.getKeys(false).size();
//
//                for (int i = 1; i < secondKeysCount3; i++) {
//                    secondKeyAndNumber = secondKeyName + i;
//                    mainKeyAndNumber = mainKey + ":" + i;
//                    Set<String> selectedAreaOriBlocks = selectionAllMainKeySection.getConfigurationSection(secondKeyAndNumber).getConfigurationSection("areaInfo").getConfigurationSection("selectedAreaOriBlocks").getKeys(false);
//                    Double selectedAreaOriBlocksHardness = selectionAllMainKeySection.getDouble(secondKeyAndNumber + ".areaInfo" + ".totalBlockHardness");
//
//                    allRecordBlock.put(mainKeyAndNumber, mainKey + " - " + secondKeyAndNumber + " - " + selectedAreaOriBlocks.toString() + "BlockHardness=" + selectedAreaOriBlocksHardness);
//
//                    if (allRecordBlock.get(mainKeyAndNumber).contains(blockBreak)) {
//                        selectedBlockList.put(mainKey, allRecordBlock.get(mainKeyAndNumber));
//
//                        //split hardness and save to "hardnessValue"
//                        if (selectedBlockList.get(mainKey).contains(blockBreak)) {
//                            String blockBreakingList2 = selectedBlockList.get(mainKey);
//                            // 步骤1：将字符串分割成两部分
//                            String[] splitValueParts = blockBreakingList2.split(",");
//                            String[] splitHardnessParts = blockBreakingList2.split("=");
//                            String blockInfo = splitHardnessParts[0];
//
//                            hardnessValue = Double.parseDouble(splitHardnessParts[1]);
//
//                        }
//
//                        PlayerSelectedAreaConfig.set(mainKey + "." + secondKeyAndNumber + ".areaInfo" + ".breakBlockTime", breakBlockTime.toString());
//
//                        if (hardnessValue >= 0 && !(breakBlockHardness > hardnessValue)) {
//                            LocalDateTime now = LocalDateTime.now();
//                            PlayerSelectedAreaConfig.set(mainKey + "." + secondKeyAndNumber + ".areaInfo" + ".totalBlockHardness", hardnessValue - breakBlockHardness);
//                            hardnessValue = (Double) PlayerSelectedAreaConfig.getDouble(mainKey + "." + secondKeyAndNumber + ".areaInfo" + ".totalBlockHardness");
//                            String areaName = PlayerSelectedAreaConfig.getString(mainKey + "." + secondKeyAndNumber + ".areaInfo" + ".name");
//
//                            double bossBarProgress = hardnessValue / repairHardnessValue;
//
//                            try {
//
//                                if (playerAreaGetBreakBlockTime.toString().equals("{}")) {
//                                    playerAreaGetBreakBlockTime.put(Bukkit.getPlayer(mainKey), now);
//                                    Bukkit.getPlayer(mainKey).sendMessage(ChatColor.YELLOW + "Caution! Your area " + ChatColor.GREEN + areaName + ChatColor.YELLOW + " has been attacked!");
//
//                                }
//                                Duration duration = Duration.between(playerAreaGetBreakBlockTime.get(Bukkit.getPlayer(mainKey)), now);
//                                long finalTime = duration.toSeconds();
//
//                                if (finalTime >= 5) {
//                                    if (bossBarProgress < 1 && bossBarProgress > 0.75) {
//                                        Bukkit.getPlayer(mainKey).sendMessage(ChatColor.YELLOW + "Caution! Your area " + ChatColor.GREEN + areaName + ChatColor.YELLOW + " has been attacked!");
//                                        playerAreaGetBreakBlockTime.put(Bukkit.getPlayer(mainKey), now);
//
//                                    } else if (bossBarProgress < 0.75 && bossBarProgress > 0.5) {
//                                        Bukkit.getPlayer(mainKey).sendMessage(ChatColor.YELLOW + "Caution! Your area " + ChatColor.GREEN + areaName + ChatColor.YELLOW + " has been attacked and has only" + ChatColor.RED + " 75% health" + ChatColor.YELLOW + " remaining");
//                                        playerAreaGetBreakBlockTime.put(Bukkit.getPlayer(mainKey), now);
//
//                                    } else if (bossBarProgress < 0.5 && bossBarProgress > 0.25) {
//                                        Bukkit.getPlayer(mainKey).sendMessage(ChatColor.YELLOW + "Caution! Your area " + ChatColor.GREEN + areaName + ChatColor.YELLOW + " has been attacked and has only" + ChatColor.RED + " 50% health" + ChatColor.YELLOW + " remaining");
//                                        playerAreaGetBreakBlockTime.put(Bukkit.getPlayer(mainKey), now);
//
//                                    } else if (bossBarProgress < 0.25 && bossBarProgress > 0) {
//                                        Bukkit.getPlayer(mainKey).sendMessage(ChatColor.YELLOW + "Caution! Your area " + ChatColor.GREEN + areaName + ChatColor.YELLOW + " has been attacked and has only" + ChatColor.RED + " 25% health" + ChatColor.YELLOW + " remaining");
//                                        playerAreaGetBreakBlockTime.put(Bukkit.getPlayer(mainKey), now);
//
//                                    }
//                                }
//                            } catch (Exception error) {
//                                playerAreaGetBreakBlockTime.clear();
//                            }
//
//                        } else if (hardnessValue < 0 || breakBlockHardness > hardnessValue) {
//                            RestoreRegionBlocks.restoreBLockBeforeBreak(PlayerSelectedAreaConfig, p, mainKey, secondKeyAndNumber);
//                            String areaName = PlayerSelectedAreaConfig.getString(mainKey + "." + secondKeyAndNumber + ".areaInfo" + ".name");
//                            SiegeSafeBaseShield.areaUnderAttack = false;
//                            p.sendMessage(ChatColor.GOLD + "Successfully Breakthrough the shield!");
//                            int count = config.getInt(mainKey + " Count");
//                            count--;
//                            config.set(mainKey + " Count", count);
//                            p.playSound(p.getLocation(), Sound.ENTITY_IRON_GOLEM_DAMAGE, 2.0f, 1.0f);
//                            bossBar.removeAll();
//                            isBossBarCreated = false;
//                            allRecordBlock.clear();
//                            selectedBlockList.clear();
//
//                            // 删除被选择的 Number-Of-Selected-Location 子节点
//                            PlayerSelectedAreaConfig.set(mainKey + "." + secondKeyAndNumber, null);
//
//                            //整理secondKey
//                            for (String secondKeys : PlayerSelectedAreaConfig.getConfigurationSection(mainKey).getKeys(false)) {
//                                if (PlayerSelectedAreaConfig.getConfigurationSection(mainKey).getKeys(false).size() < 2){
//
//                                }else {
//                                    if (!secondKeys.equals("Number-Of-Selected-Location")) {
//                                        secondKeyCount++;
//                                        refreshFinalSecondKeyAndNumber = secondKey + secondKeyCount;
//                                        RefreshPlayerSelectedAreaConfig.RefreshSelectionBreakConfig(mainKey, PlayerSelectedAreaConfig, config, secondKeys, refreshFinalSecondKeyAndNumber);
//
//                                    }
//                                }
//                            }
//                            secondKeyCount = 0;
//                            if (Bukkit.getPlayer(mainKey) != null) {
//                                Bukkit.getPlayer(mainKey).sendMessage(ChatColor.DARK_RED + "Warning: your area " + ChatColor.GREEN + areaName + ChatColor.DARK_RED + " has been break!!");
//                            }
//                        }
//
//                        // 保存配置文件
//                        try {
//                            config.save(file);
//                            PlayerSelectedAreaConfig.save(PlayerSelectedAreaFile);
//                        } catch (IOException error) {
//                            error.printStackTrace();
//                        }
//
//                    } else if (!allRecordBlock.get(mainKeyAndNumber).contains(blockBreak)) {
//
//                    }
//
//                }
//            });
//
//
//            LocalDateTime currentTime = LocalDateTime.now();
//            playerBreakBlockTime.put(p, currentTime);
//
//            for(String areaOwner : selectedBlockList.keySet()) {
//                if (!isBossBarCreated) {
//                    bossBar = Bukkit.createBossBar(ChatColor.RED + "Area Owner: " + ChatColor.GOLD + areaOwner, BarColor.RED, BarStyle.SOLID);
//                    bossBar.setVisible(true);
//                    bossBar.addPlayer(p);
//                    isBossBarCreated = true;
//
//                }
//            }
//            double bossBarProgress = hardnessValue / repairHardnessValue;
//            bossBar.setProgress(bossBarProgress);
//
//        }else if (allConfigBlock == false && selfConfigBlock == false){
//            //normal block
//
//        }
//        //TODO - 检查是自己的block还是其他player还是normal block
//
//        selectedBlockList.clear();
//        allRecordBlock.clear();

        }

        //TODO - 好像空手interact block会触发一些null exception 要检查，TNT炸的时候离开两格会有几率炸爆area block

    }
}
