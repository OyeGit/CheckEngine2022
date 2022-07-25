package com.velexsoft.checkengine.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;

import com.itextpdf.text.Anchor;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.Section;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import com.velexsoft.checkengine.ConnActivityParent;
import com.velexsoft.checkengine.R;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.velexsoft.checkengine.ConnActivityParent.l;
import static com.velexsoft.checkengine.ConnActivityParent.logErrors;

/**
 * Created by WoleProject on 11/14/2015.
 */
public class VehicleDiagnosticReport extends PdfPageEventHelper {

    Context context;
    File file;
    private static Font MainTitleFont = new Font(Font.FontFamily.HELVETICA , 24,Font.BOLD);
    private static Font MainTitleFontSub = new Font(Font.FontFamily.HELVETICA, 16,Font.NORMAL);
    private static Font catFont = new Font(Font.FontFamily.HELVETICA, 18,Font.BOLD);
    private static Font catFont1 = new Font(Font.FontFamily.HELVETICA, 15,Font.BOLD);
    private static Font greenFont = new Font(Font.FontFamily.HELVETICA, 10,Font.NORMAL, BaseColor.LIGHT_GRAY);
    private static Font subFont = new Font(Font.FontFamily.HELVETICA, 14,Font.BOLD);
    private static Font tableHeaderFont = new Font(Font.FontFamily.HELVETICA, 12,Font.BOLD);
    private static Font smallNormal = new Font(Font.FontFamily.HELVETICA, 12,Font.NORMAL );
    private static Font tableContentNormal = new Font(Font.FontFamily.HELVETICA, 12,Font.NORMAL, BaseColor.DARK_GRAY);
    private ArrayList<ArrayList<String>> vehInfoMon = new ArrayList<ArrayList<String>>();
    private ArrayList<ArrayList<String>> vehParamArray = new ArrayList<ArrayList<String>>();
    private ArrayList<ArrayList<String>> freezeFrameArray = new ArrayList<ArrayList<String>>();
    private ArrayList<ArrayList<String>> troubleCodeArray = new ArrayList<ArrayList<String>>();
    private ArrayList<ArrayList<String>> dashboardArray = new ArrayList<ArrayList<String>>();
    private ArrayList<ArrayList<String>> resultArrayList = new ArrayList<ArrayList<String>>();
    SharedPreferences sharedPreferences;

    Typeface bwFonts ;
    byte[] bitmapData0;
    byte[] bitmapData;
    byte[] bitmapData1;
    byte[] bitmapIcon4;

    byte[] chekbadBitMapData0;
    byte[] chekokBitMapData0;
    byte[] engTempbadBitMapData0;
    byte[] engTempokBitMapData0;
    byte[] fuelbadBitMapData0;
    byte[] fuelokBitMapData0;
    byte[] coolTempbadBitMapData0;
    byte[] cooltempokBitMapData0;

    public void lw( String msg ) {
        if ( logErrors ) {
            l.w(context, "DIAGREPORT: " + msg);
        }
    }

    public class FooterTable extends PdfPageEventHelper {
        protected PdfPTable footer;
        public FooterTable() {
            this.footer = new PdfPTable(2);
        }

        public void onEndPage(PdfWriter writer, Document document) {

            try {
                int pageNum = writer.getPageNumber();
                footer.setWidths(new int[]{24, 2});
                footer.setTotalWidth(527);
                footer.setLockedWidth(true);
                footer.getDefaultCell().setFixedHeight(40);
                footer.getDefaultCell().setBorder(Rectangle.TOP);
                footer.getDefaultCell().setBorderColor(BaseColor.LIGHT_GRAY);
                int rowEnd = pageNum;
                int rowStart = pageNum - 1;

                footer.addCell(new Phrase("\u00A9 2018 Check Engine | www.checkengine.me ", new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD)));
                footer.addCell(new Phrase(String.format("Page %d", writer.getPageNumber()), new Font(Font.FontFamily.HELVETICA, 9)));
                PdfPCell totalPageCount = new PdfPCell();
                lw (  "totalPageCount = "+totalPageCount );
                footer.writeSelectedRows(rowStart, rowEnd, 36, 64, writer.getDirectContent());

            } catch (Exception e) {
                System.out.println( "On onStartPage PageNumber = "+writer.getPageNumber() );
                lw ( "ERROR ERROR : FooterTable.onEndPage. Error = "+e );
            }
        }

