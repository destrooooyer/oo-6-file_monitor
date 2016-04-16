package oo;

import java.io.*;

/**
 * Created by DESTRooooYER on 2016/4/14.
 */
public class _file
{
	public static final Object lock = new Object();


	public boolean create_file(String str)
	{
		synchronized (lock)
		{
			try
			{
				File temp = new File(str);
				if (temp.exists() && temp.isFile())
				{
					System.out.println("文件已经存在，创建失败");
					return false;
				}
				else
				{
					try
					{
						if (temp.createNewFile())
							return true;
						else
						{

							System.out.println("创建文件失败");
							return false;
						}
					}
					catch (IOException e)
					{
						System.out.println("创建文件失败，请输入正确的参数");
						return false;
					}
				}
			}
			catch (Exception e)
			{
				System.out.println("创建文件失败，请输入正确的参数");
				return false;
			}
		}
	}

	public boolean create_dir(String str)
	{
		synchronized (lock)
		{
			try
			{
				File temp = new File(str);
				if (temp.exists() && temp.isDirectory())
				{
					System.out.println("文件夹已经存在，创建失败");
					return false;
				}
				else
				{
					if (temp.mkdir())
					{
						return true;
					}
					else
					{
						System.out.println("创建文件夹失败");
						return false;
					}
				}
			}
			catch (Exception e)
			{
				System.out.println("创建文件夹失败，请输入正确的参数");
				return false;
			}
		}
	}

	public boolean delete_file(String str)
	{
		synchronized (lock)
		{
			try
			{
				File temp = new File(str);
				if (temp.exists())
				{
					if (temp.isDirectory())
					{
						for (File i : temp.listFiles())
						{
							if (!delete_file(i.getAbsolutePath()))
							{
								System.out.println("删除失败");
								return false;
							}
						}
						if (!temp.delete())
						{
							System.out.println("删除失败");
							return false;
						}
						return true;
					}
					else
					{
						if (!temp.delete())
							System.out.println("删除失败");
						return false;
					}
				}
				else
				{
					System.out.println("文件或目录不存在");
					return false;
				}
			}
			catch (Exception e)
			{
				System.out.println("删除文件失败，请输入正确的参数");
				return false;
			}
		}
	}

	public boolean rename_file(String src, String dest)
	{
		synchronized (lock)
		{
			try
			{
				if (!src.equals(dest))
				{
					File oldfile = new File(src);
					File newfile = new File(dest);
					if (newfile.exists())
					{
						System.out.println("重命名失败,目标已经存在");
						return false;
					}
					else if (oldfile.exists() && oldfile.isFile())
					{
						if (oldfile.renameTo(newfile))
							return true;
						else
						{
							System.out.println("重命名失败");
							return false;
						}
					}
					else
					{
						System.out.println("重命名失败，可能重命名对象不存在或是文件夹");
						return false;
					}
				}
				return true;
			}
			catch (Exception e)
			{
				System.out.println("重命名文件失败，请输入正确的参数");
				return false;
			}
		}
	}


//	public boolean copy_file(String src, String dest)
//	{
//		synchronized (lock)
//		{
//			try
//			{
//				FileInputStream in = null;
//				File temp_file=new File(src);
//				if(temp_file.exists()&&temp_file.isFile())
//				{
//
//				}
//				else
//				{
//					System.out.println("复制文件失败");
//					return false;
//				}
//				try
//				{
//					in = new FileInputStream(src);
//				}
//				catch (FileNotFoundException e)
//				{
//					e.printStackTrace();
//					return false;
//				}
//				File file = new File(dest);
//				if (!file.exists())
//				{
//					try
//					{
//						file.createNewFile();
//					}
//					catch (IOException e)
//					{
//						e.printStackTrace();
//						return false;
//					}
//				}
//				FileOutputStream out = null;
//				try
//				{
//					out = new FileOutputStream(file);
//				}
//				catch (FileNotFoundException e)
//				{
//					e.printStackTrace();
//				}
//				int c;
//				byte buffer[] = new byte[1024];
//				try
//				{
//					while ((c = in.read(buffer)) != -1)
//					{
//						for (int i = 0; i < c; i++)
//							out.write(buffer[i]);
//					}
//				}
//				catch (IOException e)
//				{
//					e.printStackTrace();
//					return false;
//				}
//				try
//				{
//					in.close();
//					out.close();
//				}
//				catch (IOException e)
//				{
//					e.printStackTrace();
//					return false;
//				}
//				return true;
//			}
//			catch (Exception e)
//			{
//				System.out.println("复制文件失败，请输入正确的参数");
//				return false;
//			}
//		}
//	}

	public boolean move_file(String src, String dest)
	{
		return (rename_file(src, dest));
	}

	public String get_file_name(String str)
	{
		synchronized (lock)
		{
			try
			{
				File temp = new File(str);
				return temp.getAbsolutePath();
			}
			catch (Exception e)
			{
				System.out.println("获取文件名，请输入正确的参数");
				return null;
			}
		}
	}
	public long get_last_modified(String str)
	{
		synchronized (lock)
		{
			try
			{
				File temp = new File(str);
				return temp.lastModified();
			}
			catch (Exception e)
			{
				System.out.println("获取最后修改时间，请输入正确的参数");
				return -1;
			}
		}
	}

	public long get_size(String str)
	{
		synchronized (lock)
		{
			try
			{
				File temp_file = new File(str);
				if (temp_file.exists())
				{
					if (temp_file.isDirectory())
					{
						long size = 0;
						for (File i : temp_file.listFiles())
						{
							if (i.isFile())
								size += i.length();
						}
						return size;
					}
					else
						return temp_file.length();
				}
				else
				{
					System.out.println("文件不存在");
					return -1;
				}

			}
			catch (Exception e)
			{
				System.out.println("获取文件大小失败，请输入正确的参数");
				return -1;
			}
		}
	}

	public boolean file_append(String str)
	{
		synchronized (lock)
		{
			try
			{
				File temp_file = new File(str);
				if (temp_file.exists())
				{
					FileWriter fw = new FileWriter(temp_file, true);
					fw.append("wtf");
					fw.close();
				}
				else
				{
					System.out.println("文件不存在，写入失败");
					return false;
				}
				return true;
			}
			catch (Exception e)
			{
				System.out.println("文件写入失败，请输入正确的参数");
				return false;
			}
		}
	}

}
