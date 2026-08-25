package com.example.arithmetic_lib

import java.util.PriorityQueue
import kotlin.math.pow
import kotlin.random.Random
import kotlin.random.nextInt

fun main() {
    repeat(2) { times ->
        println("==========================================")


        val testData = IntArray((10).toDouble().pow(times + 1).toInt())
        testData.forEachIndexed { index, i ->
            val data = Random.nextInt(0..100)
            testData[index] = data
        }
        println("size:${testData.size}, testData:" + testData.contentToString())
        val r1 = testData.sortedBy {
            it
        }
        println("sort:$r1")


        selectSort(testData.clone())
        bubbleSort(testData.clone())
        insertSort(testData.clone())
        quickSort(testData.clone())
        mergeSort(testData.clone())
        pileSort(testData.clone())

        val minHeap = PriorityQueue<Int>()
        minHeap.addAll(testData.toList())
        println("minHeap:$minHeap")
        val maxHeap = PriorityQueue<Int>(compareByDescending { it })
        maxHeap.addAll(testData.toList())
        println("maxHeap:$maxHeap")
        fullTree(testData.clone())
    }
}

/**
 * 选择排序
 * 时间复杂度 O(n^2)
 * 空间复杂度 O(1)
 * 不稳定
 */
fun selectSort(array: IntArray) {
    var swaps = 0
    for (i in 0 until array.size) {
        for (j in i + 1 until array.size) {
            if (array[j] < array[i]) {
                array[i] = array[j].also { array[j] = array[i] }
                swaps++
            }
        }
    }
    println("select swaps:$swaps ,result:" + array.contentToString())
}

/**
 * 冒牌排序
 * 时间复杂度 O(n^2)
 * 空间复杂度 O(1)
 * 稳定
 */
fun bubbleSort(array: IntArray) {
    var swaps = 0
    for (i in array.size - 1 downTo 1) {
        for (j in 0 until i) {
            if (array[j] > array[j+1]) {
                array[j] = array[j+1].also { array[j+1] = array[j] }
                swaps++
            }
        }
    }
    println("bubble swaps:$swaps, result:" + array.contentToString())
}

/**
 * 插入排序
 * 时间复杂度 O(n^2)
 *空间复杂度 O(1)
 * 稳定
 */
fun insertSort(array: IntArray) {
    var swaps = 0
    for (i in 1 until array.size) {
        val temp =  array[i]
        for (j in i - 1 downTo 0) {
            if (temp < array[j]) {
                array[j+1] = array[j]
                array[j] = temp
                swaps++
            } else {
                break
            }
        }
    }
    println("insert swaps:$swaps, result:" + array.contentToString())
}

/**
 * 快速排序
 * 时间复杂度 O(n*log(n))
 * 空间复杂度 O(log(n))
 * 不稳定
 */
fun quickSort(array: IntArray) {
    var swaps = 0
    fun recursive(array: IntArray, start: Int = 0, end: Int = array.lastIndex) {
        if (start >= end) return
        if (end - start == 1) {
            if (array[end] < array[start]) {
                array[start] = array[end].also { array[end] = array[start] }
            }
            return
        }
        val pivot = array[end]
        var j = start - 1
        for (i in start until end) {
            if (array[i] < pivot) {
                j++
                array[j] = array[i].also { array[i] = array[j] }
                swaps++
            }
        }
        array[end] = array[j+1]
        array[j+1] = pivot
        swaps++
        recursive(array, start, j)
        recursive(array, j + 2, end)
    }
    recursive(array)
    println("quick swaps:$swaps, result:${array.contentToString()}")
}

/**
 * 归并排序
 * 时间复杂度 O(n*log(n))
 * 空间复杂度 O(n)
 */
fun mergeSort(array: IntArray) {
    var swaps = 0
    fun merge(array: IntArray, array1: IntArray, array2: IntArray) {
        var index1 = 0
        var index2 = 0
        for (i in 0 until array.size) {
            if (index1 < array1.size && index2 < array2.size) {
                if (array1[index1] < array2[index2]) {
                    array[i] = array1[index1++]
                } else {
                    array[i] = array2[index2++]
                }
                swaps ++
            } else {
                if (index1 < array1.size) {
                    array[i] = array1[index1++]
                    swaps ++
                }
                if (index2 < array2.size) {
                    array[i] = array2[index2++]
                    swaps ++
                }
            }
        }
    }
    fun partition(array: IntArray) {
        if (array.size > 1) {
            val middle = array.size / 2
            val left = array.copyOfRange(0, middle)
            val right = array.copyOfRange(middle, array.size)
            partition(left)
            partition(right)
            merge(array, left, right)
        }
    }
    partition(array)
    println("merge swaps:$swaps, result:${array.contentToString()}")
}

