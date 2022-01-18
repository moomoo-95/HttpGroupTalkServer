package moomoo.hgtp.server.protocol.hgtp.message.request.handler;


import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioDatagramChannel;
import moomoo.hgtp.server.network.NetworkManager;
import moomoo.hgtp.server.network.handler.HgtpChannelHandler;
import moomoo.hgtp.server.protocol.hgtp.message.base.HgtpHeader;
import moomoo.hgtp.server.protocol.hgtp.message.base.HgtpMessageType;
import moomoo.hgtp.server.protocol.hgtp.message.base.content.HgtpRegisterContent;
import moomoo.hgtp.server.protocol.hgtp.message.base.content.HgtpRoomContent;
import moomoo.hgtp.server.protocol.hgtp.message.request.*;
import moomoo.hgtp.server.protocol.hgtp.message.response.HgtpCommonResponse;
import moomoo.hgtp.server.protocol.hgtp.message.response.HgtpUnauthorizedResponse;
import moomoo.hgtp.server.protocol.hgtp.message.response.handler.HgtpResponseHandler;
import moomoo.hgtp.server.service.AppInstance;
import moomoo.hgtp.server.session.SessionManager;
import moomoo.hgtp.server.session.base.RoomInfo;
import moomoo.hgtp.server.session.base.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HgtpRequestHandler {

    private static final Logger log = LoggerFactory.getLogger(HgtpRequestHandler.class);
    private static final String LOG_FORMAT = "({}) () () RECV HGTP MSG [{}]";

    private static AppInstance appInstance = AppInstance.getInstance();

    private final HgtpResponseHandler hgtpResponseHandler = new HgtpResponseHandler();

    private SessionManager sessionManager = SessionManager.getInstance();

    public HgtpRequestHandler() {
        // nothing
    }

    public void registerRequestProcessing(HgtpRegisterRequest hgtpRegisterRequest) {
        HgtpHeader hgtpHeader = hgtpRegisterRequest.getHgtpHeader();
        HgtpRegisterContent hgtpRegisterContent = hgtpRegisterRequest.getHgtpContent();

        String userId = hgtpHeader.getUserId();

        log.debug(LOG_FORMAT, userId, hgtpRegisterRequest);


        // 첫 번째 Register Request
        short messageType = HgtpMessageType.UNKNOWN;
        if (hgtpRegisterContent.getNonce().equals("")) {
            // userInfo 생성
            UserInfo userInfo = sessionManager.addUserInfo(
                    userId, hgtpRegisterContent.getListenIp() , hgtpRegisterContent.getListenPort(), hgtpRegisterContent.getExpires()
            );

            if (userInfo == null) {
                // userInfo 생성 오류
                messageType = HgtpMessageType.BAD_REQUEST;
                log.debug("({}) () () UserInfo already exist.", userId);
            } else if (sessionManager.getUserInfoSize() > appInstance.getConfigManager().getUserMaxSize()) {
                // 최대 userInfo 초과
                messageType = HgtpMessageType.SERVER_UNAVAILABLE;
                log.debug("({}) () () Unavailable add UserInfo", userId);
            }

            if (messageType == HgtpMessageType.UNKNOWN) {
                HgtpUnauthorizedResponse hgtpUnauthorizedResponse = new HgtpUnauthorizedResponse(
                        AppInstance.MAGIC_COOKIE, HgtpMessageType.UNAUTHORIZED, hgtpHeader.getRequestType(),
                        userId, hgtpHeader.getSeqNumber() + AppInstance.SEQ_INCREMENT, appInstance.getTimeStamp(), AppInstance.MD5_REALM);

                hgtpResponseHandler.sendUnauthorizedResponse(hgtpUnauthorizedResponse);
            } else {
                // UNAUTHORIZED 이외인 경우 UserInfo 삭제
                HgtpCommonResponse hgtpCommonResponse = new HgtpCommonResponse(
                        AppInstance.MAGIC_COOKIE, messageType, hgtpHeader.getRequestType(),
                        userId, hgtpHeader.getSeqNumber() + AppInstance.SEQ_INCREMENT, appInstance.getTimeStamp());

                hgtpResponseHandler.sendCommonResponse(hgtpCommonResponse);
                if (userInfo != null) {
                    sessionManager.deleteUserInfo(userInfo.getUserId());
                }
            }
        }
        // 두 번째 Register Request
        else {
            // nonce 일치하면 userInfo 유지
            if (hgtpRegisterContent.getNonce().equals(appInstance.getServerNonce())) {
                messageType = HgtpMessageType.OK;
            }
            // 불일치 시 userInfo 삭제
            else {
                messageType = HgtpMessageType.FORBIDDEN;
            }

            HgtpCommonResponse hgtpCommonResponse = new HgtpCommonResponse(
                    AppInstance.MAGIC_COOKIE, messageType, hgtpHeader.getRequestType(),
                    userId, hgtpHeader.getSeqNumber() + AppInstance.SEQ_INCREMENT, appInstance.getTimeStamp());

            hgtpResponseHandler.sendCommonResponse(hgtpCommonResponse);

            UserInfo userInfo = sessionManager.getUserInfo(userId);
            if (userInfo != null && messageType == HgtpMessageType.FORBIDDEN) {
                sessionManager.deleteUserInfo(userInfo.getUserId());
            }
        }
    }

    public boolean unregisterRequestProcessing(HgtpUnregisterRequest hgtpUnregisterRequest) {
        HgtpHeader hgtpHeader = hgtpUnregisterRequest.getHgtpHeader();
        log.debug(LOG_FORMAT, hgtpHeader.getUserId(), hgtpUnregisterRequest);

        return true;
    }

    public void createRoomRequestProcessing(HgtpCreateRoomRequest hgtpCreateRoomRequest) {
        HgtpHeader hgtpHeader = hgtpCreateRoomRequest.getHgtpHeader();
        HgtpRoomContent hgtpRoomContent = hgtpCreateRoomRequest.getHgtpContent();

        String roomId = hgtpRoomContent.getRoomId();
        String userId = hgtpHeader.getUserId();

        log.debug(LOG_FORMAT, userId, hgtpCreateRoomRequest);


        if (sessionManager.getUserInfo(userId) == null) {
            log.debug("{} UserInfo is unregister", userId, roomId);
            return;
        }

        short messageType = HgtpMessageType.OK;
        if (roomId.equals("")) {
            messageType = HgtpMessageType.BAD_REQUEST;
            log.debug("({}) ({}) () RoomId is null", userId, roomId);
        } else {
            RoomInfo roomInfo = sessionManager.addRoomInfo(roomId, userId);

            if (roomInfo == null) {
                messageType = HgtpMessageType.BAD_REQUEST;
                log.debug("({}) ({}) () RoomInfo already exist.", userId, roomId);
            } else if (sessionManager.getRoomInfoSize() > appInstance.getConfigManager().getRoomMaxSize()) {
                // 최대 roomInfo 초과
                messageType = HgtpMessageType.SERVER_UNAVAILABLE;
                log.debug("({}) () () Unavailable add RoomInfo", userId);
            }
        }

        HgtpCommonResponse hgtpCommonResponse = new HgtpCommonResponse(
                AppInstance.MAGIC_COOKIE, messageType, hgtpHeader.getRequestType(),
                userId, hgtpHeader.getSeqNumber() + AppInstance.SEQ_INCREMENT, appInstance.getTimeStamp());

        hgtpResponseHandler.sendCommonResponse(hgtpCommonResponse);
        if (messageType == HgtpMessageType.SERVER_UNAVAILABLE) {
            sessionManager.deleteRoomInfo(roomId);
        }
    }

    public boolean deleteRoomRequestProcessing(HgtpDeleteRoomRequest hgtpDeleteRoomRequest) {
        log.debug(LOG_FORMAT, hgtpDeleteRoomRequest.getHgtpHeader().getUserId(), hgtpDeleteRoomRequest);
        return true;
    }

    public boolean joinRoomRequestProcessing(HgtpJoinRoomRequest hgtpJoinRoomRequest) {
        log.debug(LOG_FORMAT, hgtpJoinRoomRequest.getHgtpHeader().getUserId(), hgtpJoinRoomRequest);
        return true;
    }

    public boolean exitRoomRequestProcessing(HgtpExitRoomRequest hgtpExitRoomRequest) {
        log.debug(LOG_FORMAT, hgtpExitRoomRequest.getHgtpHeader().getUserId(), hgtpExitRoomRequest);
        return true;
    }

    public boolean inviteUserFromRoomRequestProcessing(HgtpInviteUserFromRoomRequest hgtpInviteUserFromRoomRequest) {
        log.debug(LOG_FORMAT, hgtpInviteUserFromRoomRequest.getHgtpHeader().getUserId(), hgtpInviteUserFromRoomRequest);
        return true;
    }

    public boolean removeUserFromRoomRequestProcessing(HgtpRemoveUserFromRoomRequest hgtpRemoveUserFromRoomRequest) {
        log.debug(LOG_FORMAT, hgtpRemoveUserFromRoomRequest.getHgtpHeader().getUserId(), hgtpRemoveUserFromRoomRequest);
        return true;
    }

}
