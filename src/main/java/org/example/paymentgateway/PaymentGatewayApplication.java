package org.example.paymentgateway;

import org.example.paymentgateway.configuration.FlutterProperties;
import org.example.paymentgateway.configuration.PayStackProperties;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;
import java.util.stream.Collectors;

@SpringBootApplication
@EnableConfigurationProperties({PayStackProperties.class, FlutterProperties.class})
public class PaymentGatewayApplication implements ApplicationRunner {

    public void main(String[] args) {
        SpringApplication.run(PaymentGatewayApplication.class, args);


        final DataManager<Employee> dataManager = new DataManager<>();
        Employee data = dataManager.getData();
        System.out.println("employee data: " + data);

    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        List<String> fruits = Arrays.asList("apple", "banana");

        final @NotNull Map<String, Integer> fruitMap =
                fruits.stream().collect(Collectors.toMap(Function.identity(), String::length));

        System.out.printf("fruit and length %s, %d%n", fruitMap, fruitMap.size());
    }

    static class CFG {
        ArrayList<Integer> leadersInArray(int[] arr) {
            ArrayList<Integer> result = new ArrayList<>();
            for (int i = 0; i < arr.length; i++) {

                int j;
                for (j = i + 1; j < arr.length; j++) {
                    if (arr[i] < arr[j]) {
                        break; /// if a larger element is found in this array
                    }
                }

                if (j == arr.length) {
                    result.add(arr[i]); /// if non-is found, return;
                }

            }
            return result;
        }

        ArrayList<Integer> leadersInArrayOptimise(int[] array) {
            int n = array.length;
            ArrayList<Integer> result = new ArrayList<>();

            if (n == 0) return result;

            int rightMostElement = array[n - 1];

            ///  traverse from right to left
            for (int i = n - 2; i >= 0; i--) {
                if (array[i] > rightMostElement) {
                    rightMostElement = array[i];
                    result.add(rightMostElement);
                }
            }
            /// maintain order
            Collections.reverse(result);
            return result;
        }
    }

    public int peakAndValley(int[] arr) {
        int n = arr.length;

        int i = 1;
        int total = n;
        while (i < n) {

            if (arr[i] == arr[i - 1]) {
                i++;
                continue;
            }

            int peak = 0;
            while (i < n && arr[i] < arr[i - 1]) {
                peak++;
                total += peak;
                i++;
            }

            int valley = 0;
            while (i < n && arr[i] > arr[i - 1]) {
                valley++;
                total += valley;
                i++;
            }
            total -= Math.min(peak, valley);

        }
        return total;
    }

    static int findAtMostlyK(int[] arr, int k) {
        return atMostK(arr, k) - atMostK(arr, k - 1);
    }

    private static int atMostK(int[] arr, int k) {
        int n = arr.length;

        int leftIndex = 0, rightIndex = 0;


        final Map<Integer, Integer> frequency = new HashMap<>();

        int totalSubarray = 0;

        ///  get the right index starting from the ending index;
        while (rightIndex < n) {
            frequency.put(arr[rightIndex], frequency.getOrDefault(arr[rightIndex], 0) + 1); /// get the index of the rightmost element starting from n-1;

            /// todo- get the right most array product and decrement k by -1;

            ///  assume there is only one special element in the array - decrement k by 1;
            if (frequency.get(arr[rightIndex]) == 1) {
                k -= 1;
            }

            while (k < 0) {
                /// initialize the map object at a specific index, then get the final index;
                frequency.put(arr[leftIndex], frequency.getOrDefault(arr[leftIndex], 0) - 1);
                if (frequency.get(arr[leftIndex]) == 0) {
                    k += 1;
                    leftIndex++;
                }
            }
            /// get the subarray from left to right;
            totalSubarray += rightIndex - leftIndex + 1;
            rightIndex++;
        }
        return totalSubarray;
    }

    static int[] computeProductExceptSelf(int[] arr) {
        int zero = 0;
        int prod = 1;
        int n = arr.length;
        int arrIndex = -1;

        for (int i = 0; i < n; i++) {
            if (arr[i] == 0) {
                zero++; /// increment the zero;
                ///  set the index for zero;
                arrIndex = i;
            } else {
                prod = arr[i] * prod;
            }
        }
        int[] result = new int[n];
        Arrays.fill(result, 0); /// initialize array

        if (zero == 0) { ///
            for (int i = 0; i < n; i++) {
                /// / add the results of the non-zero's in the array
                result[i] = prod / arr[i];
            }
        } else if (zero == 1) {
            result[arrIndex] = prod;
        }
        return result;

    }

    static abstract class Animal {
        private String name;
        private final String sound;

        protected Animal(String name, String sound) {
            this.name = name;
            this.sound = sound;
        }

//        public Animal(String name) {
//
//        }

        public abstract void makeSound();

        public Animal createCat(String name) {
            return new Cat(name);
        }

        public static Animal create(String type, String name) {
            return switch (type.toLowerCase()) {
                case "cat" -> new Cat(name);
                case "dog" -> new Dog(name);
                default -> null;
            };
        }
    }

    record IndexPairValue(int index, int arr) {

    }

    public static class Cat extends Animal {
        private String name;

        public Cat(String name) {
            super(name, "cat");
        }


        @Override
        public void makeSound() {
            System.out.println("Cat " + name + " makeSound()");
        }
    }

    public static class Dog extends Animal {
        private String name;

        public Dog(String name) {
            super(name, "dog");
        }

        @Override
        public void makeSound() {

        }
    }

