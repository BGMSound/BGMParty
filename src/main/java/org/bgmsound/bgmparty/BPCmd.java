package org.bgmsound.bgmparty;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class BPCmd implements CommandExecutor {
    HashMap<UUID, Party> map2 = new HashMap<>();
    HashMap<UUID, Long> map = new HashMap<>();
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) return false;
        if(args.length == 0) {
            //파티가 있다면, 파티 안내해주기
            Party party = BGMParty.getParty(((Player) sender).getUniqueId());
            if(party==null) {
                //나중에 귀찮지 않을 때 안내문 적기
                return false;
            }
            List<UUID> plist = new ArrayList<>(party.pMemList);
            plist.remove(party.pOwner);
            String powner = Bukkit.getOfflinePlayer(party.pOwner).getName();

            sender.sendMessage("파티장 : "+powner);
            sender.sendMessage("파티 멤버 ▼");
            for(UUID memberid : plist) {
                String member = Bukkit.getOfflinePlayer(memberid).getName();
                sender.sendMessage(member);
            }
            return false;
        }
        else if(args[0].equals("생성") || args[0].equals("창조")) {
            Party party = BGMParty.getParty(((Player) sender).getUniqueId());
            if  (party!=null) {
                sender.sendMessage("이미 파티가 존재합니다");
                return false;
            }
            Party partyf = new Party(((Player) sender).getUniqueId());
            BGMParty.partyList.add(partyf);
        }
        else if(args[0].equals("초대")) {
            Party party = BGMParty.getParty(((Player) sender).getUniqueId());
            if(party ==null) {
                sender.sendMessage("파티를 보유하고 있는 사람만 이용 가능합니다.");
                return false;
            }
            if(args.length != 2) {
                sender.sendMessage("초대할 플레이어 이름을 입력해주세요.");
                return false;
            }
            Player p = Bukkit.getPlayer(args[1]);

            if(p == null) {
                sender.sendMessage("해당 플레이어는 온라인이 아닙니다.");
                return false;
            }
            if(p==sender) {
                sender.sendMessage("자기자신을 초대할 수 없습니다.");
                return false;
            }
            Party party2 = BGMParty.getParty(p.getUniqueId());
            if(party2 != null) {
                sender.sendMessage("이미 그 플레이어는 파티를 지니고 있습니다.");
                return false;
            }
            p.sendMessage(sender.getName()+"님이 당신에게 파티 초대장을 보내셨습니다 (15초 후 만료)");
            sender.sendMessage(p.getName()+"님에게 초대장을 보냈스빈다 (15초 후 만료)");

            Long time = System.currentTimeMillis();
            map.put(p.getUniqueId(), time);
            map2.put(p.getUniqueId(), party);

        } //친구가 없습니다...
        else if(args[0].equals("수락") || args[0].equals("거절")) {
            Long time = map.get(((Player) sender).getUniqueId());
            if (time == null) {
                sender.sendMessage("초대가 없습니다");
                return false;
            }
            Long now = System.currentTimeMillis();
            Party party = map2.get(((Player) sender).getUniqueId());
            Long diff = (now-time)/1000; //밀리초 단위라서 /1000 해줘야 초단위로 계산됨
            map.remove(((Player) sender).getUniqueId());
            map2.remove(((Player) sender).getUniqueId());
            if (diff>= 15) {
                sender.sendMessage("초대시간이 지나가버렸습니다.");
                return false;
            }

            if (Bukkit.getPlayer(party.pOwner) == null) {
                sender.sendMessage("파티장이 오프라인입니다.");
                return false;
            }
            if(args[0].equals("수락")) party.addPartyMem(((Player) sender).getUniqueId());
            else {
                sender.sendMessage(Bukkit.getPlayer(party.pOwner).getName() + "님의 파티 초대를 거절하셨습니다.");
                Bukkit.getPlayer(party.pOwner).sendMessage(sender.getName()+"님이 당신의 초대를 거절하셨습니다.");

            }
            return false;
        }
        //스크립트로 허비한 시간이 너무 많네요 아잇싯팔
        else if(args[0].equals("추방")) {
            if(args.length != 2) {
                sender.sendMessage("추방할 플레이어를 입력해주세요.");
                return false;
            }
            Party party = BGMParty.getParty(((Player) sender).getUniqueId());
            if(party == null) {
                sender.sendMessage("당신은 보유한 파티가 없습니다.");
                return false;
            }
            if(!(((Player) sender).getUniqueId().equals(party.pOwner)) ) {
                //플러그인 고수 배규민
                sender.sendMessage("파티 주인이 아닙니다.");
                return false;
            }
            OfflinePlayer p = Bukkit.getOfflinePlayer(args[1]);
            if (p==null) {
                sender.sendMessage("존재하지 않는 플레이어입니다");
                //일반적으론 OfflinePla
                return false;
            }
            if(p == sender) {
                sender.sendMessage("자기자신은 추방할 수 없습니다.");
                return false;
            }
            List<UUID> plist = party.pMemList;
            if(!plist.contains(p.getUniqueId())) {
                sender.sendMessage("플레이어가 파티에 존재하지 않습니다.");
                return false;
            }
            party.sendPartyMsg(p.getName()+"님이 파티에서 추방당했습니다. [처리자"+sender.getName()+"]");
            party.pMemList.remove(p.getUniqueId());
            return false;
        }
        if(args[0].equals("나가기")) {
            Party party = BGMParty.getParty(((Player) sender).getUniqueId());
            if(party == null) {
                sender.sendMessage("당신은 보유한 파티가 없습니다.");
                return false;
            }
            if(!((Player) sender).getUniqueId().equals( party.pOwner)) {
                party.sendPartyMsg(sender.getName()+"님이 파티에서 나가셨습니다.");
                party.pMemList.remove(((Player) sender).getUniqueId());
                return false;
            }
            else {
                party.sendPartyMsg("파티장에 의해 파티가 해산되었습니다.");
                BGMParty.partyList.remove(party);
            }
            return false;
        }
        return false;
    }

}
