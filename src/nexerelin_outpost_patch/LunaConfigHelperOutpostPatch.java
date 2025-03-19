package nexerelin_outpost_patch;

import exerelin.ExerelinConstants;
import exerelin.utilities.StringHelper;
import lombok.extern.log4j.Log4j;
import lunalib.lunaSettings.LunaSettings;
import lunalib.lunaSettings.LunaSettingsListener;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Log4j
public class LunaConfigHelperOutpostPatch implements LunaSettingsListener {

    public static final String PREFIX = "nex_";

    @Deprecated public static final List<String> DEFAULT_TAGS = new ArrayList<>();  // we don't use tags no more
    static {
        DEFAULT_TAGS.add("spacing:0.5");
    }

    // runcode exerelin.utilities.LunaConfigHelper.initLunaConfig()
    public static void initLunaConfig() {
        String mid = ExerelinConstants.MOD_ID;
        //List<String> tags = DEFAULT_TAGS;

        String tabFleets = getString("tabFleets");

        addHeader("ui", null);
        addHeader("otherFleets", tabFleets);
        addSetting("outpostsAreColonies", "boolean", tabFleets, NexConfigOutpostPatch.outpostsAreColonies);

        LunaSettings.SettingsCreator.refresh(mid);

        tryLoadLunaConfig();

        createListener();
    }

    public static void tryLoadLunaConfig() {
        try {
            loadConfigFromLuna();
        } catch (NullPointerException npe) {
            // config not created yet I guess, do nothing
        }
    }

    /**
     * Loads only the settings that are save-specific
     */
    public static void tryLoadLunaConfigOnGameLoad() {
        try {
            loadConfigFromLunaOnGameLoad();
        } catch (NullPointerException npe) {
            // config not created yet I guess, do nothing
        }
    }

    protected static void loadConfigFromLuna() {
        NexConfigOutpostPatch.outpostsAreColonies = (boolean) loadSetting("outpostsAreColonies", "boolean");
    }

    protected static void loadConfigFromLunaOnGameLoad() {
        //DiplomacyManager.setBaseInterval((float)loadSetting("diplomacyInterval", "float"));
    }

    public static Object loadSetting(String var, String type) {
        String mid = ExerelinConstants.MOD_ID;
        var = PREFIX + var;
        switch (type) {
            case "bool":
            case "boolean":
                return LunaSettings.getBoolean(mid, var);
            case "int":
            case "integer":
            case "key":
                return LunaSettings.getInt(mid, var);
            case "float":
                return (float)(double)LunaSettings.getDouble(mid, var);
            case "double":
                return LunaSettings.getDouble(mid, var);
            default:
                log.error(String.format("Setting %s has invalid type %s", var, type));
        }
        return null;
    }

    public static void addSetting(String var, String type, Object defaultVal) {
        addSetting(var, type, null, defaultVal, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public static void addSetting(String var, String type, @Nullable String tab, Object defaultVal) {
        addSetting(var, type, tab, defaultVal, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public static void addSetting(String var, String type, Object defaultVal, double min, double max) {
        addSetting(var, type, null, defaultVal, min, max);
    }

    public static void addSetting(String var, String type, @Nullable String tab, Object defaultVal, double min, double max) {
        String tooltip = getString("tooltip_" + var);
        if (tooltip.startsWith("Missing string:")) {
            tooltip = "";
        }
        String mid = ExerelinConstants.MOD_ID;
        String name = getString("name_" + var);

        if (tab == null) tab = "";

        Double defaultAsDouble = objectToDouble(defaultVal);
        if (defaultAsDouble != null) {
            log.info("Trying default " + defaultAsDouble + " for setting " + var);
            if (defaultAsDouble < min) min = defaultAsDouble;
            else if (defaultAsDouble > max) max = defaultAsDouble;
        }

        var = PREFIX + var;

        switch (type) {
            case "boolean":
                LunaSettings.SettingsCreator.addBoolean(mid, var, name, tooltip, (boolean)defaultVal, tab);
                break;
            case "int":
            case "integer":
                if (defaultVal instanceof Float) {
                    defaultVal = Math.round((float)defaultVal);
                }
                LunaSettings.SettingsCreator.addInt(mid, var, name, tooltip,
                        (int)defaultVal, (int)Math.round(min), (int)Math.round(max), tab);
                break;
            case "float":
                // fix float -> double conversion causing an unround number
                String floatStr = ((Float)defaultVal).toString();
                LunaSettings.SettingsCreator.addDouble(mid, var, name, tooltip,
                        Double.parseDouble(floatStr), min, max, tab);
                break;
            case "double":
                LunaSettings.SettingsCreator.addDouble(mid, var, name, tooltip,
                        (double)defaultVal, min, max, tab);
                break;
            case "key":
                LunaSettings.SettingsCreator.addKeybind(mid, var, name, tooltip, (int)defaultVal, tab);
                break;
            default:
                log.error(String.format("Setting %s has invalid type %s", var, type));
        }
    }

    public static void addHeader(String id, String tab) {
        addHeader(id, getString("header_" + id), tab);
    }

    public static void addHeader(String id, String title, String tab) {
        if (tab == null) tab = "";
        LunaSettings.SettingsCreator.addHeader(ExerelinConstants.MOD_ID, id, title, tab);
    }

    public static Double objectToDouble(Object obj) {
        if (obj instanceof Double) return (double)obj;
        if (obj instanceof Float) return (double)(float)obj;
        if (obj instanceof Integer) return (double)(int)obj;
        if (obj instanceof Long) return (double)(long)obj;
        return null;
    }

    public static LunaConfigHelperOutpostPatch createListener() {
        LunaConfigHelperOutpostPatch helper = new LunaConfigHelperOutpostPatch();
        LunaSettings.addSettingsListener(helper);
        return helper;
    }

    @Override
    public void settingsChanged(String modId) {
        if (ExerelinConstants.MOD_ID.equals(modId)) {
            loadConfigFromLuna();
        }
    }

    public static String getString(String id) {
        return StringHelper.getString("nex_lunaSettings", id);
    }
}
