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
	private Detail dt;
	private int run_flag;

	public Filemonitor2(int x, String target, Summary sm, Detail dt)
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
		this.dt = dt;

		this.run_flag = 1;

		this.action = Action_kind.record_detail;

	}

	public long update(File file)
	{

		if (file.isDirectory())
		{
			long temp_size = 0;
			for (File i : file.listFiles())
			{
				temp_size += update(i);
			}
			fmap_pres.put(file, new Pair<Long, Long>(file.lastModified(), temp_size));
			return temp_size;
		}
		else
		{
			fmap_pres.put(file, new Pair<Long, Long>(file.lastModified(), file.length()));
			return file.length();
		}
	}

	public void init()
	{
		System.out.println(root.getAbsolutePath());
		update(root);
		this.fmap_prev.putAll(fmap_pres);
	}

	@Override
	public void run()
	{

	}

	public void test()
	{
		init();
		System.out.println("ready");
		while (this.run_flag == 1)
		{
			//更新present
			fmap_pres.clear();
			update(root);

			//System.out.println(fmap_pres.size()+"\t" +fmap_prev.size());

			////////////////检查previous和present的变化//////////////////////////////////////////////////////////////////
			//--------------renamed-------------------------------------------------------------------------------------
			if (this.trigger == Trigger_kinds.renamed)
			{
//				System.out.println(target.getAbsolutePath());
				if (!fmap_pres.containsKey(target))
				{
					String target_parent = target.getParent();
					long target_lastmodified = fmap_prev.get(target).getKey();
					long target_size = fmap_prev.get(target).getValue();

					int find_new = 0;
					for (File i : fmap_pres.keySet())
					{
						if (i.getParent().equals(target_parent) &&               //所处路径相同
								i.lastModified() == target_lastmodified &&       //最后修改时间相同
								i.length() == target_size &&                     //大小相同
								i.isDirectory() == false &&                      //不是文件夹
								!i.getName().equals(target.getName()) &&         //文件名不同
								!fmap_prev.containsKey(i))                       //previous不包含
						{
							//判断为renamed
							find_new = 1;
							switch (this.action)
							{
								case Action_kind.record_detail:
								{
									String str_detail=new String();
									str_detail+="renamed:\n";
									str_detail+="路径变化:\t";
									str_detail+=target.getAbsolutePath()+"\t=>\t"+i.getAbsolutePath()+"\n";
									str_detail+="修改时间变化:\t";
									str_detail+=fmap_prev.get(target).getKey().toString()+"\t=>\t"+i.lastModified()+"\n";
									str_detail+="文件大小变化:\t";
									str_detail+=fmap_prev.get(target).getValue().toString()+"\t=>\t"+i.length()+"\n";
									str_detail+="//////////////////////////////////////////////////////////////////////";
									System.out.println(str_detail);
									dt.push_back(str_detail);
									target = i;
									break;
								}
								case Action_kind.record_summary:
								{
									sm.Renamed_count_plus();
									target = i;
									break;
								}
								case Action_kind.recover:
								{
									try
									{
										Thread.sleep(10);
									}
									catch (InterruptedException e)
									{
										e.printStackTrace();
									}
									_file temp_file = new _file();
									temp_file.rename_file(i.getAbsolutePath(), target.getAbsolutePath());
									break;
								}
								default:
							}
						}
					}
					if (find_new == 0)
					{
						System.out.println("监视目标" + target.getAbsolutePath() + "丢失，该线程结束");
						return;
					}
				}

			}
			else if (this.trigger == Trigger_kinds.modified)
			{
				if(target_is_file)
				{

				}
				else
				{

				}
			}
			else if (this.trigger == Trigger_kinds.path_changed)
			{

			}
			else if (this.trigger == Trigger_kinds.size_changed)
			{

			}
			else
			{
				System.out.println("未知错误，程序将结束");
				System.exit(0);
			}

			//将previous弄成present
			fmap_prev.clear();
			fmap_prev.putAll(fmap_pres);


			try
			{
				Thread.sleep(10);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}


}
