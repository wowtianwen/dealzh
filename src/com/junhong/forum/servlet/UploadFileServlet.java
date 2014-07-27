package com.junhong.forum.servlet;

/**
 * Copyright ? 2009 www.debug.cn
 * All Rights Reserved
 */
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import com.junhong.auth.entity.User;
import com.junhong.forum.common.Constants;

/**
 * xhEditor文件上传的Java - Servlet实现.
 * 
 * @author easinchu
 * 
 */
@SuppressWarnings({ "serial", "deprecation" })
public class UploadFileServlet extends HttpServlet {

	private static String baseDir = "/ARTICLE_IMG/"; // 上传文件存储目录
	private static String fileExt = "jpg,jpeg,bmp,gif,png";
	private static Long maxSize = 0l;

	@Inject
	private Logger logger;
	// 0:不建目录 1:按天存入目录 2:按月存入目录 3:按扩展名存目录 建议使用按天存
	private static String dirType = "1";

	/**
	 * 文件上传初始化工作
	 */
	public void init() throws ServletException {
		/* 获取web.xml中servlet的配置文件目录参数 */
		baseDir = this.getInitParameter("baseDir");

		/* 获取文件上传存储的相当路径 */
		if (StringUtils.isBlank(baseDir))
			baseDir = "/ARTICLE_IMG/";

		String realBaseDir = this.getServletConfig().getServletContext().getRealPath(baseDir);
		File baseFile = new File(realBaseDir);
		if (!baseFile.exists()) {
			baseFile.mkdir();
		}

		/* 获取文件类型参数 */
		fileExt = this.getInitParameter("fileExt");
		if (StringUtils.isBlank(fileExt))
			fileExt = "jpg,jpeg,gif,bmp,png";

		/* 获取文件大小参数 */
		String maxSize_str = this.getInitParameter("maxSize");
		if (StringUtils.isNotBlank(maxSize_str))
			maxSize = new Long(maxSize_str);

		/* 获取文件目录类型参数 */
		dirType = this.getInitParameter("dirType");

		if (StringUtils.isBlank(dirType))
			dirType = "1";
		if (",0,1,2,3,".indexOf("," + dirType + ",") < 0)
			dirType = "1";
	}

	/**
	 * 上传文件数据处理过程
	 */
	@SuppressWarnings({ "unchecked" })
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html; charset=UTF-8");
		response.setHeader("Cache-Control", "no-cache");

		String err = "";
		String newFileName = "";
		OutputStream out = null;

