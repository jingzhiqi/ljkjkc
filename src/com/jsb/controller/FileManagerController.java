package com.jsb.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.jsb.Util;
import com.jsb.service.FileUploadService;
import com.jsb.util.FileEncryptAndDecrypt;

@RequestMapping(value="file")
@Controller
public class FileManagerController {
	
	@Autowired
	FileUploadService fileUploadService;
	
	@Value("${dir.virtual}")
	private String virtualDir;
	
	@RequestMapping(value="upload",method=RequestMethod.POST)
	@ResponseBody
	public String uploadFile(@RequestParam MultipartFile file){
		
		return fileUploadService.upLoad("", file,true);
	}
	
	
	
	@RequestMapping(value="uploads",method=RequestMethod.POST)
	@ResponseBody
	public String uploadFiles(@RequestParam MultipartFile... files) throws IllegalStateException, IOException{
		String urls="";
		if(files!=null&&files.length>0){
			for(MultipartFile file:files){
				urls +=fileUploadService.upLoad("", file,true)+",";
			}
		}
		if(urls!=null){
			urls = urls.substring(0, urls.length()-1);
		}
		return urls;
	}
	
	@RequestMapping(value="download",method=RequestMethod.GET)
	public void download(String name,String path,HttpServletResponse resp,HttpServletRequest req){
		OutputStream os = null;  
	    try {
	    	String fileType="";
	    	if(path!=null&&path.length()>0){
	    		if(path.indexOf(".")>0){
	    			fileType = path.substring(path.indexOf("."),path.length());
	    		}
	    	}
			
			//String filepath=virtualDir+path;
			
			String decFilePath=virtualDir+Util.randomUUID32()+fileType;
			
			FileEncryptAndDecrypt.decrypt(virtualDir+path, decFilePath, 8);
			
			File file=new File(decFilePath);
			InputStream fis = new BufferedInputStream(new FileInputStream(file));
	        byte[] buffer = new byte[fis.available()];
	        fis.read(buffer);
	        fis.close();
	        resp.reset();
//	        response.addHeader("Content-Disposition", "attachment;filename=" + new String(filename.getBytes("ISO-8859-1"),"utf-8"));
	        name = URLEncoder.encode(name,"UTF-8");
	        resp.addHeader("Content-Disposition", "attachment;filename=" + name+fileType);
	        resp.addHeader("Content-Length", "" + file.length());
	        OutputStream toClient = new BufferedOutputStream(resp.getOutputStream());
	        resp.setContentType("application/octet-stream");
//	        response.setContentType("");
	        toClient.write(buffer);
	        toClient.flush();
	        toClient.close();
	    }catch(Exception ex){
	    }finally {
	        if (os != null) {  
	            try {
					os.close();
				} catch (IOException e) {
				}  
	        }  
	    }  
	}
	
	@RequestMapping(value="downloadBrower",method=RequestMethod.GET)
	public void downloadBrower(String name,String path,HttpServletResponse resp,HttpServletRequest req){
String virtualDir = req.getSession().getServletContext().getRealPath("/");
		
		System.out.println(virtualDir);
		
		OutputStream os = null;  
	    try {
	    	String fileType="";
	    	if(path!=null&&path.length()>0){
	    		if(path.indexOf(".")>0){
	    			fileType = path.substring(path.indexOf("."),path.length());
	    		}
	    	}
			
			//String filepath=virtualDir+path;
			
			String decFilePath=virtualDir+path;
			
			
			File file=new File(decFilePath);
			InputStream fis = new BufferedInputStream(new FileInputStream(file));
	        byte[] buffer = new byte[fis.available()];
	        fis.read(buffer);
	        fis.close();
	        resp.reset();
//	        response.addHeader("Content-Disposition", "attachment;filename=" + new String(filename.getBytes("ISO-8859-1"),"utf-8"));
	        name = URLEncoder.encode(name,"UTF-8");
	        resp.addHeader("Content-Disposition", "attachment;filename=" + name+fileType);
	        resp.addHeader("Content-Length", "" + file.length());
	        OutputStream toClient = new BufferedOutputStream(resp.getOutputStream());
	        resp.setContentType("application/octet-stream");
//	        response.setContentType("");
	        toClient.write(buffer);
	        toClient.flush();
	        toClient.close();
	    }catch(Exception ex){
	    }finally {
	        if (os != null) {  
	            try {
					os.close();
				} catch (IOException e) {
				}  
	        }  
	    }
	}
	
}
