package com.minibyte.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ScheduledFuture;

/**
 * 可开启和关闭的任务
 *
 * @author: MiniByte
 * @date: 2022/5/30
 * @description:
 */
@Component
public class CloseStartAbleTaskManager {
    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    private Map<String, ScheduledFuture<?>> futureMap = new HashMap<>();

    /**
     * 获取任务管理器状态
     * @return
     */
    public String getTaskManagerStatus(String taskName) {
        ScheduledFuture<?> scheduledFuture = futureMap.get(taskName);
        if (Objects.isNull(scheduledFuture)) {
            return "stopped";
        }
        return "running";
    }

    /**
     * 获取任务状态
     * @return
     */
    public String getTaskStatus(String taskName) {
        ScheduledFuture<?> scheduledFuture = futureMap.get(taskName);
        if (Objects.isNull(scheduledFuture) || scheduledFuture.isDone()) {
            return "stopped";
        }
        return "running";
    }

    /**
     * 开启任务
     * @param task
     */
    public void addTask(MBRunnable task) {
        ScheduledFuture<?> future = futureMap.get(task.getTaskName());
        if (Objects.isNull(future)) {
            future = threadPoolTaskScheduler.schedule(task, task.getTrigger());
            futureMap.put(task.getTaskName(), future);
        }
    }

    /**
     * 停止任务
     */
    public void stopTask(String taskName) {
        ScheduledFuture<?> future = futureMap.get(taskName);
        if (Objects.nonNull(future)) {
            future.cancel(true);
        }
    }
}
