package mao.t3;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
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
 * Package(包名): mao.t3
 * Class(类名): Server
 * Author(作者）: mao
 * Author QQ：1296193245
 * GitHub：https://github.com/maomao124/
 * Date(创建日期)： 2023/4/24
 * Time(创建时间)： 14:27
 * Version(版本): 1.0
 * Description(描述)： 让应用易诊断-Netty日志
 * Netty中加入日志的方式也是通过pipeline中添加handle的方式实现的，
 * 所以在不同的位置加入日志打印的数据是不同的，所以我们可以把控住日志在pipeline中加入的位置来帮组我们进行诊断Netty应用程序
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
                        //日志打印：加入在消息未解码之前
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
