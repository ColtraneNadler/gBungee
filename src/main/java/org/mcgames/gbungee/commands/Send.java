package org.mcgames.gbungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import org.mcgames.gbungee.utils.Color;
import org.mcgames.gbungee.gBungee;
import org.mcgames.gbungee.utils.PlayerManager;
import redis.clients.jedis.Jedis;

import java.util.Set;

public class Send extends Command {
    private final gBungee plugin;

    public Send(gBungee plugin) {
        super("send", "gbungeecord.command.send", "gbungee", "send", "gsend");
        this.plugin = plugin;
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        if(args.length > 2 || args.length < 2) {
            sender.sendMessage(Color.color("&cImporper syntax. /send <username> <server>"));
            return;
        }

        gBungee.getPlugin().getProxy().getScheduler().runAsync(gBungee.getPlugin(), new Runnable() {
            @Override
            public void run() {
                ProxiedPlayer player = plugin.getProxy().getPlayer(args[0]);
                ServerInfo sv = gBungee.getPlugin().getProxy().getServerInfo(args[1]);

                if(sv == null) {
                    sender.sendMessage(Color.color("&c" + args[1] + " does not exist"));
                    return;
                }

                if(player != null) {
                    PlayerManager.sendPlayer(args[0], args[1]);
                    return;
                }

                Jedis js = plugin.getJedis();
                Set<String> proxies = js.smembers(gBungee.PROXIES);

                for(String p : proxies) {
                    if(js.sismember(gBungee.PROXY_PLAYERS + p, args[0])) {
                        sender.sendMessage(Color.color("&dAttempting to send " +
                                args[0] + " to " + args[1] + " accross proxies!"));
                        js.publish(p, "send:" + args[0] + ":" + args[1]);
                        return;
                    }
                }

                sender.sendMessage(Color.color("&c" + args[0] + " was not found on the network."));
            }
        });
    }
}
