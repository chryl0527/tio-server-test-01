package com.sdsoon.client;

import org.tio.core.intf.Packet;

/**
 * 相当于Vo对象
 * <p>
 * Created By Chr on 2019/4/16.
 */
public class RequestPacket extends Packet {

    private static final long serialVersionUID = 4019214290598456792L;

    //头
    public static final int HANDER_LENGTH = 4;

    //编码
    public static final String CHARSET = "UTF-8";

    //数据
    private byte[] body;

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

}
