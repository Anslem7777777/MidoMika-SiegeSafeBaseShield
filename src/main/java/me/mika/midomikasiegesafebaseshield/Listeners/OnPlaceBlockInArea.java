package me.mika.midomikasiegesafebaseshield.Listeners;

import me.mika.midomikasiegesafebaseshield.Utils.SaveDataToConfig;
import me.mika.midomikasiegesafebaseshield.SiegeSafeBaseShield;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.sql.SQLOutput;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class OnPlaceBlockInArea implements Listener {
    SiegeSafeBaseShield plugin;
    public OnPlaceBlockInArea(SiegeSafeBaseShield plugin) {
        this.plugin = plugin;
    }
    public static int mainKeyCount = 0;
    private Set<String> selectedAreaOriBlocks;
    private String mainKeyAndNumber;
    private HashMap<String, String> allRecordBlock = new HashMap<>();
    private Location selfFinalMinLocation;
    private Location selfFinalMaxLocation;
    public Player whoPlaceTheBlock;
    private Player whoEmptyBucket;
    private Boolean whoEmptyBucketSelfConfigBlock = false;
    private Boolean whoEmptyBucketAllConfigBlock = false;
    private Set<String> whoEmptyBucketAllSelectedAreaOriBlocks = new HashSet<>();
    private Set<String> whoEmptyBucketSelfSelectedAreaOriBlocks = new HashSet<>();
    private String igniteBlockWorldString;

    @EventHandler
    public void onPlaceBlockInArea(BlockPlaceEvent e){
        whoPlaceTheBlock = e.getPlayer();
        File file = new File(Bukkit.getServer().getPluginManager().getPlugin("SiegeSafeBaseShield").getDataFolder(), "config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        File PlayerSelectedAreaFile = new File(Bukkit.getServer().getPluginManager().getPlugin("SiegeSafeBaseShield").getDataFolder(), "PlayerSelectedAreaConfig.yml");
        FileConfiguration PlayerSelectedAreaConfig = YamlConfiguration.loadConfiguration(PlayerSelectedAreaFile);
        ConfigurationSection selectionMainKeySection = PlayerSelectedAreaConfig.getConfigurationSection(whoPlaceTheBlock.getName());
        Boolean selfConfigBlock = false;
        Boolean allConfigBlock = false;
        Block blockPlace = e.getBlock();
        Location blockPlaceLocation = blockPlace.getLocation();
        int blockPlacelockX = blockPlace.getLocation().getBlockX();
        int blockPlacelockY = blockPlace.getLocation().getBlockY();
        int blockPlacelockZ = blockPlace.getLocation().getBlockZ();
        String blockPlacelockType = blockPlace.getType().toString();
        String stringBlockPlaceLocation = blockPlaceLocation.getWorld().getName() + ";" + blockPlacelockX + ";" + blockPlacelockY + ";" + blockPlacelockZ;


        for (String mainKey : PlayerSelectedAreaConfig.getKeys(false)) {
            for (String secondKey : PlayerSelectedAreaConfig.getConfigurationSection(mainKey).getKeys(false)) {
                if (!secondKey.equals("Number-Of-Selected-Location")) {
                    mainKeyCount++;
                    try {
                        selectedAreaOriBlocks = PlayerSelectedAreaConfig.getConfigurationSection(mainKey).getConfigurationSection(secondKey).getConfigurationSection("areaInfo").getConfigurationSection("selectedAreaOriBlocks").getKeys(false);
                    } catch (Exception error) {

                    }

                    //mainkey 玩家名字加上forloop次数准备放入hashmap
                    mainKeyAndNumber = mainKey + ":" + mainKeyCount;
                    //获取玩家area所有数据里的totalBlockHardness
                    int selectedAreaOriBlocksHardness = PlayerSelectedAreaConfig.getInt(mainKey + "." + secondKey + ".areaInfo" + ".totalBlockHardness");
                    int selectedAreaOriRepairBlocksHardness = PlayerSelectedAreaConfig.getInt(mainKey + "." + secondKey + ".areaInfo" + ".repairTotalBlockHardness");

                    //列子：Ansalem2608:1=Ansalem2608 - Number-Of-Selected-Location3 - [world;-39;59;39, world;-39;59;40, world;-38;59;39, world;-38;59;40]BlockHardness=70=repairTotalBlockHardness=70
                    allRecordBlock.put(mainKeyAndNumber, mainKey + " - " + secondKey + " - " + selectedAreaOriBlocks + "BlockHardness=" + selectedAreaOriBlocksHardness + "=repairTotalBlockHardness=" + selectedAreaOriRepairBlocksHardness);

                }
            }
            mainKeyCount = 0;
        }

        if (String.valueOf(allRecordBlock.values().toString().contains(stringBlockPlaceLocation)).equals("true")) {
            allConfigBlock = true;

        } else {
        }

        int secondKeysCount = selectionMainKeySection.getKeys(false).size();
        for (int i = 1; i < secondKeysCount; i++) {
            String mainKeyAndNumber = whoPlaceTheBlock.getName() + ":" + i;

            //检查是不是config里跟玩家名字相符的数据，用于从allConfigBlock中分离
            if (String.valueOf(allRecordBlock.get(mainKeyAndNumber).contains(stringBlockPlaceLocation)).equals("true")){
                selfConfigBlock = true;
                break;

            }else {
            }
        }

        if (allConfigBlock == true && selfConfigBlock != true){
            e.setCancelled(true);
            whoPlaceTheBlock.sendMessage(ChatColor.RED + "Block cannot be placed in other player’s area");

        }else if (allConfigBlock == true && selfConfigBlock == true){
            //自己的block
            e.setCancelled(false);
            if (!SiegeSafeBaseShield.areaUnderAttack) {
                for (String selfMainKey : PlayerSelectedAreaConfig.getKeys(false)) {
                    if (selfMainKey.equals(whoPlaceTheBlock.getName())) {
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

                                if (selfSelectedAreaOriBlocks.contains(stringBlockPlaceLocation) && selfBlocksReplaceBySelectedBlocks.contains(stringBlockPlaceLocation)) {
                                    PlayerSelectedAreaConfig.set(selfMainKey + "." + selfSecondKey + ".areaInfo" + ".selectedAreaOriBlocks." + stringBlockPlaceLocation, blockPlacelockType);
                                    PlayerSelectedAreaConfig.set(selfMainKey + "." + selfSecondKey + ".areaInfo" + ".blocksReplaceBySelectedBlocks." + stringBlockPlaceLocation, blockPlacelockType);

                                    //AsyncTask 可以让异步任务在后台运行
                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            SaveDataToConfig.refreshHardness(selfFinalMinLocation, selfFinalMaxLocation, selfMainKey, selfSecondKey);
                                        }
                                    }.runTaskAsynchronously(plugin);
                                    break;

                                } else if (selfSelectedAreaOriBlocks.contains(stringBlockPlaceLocation)) {
                                    PlayerSelectedAreaConfig.set(selfMainKey + "." + selfSecondKey + ".areaInfo" + ".selectedAreaOriBlocks." + stringBlockPlaceLocation, blockPlacelockType);

                                    //AsyncTask 可以让异步任务在后台运行
                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            SaveDataToConfig.refreshHardness(selfFinalMinLocation, selfFinalMaxLocation, selfMainKey, selfSecondKey);
                                        }
                                    }.runTaskAsynchronously(plugin);
                                    break;

                                } else if (selfBlocksReplaceBySelectedBlocks.contains(stringBlockPlaceLocation)) {
                                    PlayerSelectedAreaConfig.set(selfMainKey + "." + selfSecondKey + ".areaInfo" + ".blocksReplaceBySelectedBlocks." + stringBlockPlaceLocation, blockPlacelockType);

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
            }else {
                whoPlaceTheBlock.sendMessage(ChatColor.RED + "Your area is currently under attack. You cannot edit any blocks.");
                e.setCancelled(true);
            }
        }
        allRecordBlock.clear();

    }

    @EventHandler
    public void onInteractBlockInArea(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        File file = new File(Bukkit.getServer().getPluginManager().getPlugin("SiegeSafeBaseShield").getDataFolder(), "config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        File PlayerSelectedAreaFile = new File(Bukkit.getServer().getPluginManager().getPlugin("SiegeSafeBaseShield").getDataFolder(), "PlayerSelectedAreaConfig.yml");
        FileConfiguration PlayerSelectedAreaConfig = YamlConfiguration.loadConfiguration(PlayerSelectedAreaFile);
        ConfigurationSection selectionMainKeySection = PlayerSelectedAreaConfig.getConfigurationSection(p.getName());
        Boolean selfConfigBlock = false;
        Boolean allConfigBlock = false;

        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block interactBlock = e.getClickedBlock();
            String interactBlockType = interactBlock.getType().toString();
            String interactBlockWorld = interactBlock.getWorld().getName();
            int interactBlockLocationX = interactBlock.getX();
            int interactBlockLocationY = interactBlock.getY();
            int interactBlockLocationZ = interactBlock.getZ();
            String interactBlockLocationString = interactBlockWorld + ";" + interactBlockLocationX + ";" + interactBlockLocationY + ";" + interactBlockLocationZ;

            if (interactBlockType.contains("DOOR") || interactBlockType.contains("CHEST") || interactBlockType.contains("BUTTON") || interactBlockType.contains("LEVER") || interactBlockType.contains("BARREL") || interactBlockType.contains("HOPPER")) {
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
                            int selectedAreaOriBlocksHardness = PlayerSelectedAreaConfig.getInt(mainKey + "." + secondKey + ".areaInfo" + ".totalBlockHardness");
                            int selectedAreaOriRepairBlocksHardness = PlayerSelectedAreaConfig.getInt(mainKey + "." + secondKey + ".areaInfo" + ".repairTotalBlockHardness");

                            //列子：Ansalem2608:1=Ansalem2608 - Number-Of-Selected-Location3 - [world;-39;59;39, world;-39;59;40, world;-38;59;39, world;-38;59;40]BlockHardness=70=repairTotalBlockHardness=70
                            allRecordBlock.put(mainKeyAndNumber, mainKey + " - " + secondKey + " - " + selectedAreaOriBlocks + "BlockHardness=" + selectedAreaOriBlocksHardness + "=repairTotalBlockHardness=" + selectedAreaOriRepairBlocksHardness);

                        }
                    }
                    mainKeyCount = 0;
                }

                if (String.valueOf(allRecordBlock.values().toString().contains(interactBlockLocationString)).equals("true")) {
                    allConfigBlock = true;

                } else {
                }

                int secondKeysCount = selectionMainKeySection.getKeys(false).size();
                for (int i = 1; i < secondKeysCount; i++) {
                    String mainKeyAndNumber = p.getName() + ":" + i;

                    //检查是不是config里跟玩家名字相符的数据，用于从allConfigBlock中分离
                    if (String.valueOf(allRecordBlock.get(mainKeyAndNumber).contains(interactBlockLocationString)).equals("true")) {
                        selfConfigBlock = true;
                        break;

                    } else {
                    }
                }

                if (allConfigBlock == true && selfConfigBlock == true) {
                    //自己的area
                    e.setCancelled(false);

                } else if (allConfigBlock == true && selfConfigBlock != true) {
                    //别人的area
                    e.setCancelled(true);
                    p.sendMessage(ChatColor.RED + "This area belongs to another player. You cannot use this item.");


                }
            }
        }else {

        }
    }

