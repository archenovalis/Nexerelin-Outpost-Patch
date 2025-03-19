package nexerelin_outpost_patch;

import exerelin.ExerelinConstants;
import exerelin.utilities.LunaConfigHelper;
import lombok.extern.log4j.Log4j;
import lunalib.lunaSettings.LunaSettings;

@Log4j
public class LunaConfigHelperOutpostPatch extends LunaConfigHelper {

    public static void initLunaConfig() {
        log.info("LunaConfigHelperOutpostPatch.initLunaConfig: Starting");
        LunaConfigHelper.initLunaConfig();

        String tabFleets = getString("tabFleets");
        log.info("LunaConfigHelperOutpostPatch.initLunaConfig: Adding setting 'outpostsAreColonies' to tab " + tabFleets);
        addSetting("outpostsAreColonies", "boolean", tabFleets, NexConfigOutpostPatch.outpostsAreColonies);

        log.info("LunaConfigHelperOutpostPatch.initLunaConfig: Refreshing Luna settings for " + ExerelinConstants.MOD_ID);
        LunaSettings.SettingsCreator.refresh(ExerelinConstants.MOD_ID);

        log.info("LunaConfigHelperOutpostPatch.initLunaConfig: Reloading config from Luna");
        tryLoadLunaConfig();
        log.info("LunaConfigHelperOutpostPatch.initLunaConfig: Creating listener");
        createListener();

        log.info("LunaConfigHelperOutpostPatch.initLunaConfig: Completed");
    }

    protected static void loadConfigFromLuna() {
        log.info("LunaConfigHelperOutpostPatch.loadConfigFromLuna: Starting");
        LunaConfigHelper.loadConfigFromLuna();

        log.info("LunaConfigHelperOutpostPatch.loadConfigFromLuna: Loading setting 'outpostsAreColonies'");
        NexConfigOutpostPatch.outpostsAreColonies = (boolean) loadSetting("outpostsAreColonies", "boolean");
        log.info("LunaConfigHelperOutpostPatch.loadConfigFromLuna: outpostsAreColonies set to " + NexConfigOutpostPatch.outpostsAreColonies);

        log.info("LunaConfigHelperOutpostPatch.loadConfigFromLuna: Completed");
    }

    public static void tryLoadLunaConfigOnGameLoad() {
        log.info("LunaConfigHelperOutpostPatch.tryLoadLunaConfigOnGameLoad: Starting");
        try {
            initLunaConfig();
            log.info("LunaConfigHelperOutpostPatch.tryLoadLunaConfigOnGameLoad: Luna config initialized");
        } catch (Exception e) {
            log.error("LunaConfigHelperOutpostPatch.tryLoadLunaConfigOnGameLoad: Failed to initialize Luna config", e);
            throw e;
        }
        log.info("LunaConfigHelperOutpostPatch.tryLoadLunaConfigOnGameLoad: Completed");
    }

    @Override
    public void settingsChanged(String modId) {
        log.info("LunaConfigHelperOutpostPatch.settingsChanged: Called with modId " + modId);
        if (ExerelinConstants.MOD_ID.equals(modId)) {
            log.info("LunaConfigHelperOutpostPatch.settingsChanged: modId matches, loading config from Luna");
            loadConfigFromLuna();
        } else {
            log.info("LunaConfigHelperOutpostPatch.settingsChanged: modId does not match, skipping");
        }
    }

    public static LunaConfigHelper createListener() {
        log.info("LunaConfigHelperOutpostPatch.createListener: Creating new listener");
        LunaConfigHelper helper = new LunaConfigHelperOutpostPatch();
        LunaSettings.addSettingsListener(helper);
        log.info("LunaConfigHelperOutpostPatch.createListener: Listener added");
        return helper;
    }
}