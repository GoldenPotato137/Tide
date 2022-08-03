package cn.goldenpotato.tide.Util;

import cn.goldenpotato.tide.Tide;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class Util
{
    public static void Message(CommandSender player, String s)
    {
        if (player==null) return;
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
    }

    public static void Message(List<UUID> players, String s)
    {
        for (UUID uuid : players)
        {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) continue;
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
        }
    }

    public static void Title(Player player, String s, int stay)
    {
        player.sendTitle(ChatColor.translateAlternateColorCodes('&', s), "", 0, stay, 0);
    }

    public static void Log(String s)
    {
        Tide.instance.getLogger().info(ChatColor.translateAlternateColorCodes('&', s));
    }

    static ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();

    public static void Command(String command, List<UUID> players)
    {
        if (command.equals("[null]")) return;
        if (command.contains("[player]"))
        {
            for (UUID uuid : players)
            {
                Player player = Bukkit.getPlayer(uuid);
                if (player == null) continue;
                String com = command;
                com = com.replace("[player]", player.getName());
                Bukkit.dispatchCommand(console, com);
            }
        }
        else
            Bukkit.dispatchCommand(console, command);
    }

    public static String TickToTime(long tick)
    {
        long hour = (tick / 1000 + 7) % 24;
        long minute = (int)((float)(tick % 1000) / (1000.0/60));
        return String.format("%02d:%02d", hour, minute);
    }
}