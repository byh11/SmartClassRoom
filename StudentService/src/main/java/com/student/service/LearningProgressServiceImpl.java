package com.student.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.student.entity.LearningProgress;
import com.student.mapper.LearningProgressMapper;
import com.student.service.service.LearningProgressService;
import com.student.service.service.LearningStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class LearningProgressServiceImpl extends ServiceImpl<LearningProgressMapper, LearningProgress> implements LearningProgressService {

    @Autowired
    private LearningStatsService learningStatsService;

    @Override
    public void updateProgress(String studentId, Long videoId, Integer watchDuration, Float progress, Integer lastPosition) {
        LearningProgress learningProgress = getProgress(studentId, videoId);
        if (learningProgress == null) {
            learningProgress = new LearningProgress();
            learningProgress.setStudentId(studentId);
            learningProgress.setVideoId(videoId);
        }

        learningProgress.setWatchDuration(watchDuration);
        learningProgress.setProgress(progress);
        learningProgress.setLastPosition(lastPosition);

        saveOrUpdate(learningProgress);
        // 更新学习统计
        learningStatsService.updateStats(studentId, LocalDate.now());
    }


    @Override
    public LearningProgress getProgress(String studentId, Long videoId) {
        LambdaQueryWrapper<LearningProgress> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LearningProgress::getStudentId, studentId).eq(LearningProgress::getVideoId, videoId);
        return baseMapper.selectOne(wrapper);
    }

    public List<LearningProgress> getProgressByStudentId(String studentId, LocalDate date) {
        LambdaQueryWrapper<LearningProgress> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LearningProgress::getStudentId, studentId).eq(LearningProgress::getUpdatedAt, date);
        return baseMapper.selectList(wrapper);
    }


}
