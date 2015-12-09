package org.mcgames.gbungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import org.mcgames.gbungee.utils.Color;
import org.mcgames.gbungee.gBungee;
import redis.clients.jedis.Jedis;

public class Alert extends Command {
    private final gBungee plugin;

    public Alert(gBungee plugin) {
        super("alert", "gbungeecord.command.alert", "gbungee", "alert", "galert");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, final String[] args) {
        if(args.length == 0) {
            sender.sendMessage(Color.color("&cImporper syntax. /alert <message>"));
            return;
        }

        sender.sendMessage(Color.color("&dSent to all proxies."));

        gBungee.getPlugin().getProxy().getScheduler().runAsync(gBungee.getPlugin(), new Runnable() {
            @Override
            public void run() {
                final Jedis js = gBungee.getJedis();

                gBungee.getPlugin().getProxy().getScheduler().runAsync(gBungee.getPlugin(), new Runnable() {
                    @Override
                    public void run() {
                        js.publish("allservers", "alert:" + String.join(" ", args));
                    }
                });
            }
        });
    }
}