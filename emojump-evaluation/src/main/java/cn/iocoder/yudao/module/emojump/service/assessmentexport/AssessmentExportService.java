package cn.iocoder.yudao.module.emojump.service.assessmentexport;

import cn.iocoder.yudao.module.emojump.controller.app.assessmentresult.vo.AssessmentExportReqVO;
import cn.iocoder.yudao.module.emojump.controller.app.assessmentresult.vo.AssessmentExportRespVO;

import javax.validation.Valid;

/**
 * 测评报告导出 Service 接口
 *
 * @author 芋道源码
 */
public interface AssessmentExportService {

    /**
     * 导出测评报告
     *
     * @param exportReqVO 导出请求
     * @return 导出响应
     */
    AssessmentExportRespVO exportAssessmentReport(@Valid AssessmentExportReqVO exportReqVO);

    /**
     * 生成测评报告PDF
     *
     * @param assessmentId 测评ID
     * @param babyId 宝宝ID
     * @return PDF文件字节数组
     */
    byte[] generateAssessmentReportPdf(Long assessmentId, Long babyId);

    /**
     * 创建ZIP文件包含报告和附件
     *
     * @param assessmentId 测评ID
     * @param babyId 宝宝ID
     * @return ZIP文件字节数组
     */
    byte[] createZipPackage(Long assessmentId, Long babyId);

    /**
     * 获取导出状态
     *
     * @param taskId 任务ID
     * @return 导出响应
     */
    AssessmentExportRespVO getExportStatus(String taskId);

}