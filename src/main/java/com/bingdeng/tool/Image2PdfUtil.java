package com.bingdeng.tool;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Fran
 * @Date: 2019/3/29
 * @Desc:
 **/
public class Image2PdfUtil {

    public static File Pdf(List<String> imageUrllist, String mOutputPdfFileName) {
        Document doc = new Document(PageSize.A4, 20, 20, 20, 20); //new一个pdf文档
        try {
            PdfWriter.getInstance(doc, new FileOutputStream(mOutputPdfFileName)); //pdf写入
            doc.open();//打开文档
            for (int i = 0; i < imageUrllist.size(); i++) {  //循环图片List，将图片加入到pdf中
                doc.newPage();  //在pdf创建一页
                Image png1 = Image.getInstance(imageUrllist.get(i)); //通过文件路径获取image
                //图片原比例设置
//                float heigth = png1.getHeight();
//                float width = png1.getWidth();
//                int percent = getPercent(heigth, width);
//                png1.setAlignment(Image.MIDDLE);
//                png1.scalePercent(percent + 3);// 表示是原来图像的比例;
                //图片原比例设置----end
                //根据文档配置----start
                float with = doc.getPageSize().getWidth()-doc.leftMargin()-doc.rightMargin();
                float height = with / 580 * 320;
                png1.scaleAbsolute(with, height);
                //根据文档配置----end
                doc.add(png1);
            }
            doc.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        File mOutputPdfFile = new File(mOutputPdfFileName);  //输出流
        if (!mOutputPdfFile.exists()) {
            mOutputPdfFile.deleteOnExit();
            return null;
        }
        return mOutputPdfFile; //反回文件输出流
    }

    public static int getPercent(float h, float w) {
        int p = 0;
        float p2 = 0.0f;
        if (h > w) {
            p2 = 297 / h * 100;
        } else {
            p2 = 210 / w * 100;
        }
        p = Math.round(p2);
        return p;
    }

    public static int getPercent2(float h, float w) {
        int p = 0;
        float p2 = 0.0f;
        p2 = 530 / w * 100;
        p = Math.round(p2);
        return p;
    }

    public static void main(String[] args) {
        List<String> list = new ArrayList();
        list.add("C:\\Users\\yyx\\Desktop\\照片\\1547109181(1).jpg");
        list.add("C:\\Users\\yyx\\Desktop\\照片\\1547109181(1).jpg");
        list.add("C:\\Users\\yyx\\Desktop\\照片\\1547109181(1).jpg");
        Pdf(list, "E:\\testJpg3.pdf");
    }


}
