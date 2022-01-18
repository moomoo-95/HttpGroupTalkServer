package moomoo.hgtp.server.session.base;

public class RoomInfo {

    private final String rooId;
    private final String managerId;

    private final long createTime;

    public RoomInfo(String rooId, String managerId) {
        this.rooId = rooId;
        this.managerId = managerId;
        this.createTime = System.currentTimeMillis();
    }
}
