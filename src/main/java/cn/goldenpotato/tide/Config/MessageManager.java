package cn.goldenpotato.tide.Config;

import cn.goldenpotato.tide.Tide;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class MessageManager
{
    public static Message msg;

    private static FileConfiguration localeFileManager; //实际的本地化文件
    private static FileConfiguration messageLocaleFileManager; //翻译过后的本地化文件
    private static FileConfiguration messageFileManager; //本地化文件的原版（Chinglish）

    /**
     * 按照locale -> message_xx-xx -> message的优先级加载本地化文件字段,并将其存储在locale中
     * @param path 字段路径
     * @return 字段内容
     */
    private static String GetString(String path)
    {
        String result = localeFileManager.getString(path);
        if(result == null)
        {
            result = messageLocaleFileManager.getString(path);
            if(result == null)
            {
                result = messageFileManager.getString(path);
            }
        }
        localeFileManager.set(path, result);
        return result;
    }

    public static void LoadMessage()
    {
        msg = new Message();
        File localeFile = new File(Tide.instance.getDataFolder(), "locale.yml");
        File messageLocaleFile = new File(Tide.instance.getDataFolder(), "message_"+ConfigManager.config.language +".yml");
        File messageFile = new File(Tide.instance.getDataFolder(), "message.yml");

        Tide.instance.saveResource("message_"+ConfigManager.config.language +".yml",false); //临时保存翻译后的本地化文件原版
        if(!localeFile.exists()) //如果locale文件不存在，则创建一个
        {
            try
            {
                if(!localeFile.createNewFile())
                    throw new IOException("Failed to create locale.yml");
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
        Tide.instance.saveResource("message.yml",false); //临时保存一份Chinglish原版

        localeFileManager = YamlConfiguration.loadConfiguration(localeFile);
        messageLocaleFileManager = YamlConfiguration.loadConfiguration(messageLocaleFile);
        messageFileManager = YamlConfiguration.loadConfiguration(messageFile);

        Load(); //加载本地化文件

        //删除临时保存的文件并保存合成后的locale
        try
        {
            localeFileManager.save(localeFile);
            if(!messageFile.delete() || !messageLocaleFile.delete())
            {
                Tide.instance.getLogger().warning("Failed to delete temporary files!");
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * 加载本地化文件
     */
    private static void Load()
    {
        msg.NoPermission = GetString("NoPermission");
        msg.NoCommandInConsole =  GetString("NoCommandInConsole");
        msg.WrongNum = GetString("WrongNum");
        msg.Success = GetString("Success");
        msg.SubCommand_Add_Usage = GetString("SubCommand_Add_Usage");
        msg.SubCommand_Attach_Usage = GetString("SubCommand_Attach_Usage");
        msg.SubCommand_Attach_Already = GetString("SubCommand_Attach_Already");
        msg.SubCommand_Help_Usage = GetString("SubCommand_Help_Usage");
        msg.SubCommand_Reload_Usage = GetString("SubCommand_Reload_Usage");
        msg.SubCommand_Save_Usage = GetString("SubCommand_Save_Usage");
        msg.SubCommand_Status_TideTable = GetString("SubCommand_Status_TideTable");
        msg.SubCommand_Time_Usage = GetString("SubCommand_Time_Usage");
        msg.SubCommand_Time = GetString("SubCommand_Time");
        msg.SubCommand_Time_NextTide = GetString("SubCommand_Time_NextTide");
    }
}