package nexerelin_outpost_patch;

import com.fs.starfarer.api.Global;

import exerelin.ExerelinConstants;
import exerelin.utilities.NexConfig;
import org.json.JSONObject;

public class NexConfigOutpostPatch extends NexConfig {

    public static boolean outpostsAreColonies = false;

    public static void loadSettings() {

        NexConfig.loadSettings();

        try {
            JSONObject settings = Global.getSettings().getMergedJSONForMod(CONFIG_PATH, ExerelinConstants.MOD_ID);
            outpostsAreColonies = settings.optBoolean("outpostsAreColonies", outpostsAreColonies);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load patched config: " + e.getMessage(), e);
        }
    }

    static {
        loadSettings();
    }
}