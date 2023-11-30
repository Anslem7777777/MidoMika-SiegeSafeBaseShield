package me.mika.midomikasiegesafebaseshield.Utils;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;

public class RefreshPlayerSelectedAreaConfig {
    public static void RefreshSelectionConfig(Player p, FileConfiguration PlayerSelectedAreaConfig, FileConfiguration config , String finalSecondKeyAndNumber, String refreshFinalSecondKeyAndNumber){

        //get data from PlayerSelectedAreaConfig
        //1.get name:
        String nameData = PlayerSelectedAreaConfig.getString(p.getName() + "." + finalSecondKeyAndNumber + ".areaInfo" + ".name");
        //2.get selectedAreaOriBlocks:
        ConfigurationSection selectedAreaOriBlocksKeySection = PlayerSelectedAreaConfig.getConfigurationSection(p.getName() + "." + finalSecondKeyAndNumber + ".areaInfo" + ".selectedAreaOriBlocks");
        //3.get playerSelections:
        ConfigurationSection playerSelectionsKeySection = PlayerSelectedAreaConfig.getConfigurationSection(p.getName() + "." + finalSecondKeyAndNumber + ".areaInfo" + ".playerSelections");
        //4.get blocksReplaceBySelectedBlocks:
        ConfigurationSection blocksReplaceBySelectedBlocksKeySection = PlayerSelectedAreaConfig.getConfigurationSection(p.getName() + "." + finalSecondKeyAndNumber + ".areaInfo" + ".blocksReplaceBySelectedBlocks");
        //5.get selectedBlocks:
        ConfigurationSection selectedBlocksKeySection = PlayerSelectedAreaConfig.getConfigurationSection(p.getName() + "." + finalSecondKeyAndNumber + ".areaInfo" + ".selectedBlocks");
        //6,7.get hardness:
        double totalBlockHardness = PlayerSelectedAreaConfig.getDouble(p.getName() + "." + finalSecondKeyAndNumber + ".areaInfo" + ".totalBlockHardness");
        double repairTotalBlockHardness = PlayerSelectedAreaConfig.getDouble(p.getName() + "." + finalSecondKeyAndNumber + ".areaInfo" + ".repairTotalBlockHardness");
        //8.get breakBlockTime
        LocalDateTime breakBlockTime = LocalDateTime.parse(PlayerSelectedAreaConfig.getString(p.getName() + "." + finalSecondKeyAndNumber + ".areaInfo" + ".breakBlockTime"));


        //delete all data wait from re-write
        String selectedDeleteObject = p.getName() + "." + finalSecondKeyAndNumber;
        PlayerSelectedAreaConfig.set(selectedDeleteObject, null);


        //set date to config
        //1.set name:
        PlayerSelectedAreaConfig.set(p.getName() + "." + refreshFinalSecondKeyAndNumber + ".areaInfo" + ".name", nameData);
        //2.set selectedAreaOriBlocks:
        PlayerSelectedAreaConfig.set(p.getName() + "." + refreshFinalSecondKeyAndNumber + ".areaInfo" + ".selectedAreaOriBlocks", selectedAreaOriBlocksKeySection.getValues(false));
        //3.set playerSelections:
        PlayerSelectedAreaConfig.set(p.getName() + "." + refreshFinalSecondKeyAndNumber + ".areaInfo" + ".playerSelections", playerSelectionsKeySection.getValues(false));
        //4.set blocksReplaceBySelectedBlocks:
        PlayerSelectedAreaConfig.set(p.getName() + "." + refreshFinalSecondKeyAndNumber + ".areaInfo" + ".blocksReplaceBySelectedBlocks", blocksReplaceBySelectedBlocksKeySection.getValues(false));
        //5.set selectedBlocks:
        PlayerSelectedAreaConfig.set(p.getName() + "." + refreshFinalSecondKeyAndNumber + ".areaInfo" + ".selectedBlocks", selectedBlocksKeySection.getValues(false));
        //6,7.set hardness
        PlayerSelectedAreaConfig.set(p.getName() + "." + refreshFinalSecondKeyAndNumber + ".areaInfo" + ".totalBlockHardness", totalBlockHardness);
        PlayerSelectedAreaConfig.set(p.getName() + "." + refreshFinalSecondKeyAndNumber + ".areaInfo" + ".repairTotalBlockHardness", repairTotalBlockHardness);
        //8.set breakBlockTime
        PlayerSelectedAreaConfig.set(p.getName() + "." + refreshFinalSecondKeyAndNumber + ".areaInfo" + ".breakBlockTime", breakBlockTime.toString());

    }

