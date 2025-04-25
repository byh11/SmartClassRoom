package com.student.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.student.entity.LearningStats;

import java.time.LocalDate;
import java.util.List;

/**
 * @author byh
 */
public interface LearningStatsService extends IService<LearningStats> {

    void updateStats(String studentId, LocalDate date);

    LearningStats getStats(String studentId);

    LearningStats getStatsByDate(String student, LocalDate yesterday);

    List<LearningStats> getStatsByDateRange(String studentId, LocalDate weekStart, LocalDate weekEnd);

    LearningStats getStatsByDateRangeData(String studentId, LocalDate weekStart, LocalDate weekEnd);
}
