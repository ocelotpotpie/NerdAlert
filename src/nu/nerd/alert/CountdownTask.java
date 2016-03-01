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
        _id = -1;
        initialise(title, seconds);
    }

    // ------------------------------------------------------------------------
    /**
     * Revise the display to take into account a new shutdown or restart time.
     *
     * The display is only changed if the new shutdown/restart is earlier. If
     * the down time is cancelled, this task is cancelled and destroyed. For the
     * sake of testing, the countdown can also be revised once it has counted
     * down to 0, as occurs when using "/nerdalert event" interactively.
     *
     * @param title the title string.
     * @param seconds the total countdown duration in seconds.
     */
    public void revise(String title, int seconds) {
        if (seconds < _lastSeconds || _lastSeconds <= 0) {
            initialise(title, seconds);
        }
    }

    // --------------------------------------------------------------------------
    /**
     * Start this task running if it is not currently so.
     *
     * @param plugin the owning Plugin.
     */
    public void start(Plugin plugin) {
        if (_id < 0) {
            _id = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 0, 1);
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
    /**
     * Synchronous task code.
     *
     * The task wakes up every tick during the countdown to ensure that the
     * display is accurate when the server is lagging.
     */
    @Override
    public void run() {
        int elapsedSeconds = (int) (System.currentTimeMillis() + _config.EVENT_TITLE_EARLY_MS - _startTime) / 1000;
        int remaining = _duration - elapsedSeconds;
        if (_lastSeconds != remaining) {
            _lastSeconds = remaining;

            if (remaining == _duration || remaining <= _config.EVENT_TITLE_SECONDS || remaining % 60 == 0) {
                showTitle(_title, formatSubtitle(remaining),
                    _config.EVENT_TITLE_FADE_IN_TICKS,
                    _config.EVENT_TITLE_DISPLAY_TICKS,
                    _config.EVENT_TITLE_FADE_OUT_TICKS);
            }
        }

        if (remaining <= 0) {
            cancel();
        }
    } // run

    // ------------------------------------------------------------------------
    /**
     * Formats the subtitle message as singular or plural minutes or seconds.
     *
     * @param seconds number of seconds.
     */
    protected String formatSubtitle(int seconds) {
        int number;
        String key;
        if (seconds == 0) {
            number = 0;
            key = "now";
        } else if (seconds % 60 == 0) {
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
    protected static void showTitle(String title, String subtitle, int fadeInTicks, int displayTicks, int fadeOutTicks) {
        String cmdTime = String.format("title @a times %d %d %d", fadeInTicks, displayTicks, fadeOutTicks);
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmdTime);
        String cmdTitle = String.format("title @a title {\"text\":\"%s\"}", ChatColor.translateAlternateColorCodes('&', title));
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmdTitle);
        if (subtitle != null) {
            String cmdSub = String.format("title @a subtitle {\"text\":\"%s\"}", ChatColor.translateAlternateColorCodes('&', subtitle));
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmdSub);
        }
    }

    // ------------------------------------------------------------------------
    /**
     * Common initialisation code that sets the mian title, duration of the
     * countdown and records the start real-world time.
     *
     * @param title the title string.
     * @param seconds the total countdown duration in seconds.
     */
    protected void initialise(String title, int seconds) {
        // Force display of title on the first run().
        _lastSeconds = -1;
        _title = title;
        _duration = seconds;
        _startTime = System.currentTimeMillis();
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
     * Remaining time in seconds computed on the last run() call.
     *
     * Titles will only be updated when this value changes (along with the other
     * restrictions on when they are shown).
     */
    protected int _lastSeconds;

    /**
     * Bukkit scheduler ID of this task. Set to -1 as a sentinel on
     * cancellation.
     */
    protected int _id;

    /**
     * Real world countdown start timestamp.
     */
    protected long _startTime;

} // class CountdownTask