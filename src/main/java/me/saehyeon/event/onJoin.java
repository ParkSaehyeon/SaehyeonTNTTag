package me.saehyeon.event;

import me.saehyeon.main.Game;
import me.saehyeon.main.Role;
import me.saehyeon.main.RoleType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class onJoin implements Listener {
    @EventHandler
    void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        if(Game.bossbar != null)
            Game.bossbar.addPlayer(p);

        // 게임중에 들어오면 관전자 모드로
        if(Game.isGaming)
            Role.set(p, RoleType.SPECTATOR);
    }
}
