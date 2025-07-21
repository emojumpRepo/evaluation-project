package cn.iocoder.yudao.module.emojump.framework.survey.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 外部问卷系统响应VO
 *
 * @author 芋道源码
 */
@Data
public class ExternalSurveyRespVO {

    /**
     * 问卷ID（外部系统的ID）
     */
    @JsonProperty("id")
    private String externalId;

    /**
     * 问卷标题
     */
    @JsonProperty("title")
    private String title;

    /**
     * 问卷描述
     */
    @JsonProperty("description")
    private String description;

    /**
     * 问卷链接
     */
    @JsonProperty("link")
    private String link;

    /**
     * 问卷类型
     */
    @JsonProperty("type")
    private Integer type;

    /**
     * 问卷状态
     */
    @JsonProperty("status")
    private Integer status;

    /**
     * 目标人群
     */
    @JsonProperty("targetAudience")
    private String targetAudience;

    /**
     * 预计时长（分钟）
     */
    @JsonProperty("estimatedDuration")
    private Integer estimatedDuration;

    /**
     * 是否开放
     */
    @JsonProperty("isOpen")
    private Boolean isOpen;

    /**
     * 有效期开始时间
     */
    @JsonProperty("validFrom")
    private LocalDateTime validFrom;

    /**
     * 有效期结束时间
     */
    @JsonProperty("validTo")
    private LocalDateTime validTo;

    /**
     * 备注
     */
    @JsonProperty("remark")
    private String remark;

    /**
     * 最后更新时间
     */
    @JsonProperty("lastModified")
    private LocalDateTime lastModified;

}
