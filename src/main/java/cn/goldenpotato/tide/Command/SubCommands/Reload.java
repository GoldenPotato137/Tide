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
        //加载世界
        for (String worldString : ConfigManager.config.worlds)
        {
            World world = Bukkit.getWorld(worldString);
            if(world==null)
            {
                Tide.instance.getLogger().warning("No such world: %world%".replace("%world%",worldString));
                Util.Message(player,MessageManager.msg.NoSuchWorld.replace("%world%",worldString));
                continue;
            }
            TideSystem.Load(world);
            TideSystem.CalcNearbyChunk(world);
        }
        if(player!=null)
            Util.Message(player, MessageManager.msg.Success);
    }

    @Override
    public List<String> onTab(Player player, String[] args)
    {
        return null;
    }
}
