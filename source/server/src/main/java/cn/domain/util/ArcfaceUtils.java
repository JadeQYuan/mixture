package cn.domain.util;

import com.arcsoft.face.toolkit.ImageFactory;
import com.arcsoft.face.toolkit.ImageInfo;
import com.arcsoft.face.toolkit.ImageInfoEx;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;

@Slf4j
public class ArcfaceUtils {

    /**
     * 处理 File 的图片流
     *
     * @param img
     * @return
     */
    public static ImageInfoMeta packImageInfoEx(File img) {
        ImageInfo imageInfo = ImageFactory.getRGBData(img);
        return packImageInfoMeta(imageInfo);
    }

    /**
     * 处理 byte[] 的图片流
     *
     * @param img
     * @return
     */
    public static ImageInfoMeta packImageInfoMeta(byte[] img) {
        ImageInfo imageInfo = ImageFactory.getRGBData(img);
        return packImageInfoMeta(imageInfo);
    }

    /**
     * 处理 InpuStream 的图片流
     *
     * @param img
     * @return
     */
    public static ImageInfoMeta packImageInfoMeta(InputStream img) {
        ImageInfo imageInfo = ImageFactory.getRGBData(img);
        return packImageInfoMeta(imageInfo);
    }

    /**
     * 处理 网络图片 的图片流
     *
     * @param path
     * @return
     */
    public static ImageInfoMeta packImageInfoURL(String path) {
        try {
            InputStream inputStream = getImageInputStream(path);
            ImageInfo imageInfo = ImageFactory.getRGBData(inputStream);
            return packImageInfoMeta(imageInfo);
        } catch (Exception e) {
            log.error("处理网络图片处理失败", e);
        }
        return null;
    }

    /**
     * 处理 base图片 的图片流
     *
     * @param base64
     * @return
     */
    public static ImageInfoMeta packImageInfoBase64(String base64) {
        try {
            ImageInfo imageInfo = ImageFactory.getRGBData(removeBase64Prefix(base64));
            return packImageInfoMeta(imageInfo);
        } catch (Exception e) {
            log.error("处理网络图片处理失败", e);
        }
        return null;
    }

    public static byte[] removeBase64Prefix(String base64String) {
        if (base64String.startsWith("data:image/jpeg;base64,")) {
            base64String = base64String.replace("data:image/jpeg;base64,", "");
        }
        if (base64String.startsWith("data:image/png;base64,")) {
            base64String = base64String.replace("data:image/png;base64,", "");
        }
        return Base64.getDecoder().decode(base64String);
    }

    public static InputStream getImageInputStream(String imageUrl) throws Exception {
        URL url = new URL(imageUrl);
        URLConnection connection = url.openConnection();
        return connection.getInputStream();
    }

    /**
     * 打包生成 ImageInfoMeta
     *
     * @param imageInfo
     * @return
     */
    private static ImageInfoMeta packImageInfoMeta(ImageInfo imageInfo) {
        ImageInfoMeta imageInfoMeta = new ImageInfoMeta(imageInfo);
        return imageInfoMeta;
    }

    /**
     * 对imageInfo 和 imageInfoEx 的打包对象
     *
     * @return
     */
    @Data
    public static class ImageInfoMeta {
        private ImageInfo imageInfo;
        private ImageInfoEx imageInfoEx;

        public ImageInfoMeta(ImageInfo imageInfo) {
            this.imageInfo = imageInfo;
            imageInfoEx = new ImageInfoEx();
            imageInfoEx.setHeight(imageInfo.getHeight());
            imageInfoEx.setWidth(imageInfo.getWidth());
            imageInfoEx.setImageFormat(imageInfo.getImageFormat());
            imageInfoEx.setImageDataPlanes(new byte[][] { imageInfo.getImageData() });
            imageInfoEx.setImageStrides(new int[] { imageInfo.getWidth() * 3 });
        }
    }
}
