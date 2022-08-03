package cn.goldenpotato.tide.Command.SubCommands;

import cn.goldenpotato.tide.Command.SubCommand;
import cn.goldenpotato.tide.Config.MessageManager;
import cn.goldenpotato.tide.Tide;
import cn.goldenpotato.tide.Util.Util;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Add extends SubCommand
{
    public Add()
    {
        this.name = "add";
        this.permission = "tide.admin";
        this.usage = MessageManager.msg.SubCommand_Add_Usage;
    }
    @Override
    public void onCommand(Player player, String[] args)
    {
        int targetLevel;
        if(args.length==0)
            targetLevel = player.getWorld().getSeaLevel()-1;
        else
        {
            try
            {
                targetLevel = Integer.parseInt(args[0]);
            }
            catch(NumberFormatException e)
            {
                Util.Message(player,MessageManager.msg.WrongNum);
                return;
            }
        }
        Tide.tideSystem.Add(player.getLocation(),targetLevel);
        Util.Message(player,MessageManager.msg.Success);
    }

    @Override
    public List<String> onTab(Player player, String[] args)
    {
        List<String> result = new ArrayList<>();
        if (args.length==1)
            result.add(String.valueOf(player.getLocation().getBlockY()));
        return result;
    }
}
