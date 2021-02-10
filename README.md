# My-RPC-Framework

![](https://img.shields.io/badge/building-passing-green.svg)
![GitHub](https://img.shields.io/badge/license-MIT-yellow.svg)
![jdk](https://img.shields.io/static/v1?label=oraclejdk&message=8&color=blue)

My-RPC-Framework 是一款基于 Nacos 实现的 RPC 框架。使用 Netty进行网络传输的实现并提供接口用以扩展，并且实现了多种序列化与负载均衡算法。

## 学习资源

### rpc框架

[一起写个Dubbo](https://blog.csdn.net/qq_40856284/category_10138756.html) 

[guide-rpc](https://github.com/NiceSeason/guide-rpc-framework)

### 动态代理

[基于子类和cglib的动态代理](https://niceseason.github.io/2020/05/18/%E5%8A%A8%E6%80%81%E4%BB%A3%E7%90%86/)

[代理模式|静态代理|动态代理](https://snailclimb.gitee.io/javaguide/#/docs/java/basis/%E4%BB%A3%E7%90%86%E6%A8%A1%E5%BC%8F%E8%AF%A6%E8%A7%A3)

### netty

[Java I/O模型从BIO到NIO和Reactor模式](http://www.jasongj.com/java/nio_reactor/)

[【NIO系列】——之Reactor模型](https://my.oschina.net/u/1859679/blog/1844109)

[《Netty 实战(精髓)》](https://waylau.com/essential-netty-in-action/)

[《Netty 4.x 用户指南》](https://waylau.gitbooks.io/netty-4-user-guide/content/)

[【Netty】(8）---理解ChannelPipeline ](https://www.cnblogs.com/qdhxhz/p/10234908.html)

[netty的option和childOption区别](https://g.yuque.com/simonalong/jishu/wwe8f1)

### 其他

[单例模式](https://mp.weixin.qq.com/s?__biz=MzI1NDQ3MjQxNA==&mid=2247485831&idx=2&sn=bcfc0a3d8b427995eb79d1a1762c55a4&chksm=e9c5f036deb27920566dd5ebe450e8f595a82f046366a06bf6df599eb21cd7fd823b322ca8ba&scene=21#wechat_redirect)

[java注解](https://www.runoob.com/w3cnote/java-annotation.html)

...



## 架构

![系统架构](./images/architecture.png)

消费者调用提供者的方式取决于消费者的客户端选择，如选用原生 Socket 则该步调用使用 BIO，如选用 Netty 方式则该步调用使用 NIO。如该调用有返回值，则提供者向消费者发送返回值的方式同理。

## 特性

- 实现了基于 Netty 传输网络传输方式
- 实现了两种序列化算法，Json 方式、Kryo 算法（默认采用 Kryo方式序列化）
- 实现了两种负载均衡算法：随机算法与轮转算法
- 使用 Nacos 作为注册中心，管理服务提供者信息
- 消费端如采用 Netty 方式，会复用 Channel 避免多次连接
- 如消费端和提供者都采用 Netty 方式，会采用 Netty 的心跳机制，保证连接
- 接口抽象良好，模块耦合度低，网络传输、序列化器、负载均衡算法可配置
- 实现自定义的通信协议
- 服务提供侧自动注册服务

## 项目模块概览

- **java-demo**	——	netty、socket等demo
- **roc-api**	——	通用接口
- **rpc-common**	——	实体对象、工具类等公用类
- **rpc-core**	——	框架的核心实现
- **test-client**	——	测试用消费侧
- **test-server**	——	测试用提供侧

## 传输协议（MRF协议）

调用参数与返回值的传输采用了如下 MRF 协议（ My-RPC-Framework 首字母）以防止粘包：

```
+---------------+---------------+-----------------+-------------+
|  Magic Number |  Package Type | Serializer Type | Data Length |
|    4 bytes    |    4 bytes    |     4 bytes     |   4 bytes   |
+---------------+---------------+-----------------+-------------+
|                          Data Bytes                           |
|                   Length: ${Data Length}                      |
+---------------------------------------------------------------+
```

| 字段            | 解释                                                         |
| :-------------- | :----------------------------------------------------------- |
| Magic Number    | 魔数，表识一个 MRF 协议包，0xCAFEBABE                        |
| Package Type    | 包类型，标明这是一个调用请求还是调用响应                     |
| Serializer Type | 序列化器类型，标明这个包的数据的序列化方式                   |
| Data Length     | 数据字节的长度                                               |
| Data Bytes      | 传输的对象，通常是一个`RpcRequest`或`RpcClient`对象，取决于`Package Type`字段，对象的序列化方式取决于`Serializer Type`字段。 |

## 使用

### 定义调用接口

```java
package top.guoziyang.rpc.api;

public interface HelloService {
    String hello(String name);
}
```

### 在服务提供侧实现该接口

```java
@RpcService
public class HelloServiceImpl implements HelloService {
    private static final Logger logger = LoggerFactory.getLogger(HelloServiceImpl.class);

    @Override
    public String hello(HelloObject object) {
        logger.info("HelloServiceImpl接收到：{}", object.getMessage());
        return object.getId() + "---" + object.getMessage();
    }
}
```

### 编写服务提供者

```java
@RpcScan
public class TestNettyServer {
    public static void main(String[] args) {
        RpcServer rpcServer = new NettyServer("127.0.0.1", 7);
        rpcServer.start();
    }
}
```

### 在服务消费侧远程调用

```java
public class TestNettyClient {
    public static void main(String[] args) {
        RpcClient client = new NettyClient();
        RpcClientProxy proxy = new RpcClientProxy(client);
        HelloService proxy1 = proxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(1, "sada");
        System.out.println(proxy1.hello(object));
    }
}
```

这里客户端也选用了 Netty 的传输方式，序列化方式采用 Kryo 方式，负载均衡策略指定为轮转方式。

### 启动

在此之前请确保 Nacos 运行在本地 `8848` 端口。

首先启动服务提供者，再启动消费者。

## TODO

- 使用接口方式自动注册服务
- 配置文件

## LICENSE

My-RPC-Framework is under the MIT license. See the [LICENSE](https://github.com/NiceSeason/My-RPC-Framework/blob/master/LICENSE) file for details.
