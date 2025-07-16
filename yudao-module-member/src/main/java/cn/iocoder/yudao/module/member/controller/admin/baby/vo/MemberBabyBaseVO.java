package cn.iocoder.yudao.module.member.controller.admin.baby.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY;

/**
 * 宝宝信息 Base VO，提供给添加、修改、详细的子 VO 使用
 * 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
 */
@Data
public class MemberBabyBaseVO {

    @Schema(description = "宝宝姓名", requiredMode = Schema.RequiredMode.REQUIRED, example = "小明")
    @NotBlank(message = "宝宝姓名不能为空")
    private String name;

    @Schema(description = "宝宝生日", requiredMode = Schema.RequiredMode.REQUIRED)
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    @JsonFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    @NotNull(message = "宝宝生日不能为空")
    private LocalDate birthday;

    @Schema(description = "性别", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "性别不能为空")
    private Integer gender;

    @Schema(description = "身高(cm)", example = "60.5")
    private BigDecimal height;

    @Schema(description = "体重(kg)", example = "3.5")
    private BigDecimal weight;

    @Schema(description = "生育方式", example = "顺产")
    private String birthType;

    @Schema(description = "监护人", example = "爸爸")
    private String guardian;

    @Schema(description = "宝宝头像", example = "https://www.iocoder.cn/xx.png")
    private String avatar;

} 