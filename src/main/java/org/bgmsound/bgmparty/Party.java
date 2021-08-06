package org.bgmsound.bgmparty;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Party {
    public UUID pOwner;
    public List<UUID> pMemList = new ArrayList<>();
    //public
    public Party(UUID uuid) {
        pOwner = uuid;
        Player p = Bukkit.getPlayer(pOwner);
        p.sendMessage("새로운 파티를 만드셨습니다!");
        pMemList.add(pOwner);
        //{party.%uuid of player%::*}
    }
    public void addPartyMem(UUID uuid) {
        pMemList.add(uuid);
        Player p = Bukkit.getPlayer(uuid);
        sendPartyMsg(p.getName()+"님이 파티에 입장하셨습니다 [" + pMemList.size() + "명]");
    }
    public void sendPartyMsg(String msg) {
        for(UUID mem : pMemList) {
            Player p = Bukkit.getPlayer(mem);
            if(p==null) continue;
            p.sendMessage(msg);
        }
    }
    //

}
