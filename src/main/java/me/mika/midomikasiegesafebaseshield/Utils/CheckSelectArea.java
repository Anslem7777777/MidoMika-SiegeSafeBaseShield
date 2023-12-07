package me.mika.midomikasiegesafebaseshield.Utils;

import me.mika.midomikasiegesafebaseshield.Listeners.SelectArea;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashMap;

import static me.mika.midomikasiegesafebaseshield.Listeners.SelectArea.selection;

public class CheckSelectArea {

    public static void checkSelectArea(HashMap<String, String> allRecordBlock, Player p){

        selection = SelectArea.playerSelections.getOrDefault(p, new Location[2]);

        if(selection[1] != null) {
            Location minLocation = new Location(
                    p.getWorld(),
                    Math.min(selection[0].getX(), selection[1].getX()),
                    Math.min(selection[0].getY(), selection[1].getY()),
                    Math.min(selection[0].getZ(), selection[1].getZ())
            );
            Location maxLocation = new Location(
                    p.getWorld(),
                    Math.max(selection[0].getX(), selection[1].getX()),
                    Math.max(selection[0].getY(), selection[1].getY()),
                    Math.max(selection[0].getZ(), selection[1].getZ())
            );


            World world = minLocation.getWorld();
            int minX = minLocation.getBlockX();
            int minY = minLocation.getBlockY();
            int minZ = minLocation.getBlockZ();
            int maxX = maxLocation.getBlockX();
            int maxY = maxLocation.getBlockY();
            int maxZ = maxLocation.getBlockZ();

            outerLoop:
            for (int x = minX; x <= maxX; x++) {
                for (int y = minY; y <= maxY; y++) {
                    for (int z = minZ; z <= maxZ; z++) {
                        String selectedBlockCheckString = world.getName() + ";" + x + ";" + y + ";" + z;
                        if (allRecordBlock.values().toString().contains(selectedBlockCheckString)) {
                            selection[0] = null;
                            selection[1] = null;
                            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "The area cannot be select as it has already been set."));
                            break outerLoop;

                        } else {

                        }

                    }
                }
            }
        }
    }

    public static void checkLeftClickSetSelectArea(HashMap<String, String> allRecordBlock, Player p){
        String leftClickSelectionWorld = selection[0].getWorld().getName();
        int leftClickSelectionLocationX = selection[0].getBlockX();
        int leftClickSelectionLocationY = selection[0].getBlockY();
        int leftClickSelectionLocationZ = selection[0].getBlockZ();
        String leftClickSelectionLocationString = leftClickSelectionWorld + ";" + leftClickSelectionLocationX + ";" + leftClickSelectionLocationY + ";" + leftClickSelectionLocationZ;

        if(allRecordBlock.values().toString().contains(leftClickSelectionLocationString)){
            selection[0] = null;
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "The area cannot be select as it has already been set."));

        }else {
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GREEN + "First point has been set."));

        }
    }

    public static void checkLeftClickResetSelectArea(HashMap<String, String> allRecordBlock, Player p){
        selection = SelectArea.playerSelections.getOrDefault(p, new Location[2]);
        String leftClickSelectionWorld = selection[0].getWorld().getName();
        int leftClickSelectionLocationX = selection[0].getBlockX();
        int leftClickSelectionLocationY = selection[0].getBlockY();
        int leftClickSelectionLocationZ = selection[0].getBlockZ();
        String leftClickSelectionLocationString = leftClickSelectionWorld + ";" + leftClickSelectionLocationX + ";" + leftClickSelectionLocationY + ";" + leftClickSelectionLocationZ;

        if(allRecordBlock.values().toString().contains(leftClickSelectionLocationString)){
            selection[0] = null;
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "The area cannot be select as it has already been set."));

        }else {
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GREEN + "First point has been reset."));

        }
    }

    public static void checkRightClickSetSelectArea(HashMap<String, String> allRecordBlock, Player p){
        selection = SelectArea.playerSelections.getOrDefault(p, new Location[2]);
        String rightClickSelectionWorld = selection[1].getWorld().getName();
        int rightClickSelectionLocationX = selection[1].getBlockX();
        int rightClickSelectionLocationY = selection[1].getBlockY();
        int rightClickSelectionLocationZ = selection[1].getBlockZ();
        String rightClickSelectionLocationString = rightClickSelectionWorld + ";" + rightClickSelectionLocationX + ";" + rightClickSelectionLocationY + ";" + rightClickSelectionLocationZ;

        if(allRecordBlock.values().toString().contains(rightClickSelectionLocationString)){
            selection[1] = null;
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "The area cannot be select as it has already been set."));

        }else {
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GREEN + "Second point has been set."));

        }
    }

    public static void checkRightClickResetSelectArea(HashMap<String, String> allRecordBlock, Player p){
        selection = SelectArea.playerSelections.getOrDefault(p, new Location[2]);
        String rightClickSelectionWorld = selection[1].getWorld().getName();
        int rightClickSelectionLocationX = selection[1].getBlockX();
        int rightClickSelectionLocationY = selection[1].getBlockY();
        int rightClickSelectionLocationZ = selection[1].getBlockZ();
        String rightClickSelectionLocationString = rightClickSelectionWorld + ";" + rightClickSelectionLocationX + ";" + rightClickSelectionLocationY + ";" + rightClickSelectionLocationZ;

        if(allRecordBlock.values().toString().contains(rightClickSelectionLocationString)){
            selection[1] = null;
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "The area cannot be select as it has already been set."));

        }else {
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.DARK_GREEN + "Second point has been reset."));

        }
    }
}
