package oo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

/**
 * Created by DESTR on 2016/4/14.
 */
public class Summary implements Runnable
{
	private int run_flag;
	private LinkedList<String> sm;
	FileWriter fout;
	private int renamed_count;
	private int modified_count;
	private int path_changed_count;
	private int size_changed_count;

	public Summary()
	{
		this.modified_count = 0;
		this.path_changed_count = 0;
		this.renamed_count = 0;
		this.size_changed_count = 0;

		this.sm = new LinkedList<String>();
		this.run_flag = 1;
		File file = new File("summary.txt");
		if (!file.exists())
		{
			try
			{
				file.createNewFile();
			}
			catch (IOException e)
			{
				System.out.println("summary.txt创建失败");
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

	public void Renamed_count_plus()
	{
		synchronized (this)
		{
			this.renamed_count++;
			String temp_str=new String();
			temp_str+="renamed count:\t";
			temp_str+=String.valueOf(this.renamed_count);
			this.sm.addLast(temp_str);
		}
	}

	public void Modified_count_plus()
	{
		synchronized (this)
		{
			this.modified_count++;
			String temp_str=new String();
			temp_str+="modified count:\t";
			temp_str+=String.valueOf(this.modified_count);
			this.sm.addLast(temp_str);
		}
	}

	public void Path_changed_count_plus()
	{
		synchronized (this)
		{
			this.path_changed_count++;
			String temp_str=new String();
			temp_str+="path-changed count:\t";
			temp_str+=String.valueOf(this.path_changed_count);
			this.sm.addLast(temp_str);
		}
	}

	public void Size_changed_count_plus()
	{
		synchronized (this)
		{
			this.size_changed_count++;
			String temp_str=new String();
			temp_str+="size-changed count:\t";
			temp_str+=String.valueOf(this.size_changed_count);
			this.sm.addLast(temp_str);
		}
	}

	public void go_die()
	{
		this.run_flag = 0;
	}

	@Override
	public void run()
	{
		while (run_flag == 1)
		{

			while (sm.size() > 0)
			{
				try
				{
					fout.write(sm.removeFirst() + "\n");
					fout.flush();
				}
				catch (IOException e)
				{
					System.out.println("summary.txt写入失败");
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
