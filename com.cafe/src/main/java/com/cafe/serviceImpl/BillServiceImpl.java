package com.cafe.serviceImpl;

import com.cafe.POJO.Bill;
import com.cafe.constants.CafeConstant;
import com.cafe.dao.BillDao;
import com.cafe.jwt.jwtFilter;
import com.cafe.service.BillService;
import com.cafe.utils.CafeUtil;
import com.google.gson.Gson;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.pdfbox.io.IOUtils;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;


@Service
public class BillServiceImpl implements BillService {

    @Autowired
    jwtFilter jwtFilter;

    @Autowired
    BillDao billDao;

    @Override
    public ResponseEntity<String> generateReport(Map<String, Object> requestMap) {
        try{

            String filename;
            if(requestMap.containsKey("uuid")){
                filename = (String) requestMap.get("uuid");
            }else{
                filename = CafeUtil.getUUID();
                requestMap.put("uuid", filename);
            }
            insertBill(requestMap);
            String data = "Name: "+requestMap.get("name")+"\n"+ "Contact Number: "+requestMap.get("contactNumber")+ "\n"+
                    "Email: "+ requestMap.get("email")+ "\n"+ "Payment Method: " +  requestMap.get("paymentMethod");
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(CafeConstant.STORE_LOCATION + "\\" + filename +".pdf"));

            document.open();
            setRectangleInPdf(document);

            Font fontHelvetica = FontFactory.getFont(FontFactory.HELVETICA_BOLDOBLIQUE);
            fontHelvetica.setColor(BaseColor.BLACK);
            fontHelvetica.setSize(18);
            fontHelvetica.setStyle(Font.BOLD);

            Font fontTimesRoman= FontFactory.getFont(FontFactory.TIMES_ROMAN);
            fontTimesRoman.setSize(11);
            fontTimesRoman.setColor(BaseColor.BLACK);

            Paragraph chunk = new Paragraph("Coffee Corner", fontHelvetica);
            chunk.setAlignment(Element.ALIGN_CENTER);

            document.add(chunk);

            Paragraph paragraph = new Paragraph(data + "\n \n", fontTimesRoman);
            document.add(paragraph);

            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            addTable(table);

            JSONArray jsonArray = CafeUtil.getJSONArray((String) requestMap.get("productDetail"));
            for (int i = 0; i<jsonArray.length(); i++){
                addCell(table,  new Gson().fromJson(jsonArray.getString(i), HashMap.class));
            }
            document.add(table);

            Paragraph footer = new Paragraph("Total: "+requestMap.get("total")+"\n"+ "" +
                    "Thank you!", fontTimesRoman);

            document.add(footer);
            document.close();
            return CafeUtil.getResponseEntity("Report generated successfully", HttpStatus.OK);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtil.getResponseEntity(CafeConstant.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<Bill>> getAllBills(String email) {
        List<Bill> bills = new ArrayList<>();
        try{
            if(jwtFilter.isAdmin()){
                bills = billDao.getAllBills();
            }
            if(jwtFilter.isUser()){
                bills = billDao.getOneBill(email);
            }
            return new ResponseEntity<>(bills, HttpStatus.OK);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<List<Bill>>(new ArrayList<Bill>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<byte[]> getPdf(Map<String, Object> requestMap) {
        try{
            byte[] byteArray = new byte[0];
            if(!requestMap.containsKey("uuid")){
                return new ResponseEntity<byte[]>(byteArray, HttpStatus.BAD_REQUEST);
            }
            String filePath = CafeConstant.STORE_LOCATION+"\\"+(String) requestMap.get("uuid")+".pdf";
            if(CafeUtil.isFileExist(filePath)){
                byteArray = getByteArray(filePath);
                return new ResponseEntity<byte[]>(byteArray, HttpStatus.OK);
            }else{
                generateReport(requestMap);
                byteArray = getByteArray(filePath);
                return new ResponseEntity<byte[]>(byteArray, HttpStatus.OK);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<byte[]>(new byte[0], HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> deleteBill(Integer id) {
        try {
            Optional<Bill> bill = billDao.findById(id);
            if(!bill.isEmpty()){
                billDao.deleteById(id);
                return CafeUtil.getResponseEntity("Bill deleted successfully.", HttpStatus.OK);
            }
            return  CafeUtil.getResponseEntity("Bill does not exist.", HttpStatus.BAD_REQUEST);

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtil.getResponseEntity(CafeConstant.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private byte[] getByteArray(String filePath) throws IOException {
        File initialFile = new File(filePath);
        InputStream targetStream = new FileInputStream(initialFile);
        byte[] byteArray = IOUtils.toByteArray(targetStream);
        targetStream.close();
        return byteArray;
    }

    private void addCell(PdfPTable table, Map<String, Object> data) {
        table.addCell((String) data.get("name"));
        table.addCell((String) data.get("category"));
        table.addCell(Double.toString((Double) data.get("quantity")));
        table.addCell(Double.toString((Double) data.get("price")));
        table.addCell(Double.toString((Double) data.get("total")));
    }

    private void addTable(PdfPTable table) {
        Stream.of("Name", "Category", "Quantity", "Price", "Sub Total")
                .forEach(columnTitle->{
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(2);
                    header.setPhrase(new Phrase(columnTitle));
                    header.setBackgroundColor(new BaseColor(239,209,191));
                    header.setHorizontalAlignment(Element.ALIGN_CENTER);
                    header.setVerticalAlignment(Element.ALIGN_CENTER);
                    table.addCell(header);
                });

    }

    private void setRectangleInPdf(Document document) throws DocumentException {
        Rectangle rect = new Rectangle(580, 825, 15, 15);
        rect.enableBorderSide(1);
        rect.enableBorderSide(2);
        rect.enableBorderSide(4);
        rect.enableBorderSide(8);
        rect.setBorderWidth(1);
        document.add(rect);
    }

    private void insertBill(Map<String, Object> requestMap){
        try{
            Bill bill = new Bill();
            bill.setUuid((String) requestMap.get("uuid"));
            bill.setName((String) requestMap.get("name"));
            bill.setContactNumber((String) requestMap.get("contactNumber"));
            bill.setPaymentMethod((String) requestMap.get("paymentMethod"));
            bill.setTotal((Integer) requestMap.get("total"));
            bill.setEmail((String) requestMap.get(("email")));
            bill.setProductDetail((String) requestMap.get("productDetail"));
            bill.setCreatedBy(jwtFilter.getCurrentUser());
            billDao.save(bill);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

}
