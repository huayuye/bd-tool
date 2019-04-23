package com.bingdeng.tool;

import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorker;
import com.itextpdf.tool.xml.XMLWorkerFontProvider;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.itextpdf.tool.xml.html.CssAppliers;
import com.itextpdf.tool.xml.html.CssAppliersImpl;
import com.itextpdf.tool.xml.html.Tags;
import com.itextpdf.tool.xml.parser.XMLParser;
import com.itextpdf.tool.xml.pipeline.css.CSSResolver;
import com.itextpdf.tool.xml.pipeline.css.CssResolverPipeline;
import com.itextpdf.tool.xml.pipeline.end.PdfWriterPipeline;
import com.itextpdf.tool.xml.pipeline.html.AbstractImageProvider;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipelineContext;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import org.springframework.util.StringUtils;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * @Author: Fran
 * @Date: 2019/2/22
 * @Desc:
 **/
public class Html2PdfUtil {

    /**
     * html to pdf
     * 对css支持不好,此方法的图片路径src,不支持base64,可支持网络路径（http://）和绝对路径
     *
     * @param templatePath  ftl folder：模板路径，
     *                      若是放在项目的resources下template/pdf/,则取后面的部分，如 template/pdf/
     *                      也可以为本地绝对路径，如 c:/pdf
     * @param templateName  ftl name 模板名称 如 pdf.ftl
     * @param imageRootPath image resources folder 图片资源（绝对路径）
     * @param pdfDestPath   pdf file path 生成pdf的路径
     * @param data          data of replace ftl template content 数据
     * @param isRotate      pdf direction：true-crosswise 是否横向
     * @param fontSize      pdf font size 字体大小
     * @param classLoader
     * @return
     */
    public static String html2Pdf(String templatePath, String templateName, String imageRootPath, String pdfDestPath, Object data, boolean isRotate, float fontSize, ClassLoader classLoader) {
        try {
            String fileContent = getContent(data, templatePath, templateName, classLoader);
            // step 1
//            Document document = new Document(PageSize.A4.rotate());
            Document document = null;
            if (isRotate) {
                document = new Document(PageSize.A4.rotate());
            } else {
                document = new Document(PageSize.A4);
            }
            // step 2
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfDestPath));//"E:/testpdf.pdf"
            // step 3
            document.open();

            //设置字体大小
            CssAppliers cssAppliers = new CssAppliersImpl(new XMLWorkerFontProvider() {
                @Override
                public Font getFont(final String fontname, String encoding, float size, final int style) {
                    try {
                        BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);//中文字体
                        return new Font(bfChinese, fontSize, style);
                    } catch (Exception ex) {
                        return new Font(Font.FontFamily.UNDEFINED, fontSize, style);
                    }
                }
            });

            // step 4
            //设置样式
            //  1
            CSSResolver cssResolver = XMLWorkerHelper.getInstance().getDefaultCssResolver(true);
            //2
//            String csss = "";
//            CSSResolver cssResolver = new StyleAttrCSSResolver();
//            if (csss != null) {
//                CssFile cssFile = XMLWorkerHelper.getCSS(new ByteArrayInputStream(csss.getBytes()));
//                cssResolver.addCss(cssFile);
//            }

            // HTML
            HtmlPipelineContext htmlContext = new HtmlPipelineContext(cssAppliers);
            htmlContext.setTagFactory(Tags.getHtmlTagProcessorFactory());
            htmlContext.autoBookmark(false);
            htmlContext.setImageProvider(new AbstractImageProvider() {
                @Override
                public String getImageRootPath() {
                    return imageRootPath;//"E:/pdfhtml/";
                }
            });

            // Pipelines
            PdfWriterPipeline pdf = new PdfWriterPipeline(document, writer);
            HtmlPipeline html = new HtmlPipeline(htmlContext, pdf);
            CssResolverPipeline css = new CssResolverPipeline(cssResolver, html);

            // XML Worker
            XMLWorker worker = new XMLWorker(css, true);
            XMLParser p = new XMLParser(worker);

            p.parse(new StringReader(fileContent));
            document.close();
            return pdfDestPath;
        } catch (Exception e) {
            throw new RuntimeException("html2Pdf process fail", e);
        }
    }

    /**
     * html to pdf
     * 支持css2.1
     *
     * @param templatePath ftl folder 模板路径，
     *                     *                     若是放在项目的resources下template/pdf/,则取后面的部分，如 template/pdf/
     *                     *                     也可以为本地绝对路径，如 c:/pdf
     *                     这里默认是放在resources下template/pdf/里面
     * @param templateName ftl name 模板名称 如 pdf.ftl
     * @param pdfDestPath  生成pdf存放的路径
     * @param data         数据
     * @param classLoader  调用此方法的当前类的类加载器 this.getClass().getClassLoader()
     * @return
     */
    public static byte[] generatePdf(String templatePath, String templateName, String pdfDestPath, Object data, ClassLoader classLoader) {
        final String charsetName = "UTF-8";
        String htmlContent = getContent(data, templatePath, templateName, classLoader);
        OutputStream out = null;
        ITextRenderer iTextRenderer = new ITextRenderer();
        ByteArrayOutputStream outputStream = null;
        FileInputStream in = null;
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder();
            org.w3c.dom.Document doc = builder.parse(new ByteArrayInputStream(htmlContent
                    .getBytes(StandardCharsets.UTF_8)));
            outputStream = new ByteArrayOutputStream();
            iTextRenderer.setDocument(doc, null);
            iTextRenderer.layout();
            if (StringUtils.hasText(pdfDestPath)) {
                File f = new File(pdfDestPath);
                if (f != null && !f.getParentFile().exists()) {
                    f.getParentFile().mkdir();
                }
                out = new FileOutputStream(pdfDestPath);
                iTextRenderer.createPDF(out);
                byte[] buffer = new byte[4096];
                int len = -1;
                in = new FileInputStream(pdfDestPath);
                while ((len = in.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, len);
                }
                outputStream.flush();
            } else {
                iTextRenderer.createPDF(outputStream);
            }
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("html2Pdf iTextRenderer fail", e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * @description 获取模板
     */
    private static String getContent(Object data, String templatePath, String templateName, ClassLoader classLoader) {

        String templateFileName = templateName;//"packing_list_by_marshal.ftl";
        String templateFilePath = templatePath;//"E:/pdfhtml";
        try {
            Configuration config = new Configuration(Configuration.VERSION_2_3_25);//FreeMarker配置
            config.setDefaultEncoding("UTF-8");
            //注意这里是模板所在文件夹，不是文件，如/fold/ 或 c:/fold/
//            config.setDirectoryForTemplateLoading(new File(templateFilePath));
            //classLoader 调用类的classLoader,templateFilePath 指在项目resources下的文件夹路径：如template/pdf/
            config.setClassLoaderForTemplateLoading(classLoader, templateFilePath);
            config.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
            config.setLogTemplateExceptions(false);
            Template template = config.getTemplate(templateFileName);//根据模板名称 获取对应模板
            StringWriter writer = new StringWriter();
            template.process(data, writer);//模板和数据的匹配
            writer.flush();
            String html = writer.toString();
            writer.close();
            return html;
        } catch (Exception ex) {
            throw new RuntimeException("FreeMarkerUtil process fail", ex);
        }
    }

}
