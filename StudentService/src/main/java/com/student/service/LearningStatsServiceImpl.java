package com.student.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.student.entity.LearningProgress;
import com.student.entity.LearningStats;
import com.student.mapper.LearningStatsMapper;
import com.student.service.service.LearningStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class LearningStatsServiceImpl extends ServiceImpl<LearningStatsMapper, LearningStats> implements LearningStatsService {

    @Autowired
    private LearningProgressServiceImpl learningProgressService;

    @Override
    @Transactional
    public void updateStats(String studentId, LocalDate date) {
        // 获取或创建学生的学习统计记录
        LearningStats stats = getStatsByDate(studentId, date);
        if (stats == null) {
            stats = new LearningStats();
            stats.setStudentId(studentId);
        }
        List<LearningProgress> progressByStudentId = learningProgressService.getProgressByStudentId(studentId, date);
        int totalVideos = 0;
        int completedVideos = 0;
        int totalWatchTime = 0;
        for (LearningProgress progress : progressByStudentId) {
            if (progress.getProgress() >= 99) {
                completedVideos++;
            }
            totalVideos++;
            totalWatchTime += progress.getWatchDuration();
        }
        // 更新最后学习日期
        stats.setLastStudyDate(LocalDateTime.now());
        stats.setTotalWatchTime(totalWatchTime);
        stats.setCompletedVideos(completedVideos);
        stats.setTotalVideos(totalVideos);

        // 保存更新
        saveOrUpdate(stats);
    }

    @Override
    public LearningStats getStats(String studentId) {
        LambdaQueryWrapper<LearningStats> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LearningStats::getStudentId, studentId);
        return baseMapper.selectOne(queryWrapper);
    }

    @Override
    public LearningStats getStatsByDate(String studentId, LocalDate date) {
        LambdaQueryWrapper<LearningStats> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LearningStats::getStudentId, studentId)
                .apply("DATE(created_at) = {0}", date);
        return baseMapper.selectOne(queryWrapper);
    }

    @Override
    public List<LearningStats> getStatsByDateRange(String studentId, LocalDate weekStart, LocalDate weekEnd) {
        LambdaQueryWrapper<LearningStats> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LearningStats::getStudentId, studentId)
                .apply("DATE(created_at) BETWEEN {0} AND {1}", weekStart, weekEnd)
                .orderByAsc(LearningStats::getCreatedAt);
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public LearningStats getStatsByDateRangeData(String studentId, LocalDate weekStart, LocalDate weekEnd) {
        List<LearningStats> statsByDateRange = getStatsByDateRange(studentId, weekStart, weekEnd);
        LearningStats stats = new LearningStats();
        int totalWatchTime = 0;
        int completedVideos = 0;
        int totalVideos = 0;
        stats.setLastStudyDate(statsByDateRange.get(0).getLastStudyDate());
        for (LearningStats stat : statsByDateRange) {
            totalWatchTime += stat.getTotalWatchTime();
            completedVideos += stat.getCompletedVideos();
            totalVideos += stat.getTotalVideos();
            stats.setLastStudyDate(stats.getLastStudyDate().isAfter(stat.getLastStudyDate()) ? stats.getLastStudyDate() : stat.getLastStudyDate());
        }
        stats.setStudentId(studentId);
        stats.setTotalWatchTime(totalWatchTime);
        stats.setCompletedVideos(completedVideos);
        stats.setTotalVideos(totalVideos);
        return stats;
    }

    public LearningStats aggregateStats(List<LearningStats> statsList) {
        LearningStats weeklyStats = new LearningStats();
        weeklyStats.setStudentId(statsList.get(0).getStudentId());
        weeklyStats.setLastStudyDate(statsList.get(statsList.size() - 1).getLastStudyDate());

        int totalWatchTime = 0;
        int completedVideos = 0;
        int totalVideos = 0;
        for (LearningStats stats : statsList) {
            totalWatchTime += stats.getTotalWatchTime();
            completedVideos += stats.getCompletedVideos();
            totalVideos += stats.getTotalVideos();
        }

        weeklyStats.setTotalWatchTime(totalWatchTime);
        weeklyStats.setCompletedVideos(completedVideos);
        weeklyStats.setTotalVideos(totalVideos);

        return weeklyStats;
    }

}