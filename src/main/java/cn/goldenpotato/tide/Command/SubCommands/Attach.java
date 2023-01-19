package cn.goldenpotato.tide.Command.SubCommands;

import cn.goldenpotato.tide.Command.SubCommand;
import cn.goldenpotato.tide.Config.ConfigManager;
import cn.goldenpotato.tide.Config.MessageManager;
import cn.goldenpotato.tide.Tide;
import cn.goldenpotato.tide.Util.Util;
import cn.goldenpotato.tide.Water.TideSystem;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Attach extends SubCommand
{
    public Attach()
    {
        name = "attach";
        permission = "tide.admin";
        usage = MessageManager.msg.SubCommand_Attach_Usage;
    }
    @Override
    public void onCommand(Player player, String[] args)
    {
        World world = player.getWorld();
        if(args.length!=0)
            world = Bukkit.getWorld(args[0]);
        if(world==null) return;
        if(ConfigManager.config.worlds.contains(world.getName()))
        {
            Util.Message(player,MessageManager.msg.SubCommand_Attach_Already);
            return;
        }
        TideSystem.Load(world);
        ConfigManager.Save();
        Util.Message(player,MessageManager.msg.Success);
    }

    @Override
    public List<String> onTab(Player player, String[] args)
    {
        List<String> result = new ArrayList<>();
        if(args.length==1)
            for(World world : Bukkit.getWorlds())
                result.add(world.getName());
        return result;
    }
}
