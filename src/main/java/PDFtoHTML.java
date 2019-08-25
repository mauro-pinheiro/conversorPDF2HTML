
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
import org.apache.pdfbox.tools.PDFText2HTML;
import org.apache.pdfbox.util.Matrix;
import org.w3c.dom.ls.LSOutput;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PDFtoHTML {
    private static int index = 0;
    private static String htmlName = "htmlDoc.html";

    public static void main(String[] args) {
        try{
            PDDocument document = PDDocument.load(new File("pdfDoc.pdf"));
            FileOutputStream fileOutputStream = new FileOutputStream("htmlDoc.html");
            OutputStreamWriter output  = new OutputStreamWriter(fileOutputStream);

            PDFTextStripper stripper = new PDFTextStripper();
            //((PDFTextStripper)stripper).writeText(document,output);
            String texto = stripper.getText(document);

            List<String> strings = extrairParagrafos(texto);
            //strings = adicionaIndicies(strings);
            strings.forEach(System.out::println);
            System.out.println("\n\n\n\n"+strings.size());
            //System.out.println(texto);
            document.close();
            output.close();
        } catch (InvalidPasswordException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> extrairParagrafos(String texto){
        String lines[] = texto.split("\\r?\\n");
        List<String> paragrafos = new ArrayList<>();
        String aux = new String();
        for(String line : lines){
            int length = line.length();
            aux += " " + line;
            if(line.lastIndexOf('.') == length-1){
                paragrafos.add(aux);
                aux = new String();
            }
        }
        return paragrafos;
    }

    public static List<String> adicionaIndicies(List<String> paragrafos){
        List<String> indexados = new ArrayList<>();
        for(String paragrafo : paragrafos){
            String tag = paragrafo.substring(0,2);
            String resto = paragrafo.substring(2);
            String strIndex = " id = \"P"+index+"\"";
            index++;
            indexados.add(tag+strIndex+resto);
        }
        return indexados;
    }

    /*
    public static String criaIndice(){
        String indice = "<ul>";
        for(int i = 0; i < index; i++){
            indice += "<li><a href="+htmlName+"#P"+i+"</li>";
        }
        indice += "</ul>";
    }
    */
    /*
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
                float x = ctmNew.getTranslateX();
                float y = ctmNew.getTranslateY();

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
    }*/
}
