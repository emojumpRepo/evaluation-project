package cn.iocoder.yudao.module.emojump.service.assessmentexport;

import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.emojump.controller.admin.assessmentresult.vo.AssessmentResultRespVO;
import cn.iocoder.yudao.module.emojump.controller.app.assessmentresult.vo.AssessmentExportReqVO;
import cn.iocoder.yudao.module.emojump.controller.app.assessmentresult.vo.AssessmentExportRespVO;
import cn.iocoder.yudao.module.member.dal.dataobject.baby.BabyAssessmentFileDO;
import cn.iocoder.yudao.module.member.dal.dataobject.baby.MemberBabyDO;
import cn.iocoder.yudao.module.emojump.service.assessmentresult.AssessmentResultPdfGenerator;
import cn.iocoder.yudao.module.emojump.service.assessmentresult.AssessmentResultService;
import cn.iocoder.yudao.module.infra.service.file.FileService;
import cn.iocoder.yudao.module.member.service.baby.BabyAssessmentFileService;
import cn.iocoder.yudao.module.member.service.baby.MemberBabyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 测评报告导出 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Slf4j
public class AssessmentExportServiceImpl implements AssessmentExportService {

    @Resource
    private AssessmentResultService assessmentResultService;

    @Resource
    private BabyAssessmentFileService babyAssessmentFileService;

    @Resource
    private MemberBabyService babyService;

    @Resource
    private FileService fileService;

    @Override
    public AssessmentExportRespVO exportAssessmentReport(AssessmentExportReqVO exportReqVO) {
        log.info("[exportAssessmentReport] 开始导出，assessmentId: {}, babyId: {}, exportType: {}",
            exportReqVO.getAssessmentId(), exportReqVO.getBabyId(), exportReqVO.getExportType());

        // 验证用户权限
        Long currentUserId = SecurityFrameworkUtils.getLoginUserId();
        log.info("[exportAssessmentReport] 当前用户ID: {}", currentUserId);
        
        if (currentUserId != null && !babyService.isBabyParent(currentUserId, exportReqVO.getBabyId())) {
            log.warn("[exportAssessmentReport] 权限验证失败，用户 {} 不是宝宝 {} 的父母", currentUserId, exportReqVO.getBabyId());
            AssessmentExportRespVO respVO = new AssessmentExportRespVO();
            respVO.setStatus(AssessmentExportRespVO.ExportStatus.FAILED);
            return respVO;
        }

        String taskId = generateTaskId();
        AssessmentExportRespVO respVO = new AssessmentExportRespVO();
        respVO.setExportTaskId(taskId);
        respVO.setStatus(AssessmentExportRespVO.ExportStatus.PROCESSING);

        try {
            byte[] fileData;
            String fileName;

            log.info("[exportAssessmentReport] 开始处理导出类型: {}", exportReqVO.getExportType());

            switch (exportReqVO.getExportType()) {
                case AssessmentExportReqVO.ExportType.SINGLE_ASSESSMENT_REPORT:
                    // 1. 单个测评结果报告PDF
                    if (exportReqVO.getAssessmentId() == null) {
                        log.error("[exportAssessmentReport] 测评ID为空");
                        throw new RuntimeException("导出单个测评报告时，测评ID不能为空");
                    }
                    log.info("[exportAssessmentReport] 生成单个测评报告PDF，assessmentId: {}, babyId: {}", 
                        exportReqVO.getAssessmentId(), exportReqVO.getBabyId());
                    fileData = generateAssessmentReportPdf(exportReqVO.getAssessmentId(), exportReqVO.getBabyId());
                    fileName = generateFileName(exportReqVO.getBabyId(), "单测评报告", "pdf");
                    log.info("[exportAssessmentReport] 单个测评报告PDF生成成功，文件名: {}", fileName);
                    break;

                case AssessmentExportReqVO.ExportType.ALL_ASSESSMENTS_PDF:
                    // 2. 所有测评的PDF合集
                    log.info("[exportAssessmentReport] 生成所有测评PDF合集");
                    fileData = generateAllAssessmentsPdf(exportReqVO.getBabyId());
                    fileName = generateFileName(exportReqVO.getBabyId(), "全部测评报告", "pdf");
                    break;

                case AssessmentExportReqVO.ExportType.FILES_PACKAGE:
                    // 3. 附件打包ZIP
                    log.info("[exportAssessmentReport] 创建附件ZIP包");
                    fileData = createFilesZip(exportReqVO.getBabyId(), exportReqVO.getAssessmentId());
                    fileName = generateFileName(exportReqVO.getBabyId(), "附件打包", "zip");
                    break;

                case AssessmentExportReqVO.ExportType.COMPLETE_PACKAGE:
                    // 4. 完整报告包（PDF+附件）
                    log.info("[exportAssessmentReport] 创建完整报告包");
                    fileData = createCompletePackage(exportReqVO.getBabyId());
                    fileName = generateFileName(exportReqVO.getBabyId(), "完整报告包", "zip");
                    break;

                default:
                    log.warn("[exportAssessmentReport] 未知的导出类型: {}", exportReqVO.getExportType());
                    respVO.setStatus(AssessmentExportRespVO.ExportStatus.FAILED);
                    return respVO;
            }

            log.info("[exportAssessmentReport] 文件生成成功，大小: {} bytes, 文件名: {}", fileData.length, fileName);

            // 上传文件到文件系统，返回的是完整的文件访问URL
            String downloadUrl = fileService.createFile(fileData, fileName, "assessment-exports", "application/octet-stream");
            log.info("[exportAssessmentReport] 文件上传成功，下载地址: {}", downloadUrl);

            respVO.setDownloadUrl(downloadUrl);
            respVO.setFileName(fileName);
            respVO.setFileSize((long) fileData.length);
            respVO.setStatus(AssessmentExportRespVO.ExportStatus.SUCCESS);

            log.info("[exportAssessmentReport] 导出成功，taskId: {}", taskId);

        } catch (Exception e) {
            log.error("[exportAssessmentReport] 导出失败，taskId: {}, exportType: {}, error: {}", 
                taskId, exportReqVO.getExportType(), e.getMessage(), e);
            respVO.setStatus(AssessmentExportRespVO.ExportStatus.FAILED);
        }

        return respVO;
    }

