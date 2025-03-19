package nexerelin_outpost_patch;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import exerelin.campaign.ColonyManager;
import exerelin.world.scenarios.ScenarioManager;
import exerelin.campaign.ai.StrategicAI;
import exerelin.campaign.intel.PersonalConfigIntel;
import exerelin.campaign.submarkets.PrismMarket;
import exerelin.plugins.ExerelinCampaignPlugin;
import exerelin.plugins.ExerelinModPlugin;
import exerelin.utilities.ModPluginEventListener;
import exerelin.utilities.versionchecker.VCModPluginCustom;
import lombok.extern.log4j.Log4j;

@Log4j
public class ExerelinModPluginOutpostPatch extends ExerelinModPlugin {

    @Override
    public void onGameLoad(boolean newGame) {
        log.info("Starting ExerelinModPluginOutpostPatch.onGameLoad");
        log.info("New game: " + newGame);

        log.info("Calling NexConfigOutpostPatch.loadSettings");
        NexConfigOutpostPatch.loadSettings();
        
        if (HAVE_LUNALIB) {
            log.info("HAVE_LUNALIB is true, calling LunaConfigHelperOutpostPatch.tryLoadLunaConfigOnGameLoad");
            LunaConfigHelperOutpostPatch.tryLoadLunaConfigOnGameLoad();
        } else {
            log.info("HAVE_LUNALIB is false, skipping Luna config load");
        }

        log.info("Game load");
        isNewGame = newGame;

        log.info("Clearing scenario");
        ScenarioManager.clearScenario();

        log.info("Adding scripts and events if needed");
        addScriptsAndEventsIfNeeded();

        if (!newGame) {
            log.info("Not a new game, running reverse compatibility");
            reverseCompatibility();
        }
        log.info("Refreshing market settings");
        refreshMarketSettings();
        log.info("Refreshing faction settings");
        refreshFactionSettings();

        log.info("Registering ExerelinCampaignPlugin");
        SectorAPI sector = Global.getSector();
        sector.registerPlugin(new ExerelinCampaignPlugin());
        log.info("Adding transient scripts and listeners");
        addTransientScriptsAndListeners(newGame);

        log.info("Clearing PrismMarket submarket cache");
        PrismMarket.clearSubmarketCache();

        log.info("Updating player bonus admins");
        ColonyManager.getManager().updatePlayerBonusAdmins();
        log.info("Updating income");
        ColonyManager.updateIncome();

        if (!HAVE_VERSION_CHECKER && Global.getSettings().getBoolean("nex_enableVersionChecker")) {
            log.info("Running version checker via VCModPluginCustom");
            VCModPluginCustom.onGameLoad(newGame);
        }

        if (NexConfigOutpostPatch.enableStrategicAI) {
            log.info("Strategic AI enabled, adding AIs if needed");
            StrategicAI.addAIsIfNeeded();
        } else {
            log.info("Strategic AI disabled, removing AIs");
            StrategicAI.removeAIs();
        }

        log.info("Running alpha site and sentinel workaround");
        alphaSiteAndSentinelWorkaround();

        log.info("Checking PersonalConfigIntel");
        if (PersonalConfigIntel.get() == null) {
            log.info("PersonalConfigIntel is null, creating it");
            PersonalConfigIntel.create();
        }
        log.info("Adding PersonalConfigIntel listener");
        Global.getSector().getListenerManager().addListener(PersonalConfigIntel.get(), true);

        log.info("Processing markets for faction conditions");
        for (MarketAPI market : Global.getSector().getEconomy().getMarketsCopy()) {
            log.info("Checking market: " + market.getId());
            ColonyManager.getManager().checkFactionMarketCondition(market);
        }

        log.info("Calling other ModPluginEventListeners");
        for (ModPluginEventListener x : Global.getSector().getListenerManager().getListeners(ModPluginEventListener.class)) {
            log.info("Calling onGameLoad for listener: " + x.getClass().getSimpleName());
            x.onGameLoad(newGame);
        }

        log.info("Checking self version");
        checkSelfVersion();

        log.info("Finished ExerelinModPluginOutpostPatch.onGameLoad");
    }
}