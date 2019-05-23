package com.sdsoon.server;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.tio.server.ServerGroupContext;
import org.tio.server.TioServer;
import org.tio.server.intf.ServerAioHandler;
import org.tio.server.intf.ServerAioListener;

import java.io.IOException;

/**
 * Created By Chr on 2019/4/16.
 */
@Component
@Order(value = 1)
public class ServerStarter implements CommandLineRunner {

    //handler处理类：编码，解码，消息处理
    public static ServerAioHandler serverAioHandler = new ServerHandler();
    //listener事件监听器：可以为null，但是建议实现自己的接口
    public static ServerAioListener serverAioListener = new ServerListener();

    //context：一组连接共用上下文对象（Handler,Listener）
    public static ServerGroupContext serverGroupContext = new ServerGroupContext("tio-server-context", serverAioHandler, serverAioListener);

    //    tioServer：服务器端 的入口（groupContext）
    public static TioServer tioServer = new TioServer(serverGroupContext);

    //Ip+Port
    //ip：如果不需要绑定ip，可为null
    public static String serverIp = null;
//    public static String serverIp = TcpConfig.HOST;
    //port：监听的端口
    public static int serverPort = TcpConfig.PORT;

    //serverChannelContext
//    ServerChannelContext serverChannelContext=new ServerChannelContext(serverGroupContext,new AsynchronousServerSocketChannel());

    //调用
    public static void start() throws IOException {
        //设置心跳时间
        serverGroupContext.setHeartbeatTimeout(TcpConfig.TimeOut);

        //监听，启动
        tioServer.start(serverIp, serverPort);
        System.err.println(" 服务端已经 开始监听 ~");
    }

    /**
     * 启动
     */
    public static void main(String args[]) throws IOException {
        //设置心跳时间
        serverGroupContext.setHeartbeatTimeout(TcpConfig.TimeOut);

        //监听，启动
        tioServer.start(serverIp, serverPort);
        System.err.println(" 服务端已经 开始监听 ~");


        // show();
    }


    //服务端主动发送
//    public static void show() throws UnsupportedEncodingException {
//
//        RequestPacket r = new RequestPacket();
//        r.setBody("主动发送~".getBytes(RequestPacket.CHARSET));
//
//        ServerChannelContext channelContext = new ServerChannelContext(serverGroupContext);
//
//        //绑定
//        Tio.bindGroup(channelContext,"tio-context");
//        Tio.send(channelContext,r);
//
//    }

    @Override
    public void run(String... strings) throws Exception {
        System.out.println("tio server start===============================");
        start();
    }


}
