package cn.iocoder.yudao.module.emojump.service.assessmentresult;

import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.module.emojump.controller.admin.assessmentresult.vo.AssessmentResultRespVO;
import cn.iocoder.yudao.module.emojump.controller.admin.assessmentresult.vo.QuestionnaireResultRespVO;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * PDF 生成器：将测评结果以现代化版式渲染为多页 PDF，支持中文字体、自动分页、斑马线表格等
 */
public class AssessmentResultPdfGenerator {

    private static final float MARGIN = 50f;
    private static final float LEADING = 18f;
    private static final float CONTENT_WIDTH = PDRectangle.A4.getWidth() - MARGIN * 2;
    private static final float TABLE_ROW_HEIGHT = 20f;
    private static final float BLOCK_SPACING = 16f; // 模块间统一留白（加大）
    private static final float HEADER_GAP = 12f; // 小节标题下间距（加大）
    private static final float PARAGRAPH_GAP = 5f; // 段落间距（加大）
    private static final float CONTENT_INDENT = 12f; // 内容相对于模块标题的统一缩进

    public static byte[] generate(AssessmentResultRespVO result) throws IOException {
        try (PDDocument document = new PDDocument(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PDFont baseFont = loadCjkFont(document);
            PDFont baseFontBold = baseFont; // 简化：中文字体不区分粗体

            RenderContext ctx = new RenderContext(document, baseFont, baseFontBold);
            ctx.newPage();

            // 标题
            drawCenteredTitle(ctx, 20f, "测评报告");
            ctx.y -= BLOCK_SPACING;

            // 基本信息
            drawKeyValue(ctx, 12f, "测评标题", nvl(result.getAssessmentTitle()));
            drawKeyValue(ctx, 12f, "宝宝", nvl(result.getBabyName()) + "  (ID: " + nvl(result.getBabyId()) + ")");
            if (result.getCompletedTime() != null) {
                drawKeyValue(ctx, 12f, "完成时间", result.getCompletedTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            }
            drawDivider(ctx);
            ctx.y -= BLOCK_SPACING;

            // 总体结论（若有分数和等级）
            if (result.getOverallScore() != null || result.getOverallLevel() != null) {
                drawSectionHeader(ctx, 14f, "总体结论");
                if (result.getOverallScore() != null) {
                    drawParagraph(ctx, 12f, "总体得分: " + result.getOverallScore());
                }
                if (result.getOverallLevel() != null) {
                    drawParagraph(ctx, 12f, "总体评级: " + result.getOverallLevel());
                }
                ctx.y -= BLOCK_SPACING;
            }

            // 从 JSON 渲染结构化报告
            OverallReport report = null;
            if (result.getOverallReport() != null && !result.getOverallReport().isEmpty()) {
                try {
                    report = JsonUtils.parseObject(result.getOverallReport(), OverallReport.class);
                } catch (Exception ignore) {
                    // 解析失败则原样输出
                }
            }

            if (report != null) {
                // 发展商概览
                if (report.developmentQuotient != null) {
                    drawSectionHeader(ctx, 14f, "发展商概览");
                    DevelopmentQuotient dq = report.developmentQuotient;
                    if (dq.description != null) {
                        drawWrappedText(ctx, 12f, dq.description, CONTENT_WIDTH);
                    }
                    drawInfoCard(ctx, new String[][]{
                            {"发育商", nvl(dq.value)},
                            {"等级", nvl(dq.level)},
                            {"心理年龄(月)", nvl(dq.mentalAge)},
                            {"实际年龄(月)", nvl(dq.actualAge)}
                    });
                    ctx.y -= BLOCK_SPACING;
                }

                // 问卷得分表
                if (report.questionnaireScores != null && !report.questionnaireScores.isEmpty()) {
                    drawSectionHeader(ctx, 14f, "问卷得分");
                    float[] colWidths = new float[]{CONTENT_WIDTH * 0.7f, CONTENT_WIDTH * 0.3f};
                    drawTableHeader(ctx, 13f, new String[]{"问卷", "分数"}, colWidths);
                    int rowIdx = 0;
                    for (QuestionnaireScore qs : report.questionnaireScores) {
                        drawTableRow(ctx, 12f, new String[]{nvl(qs.questionnaireName), nvl(qs.score)}, colWidths, rowIdx++);
                    }
                    ctx.y -= BLOCK_SPACING;
                }

                // 建议
                if (report.advice != null) {
                    drawSectionHeader(ctx, 14f, "建议");
                    if (report.advice.description != null) {
                        drawWrappedText(ctx, 12f, report.advice.description, CONTENT_WIDTH);
                    }
                    if (report.advice.content != null && !report.advice.content.isEmpty()) {
                        for (String c : report.advice.content) {
                            drawBullet(ctx, 12f, c);
                        }
                    }
                    ctx.y -= BLOCK_SPACING;
                }
            } else if (result.getOverallReport() != null && !result.getOverallReport().isEmpty()) {
                // 无法解析则作为原始文本输出
                drawSectionHeader(ctx, 14f, "总体报告");
                drawWrappedText(ctx, 12f, result.getOverallReport(), CONTENT_WIDTH);
                ctx.y -= BLOCK_SPACING;
            }

            // 如未提供 JSON 的问卷明细，则退回使用问卷列表
            List<QuestionnaireResultRespVO> list = result.getQuestionnaireResults();
            if ((report == null || report.questionnaireScores == null || report.questionnaireScores.isEmpty())
                    && list != null && !list.isEmpty()) {
                drawSectionHeader(ctx, 14f, "问卷得分");
                float[] colWidths = new float[]{CONTENT_WIDTH * 0.7f, CONTENT_WIDTH * 0.3f};
                drawTableHeader(ctx, 13f, new String[]{"问卷", "分数"}, colWidths);
                int rowIdx = 0;
                for (QuestionnaireResultRespVO qr : list) {
                    drawTableRow(ctx, 12f, new String[]{nvl(qr.getQuestionnaireTitle()), nvl(qr.getScore())}, colWidths, rowIdx++);
                }
                ctx.y -= BLOCK_SPACING;
            }

            // 关闭主流并绘制页脚
            ctx.closeContent();
            drawFooterPageNumber(document, 1);

            document.save(out);
            return out.toByteArray();
        }
    }

    private static String nvl(Object obj) {
        return obj == null ? "" : String.valueOf(obj);
    }

    // ======== 渲染方法（RenderContext 支持自动分页） ========

    private static void drawCenteredTitle(RenderContext ctx, float fontSize, String title) throws IOException {
        ctx.ensureSpace(LEADING);
        ctx.content.beginText();
        ctx.content.setFont(ctx.bold, fontSize);
        float titleWidth = getStringWidth(ctx.bold, fontSize, title);
        float startX = MARGIN + (CONTENT_WIDTH - titleWidth) / 2f;
        ctx.content.newLineAtOffset(startX, ctx.y);
        ctx.content.showText(title);
        ctx.content.endText();
        ctx.y -= LEADING;
    }

    private static void drawKeyValue(RenderContext ctx, float fontSize, String key, String value) throws IOException {
        ctx.ensureSpace(LEADING);
        String kv = key + ": ";
        float x = MARGIN + CONTENT_INDENT;
        ctx.content.beginText();
        ctx.content.newLineAtOffset(x, ctx.y);
        ctx.content.setFont(ctx.bold, fontSize);
        ctx.content.showText(kv);
        ctx.content.setFont(ctx.font, fontSize);
        ctx.content.showText(value);
        ctx.content.endText();
        ctx.y -= (LEADING + PARAGRAPH_GAP);
    }

    private static void drawDivider(RenderContext ctx) throws IOException {
        float bottomPad = 10f; // 稍大一些的下边距让分割更自然
        ctx.ensureSpace(1f);
        float lineY = ctx.y;
        ctx.content.moveTo(MARGIN, lineY);
        ctx.content.lineTo(MARGIN + CONTENT_WIDTH, lineY);
        ctx.content.stroke();
        ctx.y = lineY - bottomPad;
    }

    private static void drawSectionHeader(RenderContext ctx, float fontSize, String text) throws IOException {
        ctx.y -= 10f;
        ctx.ensureSpace(LEADING);
        // 加粗效果：中文字体常无独立粗体，这里用多次轻微偏移叠加模拟加粗
        float x = MARGIN;
        float y = ctx.y;
        for (float dx : new float[]{0f, 0.25f, -0.25f}) {
            ctx.content.beginText();
            ctx.content.setFont(ctx.bold, fontSize);
            ctx.content.newLineAtOffset(x + dx, y);
            ctx.content.showText(text);
            ctx.content.endText();
        }
        ctx.y -= (LEADING + HEADER_GAP);
    }

    private static void drawParagraph(RenderContext ctx, float fontSize, String text) throws IOException {
        ctx.ensureSpace(LEADING);
        ctx.content.beginText();
        ctx.content.setFont(ctx.font, fontSize);
        ctx.content.newLineAtOffset(MARGIN + CONTENT_INDENT, ctx.y);
        ctx.content.showText(text);
        ctx.content.endText();
        ctx.y -= (LEADING + PARAGRAPH_GAP);
    }

    private static void drawWrappedText(RenderContext ctx, float fontSize, String text, float maxWidth) throws IOException {
        if (text == null) return;
        String[] paragraphs = text.replace("\r\n", "\n").split("\n");
        for (String p : paragraphs) {
            String line = "";
            for (String word : splitBySpacePreserve(p)) {
                String testLine = line.isEmpty() ? word : line + word;
                if (getStringWidth(ctx.font, fontSize, testLine) > maxWidth) {
                    ctx.ensureSpace(LEADING);
                    writeLine(ctx, fontSize, line);
                    line = word.trim();
                } else {
                    line = testLine;
                }
            }
            if (!line.isEmpty()) {
                ctx.ensureSpace(LEADING);
                writeLine(ctx, fontSize, line);
            }
            ctx.y -= PARAGRAPH_GAP;
        }
    }

    private static String[] splitBySpacePreserve(String s) {
        if (s == null || s.isEmpty()) return new String[]{};
        if (s.contains(" ")) {
            String[] parts = s.split("( )");
            java.util.List<String> out = new java.util.ArrayList<>();
            for (int i = 0; i < parts.length; i++) out.add(parts[i]);
            return out.toArray(new String[0]);
        }
        int step = 12;
        java.util.List<String> chunks = new java.util.ArrayList<>();
        for (int i = 0; i < s.length(); i += step) chunks.add(s.substring(i, Math.min(i + step, s.length())));
        return chunks.toArray(new String[0]);
    }

    private static void writeLine(RenderContext ctx, float fontSize, String line) throws IOException {
        ctx.y -= PARAGRAPH_GAP; // 统一段落间距
        ctx.content.beginText();
        ctx.content.setFont(ctx.font, fontSize);
        ctx.content.newLineAtOffset(MARGIN + CONTENT_INDENT, ctx.y);
        ctx.content.showText(line.trim());
        ctx.content.endText();
        ctx.y -= LEADING;
    }

    private static void drawTableHeader(RenderContext ctx, float fontSize, String[] headers, float[] colWidths) throws IOException {
        drawTableRow(ctx, fontSize, headers, colWidths, -1);
    }

    private static void drawTableRow(RenderContext ctx, float fontSize, String[] cells, float[] colWidths, int rowIndex) throws IOException {
        ctx.ensureSpace(TABLE_ROW_HEIGHT);
        float x = MARGIN;
        float cellY = ctx.y;

        // 取消数据行斑马条纹背景，保持纯白背景

        for (int i = 0; i < cells.length; i++) {
            String cell = cells[i] == null ? "" : cells[i];
            ctx.content.beginText();
            ctx.content.setFont(rowIndex < 0 ? ctx.bold : ctx.font, fontSize);
            ctx.content.newLineAtOffset(x + (i == 0 ? CONTENT_INDENT : 4f), cellY);
            String clipped = clipToWidth(rowIndex < 0 ? ctx.bold : ctx.font, fontSize, cell, colWidths[i] - 8f);
            ctx.content.showText(clipped);
            ctx.content.endText();
            x += colWidths[i];
        }
        // 去除行底部横线，保持更简洁的列表风格
        ctx.y -= TABLE_ROW_HEIGHT;
    }

    private static void drawInfoCard(RenderContext ctx, String[][] kvs) throws IOException {
        float padding = 12f;   // 上内边距更大，让内容不贴边
        float lineHeight = LEADING;
        float cardHeight = (kvs.length * lineHeight) + padding;
        ctx.ensureSpace(cardHeight);
        // 将卡片整体上移一点，减少与上方内容的间距
        float topLift = 8f;
        float yTop = ctx.y + topLift;

        // 背景
        ctx.content.setNonStrokingColor(new Color(248, 249, 251));
        ctx.content.addRect(MARGIN, yTop - cardHeight, CONTENT_WIDTH, cardHeight);
        ctx.content.fill();
        ctx.content.setNonStrokingColor(Color.BLACK);

        // 边框
        ctx.content.setStrokingColor(new Color(225, 229, 235));
        ctx.content.addRect(MARGIN, yTop - cardHeight, CONTENT_WIDTH, cardHeight);
        ctx.content.stroke();
        ctx.content.setStrokingColor(Color.BLACK);

        // 文本基线从上内边距再往下偏移一点，使每一行在其行高内视觉居中
        float baselineOffset = 8f;
        float yCursor = yTop - padding - baselineOffset;
        for (String[] pair : kvs) {
            String key = pair[0] + ": ";
            String val = pair[1] == null ? "" : pair[1];
            ctx.content.beginText();
            ctx.content.setFont(ctx.bold, 12f);
            ctx.content.newLineAtOffset(MARGIN + 12f, yCursor);
            ctx.content.showText(key);
            ctx.content.endText();

            float keyWidth = getStringWidth(ctx.bold, 12f, key);
            ctx.content.beginText();
            ctx.content.setFont(ctx.font, 12f);
            ctx.content.newLineAtOffset(MARGIN + 12f + keyWidth, yCursor);
            ctx.content.showText(val);
            ctx.content.endText();
            yCursor -= lineHeight;
        }
        ctx.y = yTop - cardHeight - 8f;
    }

    private static String clipToWidth(PDFont font, float fontSize, String text, float maxWidth) throws IOException {
        if (getStringWidth(font, fontSize, text) <= maxWidth) return text;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            String t = sb.toString() + text.charAt(i);
            if (getStringWidth(font, fontSize, t + "…") > maxWidth) {
                return sb.append('…').toString();
            }
            sb.append(text.charAt(i));
        }
        return text;
    }

    private static float getStringWidth(PDFont font, float fontSize, String text) throws IOException {
        return font.getStringWidth(text) / 1000f * fontSize;
    }

    private static void drawBullet(RenderContext ctx, float fontSize, String text) throws IOException {
        ctx.ensureSpace(LEADING);
        // 使用通用连字符，避免部分字体缺少 U+2022
        ctx.content.beginText();
        ctx.content.setFont(ctx.bold, fontSize);
        ctx.content.newLineAtOffset(MARGIN, ctx.y);
        ctx.content.showText("- ");
        ctx.content.endText();

        ctx.content.beginText();
        ctx.content.setFont(ctx.font, fontSize);
        ctx.content.newLineAtOffset(MARGIN + 14f + CONTENT_INDENT, ctx.y);
        ctx.content.showText(text);
        ctx.content.endText();
        ctx.y -= LEADING;
    }

    private static void drawFooterPageNumber(PDDocument document, int startIndex) throws IOException {
        int total = document.getNumberOfPages();
        for (int i = 0; i < total; i++) {
            PDPage pg = document.getPage(i);
            try (PDPageContentStream cs = new PDPageContentStream(document, pg, PDPageContentStream.AppendMode.APPEND, true)) {
                String text = (startIndex + i) + "/" + (startIndex + total - 1);
                PDFont font = loadCjkFont(document);
                cs.beginText();
                cs.setFont(font, 9f);
                float w = getStringWidth(font, 9f, text);
                cs.newLineAtOffset(pg.getMediaBox().getWidth() - MARGIN - w, MARGIN - 20f);
                cs.showText(text);
                cs.endText();
            }
        }
    }

    /**
     * 加载可显示中文的字体：优先加载类路径 /fonts/NotoSansSC-Regular.otf；
     * 失败时尝试 Windows 常见中文字体；仍失败则抛出异常避免使用不支持中文的字体。
     */
    private static PDFont loadCjkFont(PDDocument document) {
        try {
            java.io.InputStream is = AssessmentResultPdfGenerator.class.getResourceAsStream("/fonts/NotoSansSC-Regular.otf");
            if (is != null) {
                return PDType0Font.load(document, is);
            }
        } catch (Exception ignore) {}

        // 扩展Windows中文字体路径列表
        String[] winFonts = {
            "C:/Windows/Fonts/msyh.ttc",      // 微软雅黑
            "C:/Windows/Fonts/msyh.ttf",      // 微软雅黑
            "C:/Windows/Fonts/simhei.ttf",    // 黑体
            "C:/Windows/Fonts/simsun.ttc",    // 宋体
            "C:/Windows/Fonts/simsun.ttf",    // 宋体
            "C:/Windows/Fonts/simkai.ttf",    // 楷体
            "C:/Windows/Fonts/simfang.ttf",   // 仿宋
            "C:/Windows/Fonts/Deng.ttf",      // 等线
            "C:/Windows/Fonts/Dengb.ttf",     // 等线 Bold
            "C:/Windows/Fonts/STKAITI.TTF",   // 华文楷体
            "C:/Windows/Fonts/STSONG.TTF",    // 华文宋体
            "C:/Windows/Fonts/STHEITI.TTF"    // 华文黑体
        };
        
        for (String path : winFonts) {
            try {
                java.io.File f = new java.io.File(path);
                if (f.exists()) {
                    PDFont font = PDType0Font.load(document, f);
                    // 验证字体是否能正确显示中文字符
                    try {
                        font.getStringWidth("测试");
                        return font;
                    } catch (Exception e) {
                        // 如果测试失败，继续尝试下一个字体
                        continue;
                    }
                }
            } catch (Exception ignore) {}
        }
        
        // 如果所有字体都加载失败，抛出异常而不是使用不支持中文的字体
        throw new RuntimeException("无法加载支持中文的字体，请确保系统安装了中文字体");
    }

    // ========== 渲染上下文与 JSON 数据结构 ==========
    private static final class RenderContext {
        final PDDocument document;
        final PDFont font;
        final PDFont bold;
        PDPageContentStream content;
        PDPage page;
        float y;

        RenderContext(PDDocument document, PDFont font, PDFont bold) {
            this.document = document;
            this.font = font;
            this.bold = bold;
        }

        void newPage() throws IOException {
            closeContent();
            this.page = new PDPage(PDRectangle.A4);
            this.document.addPage(this.page);
            this.content = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.OVERWRITE, false);
            this.y = page.getMediaBox().getHeight() - MARGIN;
        }

        void ensureSpace(float needHeight) throws IOException {
            if (this.y - needHeight < MARGIN) {
                newPage();
            }
        }

        void closeContent() throws IOException {
            if (content != null) content.close();
        }
    }

    // JSON 对象结构
    private static final class OverallReport {
        public DevelopmentQuotient developmentQuotient;
        public List<QuestionnaireScore> questionnaireScores;
        public Advice advice;
    }

    private static final class DevelopmentQuotient {
        public Object value;
        public String level;
        public String description;
        public Object mentalAge;
        public Object actualAge;
    }

    private static final class QuestionnaireScore {
        public Long questionnaireId;
        public String questionnaireName;
        public Object score;
        public String level;
    }

    private static final class Advice {
        public String description;
        public List<String> content;
    }
}


