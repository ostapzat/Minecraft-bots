package wtf.optimusprimebots;

import com.github.steveice10.mc.protocol.MinecraftConstants;
import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.mc.protocol.ServerLoginHandler;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerChatPacket;
import com.github.steveice10.packetlib.ProxyInfo;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.event.session.DisconnectedEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;
import com.github.steveice10.packetlib.packet.Packet;
import com.github.steveice10.packetlib.tcp.TcpClientSession;
import wtf.optimusprimebots.ui.BotControlUI;

public class Bot {
    private final BotControlUI botControlUI;
    private Session client;
    private String name;

    public Bot(BotControlUI botControlUI, String name) {
        this.botControlUI = botControlUI;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void startBot(String host, int port, String username) {
        MinecraftProtocol protocol = new MinecraftProtocol(username);
        client = new TcpClientSession(host, port, protocol, (ProxyInfo) null);

        client.addListener(new SessionAdapter() {
            public void packetReceived(Session session, Packet packet) {
                if (packet instanceof ServerChatPacket chatPacket) {
                    botControlUI.updateChatLog(String.valueOf(chatPacket.getMessage()));
                }
            }

            @Override
            public void disconnected(DisconnectedEvent event) {
                botControlUI.updateErrorLog("Disconnected: " + event.getReason());
            }
        });

        client.setFlag(MinecraftConstants.SERVER_LOGIN_HANDLER_KEY, new ServerLoginHandler() {
            @Override
            public void loggedIn(Session session) {
                botControlUI.updateBotActivity("Bot logged in as " + username);
            }
        });

        client.connect();
    }

    public void sendMessage(String message) {
        if (client != null && client.isConnected()) {
            System.out.println("Sending message: " + message); // Додаємо лог
            client.send(new ClientChatPacket(message));
        } else {
            System.out.println("Bot is not connected, unable to send message.");
        }
    }

}
