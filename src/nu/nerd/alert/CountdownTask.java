package nu.nerd.alert;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

// ----------------------------------------------------------------------------
/**
 * The task that shows the countdown using the /title command.
 */
public class CountdownTask implements Runnable {
    // ------------------------------------------------------------------------
    /**
     * Constructor.
     *
     * @param config the configuration.
     * @param title the title string.
     * @param seconds the total countdown duration in seconds.
     */
    public CountdownTask(Configuration config, String title, int seconds) {
        _config = config;
        _title = title;
        _remaining = _duration = seconds;
        _id = -1;
    }

    // ------------------------------------------------------------------------
    /**
     * Revise the display to take into account a new shutdown or restart time.
     *
     * The display is only changed if the new shutdown/restart is earlier. If
     * the down time is cancelled, this task is cancelled and destroyed.
     *
     * @param title the title string.
     * @param seconds the total countdown duration in seconds.
     */
    public void revise(String title, int seconds) {
        if (seconds < _remaining) {
            _title = title;
            _remaining = _duration = seconds;
        }
    }

    // --------------------------------------------------------------------------
    /**
     * Start this task running.
     *
     * @param plugin the owning Plugin.
     */
    public void start(Plugin plugin) {
        if (_id < 0) {
            _id = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 0, 20);
        }
    }

    // ------------------------------------------------------------------------
    /**
     * Cancel this task.
     */
    public void cancel() {
        if (_id >= 0) {
            Bukkit.getScheduler().cancelTask(_id);
            _id = -1;
        }
    }

    // ------------------------------------------------------------------------

    @Override
    public void run() {
        if (_remaining == _duration || _remaining <= _config.EVENT_TITLE_SECONDS || _remaining % 60 == 0) {
            showTitle(_title, formatSubtitle(_remaining),
                _config.EVENT_TITLE_FADE_IN_TICKS,
                _config.EVENT_TITLE_DISPLAY_TICKS,
                _config.EVENT_TITLE_FADE_OUT_TICKS);
        }
        --_remaining;
        if (_remaining < 0) {
            cancel();
        }
    }

    // ------------------------------------------------------------------------
    /**
     * Formats the subtitle message as singular or plural minutes or seconds.
     *
     * @param seconds number of seconds.
     */
    protected String formatSubtitle(int seconds) {
        int number;
        String key;
        if (seconds > 0 && seconds % 60 == 0) {
            number = seconds / 60;
            key = (number == 1) ? "minute" : "minutes";
        } else {
            number = seconds;
            key = (number == 1) ? "second" : "seconds";
        }
        String format = _config.getConfig().getString("event.subtitle." + key);
        return String.format(ChatColor.translateAlternateColorCodes('&', format), number);
    }

    // ------------------------------------------------------------------------
    /**
     * Show a title and subtitle using /title.
     *
     * @param title the title.
     * @param subtitle the subtitle.
     * @param fadeInTicks the number of ticks to fade in.
     * @param displayTicks the number of ticks to display.
     * @param fadeOutTicks the number of ticks to fade out.
     */
    protected void showTitle(String title, String subtitle, int fadeInTicks, int displayTicks, int fadeOutTicks) {
        String cmdTime = String.format("title @a times %d %d %d", fadeInTicks, displayTicks, fadeOutTicks);
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmdTime);
        String cmdTitle = String.format("title @a title {text:\"%s\"}", ChatColor.translateAlternateColorCodes('&', title));
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmdTitle);
        if (subtitle != null) {
            String cmdSub = String.format("title @a subtitle {text:\"%s\"}", ChatColor.translateAlternateColorCodes('&', subtitle));
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmdSub);
        }
    }

    // ------------------------------------------------------------------------
    /**
     * Configuration.
     */
    protected Configuration _config;

    /**
     * Title string.
     */
    protected String _title;

    /**
     * Total countdown duration in seconds.
     */
    protected int _duration;

    /**
     * Remaining countdown duration in seconds.
     */
    protected int _remaining;

    /**
     * Bukkit scheduler ID of this task. Set to -1 as a sentinel on
     * cancellation.
     */
    protected int _id;
} // class CountdownTask