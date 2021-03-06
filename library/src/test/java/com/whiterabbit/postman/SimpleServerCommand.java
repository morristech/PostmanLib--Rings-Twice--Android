package com.whiterabbit.postman;

import android.content.Context;
import android.os.Parcel;
import com.whiterabbit.postman.commands.ServerCommand;

/**
 * Created with IntelliJ IDEA.
 * User: fedepaol
 * Date: 12/29/12
 * Time: 11:59 AM
 */
public class SimpleServerCommand extends ServerCommand{
    private boolean mSuccess;
    private String mMessage;

    public SimpleServerCommand(boolean success, String resMessage){
        mMessage = resMessage;
        mSuccess  = success;

    }

    @Override
    public void execute(Context c) {
        if(mSuccess){
            notifyResult(mMessage, c);
        }else{
            notifyError(mMessage, c);
        }
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
    }

    public static final Creator<SimpleServerCommand> CREATOR
            = new Creator<SimpleServerCommand>() {
        public SimpleServerCommand createFromParcel(Parcel in) {
            return new SimpleServerCommand(in);
        }

        public SimpleServerCommand[] newArray(int size) {
            return new SimpleServerCommand[size];
        }
    };

    public  SimpleServerCommand(Parcel p){

    }
}
