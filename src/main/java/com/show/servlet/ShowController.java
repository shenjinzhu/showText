package com.show.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.show.entity.FileSave;

@Controller
public class ShowController {

	private static String text = "ks";
	private static List<FileSave> files = new ArrayList<FileSave>();
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static int num = 12345;

	@RequestMapping(value = "/save", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String saveMsg(String msg) {
		try {
			text = URLDecoder.decode(msg, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return JSON.toJSONString("Ok");
	}

	@RequestMapping("/show")
	public String show() {
		return "scan";
	}

	@RequestMapping(value = "/getText", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String getText() {
		return JSON.toJSONString(text);
	}

	@RequestMapping("/fileHtml")
	public String fileHtml() {
		return "fileUp";
	}

	@RequestMapping("/fileShow")
	public String showFile() {
		return "showFile";
	}

	@RequestMapping("/upFile")
	public String upFile(MultipartFile file) throws Exception {
		System.out.println("文件上传");
		String fileName = file.getOriginalFilename();
		String path = "/usr/local/files/upfile/" + fileName;
		File targetFile = new File(path);
		if (!targetFile.exists()) {
			targetFile.mkdirs();
		}
		try {
			file.transferTo(targetFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		FileSave fs = new FileSave();
		fs.setName(path.substring(path.split("/").length - 1, path.length()));
		fs.setUrl(path);
		fs.setTime(sdf.format(new Date()));
		fs.setSize(targetFile.length() / 1024.0 + "KB");
		fs.setId("" + (num++));
		fileAdd(fs);
		System.out.println(fs);
		return "redirect:fileShow";
	}

	@RequestMapping(value = "/showFileAchieve", produces = "application/json;charset=utf-8")
	public void showFileAchieve(HttpServletResponse response, HttpServletRequest request) {
		try {
			response.setContentType("text/plain;charset=utf-8");
			response.setHeader("Pragma", "No-cache");
			response.setHeader("Cache-Control", "no-cache");
			response.setDateHeader("Expires", 0);
			response.setHeader("Access-Control-Allow-Origin", "*");
			PrintWriter out;
			out = response.getWriter();
			// String jsonpCallback = request.getParameter("callback");
			out.println(JSON.toJSONString(files));
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/fileDelete")
	public String fileDelete(HttpServletRequest request, HttpServletResponse response, String id) {
		for (FileSave fs : files) {
			if (fs != null && fs.getId() != null && fs.getId().equals(id)) {
				fileRemove(fs);
				break;
			}
		}
		return "redirect:fileShow";
	}

	@RequestMapping(value = "/fileDownload_real")
	public void downFile_real(HttpServletRequest request, HttpServletResponse response, String url) {
		try {
			response.setContentType("application/msexcel;");
			response.setHeader("Content-Disposition",
					new String(("attachment;filename=" + url.substring(url.split("/").length - 1, url.length()))
							.getBytes("GB2312"), "UTF-8"));
			File f = new File(url);
			FileInputStream in = new FileInputStream(f);
			byte b[] = new byte[1024];
			int i = 0;
			ServletOutputStream out = response.getOutputStream();
			while ((i = in.read(b)) != -1) {
				out.write(b, 0, i);
			}
			out.flush();
			out.close();
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@RequestMapping(value = "/fileDownload")
	public void downFile(HttpServletRequest request, HttpServletResponse response, String id) {
		try {
			String url = "";
			for (FileSave fs : files) {
				if (fs != null && fs.getId() != null && fs.getId().equals(id)) {
					url = fs.getUrl();
				}
			}
			System.out.println(url);
			response.setContentType("application/msexcel;");
			response.setHeader("Content-Disposition",
					new String(("attachment;filename=" + url.substring(url.split("/").length - 1, url.length()))
							.getBytes("GB2312"), "UTF-8"));
			File f = new File(url);
			FileInputStream in = new FileInputStream(f);
			byte b[] = new byte[1024];
			int i = 0;
			ServletOutputStream out = response.getOutputStream();
			while ((i = in.read(b)) != -1) {
				out.write(b, 0, i);
			}
			out.flush();
			out.close();
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 文件放入
	 * 
	 * @param file
	 */
	private synchronized void fileAdd(FileSave file) {
		files.add(file);
	}

	/**
	 * 文件删除操作
	 * 
	 * @param file
	 */
	private synchronized void fileRemove(FileSave file) {
		File f = new File(file.getUrl());
		f.delete();
		files.remove(file);
	}

	/**
	 * 文件时间获取
	 * 
	 * @param fullFileName
	 * @return
	 */
	private static Date getCreateTime2(String fullFileName) {
		Path path = Paths.get(fullFileName);
		BasicFileAttributeView basicview = Files.getFileAttributeView(path, BasicFileAttributeView.class,
				LinkOption.NOFOLLOW_LINKS);
		BasicFileAttributes attr;
		try {
			attr = basicview.readAttributes();
			Date createDate = new Date(attr.creationTime().toMillis());
			return createDate;
		} catch (Exception e) {
			e.printStackTrace();
		}
		Calendar cal = Calendar.getInstance();
		cal.set(1970, 0, 1, 0, 0, 0);
		return cal.getTime();
	}
}
