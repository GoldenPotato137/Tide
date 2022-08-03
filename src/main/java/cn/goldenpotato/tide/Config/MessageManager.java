package cn.goldenpotato.tide.Config;

import cn.goldenpotato.tide.Tide;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class MessageManager
{
    public static Message msg;
    public static void LoadMessage()
    {
        msg = new Message();
        Tide.instance.saveResource("message_"+ConfigManager.config.language+".yml",true);
        File messageFile = new File(Tide.instance.getDataFolder(), "message_"+ConfigManager.config.language+".yml");
        FileConfiguration messageReader = YamlConfiguration.loadConfiguration(messageFile);

        msg.NoPermission = messageReader.getString("NoPermission","");
        msg.NoCommandInConsole =  messageReader.getString("NoCommandInConsole","");
        msg.WrongNum = messageReader.getString("WrongNum","");
        msg.Success = messageReader.getString("Success","");
        msg.SubCommand_Add_Usage = messageReader.getString("SubCommand_Add_Usage","");
        msg.SubCommand_Attach_Usage = messageReader.getString("SubCommand_Attach_Usage","");
        msg.SubCommand_Attach_Already = messageReader.getString("SubCommand_Attach_Already","");
        msg.SubCommand_Help_Usage = messageReader.getString("SubCommand_Help_Usage","");
        msg.SubCommand_Reload_Usage = messageReader.getString("SubCommand_Reload_Usage","");
        msg.SubCommand_Save_Usage = messageReader.getString("SubCommand_Save_Usage","");
        msg.SubCommand_Status_TideTable = messageReader.getString("SubCommand_Status_TideTable","");
        msg.SubCommand_Time_Usage = messageReader.getString("SubCommand_Time_Usage","");
        msg.SubCommand_Time = messageReader.getString("SubCommand_Time","");
        msg.SubCommand_Time_NextTide = messageReader.getString("SubCommand_Time_NextTide","");
    }
}