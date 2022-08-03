package cn.goldenpotato.tide.Command.SubCommands;

import cn.goldenpotato.tide.Command.CommandManager;
import cn.goldenpotato.tide.Command.SubCommand;
import cn.goldenpotato.tide.Config.MessageManager;
import cn.goldenpotato.tide.Util.Util;
import org.bukkit.entity.Player;

import java.util.List;

public class Help extends SubCommand
{
    public Help()
    {
        name = "help";
        permission = "tide.help";
        usage = MessageManager.msg.SubCommand_Help_Usage;
    }
    @Override
    public void onCommand(Player player, String[] args)
    {
        for(SubCommand command : CommandManager.subCommands)
            if(player.hasPermission(command.permission))
                Util.Message(player,command.name + ": " + command.usage);
    }

    @Override
    public List<String> onTab(Player player, String[] args)
    {
        return null;
    }
}
