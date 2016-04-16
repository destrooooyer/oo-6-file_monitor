package oo;

import java.io.File;
import java.util.Scanner;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by DESTR on 2016/4/13.
 */
public class Main
{
	public static void main(String argv[])
	{

		Summary sm = new Summary();
		Thread T_summary = new Thread(sm);
		T_summary.start();

		Detail dt=new Detail();
		Thread T_detail=new Thread(dt);
		T_detail.start();

		Vector<Filemonitor2> fms=new Vector<Filemonitor2>();


		Pattern pt = Pattern.compile("IF\\s+\"(.*)\"\\s+(renamed|modified|path-changed|size-changed)\\s+THEN\\s+(record-summary|recover|record-detail)");
		String str_input;
		Scanner sc = new Scanner(System.in);
		while (sc.hasNextLine())
		{
			str_input = sc.nextLine();
			if (str_input.equals("run"))
				break;
			else
			{
				Matcher mt = pt.matcher(str_input);
				if (mt.find())
				{
					String path = mt.group(1);
					String trigger = mt.group(2);
					String action = mt.group(3);

					int trigger_kind = 0;
					int action_kind = 0;

					File file = new File(path);

					if (!file.exists())
						System.out.println("输入的文件或路径不存在");
					else
					{
						for (int i = 1; i < 5; i++)
						{
							if (trigger.equals(Trigger_kinds.trigger_kinds[i]))
							{
								trigger_kind = i;
								break;
							}
						}

						for (int i = 1; i < 4; i++)
						{
							if (action.equals(Action_kind.action_kinds[i]))
							{
								action_kind = i;
								break;
							}
						}

						if(trigger_kind==0)
						{
							System.out.println("输入的触发器不合法");
						}
						else if(action_kind==0)
						{
							System.out.println("输入的任务不合法");
						}
						else
						{
							fms.add(new Filemonitor2(trigger_kind,file.getAbsolutePath(),sm,dt,action_kind));
						}
					}

				}
				else
				{
					System.out.println("输入不合法");
				}
			}
		}

		Vector<Thread> T=new Vector<Thread>();
		for(int i=0;i<fms.size();i++)
		{
			T.add(new Thread(fms.get(i)));
			T.get(i).start();
		}

		try
		{
			Thread.sleep(2000);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}

		////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		//在此处启动测试线程
//		Test test=new Test();
//		Thread T_test=new Thread(test);
//		T_test.start();

		////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////


	}
}
