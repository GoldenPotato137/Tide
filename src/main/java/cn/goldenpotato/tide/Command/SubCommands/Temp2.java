package cn.goldenpotato.tide.Command.SubCommands;

import cn.goldenpotato.tide.Command.SubCommand;
import cn.goldenpotato.tide.Water.TideSystem;
import cn.goldenpotato.tide.Water.WaterCalculator;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import java.util.List;

public class Temp2 extends SubCommand
{
    public Temp2()
    {
        name = "temp2";
        permission = "tide.admin";
        usage = "qwq";
    }

    @Override
    public void onCommand(Player player, String[] args)
    {
        TideSystem._seaLevel --;
        for(Chunk chunk : player.getWorld().getLoadedChunks())
            WaterCalculator.AddUpdate(chunk);
    }

    @Override
    public List<String> onTab(Player player, String[] args)
    {
        return null;
    }
}
