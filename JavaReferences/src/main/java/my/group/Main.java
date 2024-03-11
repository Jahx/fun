package my.group;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        Object object = new Object();
        Object object2 = new Object();
        Object object3 = new Object();

        SoftReference<Object> softReference = new SoftReference<>(object2);
        WeakReference<Object> weakReference = new WeakReference<>(object3);
        ReferenceQueue<Object> referenceQueue = new ReferenceQueue<>();
        PhantomReference<Object> phantomReference = new PhantomReference<>(object, referenceQueue);

        object = null;
        object2 = null;
        object3 = null;

        System.out.println(softReference.get());
        System.out.println(weakReference.get());
        System.out.println(phantomReference.get());

        System.gc();
        System.out.println("==GC==");

        System.out.println(softReference.get());
        System.out.println(weakReference.get());
        System.out.println(phantomReference.get());

        referenceQueue.remove();
    }
}