package me.skean.fillblankview;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Skean on 21/6/4.
 */
public  class ArrayListParcelableWrapper implements Parcelable {
    private ArrayList<String> list;

    public ArrayList<String> getList() {
        return list;
    }

    public ArrayListParcelableWrapper setList(ArrayList<String> list) {
        this.list = list;
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(this.list);
    }

    public void readFromParcel(Parcel source) {
        this.list = source.createStringArrayList();
    }

    public ArrayListParcelableWrapper() {
    }

    protected ArrayListParcelableWrapper(Parcel in) {
        this.list = in.createStringArrayList();
    }

    public static final Parcelable.Creator<ArrayListParcelableWrapper> CREATOR = new Parcelable.Creator<ArrayListParcelableWrapper>() {
        @Override
        public ArrayListParcelableWrapper createFromParcel(Parcel source) {
            return new ArrayListParcelableWrapper(source);
        }

        @Override
        public ArrayListParcelableWrapper[] newArray(int size) {
            return new ArrayListParcelableWrapper[size];
        }
    };
}
