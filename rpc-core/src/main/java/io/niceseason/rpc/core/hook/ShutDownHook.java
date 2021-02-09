package io.niceseason.rpc.core.hook;

import io.niceseason.rpc.common.util.NacosUtils;
import io.niceseason.rpc.common.factory.ThreadPoolFactoryUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ShutDownHook {

    private static final ShutDownHook SHUT_DOWN_HOOK = new ShutDownHook();

    public static ShutDownHook getShutDownHook() {
        return SHUT_DOWN_HOOK;
    }

    public void addClearAllHook() {
        log.info("服务关闭后将注销所有服务以及线程池...");
        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            NacosUtils.clearRegistry();
            ThreadPoolFactoryUtils.shutDownAllThreadPool();
        }));
    }
}
