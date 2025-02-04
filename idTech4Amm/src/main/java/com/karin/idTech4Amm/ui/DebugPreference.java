package com.karin.idTech4Amm.ui;
import android.content.Intent;
import android.os.Process;
import android.preference.PreferenceFragment;
import android.os.Bundle;
import android.preference.PreferenceScreen;
import android.preference.Preference;
import android.content.Context;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;

import com.karin.idTech4Amm.LogcatActivity;
import com.karin.idTech4Amm.R;
import com.karin.idTech4Amm.lib.ContextUtility;
import com.karin.idTech4Amm.sys.Constants;
import com.n0n3m4.q3e.Q3ELang;
import com.n0n3m4.q3e.Q3EUtils;
import com.n0n3m4.q3e.karin.KBacktraceHandler;
import com.n0n3m4.q3e.karin.KUncaughtExceptionHandler;

/**
 * Debug preference fragment
 */
public class DebugPreference extends PreferenceFragment implements Preference.OnPreferenceChangeListener
{

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.debug_preference);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference)
    {
        String key = preference.getKey();
        if("last_dalvik_crash_info".equals(key))
        {
            OpenCrashInfo();
        }
        else if("get_pid".equals(key))
        {
            GetPID();
        }
        else if("open_documentsui".equals(key))
        {
            OpenDocumentsUI();
        }
        else if("open_logcat".equals(key))
        {
            OpenLogcat();
        }
        else if("show_preference".equals(key))
        {
            ShowPreference();
        }
        else if("last_backtrace".equals(key))
        {
            OpenBacktraceInfo();
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    private void OpenCrashInfo()
    {
        Context activity = ContextUtility.GetContext(this);
        String text = KUncaughtExceptionHandler.GetDumpExceptionContent();
        AlertDialog.Builder builder = ContextUtility.CreateMessageDialogBuilder(activity, Q3ELang.tr(activity, R.string.last_crash_info), text != null ? text : Q3ELang.tr(activity, R.string.none));
        if(text != null)
        {
            builder.setNeutralButton(R.string.clear, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id)
                {
                    KUncaughtExceptionHandler.ClearDumpExceptionContent();
                    dialog.dismiss();
                }
            });
            if(Constants.IsDebug())
            {
                builder.setNegativeButton("Trigger", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        throw new RuntimeException("Manuel trigger exception for testing");
                    }
                });
            }
        }
        builder.create().show();
    }

    private void GetPID()
    {
        Context activity = ContextUtility.GetContext(this);
        final String PID = "" + Process.myPid();
        Toast.makeText(activity, getString(R.string.application_pid) + PID, Toast.LENGTH_LONG).show();
        Q3EUtils.CopyToClipboard(activity, PID);
    }

    private void OpenDocumentsUI()
    {
        Context activity = ContextUtility.GetContext(this);
        try
        {
            ContextUtility.OpenDocumentsUI(activity);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void OpenLogcat()
    {
        Context activity = ContextUtility.GetContext(this);
        activity.startActivity(new Intent(activity, LogcatActivity.class));
    }

    private void ShowPreference()
    {
        Context activity = ContextUtility.GetContext(this);
        new SharedPreferenceViewer(activity).run();
    }

    private void OpenBacktraceInfo()
    {
        Context activity = ContextUtility.GetContext(this);
        KBacktraceHandler.HandleBacktrace(activity);
        String text = KBacktraceHandler.GetDumpExceptionContent();
        AlertDialog.Builder builder = ContextUtility.CreateMessageDialogBuilder(activity, Q3ELang.tr(activity, R.string.last_backtrace_info), text != null ? text : Q3ELang.tr(activity, R.string.none));
        if(text != null)
        {
            builder.setNegativeButton(R.string.clear, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id)
                {
                    KBacktraceHandler.ClearDumpBacktraceContent();
                    dialog.dismiss();
                }
            });
            builder.setNeutralButton(R.string.copy, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id)
                {
                    Q3EUtils.CopyToClipboard(activity, text);
                    Toast.makeText(activity, R.string.success, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });
        }
        builder.create().show();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue)
    {
        return true;
    }
}
