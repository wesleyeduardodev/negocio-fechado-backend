package com.negociofechado.modulos.arquivo.service;
import com.negociofechado.comum.exception.NegocioException;
import com.negociofechado.modulos.arquivo.dto.ProcessedImage;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Set;

@Service
public class ImagemService {

    private static final long MAX_SIZE_BYTES = 10 * 1024 * 1024;
    private static final int MAX_DIMENSION = 1920;
    private static final float JPEG_QUALITY = 0.85f;
    private static final Set<String> ALLOWED_TYPES = Set.of("image/jpeg", "image/jpg", "image/png", "image/webp");

    public void validar(MultipartFile file) {
        if (file.isEmpty()) {
            throw new NegocioException("Arquivo vazio");
        }

        if (file.getSize() > MAX_SIZE_BYTES) {
            throw new NegocioException("Arquivo muito grande. Maximo: 10MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType.toLowerCase())) {
            throw new NegocioException("Tipo de arquivo nao permitido. Use: JPG, PNG ou WebP");
        }

        try {
            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image == null) {
                throw new NegocioException("Arquivo nao e uma imagem valida");
            }
        } catch (IOException e) {
            throw new NegocioException("Nao foi possivel processar a imagem");
        }
    }

    public ProcessedImage processar(MultipartFile file) {
        try {
            BufferedImage original = ImageIO.read(file.getInputStream());

            BufferedImage resized = redimensionar(original);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            ImageWriter writer = ImageIO.getImageWritersByFormatName("jpeg").next();
            ImageWriteParam param = writer.getDefaultWriteParam();
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(JPEG_QUALITY);

            try (ImageOutputStream ios = ImageIO.createImageOutputStream(baos)) {
                writer.setOutput(ios);
                writer.write(null, new IIOImage(resized, null, null), param);
            }
            writer.dispose();

            return new ProcessedImage(
                    baos.toByteArray(),
                    "image/jpeg",
                    "jpg",
                    resized.getWidth(),
                    resized.getHeight()
            );
        } catch (IOException e) {
            throw new NegocioException("Erro ao processar imagem");
        }
    }

    private BufferedImage redimensionar(BufferedImage original) {
        int width = original.getWidth();
        int height = original.getHeight();

        if (width <= MAX_DIMENSION && height <= MAX_DIMENSION) {
            if (original.getType() == BufferedImage.TYPE_INT_RGB) {
                return original;
            }
            BufferedImage converted = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = converted.createGraphics();
            g.drawImage(original, 0, 0, null);
            g.dispose();
            return converted;
        }

        double ratio = Math.min(
                (double) MAX_DIMENSION / width,
                (double) MAX_DIMENSION / height
        );

        int newWidth = (int) (width * ratio);
        int newHeight = (int) (height * ratio);

        BufferedImage resized = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);

        Graphics2D g = resized.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawImage(original, 0, 0, newWidth, newHeight, null);
        g.dispose();

        return resized;
    }
}
