package mao.t1;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * Project name(项目名称)：Netty_optimization_easy_to_diagnose
 * Package(包名): mao.t1
 * Class(类名): Server
 * Author(作者）: mao
 * Author QQ：1296193245
 * GitHub：https://github.com/maomao124/
 * Date(创建日期)： 2023/4/24
 * Time(创建时间)： 14:02
 * Version(版本): 1.0
 * Description(描述)： 让应用易诊断-完善线程名
 */

@Slf4j
public class Server
{
    @SneakyThrows
    public static void main(String[] args)
    {
        NioEventLoopGroup bossNioEventLoopGroup =
                new NioEventLoopGroup(2, new DefaultThreadFactory("bossThread"));
        NioEventLoopGroup workNioEventLoopGroup =
                new NioEventLoopGroup(32, new DefaultThreadFactory("workThread"));

        ChannelFuture channelFuture = new ServerBootstrap()
                .group(bossNioEventLoopGroup, workNioEventLoopGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>()
                {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception
                    {
                        ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                        ch.pipeline().addLast(new SimpleChannelInboundHandler<String>()
                        {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception
                            {
                                log.debug("读事件：" + msg);
                            }
                        });
                    }
                })
                .bind(8080)
                .sync();
    }
}
