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
        NexConfigOutpostPatch.loadSettings();
        
        if (HAVE_LUNALIB) {
            LunaConfigHelperOutpostPatch.tryLoadLunaConfigOnGameLoad();
        }

        log.info("Game load");
        isNewGame = newGame;

        ScenarioManager.clearScenario();

        addScriptsAndEventsIfNeeded();

        if (!newGame) reverseCompatibility();
        refreshMarketSettings();
        refreshFactionSettings();

        SectorAPI sector = Global.getSector();
        sector.registerPlugin(new ExerelinCampaignPlugin());
        addTransientScriptsAndListeners(newGame);

        PrismMarket.clearSubmarketCache();

        ColonyManager.getManager().updatePlayerBonusAdmins();
        ColonyManager.updateIncome();

        if (!HAVE_VERSION_CHECKER && Global.getSettings().getBoolean("nex_enableVersionChecker"))
            VCModPluginCustom.onGameLoad(newGame);

        if (NexConfigOutpostPatch.enableStrategicAI) {
            StrategicAI.addAIsIfNeeded();
        } else {
            StrategicAI.removeAIs();
        }

        alphaSiteAndSentinelWorkaround();

        if (PersonalConfigIntel.get() == null) {
            PersonalConfigIntel.create();
        }
        Global.getSector().getListenerManager().addListener(PersonalConfigIntel.get(), true);

        for (MarketAPI market : Global.getSector().getEconomy().getMarketsCopy()) {
            ColonyManager.getManager().checkFactionMarketCondition(market);
        }

        // Call listeners (should be last)
        for (ModPluginEventListener x : Global.getSector().getListenerManager().getListeners(ModPluginEventListener.class)) {
            x.onGameLoad(newGame);
        }

        checkSelfVersion();
    }
}