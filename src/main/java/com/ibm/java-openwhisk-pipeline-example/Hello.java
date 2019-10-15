/**
 * Hello world!
 *
 */
import com.google.gson.JsonObject;
import java.util.Base64;
import java.io.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.color.ColorSpace;
import javax.imageio.ImageIO;

public class Hello {
    public static JsonObject main(JsonObject args) {
        // write input png
        byte[] png;
        String inputFilename;
        String encodedPng = "";
        String encodedGrayImg = "";
        if (args.has("png"))
            encodedPng = args.getAsJsonPrimitive("png").getAsString();
        png = Base64.getDecoder().decode(encodedPng);
        
        inputFilename = "/tmp/"+encodedPng.substring(0,12)+".png";
        try (OutputStream fs = new FileOutputStream(inputFilename)) {
            fs.write(png);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        try {
            // Open input png for manipulation
            File srcFile = new File(inputFilename);
            ColorConvertOp shifter = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
            BufferedImage srcImg = ImageIO.read(srcFile);
            BufferedImage grayImg = shifter.filter(srcImg, null);
            ByteArrayOutputStream grayOut = new ByteArrayOutputStream();
            ImageIO.write(grayImg, "PNG", grayOut);
            byte[] rawGrayImg = grayOut.toByteArray();
            encodedGrayImg = Base64.getEncoder().encodeToString(rawGrayImg);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        
        JsonObject response = new JsonObject();
        JsonObject header = new JsonObject();
        header.addProperty("Content-Type", "image/png");
        response.add("headers", header);
        response.addProperty("statusCode", 200);
        response.addProperty("body", encodedGrayImg);
        return response;
    }
}
