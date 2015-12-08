package nu.nerd.alert;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

// ----------------------------------------------------------------------------
/**
 * Main plugin class.
 *
 * Displays alerts to players.
 */
public class NerdAlert extends JavaPlugin {
    // ------------------------------------------------------------------------
    /**
     * @see org.bukkit.plugin.java.JavaPlugin#onEnable()
     */
    @Override
    public void onEnable() {
        saveDefaultConfig();
        _config = new Configuration(this);
        _config.reload();
    }

    // ------------------------------------------------------------------------
    /**
     * @see org.bukkit.plugin.java.JavaPlugin#onDisable()
     */
    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
    }

    // ------------------------------------------------------------------------
    /**
     * @see org.bukkit.plugin.java.JavaPlugin#onCommand(org.bukkit.command.CommandSender,
     *      org.bukkit.command.Command, java.lang.String, java.lang.String[])
     *
     *      <ul>
     *      <li>/nerdalert reload</li>
     *      <li>/nerdalert event [...]</li>
     *      </ul>
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equals("nerdalert")) {
            if (args.length == 1 && args[0].equals("reload")) {
                _config.reload();
                return true;
            } else if (args.length >= 2 && args[0].equals("event")) {
                return onCommandEvent(args);
            }
        }
        return true;
    }

    // ------------------------------------------------------------------------
    /**
     * Handle /nerdalert event subcommands.
     *
     * <ul>
     * <li>/nerdalert event <key> [<number> <unit>]</li>
     * <li>/nerdalert event <key> [<reason>...]</li>
     * </ul>
     *
     * @param args command argument strings; args.length >= 2.
     */
    protected boolean onCommandEvent(String[] args) {
        String event = args[1];
        int seconds = 0;
        if (args.length == 4) {
            try {
                int number = Integer.parseInt(args[2]);
                String units = args[3];
                if (units.toLowerCase().startsWith("second")) {
                    seconds = number;
                } else if (units.toLowerCase().startsWith("minute")) {
                    seconds = number * 60;
                } else if (units.toLowerCase().startsWith("hour")) {
                    seconds = number * 60 * 60;
                }
            } catch (NumberFormatException ex) {
                getLogger().warning("Passed a weird number argument: " + args[2]);
            }
        }

        if (_config.EVENT_BROADCAST_SHOW) {
            String format = getConfig().getString("event.messages." + event + ".broadcast", event);
            Object[] formatArgs = Arrays.copyOfRange(args, 2, args.length);
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', String.format(format, formatArgs)));
        }

        if (event.toLowerCase().startsWith("cancel")) {
            cancelCountdown();
        } else if (_config.EVENT_TITLE_SHOW) {
            String title = getConfig().getString("event.messages." + event + ".title", event);
            showCountdown(title, seconds);
        }
        return true;
    } // onCommandEvent

    // ------------------------------------------------------------------------
    /**
     * Start the countdown title display task, or adjust it if it has started.
     *
     * @param title the title string.
     * @param seconds the total number of seconds of the countdown.
     */
    protected void showCountdown(final String title, final int seconds) {
        if (_countdownTask == null) {
            _countdownTask = new CountdownTask(_config, title, seconds);
            _countdownTask.start(this);
        } else {
            _countdownTask.revise(title, seconds);
        }
    }

    // ------------------------------------------------------------------------
    /**
     * Cancel the countdown title display task.
     */
    protected void cancelCountdown() {
        if (_countdownTask != null) {
            _countdownTask.cancel();
            _countdownTask = null;
        }
    }

    // ------------------------------------------------------------------------
    /**
     * Scheduler ID of the task that shows shutdown-related messages as titles.
     */
    protected CountdownTask _countdownTask;

    /**
     * Configuration.
     */
    protected Configuration _config;
} // class NerdAlert