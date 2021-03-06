/*
 * Copyright (C) 2012 daniel
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package darwin.util.image;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.nio.file.*;

import darwin.util.misc.IterableFacade;

import javax.imageio.*;
import javax.imageio.stream.ImageInputStream;

/**
 *
 * @author daniel
 */
public class ImageUtil2 {

    public static BufferedImage loadImageByMimeType(Path file) throws IOException {
        String mime = Files.probeContentType(file);
        Iterable<ImageReader> iter = new IterableFacade<>(ImageIO.getImageReadersByMIMEType(mime));
        for (ImageReader reader : iter) {
            InputStream in = Files.newInputStream(file);
            try (ImageInputStream ii = ImageIO.createImageInputStream(in);) {
                reader.setInput(ii);
                return reader.read(0);
            }
        }
        throw new IOException("No ImageReader found for the file: " + file);
    }

    public static BufferedImage filter(BufferedImage image, BufferedImageOp op) {
        BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        op.filter(image, result);
        return result;
    }

    //TODO sehr komische ergebnisse bei Bilder vom Type USHORT_GRAY (bilder haben nur noch 3-4 graustufen)
    public static BufferedImage getScaledImage(BufferedImage image, int targetWidth,
                                               int targetHeight, boolean highQuality) {
        assert targetHeight > 0 || targetWidth > 0;
        
        int accWidth, accHeight, targetW, targetH;
        
        float aspect = (float)image.getWidth() / image.getHeight();
        
        if(targetHeight > 0)
            targetH = targetHeight;
        else
            targetH = (int) (targetWidth / aspect);
        
        if(targetWidth > 0)
            targetW = targetWidth;
        else
            targetW = (int) (targetHeight * aspect);
        
        if (highQuality) {
            accHeight = image.getHeight();
            accWidth = image.getWidth();
        } else {
            accHeight = targetH;
            accWidth = targetW;
        }

        BufferedImage result = image;
        do {
            if (highQuality) {
                accHeight = Math.max(accHeight / 2, targetH);
                accWidth = Math.max(accWidth / 2, targetW);
            }
            BufferedImage tmp = new BufferedImage(accWidth, accHeight, image.getType());

            Graphics2D g2 = tmp.createGraphics();

            if (highQuality) {
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                                    RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            } else {
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                                    RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            }
            g2.drawImage(result, 0, 0, accWidth, accHeight, null);

            result = tmp;
        } while (accHeight != targetH && accWidth != targetW);

        return result;
    }
}
