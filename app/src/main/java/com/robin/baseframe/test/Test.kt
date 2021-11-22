package com.robin.baseframe.test

object Test {

    fun quickSort(data: IntArray, left: Int, right: Int) {
        if (right < left) {
            return
        }
        var base = data[left]
        var i = left
        var j = right
        while (i != j) {
            while (data[j] >= base && i < j) {
                j--
            }
            while (data[i] <= base && i < j) {
                i++
            }
            if (i < j) {
                val temp = data[i]
                data[i] = data[j]
                data[j] = temp
            }
        }
        data[left] = data[i]
        data[i] = base
        quickSort(data, left, i - 1)
        quickSort(data, i + 1, right)
    }

}