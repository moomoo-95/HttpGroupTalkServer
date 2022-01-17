package moomoo.hgtp.server.protocol.hgtp.message.response.handler;

import moomoo.hgtp.server.protocol.hgtp.message.base.HgtpHeader;
import moomoo.hgtp.server.protocol.hgtp.message.base.HgtpMessageType;
import moomoo.hgtp.server.protocol.hgtp.message.response.HgtpCommonResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HgtpResponseHandler {

    private static final Logger log = LoggerFactory.getLogger(HgtpResponseHandler.class);

    public HgtpResponseHandler() {
        // nothing
    }

    public static boolean okResponseProcessing(HgtpCommonResponse hgtpOkResponse) {
        HgtpHeader hgtpHeader = hgtpOkResponse.getHgtpHeader();
        log.debug("({}) () () RECV HGTP MSG [{}]", hgtpHeader.getUserId(), hgtpOkResponse);

        if (hgtpHeader.getRequestType() == HgtpMessageType.REGISTER) {
            // nothing
        }
        return true;
    }

    public static boolean badRequestResponseProcessing(HgtpCommonResponse hgtpBadRequestResponse) {
        log.debug("({}) () () RECV HGTP MSG [{}]", hgtpBadRequestResponse.getHgtpHeader().getUserId(), hgtpBadRequestResponse);
        return true;
    }


    public static boolean forbiddenResponseProcessing(HgtpCommonResponse hgtpForbiddenResponse) {
        log.debug("({}) () () RECV HGTP MSG [{}]", hgtpForbiddenResponse.getHgtpHeader().getUserId(), hgtpForbiddenResponse);
        return true;
    }

    public static boolean serverUnavailableResponseProcessing(HgtpCommonResponse hgtpServerUnavailableResponse) {
        log.debug("({}) () () RECV HGTP MSG [{}]", hgtpServerUnavailableResponse.getHgtpHeader().getUserId(), hgtpServerUnavailableResponse);
        return true;
    }

    public static boolean declineResponseProcessing(HgtpCommonResponse hgtpDeclineResponse) {
        log.debug("({}) () () RECV HGTP MSG [{}]", hgtpDeclineResponse.getHgtpHeader().getUserId(), hgtpDeclineResponse);
        return true;
    }
}