/**
 * 堆排序
 * 时间复杂度 O(log(n))
 * 空间复杂度 O(1)
 */
fun pileSort(array: IntArray) {
    fun heapify(array: IntArray, len: Int, index: Int) {
        var max = index
        val left = 2 * index + 1
        val right = 2 * index + 2
        if (left < len && array[left] > array[max]) {
            max = left
        }
        if (right < len && array[right] > array[max]) {
            max = right
        }
        if (max != index) {
            array.swap(index, max)
            heapify(array, len, max)
        }
    }

    for (i in array.size/2-1 downTo 0) {
        heapify(array, array.size, i)
    }

    for (i in array.size -1 downTo 1) {
        array.swap(i, 0)
        heapify(array, i, 0)
    }

    println("pileSort:${array.contentToString()}")
}

fun IntArray.swap(i: Int, j: Int) {
    this[i] = this[j].also { this[j] = this[i] }
}

/**
 * 二叉树
 */
fun fullTree(array: IntArray) {

    data class TreeNode<T>(
        val value: T,
        var left: TreeNode<T>? = null,
        var right: TreeNode<T>? = null
    )

    /**
     * 将数组构建为完全二叉树
     * @param array 输入数组
     * @param index 当前节点的下标，默认为 0（根节点）
     */
    fun <T> buildCompleteBinaryTree(array: Array<T>, index: Int = 0): TreeNode<T>? {
        // 越界则返回 null
        if (index >= array.size) {
            return null
        }

        // 1. 创建当前节点
        val root = TreeNode(array[index])

        // 2. 递归构建左子树 (2 * index + 1)
        root.left = buildCompleteBinaryTree(array, 2 * index + 1)

        // 3. 递归构建右子树 (2 * index + 2)
        root.right = buildCompleteBinaryTree(array, 2 * index + 2)

        return root
    }

    val tree = buildCompleteBinaryTree(array.toTypedArray())

    fun <T> printPreOrder(node: TreeNode<T>?) {
        if (node == null) return
        print("${node.value} ")
        printPreOrder(node.left)
        printPreOrder(node.right)
    }

    fun <T> printMidOrder(node: TreeNode<T>?) {
        if (node == null) return
        printMidOrder(node.left)
        print("${node.value} ")
        printMidOrder(node.right)
    }

    fun <T> printLastOrder(node: TreeNode<T>?) {
        if (node == null) return
        printLastOrder(node.left)
        printLastOrder(node.right)
        print("${node.value} ")
    }

    fun <T> levelOrderTraversal(root: TreeNode<T>?): List<T> {
        if (root == null) return emptyList()

        val result = mutableListOf<T>()
        val queue = ArrayDeque<TreeNode<T>>()

        queue.add(root)

        while (queue.isNotEmpty()) {
            val node = queue.removeFirst()
            result.add(node.value)

            // 先左后右入队，保证从左到右的访问顺序
            node.left?.let { queue.add(it) }
            node.right?.let { queue.add(it) }
        }

        return result
    }

    printPreOrder(tree)
    println()

    printMidOrder(tree)
    println()

    printLastOrder(tree)
    println()

    println(levelOrderTraversal(tree))

    class ArrayCompleteBinaryTree<T>(private val nodes: Array<T>) {

        // 获取节点的值
        fun getValue(index: Int): T? = nodes.getOrNull(index)

        // 获取左子节点的值
        fun getLeftChild(index: Int): T? {
            val leftIndex = 2 * index + 1
            return nodes.getOrNull(leftIndex)
        }

        // 获取右子节点的值
        fun getRightChild(index: Int): T? {
            val rightIndex = 2 * index + 2
            return nodes.getOrNull(rightIndex)
        }

        // 获取父节点的值
        fun getParent(index: Int): T? {
            if (index <= 0 || index >= nodes.size) return null
            val parentIndex = (index - 1) / 2
            return nodes[parentIndex]
        }

        // 层序遍历打印
        fun printLevelOrder() {
            println(nodes.joinToString(", "))
        }
    }

    val tree2 = ArrayCompleteBinaryTree(array.toTypedArray())

    tree2.printLevelOrder()
}