    @Override
    public byte[] generateAssessmentReportPdf(Long assessmentId, Long babyId) {
        try {
            log.info("[generateAssessmentReportPdf] 开始生成PDF，assessmentId: {}, babyId: {}", assessmentId, babyId);
            
            // 获取测评结果
            AssessmentResultRespVO result = assessmentResultService.getLatestAssessmentResult(assessmentId, babyId);
            if (result == null) {
                log.error("[generateAssessmentReportPdf] 未找到测评结果，assessmentId: {}, babyId: {}", assessmentId, babyId);
                throw new RuntimeException("未找到测评结果，请先完成测评");
            }

            log.info("[generateAssessmentReportPdf] 找到测评结果，ID: {}, status: {}, assessmentTitle: {}", 
                result.getId(), result.getStatus(), result.getAssessmentTitle());

            // 生成PDF
            try {
                byte[] pdfData = AssessmentResultPdfGenerator.generate(result);
                log.info("[generateAssessmentReportPdf] PDF生成成功，大小: {} bytes", pdfData.length);
                return pdfData;
            } catch (Exception pdfEx) {
                log.error("[generateAssessmentReportPdf] PDF生成器失败，尝试使用简化版PDF生成，error: {}", pdfEx.getMessage(), pdfEx);
                // 如果PDF生成失败（比如缺少字体），生成一个简化的PDF
                return generateSimplePdf(result);
            }

        } catch (Exception e) {
            log.error("[generateAssessmentReportPdf] 生成PDF失败，assessmentId: {}, babyId: {}, error: {}",
                assessmentId, babyId, e.getMessage(), e);
            throw new RuntimeException("生成PDF失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 生成简化版PDF（不依赖中文字体）
     */
    private byte[] generateSimplePdf(AssessmentResultRespVO result) throws IOException {
        try (org.apache.pdfbox.pdmodel.PDDocument document = new org.apache.pdfbox.pdmodel.PDDocument();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            
            org.apache.pdfbox.pdmodel.PDPage page = new org.apache.pdfbox.pdmodel.PDPage();
            document.addPage(page);
            
            try (org.apache.pdfbox.pdmodel.PDPageContentStream contentStream = 
                    new org.apache.pdfbox.pdmodel.PDPageContentStream(document, page)) {
                
                contentStream.beginText();
                contentStream.setFont(org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA_BOLD, 16);
                contentStream.newLineAtOffset(50, 750);
                contentStream.showText("Assessment Report");
                contentStream.endText();
                
                contentStream.beginText();
                contentStream.setFont(org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA, 12);
                contentStream.setLeading(14.5f);
                contentStream.newLineAtOffset(50, 720);
                
                contentStream.showText("Assessment ID: " + result.getAssessmentId());
                contentStream.newLine();
                contentStream.showText("Baby ID: " + result.getBabyId());
                contentStream.newLine();
                contentStream.showText("Baby Name: " + (result.getBabyName() != null ? result.getBabyName() : ""));
                contentStream.newLine();
                if (result.getOverallScore() != null) {
                    contentStream.showText("Score: " + result.getOverallScore());
                    contentStream.newLine();
                }
                if (result.getOverallLevel() != null) {
                    contentStream.showText("Level: " + result.getOverallLevel());
                    contentStream.newLine();
                }
                contentStream.showText("Status: " + (result.getStatus() == 1 ? "Completed" : "In Progress"));
                contentStream.newLine();
                if (result.getCompletedTime() != null) {
                    contentStream.showText("Completed: " + result.getCompletedTime().toString());
                }
                
                contentStream.endText();
            }
            
            document.save(out);
            log.info("[generateSimplePdf] 简化版PDF生成成功");
            return out.toByteArray();
        }
    }

    @Override
    public byte[] createZipPackage(Long assessmentId, Long babyId) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(bos)) {

            // 1. 添加PDF报告
            byte[] pdfData = generateAssessmentReportPdf(assessmentId, babyId);
            ZipEntry pdfEntry = new ZipEntry("测评报告.pdf");
            zos.putNextEntry(pdfEntry);
            zos.write(pdfData);
            zos.closeEntry();

            // 2. 添加附件文件
            List<BabyAssessmentFileDO> fileList = babyAssessmentFileService.getBabyAssessmentFileList(babyId, assessmentId);
            for (BabyAssessmentFileDO file : fileList) {
                try {
                    // FileService 没有直接根据ID获取文件内容的方法，这里暂时跳过
                    // 在实际项目中需要根据文件配置和路径获取文件内容
                    log.warn("[createZipPackage] 获取文件内容功能待实现，fileId: {}, fileName: {}",
                        file.getFileId(), file.getFileName());
                } catch (Exception e) {
                    log.warn("[createZipPackage] 添加文件失败，fileId: {}, fileName: {}, error: {}",
                        file.getFileId(), file.getFileName(), e.getMessage());
                }
            }

            zos.finish();
            return bos.toByteArray();

        } catch (Exception e) {
            log.error("[createZipPackage] 创建ZIP包失败，assessmentId: {}, babyId: {}, error: {}",
                assessmentId, babyId, e.getMessage(), e);
            throw new RuntimeException("创建ZIP包失败: " + e.getMessage());
        }
    }

    @Override
    public AssessmentExportRespVO getExportStatus(String taskId) {
        // TODO: 实现获取导出状态的逻辑，可以从缓存或数据库中查询
        AssessmentExportRespVO respVO = new AssessmentExportRespVO();
        respVO.setExportTaskId(taskId);
        respVO.setStatus(AssessmentExportRespVO.ExportStatus.SUCCESS);
        return respVO;
    }

    private String generateTaskId() {
        return "EXPORT_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 10000);
    }

    /**
     * 生成所有测评的PDF合集
     */
    private byte[] generateAllAssessmentsPdf(Long babyId) {
        try {
            // 获取宝宝所有的测评结果
            List<AssessmentResultRespVO> allResults = assessmentResultService.getAllAssessmentResultsByBabyId(babyId);
            
            if (allResults == null || allResults.isEmpty()) {
                throw new RuntimeException("该宝宝暂无测评结果");
            }

            // 合并所有PDF
            return AssessmentResultPdfGenerator.generateMultiple(allResults);

        } catch (Exception e) {
            log.error("[generateAllAssessmentsPdf] 生成所有测评PDF失败，babyId: {}, error: {}",
                babyId, e.getMessage(), e);
            throw new RuntimeException("生成所有测评PDF失败: " + e.getMessage());
        }
    }

    /**
     * 创建完整报告包（包含所有PDF报告和附件）
     */
    private byte[] createCompletePackage(Long babyId) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(bos)) {

            // 1. 添加所有测评的PDF报告
            byte[] allPdfData = generateAllAssessmentsPdf(babyId);
            ZipEntry pdfEntry = new ZipEntry("测评报告合集.pdf");
            zos.putNextEntry(pdfEntry);
            zos.write(allPdfData);
            zos.closeEntry();

            // 2. 添加所有附件
            List<BabyAssessmentFileDO> allFiles = babyAssessmentFileService.getBabyAssessmentFileList(babyId);
            if (allFiles != null && !allFiles.isEmpty()) {
                // 创建附件子目录
                for (BabyAssessmentFileDO file : allFiles) {
                    try {
                        byte[] fileContent = getFileContent(file);
                        if (fileContent != null && fileContent.length > 0) {
                            String entryName = "附件/" + sanitizeFileName(file.getFileName());
                            ZipEntry fileEntry = new ZipEntry(entryName);
                            zos.putNextEntry(fileEntry);
                            zos.write(fileContent);
                            zos.closeEntry();
                        }
                    } catch (Exception e) {
                        log.warn("[createCompletePackage] 添加附件失败，fileId: {}, fileName: {}, error: {}",
                            file.getFileId(), file.getFileName(), e.getMessage());
                    }
                }
            }

            zos.finish();
            return bos.toByteArray();

        } catch (Exception e) {
            log.error("[createCompletePackage] 创建完整报告包失败，babyId: {}, error: {}",
                babyId, e.getMessage(), e);
            throw new RuntimeException("创建完整报告包失败: " + e.getMessage());
        }
    }

    /**
     * 创建仅包含附件的ZIP文件（增强版）
     */
    private byte[] createFilesZip(Long babyId, Long assessmentId) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(bos)) {

            List<BabyAssessmentFileDO> fileList;
            if (assessmentId != null) {
                // 获取特定测评的附件
                fileList = babyAssessmentFileService.getBabyAssessmentFileList(babyId, assessmentId);
            } else {
                // 获取所有附件
                fileList = babyAssessmentFileService.getBabyAssessmentFileList(babyId);
            }

            if (fileList == null || fileList.isEmpty()) {
                // 创建一个说明文件
                ZipEntry readmeEntry = new ZipEntry("说明.txt");
                zos.putNextEntry(readmeEntry);
                zos.write("暂无附件文件".getBytes("UTF-8"));
                zos.closeEntry();
            } else {
                for (BabyAssessmentFileDO file : fileList) {
                    try {
                        byte[] fileContent = getFileContent(file);
                        if (fileContent != null && fileContent.length > 0) {
                            ZipEntry fileEntry = new ZipEntry(sanitizeFileName(file.getFileName()));
                            zos.putNextEntry(fileEntry);
                            zos.write(fileContent);
                            zos.closeEntry();
                        }
                    } catch (Exception e) {
                        log.warn("[createFilesZip] 添加文件失败，fileId: {}, fileName: {}, error: {}",
                            file.getFileId(), file.getFileName(), e.getMessage());
                    }
                }
            }

            zos.finish();
            return bos.toByteArray();

        } catch (Exception e) {
            log.error("[createFilesZip] 创建附件ZIP失败，babyId: {}, assessmentId: {}, error: {}",
                babyId, assessmentId, e.getMessage(), e);
            throw new RuntimeException("创建附件ZIP失败: " + e.getMessage());
        }
    }

    /**
     * 获取文件内容
     */
    private byte[] getFileContent(BabyAssessmentFileDO file) {
        try {
            // 根据文件ID从文件服务获取内容
            // TODO: 需要根据实际的 FileService 实现来获取文件内容
            // 这里假设文件存储在配置的路径中
            if (file.getFileId() != null) {
                // 暂时返回空，等待实际实现
                log.warn("[getFileContent] 文件内容获取功能待实现，fileId: {}", file.getFileId());
                return new byte[0];
            }
            return new byte[0];
        } catch (Exception e) {
            log.error("[getFileContent] 获取文件内容失败，fileId: {}, error: {}", 
                file.getFileId(), e.getMessage(), e);
            return new byte[0];
        }
    }

    /**
     * 清理文件名，避免ZIP Entry名称冲突和非法字符
     */
    private String sanitizeFileName(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "未命名文件";
        }
        // 移除路径分隔符，避免在ZIP中创建意外的目录结构
        return fileName.replaceAll("[/\\\\]", "_");
    }

    private String generateFileName(Long babyId, String type, String extension) {
        try {
            // 获取宝宝信息
            MemberBabyDO baby = babyService.getBaby(babyId);
            String babyName = baby != null ? baby.getName() : "未知宝宝";

            // 生成文件名
            String timestamp = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

            return String.format("%s_%s_%s.%s", babyName, type, timestamp, extension);

        } catch (Exception e) {
            // 如果获取宝宝信息失败，使用默认名称
            String timestamp = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            return String.format("%s_%s.%s", type, timestamp, extension);
        }
    }

}