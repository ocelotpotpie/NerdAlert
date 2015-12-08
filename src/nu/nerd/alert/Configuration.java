package nu.nerd.alert;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

// --------------------------------------------------------------------------
/**
 * Access to the plugin configuration.
 */
public class Configuration {
    public boolean EVENT_BROADCAST_SHOW;
    public boolean EVENT_TITLE_SHOW;
    public int EVENT_TITLE_SECONDS;
    public int EVENT_TITLE_FADE_IN_TICKS;
    public int EVENT_TITLE_DISPLAY_TICKS;
    public int EVENT_TITLE_FADE_OUT_TICKS;

    // ------------------------------------------------------------------------
    /**
     * Constructor.
     *
     * @param plugin the owning plugin.
     */
    public Configuration(Plugin plugin) {
        _plugin = plugin;
    }

    // ------------------------------------------------------------------------
    /**
     * Return the raw configuration.
     *
     * @return the FileConfiguration.
     */
    public FileConfiguration getConfig() {
        return _plugin.getConfig();
    }

    // ------------------------------------------------------------------------
    /**
     * Reload the configuration.
     */
    public void reload() {
        _plugin.reloadConfig();
        EVENT_BROADCAST_SHOW = getConfig().getBoolean("event.broadcast.show");
        EVENT_TITLE_SHOW = getConfig().getBoolean("event.title.show");
        EVENT_TITLE_SECONDS = getConfig().getInt("event.title.seconds");
        EVENT_TITLE_FADE_IN_TICKS = getConfig().getInt("event.title.fade_in_ticks");
        EVENT_TITLE_DISPLAY_TICKS = getConfig().getInt("event.title.display_ticks");
        EVENT_TITLE_FADE_OUT_TICKS = getConfig().getInt("event.title.fade_out_ticks");
    }

    // ------------------------------------------------------------------------
    /**
     * The owning plugin.
     */
    protected final Plugin _plugin;
} // class Configuration