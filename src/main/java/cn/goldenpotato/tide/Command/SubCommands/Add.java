package cn.goldenpotato.tide.Command.SubCommands;

import cn.goldenpotato.tide.Command.SubCommand;
import cn.goldenpotato.tide.Config.MessageManager;
import cn.goldenpotato.tide.Util.Util;
import cn.goldenpotato.tide.Water.TideSystem;
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
        TideSystem.GetChunkData(player.getLocation().getChunk()).isInner = 0;
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
