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
						temp.createNewFile();
					}
					catch (IOException e)
					{
						System.out.println("创建文件失败，请输入正确的参数");
						return false;
					}
				}
				return true;
			}
			catch (Exception e)
			{
				System.out.println("创建文件失败，请输入正确的参数");
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
							System.out.println("删除失败");
					}
					else
					{
						if (!temp.delete())
							System.out.println("删除失败");
					}
				}
				else
				{
					System.out.println("文件或目录不存在");
					return false;
				}
				return true;
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
						System.out.println("重命名失败");
						return false;
					}
					else
					{
						if (oldfile.renameTo(newfile))
							return true;
						else
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


	public boolean copy_file(String src, String dest)
	{
		synchronized (lock)
		{
			try
			{
				FileInputStream in = null;
				try
				{
					in = new FileInputStream(src);
				}
				catch (FileNotFoundException e)
				{
					e.printStackTrace();
					return false;
				}
				File file = new File(dest);
				if (!file.exists())
				{
					try
					{
						file.createNewFile();
					}
					catch (IOException e)
					{
						e.printStackTrace();
						return false;
					}
				}
				FileOutputStream out = null;
				try
				{
					out = new FileOutputStream(file);
				}
				catch (FileNotFoundException e)
				{
					e.printStackTrace();
				}
				int c;
				byte buffer[] = new byte[1024];
				try
				{
					while ((c = in.read(buffer)) != -1)
					{
						for (int i = 0; i < c; i++)
							out.write(buffer[i]);
					}
				}
				catch (IOException e)
				{
					e.printStackTrace();
					return false;
				}
				try
				{
					in.close();
					out.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
					return false;
				}
				return true;
			}
			catch (Exception e)
			{
				System.out.println("复制文件失败，请输入正确的参数");
				return false;
			}
		}
	}

	public boolean move_file(String src, String dest)
	{
		synchronized (lock)
		{
			if (copy_file(src, dest))
			{
				if (delete_file(src))
					return true;
				else
					return false;
			}
			else
				return false;
		}
	}

	public String get_file_name(String str)
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

	public long get_last_modified(String str)
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

	public long get_size(String str)
	{
		try
		{
			File temp_file = new File(str);
			if (temp_file.isDirectory())
			{
				long size = 0;
				for (File i : temp_file.listFiles())
				{
					size += get_size(i.getAbsolutePath());
				}
				return size;
			}
			else
				return temp_file.length();

		}
		catch (Exception e)
		{
			System.out.println("获取文件大小失败，请输入正确的参数");
			return -1;
		}
	}

}
