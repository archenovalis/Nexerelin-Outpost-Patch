package nexerelin_outpost_patch;

import exerelin.ExerelinConstants;
import exerelin.utilities.LunaConfigHelper;
import lombok.extern.log4j.Log4j;
import lunalib.lunaSettings.LunaSettings;

@Log4j
public class LunaConfigHelperOutpostPatch extends LunaConfigHelper {

    public static void initLunaConfig() {
        // Call the original method to set up all existing settings
        LunaConfigHelper.initLunaConfig();

        String tabFleets = getString("tabFleets");
        addSetting("outpostsAreColonies", "boolean", tabFleets, NexConfigOutpostPatch.outpostsAreColonies);

        // Refresh Luna settings to include the new one (already called in super, but safe to ensure)
        LunaSettings.SettingsCreator.refresh(ExerelinConstants.MOD_ID);

        // Reload config and recreate listener (already handled in super, but ensures consistency)
        tryLoadLunaConfig();
        createListener();
    }

    protected static void loadConfigFromLuna() {
        // Call the original method to load all existing settings
        LunaConfigHelper.loadConfigFromLuna();

        // Load new setting
        NexConfigOutpostPatch.outpostsAreColonies = (boolean) loadSetting("outpostsAreColonies", "boolean");
    }

    @Override
    public void settingsChanged(String modId) {
        if (ExerelinConstants.MOD_ID.equals(modId)) {
            loadConfigFromLuna();
        }
    }

    public static LunaConfigHelper createListener() {
        LunaConfigHelper helper = new LunaConfigHelper();
        LunaSettings.addSettingsListener(helper);
        return helper;
    }
}