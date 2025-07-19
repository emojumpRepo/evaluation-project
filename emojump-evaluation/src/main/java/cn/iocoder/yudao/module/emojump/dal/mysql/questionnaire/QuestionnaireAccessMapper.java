package cn.iocoder.yudao.module.emojump.dal.mysql.questionnaire;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.emojump.dal.dataobject.questionnaire.QuestionnaireAccessDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 问卷访问记录 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface QuestionnaireAccessMapper extends BaseMapperX<QuestionnaireAccessDO> {
}
