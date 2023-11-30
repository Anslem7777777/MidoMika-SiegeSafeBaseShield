package me.mika.midomikasiegesafebaseshield.Commands;
import me.mika.midomikasiegesafebaseshield.SiegeSafeBaseShield;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.ArrayList;


public class SSBSCommandsManager implements CommandExecutor {
    SiegeSafeBaseShield plugin;
    public SSBSCommandsManager(SiegeSafeBaseShield plugin) {
        this.plugin = plugin;
    }
    private ArrayList<SubCommands> subcommands = new ArrayList<>();

    public SSBSCommandsManager(){

        subcommands.add(new SaveCommand(plugin));
        subcommands.add(new DeleteCommand());
        subcommands.add(new ListCommand());

    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (commandSender instanceof Player) {

            Player p = (Player) commandSender;
            if (strings.length > 0){
                for (int i = 0; i < getSubcommands().size(); i++){
                    if (strings[0].equalsIgnoreCase(getSubcommands().get(i).getName())){
                        //perform equals Executor
                        getSubcommands().get(i).perform(p, strings);

                    }
                }
            } else if (strings.length == 0) {

                p.sendMessage( "==========================");
                p.sendMessage(ChatColor.AQUA + "SiegeSafe Base Shield Commands");
                p.sendMessage("==========================");
                p.sendMessage(ChatColor.GREEN + "--------------------------");
                for (int i = 0; i < getSubcommands().size(); i++){
                    p.sendMessage( ChatColor.YELLOW + getSubcommands().get(i).getSyntax() +  ": " + ChatColor.WHITE + getSubcommands().get(i).getDescription());
                }
                p.sendMessage(ChatColor.GREEN + "--------------------------");

            }
        }
        return true;
    }

    public ArrayList<SubCommands> getSubcommands(){
        return subcommands;
    }
}
