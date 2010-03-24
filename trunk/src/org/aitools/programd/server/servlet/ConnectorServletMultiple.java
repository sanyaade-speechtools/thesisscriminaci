package org.aitools.programd.server.servlet;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ConnectorServletMultiple extends HttpServlet {

	private static final long serialVersionUID = -2390671649459325455L;
	private static boolean debug=false;
	private static String baseDir = "plugins/lsa/spaces";
	private static String realBaseDir = "";

	/**
	 * Initialize the servlet.<br>
	 * Retrieve from the servlet configuration the "baseDir" which is the root of the file repository:<br>
	 * If not specified the value of "/UserFiles/" will be used.
	 *
	 */
	public void init() throws ServletException { 

		realBaseDir=getServletContext().getRealPath(baseDir);
		File baseFile=new File(realBaseDir);
		if(!baseFile.exists()){
			baseFile.mkdir();
		}
		if(debug) System.out.println("\r\n---- Connector Servlet initialization started ----");

		if(debug) System.out.println("----  Connector Servlet initialization completed ----\r\n");

	}


	/**
	 * Manage the Get requests (GetFolders, GetFoldersAndFiles, CreateFolder).<br>
	 *
	 * The servlet accepts commands sent in the following format:<br>
	 * connector?Command=CommandName&Type=ResourceType&CurrentFolder=FolderPath<br><br>
	 * It execute the command and then return the results to the client in XML format.
	 *
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (debug) System.out.println("--- BEGIN DOGET ---");

		response.setContentType("text/xml; charset=UTF-8");
		response.setHeader("Cache-Control","no-cache");
		PrintWriter out = response.getWriter();

		String commandStr=request.getParameter("Command");
		String currentFolderStr=request.getParameter("CurrentFolder");
		String typeStr=request.getParameter("Type");
		String relatedPath=request.getParameter("RelatedPath");

		Document document=null;
		try {
			DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			document=builder.newDocument();
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		}

		String currentDirPath = "";
		String currentUrlPath = "";

		if(relatedPath != null && relatedPath.equals("true")){
			currentUrlPath =  File.separator + typeStr + currentFolderStr;
			currentDirPath =  realBaseDir + File.separator + typeStr + File.separator +  currentFolderStr;
		}else{
			currentUrlPath =  File.separator + typeStr + currentFolderStr;
			currentDirPath =  realBaseDir + File.separator + typeStr + File.separator +  currentFolderStr;
		}


		File currentDir=new File(currentDirPath);
		if(!commandStr.equals("DeleteFile")){
			if(!currentDir.exists()){
				currentDir.mkdirs();		
			}
		}

		Node root=CreateCommonXml(document,commandStr,currentFolderStr,currentUrlPath);

		if (debug) System.out.println("Command = " + commandStr);

		if(commandStr.equals("GetFolders")) {
			getFolders(currentDir,root,document);
		}
		else if (commandStr.equals("GetFoldersAndFiles")) {
			getFolders(currentDir,root,document);
			getFiles(currentDir,root,document);
		}
		else if (commandStr.equals("DeleteFile")) {
			deleteFile(currentDir,root,document);
		}
		else if (commandStr.equals("CreateFolder")) {
			String newFolderStr=request.getParameter("NewFolderName");
			File newFolder=new File(currentDir,newFolderStr);
			String retValue="110";

			if(newFolder.exists()){
				retValue="101";
			}
			else {
				try {
					boolean dirCreated = newFolder.mkdirs();
					if(dirCreated)
						retValue="0";
					else
						retValue="102";
				}catch(SecurityException sex) {
					retValue="103";
				}

			}			
			setCreateFolderResponse(retValue,root,document);
		}	
		else if (commandStr.equals("RenameFolder")) {
			String newName=request.getParameter("NewName");
			String oldName=request.getParameter("OldName");
			File destFolder=new File(currentDir,newName);
			File sourceFolder=new File(currentDir,oldName);
			String retValue = "101";
			if(destFolder.exists()){
				retValue="101";
			}
			else {
				try {
					/*if(sourceFolder.isDirectory()){
							copyDirectory(sourceFolder, destFolder);
						}else{
							copyFile(sourceFolder, destFolder);
						}*/
					boolean t = sourceFolder.renameTo(destFolder);
					if(t)
						retValue="0";
					else
						retValue="103";
				}catch(Exception sex) {
					retValue="103";
				}
			}	
			setCreateFolderResponse(retValue,root,document);
		}


		document.getDocumentElement().normalize();
		try {
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer();

			DOMSource source = new DOMSource(document);

			StreamResult result = new StreamResult(out);
			transformer.transform(source, result);

			if (debug) {
				StreamResult dbgResult = new StreamResult(System.out);
				transformer.transform(source, dbgResult);
				System.out.println("");
				System.out.println("--- END DOGET ---");
			}


		} catch (Exception ex) {
			ex.printStackTrace();
		}

		out.flush();
		out.close();
	}


	/**
	 * Manage the Post requests (FileUpload).<br>
	 *
	 * The servlet accepts commands sent in the following format:<br>
	 * connector?Command=FileUpload&Type=ResourceType&CurrentFolder=FolderPath<br><br>
	 * It store the file (renaming it in case a file with the same name exists) and then return an HTML file
	 * with a javascript command in it.
	 *
	 */	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		if (debug) System.out.println("--- BEGIN DOPOST ---");

		response.setContentType("text/html; charset=UTF-8");
		response.setHeader("Cache-Control","no-cache");
		PrintWriter out = response.getWriter();

		String commandStr=request.getParameter("Command");
		String typeStr=request.getParameter("Type");
		String currentFolderStr=request.getParameter("CurrentFolder");

		String relatedPath=request.getParameter("RelatedPath");


		//Load Config
		//MediaLibrarySetting conf = this.loadFilemanagerConf(typeStr,request);
		String currentDirPath = "";
		String currentUrlPath = "";


		if(relatedPath != null && relatedPath.equals("true")){
			currentUrlPath =  File.separator + typeStr + currentFolderStr;
			currentDirPath =  realBaseDir + File.separator + typeStr + File.separator +  currentFolderStr;
		}else{
			currentUrlPath = File.separator + typeStr + currentFolderStr;
			currentDirPath =  realBaseDir + File.separator + typeStr + File.separator +  currentFolderStr;
		}
		//create typeStr dir
		File currentDir=new File(currentDirPath);
		if(!currentDir.exists()){
			currentDir.mkdirs();		
		}

		if (debug) System.out.println(currentDirPath);
		if(debug) System.out.println(commandStr);

		String retVal="0";
		String newName="";
		String fileUrl="";
		String errorMessage="";

		commandStr = commandStr.trim();
		
		String script="";

		String strCommand = "";

		if((!commandStr.equalsIgnoreCase("FileUpload")) && (!commandStr.equalsIgnoreCase("MultipleFileUpload")) && (!commandStr.equalsIgnoreCase("ZipFileUpload"))){
			//System.out.println("Comando non conosciuto");
			retVal="203";
			script = strCommand + "OnUploadCompleted("+retVal+",'"+fileUrl+"','"+newName+"','"+errorMessage+"');";
		}
		else {
			FileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload up = new ServletFileUpload(factory);
			try {
				if(debug) System.out.println("Sono dentro il try.");
				List<?> items = up.parseRequest(request);

				Map<String, FileItem> fields=new HashMap<String, FileItem>();

				Iterator<?> iter = items.iterator();
				while (iter.hasNext()) {
					FileItem item = (FileItem) iter.next();
					if (item.isFormField()){
						//System.out.println("Form field name" + item.getFieldName());
						//fields.put(item.getFieldName(),item.getString());
					}
					else{
						//System.out.println("File field Name " + item.getFieldName());
						fields.put(item.getFieldName(),item);
					}
				}				

				if(commandStr.equalsIgnoreCase("FileUpload")){
					FileItem uplFile=(FileItem)fields.get("NewFile");
					String fileNameLong=uplFile.getName();
					fileNameLong=fileNameLong.replace('\\','/');
					strCommand = "window.parent.frames['frmUpload'].";
					script += strCommand + this.saveFile(fileNameLong, currentDirPath, currentUrlPath, typeStr, uplFile);	
				}else if(commandStr.equalsIgnoreCase("ZipFileUpload")){
					FileItem uplFile=(FileItem)fields.get("NewFile");
					strCommand = "window.parent.frames['frmZipUpload'].";
					String fileNameLong=uplFile.getName();
					fileNameLong=fileNameLong.replace('\\','/');

					//Save Zip File
					File zip = this.saveZipFile(fileNameLong, currentDirPath, currentUrlPath, typeStr, uplFile);
					if(zip==null){
						retVal="203";
						errorMessage="Impossible to upload file " + fileNameLong;
					}else{
						//Unzip File
						ZipFile zipFile = new ZipFile(zip);
						Enumeration<?> entries = zipFile.entries();

						while(entries.hasMoreElements()) {
							ZipEntry entry = (ZipEntry)entries.nextElement();					
							if(entry.isDirectory()) {
								File dir = new File(currentDirPath + entry.getName());
								if(!dir.exists())
									dir.mkdirs();
							}
							else{
								script += strCommand + this.saveEntryZipFile(entry.getName(), currentDirPath, currentUrlPath, typeStr, zipFile.getInputStream(entry));
							}
						}						
						//delete zip file
						zip.delete();
					}				
				}else{
					strCommand = "window.parent.frames['frmMultipleUploads'].";
					Iterator<?> it = fields.keySet().iterator();
					while(it.hasNext()){
						String key = (String)it.next();
						FileItem uplFile=(FileItem)fields.get(key);
						String fileNameLong=uplFile.getName();
						fileNameLong=fileNameLong.replace('\\','/');
						script += strCommand + this.saveFile(fileNameLong, currentDirPath, currentUrlPath, typeStr, uplFile);	
					}					
				}
			}catch (Throwable t) {
				
				if(debug) System.out.println("Sono dentro il catch");
				
				t.printStackTrace();
				retVal="203";
				script = strCommand + "OnUploadCompleted("+retVal+",'"+fileUrl+"','"+newName+"','"+errorMessage+"');";
			}			
		}

		out.println("<script type=\"text/javascript\">");
		out.println(script);
		out.println("</script>");
		out.flush();
		out.close();

		if (debug) System.out.println("--- END DOPOST ---");	
	}


	private String saveFile(String fileNameLong, String currentDirPath,  String currentPath,  String typeStr, FileItem uplFile){
		String[] pathParts=fileNameLong.split("/");
		String fileName=pathParts[pathParts.length-1];

		String nameWithoutExt=getNameWithoutExtension(fileName);
		String ext=getExtension(fileName);
		String newName="";
		String fileUrl="";
		String retVal="0";
		String errorMessage="";
		if (debug)  
			System.out.println("File: " + currentDirPath + " "+ fileName + " " + currentPath);
		File pathToSave=new File(currentDirPath,fileName);
		if(extIsAllowed(typeStr,ext)) {
			int counter=1;
			while(pathToSave.exists()){
				newName=nameWithoutExt+"("+counter+")"+"."+ext;
				fileUrl=currentPath + newName;
				retVal="201";
				pathToSave=new File(currentDirPath,newName);
				counter++;
			}
			try {
				uplFile.write(pathToSave);
			} catch (Exception e) {
				e.printStackTrace();
				retVal="203";
				errorMessage="Impossible to upload file " + fileNameLong;
			}
		}	
		else {
			retVal="202";
			errorMessage="Invalid file type: " + fileNameLong;
			if (debug)  
				System.out.println("Invalid file type: " + ext);	
		}
		return "OnUploadCompleted("+retVal+",'"+fileUrl+"','"+newName+"','"+errorMessage+"');";
	}

	private String saveEntryZipFile(String fileNameLong, String currentDirPath,  String currentPath,  String typeStr, InputStream uplFile){
		String[] pathParts=fileNameLong.split("/");
		String pathRel="";
		if(fileNameLong.lastIndexOf("/")>0){
			pathRel= File.separator + fileNameLong.substring(0,fileNameLong.lastIndexOf("/"));
		}
		String fileName=pathParts[pathParts.length-1];		
		String nameWithoutExt=getNameWithoutExtension(fileName);
		String ext=getExtension(fileName);
		String newName="";
		String fileUrl="";
		String retVal="0";
		String errorMessage="";
		String cPath = currentDirPath + pathRel;
		String cUrl = currentPath + pathRel;
		File pathToSave=new File(cPath,fileName);
		if(extIsAllowed(typeStr,ext)) {
			int counter=1;
			while(pathToSave.exists()){
				newName=nameWithoutExt+"("+counter+")"+"."+ext;
				fileUrl= cUrl + newName;
				retVal="201";
				pathToSave=new File(cPath,newName);
				counter++;
			}
			try {
				BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(pathToSave));
				this.copyInputStream(uplFile, out);			
			} catch (Exception e) {
				e.printStackTrace();
				retVal="203";
				errorMessage="Impossible to upload file " + fileNameLong;
			}
		}	
		else {
			retVal="202";
			errorMessage="Invalid file type: " + fileNameLong;
			if (debug)  
				System.out.println("Invalid file type: " + ext);	
		}
		return "OnUploadCompleted("+retVal+",'"+fileUrl+"','"+newName+"','"+errorMessage+"');";
	}


	private File saveZipFile(String fileNameLong, String currentDirPath,  String currentPath,  String typeStr, FileItem uplFile){
		String[] pathParts=fileNameLong.split("/");
		String fileName=pathParts[pathParts.length-1];		
		String nameWithoutExt=getNameWithoutExtension(fileName);
		String ext=getExtension(fileName);
		String newName="";
		File pathToSave=new File(currentDirPath,"." + fileName);
		int counter=1;
		while(pathToSave.exists()){
			newName="." + nameWithoutExt+"("+counter+")"+"."+ext;
			pathToSave=new File(currentDirPath,newName);
			counter++;
		}
		try {
			uplFile.write(pathToSave);
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return pathToSave;		
	}


	private void setCreateFolderResponse(String retValue,Node root,Document doc) {
		Element myEl=doc.createElement("Error");
		myEl.setAttribute("number",retValue);
		root.appendChild(myEl);
	}

	@SuppressWarnings("unused")
	private void setConfigErrorResponse(String retValue,Node root,Document doc) {
		Element myEl=doc.createElement("Error");
		myEl.setAttribute("number",retValue);
		root.appendChild(myEl);
	}

	private void getFolders(File dir,Node root,Document doc) {
		Element folders=doc.createElement("Folders");
		root.appendChild(folders);
		File[] fileList=dir.listFiles();
		for(int i=0;i<fileList.length;++i) {
			if(fileList[i].isDirectory() && !fileList[i].isHidden()){
				Element myEl=doc.createElement("Folder");
				myEl.setAttribute("name",fileList[i].getName());
				folders.appendChild(myEl);
			}
		}		
	}

	private void deleteFile(File dir,Node root,Document doc) {
		if(dir.isDirectory()){
			deleteFolder(dir);
		}
		dir.delete();
	}

	//delete directory and files
	private void deleteFolder(File dir) {
		for(File file: dir.listFiles()){
			if(file.isDirectory()){
				deleteFolder(file);
			}
			file.delete();
		}
	}

	private void getFiles(File dir,Node root,Document doc) {
		Element files=doc.createElement("Files");
		root.appendChild(files);
		File[] fileList=dir.listFiles();
		for(int i=0;i<fileList.length;++i) {
			if(fileList[i].isFile() && !fileList[i].isHidden()){
				Element myEl=doc.createElement("File");
				myEl.setAttribute("name",fileList[i].getName());
				myEl.setAttribute("size",""+fileList[i].length()/1024);
				files.appendChild(myEl);
			}
		}	
	}	

	private Node CreateCommonXml(Document doc,String commandStr, String currentPath, String currentUrl ) {

		Element root=doc.createElement("Connector");
		doc.appendChild(root);
		root.setAttribute("command",commandStr);

		Element myEl=doc.createElement("CurrentFolder");
		myEl.setAttribute("path",currentPath);
		myEl.setAttribute("url",currentUrl);
		root.appendChild(myEl);

		return root;

	}

	/*
	 * This method was fixed after Kris Barnhoorn (kurioskronic) submitted SF bug #991489
	 */
	private static String getNameWithoutExtension(String fileName) {
		if(fileName.lastIndexOf(".") >= 0)
			return fileName.substring(0, fileName.lastIndexOf("."));
		return fileName;
	}

	/*
	 * This method was fixed after Kris Barnhoorn (kurioskronic) submitted SF bug #991489
	 */
	private String getExtension(String fileName) {
		return fileName.substring(fileName.lastIndexOf(".")+1);
	}

	/**
	 * Helper function to convert the configuration string to an ArrayList.
	 */

	@SuppressWarnings("unused")
	private ArrayList<String> stringToArrayList(String str) {

		if(debug) System.out.println(str);
		String[] strArr=str.split("\\|");

		ArrayList<String> tmp=new ArrayList<String>();
		if(str.length()>0) {
			for(int i=0;i<strArr.length;++i) {
				if(debug) System.out.println(i +" - "+strArr[i]);
				tmp.add(strArr[i].toLowerCase());
			}
		}
		return tmp;
	}


	/**
	 * Helper function to verify if a file extension is allowed or not allowed.
	 */	 
	private boolean extIsAllowed(String fileType, String ext) {
		return true;
		/*ext=ext.toLowerCase();

	 		ArrayList allowList=(ArrayList)allowedExtensions.get(fileType);
	 		ArrayList denyList=(ArrayList)deniedExtensions.get(fileType);

	 		if(allowList.size()==0)
	 			if(denyList.contains(ext))
	 				return false;
	 			else
	 				return true;

	 		if(denyList.size()==0)
	 			if(allowList.contains(ext))
	 				return true;
	 			else
	 				return false;

	 		return false;*/
	}

	public static void copyDirectory(File sourceDir, File destDir)
	throws IOException{
		if(!destDir.exists()){
			destDir.mkdirs();
		}

		File[] children = sourceDir.listFiles();
		for(File sourceChild : children){
			String name = sourceChild.getName();
			File destChild = new File(destDir, name);
			if(sourceChild.isDirectory()){
				copyDirectory(sourceChild, destChild);
			}
			else{
				copyFile(sourceChild, destChild);
			}
		}
	}


	public static void copyFile(File source, File dest) throws IOException{
		if(!dest.exists()){
			dest.createNewFile();
		}
		InputStream in = null;
		OutputStream out = null;
		try{
			in = new FileInputStream(source);
			out = new FileOutputStream(dest);
			byte[] buf = new byte[1024];
			int len;
			while((len = in.read(buf)) > 0){
				out.write(buf, 0, len);
			}
		}
		finally{
			in.close();
			out.close();
		}
	}	 

	public void unZipFile(String name){
		try {
			ZipFile zipFile = new ZipFile(name);

			Enumeration<?> entries = zipFile.entries();

			while(entries.hasMoreElements()) {
				ZipEntry entry = (ZipEntry)entries.nextElement();

				if(entry.isDirectory()) {
					// Assume directories are stored parents first then children.
					System.err.println("Extracting directory: " + entry.getName());
					// This is not robust, just for demonstration purposes.
					(new File(entry.getName())).mkdir();
					continue;
				}

				System.err.println("Extracting file: " + entry.getName());
				copyInputStream(zipFile.getInputStream(entry),
						new BufferedOutputStream(new FileOutputStream(entry.getName())));
			}
			zipFile.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}		 
	}


	public final void copyInputStream(InputStream in, OutputStream out)
	throws IOException
	{
		byte[] buffer = new byte[1024];
		int len;

		while((len = in.read(buffer)) >= 0)
			out.write(buffer, 0, len);

		in.close();
		out.close();
	}



}

