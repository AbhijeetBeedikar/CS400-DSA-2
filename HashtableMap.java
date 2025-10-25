import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * A Hashtable Map constructed from scratch
 *
 * @param <KeyType>   Datatype for the Key
 * @param <ValueType> Datatype that the key maps to
 */
public class HashtableMap<KeyType, ValueType> implements MapADT<KeyType,ValueType> {
    private int capacity;
    private LinkedList<Pair>[] table;
    private int size;

    /**
     * This class is used to define each (key,value) pair
     */
    protected class Pair {
        public KeyType key;
        public ValueType value;


        public Pair(KeyType key, ValueType value) {
            this.key = key;
            this.value = value;
        }


    }

    /**
     * Constructor for HashtableMap with custom capacity
     *
     * @param capacity maximum number of unique hashes that the table can hold
     */
    @SuppressWarnings("unchecked")
    public HashtableMap(int capacity) {
        table = (LinkedList<Pair>[]) new LinkedList[capacity];
        for (int i = 0; i < table.length; i++) {
            table[i] = new LinkedList<>();
        }
        this.capacity = capacity;
        this.size = 0;
    }

    /**
     * Default Constructor for HashtableMap
     */
    @SuppressWarnings("unchecked")
    public HashtableMap() {
        table = (LinkedList<Pair>[]) new LinkedList[64];
        for (int i = 0; i < table.length; i++) {
            table[i] = new LinkedList<>();
        }
        this.capacity = 64;
        this.size = 0;
    }


    /**
     * Adds a new key,value pair/mapping to this collection. It is ok that the value is null.
     *
     * @param key   the key of the key,value pair
     * @param value the value that key maps to
     * @throws IllegalArgumentException if key already maps to a value
     * @throws NullPointerException     if key is null
     */
    public void put(KeyType key, ValueType value) throws IllegalArgumentException {
        if(key == null) throw new NullPointerException("key cannot be null");
        if(containsKey(key)) throw new IllegalArgumentException("key already exists");
        if (table[Math.abs(key.hashCode()) % this.capacity].isEmpty()){
            size++;
        }
        table[Math.abs(key.hashCode()) % this.capacity].add(new Pair(key, value));

        if((double)size / this.capacity >= 0.8)
            resize();
    }


    /**
     * Checks whether a key maps to a value in this collection.
     *
     * @param key the key to check
     * @return true if the key maps to a value, and false is the
     * key doesn't map to a value
     */
    public boolean containsKey(KeyType key) {
        if (key == null) return false;
        if (table[Math.abs(key.hashCode()) % this.capacity] != null) {
            for (Pair i : table[Math.abs(key.hashCode()) % this.capacity]) {
                if (i != null && i.key.equals(key)) return true;
            }
        }
        return false;
    }


    /**
     * Retrieves the specific value that a key maps to.
     *
     * @param key the key to look up
     * @return the value that key maps to
     * @throws NoSuchElementException when key is not stored in this
     *                                collection
     */
    public ValueType get(KeyType key) throws NoSuchElementException {
        if (key == null) throw new NoSuchElementException();
        if (table[Math.abs(key.hashCode()) % this.capacity] != null) {
            for (Pair i : table[Math.abs(key.hashCode()) % this.capacity]) {
                if (i != null && i.key.equals(key)) return i.value;
            }
        }
        throw new NoSuchElementException();
    }


    /**
     * Remove the mapping for a key from this collection.
     *
     * @param key the key whose mapping to remove
     * @return the value that the removed key mapped to
     * @throws NoSuchElementException when key is not stored in this
     *                                collection
     */
    public ValueType remove(KeyType key) throws NoSuchElementException {
        int indexCounter = 0;
        if (key == null) throw new NoSuchElementException();
        if (table[Math.abs(key.hashCode()) % this.capacity] != null) {
            for (Pair i : table[Math.abs(key.hashCode()) % this.capacity]) {
                if (i.key.equals(key)) {
                    ValueType returnValue = i.value;
                    table[Math.abs(key.hashCode()) % this.capacity].remove(indexCounter);
                    if (table[Math.abs(key.hashCode()) % this.capacity].isEmpty())
                        this.size--;
                    return returnValue;
                }
                indexCounter++;
            }
        }
        throw new NoSuchElementException();
    }


