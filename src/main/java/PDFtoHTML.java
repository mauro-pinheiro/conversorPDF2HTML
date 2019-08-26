
    import org.apache.pdfbox.contentstream.operator.DrawObject;
    import org.apache.pdfbox.contentstream.operator.state.*;
    import org.apache.pdfbox.pdmodel.PDDocument;
    import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
    import org.apache.pdfbox.tools.PDFText2HTML;

    import java.io.File;
    import java.io.FileOutputStream;
    import java.io.IOException;
    import java.io.OutputStreamWriter;
    import java.util.ArrayList;
    import java.util.Arrays;
    import java.util.List;
    import java.util.regex.Matcher;
    import java.util.regex.Pattern;

    public class PDFtoHTML extends PDFText2HTML{
        private static int index = 0;
        private static String htmlName = "htmlDoc.html";


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
                FileOutputStream fileOutputStream = new FileOutputStream(htmlName);
                OutputStreamWriter output  = new OutputStreamWriter(fileOutputStream);

                PDFtoHTML stripper = new PDFtoHTML();

                //((PDFTextStripper)stripper).writeText(document,output);
                String texto = stripper.getText(document);

                //List<String> strings = stripper.extrairparagrafos(texto);
                texto = criaHTMLIndexado(texto);
                output.write(texto);
                //strings = adicionaIndicies(strings);
                //strings.forEach(System.out::println);
                //System.out.println(strings.get(0));
                System.out.println(index);
                System.out.println(texto);
                document.close();
                output.close();
            } catch (InvalidPasswordException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        public static String criaHTMLIndexado(String texto){
            texto = identificaParagrafos(texto);
            texto = numeraParagrafos(texto);
            texto = criaIndice(texto);
            return texto;
        }

        private static String criaIndice(String texto) {
            //cria o indice
            String indice = "\r\n<div><ul>";
            for(int i = 0; i < index; i++){
               indice += "<li><a href="+htmlName+"#P"+i+">" + "Paragrafo " + i +"</a></li>\r\n";
            }
            indice += "</ul></div>\r\n";

            int indexOfBodyStart = texto.indexOf("<body>");
            texto = texto.substring(0, indexOfBodyStart + 6) + indice + texto.substring(indexOfBodyStart+6);
            return texto;
        }

        private static String numeraParagrafos(String texto) {
            //Numera Paragrafos
            for(int i = 0; i < texto.length(); i++){
                if(texto.charAt(i) == 'P'){
                    i++;
                    if(texto.charAt(i) == '?'){
                       texto = texto.substring(0,i) + index + texto.substring(i+1);
                       index++;
                    }
                }
            }
            return texto;
        }

        private static String identificaParagrafos(String texto) {
            Pattern pattern = Pattern.compile("(<div>|(\\.(\\s*&#160;)*)\\s*</p>)\\s*<p>\\s*\\w");
            //Pattern listPattern = Pattern.compile("<p>&#9679;\\s*");
            //Pattern numberedListPattern = Pattern.compile("<p>\\d+\\.\\s*");
            Matcher matcher = pattern.matcher(texto);

            //Indentifica Paragrafos
            while(matcher.find()){
                String aux1 = matcher.group();
                String aux2 = aux1;
                String aux3 = "<p id = \"P?\">";
                aux2 = aux2.replace("<p>",aux3);
                //System.out.println(aux2);
                texto = texto.replace(aux1,aux2);
            }
            return texto;
        }


        @Override
        protected void startArticle(boolean isLTR) throws IOException {
            super.startArticle(isLTR);
            output.write(getArticleStart());
        }











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

    class Test{
        public static void main(String[] args) {
            String str = "Mauro Sergio";
            str = str.replace("uro", "oru");
            System.out.println(str);
        }
    }
