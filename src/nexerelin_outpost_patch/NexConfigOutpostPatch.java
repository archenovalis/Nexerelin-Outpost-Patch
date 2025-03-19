package nexerelin_outpost_patch;

import com.fs.starfarer.api.Global;
import exerelin.ExerelinConstants;
import exerelin.utilities.NexConfig;
import org.json.JSONObject;

import lombok.extern.log4j.Log4j;

@Log4j
public class NexConfigOutpostPatch extends NexConfig {

    public static boolean outpostsAreColonies = false;

    public static void loadSettings() {
        log.info("NexConfigOutpostPatch.loadSettings: Starting");
        NexConfig.loadSettings();

        try {
            log.info("NexConfigOutpostPatch.loadSettings: Loading merged JSON from " + CONFIG_PATH);
            JSONObject settings = Global.getSettings().getMergedJSONForMod(CONFIG_PATH, ExerelinConstants.MOD_ID);
            log.info("NexConfigOutpostPatch.loadSettings: Current outpostsAreColonies = " + outpostsAreColonies);
            outpostsAreColonies = settings.optBoolean("outpostsAreColonies", outpostsAreColonies);
            log.info("NexConfigOutpostPatch.loadSettings: Updated outpostsAreColonies = " + outpostsAreColonies);
        } catch (Exception e) {
            log.error("NexConfigOutpostPatch.loadSettings: Failed to load patched config", e);
            throw new RuntimeException("Failed to load patched config: " + e.getMessage(), e);
        }
        log.info("NexConfigOutpostPatch.loadSettings: Completed");
    }

    static {
        log.info("NexConfigOutpostPatch: Static initializer calling loadSettings");
        loadSettings();
    }
}