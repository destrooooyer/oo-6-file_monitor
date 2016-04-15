package oo;

import java.io.*;

/**
 * Created by DESTRooooYER on 2016/4/14.
 */
public class _file
{
	public boolean create_file(String str)
	{
		File temp = new File(str);
		if (temp.exists())
		{
			System.out.println(str + "已经存在了，创建失败");
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
				System.out.println(e.toString() + "\t" + str + "创建失败");
				return false;
			}
		}
		return true;
	}
	

	public boolean delete_file(String str)
	{
		File temp = new File(str);
		if (temp.exists())
		{
			if (temp.isDirectory())
			{
				for (File i : temp.listFiles())
				{
					if (!delete_file(i.getAbsolutePath()))
						return false;
				}
			}
			else
			{
				temp.delete();
			}
		}
		else
		{
			System.out.println(str + "不存在，删除失败");
			return false;
		}
		return true;
	}

	public boolean rename_file(String src, String dest)
	{
		if (!src.equals(dest))
		{
			File oldfile = new File(src);
			File newfile = new File(dest);
			if (newfile.exists())
			{
				System.out.println(dest + "已经存在！");
				return false;
			}
			else
			{
				oldfile.renameTo(newfile);
			}
		}
		return true;
	}


	public boolean copy_file(String src, String dest)
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

	public boolean move_file(String src, String dest)
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

	public String get_file_name(String str)
	{
		File temp = new File(str);
		return temp.getAbsolutePath();
	}

	public long get_last_modified(String str)
	{
		File temp = new File(str);
		return temp.lastModified();
	}

	public long get_size(String str)
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

}
