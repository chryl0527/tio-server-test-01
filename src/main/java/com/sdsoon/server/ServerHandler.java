package com.sdsoon.server;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;
import org.tio.core.Tio;
import org.tio.core.exception.AioDecodeException;
import org.tio.core.intf.Packet;
import org.tio.server.intf.ServerAioHandler;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * 处理执行：为服务端的handler，
 * 功能：主要是接受client的数据，并解码
 * 发送服务端的数据，并操作db
 * <p>
 * channelContext是本服务的 channelContext,不是对方发过来的
 * <p>
 * Created By Chr on 2019/4/16.
 */
@Slf4j
public class ServerHandler implements ServerAioHandler {


    /**
     * 解码：
     * 根据ByteBuffer解码成业务需要的Packet对象.
     * 如果收到的数据不全，导致解码失败，请返回null，在下次消息来时框架层会自动续上前面的收到的数据
     *
     * @param buffer         参与本次希望解码的ByteBuffer
     * @param limit          ByteBuffer的limit
     * @param position       ByteBuffer的position，不一定是0哦
     * @param readableLength ByteBuffer参与本次解码的有效数据（= limit - position）
     * @param channelContext
     * @return
     * @throws AioDecodeException
     */
    @Override
    public Packet decode(ByteBuffer buffer, int limit, int position, int readableLength, ChannelContext channelContext) throws AioDecodeException {

        //

        //#####################################//
        //提醒：buffer的开始位置并不一定是0，应用需要从buffer.position()开始读取数据
        //收到的数据组不了业务包，则返回null以告诉框架数据不够
        //拿到client的packet,对比收到的消息头格式
        /**
         * 这里修改了
         */
        /*if (readableLength < RequestPacket.HANDER_LENGTH) {
            return null;
        }*/
        if (readableLength < 0) {
            return null;
        }
        //读取消息体的长度
        //格式正确，操作消息体
        //缓冲区当前位置的 int 值
        /**
         * 这里修改了
         */
//        int bodyLength = buffer.getInt();
        /**
         * 测试
         */
//        int bodyInt = buffer.getInt();

//        buffer.flip();//4

        int bodyLength = buffer.remaining();//17
        //消息体格式不正确//数据不正确，则抛出AioDecodeException异常
        if (bodyLength < 0) {
            throw new AioDecodeException//
                    ("bodyLength [" + bodyLength + "] is not right, remote:" + channelContext.getClientNode());
        }
        //计算本次需要的数据长度
        //本次接收的数据需要的 缓冲区的长度(总长度=消息头长度+消息体长度)
        int neededLength = RequestPacket.HANDER_LENGTH + bodyLength;
        //验证 本地收到的 数据是否足够组包：防止发生 半包 和 粘包
        //收到的数据是否足够组包
        int isDataEnough = readableLength - neededLength;
        //不够消息体长度，无法用buffer组合 // 不够消息体长度(剩下的buffe组不了消息体)
        /**
         * 这里修改了
         */
        if (isDataEnough + 4 < 0) {
            return null;
        } else {//组包成功

            RequestPacket requestPacket = new RequestPacket();
            if (bodyLength > 0) {
                //本次接受的 位置的int值
                byte[] bytes = new byte[bodyLength];
//                byte[] bytes = new byte[Integer.MAX_VALUE>>4];
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
        int byteBufferLen = RequestPacket.HANDER_LENGTH + bodyLength;
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
            buffer.put(body);
        }
        return buffer;
    }

    /**
     * 数据处理:需要进行 一些校验,是否为该协议(16进制,crc校验)
     * <p>
     * 硬件发送给server的数据：为16进制，需要16进制解码
     * client发送给server的数据：目前为byte[]，不需要16进制解码，转为16进制接受给硬件即可
     * </P>
     *
     * @param packet
     * @param channelContext
     * @throws Exception
     */
    @Override
    public void handler(Packet packet, ChannelContext channelContext) throws Exception {
        //这里可以感知 client的心跳包
        System.out.println("客户端心跳包~");
        System.out.println("设备唯一标示:" + channelContext.getGroupContext().getName());
        Object client = channelContext.getAttribute("client-01");
        System.out.println(client);


        //#################################################//
        //接受 client发送来的 数据
        RequestPacket requestPacket = (RequestPacket) packet;

        //得到包装的数据
        byte[] body = requestPacket.getBody();
        //############################################
        //解码：设备发送过来的16进制
        String s1 = byte2Hex(body);
        System.out.println("（硬件发送的数据-16进制解码为）s1:" + s1);
        String s2 = BinaryToHexString(body);
        System.out.println("（硬件发送的数据-16进制解码为）s2:" + s2);
        //#####################################################
        //检验body
        if (body != null) {

            String s = new String(body, RequestPacket.CHARSET);

            try {
                TestBean testBean = JSON.parseObject(s, TestBean.class);
                System.err.println("FastJson：" + testBean.getComm());
            } catch (Exception e) {
                // e.printStackTrace();
            } finally {
                //######################################################
                //这是客户都按发送过来的
                System.err.println(s.length());
                System.err.println(" （未16进制解码）模拟服务端接收的命令数据是:" + s);
                log.info("未16进制解码-数据是：{}", s);
                //######################################################

                //服务端  回执 客户端
                RequestPacket reRequestPacket = new RequestPacket();

                //################################################
                //客户都主动发送消息：数据位16进制解码之后的数据
//                reRequestPacket.setBody((" 模拟硬件接到指令并操作完毕,我已经收到消息=====" + s + "======啦").getBytes());
                reRequestPacket.setBody(s1.getBytes());
                //#####服务端主动发送给client：（1）client先绑定userId（2）服务端绑定发送#####//


                //################################################
                //那个channel发送，就回执给那哪个，binding?测试不绑定也能对应发送
//                Tio.bindUser(channelContext, "client-01");//测试不bind也可以对应发送
//                Tio.send(channelContext, reRequestPacket);


                //#####服务端主动发送给client：（1）server端bind的userId不需要是client的userId（2）服务端bindSend(绑定发送),只有Tio.bindSend才行#####//
                //..server主动发送给client //会发送给所有client端；
//                Tio.bindUser(channelContext, "client-01");
//                Tio.sendToUser(channelContext.groupContext, "client-01", requestPacket);

                Tio.bindUser(channelContext, "server");
                Tio.sendToUser(channelContext.groupContext, "server", requestPacket);


            }

        }
    }
    //=========================================================================//

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


    //=========================================================================//

    /**
     * 将byte[]转为16进制：分割
     * FE 0A 4A 4E 31 32 71 50 67 73 4D 34 37 45 50 53 49 76 09 9C FE
     *
     * @param bytes
     * @return
     */
    //将字节数组转换为16进制字符串
    public static String BinaryToHexString(byte[] bytes) {
        String hexStr = "0123456789ABCDEF";
        String result = "";
        String hex = "";
        for (byte b : bytes) {
            hex = String.valueOf(hexStr.charAt((b & 0xF0) >> 4));
            hex += String.valueOf(hexStr.charAt(b & 0x0F));
            result += hex + " ";//分隔符
        }
        return result;
    }

}
