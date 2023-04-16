package me.loving11ish.epichomes.commands;

import me.loving11ish.epichomes.EpicHomes;
import me.loving11ish.epichomes.models.User;
import me.loving11ish.epichomes.utils.UsermapStorageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class HomeCommandTabCompleter implements TabCompleter {

    private UsermapStorageUtil usermapStorageUtil = EpicHomes.getPlugin().usermapStorageUtil;

    List<String> arguments = new ArrayList<>();

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player){
            Player player = (Player) sender;
            User user = usermapStorageUtil.getUserByOnlinePlayer(player);
            if (user != null){
                if (arguments.isEmpty()){
                    List<String> userHomesList = usermapStorageUtil.getHomeNamesListByUser(user);
                    arguments.addAll(userHomesList);
                    arguments.add("set");
                    arguments.add("delete");
                    arguments.add("list");
                    if (player.hasPermission("epichomes.command.reload")
                            ||player.hasPermission("epichomes.command.*")
                            ||player.hasPermission("epichomes.*")
                            ||player.isOp()){
                        arguments.add("reload");
                    }
                }

                List<String> result = new ArrayList<>();
                if (args.length == 1){
                    for (String a : arguments){
                        if (a.toLowerCase().startsWith(args[0].toLowerCase())){
                            result.add(a);
                        }
                    }
                    return result;
                }
            }
        }
        return null;
    }
}
