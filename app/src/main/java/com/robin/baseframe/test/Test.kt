package com.robin.baseframe.test

object Test {

    //快速排序
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

    //冒泡排序
    fun bubbleSort(data: IntArray) {
        for (i in data.indices) {
            for (j in 0 until data.size - i - 1) {
                if (data[j] > data[j + 1]) {
                    val temp = data[j]
                    data[j] = data[j + 1]
                    data[j + 1] = temp
                }
            }
        }
    }

    //接雨滴
    fun trap(height: IntArray): Int {
        var sum = 0
        for (i in height.indices) {
            var maxLeft = 0
            var maxRight = 0
            for (j in 0..i) {
                maxLeft = Math.max(maxLeft, height[j])
            }
            for (j in i until height.size) {
                maxRight = Math.max(maxRight, height[j])
            }
            sum += Math.min(maxLeft, maxRight) - height[i]
        }
        return sum
    }

    //贪心算法
    fun canJump(nums: IntArray): Boolean {
        var max = 0
        for (i in nums.indices) {
            if (i > max) {
                return false
            }
            max = Math.max(max, i + nums[i])
        }
        return true
    }
}