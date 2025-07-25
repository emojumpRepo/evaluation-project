package cn.iocoder.yudao.module.emojump.controller.admin.questionnaireresult.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 问卷结果 Excel VO
 *
 * @author 芋道源码
 */
@Data
public class QuestionnaireResultExcelVO {

    @ExcelProperty("问卷结果编号")
    private Long id;

    @ExcelProperty("测评结果ID")
    private Long assessmentResultId;

    @ExcelProperty("测评ID")
    private Long assessmentId;

    @ExcelProperty("宝宝ID")
    private Long babyId;

    @ExcelProperty("问卷ID")
    private Long questionnaireId;

    @ExcelProperty("问卷结果数据")
    private String resultData;

    @ExcelProperty("用户填写的答案数据")
    private String answerData;

    @ExcelProperty("问卷得分")
    private BigDecimal score;

    @ExcelProperty("问卷评级")
    private String level;

    @ExcelProperty("问卷报告")
    private String report;

    @ExcelProperty("完成时间")
    private LocalDateTime completedTime;

    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}
