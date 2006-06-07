import java.io.File;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.platonos.pluginengine.Plugin;
import org.platonos.pluginengine.PluginEngine;
import org.platonos.pluginengine.logging.LoggerLevel;

/**
 * Starts a plugin shell for a Swing application.
 */
public class Launcher {

    /**
     * A property specifying a location of the plugins directory. It can be a comma
     * separated list of directories. If not set, {@link #PLUGINS_DIR_DEFAULT} directory
     * is used.
     */
    public static final String PLUGINS_DIR_PROPERTY = "platonos.plugins.dirs";
    public static final String PLUGINS_DIR_DEFAULT = "plugins";

    /**
     * A property specifying the name of the PluginEngine (same as the name of the
     * application). If not set, {@link #ENGINE_NAME_DEFAULT} directory is used.
     */
    public static final String ENGINE_NAME_PROPERTY = "platonos.engine.name";
    public static final String ENGINE_NAME_DEFAULT = "Platonos Plugin Engine";

    public static void main(String[] args) {

        String engineName = System.getProperty(ENGINE_NAME_PROPERTY);
        if (engineName == null) {
            engineName = ENGINE_NAME_DEFAULT;
        }

        PluginEngine pluginEngine = new PluginEngine(engineName);
        pluginEngine.setAttribute(PluginEngine.COMMAND_LINE_ATTRIBUTE, args);

        // load plugins...
        String pluginDirectories = System.getProperty(PLUGINS_DIR_PROPERTY);

        // try "../plugins"
        if (pluginDirectories == null) {

            // try relative to current dir
            File defaultDir = new File(PLUGINS_DIR_DEFAULT);

            // try relative to parent dir
            if (!defaultDir.isDirectory()) {
                File parent = new File(System.getProperty("user.dir")).getParentFile();
                if (parent != null) {
                    defaultDir = new File(parent, PLUGINS_DIR_DEFAULT);
                }
            }

            if (defaultDir.isDirectory()) {
                pluginDirectories = defaultDir.getAbsolutePath();
            }
        }

        pluginEngine.getLogger().log(
                LoggerLevel.INFO,
                "Plugins directory: " + pluginDirectories,
                null);

        if (pluginDirectories != null) {
            StringTokenizer toks = new StringTokenizer(pluginDirectories, ",");
            while (toks.hasMoreTokens()) {
                pluginEngine.loadPlugins(toks.nextToken());
            }
        }

        pluginEngine.start();

        boolean hasStartedPlugins = false;
        Iterator it = pluginEngine.getPlugins().iterator();
        while (it.hasNext()) {
            Plugin p = (Plugin) it.next();
            if (p.isStarted()) {
                hasStartedPlugins = true;
                break;
            }
        }

        // either no plugins configured, all all of them failed to start.
        if (!hasStartedPlugins) {
            pluginEngine.getLogger().log(
                    LoggerLevel.INFO,
                    "No plugins started, exiting",
                    null);

            // must explicitly kill all UI threads
            System.exit(0);
        }
    }
}
