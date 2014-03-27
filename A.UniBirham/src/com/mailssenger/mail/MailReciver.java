package com.mailssenger.mail;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;

import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

import com.mailssenger.util.L;
import com.mailssenger.util.TimeUtil;

import android.annotation.SuppressLint;

/**  
 * reciver the email, with the attachment
 * */  
@SuppressLint("DefaultLocale")  
public class MailReciver {  
  
    private MimeMessage mimeMessage = null;  
    private String saveAttachPath = ""; // path that store thw attachment
    private StringBuffer bodytext = new StringBuffer();// save the mail content
    private String dateformat = "yy-MM-dd HH:mm"; // defalut time format
  
    public MailReciver(MimeMessage mimeMessage) {  
        this.mimeMessage = mimeMessage;  
    }  
  
    /*
     * set mimemessage
     */
    public void setMimeMessage(MimeMessage mimeMessage) {  
        this.mimeMessage = mimeMessage;  
    }  
  
    /**  
     *get sender's name and address 
     */  
    public String getFrom() throws Exception {  
        InternetAddress address[] = (InternetAddress[]) mimeMessage.getFrom();  
        String from = address[0].getAddress();  
        if (from == null)  
            from = "";  
        String personal = address[0].getPersonal();  
        if (personal == null)  
            personal = "";  
        String fromaddr = personal + "<" + from + ">";  
        return fromaddr;  
    }  
  
    /** 
     * get the reciver, cc and bcc address and name
     * by pass %to  %cc %bcc 
     */  
    @SuppressLint("DefaultLocale")  
    public String getMailAddress(String type) throws Exception {  
        String mailaddr = "";  
        String addtype = type.toUpperCase();  
        InternetAddress[] address = null;  
        
        //judge what type is the addr  and get it
        if (addtype.equals("TO") || addtype.equals("CC")  
                || addtype.equals("BCC")) {  
            if (addtype.equals("TO")) {  
                address = (InternetAddress[]) mimeMessage  
                        .getRecipients(Message.RecipientType.TO);  
            } else if (addtype.equals("CC")) {  
                address = (InternetAddress[]) mimeMessage  
                        .getRecipients(Message.RecipientType.CC);  
            } else {  
                address = (InternetAddress[]) mimeMessage  
                        .getRecipients(Message.RecipientType.BCC);  
            }  
            //if address is not null, get the address and convert it into string
            if (address != null) {  
                for (int i = 0; i < address.length; i++) {  
                    String email = address[i].getAddress();  
                    if (email == null)  
                        email = "";  
                    else {  
                    	//decode for non english
                        email = MimeUtility.decodeText(email);  
                    }  
                    String personal = address[i].getPersonal();  
                    if (personal == null)  
                        personal = "";  
                    else {  
                    	//decode for non english
                        personal = MimeUtility.decodeText(personal);  
                    }  
                    String compositeto = personal + "<" + email + ">";  
                    mailaddr += "," + compositeto;  
                }  
                mailaddr = mailaddr.substring(1);  
            }  
        } else {  
            throw new Exception("Error emailaddr type!");  
        }  
        return mailaddr;  
    }  
  
    /* 
     * Get subject of the mail
     */  
    public String getSubject() throws MessagingException {  
        String subject = "";  
        try {  
        	//decode for non english
            subject = MimeUtility.decodeText(mimeMessage.getSubject());  
            if (subject == null)  
                subject = "";  
        } catch (Exception exce) {  
        }  
        return subject;  
    }  
  
    /* 
     * Get send date of mail
     */  
    @SuppressLint("SimpleDateFormat")  
    public String getSentDate() throws Exception {
    	
        java.util.Date sentdate = mimeMessage.getSentDate(); 
        SimpleDateFormat format = new SimpleDateFormat(dateformat);  
        String result = format.format(sentdate);
        
//        L.i("邮箱本来时间格式:");
//        System.out.println(sentdate);
//        L.i("存在数据库中的格式");
//        System.out.println(result);
//        L.i("时间工具转换后格式:");
//        System.out.println(TimeUtil.parseTime(result ));
//        
//        L.i("时间工具转换后格式:");
//        System.out.println(System.currentTimeMillis());
        
        return result;  
    }  
  
    /*
     * get email content
     */  
    public String getBodyText() {  
        return bodytext.toString();  
    }  
  
    /*
     * parse the mail .save the mail content into a StringBuffer
     * parse the mail according to the MimeType
     */  
    public void getMailContent(Part part) throws Exception {  
        String contenttype = part.getContentType();  
        int nameindex = contenttype.indexOf("name");  
        boolean conname = false;  
        if (nameindex != -1)  
            conname = true;  
        System.out.println("CONTENTTYPE: " + contenttype);  
        
        //save the content accroding to different type
        if (part.isMimeType("text/plain") && !conname) { 
        	//if plain text appen directly
            bodytext.append((String) part.getContent());  
        } else if (part.isMimeType("text/html") && !conname) { 
        	//if html appen too
            bodytext.append((String) part.getContent());  
        } else if (part.isMimeType("multipart/*")) { 
        	//if multipart/* get all the mutipart
            Multipart multipart = (Multipart) part.getContent();  
            int counts = multipart.getCount();  
            for (int i = 0; i < counts; i++) {  
                getMailContent(multipart.getBodyPart(i));  
            }  
        } else if (part.isMimeType("message/rfc822")) {  
            getMailContent((Part) part.getContent());  
        } else {  
        }  
    }  
  
    /*  
     * judge whether the mail need reply
     */  
    public boolean getReplySign() throws MessagingException {  
        boolean replysign = false;  
        String needreply[] = mimeMessage  
                .getHeader("Disposition-Notification-To");  
        if (needreply != null) {  
            replysign = true;  
        }  
        return replysign;  
    }  
  
