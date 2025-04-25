package com.student.service;

import com.student.service.service.LearningReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class LearningReportGenerator {
    @Autowired
    private LearningReportService learningReportService;

    // 每天凌晨1点执行
    @Scheduled(cron = "0 0 1 * * ?")
    public void generateDailyReports() {
        // 生成前一天的日报
        learningReportService.generateDailyReport();
    }

    // 每周一凌晨2点执行
    @Scheduled(cron = "0 0 2 * * 1")
    public void generateWeeklyReports() {
        // 生成上周的周报
        learningReportService.generateWeeklyReport();
    }

    // 每月1号凌晨3点执行
    @Scheduled(cron = "0 0 3 1 * ?")
    public void generateMonthlyReports() {
        // 生成上月的月报
        learningReportService.generateMonthlyReport();
    }
}