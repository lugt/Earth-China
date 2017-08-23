package earth.server.tianjin.Server;

import earth.server.Monitor;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolver;
import io.netty.handler.timeout.IdleStateHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.TimeUnit;

/**
 * Created by Frapo on 2017/1/22.
 * Version :21
 * Earth - Moudule ${PACKAGE_NAME}
 */
public class Tianjin {

    private static final int LONGPORT = 9999;
    private static Log log = LogFactory.getLog(Tianjin.class);

    private int port;

    private static final int READ_IDLE_TIME_OUT = 120; // 读超时
    private static final int WRITE_IDLE_TIME_OUT = 200;// 写超时
    private static final int ALL_IDLE_TIME_OUT = 300; // 所有超时

    public Tianjin(int port) throws InterruptedException {
        this.port = port;
        bind();
    }

    private void bind() throws InterruptedException {

        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(boss, worker);
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
        bootstrap.option(ChannelOption.SO_REUSEADDR, true);
        //通过NoDelay禁用Nagle,使消息立即发出去，不用等待到一定的数据量才发出去
        bootstrap.option(ChannelOption.TCP_NODELAY, true);
        //保持长连接状态
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                ChannelPipeline p = socketChannel.pipeline();
                p.addLast(new XLServerEncoder());
                p.addLast(new XLServerDecoder());
                p.addLast(new TianjinHandler()); //ClassResolvers.cacheDisabled(null)
                //p.addLast();
                p.addLast(new IdleStateHandler(READ_IDLE_TIME_OUT, WRITE_IDLE_TIME_OUT, ALL_IDLE_TIME_OUT, TimeUnit.SECONDS));
            }
        });
        ChannelFuture f = bootstrap.bind(port).sync();
        if (f.isSuccess()) {
            Monitor.logger("[Tianjin] long has started : " + port);
        } else {
            System.exit(1);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Tianjin bootstrap = new Tianjin(LONGPORT);
    }

    public static void main() throws InterruptedException {
        Tianjin bootstrap = new Tianjin(LONGPORT);
    }


}