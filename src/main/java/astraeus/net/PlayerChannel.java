package astraeus.net;

import astraeus.Server;
import astraeus.game.model.World;
import astraeus.game.model.entity.mob.player.Player;
import astraeus.game.model.entity.mob.player.attribute.Attribute;
import astraeus.game.model.entity.mob.player.io.PlayerDeserializer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import astraeus.net.codec.game.GamePacketDecoder;
import astraeus.net.codec.game.GamePacketEncoder;
import astraeus.net.codec.login.LoginDetailsPacket;
import astraeus.net.codec.login.LoginResponse;
import astraeus.net.codec.login.LoginResponsePacket;
import astraeus.net.packet.IncomingPacket;
import astraeus.net.packet.OutgoingPacket;
import astraeus.net.packet.in.IncomingPacketHandlerRegistration;
import astraeus.net.packet.out.SetPlayerSlotPacket;
import io.netty.channel.socket.SocketChannel;

import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

/**
 * Represents a {@link Channel} that provides asynchronous I/O operations for a {@link Player}.
 * 
 * @author Seven <https://github.com/vult-r>
 */
public class PlayerChannel {

      /**
       * The single logger for this class.
       */
      public static final Logger logger = Logger.getLogger(PlayerChannel.class.getName());

      /**
       * The prioritized packets that will be handled on the next sequence.
       */
      private final Queue<IncomingPacket> prioritizedPackets = new ConcurrentLinkedQueue<>();

      /**
       * The packets that will be handled on the next sequence.
       */
      public final Queue<IncomingPacket> queuedPackets = new ConcurrentLinkedQueue<>();

      /**
       * The channel that will manage the connection for this player.
       */
      private Optional<Channel> channel = Optional.empty();

      /**
       * The player that this operation will be executed for.
       */
      private Player player;

      /**
       * The address of this user connected by this channel.
       */
      private final String hostAddress;

      /**
       * Creates a new {@link PlayerChannel}
       * 
       * @param channel the channel that data will be written to.
       */
      public PlayerChannel(SocketChannel channel) {
            this.channel = Optional.of(channel);
            this.player = new Player(this);
            this.hostAddress = channel.remoteAddress().getAddress().getHostAddress();
      }

      /**
       * Processes a connection into an actual player.
       *
       * @param packet
       * 		The packet containing all the users login information.
       */
      public void handleUserLoginDetails(LoginDetailsPacket packet) {

            SocketChannel channel = (SocketChannel) packet.getContext().channel();

            final String username = packet.getUsername();

            final String password = packet.getPassword();

            @SuppressWarnings("unused")
			final String uuid = packet.getUUID();

            @SuppressWarnings("unused")
			final String hostAddress = channel.remoteAddress().getAddress().getHostAddress();

            Player player = this.getPlayer();

            player.setUsername(username);

            player.setPassword(password);

            try {
                  player.attr().put(Attribute.NEW_PLAYER, !PlayerDeserializer.deserialize(player));
            } catch (Exception e) {
                  e.printStackTrace();
            }

            LoginResponse response = evaluate(packet);

            ChannelFuture future = channel.writeAndFlush(new LoginResponsePacket(response, player.getRights(), false));

            if (response != LoginResponse.NORMAL) {
                  future.addListener(ChannelFutureListener.CLOSE);
                  return;
            }

            future.awaitUninterruptibly();

            player.send(new SetPlayerSlotPacket());

            channel.pipeline().replace("login-encoder", "game-encoder", new GamePacketEncoder(packet.getEncryptor()));
            channel.pipeline().replace("login-decoder", "game-decoder", new GamePacketDecoder(packet.getDecryptor()));

            World.queueLogin(player);
      }