    /**
     * Removes all key,value pairs from this collection.
     */
    @SuppressWarnings("unchecked")
    public void clear() {
        this.table = (LinkedList<Pair>[]) new LinkedList[this.capacity];
    }


    /**
     * Retrieves the number of keys stored in this collection.
     *
     * @return the number of keys stored in this collection
     */
    public int getSize() {
        int size = 0;
        for (int i = 0; i < table.length; i++) {
            if (table[i] != null) size += table[i].size();
        }
        return size;
    }


    /**
     * Retrieves this collection's capacity.
     *
     * @return the size of te underlying array for this collection
     */
    public int getCapacity() {
        return this.capacity;
    }

    /**
     * Retrieves this collection's keys.
     * @return a list of keys in the underlying array for this collection
     */
    public List<KeyType> getKeys(){
        List<KeyType> keys = new LinkedList<>();
        for(int i = 0; i < this.capacity; i++){
            if (table[i] != null) {
                for (Pair p : table[i]) {
                    keys.add(p.key);
                }
            }
        }
        return keys;
    }

    /**
     * Resizes the table array if the number of indices filled is >=80% of the table's capacity
     */
    @SuppressWarnings("unchecked")
    private void resize() {

        LinkedList<Pair>[] newTable = (LinkedList<Pair>[]) new LinkedList[this.capacity*2];

        // initializing all nodes to be empty LinkedLists
        for(int i = 0; i < this.capacity*2; i++){
            newTable[i] = new LinkedList<>();
        }

        // re-hashing
        for (int i = 0; i < this.capacity; i++) {
            for (Pair p : table[i]) {
                newTable[Math.abs(p.key.hashCode()) % (this.capacity*2)].add(p);
            }
        }
        this.capacity *= 2;
        this.table = newTable;
    }


    //-----------TESTS--------------


    /**
     * Tests put method extensively and also containsKey and get methods for the most part
     */
    @Test
    public void test1() {
        HashtableMap<String, String> test = new HashtableMap<String, String>(8);


        // ordinary implementation
        test.put("test", "true");
        assertTrue(test.containsKey("test"));
        assertTrue(test.get("test").equals("true"));


        // correctly throws exception for duplicate key value
        try {
            test.put("test", "false");
            fail("Should've thrown an IllegalArgumentException");
        } catch (IllegalArgumentException ignored) {
        }


        // correctly throws exception for null key value
        try {
            test.put(null, "false");
            fail("Should've thrown an NullPointerException");
        } catch (NullPointerException ignored) {
            try {
                // making sure that .get() is protected against NullPointerExceptions
                test.get(null);
                fail("Should've thrown an NoSuchElementException");
            } catch (NoSuchElementException e) {
                // making sure that .containsKey() is protected against NullPointerExceptions
                assertFalse(test.containsKey(null));
            }
        }


        // accepts null as a value for some valid key
        try {
            // Note: "tec" and "test" are stored at the same table index in a linked list, thus this block tests the containsKey and get methods in such a case as well
            test.put("tec", null);
            assertTrue(test.containsKey("tec"));
            assertTrue(test.get("tec") == null);
            assertTrue(test.table[2].size() == 2); // making sure that "tec" and "test" are actually stored in the same LinkedList
        } catch (Exception e) {
            e.printStackTrace();
            fail("Shouldn't have thrown any exception");
        }

        // Rehashing
        HashtableMap<String, Double> test2 = new HashtableMap<String, Double>(8);
        test2.put("a",1.0);
        test2.put("b",2.0);
        test2.put("c",3.0);
        test2.put("d",4.0);
        test2.put("e",5.0);
        assertTrue(test2.getCapacity() == 8);
        test2.put("f",6.0);
        test2.put("g",7.0);
        test2.put("h",8.0);
        test2.put("i",9.0);
        assertTrue(test2.getCapacity() == 16);
    }

