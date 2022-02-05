package aperr.android.questionsdescience;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by perrault on 02/07/2017.
 */
public class Pool<T> {
    public interface PoolobjectFactory<T>{
        public T createObject();
    }

    private final List<T> freeObjects;
    private final PoolobjectFactory<T> factory;
    private final int maxSize;

    public Pool(PoolobjectFactory<T> factory, int maxSize){
        this.factory = factory;
        this.maxSize = maxSize;
        this.freeObjects = new ArrayList<T>(maxSize);
    }

    public T newObject(){
        T object = null;
        if(freeObjects.isEmpty()){
            object = factory.createObject();
        }else{
            object = freeObjects.remove(freeObjects.size()-1);
        }
        return object;
    }

    public void free(T object){
        if(freeObjects.size() < maxSize) freeObjects.add(object);
    }
}
