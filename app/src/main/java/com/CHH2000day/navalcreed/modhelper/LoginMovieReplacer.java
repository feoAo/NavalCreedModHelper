package com.CHH2000day.navalcreed.modhelper;
import android.content.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v4.app.*;
import android.view.*;
import android.widget.*;
import android.net.*;
import java.io.*;
import android.view.View.*;
import android.support.v7.app.*;

public class LoginMovieReplacer extends Fragment
{

	private ModHelperApplication mapplication;
	private View v;
	private TextView file;
	private Button select,remove,update;

	private Uri srcfile;
	private File target;

	private static int QUERY_CODE=2;
	@Override
	public View onCreateView ( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
	{

		mapplication = (ModHelperApplication)getActivity ( ).getApplication ( );
		v = inflater.inflate ( R.layout.loginmoviereplacer_fragment, null );
		file = (TextView)v.findViewById ( R.id.loginmoviereplacerfragmentTextView );
		select = (Button)v.findViewById ( R.id.loginmoviereplacerfragmentButtonSelect );
		update = (Button)v.findViewById ( R.id.loginmoviereplacerfragmentButtonUpdate );
		remove = (Button)v.findViewById ( R.id.loginmoviereplacerfragmentButtonRemove );
		// TODO: Implement this method

		select.setOnClickListener ( new OnClickListener ( ){

				@Override
				public void onClick ( View p1 )
				{
					Intent intent=new Intent ( Intent.ACTION_GET_CONTENT );
					intent.setType ( "*/*" );
					startActivityForResult ( intent, QUERY_CODE );

					// TODO: Implement this method
				}
			} );
		update.setOnClickListener ( new OnClickListener ( ){

				@Override
				public void onClick ( View p1 )
				{
					if ( srcfile == null )
					{
						Snackbar.make ( v, "请先选择文件", Snackbar.LENGTH_LONG ).show ( );
						return;
					}
					AlertDialog.Builder adb=new AlertDialog.Builder ( getActivity ( ) );
					adb.setTitle ( "请稍等" )
						.setMessage ( "正在复制文件" )
						.setCancelable ( false );
					final AlertDialog ad=adb.create ( );
					ad.setCancelable ( false );
					final Handler h=new Handler ( ){
						public void handleMessage ( Message msg )
						{
							ad.dismiss ( );
							switch ( msg.what )
							{
								case 0:
									//无异常
									Snackbar.make ( v, "操作完成", Snackbar.LENGTH_LONG ).show ( );
									break;
								case 1:
									//操作出现异常
									Snackbar.make ( v, ( (Throwable)msg.obj ).getMessage ( ), Snackbar.LENGTH_LONG ).show ( );
							}
						}
					};
					ad.show ( );
					new Thread ( ){
						public void run ( )
						{
							try
							{
								Utils.copyFile ( getActivity ( ).getContentResolver ( ).openInputStream ( srcfile ), gettargetfile());
								h.sendEmptyMessage ( 0 );
							}
							catch (IOException e)
							{
								h.sendMessage ( h.obtainMessage ( 1, e ) );
							}

						}
					}.start ( );

					// TODO: Implement this method
				}
			} );
			
		remove.setOnClickListener ( new OnClickListener ( ){

				@Override
				public void onClick ( View p1 )
				{
					String result = gettargetfile().delete()?"操作成功！":"操作失败";
					Snackbar.make(v,result,Snackbar.LENGTH_LONG).show();
					// TODO: Implement this method
				}
			} );

		return v;
	}

	private File gettargetfile(){
		if(target==null){
			target=new File(mapplication.getResFilesDir(),"loginmovie.ogv");
		}
		return target;
	}
	@Override
	public void onActivityResult ( int requestCode, int resultCode, Intent data )
	{
		// TODO: Implement this method
		super.onActivityResult ( requestCode, resultCode, data );
		if ( requestCode != QUERY_CODE )
		{return;}
		if( resultCode != AppCompatActivity.RESULT_OK){
			return;
		}
		if ( data == null || data.getData ( ) == null )
		{Snackbar.make ( v, "源文件不能为空", Snackbar.LENGTH_LONG ).show ( );
			return;}
		try
		{
			//OGG与OGV拥有相同的magic number
			srcfile = data.getData ( );

			if ( !Utils.FORMAT_OGG.equals ( Utils.identifyFormat ( getActivity ( ).getContentResolver ( ).openInputStream ( srcfile ), true ) ) )
			{
				srcfile = null;
				Snackbar.make ( v, "源文件不为ogv格式！操作终止！", Snackbar.LENGTH_LONG ).show ( );
			}

		}
		catch (IOException e)
		{
			srcfile = null;
			Snackbar.make ( v, "源文件读取错误", Snackbar.LENGTH_LONG ).show ( );
		}
		if ( srcfile != null )
		{
			file.setText ( srcfile.getPath ( ) );
		}

	}


}