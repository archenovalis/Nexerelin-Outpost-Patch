package nexerelin_outpost_patch;

import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.intel.deciv.DecivTracker;
import exerelin.campaign.intel.PlayerOutpostIntel;

public class PlayerOutpostIntelOutpostPatch extends PlayerOutpostIntel {

    @Override
    public SectorEntityToken createOutpost(CampaignFleetAPI fleet, SectorEntityToken target) {
        // Call the original method to get the default outpost
        SectorEntityToken outpost = super.createOutpost(fleet, target);

        // Fix 1: Ensure unique ID
        String baseId = outpost.getId();  // Get the ID set by super
        int suffix = 1;
        String uniqueId = baseId;
        while (true) {
            boolean isDuplicate = false;
            for (PlayerOutpostIntel existing : getOutposts()) {
                if (existing != this && existing.getMapLocation(null) != null 
                    && existing.getMapLocation(null).getId().equals(uniqueId)) {
                    isDuplicate = true;
                    break;
                }
            }
            if (!isDuplicate) break;
            uniqueId = baseId + "_" + suffix++;
        }
        if (!uniqueId.equals(baseId)) {
            outpost.setId(uniqueId);  // Update ID if needed
        }

        // Fix 2: Adjust market setup based on NexConfig.outpostsAreColonies
        MarketAPI market = outpost.getMarket();  // Public method to get market
        if (market != null) {
            if (!NexConfigOutpostPatch.outpostsAreColonies) {
                DecivTracker.decivilize(market, true, false);
            } else {
                // Ensure it's a colony
                if (market.hasCondition(Conditions.ABANDONED_STATION)) {
                    market.removeCondition(Conditions.ABANDONED_STATION);
                }
            }
        }

        return outpost;
    }
}