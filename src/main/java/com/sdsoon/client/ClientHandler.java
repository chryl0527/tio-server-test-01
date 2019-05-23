package com.sdsoon.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.client.intf.ClientAioHandler;
import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;
import org.tio.core.Tio;
import org.tio.core.exception.AioDecodeException;
import org.tio.core.intf.Packet;
import org.tio.utils.lock.SetWithLock;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Set;

/**
 * 客户端的处理类:与ServerHandler一样
 * <p>
 * Created By Chr on 2019/4/16.
 */
public class ClientHandler implements ClientAioHandler {

    private static Logger log = LoggerFactory.getLogger(ClientHandler.class);

    /**
     * 解码：
     *
     * @param buffer
     * @param limit
     * @param position
     * @param readableLength
     * @param channelContext
     * @return
     * @throws AioDecodeException
     */
    @Override
    public Packet decode(ByteBuffer buffer, int limit, int position, int readableLength, ChannelContext channelContext) throws AioDecodeException {

        //拿到client的packet,对比收到的消息头格式
        if (readableLength < RequestPacket.HANDER_LENGTH) {
            return null;
        }

        //格式正确，操作消息体
        //缓冲区当前位置的 int 值
        int bodyLength = buffer.getInt();
        //消息体格式不正确
        if (bodyLength < 0) {
            throw new AioDecodeException//
                    ("bodyLength [" + bodyLength + "] is not right, remote:" + channelContext.getClientNode());
        }
        //本次接收的数据需要的 缓冲区的长度(总长度=消息头长度+消息体长度)
        int neededLength = RequestPacket.HANDER_LENGTH + bodyLength;
        //验证 本地收到的 数据是否足够组包：防止发生 半包 和 粘包
        int isDataEnough = readableLength - neededLength;
        //不够消息体长度，无法用buffer组合
        /**
         * 这里修改了:未 +4
         */
        if (isDataEnough + 4 < 0) {
            return null;
        } else {//组包成功

            RequestPacket requestPacket = new RequestPacket();
            if (bodyLength > 0) {
                //本次接受的 位置的int值
                byte[] bytes = new byte[bodyLength];
//                byte[] bytes = new byte[Integer.MAX_VALUE];
                buffer.get(bytes);
                requestPacket.setBody(bytes);
            }
            return requestPacket;
        }

    }

    /**
     * 编码
     *
     * @param packet
     * @param groupContext
     * @param channelContext
     * @return
     */
    @Override
    public ByteBuffer encode(Packet packet, GroupContext groupContext, ChannelContext channelContext) {

        RequestPacket requestPacket = (RequestPacket) packet;

        //要发送的数据对象，以字节数组byte[]放在Packet的body中
        byte[] body = requestPacket.getBody();


        int bodyLength = 0;
        if (body != null) {
            bodyLength = body.length;
        }
        //byteBuffer的总长度=消息头长度（headLen）+消息体长度（bodyLen）
        /***
         * 这里修改了
         */
//        int byteBufferLen = RequestPacket.HANDER_LENGTH + bodyLength;
        int byteBufferLen = RequestPacket.HANDER_LENGTH + bodyLength - 4;
        //初始化新的ByteBuffer
        ByteBuffer buffer = ByteBuffer.allocate(byteBufferLen);
        //设置字节序：？？？？？？
        //新的字节顺序，要么是 BIG_ENDIAN，要么是 LITTLE_ENDIAN
//        buffer.order(groupContext.getByteOrder());
        buffer.order(ByteOrder.BIG_ENDIAN);
        //写入消息头
        buffer.putInt(bodyLength);
        //写入消息体
        if (body != null) {
            /***
             * 这里修改了:框架会把pos改为:pos=4,此时http发送可用
             */
            buffer.position(0);

            //
            buffer.put(body);
        }
        return buffer;
    }

    /**
     * 数据处理
     *
     * @param packet
     * @param channelContext
     * @throws Exception
     */
    @Override
    public void handler(Packet packet, ChannelContext channelContext) throws Exception {

        System.out.println("设备唯一标示:" + channelContext.getGroupContext().getName());
        String userid = channelContext.userid;
        System.out.println(userid);
        Object attribute = channelContext.getAttribute("client-01");
        System.out.println(attribute);
        SetWithLock<ChannelContext> channelContextsByUserid = Tio.getChannelContextsByUserid(channelContext.groupContext, "client-01");
        Set<ChannelContext> obj = channelContextsByUserid.getObj();
        for(ChannelContext c:obj){
            System.out.println(c.getAttribute());
            System.out.println();
        }
        //#######################################################//
        //接受 server发送来的 数据
        RequestPacket requestPacket = (RequestPacket) packet;

        //得到包装的数据
        byte[] body = requestPacket.getBody();
        String s1 = byte2Hex(body);

        if (body != null) {
            String s = new String(body, RequestPacket.CHARSET);
            System.out.println("(s):" + s);
            System.err.println(" 客户端 收到 服务端的回执 (s1): " + s1);
            log.info("客户端 收到 服务端的回执(s1):{}", s1);


            //循环发送
            //Tio.send(channelContext, requestPacket);
        }

    }

    /**
     * 心跳包:在此处定义心跳发送的内容
     *
     * @return
     */
    @Override
    public Packet heartbeatPacket() {
        RequestPacket requestPacket = new RequestPacket();
        //发送的心跳包
        requestPacket.setBody("AAAAAAAAAA".getBytes());
        return requestPacket;
    }


    /**
     * 将byte[]转为16进制:不分割
     * FE0A4A4E3132715067734D34374550534976099CFE
     *
     * @param bytes
     * @return
     */
    public static String byte2Hex(byte[] bytes) {

        StringBuffer stringBuffer = new StringBuffer();
        String temp = null;

        for (int x = 0; x < bytes.length; x++) {
            temp = Integer.toHexString(bytes[x] & 0xFF);
            if (temp.length() == 1) {
                //1得到一位的进行补0操作
                stringBuffer.append("0");
            }
            stringBuffer.append(temp);
        }
        return stringBuffer.toString().toUpperCase();//转为大写
    }


}