    /* 
     * Get the message id of the mail 
     */  
    public String getMessageId() throws MessagingException {  
        return mimeMessage.getMessageID();  
    }  
  
    /*  
     * to see whether the mail has been read or not ,
     * false when unread and true when read
     * can not be used when using POP3
     */  
    public boolean isNew() throws MessagingException {  
        boolean isnew = false;
        //since is new is false , so mail is show as unread every time
        Flags flags = ((Message) mimeMessage).getFlags();  
        System.out.println("--------flags-------" + flags);  
       //get flag
        Flags.Flag[] flag = flags.getSystemFlags();  
        //debug
        System.out.println("----flag----" + flag);  
        System.out.println("flags's length: " + flag.length);  
        
        for (int i = 0; i < flag.length; i++) {  
            //debug
        	System.out.println("flag=======" + flag[i]);  
            System.out.println("-=-=-=Flags.Flag.SEEN=-=-=-="+Flags.Flag.SEEN);  
            //se whether the flag is seen
            if (flag[i] == Flags.Flag.SEEN) {  
                isnew = true;  
                System.out.println("seen Message.......");  
                break;  
            }  
        }  
        return isnew;  
    }  
  
    /*  
     * To see whether the mail has attachment  
     */  
    @SuppressLint("DefaultLocale")  
    public boolean isContainAttach(Part part) throws Exception {  
        boolean attachflag = false;  
        // String contentType = part.getContentType();  
        if (part.isMimeType("multipart/*")) {  
            Multipart mp = (Multipart) part.getContent();  
            for (int i = 0; i < mp.getCount(); i++) {  
                BodyPart mpart = mp.getBodyPart(i);  
                String disposition = mpart.getDisposition();  
                if ((disposition != null)  
                        && ((disposition.equals(Part.ATTACHMENT)) || (disposition  
                                .equals(Part.INLINE))))  
                    attachflag = true;  
                else if (mpart.isMimeType("multipart/*")) {  
                    attachflag = isContainAttach((Part) mpart);  
                } else {  
                    String contype = mpart.getContentType();  
                    if (contype.toLowerCase().indexOf("application") != -1)  
                        attachflag = true;  
                    if (contype.toLowerCase().indexOf("name") != -1)  
                        attachflag = true;  
                }  
            }  
        } else if (part.isMimeType("message/rfc822")) {  
            attachflag = isContainAttach((Part) part.getContent());  
        }  
        return attachflag;  
    }  
  
    /**  
     * Save the Attachment
     */  
    @SuppressLint("DefaultLocale")  
    public void saveAttachMent(Part part) throws Exception {  
        String fileName = "";  
        if (part.isMimeType("multipart/*")) {  
            Multipart mp = (Multipart) part.getContent();  
            for (int i = 0; i < mp.getCount(); i++) {
            	//handle the main content
                BodyPart mpart = mp.getBodyPart(i);
                String disposition = mpart.getDisposition();  
                if ((disposition != null)  
                        && ((disposition.equals(Part.ATTACHMENT)) || (disposition  
                                .equals(Part.INLINE)))) {//ATTACHMENT，INLINE  
                    fileName = mpart.getFileName();  
                    if (fileName.toLowerCase().indexOf("gb18030") != -1) {  
                        fileName = MimeUtility.decodeText(fileName);  
                    }  
                    saveFile(fileName, mpart.getInputStream());  
                } else if (mpart.isMimeType("multipart/*")) {  
                    saveAttachMent(mpart);  
                } else {  
                    fileName = mpart.getFileName();  
                    if ((fileName != null)  
                            && (fileName.toLowerCase().indexOf("GB18030") != -1)) {  
                        fileName = MimeUtility.decodeText(fileName);  
                        saveFile(fileName, mpart.getInputStream());  
                    }  
                }  
            }  
        } else if (part.isMimeType("message/rfc822")) {  
            saveAttachMent((Part) part.getContent());  
        }  
    }  
  
    /**  
     * Set the path where to save the attachment
     */  
  
    public void setAttachPath(String attachpath) {  
        this.saveAttachPath = attachpath;  
    }  
  
    /**  
     * get the format how the date will be show
     */  
    public void setDateFormat(String format) throws Exception {  
        this.dateformat = format;  
    }  
  
    /**  
     * get the attchment save path
     */  
    public String getAttachPath() {  
        return saveAttachPath;  
    }  
  
    /**  
     * Save file to certain catogary
     */  
    @SuppressLint("DefaultLocale")  
    private void saveFile(String fileName, InputStream in) throws Exception {  
        String osName = System.getProperty("os.name");  
        System.out.println("----fileName----" + fileName);  
        // String storedir = getAttachPath();  
//      String separator = "";  
        if (osName == null)  
            osName = "";  
        File storefile = new File(File.separator + "mnt" + File.separator  
                + "sdcard" + File.separator + fileName);  
  
        storefile.createNewFile();  
        System.out.println("storefile's path: " + storefile.toString());  
//       for(int i=0;storefile.exists();i++){  
//       storefile = new File(storedir+separator+fileName+i);  
//       }  
  
        BufferedOutputStream bos = null;  
        BufferedInputStream bis = null;  
        try {  
            bos = new BufferedOutputStream(new FileOutputStream(storefile));  
            bis = new BufferedInputStream(in);  
            int c;  
            while ((c = bis.read()) != -1) {  
                bos.write(c);  
                bos.flush();  
            }  
        } catch (Exception exception) {  
            exception.printStackTrace();  
            throw new Exception("File save failed!");  
        } finally {  
            bos.close();  
            bis.close();  
        }  
    }  

}
