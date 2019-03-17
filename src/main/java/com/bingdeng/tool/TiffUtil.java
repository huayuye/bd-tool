package com.bingdeng.tool;

import com.github.jaiimageio.plugins.tiff.TIFFTag;
import com.sun.media.jai.codec.*;
import lombok.extern.slf4j.Slf4j;

import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import java.awt.image.renderable.ParameterBlock;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class TiffUtil {

    /**
     * 将 images to tiff
     * @param imageBytes 需要转换的图片的字节流
     * @param outputStream 转后的tiff输出流
     * @return
     */
    public static boolean images2TIFF(List<byte[]> imageBytes, OutputStream outputStream) {

        if (imageBytes != null && imageBytes.size() > 0) {
            try {
                ArrayList pages = new ArrayList(imageBytes.size() - 1);
                ParameterBlock pb = (new ParameterBlock());
                PlanarImage firstPage = JAI.create("stream", new ByteArraySeekableStream(imageBytes.get(0)) );
                for (int i = 1; i < imageBytes.size(); i++) {
                    PlanarImage page = JAI.create("stream", new ByteArraySeekableStream(imageBytes.get(i)));
                    pages.add(page);
                }
                TIFFEncodeParam param = new TIFFEncodeParam();
                param.setCompression(TIFFEncodeParam.COMPRESSION_JPEG_TTN2);
//					param.setCompression(TIFFEncodeParam.COMPRESSION_DEFLATE);
//					param.setDeflateLevel(9);
//					param.setWriteTiled(true);
//					param.setReverseFillOrder(true);
                TIFFField[] extras = new TIFFField[4];
                extras[0] = new TIFFField(262, TIFFField.TIFF_SHORT, 1, (Object) new short[] { 6 });
                extras[1] = new TIFFField(282, TIFFTag.TIFF_RATIONAL, 1, (Object) new long[][]{{(long) 200, 1}, {0, 0}});
                extras[2] = new TIFFField(283, TIFFTag.TIFF_RATIONAL, 1, (Object) new long[][]{{(long) 200, 1}, {0, 0}});
                extras[3] = new TIFFField(258, TIFFField.TIFF_SHORT, 1, (Object) new char[] { 8 });
                param.setExtraFields(extras);
                param.setExtraImages(pages.iterator());
                ImageEncoder enc = ImageCodec.createImageEncoder("tiff", outputStream, param);
                enc.encode(firstPage);
                log.info("Tiff Successful, Over");
            } catch (IOException e) {
                log.error("Tiff Fail");
                return false;
            }finally {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    log.error("Stream Close Fail");
                }
            }
        }
        return true;
    }

}
