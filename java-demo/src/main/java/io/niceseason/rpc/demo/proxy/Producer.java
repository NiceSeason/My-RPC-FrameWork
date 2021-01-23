package io.niceseason.rpc.demo.proxy;

public class Producer implements IProducer {
    public float sale(float money) {
        System.out.println("销售产品，并拿到钱:"+money);
        return money;
    }

    public void afterSale(float money) {
        System.out.println("提供售后服务，并拿到钱:"+money);
    }
}
