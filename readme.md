



# Netty优化

## 让应用易诊断

### 完善线程名

Netty中默认的线程组命名方式是`线程组类名-2-1`的方式命名（也就是`nioEventLoopGroup-2-1`），其中的`-2-1`表示boss线程组，`-3-1`表示的work线程组。而`-1-1`的线程表示的是`MultithreadEventExecutorGroup`对象中的`GlobalEventExecutor.INSTANCE`创建的线程组。



```java
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
```





### 完善Handler名称

Netty中默认的handle命名方式是`类名#0`的方式命名，#0表示可能会有多个handle，所以用数字标识，如果我们给handle命名了之后，handle就会以我们起名的方式进行命名



```java
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
```





### Netty日志

Netty中加入日志的方式也是通过pipeline中添加handle的方式实现的，所以在不同的位置加入日志打印的数据是不同的，所以我们可以把控住日志在pipeline中加入的位置来帮组我们进行诊断Netty应用程序



```java
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
```





