package com.student.contorller;

import com.student.dto.LearningProgressDTO;
import com.student.entity.LearningProgress;
import com.student.entity.LearningReport;
import com.student.entity.LearningStats;
import com.student.service.service.LearningProgressService;
import com.student.service.service.LearningReportService;
import com.student.service.service.LearningStatsService;
import org.com.entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * @author byh
 */

@RestController
@RequestMapping("/learning")
public class LearningController {

    @Autowired
    private LearningProgressService learningProgressService;

    @Autowired
    private LearningStatsService learningStatsService;

    @Autowired
    private LearningReportService learningReportService;

    // 更新学习进度
    @PostMapping("/progress")
    public Result updateProgress(@RequestBody LearningProgressDTO dto) {
        try {
            learningProgressService.updateProgress(
                    dto.getStudentId(),
                    dto.getVideoId(),
                    dto.getWatchDuration(),
                    dto.getProgress(),
                    dto.getLastPosition()
            );
            return Result.success();
        } catch (Exception e) {
            return Result.error("更新进度失败");
        }
    }

    // 获取学习进度
    @GetMapping("/progress/{videoId}")
    public Result getProgress(@PathVariable Long videoId, @RequestParam String studentId) {
        try {
            LearningProgress progress = learningProgressService.getProgress(studentId, videoId);
            return Result.success(progress);
        } catch (Exception e) {
            return Result.error("获取进度失败");
        }
    }

    // 获取学习统计
    @GetMapping("/stats")
    public Result getStats(@RequestParam String studentId,
                           @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                           @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        try {
            LearningStats stats = learningStatsService.getStatsByDateRangeData(studentId, startDate, endDate);
            return Result.success(stats);
        } catch (Exception e) {
            return Result.error("获取统计失败");
        }
    }

    // 生成学习报告
    @PostMapping("/report")
    public Result generateReport(@RequestParam String studentId, @RequestParam Integer reportType) {
        try {
            learningReportService.generateReport(studentId, reportType);
            return Result.success();
        } catch (Exception e) {
            return Result.error("生成报告失败");
        }
    }

    // 获取学习报告列表
    @GetMapping("/reports")
    public Result getReports(
            @RequestParam String studentId,
            @RequestParam Integer reportType,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate
    ) {
        try {
            List<LearningReport> reports = learningReportService.getReports(studentId, reportType, startDate, endDate);
            return Result.success(reports);
        } catch (Exception e) {
            return Result.error("获取报告失败");
        }
    }
}
