package moomoo.hgtp.server.session;

import moomoo.hgtp.server.session.base.RoomInfo;
import moomoo.hgtp.server.session.base.UserInfo;

import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private static SessionManager sessionManager = null;

    private final ConcurrentHashMap<String, UserInfo> userInfoHashMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, RoomInfo> roomInfoHashMap = new ConcurrentHashMap<>();

    public SessionManager() {
        // nothing
    }

    public static SessionManager getInstance() {
        if (sessionManager == null) {
            sessionManager = new SessionManager();
        }
        return sessionManager;
    }


    public UserInfo addUserInfo(String userId, String userIp, short userPort, long expire) {
        if (userInfoHashMap.containsKey(userId)) {
            return null;
        }
        UserInfo userInfo = new UserInfo(userId, userIp, userPort, expire);
        synchronized (userInfoHashMap) {
            userInfoHashMap.put(userId, userInfo);
        }
        return  userInfo;
    }

    public void deleteUserInfo(String userId) {
        if (userInfoHashMap.containsKey(userId)) {
            synchronized (userInfoHashMap) {
                userInfoHashMap.remove(userId);
            }
        }
    }

    public RoomInfo addRoomInfo(String roomId, String managerId) {
        if (roomInfoHashMap.containsKey(roomId)) {
            return null;
        }
        RoomInfo roomInfo = new RoomInfo(roomId, managerId);
        synchronized (roomInfoHashMap) {
            roomInfoHashMap.put(roomId, roomInfo);
        }
        return  roomInfo;
    }

    public void deleteRoomInfo(String roomId) {
        if (roomInfoHashMap.containsKey(roomId)) {
            synchronized (roomInfoHashMap) {
                roomInfoHashMap.remove(roomId);
            }
        }
    }

    public int getUserInfoSize() {
        return userInfoHashMap.size();
    }

    public int getRoomInfoSize() { return roomInfoHashMap.size(); }

    public UserInfo getUserInfo(String userId) {
        if ( userInfoHashMap.containsKey(userId) ) {
            return userInfoHashMap.get(userId);
        } else {
            return null;
        }
    }

    public RoomInfo getRoomInfo(String roomId) {
        if ( roomInfoHashMap.containsKey(roomId) ) {
            return roomInfoHashMap.get(roomId);
        } else {
            return null;
        }
    }

}
