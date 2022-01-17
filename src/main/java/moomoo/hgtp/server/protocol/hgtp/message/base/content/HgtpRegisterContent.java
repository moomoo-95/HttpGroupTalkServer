package moomoo.hgtp.server.protocol.hgtp.message.base.content;

import moomoo.hgtp.server.protocol.hgtp.message.base.HgtpHeader;
import util.module.ByteUtil;

import java.nio.charset.StandardCharsets;

public class HgtpRegisterContent implements HgtpContent {


    private final long expires;             // 8 bytes
    private final int listenIpLength;       // 4 bytes
    private final String listenIp;          // listenIpLength bytes
    private final short listenPort;         // 2 bytes
    private int nonceLength = 0;            // 4 bytes
    private String nonce = "";              // nonceLength bytes

    public HgtpRegisterContent(byte[] data) {

        if (data.length >= ByteUtil.NUM_BYTES_IN_LONG + ByteUtil.NUM_BYTES_IN_SHORT + ByteUtil.NUM_BYTES_IN_INT) {
            int index = 0;
            byte[] expiresByteData = new byte[ByteUtil.NUM_BYTES_IN_LONG];
            System.arraycopy(data, index, expiresByteData, 0, expiresByteData.length);
            expires = ByteUtil.bytesToLong(expiresByteData, true);
            index += expiresByteData.length;

            byte[] listenIpLengthByteData = new byte[ByteUtil.NUM_BYTES_IN_INT];
            System.arraycopy(data, index, listenIpLengthByteData, 0, listenIpLengthByteData.length);
            listenIpLength = ByteUtil.bytesToInt(listenIpLengthByteData, true);
            index += listenIpLengthByteData.length;

            byte[] listenIpByteData = new byte[listenIpLength];
            System.arraycopy(data, index, listenIpByteData, 0, listenIpByteData.length);
            listenIp = new String(listenIpByteData);
            index += listenIpByteData.length;

            byte[] listenPortByteData = new byte[ByteUtil.NUM_BYTES_IN_SHORT];
            System.arraycopy(data, index, listenPortByteData, 0, listenPortByteData.length);
            listenPort = ByteUtil.bytesToShort(listenPortByteData, true);
            index += listenPortByteData.length;

            byte[] nonceLengthByteData = new byte[ByteUtil.NUM_BYTES_IN_INT];
            System.arraycopy(data, index, nonceLengthByteData, 0, nonceLengthByteData.length);
            nonceLength = ByteUtil.bytesToInt(nonceLengthByteData, true);
            if (nonceLength > 0) {
                index += nonceLengthByteData.length;

                byte[] nonceByteData = new byte[nonceLength];
                System.arraycopy(data, index, nonceByteData, 0, nonceByteData.length);
                nonce = new String(nonceByteData);
            }

        } else {
            this.expires = 0;
            this.listenPort = 0;
            this.listenIpLength = 0;
            this.listenIp = "";
        }
    }

    public HgtpRegisterContent(long expires, String listenIp, short listenPort) {
        this.expires = expires;
        this.listenIp = listenIp;
        this.listenPort = listenPort;
        this.listenIpLength = listenIp.length();
    }

    @Override
    public byte[] getByteData() {
        byte[] data = new byte[getBodyLength()];
        int index = 0;

        byte[] expiresByteData = ByteUtil.longToBytes(expires, true);
        System.arraycopy(expiresByteData, 0, data, index, expiresByteData.length);
        index += expiresByteData.length;

        byte[] listenIpLengthByteData = ByteUtil.intToBytes(listenIpLength, true);
        System.arraycopy(listenIpLengthByteData, 0, data, index, listenIpLengthByteData.length);
        index += listenIpLengthByteData.length;

        byte[] listenIpByteData = listenIp.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(listenIpByteData, 0, data, index, listenIpByteData.length);
        index += listenIpByteData.length;

        byte[] listenPortByteData = ByteUtil.shortToBytes(listenPort, true);
        System.arraycopy(listenPortByteData, 0, data, index, listenPortByteData.length);
        index += listenPortByteData.length;

        byte[] nonceLengthByteData = ByteUtil.intToBytes(nonceLength, true);
        System.arraycopy(nonceLengthByteData, 0, data, index, nonceLengthByteData.length);

        if (nonceLength > 0 && nonce.length() > 0) {
            byte[] nonceByteData = nonce.getBytes(StandardCharsets.UTF_8);
            byte[] newData = new byte[data.length];
            System.arraycopy(data, 0, newData, 0, data.length);
            index += nonceLengthByteData.length;
            System.arraycopy(nonceByteData, 0, newData, index, nonceByteData.length);
            data = newData;
        }

        return data;
    }

    public int getBodyLength() {
        return ByteUtil.NUM_BYTES_IN_LONG + ByteUtil.NUM_BYTES_IN_INT + listenIpLength + ByteUtil.NUM_BYTES_IN_SHORT + ByteUtil.NUM_BYTES_IN_INT + nonceLength;
    }

    public long getExpires() {return expires;}

    public String getListenIp() {return listenIp;}

    public short getListenPort() {return listenPort;}

    public String getNonce() {return nonce;}

    public void setNonce(HgtpHeader hgtpHeader, String nonce) {
        this.nonceLength = nonce.getBytes(StandardCharsets.UTF_8).length;
        this.nonce = nonce;

        hgtpHeader.setBodyLength(hgtpHeader.getBodyLength() + nonceLength);
    }
}
