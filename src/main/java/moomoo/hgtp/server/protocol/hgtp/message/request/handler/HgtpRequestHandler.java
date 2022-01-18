package moomoo.hgtp.server.protocol.hgtp.message.request.handler;


import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioDatagramChannel;
import moomoo.hgtp.server.network.NetworkManager;
import moomoo.hgtp.server.network.handler.HgtpChannelHandler;
import moomoo.hgtp.server.protocol.hgtp.message.base.HgtpHeader;
import moomoo.hgtp.server.protocol.hgtp.message.base.HgtpMessageType;
import moomoo.hgtp.server.protocol.hgtp.message.base.content.HgtpRegisterContent;
import moomoo.hgtp.server.protocol.hgtp.message.request.*;
import moomoo.hgtp.server.protocol.hgtp.message.response.HgtpCommonResponse;
import moomoo.hgtp.server.protocol.hgtp.message.response.HgtpUnauthorizedResponse;
import moomoo.hgtp.server.protocol.hgtp.message.response.handler.HgtpResponseHandler;
import moomoo.hgtp.server.service.AppInstance;
import moomoo.hgtp.server.session.SessionManager;
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
        log.debug(LOG_FORMAT, hgtpHeader.getUserId(), hgtpRegisterRequest);

        // 첫 번째 Register Request
        if (hgtpRegisterContent.getNonce().equals("")) {
            HgtpUnauthorizedResponse hgtpUnauthorizedResponse = new HgtpUnauthorizedResponse(
                    AppInstance.MAGIC_COOKIE, HgtpMessageType.UNAUTHORIZED, hgtpHeader.getRequestType(),
                    hgtpHeader.getUserId(), hgtpHeader.getSeqNumber() + AppInstance.SEQ_INCREMENT, appInstance.getTimeStamp(), AppInstance.MD5_REALM);

            UserInfo userInfo = sessionManager.addUserInfo(
                    hgtpHeader.getUserId(), hgtpRegisterContent.getListenIp() , hgtpRegisterContent.getListenPort(), hgtpRegisterContent.getExpires()
            );

            if (userInfo == null) {
                log.debug("({}) () () UserInfo already exist.", hgtpHeader.getUserId());
                HgtpCommonResponse hgtpCommonResponse = new HgtpCommonResponse(
                        AppInstance.MAGIC_COOKIE, HgtpMessageType.BAD_REQUEST, hgtpHeader.getRequestType(),
                        hgtpHeader.getUserId(), hgtpHeader.getSeqNumber() + AppInstance.SEQ_INCREMENT, appInstance.getTimeStamp());

                hgtpResponseHandler.sendCommonResponse(hgtpCommonResponse);
                return;
            }

            NetworkManager.getInstance().getHgtpGroupSocket().addDestination(userInfo.getUserNetAddress(), null, userInfo.getSessionId(),
                    new ChannelInitializer<NioDatagramChannel>() {
                        @Override
                        protected void initChannel(NioDatagramChannel datagramChannel) {
                            final ChannelPipeline channelPipeline = datagramChannel.pipeline();
                            channelPipeline.addLast(new HgtpChannelHandler());
                        }
                    });

            hgtpResponseHandler.sendUnauthorizedResponse(hgtpUnauthorizedResponse);
        } else {
            short messageType;
            if (hgtpRegisterContent.getNonce().equals(appInstance.getServerNonce())) {
                if (sessionManager.getUserInfoSize() > appInstance.getConfigManager().getUserMaxSize()) {
                    messageType = HgtpMessageType.SERVER_UNAVAILABLE;
                } else {
                    messageType = HgtpMessageType.OK;
                }
            } else {
                messageType = HgtpMessageType.FORBIDDEN;
            }

            HgtpCommonResponse hgtpCommonResponse = new HgtpCommonResponse(
                    AppInstance.MAGIC_COOKIE, messageType, hgtpHeader.getRequestType(),
                    hgtpHeader.getUserId(), hgtpHeader.getSeqNumber() + AppInstance.SEQ_INCREMENT, appInstance.getTimeStamp());

            hgtpResponseHandler.sendCommonResponse(hgtpCommonResponse);
        }
    }

    public boolean unregisterRequestProcessing(HgtpUnregisterRequest hgtpUnregisterRequest) {
        HgtpHeader hgtpHeader = hgtpUnregisterRequest.getHgtpHeader();
        log.debug(LOG_FORMAT, hgtpHeader.getUserId(), hgtpUnregisterRequest);

        return true;
    }

    public boolean createRoomRequestProcessing(HgtpCreateRoomRequest hgtpCreateRoomRequest) {
        log.debug(LOG_FORMAT, hgtpCreateRoomRequest.getHgtpHeader().getUserId(), hgtpCreateRoomRequest);
        return true;
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