//    @EventHandler
//    public void OnPlayerSetFire (BlockIgniteEvent e){
//        Block igniteBlock = e.getIgnitingBlock();
//        Block setFireBlock = e.getBlock();
//        Player p = e.getPlayer();
//
//        if (setFireBlock != null) {
//            File file = new File(Bukkit.getServer().getPluginManager().getPlugin("SiegeSafeBaseShield").getDataFolder(), "config.yml");
//            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
//            File PlayerSelectedAreaFile = new File(Bukkit.getServer().getPluginManager().getPlugin("SiegeSafeBaseShield").getDataFolder(), "PlayerSelectedAreaConfig.yml");
//            FileConfiguration PlayerSelectedAreaConfig = YamlConfiguration.loadConfiguration(PlayerSelectedAreaFile);
//            String setFireBlockWorld = setFireBlock.getWorld().getName();
//            int setFireBlockLocationX = setFireBlock.getLocation().getBlockX();
//            int setFireBlockLocationY = setFireBlock.getLocation().getBlockY();
//            int setFireBlockLocationZ = setFireBlock.getLocation().getBlockZ();
//            String setFireBlockLocationString = setFireBlockWorld + ";" + setFireBlockLocationX + ";" + setFireBlockLocationY + ";" + setFireBlockLocationZ;
//
//            for (String mainKey : PlayerSelectedAreaConfig.getKeys(false)) {
//                if (p != null) {
//                    if (!mainKey.equalsIgnoreCase(p.getName())) {
//                        for (String secondKey : PlayerSelectedAreaConfig.getConfigurationSection(mainKey).getKeys(false)) {
//                            if (!secondKey.equals("Number-Of-Selected-Location")) {
//                                mainKeyCount++;
//                                try {
//                                    selectedAreaOriBlocks = PlayerSelectedAreaConfig.getConfigurationSection(mainKey).getConfigurationSection(secondKey).getConfigurationSection("areaInfo").getConfigurationSection("selectedAreaOriBlocks").getKeys(false);
//                                } catch (Exception error) {
//                                    System.out.println(error.toString());
//                                }
//
//                                //mainkey 玩家名字加上forloop次数准备放入hashmap
//                                mainKeyAndNumber = mainKey + ":" + mainKeyCount;
//                                //获取玩家area所有数据里的totalBlockHardness
//                                int selectedAreaOriBlocksHardness = PlayerSelectedAreaConfig.getInt(mainKey + "." + secondKey + ".areaInfo" + ".totalBlockHardness");
//                                int selectedAreaOriRepairBlocksHardness = PlayerSelectedAreaConfig.getInt(mainKey + "." + secondKey + ".areaInfo" + ".repairTotalBlockHardness");
//
//                                //列子：Ansalem2608:1=Ansalem2608 - Number-Of-Selected-Location3 - [world;-39;59;39, world;-39;59;40, world;-38;59;39, world;-38;59;40]BlockHardness=70=repairTotalBlockHardness=70
//                                allRecordBlock.put(mainKeyAndNumber, mainKey + " - " + secondKey + " - " + selectedAreaOriBlocks + "BlockHardness=" + selectedAreaOriBlocksHardness + "=repairTotalBlockHardness=" + selectedAreaOriRepairBlocksHardness);
//
//                            }
//                        }
//                    }
//                }
//                mainKeyCount = 0;
//            }
//
//            int x = 0;
//            int y = -3;
//            int z = -3;
//            for ( x = -3; x < 5; x++) {
//                if (!(x == 0 && y == 0 && z == 0)) {
//                    if (x < 4) {
//                        if (allRecordBlock.values().toString().contains(setXYZ(setFireBlock, x, 0, 0))) {
//                            e.setCancelled(true);
//
//                        }
//                    }
//                    if (y < 4) {
//                        if (allRecordBlock.values().toString().contains(setXYZ(setFireBlock, 0, y, 0))) {
//                            e.setCancelled(true);
//
//                        }
//                        y++;
//                    }
//                    if (z < 4){
//                        if (allRecordBlock.values().toString().contains(setXYZ(setFireBlock, 0, 0, z))) {
//                            e.setCancelled(true);
//
//                        }
//                        z++;
//                    }
//                }
//            }
//
//            allRecordBlock.clear();
//
//        }
//
//    }

    @EventHandler
    public void OnBlockBurn(BlockBurnEvent e){
        Block igniteBlock = e.getIgnitingBlock();
        Set<String> allBlockInConfig = new HashSet<>();

        if (igniteBlock != null) {
            igniteBlockWorldString = igniteBlock.getWorld().getName() + ";" + igniteBlock.getX() + ";" + igniteBlock.getY() + ";" + igniteBlock.getZ();
            File file = new File(Bukkit.getServer().getPluginManager().getPlugin("SiegeSafeBaseShield").getDataFolder(), "config.yml");
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            File PlayerSelectedAreaFile = new File(Bukkit.getServer().getPluginManager().getPlugin("SiegeSafeBaseShield").getDataFolder(), "PlayerSelectedAreaConfig.yml");
            FileConfiguration PlayerSelectedAreaConfig = YamlConfiguration.loadConfiguration(PlayerSelectedAreaFile);


            for (String mainKey : PlayerSelectedAreaConfig.getKeys(false)){
                    for (String secondKey : PlayerSelectedAreaConfig.getConfigurationSection(mainKey).getKeys(false)) {
                        if (!secondKey.equals("Number-Of-Selected-Location")) {
                            for (String selectedAreaOriBlocks : PlayerSelectedAreaConfig.getConfigurationSection(mainKey).getConfigurationSection(secondKey).getConfigurationSection("areaInfo").getConfigurationSection("selectedAreaOriBlocks").getKeys(false)) {
                                allBlockInConfig.add(selectedAreaOriBlocks);

                            }
                        }
                    }
                }
            }

            if (allBlockInConfig.contains(igniteBlockWorldString)){
                igniteBlock.setType(Material.AIR);
                e.setCancelled(true);

            }

        }



    @EventHandler
    public void OnWaterFlow (BlockFromToEvent e){
        Block sorceBlock = e.getBlock();
        Block toBlock = e.getToBlock();
        String toBlockWorld = toBlock.getWorld().getName();
        Integer toBlockWorldLocationX = toBlock.getX();
        Integer toBlockWorldLocationY = toBlock.getY();
        Integer toBlockWorldLocationZ = toBlock.getZ();
        String toBLockWorldLocationString = toBlockWorld + ";" + toBlockWorldLocationX + ";" + toBlockWorldLocationY + ";" + toBlockWorldLocationZ;

        Block belowWaterBlock = toBlock.getRelative(0, -1, 0);

        if (whoEmptyBucketAllSelectedAreaOriBlocks.contains(toBLockWorldLocationString)) {
            e.setCancelled(true);

        }
    }

    @EventHandler
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent e) {
        whoEmptyBucket = e.getPlayer();
        Block emptyBucketBlock = e.getBlock();
        String emptyBucketBlockType = "WATER";
        String emptyBucketBlockWorld = emptyBucketBlock.getWorld().getName();
        Integer emptyBucketBlockWorldLocationX = emptyBucketBlock.getX();
        Integer emptyBucketBlockWorldLocationY = emptyBucketBlock.getY();
        Integer emptyBucketBlockWorldLocationZ = emptyBucketBlock.getZ();
        String emptyBucketBlockWorldLocationString = emptyBucketBlockWorld + ";" + emptyBucketBlockWorldLocationX + ";" + emptyBucketBlockWorldLocationY + ";" + emptyBucketBlockWorldLocationZ;

        File file = new File(Bukkit.getServer().getPluginManager().getPlugin("SiegeSafeBaseShield").getDataFolder(), "config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        File PlayerSelectedAreaFile = new File(Bukkit.getServer().getPluginManager().getPlugin("SiegeSafeBaseShield").getDataFolder(), "PlayerSelectedAreaConfig.yml");
        FileConfiguration PlayerSelectedAreaConfig = YamlConfiguration.loadConfiguration(PlayerSelectedAreaFile);

        whoEmptyBucketAllConfigBlock = false;
        whoEmptyBucketSelfConfigBlock = false;
        whoEmptyBucketAllSelectedAreaOriBlocks.clear();
        whoEmptyBucketSelfSelectedAreaOriBlocks.clear();

        for (String mainKey : PlayerSelectedAreaConfig.getKeys(false)){
            if (!mainKey.equals(whoEmptyBucket.getName())) {
                for (String secondKey : PlayerSelectedAreaConfig.getConfigurationSection(mainKey).getKeys(false)) {
                    if (!secondKey.equals("Number-Of-Selected-Location")) {
//                    whoEmptyBucket.sendMessage(mainKey);
//                    whoEmptyBucket.sendMessage(secondKey);
                        for (String selectedAreaOriBlocks : PlayerSelectedAreaConfig.getConfigurationSection(mainKey).getConfigurationSection(secondKey).getConfigurationSection("areaInfo").getConfigurationSection("selectedAreaOriBlocks").getKeys(false)) {
                            whoEmptyBucketAllSelectedAreaOriBlocks.add(selectedAreaOriBlocks);


                        }
                    }
                }
            }
        }

        for (String mainKey : PlayerSelectedAreaConfig.getKeys(false)){
            if (mainKey.equals(whoEmptyBucket.getName())) {
                for (String secondKey : PlayerSelectedAreaConfig.getConfigurationSection(mainKey).getKeys(false)) {
                    if (!secondKey.equals("Number-Of-Selected-Location")) {
//                        whoEmptyBucket.sendMessage(mainKey);
//                        whoEmptyBucket.sendMessage(secondKey);
                        for (String selectedAreaOriBlocks : PlayerSelectedAreaConfig.getConfigurationSection(mainKey).getConfigurationSection(secondKey).getConfigurationSection("areaInfo").getConfigurationSection("selectedAreaOriBlocks").getKeys(false)) {
                            whoEmptyBucketSelfSelectedAreaOriBlocks.add(selectedAreaOriBlocks);

                        }
                    }
                }
            }
        }

        if (whoEmptyBucketAllSelectedAreaOriBlocks.contains(emptyBucketBlockWorldLocationString)) {
            whoEmptyBucketAllConfigBlock = true;

        }

        if (whoEmptyBucketSelfSelectedAreaOriBlocks.contains(emptyBucketBlockWorldLocationString)) {
            whoEmptyBucketSelfConfigBlock = true;

        }

        if (whoEmptyBucketAllConfigBlock == false && whoEmptyBucketSelfConfigBlock == true){
            //自己的block
            e.setCancelled(false);
            if (!SiegeSafeBaseShield.areaUnderAttack) {
                outerloop:
                for (String selfMainKey : PlayerSelectedAreaConfig.getKeys(false)) {
                    if (selfMainKey.equals(whoEmptyBucket.getName())) {
                        for (String selfSecondKey : PlayerSelectedAreaConfig.getConfigurationSection(selfMainKey).getKeys(false)) {
                            if (!selfSecondKey.equals("Number-Of-Selected-Location")) {
                                Set<String> selfSelectedAreaOriBlocks = PlayerSelectedAreaConfig.getConfigurationSection(selfMainKey).getConfigurationSection(selfSecondKey).getConfigurationSection("areaInfo").getConfigurationSection("selectedAreaOriBlocks").getKeys(false);
                                Set<String> selfBlocksReplaceBySelectedBlocks = PlayerSelectedAreaConfig.getConfigurationSection(selfMainKey).getConfigurationSection(selfSecondKey).getConfigurationSection("areaInfo").getConfigurationSection("blocksReplaceBySelectedBlocks").getKeys(false);

                                if (selfSelectedAreaOriBlocks.contains(emptyBucketBlockWorldLocationString) && selfBlocksReplaceBySelectedBlocks.contains(emptyBucketBlockWorldLocationString)) {
                                    PlayerSelectedAreaConfig.set(selfMainKey + "." + selfSecondKey + ".areaInfo" + ".selectedAreaOriBlocks." + emptyBucketBlockWorldLocationString, emptyBucketBlockType);
                                    PlayerSelectedAreaConfig.set(selfMainKey + "." + selfSecondKey + ".areaInfo" + ".blocksReplaceBySelectedBlocks." + emptyBucketBlockWorldLocationString, emptyBucketBlockType);
                                    break outerloop;

                                } else if (selfSelectedAreaOriBlocks.contains(emptyBucketBlockWorldLocationString)) {
                                    PlayerSelectedAreaConfig.set(selfMainKey + "." + selfSecondKey + ".areaInfo" + ".selectedAreaOriBlocks." + emptyBucketBlockWorldLocationString, emptyBucketBlockType);
                                    break outerloop;

                                } else if (selfBlocksReplaceBySelectedBlocks.contains(emptyBucketBlockWorldLocationString)) {
                                    PlayerSelectedAreaConfig.set(selfMainKey + "." + selfSecondKey + ".areaInfo" + ".blocksReplaceBySelectedBlocks." + emptyBucketBlockWorldLocationString, emptyBucketBlockType);
                                    break outerloop;

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
            }else {
                whoEmptyBucket.sendMessage(ChatColor.RED + "Your area is currently under attack. You cannot edit any blocks.");
                e.setCancelled(true);
            }

        } else if (whoEmptyBucketAllConfigBlock == true && whoEmptyBucketSelfConfigBlock == false) {
            //其他人的block
            e.setCancelled(true);
            whoEmptyBucket.sendMessage(ChatColor.RED + "Water cannot be placed in other player’s area");

        }
    }

    @EventHandler
    public void OnPlayerBucketFill (PlayerBucketFillEvent e){
        Player whoFillBucket = e.getPlayer();
        Block fillBucketBlock = e.getBlock();
        String fillBucketBlockType = "AIR";
        String fillBucketBlockWorld = fillBucketBlock.getWorld().getName();
        Integer fillBucketBlockWorldLocationX = fillBucketBlock.getX();
        Integer fillBucketBlockWorldLocationY = fillBucketBlock.getY();
        Integer fillBucketBlockWorldLocationZ = fillBucketBlock.getZ();
        String fillBucketBlockWorldLocationString = fillBucketBlockWorld + ";" + fillBucketBlockWorldLocationX + ";" + fillBucketBlockWorldLocationY + ";" + fillBucketBlockWorldLocationZ;

        File file = new File(Bukkit.getServer().getPluginManager().getPlugin("SiegeSafeBaseShield").getDataFolder(), "config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        File PlayerSelectedAreaFile = new File(Bukkit.getServer().getPluginManager().getPlugin("SiegeSafeBaseShield").getDataFolder(), "PlayerSelectedAreaConfig.yml");
        FileConfiguration PlayerSelectedAreaConfig = YamlConfiguration.loadConfiguration(PlayerSelectedAreaFile);

        Boolean allConfigBlock = false;
        Boolean selfConfigBlock = false;

        for (String mainKey : PlayerSelectedAreaConfig.getKeys(false)){
            for (String secondKey : PlayerSelectedAreaConfig.getConfigurationSection(mainKey).getKeys(false)){
                if (!secondKey.equals("Number-Of-Selected-Location")){
//                    whoEmptyBucket.sendMessage(mainKey);
//                    whoEmptyBucket.sendMessage(secondKey);
                    for (String selectedAreaOriBlocks : PlayerSelectedAreaConfig.getConfigurationSection(mainKey).getConfigurationSection(secondKey).getConfigurationSection("areaInfo").getConfigurationSection("selectedAreaOriBlocks").getKeys(false)) {
                        whoEmptyBucketAllSelectedAreaOriBlocks.add(selectedAreaOriBlocks);


                    }
                }
            }
        }

        for (String mainKey : PlayerSelectedAreaConfig.getKeys(false)){
            if (mainKey.equals(whoFillBucket.getName())) {
                for (String secondKey : PlayerSelectedAreaConfig.getConfigurationSection(mainKey).getKeys(false)) {
                    if (!secondKey.equals("Number-Of-Selected-Location")) {
//                        whoEmptyBucket.sendMessage(mainKey);
//                        whoEmptyBucket.sendMessage(secondKey);
                        for (String selectedAreaOriBlocks : PlayerSelectedAreaConfig.getConfigurationSection(mainKey).getConfigurationSection(secondKey).getConfigurationSection("areaInfo").getConfigurationSection("selectedAreaOriBlocks").getKeys(false)) {
                            whoEmptyBucketSelfSelectedAreaOriBlocks.add(selectedAreaOriBlocks);

                        }
                    }
                }
            }
        }

        if (whoEmptyBucketAllSelectedAreaOriBlocks.contains(fillBucketBlockWorldLocationString)) {
            allConfigBlock = true;

        }

        if (whoEmptyBucketSelfSelectedAreaOriBlocks.contains(fillBucketBlockWorldLocationString)) {
            selfConfigBlock = true;

        }

        if (allConfigBlock == true && selfConfigBlock == true){
            //自己的block
            e.setCancelled(false);
            if (!SiegeSafeBaseShield.areaUnderAttack) {
                outerloop:
                for (String selfMainKey : PlayerSelectedAreaConfig.getKeys(false)) {
                    if (selfMainKey.equals(whoFillBucket.getName())) {
                        for (String selfSecondKey : PlayerSelectedAreaConfig.getConfigurationSection(selfMainKey).getKeys(false)) {
                            if (!selfSecondKey.equals("Number-Of-Selected-Location")) {
                                Set<String> selfSelectedAreaOriBlocks = PlayerSelectedAreaConfig.getConfigurationSection(selfMainKey).getConfigurationSection(selfSecondKey).getConfigurationSection("areaInfo").getConfigurationSection("selectedAreaOriBlocks").getKeys(false);
                                Set<String> selfBlocksReplaceBySelectedBlocks = PlayerSelectedAreaConfig.getConfigurationSection(selfMainKey).getConfigurationSection(selfSecondKey).getConfigurationSection("areaInfo").getConfigurationSection("blocksReplaceBySelectedBlocks").getKeys(false);

                                if (selfSelectedAreaOriBlocks.contains(fillBucketBlockWorldLocationString) && selfBlocksReplaceBySelectedBlocks.contains(fillBucketBlockWorldLocationString)) {
                                    PlayerSelectedAreaConfig.set(selfMainKey + "." + selfSecondKey + ".areaInfo" + ".selectedAreaOriBlocks." + fillBucketBlockWorldLocationString, fillBucketBlockType);
                                    PlayerSelectedAreaConfig.set(selfMainKey + "." + selfSecondKey + ".areaInfo" + ".blocksReplaceBySelectedBlocks." + fillBucketBlockWorldLocationString, fillBucketBlockType);
                                    break outerloop;

                                } else if (selfSelectedAreaOriBlocks.contains(fillBucketBlockWorldLocationString)) {
                                    PlayerSelectedAreaConfig.set(selfMainKey + "." + selfSecondKey + ".areaInfo" + ".selectedAreaOriBlocks." + fillBucketBlockWorldLocationString, fillBucketBlockType);
                                    break outerloop;

                                } else if (selfBlocksReplaceBySelectedBlocks.contains(fillBucketBlockWorldLocationString)) {
                                    PlayerSelectedAreaConfig.set(selfMainKey + "." + selfSecondKey + ".areaInfo" + ".blocksReplaceBySelectedBlocks." + fillBucketBlockWorldLocationString, fillBucketBlockType);
                                    break outerloop;

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
            }else {
                whoFillBucket.sendMessage(ChatColor.RED + "Your area is currently under attack. You cannot edit any blocks.");
                e.setCancelled(true);
            }

        } else if (allConfigBlock == true && selfConfigBlock != true) {
            //其他人的block
            e.setCancelled(true);
            whoEmptyBucket.sendMessage(ChatColor.RED + "Cannot fill water in other player’s area");

        }
    }

    public static String setXYZ(Block setFireBlock, int x, int y, int z){
        Block blockAroundFire = setFireBlock.getRelative(x,y,z);
        String blockAroundFireWorld = blockAroundFire.getWorld().getName();
        Integer blockAroundFireLocationX = blockAroundFire.getX();
        Integer blockAroundFireLocationY = blockAroundFire.getY();
        Integer blockAroundFireLocationZ = blockAroundFire.getZ();
        String blockAroundFireLocationString = blockAroundFireWorld + ";" + blockAroundFireLocationX + ";" + blockAroundFireLocationY + ";" + blockAroundFireLocationZ;

        return blockAroundFireLocationString;
    }

}
