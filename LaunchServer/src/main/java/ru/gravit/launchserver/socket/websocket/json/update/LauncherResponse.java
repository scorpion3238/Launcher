package ru.gravit.launchserver.socket.websocket.json.update;

import io.netty.channel.ChannelHandlerContext;
import ru.gravit.launcher.events.request.LauncherRequestEvent;
import ru.gravit.launchserver.LaunchServer;
import ru.gravit.launchserver.socket.Client;
import ru.gravit.launchserver.socket.websocket.WebSocketService;
import ru.gravit.launchserver.socket.websocket.json.JsonResponseInterface;
import ru.gravit.utils.Version;

import java.util.Arrays;
import java.util.Base64;

public class LauncherResponse implements JsonResponseInterface {
    public Version version;
    public String hash;
    public int launcher_type;
    //REPLACED TO REAL URL
    public static final String JAR_URL = "http://localhost:9752/Launcher.jar";
    public static final String EXE_URL = "http://localhost:9752/Launcher.exe";

    @Override
    public String getType() {
        return "launcherUpdate";
    }

    @Override
    public void execute(WebSocketService service, ChannelHandlerContext ctx, Client client) {
        byte[] bytes = Base64.getDecoder().decode(hash);
        if (launcher_type == 1) // JAR
        {
            byte[] hash = LaunchServer.server.launcherBinary.getBytes().getDigest();
            if (hash == null) service.sendObjectAndClose(ctx, new LauncherRequestEvent(true, JAR_URL));
            if (Arrays.equals(bytes, hash)) {
                client.checkSign = true;
                service.sendObject(ctx, new LauncherRequestEvent(false, JAR_URL));
            } else {
                service.sendObjectAndClose(ctx, new LauncherRequestEvent(true, JAR_URL));
            }
        } else if (launcher_type == 2) //EXE
        {
            byte[] hash = LaunchServer.server.launcherEXEBinary.getBytes().getDigest();
            if (hash == null) service.sendObjectAndClose(ctx, new LauncherRequestEvent(true, EXE_URL));
            if (Arrays.equals(bytes, hash)) {
                client.checkSign = true;
                service.sendObject(ctx, new LauncherRequestEvent(false, EXE_URL));
            } else {
                service.sendObjectAndClose(ctx, new LauncherRequestEvent(true, EXE_URL));
            }
        } else service.sendObject(ctx, new WebSocketService.ErrorResult("Request launcher type error"));

    }

}
