package oo;

import java.io.File;
import java.util.Scanner;
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
							////////////////////////////////////////////////////////////////////////////////////////////
							////////////////////////////////////////////////////////////////////////////////////////////
							//创建filemonitor
							////////////////////////////////////////////////////////////////////////////////////////////
							////////////////////////////////////////////////////////////////////////////////////////////
						}
					}

				}
				else
				{
					System.out.println("输入不合法");
				}
			}
		}

//		Filemonitor fm=new Filemonitor(Trigger_kinds.renamed,"C:\\Users\\DESTR\\Desktop\\test");
//		Filemonitor fm = new Filemonitor(Trigger_kinds.path_changed, "C:\\Users\\DESTR\\Desktop\\test\\123\\a.txt", sm);
//		Thread T=new Thread(fm);
//		T.run();
//		try
//		{
//			T.join();
//		}
//		catch (InterruptedException e)
//		{
//			e.printStackTrace();
//		}
//		fm.test();
	}
}
