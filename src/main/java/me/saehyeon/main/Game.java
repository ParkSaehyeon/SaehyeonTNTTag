package me.saehyeon.main;

import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Game {

    public static boolean isGaming = false;
    public static final int ROUND_TIME_DECREMENT = 10;
    public static final int FIRST_ROUND_TIME = 30;

    public static final Location SPAWN_LOCATION = new Location(Bukkit.getWorld("world"),9,4,0);
    public static final int NEXT_ROUND_WAIT_TIME = 5;
    public static int CURRENT_ROUND = 0;

    public static BukkitTask timerTask;
    public static Scoreboard scoreboard;
    public static BossBar bossbar = null;
    public static void init() {

        // 라운드 초기화
        CURRENT_ROUND = 0;

        // 타이머 BukkitTask 초기화
        if(timerTask != null)
            timerTask.cancel();

        // 스코어 보드 초기화
        if(scoreboard == null) {
            scoreboard  = Bukkit.getScoreboardManager().getNewScoreboard();

            Team tntTeam = scoreboard.registerNewTeam("tnt");
            tntTeam.setPrefix("이 사람 TNT --> ");
            tntTeam.setColor(ChatColor.RED);

            scoreboard.registerNewTeam("default");
        }

        // 보스바 초기화
        if(bossbar == null)
            bossbar = Bukkit.createBossBar("§l남은시간", BarColor.WHITE, BarStyle.SOLID);

        Bukkit.getOnlinePlayers().forEach(p -> {

            if(bossbar != null)
                bossbar.removePlayer(p);

            bossbar.addPlayer(p);

            // 맨 처음은 모두를 생존자 역할 부여
            Role.set(p, RoleType.DEFAULT);


            // 모든 사람을 스폰 장소로 TP
            p.teleport(SPAWN_LOCATION);

            // 모든 사람의 게임모드 변경
            p.setGameMode(GameMode.ADVENTURE);
        });

    }

    public static void Start() {
        init();
        nextRound();
        Bukkit.getOnlinePlayers().forEach(p -> p.sendTitle("§l준비하세요!","곧 게임이 시작됩니다.") );
        isGaming = true;
    }

    public static void Stop(Player winner) {

        // 승리 처리 -> 게임 종료
        Bukkit.getOnlinePlayers().forEach(p -> {

            // 만약 이긴 사람이 null이라면 무승부로 처리
            if(winner == null)
                p.sendTitle("§l무승부!","§f게임이 종료되었습니다.");
            else
                p.sendTitle("§l게임종료","§6"+winner.getName()+"§f(이)가 이겼습니다!");

            p.setGameMode(GameMode.SPECTATOR);
        });

        // 진행중인 BukkitTask 없애기
        timerTask.cancel();

        // 보스바 갱신
        bossbar.setTitle("§6§l게임이 종료되었습니다!");
        bossbar.setProgress(0);

        isGaming = false;

    }

    public static void StartTimer(double seconds) {

        // 보스바 progress 재설정
        bossbar.setProgress(1);

        timerTask = Bukkit.getScheduler().runTaskTimer(TNTTag.instance, () -> {

            // 보스바 progress 차감
            double decrement        = 1/seconds;
            double totalProgress    = bossbar.getProgress()-decrement;

            if(totalProgress <= 0) {
                bossbar.setProgress(0);
                timerTask.cancel();
                nextRound();
                return;
            }

            bossbar.setProgress(totalProgress);

        },0, 20);
    }

    public static boolean checkWinner() {
        List<Player> players = Role.getPlayers(RoleType.DEFAULT);

        // 한 명만 살아있음
        if( players.size() == 1 ) {

            // 승리한 사람 있음 -> 게임 종료
            Stop( players.get(0) );
            return true;
        }

        return false;
    }

    /**
     * 서버 내의 모든 플레이어의 역할을 자동으로 다시 지급합니다.
     * 이 작업이 진행되면 게임에서 살아남은 사람 중 3분의 1이 TNT가 됩니다.
     */
    public static void setAllRandom() {

        // 현재 살아남은 사람 배열
        ArrayList<Player> players = Role.getPlayers(RoleType.DEFAULT);

        // 현재 살아남은 사람 수
        float left = (float)players.size();

        // 채워져야 하는 목표 TNT 역할의 사람 수
        int MAX_TNT = Math.round(left/3f);

        // 배열 섞기
        Collections.shuffle(players);

        for(int i = 0; i < MAX_TNT; i++) {
            Role.set(players.get(i),RoleType.TNT);
        }

    }

    public static void nextRound() {

        // TNT 역할인 사람들 죽기
        killTNT();

        // 승리한 사람이 있는지 확인 (있다면 게임을 종료)
        if(checkWinner())
            return;

        // 만약 CURRENT_ROUND가 0(게임 직후)라면 아래의 메세지를 출력하지 않음
        if(CURRENT_ROUND != 0)
            Bukkit.broadcastMessage(CURRENT_ROUND+"라운드가 종료되었습니다! 잠시 후, 다음 라운드가 시작됩니다.");

        CURRENT_ROUND++;

        // 잠시 대기후 다음 라운드 진행
        Bukkit.getScheduler().runTaskLater(TNTTag.instance, () -> {

            // 모든 플레이어 스폰으로 이동
            Bukkit.getOnlinePlayers().forEach(p -> p.teleport(SPAWN_LOCATION));

            // 타이머 재설정
            int time = FIRST_ROUND_TIME + CURRENT_ROUND*-ROUND_TIME_DECREMENT + ROUND_TIME_DECREMENT;
            //Bukkit.broadcastMessage("타이머 시간 설정: "+time);
            StartTimer( time );

            // 모든 플레이어의 역할 재설정 (관전자 제외)
            setAllRandom();

            // 채팅창에 공지
            Role.announceTNT();

        },20*NEXT_ROUND_WAIT_TIME);

    }

    public static void killTNT() {

        // TNT 역할인 사람들 구하기
        ArrayList<Player> tnt = Role.getPlayers(RoleType.TNT);

        tnt.forEach(p -> {

            // 폭발 파티클
            p.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, p.getLocation(), 1);

            // 폭발 소리
            p.getWorld().playSound(p.getLocation(), Sound.ENTITY_GENERIC_EXPLODE,1,1);

            // 플레이어를 관전자로 만들기
            Role.set(p, RoleType.SPECTATOR);

            // 탈락 메세지 공지
            Bukkit.broadcastMessage("§c§l"+p.getName()+"§f(이)가 탈락했습니다!");

        });
    }
}
