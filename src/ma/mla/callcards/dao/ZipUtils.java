package ma.mla.callcards.dao;

import java.io.File;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

public class ZipUtils {

	static public void zipFolder(File srcFolder, File destZipFile)
			throws Exception {
		if (destZipFile.exists()) {
			destZipFile.delete();
		}
		ZipFile zip = new ZipFile(destZipFile);
		ZipParameters parameters = new ZipParameters();
		parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
		parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
		for (File file : srcFolder.listFiles()) {
			zip.addFile(file, parameters);
		}
	}

	static public void unzipFolder(String srcZipFile, String destFolder)
			throws ZipException {
		ZipFile zip = new ZipFile(srcZipFile);
		zip.extractAll(destFolder);
	}
}
