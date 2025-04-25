package com.student.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.student.entity.LearningReport;

import java.time.LocalDate;
import java.util.List;

/**
 * @author byh
 */
public interface LearningReportService extends IService<LearningReport> {

    void generateReport(String studentId, Integer reportType);

    List<LearningReport> getReports(String studentId, Integer reportType, LocalDate startDate, LocalDate endDate);

    void generateDailyReport();

    void generateWeeklyReport();

    void generateMonthlyReport();

    void insert(LearningReport learningReport);

}
