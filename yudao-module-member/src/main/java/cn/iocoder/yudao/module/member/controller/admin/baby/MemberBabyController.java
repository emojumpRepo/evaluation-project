package cn.iocoder.yudao.module.member.controller.admin.baby;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.member.controller.admin.baby.vo.MemberBabyPageReqVO;
import cn.iocoder.yudao.module.member.controller.admin.baby.vo.MemberBabyRespVO;
import cn.iocoder.yudao.module.member.controller.admin.baby.vo.babyfile.*;
import cn.iocoder.yudao.module.member.convert.baby.MemberBabyConvert;
import cn.iocoder.yudao.module.member.dal.dataobject.baby.BabyAssessmentFileDO;
import cn.iocoder.yudao.module.member.dal.dataobject.baby.MemberBabyDO;
import cn.iocoder.yudao.module.member.service.baby.BabyAssessmentFileService;
import cn.iocoder.yudao.module.member.service.baby.MemberBabyService;
import cn.iocoder.yudao.module.infra.dal.dataobject.file.FileDO;
import cn.iocoder.yudao.module.infra.dal.mysql.file.FileMapper;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 宝宝信息")
@RestController
@RequestMapping("/member/baby")
@Validated
@Slf4j
public class MemberBabyController {

    @Resource
    private MemberBabyService babyService;

    @Resource
    private BabyAssessmentFileService babyAssessmentFileService;

    @Resource
    private FileMapper fileMapper;

    @Resource
    private AdminUserApi adminUserApi;

    @DeleteMapping("/delete")
    @Operation(summary = "删除宝宝信息")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> deleteBaby(@RequestParam("id") Long id) {
        babyService.deleteBaby(id);
        return success(true);
    }

    @GetMapping("/list")
    @Operation(summary = "获得宝宝信息列表")
    public CommonResult<PageResult<MemberBabyRespVO>> getBabyList(@Valid MemberBabyPageReqVO pageVO) {
        PageResult<MemberBabyDO> pageResult = babyService.getBabyList(pageVO);
        return success(MemberBabyConvert.INSTANCE.convertPage(pageResult));
    }

    // ========== 附件管理相关 ==========

    @PostMapping("/file/upload")
    @Operation(summary = "上传宝宝附件")
    @PreAuthorize("@ss.hasPermission('member:baby:create')")
    public CommonResult<Long> uploadBabyFile(@Valid @RequestBody BabyAssessmentFileCreateReqVO createReqVO) {
        Long fileId = babyAssessmentFileService.createBabyAssessmentFile(createReqVO);
        return success(fileId);
    }