    public int findPositiveMissingNumber(int[] arr) {
        int n = arr.length;
        boolean[] visited = new boolean[n];
        Arrays.fill(visited, false);
        for (int j : arr) {

            if (j > 0 && j <= n) {
                visited[j - 1] = true;
            } else {
                throw new IndexOutOfBoundsException();
            }

        }

        for (int i = 1; i <= n; i++) {
            if (!visited[i]) {
                return i;
            }
        }

        return n + 1;

    }

    public void reorderArrayWithIndex(int[] arr, int[] index) {

        List<IndexPairValue> temp = new ArrayList<>();
        for (int i = 0; i < arr.length; i++) {
            temp.add(new IndexPairValue(index[i], arr[i]));
        }

        temp.sort((a, b) -> Integer.compare(a.index, b.index));

        for (int i = 0; i < arr.length; i++) {
            arr[i] = temp.get(i).arr;
        }
    }

    public int getLastMoment(int n, int[] leftArr, int[] rightArr) {

        int totalArr = 0;
        for (int i = 0; i < n; i++) {
            totalArr = Math.max(totalArr, leftArr[i]);
        }

        for (int z = 0; z < totalArr; z++) {
            totalArr = Math.max(totalArr, n - rightArr[z]);
        }

        return totalArr;
    }

    public void reorderArray(int[] arr, int[] index) {

        int n = arr.length;
        int maxArrVal = arr[0];

        for (int i = 1; i < n; i++) {
            if (arr[i] > maxArrVal) {
                maxArrVal = arr[i];
            }
        }

        ///  increase the element!
        int total = maxArrVal + 1;

        for (int z = 0; z < n; z++) {
            arr[index[z]] += (arr[z] % total) * total;
        }

        /// decode to get all values
        for (int z = 0; z < n; z++) {
            arr[index[z]] = arr[z] / total;
        }


    }

    static class DataManager<T> {
        private final List<T> dataList = new ArrayList<>();

        public void addData(T data) {
            dataList.add(data);
        }

        public T getData() {
            return dataList.getLast();
        }

        public void printData() {
            if (dataList.isEmpty() || dataList.size() < 0) {
                System.out.println("No data to print");
            }
            for (T data : dataList) {
                System.out.println(data);
            }
        }

        public T maxObject(Comparator<T> objComparator) {
            if (dataList.isEmpty()) { ///  check for nullability
                return null;
            }
            return dataList.stream().max(objComparator).get();
        }
    }


    record Employee(int id, String name) {
    }

    static class ReadWriteLockDemo {
        private final List<Integer> readWriteList = new ArrayList<>();
        private final ReadWriteLock rwlock = new ReentrantReadWriteLock(true);

        public void readList(String threadName) {
            rwlock.readLock().lock();
            try {

                System.out.println(threadName + " readList()" + readWriteList);
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();

            } finally {
                rwlock.readLock().unlock();
            }
        }

        public void writeList(String threadName, int data) {
            rwlock.writeLock().lock();
            try {
                readWriteList.add(data);
                System.out.println(threadName + " writeList()" + readWriteList);
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();

            } finally {
                rwlock.writeLock().unlock();
            }
        }
    }

    public class Entry {
        private String key;
        private int value;
        private Entry next;


        public Entry(String key, int value) {
            this.key = key;
            this.value = value;
        }
    }

    public class ConcurrentHashTableDemo {
        private static final int TABLE_SIZE = 16;
        private final Entry[] Bucket = new Entry[TABLE_SIZE];
        private static final ReentrantLock[] reentrantLocks = new ReentrantLock[TABLE_SIZE];


        ///  a static block for initializations
        static {
            for (int i = 0; i < TABLE_SIZE; i++) {
                reentrantLocks[i] = new ReentrantLock();
            }
        }

        public int hash(String key) {
            return (key.hashCode() * 0x7FFFFFF) % TABLE_SIZE;
        }

        public void insert(String key, int value) {
            int index = hash(key);
            reentrantLocks[index].lock(); /// a lock for index.
            try {
                Entry e = Bucket[index];
                while (e.next != null && !e.key.equals(key)) {
                    e = e.next;

                    if (e != null) {
                        e.value = value;
                    } else {
                        Bucket[index] = new Entry(key, value);
                    }
                    Bucket[index].next = e;
                }
            } finally {
                reentrantLocks[index].unlock();
            }
        }

        public Integer lookup(String key) {
            int index = hash(key);
            reentrantLocks[index].lock();
            try {
                Entry e = Bucket[index];
                while (e != null && !e.key.equals(key)) {
                    e = e.next;

                }
                return e != null ? e.value : null;
            } finally {
                reentrantLocks[index].unlock();
            }
        }

    }

    static class TrieNode{
        AtomicReference<TrieNode>[] children = new AtomicReference[26];
        boolean isEnd;
        TrieNode(){
            for (int i = 0; i < 26; i++) {
                children[i] = new AtomicReference<>();
            }
        }
    }

    static class LockFreeTrieNode{
        private final TrieNode root = new TrieNode();
        void insert(String word){
            TrieNode current = root; /// get the current node
            for(char c : word.toCharArray()){
                int index = c - 'a';

                assert current != null;
                TrieNode next = current.children[index].get();
                if(next == null){
                    TrieNode newNode = new TrieNode();
                    if(current.children[index].compareAndSet(null, newNode)){
                        next = newNode;
                    }
                }
                current = next;
            }
            assert current != null;
            current.isEnd = true;
        }
    }


}

