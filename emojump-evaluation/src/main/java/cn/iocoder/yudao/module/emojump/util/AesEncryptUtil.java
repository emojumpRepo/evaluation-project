package cn.iocoder.yudao.module.emojump.util;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * AES 加密工具类（用于测试）
 *
 * @author 芋道源码
 */
@Slf4j
public class AesEncryptUtil {

    /**
     * 问卷答案加密密钥
     */
    private static final String ANSWER_AES_ENCRYPT_KEY = "answerAesEncryptEvaluationSecretKey";

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
     * 加密数据（用于测试）
     *
     * @param data 要加密的数据
     * @return 加密后的Base64字符串
     */
    public static String encrypt(String data) {
        try {
            return aesEncrypt(data, ANSWER_AES_ENCRYPT_KEY);
        } catch (Exception e) {
            log.error("[encrypt] 加密数据失败，data: {}", data, e);
            throw new RuntimeException("加密数据失败", e);
        }
    }

    /**
     * 加密ID（用于测试）
     *
     * @param id 要加密的ID
     * @return 加密后的Base64字符串
     */
    public static String encryptId(Long id) {
        return encrypt(String.valueOf(id));
    }

    /**
     * AES 加密
     *
     * @param data 要加密的数据
     * @param key 密钥
     * @return 加密后的Base64字符串
     */
    private static String aesEncrypt(String data, String key) throws Exception {
        // 创建密钥规范
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), ALGORITHM);
        
        // 创建IV参数规范
        IvParameterSpec ivParameterSpec = new IvParameterSpec(IV);
        
        // 创建Cipher实例
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
        
        // 加密
        byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        
        // Base64编码
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    /**
     * 测试方法：生成测试数据
     */
    public static void main(String[] args) {
        try {
            // 测试ID加密
            Long userId = 1024L;
            Long assessmentId = 512L;
            Long questionnaireId = 2048L;
            
            String encryptedUserId = encryptId(userId);
            String encryptedAssessmentId = encryptId(assessmentId);
            String encryptedQuestionnaireId = encryptId(questionnaireId);
            
            System.out.println("加密结果：");
            System.out.println("userId: " + userId + " -> " + encryptedUserId);
            System.out.println("assessmentId: " + assessmentId + " -> " + encryptedAssessmentId);
            System.out.println("questionnaireId: " + questionnaireId + " -> " + encryptedQuestionnaireId);
            
            // 测试问卷答案加密（新的数组格式）
            String answerData = "[{\"title\":\"我害怕在别的孩子面前做没做过的事情。\",\"answer\":\"从不是这样\",\"index\":1},{\"title\":\"我担心被人取笑。\",\"answer\":\"从不是这样\",\"index\":2}]";
            String encryptedAnswerData = encrypt(answerData);
            System.out.println("新格式answerData: " + answerData);
            System.out.println("encryptedAnswerData: " + encryptedAnswerData);

            // 测试旧格式兼容性
            String oldAnswerData = "{\"1\":{\"title\":\"我害怕在别的孩子面前做没做过的事情。\",\"answer\":\"从不是这样\"},\"2\":{\"title\":\"我担心被人取笑。\",\"answer\":\"从不是这样\"}}";
            String encryptedOldAnswerData = encrypt(oldAnswerData);
            System.out.println("旧格式answerData: " + oldAnswerData);
            System.out.println("encryptedOldAnswerData: " + encryptedOldAnswerData);
            
            // 验证解密
            System.out.println("\n解密验证：");
            System.out.println("解密userId: " + AesDecryptUtil.decryptId(encryptedUserId));
            System.out.println("解密assessmentId: " + AesDecryptUtil.decryptId(encryptedAssessmentId));
            System.out.println("解密questionnaireId: " + AesDecryptUtil.decryptId(encryptedQuestionnaireId));
            System.out.println("解密answerData: " + AesDecryptUtil.decryptAnswerData(encryptedAnswerData));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
