package com.student.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.student.entity.LearningProgress;

/**
 * @author byh
 */
public interface LearningProgressService extends IService<LearningProgress> {

    void updateProgress(String studentId, Long videoId, Integer watchDuration, Float progress, Integer lastPosition);

    LearningProgress getProgress(String studentId, Long videoId);

}
