NerdAlert
=========

NerdAlert displays notifications for important server events.


Shutdown and Restart Notifications
----------------------------------

The `/nerdalert event` command can display broadcast messages and title
overlays alerting players to an impending server shutdown or restart.
The title overlays include a count down to the event. The count down can be
cancelled. A cancellation message will be broadcast in chat, but not shown as
a title overlay.

NerdAlert does not shut down or restart the server itself. It only displays
notifications about these events on behalf of the server wrapper.


Mark2 Configuration
-------------------

NerdAlert is designed primarily for integration with the
[mark2](https://github.com/gsand/mark2) server wrapper, although it is general
enough to work with any wrapper that supports configurable notifications of
server lifecycle events.

The event names supported in the default configuration are:
 * `warn_restart` - Warn prior to restarting.
 * `warn_stop` - Warn prior to stopping.
 * `cancel_restart` - Cancel restart, with no reason specified.
 * `cancel_restart_reason` - Cancel restart, with a reason specified.
 * `cancel_stop` - Cancel stop, with no reason specified.
 * `cancel_stop_reason` - Cancel stop, with a reason specified.

The corresponding `mark2.properties` settings to use NerdAlert to handle
these events are:
```
plugin.shutdown.alert_command=nerdalert event %s

plugin.shutdown.restart-warn-message=warn_restart {delay}
plugin.shutdown.stop-warn-message=warn_stop {delay}
plugin.shutdown.restart-cancel-message=cancel_restart
plugin.shutdown.restart-cancel-reason=cancel_restart_reason {reason}
plugin.shutdown.stop-cancel-message=cancel_stop
plugin.shutdown.stop-cancel-reason=cancel_stop_reason {reason}
```

These settings can be configured for a specific server by a
`mark2.properties` file in the server directory, or globally for all
servers in `~/.config/mark2/mark2.properties`.


Commands
--------

`/nerdalert reload`
 * Reload the configuration.

`/nerdalert event <event> [...]`
 * `<event>` identifies a server lifecycle event and selects the title string
   and format specifier for broadcast messages.
 * For restart and shutdown events, two arguments after `<event>` are
   expected: a number and a unit (seconds or minutes).
 * If `<event>` begins with "cancel", subsequent arguments, if specified,
   should be the reason for cancellation, to be formatted into broadcasts.


Configuration
-------------
 * `event.*` - Configuration options relating to alerts displayed for server
   lifecycle events.
   * `event.broadcast.show` - If true, messages about restarts, shutdowns
     and cancellations are broadcast.
   * `event.title.*` - Configuration options relating to title overlays.
     * `event.title.show` - If true, title overlays about restarts and
       shutdowns are shown.
     * `event.title.seconds` - Number of seconds to display the final
       count down to 0.
     * `event.title.fade_in_ticks` - Number of ticks the title display
       fades in.
     * `event.title.display_ticks` - Number of ticks the title display
       persists on screen.
     * `event.title.fade_out_ticks` - Number of ticks the title display
       fades out.
   * `event.subtitle.second` - Format string for subtitles showing a
     seconds count of 1.
   * `event.subtitle.seconds` - Format string for subtitles showing a
     seconds count down value of 0 or multiple seconds.
   * `event.subtitle.minute` - Format string for subtitles showing a
     minutes count of 1.
   * `event.subtitle.minutes` - Format string for subtitles showing a
     minutes count down value of 0 or multiple minutes.
   * `event.mesages.*` - Message and title string formatting options.
     * `event.messages.<event>.broadcast` - Java format specifier for
       broadcast messages, including ampersand colour codes.
     * `event.messages.<event>.title` - Title string to display,
       including ampersand colour codes.


Permissions
-----------
 * `nerdalert.admin` - Permission to administer the plugin.
