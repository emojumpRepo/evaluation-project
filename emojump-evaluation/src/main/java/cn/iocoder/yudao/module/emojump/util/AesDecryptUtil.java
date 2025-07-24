package cn.iocoder.yudao.module.emojump.util;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * AES 解密工具类
 *
 * @author 芋道源码
 */
@Slf4j
public class AesDecryptUtil {

    /**
     * 问卷答案加密密钥
     */
    private static final String ANSWER_AES_ENCRYPT_KEY = "ansAesEncryptEvaluationSecretKey";

    /**
     * AES 算法
     */
    private static final String ALGORITHM = "AES";

    /**
     * AES/CBC/PKCS5Padding 转换
     */
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";

    /**
     * 16字节IV全0
     */
    private static final byte[] IV = new byte[16];

    /**
     * 解密问卷答案数据
     *
     * @param encryptedData 加密的数据
     * @return 解密后的JSON字符串
     */
    public static String decryptAnswerData(String encryptedData) {
        try {
            return aesDecrypt(encryptedData, ANSWER_AES_ENCRYPT_KEY);
        } catch (Exception e) {
            log.error("[decryptAnswerData] 解密问卷答案数据失败，encryptedData: {}", encryptedData, e);
            throw new RuntimeException("解密问卷答案数据失败", e);
        }
    }

    /**
     * 解密ID数据（用户ID、测评ID、问卷ID等）
     *
     * @param encryptedId 加密的ID
     * @return 解密后的ID
     */
    public static Long decryptId(String encryptedId) {
        try {
            String decryptedStr = aesDecrypt(encryptedId, ANSWER_AES_ENCRYPT_KEY);
            return Long.parseLong(decryptedStr.trim());
        } catch (Exception e) {
            log.error("[decryptId] 解密ID失败，encryptedId: {}", encryptedId, e);
            throw new RuntimeException("解密ID失败", e);
        }
    }

    /**
     * 批量解密ID
     *
     * @param encryptedUserId 加密的用户ID
     * @param encryptedAssessmentId 加密的测评ID
     * @param encryptedQuestionnaireId 加密的问卷ID
     * @return 解密后的ID数组 [userId, assessmentId, questionnaireId]
     */
    public static Long[] decryptIds(String encryptedUserId, String encryptedAssessmentId, String encryptedQuestionnaireId) {
        try {
            Long userId = decryptId(encryptedUserId);
            Long assessmentId = decryptId(encryptedAssessmentId);
            Long questionnaireId = decryptId(encryptedQuestionnaireId);

            log.debug("[decryptIds] 解密ID成功，userId: {}, assessmentId: {}, questionnaireId: {}",
                    userId, assessmentId, questionnaireId);

            return new Long[]{userId, assessmentId, questionnaireId};
        } catch (Exception e) {
            log.error("[decryptIds] 批量解密ID失败", e);
            throw new RuntimeException("批量解密ID失败", e);
        }
    }

    /**
     * AES 解密
     *
     * @param encryptedData 加密的数据（Base64编码）
     * @param key 密钥
     * @return 解密后的字符串
     */
    private static String aesDecrypt(String encryptedData, String key) throws Exception {
        // 创建密钥规范
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), ALGORITHM);
        
        // 创建IV参数规范
        IvParameterSpec ivParameterSpec = new IvParameterSpec(IV);
        
        // 创建Cipher实例
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
        
        // Base64解码
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedData);
        
        // 解密
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    /**
     * 验证解密是否成功（检查是否为有效的问卷答案格式）
     *
     * @param decryptedData 解密后的数据
     * @return 是否为有效的问卷答案格式
     */
    public static boolean isValidJson(String decryptedData) {
        try {
            if (decryptedData == null) {
                return false;
            }

            String trimmed = decryptedData.trim();

            // 支持新的数组格式 [...]
            boolean isArrayFormat = trimmed.startsWith("[") && trimmed.endsWith("]");
            // 兼容旧的对象格式 {...}
            boolean isObjectFormat = trimmed.startsWith("{") && trimmed.endsWith("}");

            return isArrayFormat || isObjectFormat;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 验证ID是否有效
     *
     * @param id 解密后的ID
     * @return 是否为有效的ID
     */
    public static boolean isValidId(Long id) {
        return id != null && id > 0;
    }

    /**
     * 验证所有ID是否有效
     *
     * @param ids ID数组
     * @return 是否所有ID都有效
     */
    public static boolean areValidIds(Long... ids) {
        if (ids == null || ids.length == 0) {
            return false;
        }

        for (Long id : ids) {
            if (!isValidId(id)) {
                return false;
            }
        }

        return true;
    }

}
