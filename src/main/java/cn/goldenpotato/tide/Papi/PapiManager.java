package cn.goldenpotato.tide.Papi;

import cn.goldenpotato.tide.Util.Util;
import cn.goldenpotato.tide.Water.TideSystem;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public class PapiManager extends PlaceholderExpansion
{
    @Override
    public @NotNull String getIdentifier()
    {
        return "tide";
    }

    @Override
    public @NotNull String getAuthor()
    {
        return "GoldenPotato137";
    }

    @Override
    public @NotNull String getVersion()
    {
        return "2.1";
    }

    @Override
    public boolean persist()
    {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params)
    {
        if(player==null || player.getPlayer()==null) return null;

        World world = player.getPlayer().getWorld();
        long tickNow = world.getTime();
        switch (params)
        {
            case "level": //当前潮汐海平面偏移量
                return String.valueOf(TideSystem.SeaLevel(world));
            case "nextLevel": //下一次潮汐海平面偏移量
                return String.valueOf(TideSystem.NextTide(tickNow).level);
            case "nextTime": //下一次潮汐时间
                return Util.TickToTime(TideSystem.NextTide(tickNow).tick);
            case "nextTick": //下一次潮汐tick
                return String.valueOf(TideSystem.NextTide(tickNow).tick);
            case "nextTickCountDown": //距离下一次潮汐还有多少tick
                return String.valueOf(TideSystem.NextTideCD(tickNow));
            case "time": //当前时间
                return Util.TickToTime(tickNow);
        }
        return null;
    }
}
