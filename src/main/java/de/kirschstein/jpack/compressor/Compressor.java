package de.kirschstein.jpack.compressor;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.ImageWriteException;
import org.apache.sanselan.Sanselan;
import org.apache.sanselan.common.IImageMetadata;
import org.apache.sanselan.formats.jpeg.JpegImageMetadata;
import org.apache.sanselan.formats.jpeg.exifRewrite.ExifRewriter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * @author Tobias Kirschstein
 */
public class Compressor {

    public void compress() throws IOException, ImageReadException, ImageWriteException {
        String e = "";
        File file = new File(e);
        IImageMetadata metadata = Sanselan.getMetadata(new File(e));
        BufferedImage image = ImageIO.read(new File(e));
        JPEGImageEncoder jpegImageEncoder = JPEGCodec.createJPEGEncoder(new FileOutputStream(e));
        JPEGEncodeParam param = jpegImageEncoder.getDefaultJPEGEncodeParam(image);
        param.setQuality(0.8F, true);
        jpegImageEncoder.encode(image, param);
        JpegImageMetadata jpegMetadata = (JpegImageMetadata)metadata;
        ExifRewriter exifRewriter = new ExifRewriter();
        File tempFile = new File("exif_tmp.jpg");
        FileOutputStream output = new FileOutputStream(tempFile);
        exifRewriter.updateExifMetadataLossy(new File(e), output, jpegMetadata.getExif().getOutputSet());
        output.close();
        if(new File(e).exists()) {
            file.delete();
        }

        FileInputStream input = new FileInputStream(tempFile);
        output = new FileOutputStream(file);
        FileChannel inChannel = input.getChannel();
        FileChannel outChannel = output.getChannel();

        try {
            inChannel.transferTo(0L, inChannel.size(), outChannel);
        } catch (IOException var20) {
            throw var20;
        } finally {
            if(inChannel != null) {
                inChannel.close();
            }

            if(outChannel != null) {
                outChannel.close();
            }

        }

        input.close();
        output.close();
    }
}
