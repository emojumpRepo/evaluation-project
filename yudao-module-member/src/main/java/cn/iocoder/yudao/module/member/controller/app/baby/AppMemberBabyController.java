package cn.iocoder.yudao.module.member.controller.app.baby;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.member.controller.app.baby.vo.*;
import cn.iocoder.yudao.module.member.controller.app.baby.vo.file.AppBabyFileRespVO;
import cn.iocoder.yudao.module.member.controller.app.baby.vo.file.AppBabyFileUploadReqVO;
import cn.iocoder.yudao.module.member.controller.admin.baby.vo.babyfile.BabyAssessmentFileCreateReqVO;
import cn.iocoder.yudao.module.member.convert.baby.MemberBabyConvert;
import cn.iocoder.yudao.module.member.dal.dataobject.baby.BabyAssessmentFileDO;
import cn.iocoder.yudao.module.member.dal.dataobject.baby.MemberBabyDO;
import cn.iocoder.yudao.module.member.service.baby.BabyAssessmentFileService;
import cn.iocoder.yudao.module.member.service.baby.MemberBabyService;
import cn.iocoder.yudao.module.infra.service.file.FileService;
import cn.iocoder.yudao.module.infra.dal.dataobject.file.FileDO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "用户 APP - 宝宝信息")
@RestController
@RequestMapping("/member/baby")
@Validated
public class AppMemberBabyController {

    @Resource
    private MemberBabyService babyService;

    @Resource
    private BabyAssessmentFileService babyAssessmentFileService;

    @Resource
    private FileService fileService;

