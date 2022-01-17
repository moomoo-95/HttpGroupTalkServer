package moomoo.hgtp.server.session.base;

import network.definition.NetAddress;
import network.socket.SocketProtocol;

public class UserInfo {

    private final String userId;
    private final NetAddress userNetAddress;
    private final long expireTime;

    private String roomId = "";

    public UserInfo(String userId, String listenIp, short listenPort, long expireTime) {
        this.userId = userId;
        this.userNetAddress = new NetAddress(listenIp, listenPort, true, SocketProtocol.UDP);
        this.expireTime = expireTime;
    }

    public String getUserId() {
        return userId;
    }

    public NetAddress getUserNetAddress() {
        return userNetAddress;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public String getRoomId() {
        return roomId;
    }
}
