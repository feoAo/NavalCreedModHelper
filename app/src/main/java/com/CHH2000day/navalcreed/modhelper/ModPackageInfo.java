package com.CHH2000day.navalcreed.modhelper;
import java.io.*;
import android.graphics.*;
import org.json.*;
import com.CHH2000day.navalcreed.modhelper.ModPackageInfo.*;
import android.util.*;
import java.util.*;

public class ModPackageInfo
{
	//常量声明

	//软件版本
	public static final int PKGVER=Versions.VER_3;

	public static final String MODTYPE_CV="CaptainVoice";
	public static final String MODTYPE_SOUNDEFFECT_PRIM="SoundEffect_PRIM";
	public static final String MODTYPE_SOUNDEFFECT_SEC="SoundEffect_SEC";
	public static final String MODTYPE_SOUNDEFFECT="SoundEffect";
	public static final String MODTYPE_BGM="BackgroundMusic";
	public static final String MODTYPE_BACKGROUND="Background";
	public static final String MODTYPE_CREWPIC="CrewPic";
	public static final String MODTYPE_OTHER="Other";
	public static final String SUBTYPE_EMPTY="";
	public static final String SUB_MODTYPE_CV_CN="CV_CN";
	public static final String SUB_MODTYPE_CV_EN="CV_EN";
	public static final String SUB_MODTYPE_CV_JP_CV="CV_JP_CV";
	public static final String SUB_MODTYPE_CV_JP_BB="CV_JP_BB";
	public static final String SUB_MODTYPE_CV_JP_CA="CV_JP_CA";
	public static final String SUB_MODTYPE_CV_JP_DD="CV_JP_DD";
	private static ArrayList<String> abandoned_types;
	//mod信息相关
	private static final String KEY_MINSUPPORTVER="minSupportVer";
	private static final String KEY_TARGETVER="targetVer";
	private static final String KEY_MODNANE="name";
	private static final String KEY_MODAUTHOR="author";
	private static final String KEY_MODINFO="modInfo";
	private static final String KEY_MODTYPE="modType";
	private static final String KEY_HASPREVIEW="hasPreview";
	private static final String KEY_PREVIEW="preview";

	private String modName;
	private String modType;
	private String modAuthor;
	private String modInfo;
	private Bitmap modPreview;
	private int modTargetVer;


	static{
		abandoned_types=new ArrayList<String>();
		abandoned_types.add(MODTYPE_SOUNDEFFECT);
	}
	private ModPackageInfo ()
	{

	}

	public static boolean checkIsAbandoned(String modtype){
		return abandoned_types.contains(modtype);
	}
	
	private void setModName (String modName)
	{
		this.modName = modName;
	}

	public String getModName ()
	{
		return modName;
	}

	private void setModType (String modType)
	{
		this.modType = modType;
	}

	public String getModType ()
	{
		return modType;
	}

	private void setModAuthor (String modAuthor)
	{
		this.modAuthor = modAuthor;
	}

	public String getModAuthor ()
	{
		return modAuthor;
	}

	private void setModInfo (String modInfo)
	{
		this.modInfo = modInfo;
	}

	public String getModInfo ()
	{
		return modInfo;
	}

	private void setModPreview (Bitmap modPreview)
	{
		this.modPreview = modPreview;
	}

	public boolean hasPreview(){
		return (modPreview!=null);
	}
	public Bitmap getModPreview ()
	{
		return modPreview;
	}

	private void setModTargetVer (int modTargetVer)
	{
		this.modTargetVer = modTargetVer;
	}

	public int getModTargetVer ()
	{
		return modTargetVer;
	}
	public boolean hasAllFeature(){
		return (PKGVER>=modTargetVer);
	}
	public boolean isAbandoned(){
		return checkIsAbandoned(getModType());
	}









	//使用Factory模式构造该实例
	public static class Factory
	{
		public static ModPackageInfo createFromInputStream (InputStream in) throws IOException, JSONException, ModPackageInfo.IllegalModInfoException
		{
			if (in == null)
			{
				throw new NullPointerException ( "InputStream could not be null!" );
			}
			
			byte[] cache=Utils.readAllbytes(in);
			in.close();
			return createFromByteArray ( cache );
		}
		public static ModPackageInfo createFromByteArray (byte[]data) throws JSONException, ModPackageInfo.IllegalModInfoException
		{
			if (data == null)
			{
				throw new NullPointerException ( "Data could not be null" );
			}
			JSONObject jo=new JSONObject ( new String ( data ) );
			ModPackageInfo mpi=new ModPackageInfo ( );
			//检查最低兼容版本
			if (mpi.PKGVER < jo.getInt ( mpi.KEY_MINSUPPORTVER ))
			{
				throw new IllegalModInfoException ( new StringBuilder ( ).append ( "Installer version is too low.This mod package requires a minimum version of " )
												   .append ( " " )
												   .append ( jo.getInt ( mpi.KEY_MINSUPPORTVER ) )
												   .append ( "." )
												   .append ( "But mod has a installer version of " )
												   .append ( " " )
												   .append ( mpi.PKGVER )
												   .append ( "." )
												   .toString ( )
												   );
			}
			mpi.setModName(jo.getString(mpi.KEY_MODNANE));
			mpi.setModType(jo.getString(mpi.KEY_MODTYPE));
			mpi.setModAuthor(jo.getString(mpi.KEY_MODAUTHOR));
			mpi.setModInfo(jo.getString(mpi.KEY_MODINFO));
			mpi.setModTargetVer(jo.getInt(mpi.KEY_TARGETVER));
			//检查是否有预览图并解码
			if(jo.getBoolean(mpi.KEY_HASPREVIEW)){
				byte[] piccache=android.util.Base64.decode(jo.getString(mpi.KEY_PREVIEW),android.util.Base64.DEFAULT);
				mpi.setModPreview(BitmapFactory.decodeByteArray(piccache,0,piccache.length));
			}

			return mpi;
		}
	}





	public static class IllegalModInfoException extends Exception
	{
		public IllegalModInfoException (String info)
		{
			super ( info );
		}
	}
	public static final class Versions{
		public static final int VER_0=0;
		//ver0 -> ver1 enable soundeffect support
		public static final int VER_1=1;
		//ver1 -> ver2 abandon old soudeffect interface and use a new one.
		public static final int VER_2=2;
		//ver2 -> ver3 add support for replacing Japanese Captain Voice.
		public static final int VER_3=3;
	}
}
