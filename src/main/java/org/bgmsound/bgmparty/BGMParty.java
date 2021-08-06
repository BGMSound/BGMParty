package org.bgmsound.bgmparty;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class BGMParty extends JavaPlugin {
    public static List<Party> partyList = new ArrayList();
    @Override
    public void onEnable() {
        // Plugin startup logic
        //getCommand()

        getCommand("파티").setExecutor(new BPCmd());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
    public static Party getParty (UUID uuid) {
        for(Party loopvalue : partyList) {
            if (loopvalue.pOwner.equals(uuid)) return loopvalue;
            if(loopvalue.pMemList.contains(uuid)) return loopvalue;
        }
    return null;
    }
}