    public static void RefreshSelectionBreakConfig(String areaOwnerName, FileConfiguration PlayerSelectedAreaConfig, FileConfiguration config , String finalSecondKeyAndNumber, String refreshFinalSecondKeyAndNumber){

        //get data from PlayerSelectedAreaConfig
        //get name:
        String nameData = PlayerSelectedAreaConfig.getString(areaOwnerName + "." + finalSecondKeyAndNumber + ".areaInfo" + ".name");
        //get selectedAreaOriBlocks:
        ConfigurationSection selectedAreaOriBlocksKeySection = PlayerSelectedAreaConfig.getConfigurationSection(areaOwnerName + "." + finalSecondKeyAndNumber + ".areaInfo" + ".selectedAreaOriBlocks");
        //get playerSelections:
        ConfigurationSection playerSelectionsKeySection = PlayerSelectedAreaConfig.getConfigurationSection(areaOwnerName + "." + finalSecondKeyAndNumber + ".areaInfo" + ".playerSelections");
        //get blocksReplaceBySelectedBlocks:
        ConfigurationSection blocksReplaceBySelectedBlocksKeySection = PlayerSelectedAreaConfig.getConfigurationSection(areaOwnerName + "." + finalSecondKeyAndNumber + ".areaInfo" + ".blocksReplaceBySelectedBlocks");
        //get selectedBlocks:
        ConfigurationSection selectedBlocksKeySection = PlayerSelectedAreaConfig.getConfigurationSection(areaOwnerName + "." + finalSecondKeyAndNumber + ".areaInfo" + ".selectedBlocks");
        //get hardness:
        double totalBlockHardness = PlayerSelectedAreaConfig.getDouble(areaOwnerName + "." + finalSecondKeyAndNumber + ".areaInfo" + ".totalBlockHardness");
        double repairTotalBlockHardness = PlayerSelectedAreaConfig.getDouble(areaOwnerName + "." + finalSecondKeyAndNumber + ".areaInfo" + ".repairTotalBlockHardness");
        //get breakBlockTime
        LocalDateTime breakBlockTime = LocalDateTime.parse(PlayerSelectedAreaConfig.getString(areaOwnerName + "." + finalSecondKeyAndNumber + ".areaInfo" + ".breakBlockTime"));


        //delete all data wait from re-write
        String selectedDeleteObject = areaOwnerName + "." + finalSecondKeyAndNumber;
        PlayerSelectedAreaConfig.set(selectedDeleteObject, null);

        //set date to config
        //set name:
        PlayerSelectedAreaConfig.set(areaOwnerName + "." + refreshFinalSecondKeyAndNumber + ".areaInfo" + ".name", nameData);
        //set selectedAreaOriBlocks:
        PlayerSelectedAreaConfig.set(areaOwnerName + "." + refreshFinalSecondKeyAndNumber + ".areaInfo" + ".selectedAreaOriBlocks", selectedAreaOriBlocksKeySection.getValues(false));
        //set playerSelections:
        PlayerSelectedAreaConfig.set(areaOwnerName + "." + refreshFinalSecondKeyAndNumber + ".areaInfo" + ".playerSelections", playerSelectionsKeySection.getValues(false));
        //set blocksReplaceBySelectedBlocks:
        PlayerSelectedAreaConfig.set(areaOwnerName + "." + refreshFinalSecondKeyAndNumber + ".areaInfo" + ".blocksReplaceBySelectedBlocks", blocksReplaceBySelectedBlocksKeySection.getValues(false));
        //set selectedBlocks:
        PlayerSelectedAreaConfig.set(areaOwnerName + "." + refreshFinalSecondKeyAndNumber + ".areaInfo" + ".selectedBlocks", selectedBlocksKeySection.getValues(false));
        //set hardness
        PlayerSelectedAreaConfig.set(areaOwnerName + "." + refreshFinalSecondKeyAndNumber + ".areaInfo" + ".totalBlockHardness", totalBlockHardness);
        PlayerSelectedAreaConfig.set(areaOwnerName + "." + refreshFinalSecondKeyAndNumber + ".areaInfo" + ".repairTotalBlockHardness", repairTotalBlockHardness);
        //set breakBlockTime
        PlayerSelectedAreaConfig.set(areaOwnerName + "." + refreshFinalSecondKeyAndNumber + ".areaInfo" + ".breakBlockTime", breakBlockTime.toString());

    }
}
