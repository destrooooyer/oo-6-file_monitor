package oo;

import javafx.util.Pair;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by DESTRooooYER on 2016/4/14.
 */
public class Filemonitor2 implements Runnable
{
	private File root;
	private File target;
	private Map<File, Pair<Long, Long>> fmap_prev;
	private Map<File, Pair<Long, Long>> fmap_pres;
	private int trigger;
	private boolean target_is_file;
	private int action;
	private Summary sm;
	private int run_flag;

	public Filemonitor2(int x, String target, Summary sm)
	{
		//用来记录工作区下所有文件和其修改时间以及大小
		fmap_pres = new HashMap<File, Pair<Long, Long>>();
		fmap_prev = new HashMap<File, Pair<Long, Long>>();

		this.trigger = x;
		this.target = new File(target);
		this.target_is_file = this.target.isFile();

		if (target_is_file)
			this.root = this.target.getParentFile();
		else
			this.root = this.target;

		this.sm = sm;

		this.run_flag=1;

		this.action = Action_kind.record_summary;

	}

	public long update(File file)
	{
		if(file.isDirectory())
		{
			long temp_size=0;
			for (File i : file.listFiles())
			{
				if(i.isDirectory())
					temp_size+=update(i);
				else
					temp_size+=i.length();
			}
			fmap_pres.put(file,new Pair<Long,Long>(file.lastModified(),temp_size));
			return temp_size;
		}
		else
		{
			fmap_pres.put(file,new Pair<Long,Long>(file.lastModified(),file.length()));
			return file.length();
		}
	}

	public void init()
	{
		update(root);
		this.fmap_prev.putAll(fmap_pres);
	}

	@Override
	public void run()
	{

	}

	public void test()
	{
		while(this.run_flag==1)
		{

		}
	}


}
