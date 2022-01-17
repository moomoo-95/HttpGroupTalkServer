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
import moomoo.hgtp.server.service.AppInstance;
import moomoo.hgtp.server.session.SessionManager;
import moomoo.hgtp.server.session.base.UserInfo;
import network.definition.DestinationRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class HgtpRequestHandler {

    private static final Logger log = LoggerFactory.getLogger(HgtpRequestHandler.class);

    private static AppInstance appInstance = AppInstance.getInstance();

    public HgtpRequestHandler() {
        // nothing
    }

    // only server
    public static boolean registerRequestProcessing(HgtpRegisterRequest hgtpRegisterRequest) {
        HgtpHeader hgtpHeader = hgtpRegisterRequest.getHgtpHeader();
        HgtpRegisterContent hgtpRegisterContent = hgtpRegisterRequest.getHgtpContent();
        log.debug("({}) () () RECV HGTP MSG [{}]", hgtpHeader.getUserId(), hgtpRegisterRequest);

        // 첫 번째 Register Request
        if (hgtpRegisterContent.getNonce().equals("")) {
            HgtpUnauthorizedResponse hgtpUnauthorizedResponse = new HgtpUnauthorizedResponse(
                    AppInstance.MAGIC_COOKIE, HgtpMessageType.UNAUTHORIZED, hgtpHeader.getRequestType(),
                    hgtpHeader.getUserId(), hgtpHeader.getSeqNumber() + AppInstance.SEQ_INCREMENT, appInstance.getTimeStamp(), AppInstance.MD5_REALM);

            UserInfo userInfo = SessionManager.getInstance().addUserInfo(
                    hgtpHeader.getUserId(), hgtpRegisterContent.getListenIp() , hgtpRegisterContent.getListenPort(), hgtpRegisterContent.getExpires()
            );

            NetworkManager.getInstance().getHgtpGroupSocket().addDestination(userInfo.getUserNetAddress(), null, 2L,
                    new ChannelInitializer<NioDatagramChannel>() {
                        @Override
                        protected void initChannel(NioDatagramChannel datagramChannel) {
                            final ChannelPipeline channelPipeline = datagramChannel.pipeline();
                            channelPipeline.addLast(new HgtpChannelHandler());
                        }
                    });

            DestinationRecord destinationRecord = NetworkManager.getInstance().getHgtpGroupSocket().getDestination(2L);
            if (destinationRecord == null) {
                log.warn("({}) () () DestinationRecord Channel is null.", userInfo.getUserId());
            }

            byte[] data = hgtpUnauthorizedResponse.getByteData();
            destinationRecord.getNettyChannel().sendData(data, data.length);
            log.debug("({}) () () [{}] SEND DATA {}", userInfo.getUserId(), HgtpMessageType.RESPONSE_HASHMAP.get(HgtpMessageType.UNAUTHORIZED), hgtpUnauthorizedResponse);


            return true; // todo send HgtpUnauthorizedResponse
        } else {
            short messageType;
            if (hgtpRegisterContent.getNonce().equals(appInstance.getServerNonce())) {
                if (false) { // todo 최대 유저 초과할 경우
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
            return true; // todo send hgtpCommonResponse
        }
    }

    public static boolean unregisterRequestProcessing(HgtpUnregisterRequest hgtpUnregisterRequest) {
        HgtpHeader hgtpHeader = hgtpUnregisterRequest.getHgtpHeader();
        log.debug("({}) () () RECV HGTP MSG [{}]", hgtpHeader.getUserId(), hgtpUnregisterRequest);

        return true;
    }

    public static boolean createRoomRequestProcessing(HgtpCreateRoomRequest hgtpCreateRoomRequest) {
        log.debug("({}) () () RECV HGTP MSG [{}]", hgtpCreateRoomRequest.getHgtpHeader().getUserId(), hgtpCreateRoomRequest);
        return true;
    }

    public static boolean deleteRoomRequestProcessing(HgtpDeleteRoomRequest hgtpDeleteRoomRequest) {
        log.debug("({}) () () RECV HGTP MSG [{}]", hgtpDeleteRoomRequest.getHgtpHeader().getUserId(), hgtpDeleteRoomRequest);
        return true;
    }

    public static boolean joinRoomRequestProcessing(HgtpJoinRoomRequest hgtpJoinRoomRequest) {
        log.debug("({}) () () RECV HGTP MSG [{}]", hgtpJoinRoomRequest.getHgtpHeader().getUserId(), hgtpJoinRoomRequest);
        return true;
    }

    public static boolean exitRoomRequestProcessing(HgtpExitRoomRequest hgtpExitRoomRequest) {
        log.debug("({}) () () RECV HGTP MSG [{}]", hgtpExitRoomRequest.getHgtpHeader().getUserId(), hgtpExitRoomRequest);
        return true;
    }

    public static boolean inviteUserFromRoomRequestProcessing(HgtpInviteUserFromRoomRequest hgtpInviteUserFromRoomRequest) {
        log.debug("({}) () () RECV HGTP MSG [{}]", hgtpInviteUserFromRoomRequest.getHgtpHeader().getUserId(), hgtpInviteUserFromRoomRequest);
        return true;
    }

    public static boolean removeUserFromRoomRequestProcessing(HgtpRemoveUserFromRoomRequest hgtpRemoveUserFromRoomRequest) {
        log.debug("({}) () () RECV HGTP MSG [{}]", hgtpRemoveUserFromRoomRequest.getHgtpHeader().getUserId(), hgtpRemoveUserFromRoomRequest);
        return true;
    }

}
