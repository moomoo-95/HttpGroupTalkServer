package com.hgtp.server.protocol.hgtp.message.request;

import com.hgtp.server.protocol.hgtp.exception.HgtpException;
import com.hgtp.server.protocol.hgtp.message.base.HgtpHeader;
import com.hgtp.server.protocol.hgtp.message.base.HgtpMessage;
import com.hgtp.server.protocol.hgtp.message.base.content.HgtpRoomContent;


public class HgtpCreateRoomRequest extends HgtpMessage {

    private final HgtpHeader hgtpHeader;
    private final HgtpRoomContent hgtpContext;

    public HgtpCreateRoomRequest(byte[] data) throws HgtpException {
        if (data.length >= HgtpHeader.HGTP_HEADER_SIZE + 12) {
            int index = 0;

            byte[] headerByteData = new byte[HgtpHeader.HGTP_HEADER_SIZE];
            System.arraycopy(data, index, headerByteData, 0, headerByteData.length);
            this.hgtpHeader = new HgtpHeader(headerByteData);
            index += headerByteData.length;

            byte[] contextByteData = new byte[hgtpHeader.getBodyLength()];
            System.arraycopy(data, index, contextByteData, 0, contextByteData.length);
            this.hgtpContext = new HgtpRoomContent(contextByteData);
        } else {
            this.hgtpHeader = null;
            this.hgtpContext = null;
        }
    }

    public HgtpCreateRoomRequest(short magicCookie, short messageType, String userId, int seqNumber, long timeStamp, String roomId) {
        int bodyLength = 12;

        this.hgtpHeader = new HgtpHeader(magicCookie, messageType, messageType, userId, seqNumber, timeStamp, bodyLength);
        this.hgtpContext = new HgtpRoomContent(roomId);
    }

    @Override
    public byte[] getByteData() {
        byte[] data = new byte[HgtpHeader.HGTP_HEADER_SIZE + this.hgtpHeader.getBodyLength()];
        int index = 0;

        byte[] headerByteData = this.hgtpHeader.getByteData();
        System.arraycopy(headerByteData, 0, data, index, headerByteData.length);
        index += headerByteData.length;

        byte[] contextByteData = this.hgtpContext.getByteData();
        System.arraycopy(contextByteData, 0, data, index, contextByteData.length);

        return data;
    }

    public HgtpHeader getHgtpHeader() {return hgtpHeader;}

    public HgtpRoomContent getHgtpContext() {return hgtpContext;}
}
