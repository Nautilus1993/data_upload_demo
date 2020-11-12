package com.bjsasc.FTPClientSDK.util;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.Cleaner;

import java.io.*;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.AccessController;
import java.security.MessageDigest;
import java.security.PrivilegedAction;
@Slf4j
/**
 * 计算文件MD5工具类
 */
public class FileMD5Util {

  private final static Logger logger = LoggerFactory.getLogger(FileMD5Util.class);

  public static String getFileMD5(File file) throws FileNotFoundException {

    String value = null;
    FileInputStream in = new FileInputStream(file);
    MappedByteBuffer byteBuffer = null;
    try {
      byteBuffer = in.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
      MessageDigest md5 = MessageDigest.getInstance("MD5");
      md5.update(byteBuffer);
      BigInteger bi = new BigInteger(1, md5.digest());
      value = bi.toString(16);
      if (value.length() < 32) {
        value = "0" + value;
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      close(in, byteBuffer);
    }
    return value;
  }
  public static void close(FileInputStream in, MappedByteBuffer byteBuffer) {

    if (null != in) {
      try {
        in.getChannel().close();
        in.close();
      } catch (IOException e) {
        log.error("close error:"+e.getMessage(), e);
      }
    }
    if (null != byteBuffer) {
      freedMappedByteBuffer(byteBuffer);
    }
  }

  public static void close(final Closeable closeable){
    if(closeable != null){
      try {
        closeable.close();
      } catch (IOException e) {
        log.error("close fail:"+e.getMessage(),e);
      } finally {
      }
    }
  }

  /**
   * 在MappedByteBuffer释放后再对它进行读操作的话就会引发jvm crash，在并发情况下很容易发生 正在释放时另一个线程正开始读取，于是crash就发生了。所以为了系统稳定性释放前一般需要检 查是否还有线程在读或写
   */
  public static void freedMappedByteBuffer(final MappedByteBuffer mappedByteBuffer) {

    try {
      if (mappedByteBuffer == null) {
        return;
      }

      mappedByteBuffer.force();
      AccessController.doPrivileged(new PrivilegedAction<Object>() {
        @Override
        public Object run() {

          try {
            Method getCleanerMethod = mappedByteBuffer.getClass().getMethod("cleaner", new Class[0]);
            getCleanerMethod.setAccessible(true);
            Cleaner cleaner = (Cleaner) getCleanerMethod.invoke(mappedByteBuffer,
                    new Object[0]);
            cleaner.clean();
          } catch (Exception e) {
            log.error("clean MappedByteBuffer error!!!", e);
          }
          log.info("clean MappedByteBuffer completed!!!");
          return null;
        }
      });

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void clean(final Object buffer) {

    AccessController.doPrivileged(new PrivilegedAction() {
      public Object run() {

        try {
          Method getCleanerMethod = buffer.getClass().getMethod("cleaner", new Class[0]);
          getCleanerMethod.setAccessible(true);
          Cleaner cleaner = (Cleaner) getCleanerMethod.invoke(buffer, new Object[0]);
          cleaner.clean();
        } catch (Exception e) {
          log.error("clean fail :" + e.getMessage(), e);
        }
        return null;
      }
    });

  }

  /**

   * 获取该输入流的MD5值

   *

   * @param is

   * @return

   * @throws Exception

   * @throws IOException

   */

  public static String getMD5(InputStream is) throws  Exception {
    StringBuffer md5 = new StringBuffer();
    MessageDigest md = MessageDigest.getInstance("MD5");
    byte[] dataBytes = new byte[1024];
    int nread = 0;
    while ((nread = is.read(dataBytes)) != -1) {
      md.update(dataBytes, 0, nread);
    };
    byte[] mdbytes = md.digest();
    // convert the byte to hex format
    for (int i = 0; i < mdbytes.length; i++) {
      md5.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
    }
    return md5.toString();
  }
  public static String getFileMD5(byte[] uploadBytes) {

    try {
//      byte[] uploadBytes = file.getBytes();
      MessageDigest md5 = MessageDigest.getInstance("MD5");
      byte[] digest = md5.digest(uploadBytes);
      String hashString = new BigInteger(1, digest).toString(16);
      return hashString;
    } catch (Exception e) {
      logger.error("get file md5 error!!!", e);
      e.printStackTrace();
     }
    return null;
  }



//  public static void main(String[] args) throws Exception {
//
//    long start = System.currentTimeMillis();
//    String filePath = "F:\\desktop\\uploads\\461ad6106d8253b94bd00546a4a1a8e4\\pycharm-professional-2019.1.3.exe";
//    File file = new File(filePath);
//    String md5 = FileMD5Util.getFileMD5(file);
//    long end = System.currentTimeMillis();
//    System.out.println("cost:" + (end - start) + "ms, md5:" + md5);
//  }
}
