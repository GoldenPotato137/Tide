package cn.goldenpotato.tide.Command.SubCommands;

import cn.goldenpotato.tide.Command.SubCommand;
import cn.goldenpotato.tide.Config.MessageManager;
import cn.goldenpotato.tide.Util.Util;
import cn.goldenpotato.tide.Water.TideSystem;
import cn.goldenpotato.tide.Water.TideTime;
import org.bukkit.entity.Player;

import java.util.List;

public class Status extends SubCommand
{
    public Status()
    {
        name = "status";
        permission = "tide.status";
        usage = MessageManager.msg.SubCommand_Status_TideTable;
    }

    @Override
    public void onCommand(Player player, String[] args)
    {
        StringBuilder message = new StringBuilder();
        message.append(MessageManager.msg.SubCommand_Status_TideTable).append("\n");
        for(TideTime tideTime : TideSystem.tideTime)
            message.append("§a").append(Util.TickToTime(tideTime.tick)).append(": §c").append(tideTime.level > 0 ? "+" : "").append(tideTime.level).append("\n");
        Util.Message(player,message.toString());
    }

    @Override
    public List<String> onTab(Player player, String[] args)
    {
        return null;
    }
}
