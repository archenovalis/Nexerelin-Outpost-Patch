package nexerelin_outpost_patch;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;

import lombok.extern.log4j.Log4j;

@Log4j
public class ExerelinModPluginOutpostPatch extends BaseModPlugin {
    public static final boolean HAVE_LUNALIB = Global.getSettings().getModManager().isModEnabled("lunalib");

    @Override
    public void onGameLoad(boolean newGame) {
        log.info("Starting ExerelinModPluginOutpostPatch.onGameLoad");

        NexConfigOutpostPatch.loadSettings();
        log.info("Loaded NexConfigOutpostPatch.loadSettings");
        
        if (HAVE_LUNALIB) {
            log.info("HAVE_LUNALIB is true, calling LunaConfigHelperOutpostPatch.tryLoadLunaConfigOnGameLoad");
            LunaConfigHelperOutpostPatch.tryLoadLunaConfigOnGameLoad();
        } else {
            log.info("HAVE_LUNALIB is false, skipping Luna config load");
        }

        log.info("Finished ExerelinModPluginOutpostPatch.onGameLoad");
    }
}