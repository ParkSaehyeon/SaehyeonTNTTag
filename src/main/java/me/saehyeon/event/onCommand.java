package me.saehyeon.event;

import me.saehyeon.main.Game;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class onCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(label.equals("tnt")) {
            switch(args[0]) {
                case "start":
                    Game.Start();
                    break;

                case "stop":
                    Game.Stop(null);
                    break;

                case "debug":
                    sender.sendMessage("이속: "+((Player)sender).getWalkSpeed());
                    break;

                case "debug-timer":

                    try {

                        int time = Integer.parseInt(args[1]);
                        Game.StartTimer(time);

                    } catch (Exception e) {

                        sender.sendMessage("§c숫자가 아니잖아!");

                    }

                    break;
            }
        }

        return false;
    }
}
