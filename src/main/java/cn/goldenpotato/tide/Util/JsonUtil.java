package cn.goldenpotato.tide.Util;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class JsonUtil
{
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static JsonArray LoadJsonArray(String FileName, File folder)
    {
        try
        {
            if (!folder.exists())
                folder.mkdirs();
            File f = new File(folder, FileName);
            if (!f.exists()) f.createNewFile();
            Reader reader = new InputStreamReader(Files.newInputStream(f.toPath()), StandardCharsets.UTF_8);
            int ch = 0;
            StringBuilder sb = new StringBuilder();
            while ((ch = reader.read()) != -1)
                sb.append((char) ch);
            reader.close();
            String jsonStr = sb.toString();
            JsonArray jo;
            try
            {
                jo = new JsonParser().parse(jsonStr).getAsJsonArray();
            }
            catch (IllegalStateException e)
            {
                jo = new JsonArray();
            }
            return jo;
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void SaveJson(String fileName,File folder, JsonArray jsonObject)
    {
        try
        {
            GsonBuilder gb = new GsonBuilder();
            gb.setPrettyPrinting();
            String jsonString = gb.create().toJson(jsonObject);
            if (!folder.exists())
                folder.mkdirs();
            File f = new File(folder, fileName);
            if (!f.exists()) f.createNewFile();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(Files.newOutputStream(f.toPath()));
            outputStreamWriter.write(jsonString);
            outputStreamWriter.close();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
