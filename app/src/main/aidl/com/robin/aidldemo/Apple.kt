package com.robin.aidldemo

import android.os.Parcel
import android.os.Parcelable

class Apple : Parcelable {
    var name: String?
    var price: Float
    var noticeInfo: String?
        private set

    constructor(name: String?, price: Float, noticeInfo: String?) {
        this.name = name
        this.price = price
        this.noticeInfo = noticeInfo
    }

    protected constructor(`in`: Parcel) {
        name = `in`.readString()
        price = `in`.readFloat()
        noticeInfo = `in`.readString()
    }

    fun readFromParcel(dest: Parcel) {
        //注意，此处的读值顺序应当是和writeToParcel()方法中一致的
        name = dest.readString()
        price = dest.readInt().toFloat()
        noticeInfo = dest.readString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(name)
        dest.writeFloat(price)
        dest.writeString(noticeInfo)
    }

    companion object CREATOR : Parcelable.Creator<Apple> {
        override fun createFromParcel(parcel: Parcel): Apple {
            return Apple(parcel)
        }

        override fun newArray(size: Int): Array<Apple?> {
            return arrayOfNulls(size)
        }
    }


}
