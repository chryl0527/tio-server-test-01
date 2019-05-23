package com.sdsoon.client;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.tio.client.ClientChannelContext;
import org.tio.client.ClientGroupContext;
import org.tio.client.ReconnConf;
import org.tio.client.TioClient;
import org.tio.client.intf.ClientAioHandler;
import org.tio.client.intf.ClientAioListener;
import org.tio.core.Node;
import org.tio.core.Tio;

import java.io.UnsupportedEncodingException;

/**
 * Created By Chr on 2019/4/16.
 */
@Component
@Order(value = 2)
public class ClientStarter implements CommandLineRunner {

    //连接服务器端的ip+port
    public static Node serverNode = new Node(TcpConfig.HOST, TcpConfig.PORT);
    //handler：
    public static ClientAioHandler clientAioHandler = new ClientHandler();

    //listener：
    public static ClientAioListener clientAioListener = new ClientListener();


    //短链后自动连接，不想自动连接设为null：（long ,int）
    private static ReconnConf reconnConf = new ReconnConf(5000L);
    //context：客户端上下文，客户端连接的是服务端的group？？？，客户端没有group？？？
    public static ClientGroupContext clientGroupContext = new ClientGroupContext(clientAioHandler, clientAioListener, reconnConf);

    //channel：tcp建立通道连接建立之后  就会产生channel，
    public static ClientChannelContext clientChannelContext = null;

    //tioClient客户端入口,等到连接的时候再连接
    public static TioClient tioClient = null;


    public static void start() throws Exception {
        //设置 心跳时间
        clientGroupContext.setHeartbeatTimeout(TcpConfig.TimeOut);
        //打开客户端入口
        tioClient = new TioClient(clientGroupContext);
        //根据连接服务器端，建立通道
        clientChannelContext = tioClient.connect(serverNode);



        //验证server主动发送给client
        binding();
    }


    public static void binding() throws UnsupportedEncodingException {
        RequestPacket requestPacket = new RequestPacket();

        requestPacket.setBody("FE0A4A4E3132715067734D34374550534976099CFE".getBytes(RequestPacket.CHARSET));

        Tio.bindUser(clientChannelContext, "client-01");//client先绑定userId
    }

    public static void send() throws UnsupportedEncodingException {
        RequestPacket requestPacket = new RequestPacket();

        requestPacket.setBody("FE0A4A4E3132715067734D34374550534976099CFE".getBytes(RequestPacket.CHARSET));

        Tio.send(clientChannelContext, requestPacket);
    }

    public static void bindingSend() throws UnsupportedEncodingException {
        RequestPacket requestPacket = new RequestPacket();

        requestPacket.setBody("FE0A4A4E3132715067734D34374550534976099CFE".getBytes(RequestPacket.CHARSET));

        Tio.bindUser(clientChannelContext, "client-01");//client先绑定userId
        Tio.send(clientChannelContext, requestPacket);
    }

    @Override
    public void run(String... strings) throws Exception {
//        System.out.println("tio client start===============================");
//        start();
    }
}
