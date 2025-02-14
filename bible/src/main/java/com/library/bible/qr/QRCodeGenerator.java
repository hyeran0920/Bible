package com.library.bible.qr;


import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Base64;
import java.util.HashMap;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class QRCodeGenerator {

    /*
     * @param data      QR 코드에 저장할 데이터
     * @param filePath  QR 코드 이미지 파일 경로
     * @throws WriterException QR 코드 생성 중 예외
     * @throws IOException     파일 저장 중 예외
    */
	
	//이거 UploadController에 넣을까 고민중
	//QR 생성하고 저장
	public static byte[] generateQRCode(String data) throws WriterException, IOException {
	    QRCodeWriter qrCodeWriter = new QRCodeWriter();
	    
	    // 한글 깨짐 방지: UTF-8 설정
	    HashMap<EncodeHintType, Object> hintMap = new HashMap<>();
	    hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");
	    
	    BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, 200, 200, hintMap);
	    BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

        // QR 코드를 바이트 배열로 변환
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(qrImage, "PNG", baos);

        System.out.println("generateQRCode");
        return baos.toByteArray();    
	}

	
	//QR 이미지를 Base64 문자열로 반환 - 지금 안쓰는거임
	public static String generateQRCodeURL(String data) throws WriterException, IOException {
	    QRCodeWriter qrCodeWriter = new QRCodeWriter();
	    
	    //한글 깨짐 방지: UTF-8 설정
	    HashMap<EncodeHintType, Object> hintMap = new HashMap<>();
	    hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");
	    
	    BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, 50, 50, hintMap);
	    
	    BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
	    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	    ImageIO.write(qrImage, "PNG", outputStream);

	    byte[] qrBytes = outputStream.toByteArray();
	    String base64QRCode = Base64.getEncoder().encodeToString(qrBytes);

	    return "data:image/png;base64," + base64QRCode;
	}


}
