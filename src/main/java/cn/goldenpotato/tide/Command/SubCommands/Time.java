package cn.goldenpotato.tide.Command.SubCommands;

import cn.goldenpotato.tide.Command.SubCommand;
import cn.goldenpotato.tide.Config.MessageManager;
import cn.goldenpotato.tide.Util.Util;
import cn.goldenpotato.tide.Water.TideSystem;
import cn.goldenpotato.tide.Water.TideTime;
import org.bukkit.entity.Player;

import java.util.List;

public class Time extends SubCommand
{
    public Time()
    {
        name = "time";
        permission = "tide.time";
        usage = MessageManager.msg.SubCommand_Time_Usage;
    }

    @Override
    public void onCommand(Player player, String[] args)
    {
        Util.Message(player, MessageManager.msg.SubCommand_Time + Util.TickToTime(player.getWorld().getTime()));
        if (TideSystem.tideTime.size() == 0) return;
        long tickNow = player.getWorld().getTime();
        TideTime time = null;
        for (TideTime t : TideSystem.tideTime)
            if (tickNow < t.tick)
            {
                time = t;
                break;
            }
        if (time == null)
            time = TideSystem.tideTime.get(0);
        String sTime = Util.TickToTime(time.tick) + " " + (time.level > 0 ? "+" : "") + time.level;
        Util.Message(player, MessageManager.msg.SubCommand_Time_NextTide + sTime);
    }

    @Override
    public List<String> onTab(Player player, String[] args)
    {
        return null;
    }
}