      /**
       * Evaluates the user logging in, and sends the appropriate response code indicating their status.
       *
       * @param packet
       * 		The packet containing their login information.
       */
      private final LoginResponse evaluate(LoginDetailsPacket packet) {

            // username too long or too short
            if (packet.getUsername().isEmpty() || packet.getUsername().length() >= 13) {
                  return LoginResponse.INVALID_CREDENTIALS;
            }

            // username contains invalid characters
            if (!packet.getUsername().matches("^[a-zA-Z0-9 ]{1,12}$")) {
                  return LoginResponse.INVALID_CREDENTIALS;
            }

            // there is no password or password is too long
            if (packet.getPassword().isEmpty() || packet.getPassword().length() >= 20) {
                  return LoginResponse.INVALID_CREDENTIALS;
            }

            // password does not match password on file.
            if (!packet.getPassword().equals(player.getPassword())) {
                  return LoginResponse.INVALID_CREDENTIALS;
            }

            // the world is currently full
            if (World.getPlayers().isFull()) {
                  return LoginResponse.WORLD_FULL;
            }

            // this user is already online
            if (World.getPlayers().contains(player)) {
                  return LoginResponse.ACCOUNT_ONLINE;
            }

            // the users ip address has been banned.
            if (World.getIpBans().contains(hostAddress)) {
                  return LoginResponse.ACCOUNT_DISABLED;
            }

            // the users computer has been banned
            if (World.getBannedUUIDs().contains(packet.getUUID())) {
                  return LoginResponse.ACCOUNT_DISABLED;
            }

            // check the uuid when the player first created their account.
            if (World.getBannedUUIDs().contains(player.getUUID())) {
                  return LoginResponse.ACCOUNT_DISABLED;
            }

            // prevents users from logging in before the network has been fully bound
            if (!Server.SERVER_STARTED) {
                  return LoginResponse.SERVER_BEING_UPDATED;
            }

            // everything went good
            return LoginResponse.NORMAL;
      }

      /**
       * Queues {@code out} for this session to be encoded and sent to the client.
       */
      public void queue(OutgoingPacket out) {
            try {
                  if (player != null) {
                        channel.filter($it -> $it.isOpen()).ifPresent($it -> $it.writeAndFlush(out.toPacket(player)));
                  }
            } catch (Exception ex) {
                  ex.printStackTrace();
            }
      }

      /**
       * Clears all the packets in the queue.
       */
      public void clearPackets() {
            queuedPackets.clear();
      }

      /**
       * Gets the channel that will manage the connection for this player.
       * 
       * @return the channel for this player.
       */
      public Optional<Channel> getChannel() {
            return channel;
      }

      /**
       * Gets the player for this channel.
       * 
       * @return the player for this channel.
       */
      public Player getPlayer() {
            return player;
      }

      /**
       * Prioritizes incoming packets for a player.
       */
      public void handlePrioritizedPacketQueue() {
            IncomingPacket packet;
            while ((packet = prioritizedPackets.poll()) != null) {
                  if (player != null) {
                        IncomingPacketHandlerRegistration.dispatchToHandler(player, packet);
                  }
            }
      }

      /**
       * Handles incoming queued packets for a player.
       */
      public void handleQueuedPackets() {
            handlePrioritizedPacketQueue();
            IncomingPacket packet;
            while ((packet = queuedPackets.poll()) != null) {
                  if (packet.getOpcode() > 0) {
                        if (player != null) {
                              IncomingPacketHandlerRegistration.dispatchToHandler(player, packet);
                        }
                  }
            }
      }

      /**
       * Handles an upstream {@code packet}.
       * 
       * @param msg The packet to handle.
       */
      public void handleIncomingPacket(Object msg) {
            if (msg instanceof LoginDetailsPacket) {
                  handleUserLoginDetails((LoginDetailsPacket) msg);
            } else if (msg instanceof IncomingPacket) {
                  queueIncomingPacket((IncomingPacket) msg);
            }
      }

      /**
       * Queues {@link IncomingPacket}s to be handled on the next sequence.
       * 
       * @param packet The packet to queue.
       */
      public void queueIncomingPacket(final IncomingPacket packet) {
            if (queuedPackets.size() <= NetworkConstants.PACKET_LIMIT) {
                  if (packet.isPrioritized()) {
                        prioritizedPackets.add(packet);
                  } else {
                        queuedPackets.add(packet);
                  }
            }
      }

      public String getHostAddress() {
            return hostAddress;
      }

      /**
       * @param player the player to set
       */
      public void setPlayer(Player player) {
            this.player = player;
      }

}
