package mao.t2;

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
 * Package(包名): mao.t2
 * Class(类名): Server
 * Author(作者）: mao
 * Author QQ：1296193245
 * GitHub：https://github.com/maomao124/
 * Date(创建日期)： 2023/4/24
 * Time(创建时间)： 14:21
 * Version(版本): 1.0
 * Description(描述)： 让应用易诊断-完善Handler名称
 * Netty中默认的handle命名方式是`类名#0`的方式命名，#0表示可能会有多个handle，所以用数字标识，
 * 如果我们给handle命名了之后，handle就会以我们起名的方式进行命名
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
                        // 添加Handler名称
                        ch.pipeline().addLast("InboundHandler1", new ChannelInboundHandlerAdapter()
                        {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
                            {
                                log.debug("InboundHandler1");
                                super.channelRead(ctx, msg);
                            }
                        });
                        ch.pipeline().addLast("InboundHandler2", new ChannelInboundHandlerAdapter()
                        {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
                            {
                                log.debug("InboundHandler2");
                                super.channelRead(ctx, msg);
                            }
                        });
                        ch.pipeline().addLast("InboundHandler3", new ChannelInboundHandlerAdapter()
                        {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
                            {
                                log.debug("InboundHandler3");
                                super.channelRead(ctx, msg);
                            }
                        });

                    }
                })
                .bind(8080)
                .sync();
    }
}