    @PostMapping("/create")
    @Operation(summary = "创建宝宝信息")
    public CommonResult<Long> createBaby(@Valid @RequestBody AppMemberBabyCreateReqVO createReqVO) {
        return success(babyService.createBaby(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新宝宝信息")
    public CommonResult<Boolean> updateBaby(@Valid @RequestBody AppMemberBabyUpdateReqVO updateReqVO) {
        babyService.updateBaby(updateReqVO);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得宝宝信息")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<AppMemberBabyRespVO> getBaby(@RequestParam("id") Long id) {
        MemberBabyDO baby = babyService.getBaby(id);
        return success(MemberBabyConvert.INSTANCE.convertApp(baby));
    }

    @GetMapping("/list-by-user-id")
    @Operation(summary = "根据用户编号获得宝宝信息列表")
    @Parameter(name = "userId", description = "用户编号", required = true, example = "1024")
    public CommonResult<List<AppMemberBabyRespVO>> getBabyListByUserId(@RequestParam("userId") Long userId) {
        List<MemberBabyDO> list = babyService.getBabyListByUserId(userId);
        return success(MemberBabyConvert.INSTANCE.convertAppList(list));
    }

    // ========== 附件相关接口 ==========

    @PostMapping("/file/upload")
    @Operation(summary = "上传宝宝附件")
    public CommonResult<Long> uploadBabyFile(@Valid @RequestBody AppBabyFileUploadReqVO uploadReqVO) {
        // 验证用户权限
        Long currentUserId = SecurityFrameworkUtils.getLoginUserId();
        if (!babyService.isBabyParent(currentUserId, uploadReqVO.getBabyId())) {
            return success(null);
        }

        // 根据文件URL获取文件ID
        FileDO file = fileService.getFileByUrl(uploadReqVO.getFileUrl());
        if (file == null) {
            return success(null);
        }

        // 创建附件记录
        BabyAssessmentFileCreateReqVO createReqVO = new BabyAssessmentFileCreateReqVO();
        createReqVO.setBabyId(uploadReqVO.getBabyId());
        createReqVO.setAssessmentId(uploadReqVO.getAssessmentId());
        createReqVO.setFileId(file.getId());
        createReqVO.setFileName(uploadReqVO.getFileName());
        createReqVO.setFileType(uploadReqVO.getFileType());
        createReqVO.setFileSize(uploadReqVO.getFileSize());
        createReqVO.setDescription(uploadReqVO.getDescription());

        Long id = babyAssessmentFileService.createBabyAssessmentFile(createReqVO);
        return success(id);
    }

    @GetMapping("/file/list")
    @Operation(summary = "获得宝宝附件列表")
    @Parameter(name = "babyId", description = "宝宝ID", required = true, example = "1024")
    public CommonResult<List<AppBabyFileRespVO>> getBabyFileList(@RequestParam("babyId") Long babyId) {
        // 验证用户权限
        Long currentUserId = SecurityFrameworkUtils.getLoginUserId();
        if (!babyService.isBabyParent(currentUserId, babyId)) {
            return success(java.util.Collections.emptyList());
        }

        List<BabyAssessmentFileDO> fileList = babyAssessmentFileService.getBabyAssessmentFileList(babyId);
        List<AppBabyFileRespVO> respList = new java.util.ArrayList<>();

        for (BabyAssessmentFileDO file : fileList) {
            AppBabyFileRespVO respVO = new AppBabyFileRespVO();
            respVO.setId(file.getId());
            respVO.setBabyId(file.getBabyId());
            respVO.setAssessmentId(file.getAssessmentId());
            respVO.setFileName(file.getFileName());
            respVO.setFileType(file.getFileType());
            respVO.setFileSize(file.getFileSize());
            respVO.setDescription(file.getDescription());
            respVO.setCreateTime(file.getCreateTime());

            // 获取文件下载地址
            try {
                // 从 FileService 获取完整的文件URL
                FileDO fileDO = fileService.getFile(file.getFileId());
                if (fileDO != null) {
                    respVO.setFileUrl(fileDO.getUrl());
                } else {
                    respVO.setFileUrl(null);
                }
            } catch (Exception e) {
                // 如果获取文件地址失败，设置为空
                respVO.setFileUrl(null);
            }

            respList.add(respVO);
        }

        return success(respList);
    }

    @GetMapping("/file/download")
    @Operation(summary = "下载宝宝附件")
    @Parameter(name = "id", description = "附件ID", required = true, example = "1024")
    public CommonResult<String> downloadBabyFile(@RequestParam("id") Long id) {
        // 验证权限
        Long currentUserId = SecurityFrameworkUtils.getLoginUserId();
        if (!babyAssessmentFileService.validateFileAccessPermission(id, currentUserId)) {
            return success(null);
        }

        BabyAssessmentFileDO file = babyAssessmentFileService.getBabyAssessmentFile(id);
        if (file == null) {
            return success(null);
        }

        try {
            // 从 FileService 获取完整的文件URL
            FileDO fileDO = fileService.getFile(file.getFileId());
            if (fileDO == null) {
                return success(null);
            }
            return success(fileDO.getUrl());
        } catch (Exception e) {
            return success(null);
        }
    }

    @GetMapping("/file/list-by-assessment")
    @Operation(summary = "获得测评相关附件列表")
    @Parameter(name = "babyId", description = "宝宝ID", required = true, example = "1024")
    @Parameter(name = "assessmentId", description = "测评ID", required = true, example = "2048")
    public CommonResult<List<AppBabyFileRespVO>> getAssessmentFileList(
            @RequestParam("babyId") Long babyId,
            @RequestParam("assessmentId") Long assessmentId) {
        // 验证用户权限
        Long currentUserId = SecurityFrameworkUtils.getLoginUserId();
        if (!babyService.isBabyParent(currentUserId, babyId)) {
            return success(java.util.Collections.emptyList());
        }

        List<BabyAssessmentFileDO> fileList = babyAssessmentFileService.getBabyAssessmentFileList(babyId, assessmentId);
        List<AppBabyFileRespVO> respList = new java.util.ArrayList<>();

        for (BabyAssessmentFileDO file : fileList) {
            AppBabyFileRespVO respVO = new AppBabyFileRespVO();
            respVO.setId(file.getId());
            respVO.setBabyId(file.getBabyId());
            respVO.setAssessmentId(file.getAssessmentId());
            respVO.setFileName(file.getFileName());
            respVO.setFileType(file.getFileType());
            respVO.setFileSize(file.getFileSize());
            respVO.setDescription(file.getDescription());
            respVO.setCreateTime(file.getCreateTime());

            // 获取文件下载地址
            try {
                // 从 FileService 获取完整的文件URL
                FileDO fileDO = fileService.getFile(file.getFileId());
                if (fileDO != null) {
                    respVO.setFileUrl(fileDO.getUrl());
                } else {
                    respVO.setFileUrl(null);
                }
            } catch (Exception e) {
                respVO.setFileUrl(null);
            }

            respList.add(respVO);
        }

        return success(respList);
    }

} 