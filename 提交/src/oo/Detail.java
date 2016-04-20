package oo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

/**
 * Created by DESTR on 2016/4/14.
 */
public class Detail implements Runnable
{
	private LinkedList<String> dt;
	private final Object lock = new Object();
	private int run_flag;
	FileWriter fout;

	public Detail()
	{
		this.run_flag = 1;
		this.dt = new LinkedList<String>();

		File file = new File("detail.txt");
		if (!file.exists())
		{
			try
			{
				file.createNewFile();
			}
			catch (IOException e)
			{
				System.out.println("detail.txt创建失败");
				System.exit(0);
			}
		}
		try
		{
			this.fout = new FileWriter(file);
		}
		catch (IOException e)
		{
			System.out.println(e.toString());
			System.exit(0);
		}
	}

	public void push_back(String str)
	{
		synchronized (lock)
		{
			this.dt.addLast(str);
		}
	}

	@Override
	public void run()
	{
		while (true)
		{
			while (run_flag == 1)
			{

				while (dt.size() > 0)
				{
					try
					{
						fout.write(dt.removeFirst() + "\r\n");
						fout.flush();
					}
					catch (IOException e)
					{
						System.out.println("detail.txt写入失败");
						System.exit(0);
					}
				}

				try
				{
					Thread.sleep(100);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			try
			{
				fout.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}
