package moomoo.hgtp.server.session;

import moomoo.hgtp.server.session.base.RoomInfo;
import moomoo.hgtp.server.session.base.UserInfo;

import java.util.HashMap;
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
            return userInfoHashMap.get(userId);
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

    public int getUserInfoSize() {
        return userInfoHashMap.size();
    }

    public UserInfo getUserInfo(String userId) {
        if ( userInfoHashMap.containsKey(userId) ) {
            return userInfoHashMap.get(userId);
        } else {
            return null;
        }
    }

}
