
import org.apache.pdfbox.contentstream.operator.DrawObject;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.pdfbox.contentstream.operator.state.*;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.tools.ExtractImages;
import org.apache.pdfbox.tools.PDFText2HTML;
import org.apache.pdfbox.util.Matrix;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

public class PDFtoHTML extends PDFText2HTML{
    public PDFtoHTML() throws IOException {
        addOperator(new Concatenate());
        addOperator(new DrawObject());
        addOperator(new SetGraphicsStateParameters());
        addOperator(new Save());
        addOperator(new Restore());
        addOperator(new SetMatrix());
    }

    public static void main(String[] args) {
        try{
            PDDocument document = PDDocument.load(new File("pdfDoc.pdf"));
            FileOutputStream fileOutputStream = new FileOutputStream("htmlDoc.html");
            OutputStreamWriter output  = new OutputStreamWriter(fileOutputStream);
            PDFtoHTML stripper = new PDFtoHTML();
            ((PDFTextStripper)stripper).writeText(document,output);
            document.close();
            output.close();
        } catch (InvalidPasswordException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void processOperator(Operator operator, List<COSBase> arguments) throws IOException {
        String operation = operator.getName();
        if(OperatorName.DRAW_OBJECT.equals(operation)){
            COSName objectName = (COSName) arguments.get( 0 );
            PDXObject xobject = getResources().getXObject( objectName );
            if( xobject instanceof PDImageXObject){
                PDImageXObject imageXObject = (PDImageXObject) xobject;
                BufferedImage bufferedImage = imageXObject.getImage();
                Matrix ctmNew = getGraphicsState().getCurrentTransformationMatrix();
                
                int w = bufferedImage.getWidth();
                int h = bufferedImage.getHeight();

                ImageIO.write(bufferedImage, "JPEG", new File(objectName+".jpg"));
                String htmlString = "<img src=\"" + objectName + ".jpg\"" +
                        " alt=\"" + objectName + "\"" +
                        " width=" + w + " height=" + h + ">";

                writeString(htmlString);
                // position in user space units. 1 unit = 1/72 inch at 72 dpi
                System.out.println("position in PDF = " + ctmNew.getTranslateX() + ", " + ctmNew.getTranslateY() + " in user space units");
            }
        } else {
            super.processOperator(operator,arguments);
        }
    }

    @Override
    protected void writeString(String chars) throws IOException {
        output.write(chars);
    }
}