		if ("application/octet-stream".equals(request.getContentType())) { // HTML
																			// 5
																			// 上传
			InputStream is = null;
			try {
				String dispoString = request.getHeader("Content-Disposition");
				String fileName = "filename=\"";
				int iFindStart = dispoString.indexOf(fileName) + fileName.length();
				int iFindEnd = dispoString.indexOf("\"", iFindStart);
				String sFileName = dispoString.substring(iFindStart, iFindEnd);

				int i = request.getContentLength();
				int fileSize = i;
				if (!validateUploadFile(sFileName, fileSize, response)) {
					return;
				}

				byte buffer[] = new byte[i];
				int j = 0;
				is = request.getInputStream();
				while (j < i) { // 获取表单的上传文件
					int k = is.read(buffer, j, i - j);
					j += k;
				}
				File file = null;
				if (buffer.length >= 0) { // 文件是否为空
					String newFileNameReal = generateFileName(request);
					file = new File(newFileNameReal);
					out = new BufferedOutputStream(new FileOutputStream(file, true));
					out.write(buffer);
					out.close();
					String rootPath = this.getServletConfig().getServletContext().getRealPath("");
					// determine the filepath
					String contextPath = request.getContextPath();
					if (contextPath == null || contextPath.length() == 0) {
						newFileName = newFileNameReal.substring(newFileNameReal.indexOf(rootPath) + rootPath.length());
					} else {
						newFileName = request.getContextPath() + File.separator
								+ newFileNameReal.substring(newFileNameReal.indexOf(rootPath) + rootPath.length());
					}

				}
			} catch (Exception ex) {
				logger.error(ex.getMessage());
			} finally {
				if (is != null) {
					is.close();
				}
				if (out != null) {
					out.close();
				}
			}
		} else {// HTML4 upload
			File savefile = null;
			DiskFileUpload upload = new DiskFileUpload();
			try {
				List<FileItem> items = upload.parseRequest(request);
				Map<String, Serializable> fields = new HashMap<String, Serializable>();
				Iterator<FileItem> iter = items.iterator();

				while (iter.hasNext()) {
					FileItem item = (FileItem) iter.next();
					if (item.isFormField())
						fields.put(item.getFieldName(), item.getString());
					else
						fields.put(item.getFieldName(), item);
				}

				/* 获取表单的上传文件 */
				FileItem uploadFile = (FileItem) fields.get("filedata");

				/* 获取文件上传路径名称 */
				String fileNameLong = uploadFile.getName();
				// System.out.println("fileNameLong:" + fileNameLong);

				if (!validateUploadFile(fileNameLong, uploadFile.getSize(), response)) {
					return;
				}

				String newFileNameReal = generateFileName(request);
				savefile = new File(newFileNameReal);

				/* 存储上传文件 */
				// System.out.println(upload == null);
				uploadFile.write(savefile);

				// 这个地方根据项目的不一样，需要做一些特别的定制。
				String rootPath = this.getServletConfig().getServletContext().getRealPath("");
				// determine the filepath
				String contextPath = request.getContextPath();
				if (contextPath == null || contextPath.length() == 0) {
					newFileName = newFileNameReal.substring(newFileNameReal.indexOf(rootPath) + rootPath.length());
				} else {
					newFileName = request.getContextPath() + File.separator + newFileNameReal.substring(newFileNameReal.indexOf(rootPath) + rootPath.length());
				}
				// System.out.println("newFileName:" + newFileName);
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
				ex.printStackTrace();
				newFileName = "";
				err = "错误: " + ex.getMessage();
			}

		}
		printInfo(response, err, newFileName);
	}

	/**
	 * validate if the uploaded file is valid
	 * 
	 * @param fileName
	 * @param fileSize
	 * @param response
	 * @return
	 * @throws IOException
	 */
	private boolean validateUploadFile(String fileName, long fileSize, HttpServletResponse response) throws IOException {
		boolean isValid = true;
		String uploadFileExt = fileName.substring(fileName.indexOf(".") + 1);
		if (("," + fileExt.toLowerCase() + ",").indexOf("," + uploadFileExt.toLowerCase() + ",") < 0) {
			printInfo(response, fileExt.toLowerCase() + " allowed", "");
			isValid = false;
		}
		/* 文件是否为空 */
		if (fileSize == 0) {
			printInfo(response, "uploaded file empty", "");
			isValid = false;
		}
		/* 检查文件大小 */
		if (maxSize > 0 && fileSize > maxSize) {
			printInfo(response, "uploaded file exceed " + maxSize, "");
			isValid = false;
		}
		return isValid;

	}

	/**
	 * 使用I/O流输出 json格式的数据
	 * 
	 * @param response
	 * @param err
	 * @param newFileName
	 * @throws IOException
	 */
	public void printInfo(HttpServletResponse response, String err, String newFileName) throws IOException {
		PrintWriter out = response.getWriter();
		// String filename = newFileName.substring(newFileName.lastIndexOf("/")
		// + 1);
		out.println("{\"err\":\"" + err + "\",\"msg\":\"" + newFileName + "\"}");
		out.flush();
		out.close();
	}

	private String generateFileName(HttpServletRequest request) {
		// 0:不建目录, 1:按天存入目录, 2:按月存入目录, 3:按扩展名存目录.建议使用按天存.
		String fileFolder = "";
		if (dirType.equalsIgnoreCase("1")) {
			fileFolder = new SimpleDateFormat("yyyyMMdd").format(new Date());
		}

		if (dirType.equalsIgnoreCase("2")) {
			fileFolder = new SimpleDateFormat("yyyyMM").format(new Date());
		}

		/* 文件存储的相对路径 */
		String saveDirPath = baseDir + fileFolder + "/";
		// System.out.println("saveDirPath:" + saveDirPath);

		/* 文件存储在容器中的绝对路径 */
		String saveFilePath = this.getServletConfig().getServletContext().getRealPath("") + saveDirPath;
		// System.out.println("saveFilePath:" + saveFilePath);

		/* 构建文件目录以及目录文件 */
		File fileDir = new File(saveFilePath);
		if (!fileDir.exists()) {
			fileDir.mkdirs();
		}
		HttpSession session = request.getSession();
		Object currUser = session.getAttribute(Constants.CurrentUser);
		String userId = "";
		if (currUser != null) {
			userId = ((User) currUser).getUserId();
		}

		/* 重命名文件 */
		String filename = userId + "_" + System.currentTimeMillis();
		return saveFilePath + filename + ".jpg";
	}
}
