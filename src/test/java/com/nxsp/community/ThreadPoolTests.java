package com.nxsp.community;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class ThreadPoolTests {
    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolTests.class);
    //Jdk普通线程池
    private ExecutorService executorService = Executors.newFixedThreadPool(5);
    //jdk可执行定时任务的线程池
    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);

    //注入Spring普通线程池
    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    //注入Spring可执行定时任务的线程池
    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;

    private void sleep(long m) {
        try {
            Thread.sleep(m);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //JDK
    @Test
    public void test() {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                logger.debug("hello ExecutorService");
            }
        };
        for (int i = 0; i < 10; i++) {
            executorService.submit(task);
        }
        sleep(10000);
    }


    @Test
    public void testScheduledExecutorService() {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                logger.debug("Hello ScheduledExecutorService");
            }
        };
        // 任务，延迟执行时间，执行周期，单位
        // 调用一次，就能从线程池内反复取出线程执行任务
        scheduledExecutorService.scheduleAtFixedRate(task, 10000, 1000, TimeUnit.MILLISECONDS);
        sleep(30000);
    }

    // 3.Spring普通线程池(比JDK普通线程池可以配置核心、最大线程数量，超出还有缓冲)
    @Test
    public void testThreadPoolTaskExecutor() {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                logger.debug("Hello ThreadPoolTaskExecutor");
            }
        };

        // 每次从线程池中取一个线程执行任务
        for (int i = 0; i < 10; i++) {
            taskExecutor.submit(task);
        }

        sleep(10000);
    }

    // 4.Spring定时任务线程池
    @Test
    public void testThreadPoolTaskScheduler() {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                logger.debug("Hello ThreadPoolTaskScheduler");
            }
        };

        Date startTime = new Date(System.currentTimeMillis() + 10000);
        // 任务，任务开始时间，执行任务循环周期
        taskScheduler.scheduleAtFixedRate(task, startTime, 1000);

        sleep(30000);
    }




}
