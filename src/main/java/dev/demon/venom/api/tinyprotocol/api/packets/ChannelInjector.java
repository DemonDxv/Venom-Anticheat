/*
 * Created by Justin Heflin on 4/19/18 8:21 PM
 * Copyright (c) 2018.
 *
 * Can be redistributed non commercially as long as credit is given to original copyright owner.
 *
 * last modified: 4/19/18 7:22 PM
 */
package dev.demon.venom.api.tinyprotocol.api.packets;


import dev.demon.venom.Venom;
import dev.demon.venom.api.tinyprotocol.api.packets.channelhandler.ChannelHandler1_7;
import dev.demon.venom.api.tinyprotocol.api.packets.channelhandler.ChannelHandler1_8;
import dev.demon.venom.api.tinyprotocol.api.packets.channelhandler.ChannelHandlerAbstract;
import dev.demon.venom.api.user.User;

import dev.demon.venom.api.tinyprotocol.api.ProtocolVersion;
import dev.demon.venom.api.tinyprotocol.api.packets.channelhandler.ChannelHandler1_7;
import dev.demon.venom.api.tinyprotocol.api.packets.channelhandler.ChannelHandler1_8;
import dev.demon.venom.api.tinyprotocol.api.packets.channelhandler.ChannelHandlerAbstract;
import dev.demon.venom.api.user.User;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@Getter
public class ChannelInjector implements Listener {
    private ChannelHandlerAbstract channel;

    public ChannelInjector() {
        this.channel = ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_8) ? new ChannelHandler1_8() : new ChannelHandler1_7();
    }

    public void addChannel(Player player) {
        Venom.getInstance().getUserManager().addUser(new User(player));
        this.channel.addChannel(player);
        User user = Venom.getInstance().getUserManager().getUser(player.getUniqueId());

        if (user != null) {
            try {
                ProtocolVersion protocolVersion = ProtocolVersion.getVersion(this.channel.getProtocolVersion(player).getVersion());
                user.setProtocolVersion(protocolVersion);
            } catch (Exception ignored) {
                user.setProtocolVersion(ProtocolVersion.V1_7_10);
            }

            if (user.getProtocolVersion() == ProtocolVersion.UNKNOWN) {
                user.setProtocolVersion(ProtocolVersion.V1_7_10);
            }
        }
    }

    public void removeChannel(Player player) {
        User user = Venom.getInstance().getUserManager().getUser(player.getUniqueId());
        if (user != null) Venom.getInstance().getUserManager().removeUser(user);
        this.channel.removeChannel(player);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        Venom.getInstance().getExecutorService().execute(() -> addChannel(event.getPlayer()));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        removeChannel(event.getPlayer());
    }
}
