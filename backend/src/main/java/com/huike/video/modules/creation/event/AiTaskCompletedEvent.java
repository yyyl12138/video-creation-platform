package com.huike.video.modules.creation.event;

import com.huike.video.modules.creation.domain.entity.AiTask;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * AI任务完成事件
 */
@Getter
public class AiTaskCompletedEvent extends ApplicationEvent {

    private final AiTask task;

    public AiTaskCompletedEvent(Object source, AiTask task) {
        super(source);
        this.task = task;
    }
}
