package oo;

/**
 * Created by DESTR on 2016/4/13.
 */
public class Main
{
	public static void main(String argv[])
	{
		Filemonitor fm=new Filemonitor("C:\\Users\\DESTR\\Desktop\\test\\");
		Thread T=new Thread(fm);
		T.run();
		try
		{
			T.join();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
}
