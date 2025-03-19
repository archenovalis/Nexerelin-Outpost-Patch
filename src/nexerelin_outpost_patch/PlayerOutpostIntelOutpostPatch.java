package nexerelin_outpost_patch;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.intel.deciv.DecivTracker;
import exerelin.campaign.intel.PlayerOutpostIntel;
import lombok.extern.log4j.Log4j;

@Log4j
public class PlayerOutpostIntelOutpostPatch extends PlayerOutpostIntel {

    public PlayerOutpostIntelOutpostPatch() {
        super();
        log.info("PlayerOutpostIntelOutpostPatch: Default constructor called");
    }

    @Override
    public SectorEntityToken createOutpost(CampaignFleetAPI fleet, SectorEntityToken target) {
        log.info("PlayerOutpostIntelOutpostPatch.createOutpost: Starting with fleet " + fleet.getId() + " and target " + target.getId());

        SectorEntityToken outpost = super.createOutpost(fleet, target);
        this.outpost = outpost;
        this.market = outpost.getMarket();

        String baseId = outpost.getId();
        int suffix = 1;
        String uniqueId = baseId;
        log.info("PlayerOutpostIntelOutpostPatch.createOutpost: Initial ID = " + baseId);
        while (true) {
            boolean isDuplicate = false;
            for (PlayerOutpostIntel existing : getOutposts()) {
                if (existing != this && existing.getMapLocation(null) != null 
                    && existing.getMapLocation(null).getId().equals(uniqueId)) {
                    isDuplicate = true;
                    log.info("PlayerOutpostIntelOutpostPatch.createOutpost: Duplicate ID found: " + uniqueId);
                    break;
                }
            }
            if (!isDuplicate) break;
            uniqueId = baseId + "_" + suffix++;
            log.info("PlayerOutpostIntelOutpostPatch.createOutpost: Trying new ID: " + uniqueId);
        }
        if (!uniqueId.equals(baseId)) {
            outpost.setId(uniqueId);
            log.info("PlayerOutpostIntelOutpostPatch.createOutpost: Updated outpost ID to " + uniqueId);
        } else {
            log.info("PlayerOutpostIntelOutpostPatch.createOutpost: ID " + baseId + " is unique, no change needed");
        }

        MarketAPI market = outpost.getMarket();
        if (market != null) {
            log.info("PlayerOutpostIntelOutpostPatch.createOutpost: Market found for outpost, outpostsAreColonies = " + NexConfigOutpostPatch.outpostsAreColonies);
            if (!NexConfigOutpostPatch.outpostsAreColonies) {
                log.info("PlayerOutpostIntelOutpostPatch.createOutpost: outpostsAreColonies is false, decivilizing market");
                DecivTracker.decivilize(market, true, false);
            } else {
                if (market.hasCondition(Conditions.ABANDONED_STATION)) {
                    log.info("PlayerOutpostIntelOutpostPatch.createOutpost: Removing ABANDONED_STATION condition");
                    market.removeCondition(Conditions.ABANDONED_STATION);
                } else {
                    log.info("PlayerOutpostIntelOutpostPatch.createOutpost: No ABANDONED_STATION condition to remove");
                }
            }
        } else {
            log.info("PlayerOutpostIntelOutpostPatch.createOutpost: No market found for outpost");
        }

        registerOutpost(this);
        Global.getSector().getIntelManager().addIntel(this, true);
        timestamp = Global.getSector().getClock().getTimestamp();

        log.info("PlayerOutpostIntelOutpostPatch.createOutpost: Completed, returning outpost with ID " + outpost.getId());
        return outpost;
    }
}