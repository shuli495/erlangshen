package com.website.Executor;

import com.fastjavaframework.Setting;
import com.fastjavaframework.exception.ThrowException;
import com.fastjavaframework.util.VerifyUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 控制调用接口的qps
 * 根据qps间隔调用接口
 * 超过最大调用数，放入列表，24点后调用
 * @author https://github.com/shuli495/erlangshen
 */
public abstract class AbstractQpsControl implements Runnable {

    private static Map<String, Object> map = new ConcurrentHashMap<>();
    private String controlName;

    protected AtomicInteger getIndex() {
        return (AtomicInteger)map.get(controlName + "Index");
    }

    protected AtomicBoolean getQpsSwitch() {
        return (AtomicBoolean)map.get(controlName + "QpsSwitch");
    }

    public AbstractQpsControl(String controlName) {
        this.controlName = controlName;

        String executor = controlName + "Executor";
        if(!map.containsKey(executor)) {
            map.put(executor, Executors.newCachedThreadPool());
        }

        String index = controlName + "Index";
        if(!map.containsKey(index)) {
            map.put(index, new AtomicInteger(0));
        }

        String qpsSwitch = controlName + "QpsSwitch";
        if(!map.containsKey(qpsSwitch)) {
            map.put(qpsSwitch, new AtomicBoolean(false));
        }
    }

    public void exe() {
        AtomicInteger index = (AtomicInteger)map.get(controlName + "Index");
        ExecutorService executor = (ExecutorService)map.get(controlName + "Executor");

        // 最大调用数
        String qpsMaxStr = Setting.getProperty(controlName + ".qps.max");
        double qpsMaxNum = 0;
        if(VerifyUtils.isNotEmpty(qpsMaxStr)) {
            // 0.05%做缓存，防止24点边界数据溢出
            qpsMaxNum = Integer.valueOf(qpsMaxStr) * 0.95;
        }

        // 未超过接口每日最大调用限制，直接放入线程池
        if(qpsMaxNum < 2 || index.get() <= qpsMaxNum) {
            index.addAndGet(2);
            executor.execute(this);
        } else {
            List list = null;
            String listName = controlName + "List";
            if(!map.containsKey(listName)) {
                list = new LinkedList();
                map.put(listName, list);
            } else {
                list = (List)map.get(listName);
            }

            Thread thread = null;
            String threadName = controlName + "Thread";
            if(map.containsKey(threadName)) {
                thread = (Thread)map.get(threadName);
            }

            // 超出最大限制，存入列表
            list.add(this);

            //24:05将队列中的线程放入线程池
            if(null == thread || !thread.isAlive()) {
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.HOUR, 24);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MINUTE, 5);
                cal.set(Calendar.MILLISECOND, 0);

                AddToThread addToThread = new AddToThread(cal.getTimeInMillis() - System.currentTimeMillis());

                map.put(threadName, addToThread);
                addToThread.start();
            }
        }
    }

    /**
     * 24点后将列表中的线程添加值线程池
     */
    class AddToThread extends Thread {

        private long sleepTime; // 休眠时间，当前时间到24点的毫秒数；到达接口最大限制，等待24点后执行

        public AddToThread(long sleepTime) {
            this.sleepTime = sleepTime;
        }

        public void run() {
            if(sleepTime > 0) {
                try {
                    Thread.sleep(this.sleepTime);
                } catch (InterruptedException e) {
                    throw new ThrowException("线程休眠异常："+ e.getMessage(), "801001");
                }
            }

            // 重置索引
            AtomicInteger index = (AtomicInteger)map.get(controlName + "Index");
            index.set(0);

            // 从头部将线程放入线程池
            List list = (List)map.get(controlName + "List");
            ExecutorService executor = (ExecutorService)map.get(controlName + "Executor");
            while (!list.isEmpty()) {
                index.addAndGet(2);
                executor.execute((Runnable) list.remove(0));
            }
        }
    }
}