        public void onStartPage(PdfWriter writer, Document document) {

        }
    }


    public VehicleDiagnosticReport(File vfile, ArrayList<ArrayList<String>>  dashboardArray , ArrayList<ArrayList<String>>  vehParamArray, ArrayList<ArrayList<String>>  freezeFrameArray,
                                   ArrayList<ArrayList<String>> troubleCodeArray,
                                   ArrayList<ArrayList<String>>  vehInfoMon, ArrayList<ArrayList<String>>  resultArrayList, Context vContext) {
        super();
        file = vfile;
        l.fileName = ConnActivityParent.logFileName;
        this.vehInfoMon = vehInfoMon;
        this.vehParamArray = vehParamArray;
        this.troubleCodeArray = troubleCodeArray;
        this.freezeFrameArray = freezeFrameArray;
        this.dashboardArray = dashboardArray;
        this.resultArrayList = resultArrayList;
        context = vContext;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        Drawable d0 = context.getResources().getDrawable (R.drawable.dtcstoredreport);
        Bitmap bitmap0 = ((BitmapDrawable)d0).getBitmap();
        ByteArrayOutputStream stream0 = new ByteArrayOutputStream();
        bitmap0.compress(Bitmap.CompressFormat.JPEG, 100, stream0);
        bitmapData0  = stream0.toByteArray();

        Drawable d = context.getResources().getDrawable (R.drawable.checkenginetextimg);
        Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        bitmapData = stream.toByteArray();

        Drawable d1 = context.getResources().getDrawable (R.drawable.background);
        Bitmap bitmap1 = ((BitmapDrawable)d1).getBitmap();
        ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
        bitmap1.compress(Bitmap.CompressFormat.JPEG, 10, stream1);
        bitmapData1 = stream1.toByteArray();
    }

    public void GenerateNow() {
        lw (  "PROGRAM   : VehicleDiagnosticReport.GenerateNow" );
        try {
            Document document = new Document();
            PdfWriter pdfWriter;
            try {
                document.setMargins(30, 20, 98, 80);
                pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(file));
                pdfWriter.setPageEvent( new FooterTable()  );
                document.open();
                addMetaData(document);
                addVehInfoPage(document);
                document.close();
            } catch ( IOException e ) {
                lw ( "ERROR ERROR : GenerateNow. Error = "+e );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addMetaData(Document document) {
        document.addTitle("Check Engine Diagnostic Report");
        document.addSubject("Report");
        document.addKeywords("Monitors, DTCs, Component Test");
        document.addAuthor("Check Engine");
        document.addCreator("Check Engine");
    }

    private void addVehInfoPage(Document document)
            throws DocumentException {

        String lineSep = System.getProperty("line.separator");
        Paragraph preface = new Paragraph();
        addEmptyLine(preface, 1);

        lw ( "dashboardArray size = "+dashboardArray.size() );

        preface.add(new Paragraph(" ", MainTitleFont));
        try {
            Image image = Image.getInstance(bitmapData);
            image.setAbsolutePosition(28f, 685f);
            image.scaleToFit(280f,64f);
            preface.add(image);

            Image image1 = Image.getInstance(bitmapData1);
            image1.setAbsolutePosition(290f, 350f);
            image1.scaleToFit(320f,620f);
            document.add(image1);
        } catch (Exception e) {
            lw ( "ERROR ERROR : addVehInfoPage 1. Error = "+e );
        }

        boolean showNameonReport =  sharedPreferences.getBoolean("name_on_report", false);
        String  displayName =  sharedPreferences.getString("display_name", "");
        String titleVin = "";
        System.out.println( "showNameonReport = "+showNameonReport+", displayName = "+ displayName );
        titleVin = "Diagnostic Report ";
        if ( showNameonReport ) {
            if (displayName==null||displayName.isEmpty()||displayName.contains("Enter your name here") ) {
                System.out.println( "displayName is empty. displayName = "+displayName );
                titleVin = "Diagnostic Report ";
            } else {
                System.out.println( "displayName is not empty. displayName = "+displayName );
                titleVin = "Diagnostic Report. Generated by "+displayName;
            }
        }

        preface.add(new Paragraph(titleVin, MainTitleFontSub));
        preface.add(new Paragraph("Report generated : " + new Date(),  smallNormal));
        addEmptyLine(preface, 1);
        PdfPTable tableicons = new PdfPTable(new float[]{2, 2, 2, 2, 2});
        tableicons.setWidthPercentage(90);
        tableicons.setHorizontalAlignment(0);

        PdfPCell cFirsts1 = null ;
        PdfPCell cFirsts2 = null ;
        PdfPCell cFirsts3 = null ;
        PdfPCell cFirsts4 = null ;
        PdfPCell cFirsts5 = null ;

        String MILon = this.dashboardArray.get(0).get(1);
        ArrayList<String> FuelTank = new ArrayList<>();
        FuelTank =  dashboardArray.get(1);
        ArrayList<String> engineOilTemp = new ArrayList<>();
        engineOilTemp = dashboardArray.get(3);

        IntepretDashValues intp = new IntepretDashValues( context, dashboardArray );

        try {
            Image imageicon1;
            if (MILon.equals("1")) {
                Drawable chkbadcon1 = context.getResources().getDrawable(R.drawable.checkenginebad);
                Bitmap chkbadicon1 = ((BitmapDrawable) chkbadcon1).getBitmap();
                ByteArrayOutputStream chkbadistrm = new ByteArrayOutputStream();
                chkbadicon1.compress(Bitmap.CompressFormat.PNG, 64, chkbadistrm);
                chekbadBitMapData0 = chkbadistrm.toByteArray();
                imageicon1 = Image.getInstance(chekbadBitMapData0);
            } else {
                Drawable dicon1 = context.getResources().getDrawable (R.drawable.checkengineok);
                Bitmap bitmapicon1 = ((BitmapDrawable)dicon1).getBitmap();
                ByteArrayOutputStream streamicon1 = new ByteArrayOutputStream();
                bitmapicon1.compress(Bitmap.CompressFormat.PNG, 64, streamicon1);
                chekokBitMapData0 = streamicon1.toByteArray();
                imageicon1 = Image.getInstance(chekokBitMapData0);
            }
            imageicon1.scaleToFit(24f, 24f);
            imageicon1.setPaddingTop(12f);
            cFirsts1 =  new PdfPCell(imageicon1);
            cFirsts1.setHorizontalAlignment(Element.ALIGN_CENTER);
            tableicons.addCell(cFirsts1);

        } catch ( Exception e ) {
            lw ( "ERROR ERROR: Error in imageIcon. Check Engine Light 1 = "+e );
        }

        try { // Engine Oil Temp
            Image imageicon2;
            if (intp.getEngineOilGoodBad().contains("BAD")) {
                Drawable dicon2 = context.getResources().getDrawable(R.drawable.engineoiltempbad);
                Bitmap bitmapicon2 = ((BitmapDrawable) dicon2).getBitmap();
                ByteArrayOutputStream streamicon2 = new ByteArrayOutputStream();
                bitmapicon2.compress(Bitmap.CompressFormat.PNG, 64, streamicon2);
                engTempbadBitMapData0 = streamicon2.toByteArray();
                imageicon2 = Image.getInstance(engTempbadBitMapData0);
            } else {
                Drawable dicon2a = context.getResources().getDrawable(R.drawable.engineoiltempok);
                Bitmap bitmapicon2a = ((BitmapDrawable) dicon2a).getBitmap();
                ByteArrayOutputStream streamicon2a = new ByteArrayOutputStream();
                bitmapicon2a.compress(Bitmap.CompressFormat.PNG, 64, streamicon2a);
                engTempokBitMapData0 = streamicon2a.toByteArray();
                imageicon2 = Image.getInstance(engTempokBitMapData0);
            }
            imageicon2.scaleToFit(24f, 24f);
            imageicon2.setPaddingTop(12f);
            cFirsts2 =  new PdfPCell(imageicon2);
            cFirsts2.setHorizontalAlignment(Element.ALIGN_CENTER);
            tableicons.addCell(cFirsts2);
        } catch ( Exception e ) {
            lw ( "ERROR ERROR: Error in imageIcon2. Engine Oil Temp 2 = "+e );
        }

        try { // Coolant Temperature
            Image imageicon3;
            if (intp.getCoolTempGoodBad().contains("BAD")) {
                Drawable dicon3 = context.getResources().getDrawable (R.drawable.coolantempbad);
                Bitmap bitmapicon3 = ((BitmapDrawable)dicon3).getBitmap();
                ByteArrayOutputStream streamicon3 = new ByteArrayOutputStream();
                bitmapicon3.compress(Bitmap.CompressFormat.PNG, 64, streamicon3);
                coolTempbadBitMapData0 = streamicon3.toByteArray();
                imageicon3 = Image.getInstance(coolTempbadBitMapData0);
            } else {
                Drawable dicon3a = context.getResources().getDrawable (R.drawable.coolantempok);
                Bitmap bitmapicon3a = ((BitmapDrawable)dicon3a).getBitmap();
                ByteArrayOutputStream streamicon3a = new ByteArrayOutputStream();
                bitmapicon3a.compress(Bitmap.CompressFormat.PNG, 64, streamicon3a);
                cooltempokBitMapData0 = streamicon3a.toByteArray();
                imageicon3 = Image.getInstance(cooltempokBitMapData0);
            }
            imageicon3.scaleToFit(24f, 24f);
            imageicon3.setPaddingTop(12f);
            cFirsts3 =  new PdfPCell(imageicon3);
            cFirsts3.setHorizontalAlignment(Element.ALIGN_CENTER);
            tableicons.addCell(cFirsts3);

        } catch ( Exception e ) {
            lw ( "ERROR ERROR: Error in imageIcon3. Coolant Temperature 3 = "+e );
        }

        try { // input battery
            Image imageicon4 ;
            if ( intp.getInputBatteryBad().contains("BAD") ) {
                Drawable dicon4 = context.getResources().getDrawable (R.drawable.batterybad);
                Bitmap bitmapicon4 = ((BitmapDrawable)dicon4).getBitmap();
                ByteArrayOutputStream streamicon4 = new ByteArrayOutputStream();
                bitmapicon4.compress(Bitmap.CompressFormat.PNG, 64, streamicon4);
                coolTempbadBitMapData0 = streamicon4.toByteArray();
                imageicon4 = Image.getInstance(coolTempbadBitMapData0);
            }else {
                Drawable dicon4a = context.getResources().getDrawable (R.drawable.batteryok);
                Bitmap bitmapicon4a = ((BitmapDrawable)dicon4a).getBitmap();
                ByteArrayOutputStream streamicon4a = new ByteArrayOutputStream();
                bitmapicon4a.compress(Bitmap.CompressFormat.PNG, 64, streamicon4a);
                cooltempokBitMapData0 = streamicon4a.toByteArray();
                imageicon4 = Image.getInstance(cooltempokBitMapData0);
            }
            imageicon4.scaleToFit(24f, 24f);
            imageicon4.setPaddingTop(12f);
            cFirsts4 =  new PdfPCell(imageicon4);
            cFirsts4.setHorizontalAlignment(Element.ALIGN_CENTER);
            tableicons.addCell(cFirsts4);
        } catch ( Exception e ) {
            lw ( "ERROR ERROR: Error in imageIcon4. Coolant Temperature 4 = "+e );
        }

        try {
            Image imageicon5;
            if (intp.getFuelTankGoodBad().contains("BAD")) {
                Drawable chkbadcon1 = context.getResources().getDrawable(R.drawable.gastankbad);
                Bitmap chkbadicon1 = ((BitmapDrawable) chkbadcon1).getBitmap();
                ByteArrayOutputStream chkbadistrm = new ByteArrayOutputStream();
                chkbadicon1.compress(Bitmap.CompressFormat.PNG, 64, chkbadistrm);
                fuelbadBitMapData0 = chkbadistrm.toByteArray();
                imageicon5 = Image.getInstance(fuelbadBitMapData0);
            } else {
                Drawable dicon1 = context.getResources().getDrawable (R.drawable.gastankok);
                Bitmap bitmapicon1 = ((BitmapDrawable)dicon1).getBitmap();
                ByteArrayOutputStream streamicon1 = new ByteArrayOutputStream();
                bitmapicon1.compress(Bitmap.CompressFormat.PNG, 64, streamicon1);
                fuelokBitMapData0 = streamicon1.toByteArray();
                imageicon5 = Image.getInstance(fuelokBitMapData0);
            }
            imageicon5.scaleToFit(24f, 24f);
            imageicon5.setPaddingTop(12f);
            cFirsts5 =  new PdfPCell(imageicon5);
            cFirsts5.setHorizontalAlignment(Element.ALIGN_CENTER);
            tableicons.addCell(cFirsts5);
        } catch ( Exception e ) {
            lw ( "ERROR ERROR: Error in imageIcon5. Coolant Temperature 5 = "+e );
        }

        preface.add(tableicons);
        addEmptyLine(preface, 1);
        PdfPTable tableParams = new PdfPTable(new float[]{ 6,3});
        tableParams.setWidthPercentage(90);
        tableParams.setHorizontalAlignment(0);
        String header1 = ("Vehicle Parameters");
        PdfPCell cFirstCell = new PdfPCell(new Phrase(header1, tableHeaderFont));
        cFirstCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cFirstCell.setCalculatedHeight(64f);
        tableParams.addCell(cFirstCell);

        String header2 = "Parameter Value"+lineSep;
        PdfPCell cFirstCell1 = new PdfPCell(new Phrase(header2, tableHeaderFont));
        cFirstCell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        tableParams.addCell(cFirstCell1);

        ArrayList<String> VehParamRow = new ArrayList<String>();

        for ( int i = 0; i < vehParamArray.size(); i++ ) {
            Integer vNo = i + 1;
            VehParamRow = new ArrayList();
            VehParamRow = vehParamArray.get(i);
            tableParams.addCell( new Phrase( VehParamRow.get(1), tableContentNormal )    );
            tableParams.addCell( new Phrase( VehParamRow.get(2), tableContentNormal )    );
            lw ("This is the table for loop i = " + i + ", " + VehParamRow.get(1) + ", " + VehParamRow.get(2) + ", " + VehParamRow.get(0));
        }
        preface.add(tableParams);
        addEmptyLine(preface, 1);

        Anchor anchor = new Anchor("First Chapter", catFont);
        anchor.setName("First Chapter");
        Chapter catPart = new Chapter(new Paragraph(anchor), 1);
        Paragraph subPara = new Paragraph("Subcategory 1", subFont);
        Section subCatPart = catPart.addSection(subPara);
        preface.add(new Paragraph("Diagnostic Trouble Codes", catFont1));
        addEmptyLine(preface, 1);
        int troubleCodeCnt =  troubleCodeArray.size();

        if (troubleCodeCnt>0) {

            PdfPTable tableDiag = new PdfPTable(new float[]{1, 1, 3, 4, 11});
            PdfPCell c1 = new PdfPCell(new Phrase(""));
            c1.setHorizontalAlignment(Element.ALIGN_LEFT);
            c1.setPaddingTop (Element.ALIGN_CENTER);
            tableDiag.addCell(c1);
            tableDiag.getHeader();
            c1 = new PdfPCell(new Phrase("#", tableHeaderFont));
            c1.setHorizontalAlignment(Element.ALIGN_LEFT);
            tableDiag.addCell(c1);
            c1 = new PdfPCell(new Phrase("DTC", tableHeaderFont));
            c1.setHorizontalAlignment(Element.ALIGN_LEFT);
            tableDiag.addCell(c1);
            c1 = new PdfPCell(new Phrase("Category", tableHeaderFont));
            c1.setHorizontalAlignment(Element.ALIGN_LEFT);
            tableDiag.addCell(c1);
            c1 = new PdfPCell(new Phrase("Description", tableHeaderFont));
            c1.setHorizontalAlignment(Element.ALIGN_LEFT);
            tableDiag.addCell(c1);
            ArrayList<String> newRow = new ArrayList<String>();

            try {

                Image imgbad;
                PdfPCell pcell;
                imgbad = Image.getInstance(bitmapData0);
                imgbad.scaleToFit(12f, 12f);
                imgbad.setAlignment(Image.ALIGN_CENTER);

                imgbad.setPaddingTop(12f);
                pcell = new PdfPCell(imgbad);

                for (int i = 0; i < troubleCodeCnt; i++) {
                    Integer vNo = i + 1;
                    newRow = troubleCodeArray.get(i);
                    tableDiag.addCell(pcell);
                    tableDiag.addCell(vNo.toString());
                    tableDiag.addCell(newRow.get(1));
                    tableDiag.addCell(newRow.get(2));
                    tableDiag.addCell(newRow.get(3));
                }
            } catch (Exception e) {
                lw ( "ERROR ERROR: troubleCodeArray load fail. Error = "+e );
            }
            preface.add(tableDiag);
        } else {
            preface.add(new Paragraph("No Diagnostic Trouble Codes detected", greenFont));
        }

        int monCount = vehInfoMon.size();
        addEmptyLine(preface, 1);
        String ignitionType = ConnActivityParent.ignitionType;
        String ignitionTypeDesc = "Monitors";

        if (ignitionType.equals("0")) {
            ignitionTypeDesc = "Spark Ignition Monitors";
        } else if (ignitionType.equals("1")) {
            ignitionTypeDesc = "Compression Ignition Monitors";
        }

        preface.add(new Paragraph(ignitionTypeDesc, catFont1));
        addEmptyLine(preface, 1);

        if (monCount>0) {

            PdfPTable tableMon = new PdfPTable(new float[]{1, 1, 12, 6});

            PdfPCell c112 = new PdfPCell(new Phrase(""));
            c112.setVerticalAlignment(Element.ALIGN_CENTER);
            tableMon.addCell(c112);
            PdfPCell mc1 = new PdfPCell(new Phrase("#", tableHeaderFont));
            mc1.setHorizontalAlignment(Element.ALIGN_LEFT);
            tableMon.addCell(mc1);
            mc1 = new PdfPCell(new Phrase("Monitors", tableHeaderFont));
            mc1.setHorizontalAlignment(Element.ALIGN_LEFT);
            tableMon.addCell(mc1);
            mc1 = new PdfPCell(new Phrase("Completeness", tableHeaderFont));
            mc1.setHorizontalAlignment(Element.ALIGN_LEFT);
            tableMon.addCell(mc1);
            int counter = 0;

            ArrayList<String> newRow1 = new ArrayList<String>();

            try {
                Image imgbad;
                PdfPCell pcell;

                imgbad = Image.getInstance(bitmapData0);
                imgbad.scaleToFit(12f, 12f);
                imgbad.setPaddingTop(0.16f);
                imgbad.setAlignment(Image.ALIGN_CENTER);
                pcell = new PdfPCell(imgbad);
                for (int i = 0; i < vehInfoMon.size(); i++) {
                    newRow1 = vehInfoMon.get(i);
                    if (newRow1.get(2).contains("1")) {
                        Integer vNo = counter + 1;
                        tableMon.addCell(pcell);
                        tableMon.addCell(vNo.toString());
                        tableMon.addCell(newRow1.get(0));
                        tableMon.addCell("Incomplete");
                    }
                }
            }catch( Exception e ) {
                lw ( "ERROR ERROR: vehInfoMon load fail. Error = "+e );
            }
            preface.add(tableMon);
            addEmptyLine(preface, 1);
        } else {
            preface.add(new Paragraph("All Monitors Completed", greenFont));
            addEmptyLine(preface, 1);
        }

        int freezefrmcount = freezeFrameArray.size();

        if ( freezefrmcount > 0 ) {

            preface.add(new Paragraph("Freeze Frame Data", catFont1));
            addEmptyLine(preface, 1);

            PdfPTable tablefreeze = new PdfPTable(new float[]{1, 1, 14, 4});

            PdfPCell c454 = new PdfPCell(new Phrase(""));
            c454.setHorizontalAlignment(Element.ALIGN_LEFT);
            tablefreeze.addCell(c454);
            PdfPCell cfr = new PdfPCell(new Phrase("#", tableHeaderFont));
            cfr.setHorizontalAlignment(Element.ALIGN_LEFT);
            tablefreeze.addCell(cfr);
            cfr = new PdfPCell(new Phrase("Freeze Frame Parameter", tableHeaderFont));
            cfr.setHorizontalAlignment(Element.ALIGN_LEFT);
            tablefreeze.addCell(cfr);
            cfr = new PdfPCell(new Phrase("Value", tableHeaderFont));
            cfr.setHorizontalAlignment(Element.ALIGN_LEFT);
            tablefreeze.addCell(cfr);

            try {
                Image imgbad;
                PdfPCell pcell;

                imgbad = Image.getInstance(bitmapData0);
                imgbad.scaleToFit(12f, 12f);

                imgbad.setPaddingTop(12f);

                pcell = new PdfPCell(imgbad);

                PdfPCell c444 = new PdfPCell(new Phrase(""));
                c444.setHorizontalAlignment(Element.ALIGN_LEFT);

                for (int i = 0; i < freezeFrameArray.size(); i++) {
                    Integer vNo = i + 1;
                    ArrayList<String> newFreezeRow = new ArrayList<String>();
                    newFreezeRow = freezeFrameArray.get(i);

                    if (i==0) {
                        tablefreeze.addCell(pcell);
                    }else {
                        tablefreeze.addCell(c444);
                    }
                    tablefreeze.addCell(vNo.toString());
                    tablefreeze.addCell(newFreezeRow.get(0));
                    tablefreeze.addCell(newFreezeRow.get(1));
                }

            } catch (Exception e ) {
                lw ( "ERROR ERROR: tablefreeze load fail. Error = "+e );
            }
            preface.add(tablefreeze);
        }
        addEmptyLine(preface, 1);


        int resultArrayListCnt = resultArrayList.size();



        preface.add(new Paragraph("Component Test Results", catFont1));
        addEmptyLine(preface, 1);

        if (resultArrayListCnt > 0) {



            PdfPCell pcellbad = null;
            PdfPCell pcellgd = null;
            try {
                Image imgbad;
                Image imggd;
                imgbad = Image.getInstance(bitmapData0);
                imgbad.scaleToFit(12f, 12f);
                imgbad.setAlignment(Image.ALIGN_CENTER);
                imgbad.setPaddingTop(12f);
                imggd = Image.getInstance(bitmapData0);
                imggd.scaleToFit(12f, 12f);
                imggd.setAlignment(Image.ALIGN_CENTER);
                imggd.setPaddingTop(12f);
                pcellbad = new PdfPCell(imgbad);
                pcellgd =  new PdfPCell(imggd);
            } catch (Exception e) {
                lw("ERROR ERROR: troubleCodeArray load fail. Error = " + e);
            }

            PdfPTable table = new PdfPTable(4);
            table.setSplitLate(false);
            table.setWidths(new int[]{1, 2, 12, 8});

            PdfPCell c1, c2, c3;
            table.addCell("");
            c1 = new PdfPCell(new Phrase("No", tableHeaderFont));
            table.addCell(c1);
            c2 = new PdfPCell(new Phrase("Test Result Definition", tableHeaderFont));
            table.addCell(c2);
            c3 = new PdfPCell(new Phrase("Result", tableHeaderFont));
            table.addCell(c3);

            for (int i = 0; i < resultArrayList.size(); i++) {
                ArrayList<String> n = new ArrayList<String>();
                n = resultArrayList.get(i);

                PdfPTable innertable = new PdfPTable(2);
                innertable.setWidths(new int[]{6, 8});

                innertable.addCell("Min");
                innertable.addCell(n.get(6));
                innertable.addCell("Max");
                innertable.addCell(n.get(7));
                innertable.addCell("Result");
                innertable.addCell(n.get(9));

                String otherside = n.get(3) + " - " + n.get(4) + lineSep
                        + n.get(11) + lineSep
                        + n.get(12) + lineSep
                        + n.get(13) ;

                if ( n.get(8).contains("1")) {
                    table.addCell(pcellgd);
                }else {
                    table.addCell(pcellbad);
                }

                table.addCell(String.valueOf(i + 1));
                table.addCell(otherside);
                table.addCell(innertable);
            }
            preface.add(table);
        } else {
            preface.add(new Paragraph("No failed Component test results", greenFont));
            addEmptyLine(preface, 1);
        }
        document.add(preface);
        document.newPage();

    }

    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }


}
