package com.sdsoon.server;

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


    //###################################################//
    //可以多个协议,判断之后,在进行解码编码
//    默认设备 LoRaWAN模块
//    网络IO 二维码添加
//    LoRa模块 LoRa集中器
//    CoAP/NB-
//    IoT 电信CoAP/NB-
//    IoT PLC云网关
//    public String DefaultTcp;
//    public String LoRaTcp;
//    public String CoAP;
//    public String IoT;


    public static void main(String args[]) {
        String s="2143432151";
        Integer i=new Integer(12);
        String s1 = i.toHexString(16);
        System.out.println(s1);
    }
}
