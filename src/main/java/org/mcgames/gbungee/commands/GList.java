package org.mcgames.gbungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import org.mcgames.gbungee.utils.Color;
import org.mcgames.gbungee.utils.PlayerManager;
import org.mcgames.gbungee.gBungee;

public class GList extends Command {
    private final gBungee plugin;

    public GList(gBungee plugin) {
        super("glist", "gbungeecord.command.glist", "gbungee", "glist");
        this.plugin = plugin;
    }

    @Override
    public void execute(final CommandSender sender, String[] args) {
        // run async
        gBungee.getPlugin().getProxy().getScheduler().runAsync(gBungee.getPlugin(), new Runnable() {
            @Override
            public void run() {
                String message = "&dThere are &5" + PlayerManager.getCount() + "&d players online!";

                sender.sendMessage(Color.color(message));
            }
        });
    }
}
