package com.huike.video.modules.material.listener;

import com.huike.video.modules.creation.event.AiTaskCompletedEvent;
import com.huike.video.modules.material.service.MaterialService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 监听 AI 任务完成事件，同步到素材库
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AiTaskEventListener {

    private final MaterialService materialService;

    @Async
    @EventListener
    public void onAiTaskCompleted(AiTaskCompletedEvent event) {
        log.info("Received AiTaskCompletedEvent for task: {}", event.getTask().getId());
        try {
            materialService.createFromAiResult(event.getTask());
            log.info("Synced AI task result to material library successfully.");
        } catch (Exception e) {
            log.error("Failed to sync AI task result to material library", e);
        }
    }
}
