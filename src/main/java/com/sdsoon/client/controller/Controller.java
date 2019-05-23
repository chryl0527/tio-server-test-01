package com.sdsoon.client.controller;

import com.alibaba.fastjson.JSON;
import com.sdsoon.client.RequestPacket;
import com.sdsoon.client.TestBean;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.tio.core.Tio;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import static com.sdsoon.client.ClientStarter.clientChannelContext;

/**
 * Created By Chr on 2019/4/28.
 */
@RestController
@RequestMapping("/tio")
public class Controller {

    @RequestMapping(value = {"/{comm}", ""}, method = RequestMethod.GET)
    public String show(@PathVariable(value = "comm", required = false) String comm) throws UnsupportedEncodingException {
        RequestPacket requestPacket = new RequestPacket();

        if (comm == null) {

            requestPacket.setBody("FE0A4A4E3132715067734D34374550534976099CFE".getBytes(RequestPacket.CHARSET));


            //需要在channel中发送 数据信息//下面可以
            //            Tio.sendToUser(clientChannelContext.getGroupContext(), "tio-client-01",requestPacket);


//            Tio.bindUser(clientChannelContext, "tio-client-01");
            Tio.bindUser(clientChannelContext, "client-01");
            Tio.send(clientChannelContext, requestPacket);
        } else {
            TestBean testBean = new TestBean();
            testBean.setComm(comm);
            testBean.setId(UUID.randomUUID().toString());
            String s = JSON.toJSONString(testBean);
            requestPacket.setBody(s.getBytes(RequestPacket.CHARSET));
            Tio.send(clientChannelContext, requestPacket);
        }

        return "suc";
    }

    /**
     * 当 ByteBuffer.remaining()  小于要读取或写入的长度时，再执行读取或写入操作都会产生异常；
     * <p>
     * 读取则产生 java.nio.BufferUnderflowException 异常，
     * <p>
     * 写入则产生 java.nio.BufferOverflowException 异常。
     * <p>
     * 当 ByteBuffer.remaining()  等于 0 时，不能再执行读取或写入操作，需要执行：clear() 操作，否则将产生异常。
     *
     * @param args
     */
    public static void main(String args[]) {
        String s = "FE0A4A4E3132715067734D34374550534976099CFE";
        byte[] bytes = s.getBytes();

        for (byte b : bytes) {
            System.out.print(b);

        }

//        ByteBuffer byteBuffer = ByteBuffer.allocate(42 * 2);
        /*int remaining = byteBuffer.remaining();
        if (remaining >= s.length()) {
            //就不会出现:Exception in thread "main" java.nio.BufferOverflowException
        }*/
//        byteBuffer.put(bytes);//
//        byteBuffer.putInt(s.length());
//        System.out.println(bytes);
    }
}
