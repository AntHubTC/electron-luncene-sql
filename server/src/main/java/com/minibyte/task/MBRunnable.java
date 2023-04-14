package com.minibyte.task;

import lombok.Data;
import org.springframework.scheduling.Trigger;

/**
 * @author: MiniByte
 * @date: 2022/5/30
 * @description:
 */
@Data
public abstract class MBRunnable implements Runnable {
    private String taskName;

    public MBRunnable(String taskName) {
        this.taskName = taskName;
    }

    public abstract Trigger getTrigger();
}