    @PutMapping("/file/update")
    @Operation(summary = "更新宝宝附件")
    @PreAuthorize("@ss.hasPermission('member:baby:update')")
    public CommonResult<Boolean> updateBabyFile(@Valid @RequestBody BabyAssessmentFileUpdateReqVO updateReqVO) {
        babyAssessmentFileService.updateBabyAssessmentFile(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/file/delete")
    @Operation(summary = "删除宝宝附件")
    @Parameter(name = "id", description = "附件ID", required = true)
    @PreAuthorize("@ss.hasPermission('member:baby:delete')")
    public CommonResult<Boolean> deleteBabyFile(@RequestParam("id") Long id) {
        babyAssessmentFileService.deleteBabyAssessmentFile(id);
        return success(true);
    }

    @GetMapping("/file/get")
    @Operation(summary = "获得宝宝附件详情")
    @Parameter(name = "id", description = "附件ID", required = true)
    @PreAuthorize("@ss.hasPermission('member:baby:query')")
    public CommonResult<BabyAssessmentFileRespVO> getBabyFile(@RequestParam("id") Long id) {
        BabyAssessmentFileDO babyFile = babyAssessmentFileService.getBabyAssessmentFile(id);
        return success(convertToRespVO(babyFile));
    }

    @GetMapping("/file/page")
    @Operation(summary = "获得宝宝附件分页")
    @PreAuthorize("@ss.hasPermission('member:baby:query')")
    public CommonResult<PageResult<BabyAssessmentFileRespVO>> getBabyFilePage(@Valid BabyAssessmentFilePageReqVO pageReqVO) {
        PageResult<BabyAssessmentFileDO> pageResult = babyAssessmentFileService.getBabyAssessmentFilePage(pageReqVO);
        return success(convertToPage(pageResult));
    }

    @GetMapping("/file/list-by-baby")
    @Operation(summary = "根据宝宝ID获得附件列表")
    @Parameter(name = "babyId", description = "宝宝ID", required = true)
    @PreAuthorize("@ss.hasPermission('member:baby:query')")
    public CommonResult<List<BabyAssessmentFileRespVO>> getBabyFileListByBabyId(@RequestParam("babyId") Long babyId) {
        List<BabyAssessmentFileDO> fileList = babyAssessmentFileService.getBabyAssessmentFileList(babyId);
        return success(convertToList(fileList));
    }

    @DeleteMapping("/file/batch-delete")
    @Operation(summary = "批量删除宝宝附件")
    @PreAuthorize("@ss.hasPermission('member:baby:delete')")
    public CommonResult<Boolean> deleteBabyFileBatch(@RequestBody List<Long> ids) {
        babyAssessmentFileService.deleteBabyAssessmentFileBatch(ids);
        return success(true);
    }

    // ========== 私有方法 ==========

    private BabyAssessmentFileRespVO convertToRespVO(BabyAssessmentFileDO babyFile) {
        if (babyFile == null) {
            return null;
        }
        BabyAssessmentFileRespVO respVO = new BabyAssessmentFileRespVO();
        respVO.setId(babyFile.getId());
        respVO.setBabyId(babyFile.getBabyId());
        respVO.setAssessmentId(babyFile.getAssessmentId());
        respVO.setFileId(babyFile.getFileId());
        respVO.setFileName(babyFile.getFileName());
        respVO.setFileType(babyFile.getFileType());
        respVO.setFileSize(babyFile.getFileSize());
        respVO.setDescription(babyFile.getDescription());
        respVO.setUploadUserId(babyFile.getUploadUserId());
        respVO.setCreateTime(babyFile.getCreateTime());
        
        // 获取文件URL
        try {
            FileDO file = fileMapper.selectById(babyFile.getFileId());
            if (file != null) {
                respVO.setFileUrl(file.getUrl());
            }
        } catch (Exception e) {
            log.error("[convertToRespVO] 获取文件URL失败，fileId: {}", babyFile.getFileId(), e);
        }
        
        // 获取宝宝名称
        try {
            MemberBabyDO baby = babyService.getBaby(babyFile.getBabyId());
            if (baby != null) {
                respVO.setBabyName(baby.getName());
            }
        } catch (Exception e) {
            log.error("[convertToRespVO] 获取宝宝名称失败，babyId: {}", babyFile.getBabyId(), e);
        }
        
        // 获取上传用户名称
        try {
            if (babyFile.getUploadUserId() != null) {
                cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO user = adminUserApi.getUser(babyFile.getUploadUserId());
                if (user != null) {
                    respVO.setUploadUserName(user.getNickname());
                }
            }
        } catch (Exception e) {
            log.error("[convertToRespVO] 获取上传用户名称失败，uploadUserId: {}", babyFile.getUploadUserId(), e);
        }
        
        return respVO;
    }

    private PageResult<BabyAssessmentFileRespVO> convertToPage(PageResult<BabyAssessmentFileDO> pageResult) {
        if (pageResult == null || pageResult.getList() == null) {
            return new PageResult<>(0L);
        }
        
        List<BabyAssessmentFileRespVO> list = convertToList(pageResult.getList());
        return new PageResult<>(list, pageResult.getTotal());
    }

    private List<BabyAssessmentFileRespVO> convertToList(List<BabyAssessmentFileDO> fileList) {
        if (fileList == null || fileList.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        
        List<BabyAssessmentFileRespVO> result = new java.util.ArrayList<>(fileList.size());
        for (BabyAssessmentFileDO file : fileList) {
            result.add(convertToRespVO(file));
        }
        return result;
    }

} 