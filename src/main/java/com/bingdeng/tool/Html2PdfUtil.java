package com.bingdeng.tool;

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorker;
import com.itextpdf.tool.xml.XMLWorkerHelper;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * @Author: Fran
 * @Date: 2019/2/22
 * @Desc:
 **/
public class Html2PdfUtil {
    /**
     * html to pdf
     * @param templatePath ftl folder ：c:/htmlpdf
     * @param templateName ftl name ：packing_list_by_marshal.ftl
     * @param imageRootPath image resources folder ：c:/htmlpdf/
     * @param pdfDestPath pdf file path:c:/htmlpdf/test.pdf
     * @param data data of replace ftl template content:data,map格式或者实体格式都行
     * @param isRotate pdf direction：true-crosswise：pdf是否横向生成，true-是，false-否
     * @return
     */
    public static String html2Pdf(String templatePath,String templateName,String imageRootPath, String pdfDestPath,Object data, boolean isRotate){
        try{
            String fileContent=getContent(data,templatePath,templateName);
            // step 1
//            Document document = new Document(PageSize.A4.rotate());
            Document document = null;
            if(isRotate){
                document =  new Document(PageSize.A4.rotate());
            }else {
                document = new Document(PageSize.A4);
            }
            // step 2
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfDestPath));//"E:/testpdf.pdf"
            // step 3
            document.open();
            // step 4
            // CSS
            CSSResolver cssResolver = XMLWorkerHelper.getInstance().getDefaultCssResolver(true);

            // HTML
            HtmlPipelineContext htmlContext = new HtmlPipelineContext(null);
//            htmlContext.setTagFactory(Tags.getHtmlTagProcessorFactory());
//            DefaultTagProcessorFactory tpf=(DefaultTagProcessorFactory)Tags.getHtmlTagProcessorFactory();
//            tpf.addProcessor(HTML.Tag.IMG, Image2.class.getName());//默认是com.itextpdf.tool.xml.html.Image|自个定义一个image的处理类
            htmlContext.setTagFactory(Tags.getHtmlTagProcessorFactory());

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
        }catch (Exception e) {
            throw new RuntimeException("html2Pdf process fail",e);
        }
    }

    /**
     * @description 获取模板
     */
    private static String getContent(Object data,String templatePath,String templateName){

//        String templatePath=getPDFTemplatePath(fileName);//根据PDF名称查找对应的模板名称
//        String templateFileName=getTemplateName(templatePath);
//        String templateFilePath=getTemplatePath(templatePath);
        String templateFileName=templateName;//"packing_list_by_marshal.ftl";
        String templateFilePath=templatePath;//"E:/pdfhtml";
        try{
            Configuration config = new Configuration(Configuration.VERSION_2_3_25);//FreeMarker配置
            config.setDefaultEncoding("UTF-8");
            config.setDirectoryForTemplateLoading(new File(templateFilePath));//注意这里是模板所在文件夹，不是文件
            config.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
            config.setLogTemplateExceptions(false);
            Template template = config.getTemplate(templateFileName);//根据模板名称 获取对应模板
            StringWriter writer = new StringWriter();
            template.process(data, writer);//模板和数据的匹配
            writer.flush();
            String html = writer.toString();
            writer.close();
            return html;
        }catch (Exception ex){
            throw new RuntimeException("FreeMarkerUtil process fail",ex);
        }
    }

}
