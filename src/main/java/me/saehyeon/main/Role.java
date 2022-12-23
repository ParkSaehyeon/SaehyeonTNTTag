package me.saehyeon.main;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;



public class Role {
    static HashMap<UUID, RoleType> roleTypeMap = new HashMap<>();

    /**
     * 플레이어의 역할을 설정합니다.
     * @param player 설정될 플레이어
     * @param roleType 플레이어에게 설정할 역할
     */
    public static void set(Player player, RoleType roleType) {

        // 만약 roleType이 null이라면 roleTypeMap에서 플레이어 없애기
        if(roleType == null || roleType == RoleType.SPECTATOR)
            roleTypeMap.remove(player.getUniqueId());

        roleTypeMap.put(player.getUniqueId(),roleType);

        // 역할에 따른 특성 업데이트
        updateAttribute(player);
    }

    /**
     * 특정 플레이어가 가진 역할을 반환합니다.
     * @param player 역할을 확인할 플레이어
     * @return 플레이어가 가진 역할
     */
    public static RoleType get(Player player) {
        return roleTypeMap.getOrDefault(player.getUniqueId(),null);
    }

    /**
     * 특정 역할을 가지고 있는 사람들을 반환합니다.
     * @param roleType 가지고 있어야 하는 역할
     * @return 역할을 가지고 있는 사람들의 배열
     */
    public static ArrayList<Player> getPlayers(RoleType roleType) {
        ArrayList<Player> players = new ArrayList<>( Bukkit.getOnlinePlayers() );
        players.removeIf(p -> get(p) != roleType);

        return players;
    }

    /**
     * TNT역할로 설정된 사람들을 출력합니다.
     * 일반적으로, 한 라운드가 끝나고 역할이 새로 배정된 후 호출됩니다.
     */
    public static void announceTNT() {

        // TNT인 사람들을 배열 구하기
        ArrayList<Player> tntPlayers = getPlayers(RoleType.TNT);

        // 배열 출력을 위한 문자열 만들기
        StringBuilder tntPlayersStr = new StringBuilder("");

        tntPlayers.forEach(p -> tntPlayersStr.append("§l").append(p.getName()).append(", "));

        // 출력
        Bukkit.broadcastMessage("이번 라운드에서 §c§lTNT§f가 된 플레이어는\n");
        Bukkit.broadcastMessage(tntPlayersStr.substring(0,tntPlayersStr.lastIndexOf(",")-1));
        Bukkit.broadcastMessage("§f입니다. 모두 도망치세요!");
    }

    /**
     * 플레이어의 역할에 따라 특성을 변경합니다.
     * 일반적으로, 플레이어의 역할이 변경될때 호출됩니다.
     */
    static void updateAttribute(Player player) {

        // 플레이어의 역할 얻기
        RoleType roleType = get(player);

        // 역할에 따라 특성 적용하기
        switch(roleType) {

            // 역할이 TNT라면
            case TNT:

                // 플레이어에게 TNT 모자 씌우기
                player.getInventory().setArmorContents(new ItemStack[] { null, null, null, new ItemStack(Material.TNT) });

                // 플레이어의 이속을 올리기 (기본이속은 0.2임)
                player.setWalkSpeed(0.4f);

                // 스코어보드 TNT 팀에 추가하기
                Game.scoreboard.getTeam("default").removeEntry(player.getName());
                Game.scoreboard.getTeam("tnt").addEntry(player.getName());

                break;

            // 역할이 일반인
            case DEFAULT:

                // 플레이어의 이속을 기본값으로 되돌려놓기
                player.setWalkSpeed(0.2f);

                // 플레이어의 갑옷 슬롯 없애기
                player.getInventory().setArmorContents(new ItemStack[] { null, null, null, null });

                // 스코어보드 DEFAULT 팀에 추가하기
                Game.scoreboard.getTeam("tnt").removeEntry(player.getName());
                Game.scoreboard.getTeam("default").addEntry(player.getName());
                break;

            case SPECTATOR:
                player.setGameMode(GameMode.SPECTATOR);

                Game.scoreboard.getTeam("tnt").removeEntry(player.getName());
                Game.scoreboard.getTeam("default").removeEntry(player.getName());
                break;

        }
    }
}
