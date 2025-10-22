package cn.iocoder.yudao.module.member.dal.dataobject.baby;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 宝宝测评附件 DO
 *
 * @author 芋道源码
 */
@TableName("baby_assessment_file")
@KeySequence("baby_assessment_file_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BabyAssessmentFileDO extends TenantBaseDO {

    /**
     * 附件ID
     */
    @TableId
    private Long id;

    /**
     * 宝宝ID
     */
    private Long babyId;

    /**
     * 测评ID（可为空，表示通用附件）
     */
    private Long assessmentId;

    /**
     * 文件ID（关联infra_file表）
     */
    private Long fileId;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 附件描述
     */
    private String description;

    /**
     * 上传管理员ID
     */
    private Long uploadUserId;

}