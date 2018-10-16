package com.sp.barcode;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;


@EnableWebMvc
@Controller
@SpringBootApplication
public class BarcodeApplication {


    public static void main(String[] args) {
        SpringApplication.run(BarcodeApplication.class);
    }


    @ResponseBody
    @RequestMapping(value = "/bar", method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> qr(
            @RequestParam(value="width", defaultValue = "600") Integer width,
            @RequestParam(value="height", defaultValue = "100") Integer height,
            @RequestParam(value="fontSize", defaultValue = "40") Integer fontSize,
            @RequestParam(value="code") String code) throws Exception
    {

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try {

            BitMatrix matrix;
            com.google.zxing.Writer writer = new Code128Writer();

            int marginTop = 10;
            int addHeight = (int)Math.ceil(fontSize*1.15);
            int margin = (int)(addHeight*0.2);
            addHeight+=2*margin;

            try {
                matrix = writer.encode(code, BarcodeFormat.CODE_128, width, height+addHeight);
            } catch (WriterException e) {
                throw new Exception("Error generando el QR");
            }

            BufferedImage image = new BufferedImage(width, height+addHeight, BufferedImage.TYPE_INT_RGB);
            for (int y = 0; y < height+addHeight; y++) {
                for (int x = 0; x < width; x++) {
                    int grayValue = (matrix.get(x, y) ? 0 : 1) & 0xff;
                    image.setRGB(x, y, (grayValue == 0 && y> marginTop && y<height ? 0 : 0xFFFFFF));
                }
            }

            Graphics graphics = image.getGraphics();
            graphics.drawImage(image, 0, 0, null);

            if(fontSize>0) {
                Font f = new Font("Arial", Font.PLAIN, fontSize);
                FontRenderContext frc = image.getGraphics().getFontMetrics().getFontRenderContext();
                Rectangle2D rect = f.getStringBounds(code, frc);

                graphics.setFont(f);
                graphics.setColor(Color.BLACK);
                graphics.drawString(code,
                        (int) Math.ceil((image.getWidth() / 2) - ((rect.getWidth()) / 2)),
                        (int) Math.ceil(image.getHeight() - (rect.getHeight() - 2 * margin)));
            }
            graphics.dispose();
            ImageIO.write(image, "png", bout);


        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("Error generando el Barcode");
        } finally {
            try {
                bout.close();
            } catch (Exception ex) {
                throw new Exception("Error generando el Barcode");
            }
        }

        byte[] imageInBytes = bout.toByteArray();
        InputStreamResource source = new InputStreamResource(new ByteArrayInputStream(imageInBytes));

        return ResponseEntity.ok()
                .contentLength(imageInBytes.length)
                .contentType(MediaType.parseMediaType("image/png"))
                .body(source);

    }

}
