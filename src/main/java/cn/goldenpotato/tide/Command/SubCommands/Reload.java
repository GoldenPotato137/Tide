package cn.goldenpotato.tide.Command.SubCommands;

import cn.goldenpotato.tide.Command.SubCommand;
import cn.goldenpotato.tide.Config.MessageManager;
import cn.goldenpotato.tide.Tide;
import cn.goldenpotato.tide.Util.Util;
import org.bukkit.entity.Player;

import java.util.List;

public class Reload extends SubCommand
{
    public Reload()
    {
        name = "reload";
        permission = "tide.admin";
        usage = MessageManager.msg.SubCommand_Reload_Usage;
    }

    @Override
    public void onCommand(Player player, String[] args)
    {
        Tide.Load();
        if(player!=null)
            Util.Message(player, MessageManager.msg.Success);
    }

    @Override
    public List<String> onTab(Player player, String[] args)
    {
        return null;
    }
}
