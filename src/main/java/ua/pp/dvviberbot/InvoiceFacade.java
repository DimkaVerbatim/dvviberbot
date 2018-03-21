package ua.pp.dvviberbot;


import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;


public class InvoiceFacade {
    private final String FONT = "/fonts/FreeSans.ttf";
    private final String COMPANYLOGO = "/images/logo_without_text.png";
    private AbonentMonths abonMonths;
    private BaseFont bfBold;
    private BaseFont bf;
    private int pageNumber = 0;

    private BaseFont bfs;
    private Font font;
    private Font smallBold;

    private void addMetaData(Document document) {
        document.addTitle("Квитанція");
        document.addAuthor("DimkaVerbatim");
        document.addCreator("DimkaVerbatim");
        document.addCreationDate();
        document.addProducer();
        document.setPageSize(PageSize.LETTER);
    }
    private void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }

    private void generateLayout(Document doc, PdfContentByte cb)  {

        try {

            cb.setLineWidth(1f);

            // AbonentMonths Header box layout
            cb.rectangle(270,680,300,80);
            cb.moveTo(270,700);
            cb.lineTo(570,700);
            cb.moveTo(270,720);
            cb.lineTo(570,720);
            cb.moveTo(270,740);
            cb.lineTo(570,740);
            cb.moveTo(325,680);
            cb.lineTo(325,760);
            cb.stroke();

            // AbonentMonths Header box Text Headings
            createHeadings(cb,272,743,"ОР");
            createHeadings(cb,272,723,"ПІБ");
            createHeadings(cb,272,703,"Адреса");
            createHeadings(cb,272,683,"К-ть мешк.");

            // AbonentMonths Detail box layout
            cb.rectangle(15,50,560,600);
            cb.moveTo(15,630);
            cb.lineTo(575,630);
            cb.moveTo(70,50);
            cb.lineTo(70,650);
            cb.moveTo(125,50);
            cb.lineTo(125,650);
            cb.moveTo(197,50);
            cb.lineTo(197,650);
            cb.moveTo(256,50);
            cb.lineTo(256,650);
            cb.moveTo(319,50);
            cb.lineTo(319,650);
            cb.moveTo(372,50);
            cb.lineTo(372,650);
            cb.moveTo(427,50);
            cb.lineTo(427,650);
            cb.moveTo(492,50);
            cb.lineTo(492,650);
            cb.stroke();

            // AbonentMonths Detail box Text Headings
            createHeadings(cb,20,633,"Місяць/Рік");
            createHeadings(cb,80,633,"Послуга");
            createHeadings(cb,132,633,"Вхідне сальдо");
            createHeadings(cb,202,633,"Нараховано");
            createHeadings(cb,261,633,"Перерахунок");
            createHeadings(cb,329,633,"Оплата");
            createHeadings(cb,385,633,"Пільги");
            createHeadings(cb,440,633,"Субсидія");
            createHeadings(cb,501,633,"Вихідне сальдо");

            //add the images
            Image companyLogo;
            companyLogo = Image.getInstance(InvoiceFacade.class.getResource(COMPANYLOGO));
            companyLogo.setAbsolutePosition(25,688);
            companyLogo.scalePercent(50);
            doc.add(companyLogo);

        }

        catch (DocumentException dex){
            dex.printStackTrace();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

    }


    private void generateHeader(Document doc, PdfContentByte cb)  {

        try {

            createHeadings(cb,120,750,"ПАТ DimkaVerbatim");
            createHeadings(cb,120,735,"проспект Миру");
            createHeadings(cb,120,720,"буд. 21");
            createHeadings(cb,120,705,"Житомир, 10020");
            createHeadings(cb,120,690,"Україна");

            createHeadings(cb,327,743,abonMonths.getLc());
            createHeadings(cb,327,723,abonMonths.getFio());
            createHeadings(cb,327,703,abonMonths.getAdres());
            createHeadings(cb,327,683,abonMonths.getNlivers()+"");

            createBigHeadings(cb, 140,660,"Оборотно-сальдова відомість за останій рік");


        }

        catch (Exception ex){
            ex.printStackTrace();
        }

    }

    private void generateDetail(Document doc, PdfContentByte cb, JSONObject data, int y)  {

        try {

            createContent(cb,19,y,data.getInt("MONTH")+"-"+data.getInt("YEAR"),PdfContentByte.ALIGN_LEFT);
            createContent(cb,78,y, data.getString("SERVNAME"),PdfContentByte.ALIGN_LEFT);
            createContent(cb,195,y, data.getDouble("SALDOINPUT")+"",PdfContentByte.ALIGN_RIGHT);
            createContent(cb,254,y, data.getDouble("NACHISLENO")+"",PdfContentByte.ALIGN_RIGHT);
            createContent(cb,317,y, data.getDouble("ZAMENA")+"",PdfContentByte.ALIGN_RIGHT);
            createContent(cb,370,y, data.getDouble("PAYS")+"",PdfContentByte.ALIGN_RIGHT);
            createContent(cb,425,y, data.getDouble("SUMPRIVELEGE")+"",PdfContentByte.ALIGN_RIGHT);
            createContent(cb,490,y, data.getDouble("SUBSIDIES")+"",PdfContentByte.ALIGN_RIGHT);
            createContent(cb,573,y, data.getDouble("SALDOOUTPUT")+"",PdfContentByte.ALIGN_RIGHT);
        }

        catch (Exception ex){
            ex.printStackTrace();
        }

    }

    private void createHeadings(PdfContentByte cb, float x, float y, String text){
        cb.beginText();
        cb.setFontAndSize(bfs,9);
        cb.setTextMatrix(x,y);
        cb.showText(text.trim());
        cb.endText();

    }

    private void createBigHeadings(PdfContentByte cb, float x, float y, String text){
        cb.saveState();
        // set bold font
        cb.setCharacterSpacing(1);
        cb.setRGBColorFill(66, 00, 00);
        cb.setLineWidth((float)0.5);
        cb.setTextRenderingMode(PdfContentByte.TEXT_RENDER_MODE_FILL_STROKE);
        // end set bold font
        cb.beginText();
        cb.setFontAndSize(bfs,12);
        cb.setTextMatrix(x,y);
        cb.showText(text.trim());
        cb.endText();
        cb.restoreState();

    }

    private void printPageNumber(PdfContentByte cb){
        cb.beginText();
        cb.setFontAndSize(bfs, 8);
        cb.showTextAligned(PdfContentByte.ALIGN_RIGHT, "Стр. " + (pageNumber+1), 570 , 25, 0);
        cb.endText();
        pageNumber++;
    }

    private void createContent(PdfContentByte cb, float x, float y, String text, int align){
        cb.beginText();
        cb.setFontAndSize(bfs, 8);
        cb.showTextAligned(align, text.trim(), x , y, 0);
        cb.endText();

    }

    public void writeAsPdf(AbonentMonths abonentMonths, OutputStream out) {
        abonMonths = abonentMonths;
        Document doc = new Document();
        PdfWriter docWriter = null;
        initializeFonts();
        try {
            docWriter = PdfWriter.getInstance(doc , out);
            addMetaData(doc);
            doc.open();
            PdfContentByte cb = docWriter.getDirectContent();

            boolean beginPage = true;
            int y = 0;
            JSONObject jsonAbonentMonths = abonMonths.getJsonData();
            int cntAbonentMonths = jsonAbonentMonths.getJSONArray("data").length();
            for(int i=0; i < cntAbonentMonths; i++ ){
                if(beginPage){
                    beginPage = false;
                    generateLayout(doc, cb);
                    generateHeader(doc, cb);
                    y = 615;
                }
                generateDetail(doc, cb, jsonAbonentMonths.getJSONArray("data").getJSONObject(i), y);
                y = y - 15;
                if(y < 50){
                    printPageNumber(cb);
                    doc.newPage();
                    beginPage = true;
                }
            }
            printPageNumber(cb);

        }
        catch (DocumentException dex)
        {
            dex.printStackTrace();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            if (doc != null)
            {
                doc.close();
            }
            if (docWriter != null)
            {
                docWriter.close();
            }
        }
    }

    private void initializeFonts(){
        try {
            // not works
            bfBold = BaseFont.createFont(BaseFont.TIMES_BOLD, "CP1251", BaseFont.NOT_EMBEDDED);
            bf = BaseFont.createFont(BaseFont.TIMES_ROMAN, "CP1251", BaseFont.NOT_EMBEDDED);

            // works
            bfs = BaseFont.createFont(FONT, "CP1251", BaseFont.EMBEDDED);
            font = new Font(bfs, 14);
            smallBold = new Font(bfs, 8, Font.BOLD);


        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
