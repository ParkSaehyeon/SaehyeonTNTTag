package me.saehyeon.event;

import me.saehyeon.main.Game;
import me.saehyeon.main.Role;
import me.saehyeon.main.RoleType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class onDamage implements Listener {
    @EventHandler
    void onDamage(EntityDamageByEntityEvent e) {
        Entity attackerEn = e.getDamager();
        Entity victimEn = e.getEntity();

        // 게임이 진행중이지 않을때는 이벤트 처리하지 않음
        if(!Game.isGaming)
            return;

        if(attackerEn instanceof Player && victimEn instanceof Player) {

            Player attacker = (Player)attackerEn;
            Player victim   = (Player)victimEn;

            // 공격자가 TNT이고 피해자는 TNT가 아님
            if(Role.get(attacker) == RoleType.TNT && Role.get(victim) != RoleType.TNT) {

                // 공격자는 일반인 역할, 피해자는 TNT로 역할 변경
                Role.set(attacker, RoleType.DEFAULT);
                Role.set(victim, RoleType.TNT);

                // TNT 됐다고 공지하기
                Bukkit.broadcastMessage("§7"+victim.getName()+"§f(이)가 §c§lTNT가 되었습니다!");

            }

        }
    }
}
