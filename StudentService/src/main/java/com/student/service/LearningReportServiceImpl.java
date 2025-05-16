package com.student.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.student.entity.LearningReport;
import com.student.entity.LearningStats;
import com.student.entity.Student;
import com.student.mapper.LearningReportMapper;
import com.student.mapper.StudentMapper;
import com.student.service.service.LearningReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class LearningReportServiceImpl extends ServiceImpl<LearningReportMapper, LearningReport> implements LearningReportService {

    @Autowired
    private LearningProgressServiceImpl learningProgressService;
    @Autowired
    private LearningReportServiceImpl learningReportService;
    @Autowired
    private LearningStatsServiceImpl learningStatsService;
    @Autowired
    private StudentMapper studentMapper;

    @Override
    public void generateReport(String studentId, Integer reportType) {
        if (reportType == 1) {
            saveDailyReport(studentId);
        } else if (reportType == 2) {
            saveWeeklyReport(studentId);
        } else if (reportType == 3) {
            saveMonthlyReport(studentId);
        }
    }

    @Override
    public List<LearningReport> getReports(String studentId, Integer reportType, LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<LearningReport> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LearningReport::getStudentId, studentId)
                .eq(LearningReport::getReportType, reportType)
                .between(LearningReport::getReportDate, startDate, endDate)
                .groupBy(LearningReport::getReportDate)
                .orderByDesc(LearningReport::getReportDate);

        return list(wrapper);
    }


    @Override
    public void generateDailyReport() {
        // 获取所有学生
        List<Student> students = studentMapper.selectList(new LambdaQueryWrapper<>());

        for (Student student : students) {
            saveDailyReport(student.getStudentid());
        }
    }

    private void saveDailyReport(String studentId) {
        // 获取昨天的日期
        LocalDate yesterday = LocalDate.now();

        // 获取昨天的学习数据
        LearningStats stats = learningStatsService.getStatsByDate(studentId, yesterday);

        // 生成学习建议
        String suggestions = generateSuggestions(stats);

        // 保存日报
        LearningReport report = new LearningReport();
        report.setStudentId(studentId);
        report.setReportType(1); // 日报
        report.setReportDate(yesterday);
        report.setWatchTime(stats != null ? stats.getTotalWatchTime() : 0);
        report.setCompletedVideos(stats != null ? stats.getCompletedVideos() : 0);
        report.setSuggestions(suggestions);

        learningReportService.insert(report);
    }

    @Override
    public void generateWeeklyReport() {
        // 获取所有学生
        List<Student> students = studentMapper.selectList(new LambdaQueryWrapper<>());

        for (Student student : students) {
            saveWeeklyReport(student.getStudentid());
        }
    }

    private void saveWeeklyReport(String studentId) {
        // 获取上周的日期范围
        LocalDate weekEnd = LocalDate.now();
        LocalDate weekStart = weekEnd.minusDays(6);
        // 获取上周的学习统计数据
        List<LearningStats> weeklyStatsList = learningStatsService.getStatsByDateRange(studentId, weekStart, weekEnd);

        // 汇总周统计数据
        LearningStats weeklyStats = learningStatsService.aggregateStats(weeklyStatsList, studentId);

        // 生成周报的学习建议
        String suggestions = generateWeeklySuggestions(weeklyStats, weeklyStatsList);

        // 保存周报
        LearningReport report = new LearningReport();
        report.setStudentId(studentId);
        report.setReportType(2); // 周报
        report.setReportDate(weekEnd);
        report.setWatchTime(weeklyStats.getTotalWatchTime());
        report.setCompletedVideos(weeklyStats.getCompletedVideos());
        report.setSuggestions(suggestions);

        learningReportService.insert(report);
    }

    @Override
    public void generateMonthlyReport() {
        // 获取所有学生
        List<Student> students = studentMapper.selectList(new LambdaQueryWrapper<>());

        for (Student student : students) {
            saveMonthlyReport(student.getStudentid());
        }
    }

    private void saveMonthlyReport(String studentId) {
        // 获取上月的日期范围
        LocalDate monthEnd = LocalDate.now();
        LocalDate monthStart = monthEnd.minusDays(30);
        // 获取上月的学习统计数据
        List<LearningStats> monthlyStatsList = learningStatsService.getStatsByDateRange(studentId, monthStart, monthEnd);

        // 汇总月统计数据
        LearningStats monthlyStats = learningStatsService.aggregateStats(monthlyStatsList, studentId);

        // 生成月报的学习建议
        String suggestions = generateMonthlySuggestions(monthlyStats, monthlyStatsList);

        // 保存月报
        LearningReport report = new LearningReport();
        report.setStudentId(studentId);
        report.setReportType(3); // 月报
        report.setReportDate(monthEnd);
        report.setWatchTime(monthlyStats.getTotalWatchTime());
        report.setCompletedVideos(monthlyStats.getCompletedVideos());
        report.setSuggestions(suggestions);

        learningReportService.insert(report);
    }


    private String generateWeeklySuggestions(LearningStats weeklyStats, List<LearningStats> statsList) {
        StringBuilder suggestions = new StringBuilder();

        // 计算平均每天学习时间（小时）
        double avgDailyWatchTime = weeklyStats.getTotalWatchTime() / 7.0 / 3600.0;

        // 计算平均每天完成视频数
        double avgDailyCompletedVideos = weeklyStats.getCompletedVideos() / 7.0;

        // 分析学习时间分布
        Map<LocalDateTime, Integer> dailyWatchTime = statsList.stream()
                .collect(Collectors.toMap(
                        LearningStats::getCreatedAt,
                        LearningStats::getTotalWatchTime
                ));
        int studyDays = dailyWatchTime.size();

        suggestions.append("本周学习情况分析：\n");
        suggestions.append("1. 学习天数：").append(studyDays).append("天\n");
        suggestions.append("2. 平均每天学习：").append(String.format("%.1f", avgDailyWatchTime)).append("小时\n");
        suggestions.append("3. 完成视频数：").append(weeklyStats.getCompletedVideos()).append("个\n");

        // 添加具体建议
        if (studyDays < 5) {
            suggestions.append("\n建议：\n");
            suggestions.append("- 本周学习天数较少，建议保持更稳定的学习频率\n");
        }

        if (avgDailyWatchTime < 1.5) {
            suggestions.append("- 建议每天保持1.5小时以上的学习时间\n");
        }

        if (avgDailyCompletedVideos < 2) {
            suggestions.append("- 建议每天至少完成2个视频的学习\n");
        }

        // 添加学习时间分布分析
        suggestions.append("\n日学习时间：\n");
        dailyWatchTime.forEach((date, time) -> {
            suggestions.append(date.format(DateTimeFormatter.ofPattern("MM-dd")))
                    .append(": ")
                    .append(formatTime(time))
                    .append("\n");
        });

        return suggestions.toString();
    }

    private String generateMonthlySuggestions(LearningStats monthlyStats, List<LearningStats> statsList) {
        StringBuilder suggestions = new StringBuilder();

        // 计算月度统计
        Map<LocalDate, Integer> dailyWatchTime = statsList.stream()
                .collect(Collectors.groupingBy(
                        // 提取周日作为键
                        stats -> {
                            LocalDateTime createdAt = stats.getCreatedAt();
                            LocalDate date = createdAt.toLocalDate();
                            // 找到该日期所在周的周日（若当前是周日，则返回当天）
                            return date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
                        },
                        // 累加总观看时间
                        Collectors.summingInt(LearningStats::getTotalWatchTime)
                ));
        int totalDays = dailyWatchTime.size();
        double avgDailyWatchTime = monthlyStats.getTotalWatchTime() / 30.0 / 3600.0;
        double avgDailyCompletedVideos = monthlyStats.getCompletedVideos() / 30.0;

        suggestions.append("本月学习总结：\n");
        suggestions.append("1. 学习天数：").append(totalDays).append("天\n");
        suggestions.append("2. 总学习时长：").append(formatTime(monthlyStats.getTotalWatchTime())).append("\n");
        suggestions.append("3. 完成视频数：").append(monthlyStats.getCompletedVideos()).append("个\n");
        suggestions.append("4. 平均每天学习：").append(String.format("%.1f", avgDailyWatchTime)).append("小时\n");

        // 添加周统计
        suggestions.append("\n每周学习情况：\n");
        dailyWatchTime.forEach((week, time) -> {
            suggestions.append(week).append("当周: ")
                    .append(formatTime(time))
                    .append("\n");
        });

        // 添加建议
        suggestions.append("\n改进建议：\n");
        if (totalDays < 20) {
            suggestions.append("- 本月学习天数较少，建议提高学习频率\n");
        }
        if (avgDailyWatchTime < 2) {
            suggestions.append("- 建议每天保持2小时以上的学习时间\n");
        }
        if (avgDailyCompletedVideos < 3) {
            suggestions.append("- 建议每天至少完成3个视频的学习\n");
        }
//        suggestions.append("- " + );

        // 添加下月目标
        suggestions.append("\n下月学习目标：\n");
        suggestions.append("1. 学习天数目标：25天以上\n");
        suggestions.append("2. 日均学习时间：2小时以上\n");
        suggestions.append("3. 日均完成视频：3个以上\n");

        return suggestions.toString();
    }

    private String formatTime(int seconds) {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int second = seconds % 60;
        return String.format("%d小时%d分钟%d秒", hours, minutes, second);
    }

    @Override
    public void insert(LearningReport learningReport) {
        baseMapper.insert(learningReport);
    }

    private String generateSuggestions(LearningStats stats) {
        if (stats == null) {
            return "昨日未进行学习";
        }
        StringBuilder suggestions = new StringBuilder();

        // 根据观看时长给出建议
        if (stats.getTotalWatchTime() < 3600) { // 少于1小时
            suggestions.append("建议增加学习时间，每天至少学习1小时。\n");
        }

        // 根据完成视频数给出建议
        if (stats.getCompletedVideos() < stats.getTotalVideos() * 0.5) {
            suggestions.append("建议加快学习进度，争取完成更多视频。\n");
        }

        return suggestions.toString();
    }

}