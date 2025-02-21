package com.library.bible.qr;


import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Base64;
import java.util.HashMap;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

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
	
	

private static final String AES_ALGORITHM = "AES";
    
    // AES 암호화 키 (16, 24, 32 바이트 가능)
    private static final String SECRET_KEY = "1234567890123456"; 


    // QR 생성하고 저장 (AES 암호화된 데이터 저장)
    public static void generateQRCodeAES(String data, String filePath) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        // 한글 깨짐 방지: UTF-8 설정
        HashMap<EncodeHintType, Object> hintMap = new HashMap<>();
        hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        
        try {
			String encryptedData = encryptAES(data, SECRET_KEY);
			
			// QR 코드 생성
	        BitMatrix bitMatrix = qrCodeWriter.encode(encryptedData, BarcodeFormat.QR_CODE, 200, 200, hintMap);
	        Path path = FileSystems.getDefault().getPath(filePath);
	        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
	        
	        
	        System.out.println("QR Code generated: " + path);
	        
		} catch (Exception e) {
			e.printStackTrace();
		} // AES 암호화
    }

    // AES 암호화
    public static String encryptAES(String data, String secretKey) throws Exception {
        SecretKey key = new SecretKeySpec(secretKey.getBytes(), AES_ALGORITHM);
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes); // Base64 인코딩하여 저장
    }

    // AES 복호화
    public static String decryptAES(String encryptedData) throws Exception {
        SecretKey key = new SecretKeySpec(SECRET_KEY.getBytes(), AES_ALGORITHM);
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        return new String(decryptedBytes);
    }
    
    
    
	//QR 생성하고 저장 - DEFAULT
	public static void generateQRCode(String data, String filePath) throws WriterException, IOException {
	    QRCodeWriter qrCodeWriter = new QRCodeWriter();
	    
	    // 한글 깨짐 방지: UTF-8 설정
	    HashMap<EncodeHintType, Object> hintMap = new HashMap<>();
	    hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");
	    
	    BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, 200, 200, hintMap);

	    Path path = FileSystems.getDefault().getPath(filePath);
	    MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
	    
	    System.out.println("generateQRCode = " + path);
	}
	
	

	//QR 생성하고 저장 - AZURE 용!!!!!!
//	public static byte[] generateQRCode(String data) throws WriterException, IOException {
//	    QRCodeWriter qrCodeWriter = new QRCodeWriter();
//	    
//	    // 한글 깨짐 방지: UTF-8 설정
//	    HashMap<EncodeHintType, Object> hintMap = new HashMap<>();
//	    hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");
//	    
//	    BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, 200, 200, hintMap);
//	    BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
//
//        // QR 코드를 바이트 배열로 변환
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        ImageIO.write(qrImage, "PNG", baos);
//
//        System.out.println("generateQRCode");
//        return baos.toByteArray();    
//	}

	
	//QR 이미지를 Base64 문자열로 반환 - 지금 안쓰는거임. 테스트용으로
//	public static String generateQRCodeURL(String data) throws WriterException, IOException {
//	    QRCodeWriter qrCodeWriter = new QRCodeWriter();
//	    
//	    //한글 깨짐 방지: UTF-8 설정
//	    HashMap<EncodeHintType, Object> hintMap = new HashMap<>();
//	    hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");
//	    
//	    BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, 50, 50, hintMap);
//	    
//	    BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
//	    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//	    ImageIO.write(qrImage, "PNG", outputStream);
//
//	    byte[] qrBytes = outputStream.toByteArray();
//	    String base64QRCode = Base64.getEncoder().encodeToString(qrBytes);
//
//	    return "data:image/png;base64," + base64QRCode;
//	}


}
