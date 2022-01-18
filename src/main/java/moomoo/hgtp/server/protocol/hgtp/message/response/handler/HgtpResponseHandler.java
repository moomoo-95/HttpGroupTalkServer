package moomoo.hgtp.server.protocol.hgtp.message.response.handler;

import moomoo.hgtp.server.network.NetworkManager;
import moomoo.hgtp.server.protocol.hgtp.message.base.HgtpHeader;
import moomoo.hgtp.server.protocol.hgtp.message.base.HgtpMessageType;
import moomoo.hgtp.server.protocol.hgtp.message.response.HgtpCommonResponse;
import moomoo.hgtp.server.protocol.hgtp.message.response.HgtpUnauthorizedResponse;
import moomoo.hgtp.server.session.SessionManager;
import moomoo.hgtp.server.session.base.UserInfo;
import network.definition.DestinationRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HgtpResponseHandler {

    private static final Logger log = LoggerFactory.getLogger(HgtpResponseHandler.class);

    private SessionManager sessionManager = SessionManager.getInstance();

    public HgtpResponseHandler() {
        // nothing
    }

    public boolean okResponseProcessing(HgtpCommonResponse hgtpOkResponse) {
        HgtpHeader hgtpHeader = hgtpOkResponse.getHgtpHeader();
        log.debug("({}) () () RECV HGTP MSG [{}]", hgtpHeader.getUserId(), hgtpOkResponse);

        if (hgtpHeader.getRequestType() == HgtpMessageType.REGISTER) {
            // nothing
        }
        return true;
    }

    public boolean badRequestResponseProcessing(HgtpCommonResponse hgtpBadRequestResponse) {
        log.debug("({}) () () RECV HGTP MSG [{}]", hgtpBadRequestResponse.getHgtpHeader().getUserId(), hgtpBadRequestResponse);
        return true;
    }


    public boolean forbiddenResponseProcessing(HgtpCommonResponse hgtpForbiddenResponse) {
        log.debug("({}) () () RECV HGTP MSG [{}]", hgtpForbiddenResponse.getHgtpHeader().getUserId(), hgtpForbiddenResponse);
        return true;
    }

    public boolean serverUnavailableResponseProcessing(HgtpCommonResponse hgtpServerUnavailableResponse) {
        log.debug("({}) () () RECV HGTP MSG [{}]", hgtpServerUnavailableResponse.getHgtpHeader().getUserId(), hgtpServerUnavailableResponse);
        return true;
    }

    public boolean declineResponseProcessing(HgtpCommonResponse hgtpDeclineResponse) {
        log.debug("({}) () () RECV HGTP MSG [{}]", hgtpDeclineResponse.getHgtpHeader().getUserId(), hgtpDeclineResponse);
        return true;
    }

    public void sendCommonResponse(HgtpCommonResponse hgtpCommonResponse) {
        UserInfo userInfo = sessionManager.getUserInfo(hgtpCommonResponse.getHgtpHeader().getUserId());

        if (userInfo == null) {
            log.warn("({}) () () UserInfo is null.", userInfo.getUserId());
        }

        DestinationRecord destinationRecord = NetworkManager.getInstance().getHgtpGroupSocket().getDestination(userInfo.getSessionId());
        if (destinationRecord == null) {
            log.warn("({}) () () DestinationRecord Channel is null.", userInfo.getUserId());
        }

        byte[] data = hgtpCommonResponse.getByteData();
        destinationRecord.getNettyChannel().sendData(data, data.length);
        log.debug("({}) () () [{}] SEND DATA {}", userInfo.getUserId(), HgtpMessageType.RESPONSE_HASHMAP.get(hgtpCommonResponse.getHgtpHeader().getMessageType()), hgtpCommonResponse);

    }

    public void sendUnauthorizedResponse(HgtpUnauthorizedResponse hgtpUnauthorizedResponse) {
        UserInfo userInfo = sessionManager.getUserInfo(hgtpUnauthorizedResponse.getHgtpHeader().getUserId());

        if (userInfo == null) {
            log.warn("({}) () () UserInfo is null.", userInfo.getUserId());
        }

        DestinationRecord destinationRecord = NetworkManager.getInstance().getHgtpGroupSocket().getDestination(userInfo.getSessionId());
        if (destinationRecord == null) {
            log.warn("({}) () () DestinationRecord Channel is null.", userInfo.getUserId());
        }

        byte[] data = hgtpUnauthorizedResponse.getByteData();
        destinationRecord.getNettyChannel().sendData(data, data.length);
        log.debug("({}) () () [{}] SEND DATA {}", userInfo.getUserId(), HgtpMessageType.RESPONSE_HASHMAP.get(hgtpUnauthorizedResponse.getHgtpHeader().getMessageType()), hgtpUnauthorizedResponse);
    }
}
