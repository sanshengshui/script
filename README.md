**待我把代码完善好，就来填充你！**

```
1.server端启动，启动后根据配置信息自动去zk注册自身节点信息。
2.信息注册中如果有业务目录节点则直接添加数据节点，如果没有业务目录节点则新建永久目录节点。
3.sever端注册成功后，启动zk定时心跳监听，如果注册中心down掉后继续提供client服务，server会不断重试，直到注册中心reactive，注册中心恢复
后如果session没有过期则继续使用数据节点提供服务，如果session过期后会重新新建zk数据节点并通过监听机制通知client端。
4.client端可以基于tomcat、jetty或者runnable-jar启动，client启动后会去zk上拉取服务节点信息并保存到本地。
5.每次服务请求，client随机从服务节点中选择一个提供服务。
6.client会监听zk节点信息变化，如果zk注册节点信息有变化，会通知client进行更改，如果zk注册节点连接失败，client会通过保留到本地的服务节点
继续提供服务，并且根据zk watch事件进行尝试重连，直到链接成功。
7.单次请求进行阻塞，数据返回后进行通知并设置超时时间，超时后请求抛弃。
8 在SerializationUtil中用户可以根据自己的需要定制化序列化方式，此处使用protostuff进行传输数据的编解码操作。
```
