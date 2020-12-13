package com.lys.utils;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import com.lys.config.Config;
import com.lys.config.Table;

public class FsUtils
{
	private static final Table T = Table.instance;

	public static long getFileSize(File file)
	{
		if (file.exists())
		{
			try
			{
				BasicFileAttributeView basicView = Files.getFileAttributeView(Paths.get(file.getAbsolutePath()), BasicFileAttributeView.class);
				BasicFileAttributes basicAttribs = basicView.readAttributes();
				return basicAttribs.size();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return 0;
	}

	public static void createDir(File file)
	{
		if (!file.exists())
		{
			File parent = file.getParentFile();
			if (!parent.exists())
			{
				createDir(parent);
			}
			file.mkdir();
		}
	}

	public static void delete(File file)
	{
		if (file.exists())
		{
			if (file.isFile())
			{
				file.delete();
			}
			else
			{
				file.listFiles(new FileFilter()
				{
					@Override
					public boolean accept(File pathname)
					{
						delete(pathname);
						return false;
					}
				});
				file.delete();
			}
		}
	}

	public static byte[] readBytes(File file)
	{
		if (file.exists() && file.isFile())
		{
			byte[] buffer = null;
			try
			{
				FileInputStream fis = new FileInputStream(file);
				buffer = new byte[fis.available()];
				fis.read(buffer);
				fis.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			return buffer;
		}
		else
		{
			return null;
		}
	}

	public static String readText(File file)
	{
		byte[] buffer = readBytes(file);
		if (buffer != null)
			return new String(buffer, 0, buffer.length, Charset.forName("UTF-8"));
		else
			return null;
	}

	public static void writeBytes(File file, InputStream is)
	{
		createDir(file.getParentFile());
		try
		{
			FileOutputStream fos = new FileOutputStream(file);
			byte[] buffer = new byte[16 * 1024];
			int hasRead = 0;
			while ((hasRead = is.read(buffer)) > 0)
			{
				fos.write(buffer, 0, hasRead);
			}
			fos.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static void writeBytes(File file, byte[] bytes, int off, int len)
	{
		try
		{
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(bytes, off, len);
			fos.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static void writeBytes(File file, byte[] bytes)
	{
		try
		{
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(bytes, 0, bytes.length);
			fos.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static void writeText(File file, String text)
	{
		writeBytes(file, text.getBytes(Charset.forName("UTF-8")));
	}

	public static void appendBytes(File file, byte[] bytes)
	{
		try
		{
			FileOutputStream fos = new FileOutputStream(file, true);
			fos.write(bytes, 0, bytes.length);
			fos.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static void appendText(File file, String text)
	{
		appendBytes(file, text.getBytes(Charset.forName("UTF-8")));
	}

	public static void deleteDirectoryIfEmpty(File file)
	{
		if (file.getAbsolutePath().equals(Config.fileDir.getAbsolutePath()))
			return;
		if (file.exists())
		{
			if (file.isDirectory())
			{
				String[] children = file.list();
				if (children == null || children.length == 0)
				{
					file.delete();
					deleteDirectoryIfEmpty(file.getParentFile());
				}
			}
		}
	}

	private static void searchFilesAdd(final List<File> files, File file, String endStrGroup)
	{
		if (TextUtils.isEmpty(endStrGroup))
		{
			files.add(file);
		}
		else
		{
			String[] endStrArray = endStrGroup.split(";");
			for (String endStr : endStrArray)
			{
				if (file.getName().toLowerCase().endsWith(endStr.toLowerCase()))
				{
					files.add(file);
					break;
				}
			}
		}
	}

	private static void searchFilesImpl(final List<File> files, File dir, String endStrGroup)
	{
		dir.listFiles(new FileFilter()
		{
			@Override
			public boolean accept(File file)
			{
				if (file.isFile())
				{
					searchFilesAdd(files, file, endStrGroup);
				}
				else
				{
					searchFilesImpl(files, file, endStrGroup);
				}
				return false;
			}
		});
	}

	public static List<File> searchFiles(File file, String endStrGroup)
	{
		List<File> files = new ArrayList<>();
		if (file.exists())
		{
			if (file.isFile())
			{
				searchFilesAdd(files, file, endStrGroup);
			}
			else
			{
				searchFilesImpl(files, file, endStrGroup);
			}
		}
		return files;
	}

	public static List<File> searchFiles(File file)
	{
		return searchFiles(file, null);
	}

	public static void deleteFile(File src, boolean withMd5)
	{
		if (src.exists() && src.isFile())
		{
			delete(src);
//			if (withMd5)
//			{
//				String srcPath = Config.getPath(src);
//				DBHelper.delete(T.md5, T.md5.path, srcPath);
//			}
		}
	}

	// 删除文件或目录
	public static void deletePath(File srcFile, boolean withMd5)
	{
		if (srcFile.exists())
		{
			if (srcFile.isFile())
			{
				deleteFile(srcFile, withMd5);
			}
			else
			{
				for (File srcF : searchFiles(srcFile))
				{
					deleteFile(srcF, withMd5);
				}
			}
		}
	}

	public static void copyFile(File src, File dst, boolean withMd5)
	{
		if (src.exists() && src.isFile())
		{
			createDir(dst.getParentFile());
			try
			{
				FileInputStream fis = new FileInputStream(src);
				FileOutputStream fos = new FileOutputStream(dst);
				byte[] buffer = new byte[16 * 1024];
				int hasRead = 0;
				while ((hasRead = fis.read(buffer)) > 0)
				{
					fos.write(buffer, 0, hasRead);
				}
				fis.close();
				fos.close();

//				if (withMd5)
//				{
//					String srcPath = Config.getPath(src);
//					String dstPath = Config.getPath(dst);
//
//					final SFilePath srcFilePath = new SFilePath();
//
//					DBHelper.exeSql(String.format("select %s from %s where %s = '%s';", //
//							T.md5.md5, //
//							T.md5, //
//							T.md5.path, //
//							srcPath), new OnCallback()
//							{
//								@Override
//								public void onResult(ResultSet rs) throws SQLException
//								{
//									if (rs.next())
//									{
//										srcFilePath.md5 = rs.getString(T.md5.md5);
//									}
//								}
//							});
//
//					if (!TextUtils.isEmpty(srcFilePath.md5))
//					{
//						if (DBHelper.hasRecord(T.md5, T.md5.path, dstPath))
//						{
//							DBHelper.update(T.md5, DBHelper.set(T.md5.md5, srcFilePath.md5), T.md5.path, dstPath);
//						}
//						else
//						{
//							DBHelper.insert(T.md5, //
//									T.md5.md5, srcFilePath.md5, //
//									T.md5.path, dstPath);
//						}
//					}
//				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	// 拷贝文件或目录
	public static void copyPath(File srcFile, File dstFile, boolean withMd5)
	{
		if (srcFile.exists())
		{
			if (srcFile.isFile())
			{
				copyFile(srcFile, dstFile, withMd5);
			}
			else
			{
				int len = srcFile.getAbsolutePath().length();
				for (File srcF : searchFiles(srcFile))
				{
					File dstF = new File(dstFile.getAbsolutePath() + srcF.getAbsolutePath().substring(len));
					copyFile(srcF, dstF, withMd5);
				}
			}
		}
	}
}
