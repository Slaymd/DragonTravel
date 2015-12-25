package eu.phiwa.dragontravel.core.filehandlers;

import eu.phiwa.dragontravel.core.DragonTravel;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.util.logging.Level;

public class Messages {

    // Messages
    private FileConfiguration messages;
    private File messagesFile;
    private final double messagesVersion = 0.8;

    private String language = "";

    public Messages() {
        loadMessages();
    }

    private void loadMessages() {

        language = DragonTravel.getInstance().getConfig().getString("Language");

        if (language == null) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not load messages-file because the language could not be read from the config! Disabling plugin!");
            Bukkit.getPluginManager().disablePlugin(DragonTravel.getInstance());
            return;
        }

        messagesFile = new File(DragonTravel.getInstance().getDataFolder(), "messages-" + language + ".yml");

        if (!messagesFile.exists())
            create();

        messages = YamlConfiguration.loadConfiguration(messagesFile);
        updateConfig();
    }

    private void create() {
        if (messagesFile.exists())
            return;
        try {
            messagesFile.createNewFile();
            copy(DragonTravel.getInstance().getResource("messages/messages-" + language + ".yml"), messagesFile);
            Bukkit.getLogger().log(Level.INFO, "Created messages file.");
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not create the languages file - check the language!");
            e.printStackTrace();
        }
    }

    private void updateConfig() {

        if (messages.getDouble("File.Version") != messagesVersion)
            newlyRequiredMessages();

        noLongerRequiredMessages();

        // Refresh file and config variables for persistence.
        try {
            messagesFile = new File(DragonTravel.getInstance().getDataFolder(), "messages-" + language + ".yml");
            messages.save(messagesFile);
            messages = YamlConfiguration.loadConfiguration(messagesFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void copy(InputStream in, File file) {

        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) != -1)
                out.write(buf, 0, len);
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void newlyRequiredMessages() {

    	Bukkit.getLogger().log(Level.INFO, "[DragonTravel] Updating messages file to " + messagesVersion);

        // old
        if (messages.get("Messages.Flights.Error.OnlySigns") == null)
            messages.set("Messages.Flights.Error.OnlySigns", "&cThis command has been disabled by the admin, you can only use flights using signs.");
        if (messages.get("Messages.Stations.Error.NotCreateStationWithRandomstatName") == null)
            messages.set("Messages.Stations.Error.NotCreateStationWithRandomstatName", "&cYou cannot create a staion with the name of the RandomDest.");
        if (messages.get("Messages.Factions.Error.NotYourFaction") == null)
            messages.set("Messages.Factions.Error.NotYourFaction", ": &cThis is not your faction.");
        if(messages.get("Messages.Factions.Error.FactionsNotInstalled") == null)
        	messages.set("Messages.Factions.Error.FactionsNotInstalled", ": &cFactions is not installed");
        
        // 0.5
        if (messages.get("Messages.General.Error.BelowMinMountHeight") == null)
            messages.set("Messages.General.Error.BelowMinMountHeight", "&cYou are below the minimum height required to mount a dragon. Minimum height is &f{minheight}&c.");
        if (messages.get("Messages.General.Error.DamageCooldown") == null)
            messages.set("Messages.General.Error.DamageCooldown", "&cYou must wait &f{seconds} &cmore seconds before you can mount a dragon.");

        //0.6
        if (messages.get("Messages.General.Error.StatDragonExists") == null)
            messages.set("Messages.General.Error.StatDragonExists", ": &cThis name is already taken.");
        if (messages.get("Messages.General.Error.StatDragonNotExists") == null)
            messages.set("Messages.General.Error.StatDragonNotExists", ": &cThis name is not recognised.");
        if (messages.get("Messages.General.Error.StatDragonCmdRevised") == null)
            messages.set("Messages.General.Error.StatDragonCmdRevised", ": &cThis command now takes a parameter - you must include a name. Check help page 5 for more details.");
        if (messages.get("Messages.Travels.Successful.TravellingToPlayer") == null)
            messages.set("Messages.Travels.Successful.TravellingToPlayer", "&aTravelling to &f{playername}&a.");
        if (messages.get("Messages.Travels.Successful.TravellingToFactionHome") == null)
            messages.set("Messages.Travels.Successful.TravellingToFactionHome", "&aTravelling to the faction home.");
        if (messages.get("Messages.Travels.Successful.TravellingToTownSpawn") == null)
            messages.set("Messages.Travels.Successful.TravellingToTownSpawn", "&aTravelling to the town spawn.");
        if (messages.get("Messages.General.Error.RequireSkyLight") == null)
            messages.set("Messages.General.Error.RequireSkyLight", "&cYou must have access to sky light!");
        if (messages.get("Messages.General.Error.NoConsole") == null)
            messages.set("Messages.General.Error.NoConsole", "&cThe console may not use this command in that way.");
        if (messages.get("Messages.General.Error.NameTaken") == null)
            messages.set("Messages.General.Error.NameTaken", "&cThat name is already taken.");
        if (messages.get("Messages.Payment.Free") == null)
            messages.set("Messages.Payment.Free", "&aNo charge for you, hop on!");

        //0.7 skipped due to merge problems to ensure compatibility
        
        //0.8
        if (messages.get("Messages.Towny.Error.NoTown") == null)
            messages.set("Messages.Towny.Error.NoTown", ": &cYou do not have a town.");
        if(messages.get("Messages.Towny.Error.TownyNotInstalled") == null)
        	messages.set("Messages.Towny.Error.TownyNotInstalled", ": &cTowny is not installed");
        
        
        // Update the file version
        messages.set("File.Version", messagesVersion);
    }


    private void noLongerRequiredMessages() {
        // DragonTravel.config.set("example key", null);
    }

    public String getMessage(String path) {
        String message;
        message = replaceColors(messages.getString(path));
        if (message == null) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not find the message looking for at path '" + path + "' which leads to a serious problem! Be try to generate a new language file if you previously updated DragonTravel!");
            return replaceColors("&cAn error occured, please contact the admin! Missing message '" + path + "'");
        }
        if (message.length() == 0)
            return ChatColor.RED + "Error, could not read message-text from file, please contact the admin.";

        return message;
    }

    private String replaceColors(String string) {

        String formattedMessage = null;

        try {
            formattedMessage = string.replaceAll("(?i)&([a-f0-9])", "\u00A7$1");
        } catch (Exception ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not read a message from the messages-xx.yml!");
        }

        return formattedMessage;
    }
}