    /**
     * Tests remove method extensively
     */
    @Test
    public void test2() {
        HashtableMap<String, String> test = new HashtableMap<String, String>(8);
        test.put("test", "true");
        test.put("tec", "true");
        test.put("tent", "true");

        // remove works on an index consisting of two values in the corresponding LinkedList
        assertTrue(test.remove("test").equals("true"));
        assertTrue(test.table[2].get(0).key.equals("tec") && !test.containsKey("test"));

        // normal functioning of remove method
        assertTrue(test.remove("tent").equals("true"));
        assertTrue(!test.containsKey("tent") && test.table[7].isEmpty()); // checks whether the index at which the key was removed is correctly set to null instead of an empty LinkedList<>

        // checks if .remove() is protected from NullPointerException
        try {
            test.remove(null);
            fail("Should've thrown NoSuchElementException");
        } catch (NoSuchElementException ignored) {
        }

        // checks if .remove() correctly throws an exception when the key to be removed doesn't exist
        try {
            test.remove("tent");
            fail("Should've thrown NoSuchElementException");
        } catch (NoSuchElementException ignored) {
        }
    }

    /**
     * Tests get and containsKey methods when they receive a non-existent key as their parameter
     */
    @Test
    public void test3() {
        HashtableMap<String, String> test = new HashtableMap<String, String>();
        // .containsKey() invalid case
        assertFalse(test.containsKey("test"));

        // .get() invalid case
        try {
            test.get("test");
            fail("Should've thrown an NoSuchElementException");
        } catch (NoSuchElementException ignored) {
        }
    }


    /**
     * Tests clear method
     */
    @Test
    public void test4() {
        HashtableMap<String, String> test = new HashtableMap<String, String>();
        test.put("test", "true");
        test.put("test2", "false");
        test.put("tec", "true");
        test.clear();
        // normal functioning of clear method
        assertFalse(test.containsKey("test") || test.containsKey("test2") || test.containsKey("tec"));


    }


    /**
     * Tests getSize and getCapacity methods
     */
    @Test
    public void test5() {
        HashtableMap<String, String> test = new HashtableMap<String, String>(8);
        test.put("test", "true");
        test.put("test2", "false");
        test.put("tec", "true");

        // normal functioning of the two methods
        assertTrue(test.getSize() == 3);
        assertTrue(test.getCapacity() == 8);

        // case when hashtable map is declared using default constructor
        HashtableMap<String, String> test2 = new HashtableMap<String, String>();
        assertTrue(test2.getCapacity() == 64);

        // Resizeing and rehashing test
        HashtableMap<String, Integer> test3 = new HashtableMap<String, Integer>(5);
        assertTrue(test3.getSize() == 0);
        test3.put("a", 1);
        int a = 0;
        for (int i = 0; i < test3.table.length; i++) {
            for (int j = 0; j < test3.table[i].size(); j++) {
                if (test3.table[i].get(j).key.equals("a")) {
                    a = i;
                    break;
                }
            }
        }
        assertTrue(test3.getSize() == 1);
        test3.put("b", 2);
        assertTrue(test3.getSize() == 2);
        test3.put("c", 3);
        assertTrue(test3.getSize() == 3);
        test3.put("d", 4);
        assertTrue(test3.getSize() == 4);
        test3.put("e", 5);
        assertTrue(test3.getSize() == 5);
        test3.put("f", 6);
        assertTrue(test3.getSize() == 6);
        test3.put("g", 7);
        assertTrue(test3.getSize() == 7);
        for (int i = 0; i < test3.table.length; i++) {
            for (int j = 0; j < test3.table[i].size(); j++) {
                if (test3.table[i].get(j).key.equals("a")) {
                    assertTrue( a != i && Math.abs("a".hashCode()) % test3.getCapacity() == i);
                    break;
                }
            }
        }
    }
}
