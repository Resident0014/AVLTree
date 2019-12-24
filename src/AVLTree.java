import java.util.*;

public class AVLTree<T extends Comparable<T>> implements SortedSet<T> {
    static class Node<T extends Comparable<T>> {
        T data;
        int height;
        Node<T> left;
        Node<T> right;

        Node(T data) {
            this.data = data;
            this.height = 1;
            this.left = null;
            this.right = null;
        }

        int balanceFactor() {
            int hLeft = left == null ? 0 : left.height;
            int hRight = right == null ? 0 : right.height;
            return hRight - hLeft;
        }

        void fixHeight() {
            int hLeft = left == null ? 0 : left.height;
            int hRight = right == null ? 0 : right.height;
            height = Math.max(hLeft, hRight) + 1;
        }

        Node<T> rotateRight() {
            Node<T> q = left;
            left = q.right;
            q.right = this;
            fixHeight();
            q.fixHeight();
            return q;
        }

        Node<T> rotateLeft() {
            Node<T> q = right;
            right = q.left;
            q.left = this;
            fixHeight();
            q.fixHeight();
            return q;
        }

        Node<T> balance() {
            fixHeight();
            if (balanceFactor() == 2) {
                if (right.balanceFactor() < 0) {
                    right = right.rotateRight();
                }
                return rotateLeft();
            }
            if (balanceFactor() == -2) {
                if (left.balanceFactor() > 0) {
                    left = left.rotateLeft();
                }
                return rotateRight();
            }
            return this;
        }

        Node<T> insert(T t) {
            int cmp = t.compareTo(data);
            if (cmp == 0) {
                return null;
            }
            if (cmp < 0) {
                left = left == null ? new Node<>(t) : left.insert(t);
            }
            else {
                right = right == null ? new Node<>(t) : right.insert(t);
            }
            return balance();
        }

        Node<T> findMin() {
            return left != null ? left.findMin() : this;
        }

        Node<T> removeMin() {
            if (left == null) {
                return right;
            }
            left = left.removeMin();
            return balance();
        }

        Node<T> remove(T t) {
            int cmp = t.compareTo(data);
            if (cmp < 0) {
                if (left == null) {
                    return null;
                }
                left = left.remove(t);
            }
            else if (cmp > 0) {
                if (right == null) {
                    return null;
                }
                right = right.remove(t);
            }
            else {
                if (right == null) {
                    return left;
                }
                Node<T> min = right.findMin();
                min.right = right.removeMin();
                min.left = left;
                return min.balance();
            }
            return balance();
        }
    }

    static class SubTree<T extends Comparable<T>> extends AVLTree<T> {
        T min;
        T max;
        AVLTree<T> parentTree;

        SubTree(T min, T max, AVLTree<T> parentTree) {
            super(null);
            this.min = min;
            this.max = max;
            this.parentTree = parentTree;
            for (T t : parentTree) {
                if (isCorrectValue(t)) {
                    addSimple(t);
                }
            }
        }

        @Override
        protected AVLTree<T> parentTree() {
            return this.parentTree;
        }

        boolean isCorrectValue(T t) {
            return (min == null || t.compareTo(min) >= 0) &&
                   (max == null || t.compareTo(max) < 0);
        }

        @Override
        public boolean add(T t) {
            if (isCorrectValue(t)) {
                return parentTree().add(t);
            }
            return false;
        }

        @Override
        public boolean contains(Object o) {
            if (isCorrectValue((T) o)) {
                return super.contains(o);
            }
            return false;
        }

        @Override
        public boolean remove(Object o) {
            if (isCorrectValue((T) o)) {
                return parentTree().remove(o);
            }
            return false;
        }

        void addSimple(T t) {
            if (isCorrectValue(t)) {
                super.add(t);
            }
        }

        void removeSimple(Object o) {
            if (isCorrectValue((T) o)) {
                super.remove(o);
            }
        }

        @Override
        public SortedSet<T> headSet(T toElement) {
            return subSet(min, toElement);
        }

        @Override
        public SortedSet<T> tailSet(T fromElement) {
            return subSet(fromElement, max);
        }
    }

    Node<T> root = null;
    private int size = 0;
    private LinkedList<SubTree<T>> subTrees;

    AVLTree() {
        subTrees = new LinkedList<>();
    }

    private AVLTree(LinkedList<SubTree<T>> subTrees) {
        this.subTrees = subTrees;
    }

    protected AVLTree<T> parentTree() {
        return this;
    }

    private void addToSubTrees(T t) {
        if (subTrees != null) {
            for (SubTree<T> tree : subTrees) {
                tree.addSimple(t);
            }
        }
    }

    private void removeFromSubTrees(Object o) {
        if (subTrees != null) {
            for (SubTree<T> tree : subTrees) {
                tree.removeSimple(o);
            }
        }
    }

    @Override
    public boolean add(T t) {
        if (size == 0) {
            root = new Node<>(t);
            size++;
            addToSubTrees(t);
            return true;
        }
        Node<T> res = root.insert(t);
        if (res != null) {
            root = res;
            size++;
            addToSubTrees(t);
            return true;
        }
        return false;
    }

    @Override
    public boolean remove(Object o) {
        if (size == 0) {
            return false;
        }
        if (size == 1) {
            if (root.data.equals(o)) {
                clear();
                removeFromSubTrees(o);
                return true;
            }
            return false;
        }
        Node<T> res = root.remove((T) o);
        if (res != null) {
            root = res;
            size--;
            removeFromSubTrees(o);
            return true;
        }
        return false;
    }

    @Override
    public boolean contains(Object o) {
        T t = (T) o;
        Node<T> node = root;
        while (node != null) {
            int cmp = t.compareTo(node.data);
            if (cmp < 0) {
                node = node.left;
            }
            else if (cmp > 0) {
                node = node.right;
            }
            else {
                return true;
            }
        }
        return false;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public Iterator<T> iterator() {
        return new AVLIterator();
    }

    class AVLIterator implements Iterator<T> {
        Stack<Node<T>> stack = new Stack<>();

        AVLIterator() {
            Node<T> node = root;
            while (node != null) {
                stack.push(node);
                node = node.left;
            }
        }

        @Override
        public boolean hasNext() {
            return !stack.isEmpty();
        }

        @Override
        public T next() {
            Node<T> node = stack.pop();
            T data = node.data;
            node = node.right;
            while (node != null) {
                stack.push(node);
                node = node.left;
            }
            return data;
        }
    }

    @Override
    public Comparator<? super T> comparator() {
        return Comparable::compareTo;
    }

    @Override
    public SortedSet<T> subSet(T fromElement, T toElement) {
        SubTree<T> s = new SubTree<>(fromElement, toElement, parentTree());
        parentTree().subTrees.add(s);
        return s;
    }

    @Override
    public SortedSet<T> headSet(T toElement) {
        return subSet(null, toElement);
    }

    @Override
    public SortedSet<T> tailSet(T fromElement) {
        return subSet(fromElement, null);
    }

    @Override
    public T first() {
        if (root == null) {
            return null;
        }
        Node<T> node = root;
        while (node.left != null) {
            node = node.left;
        }
        return node.data;
    }

    @Override
    public T last() {
        if (root == null) {
            return null;
        }
        Node<T> node = root;
        while (node.right != null) {
            node = node.right;
        }
        return node.data;
    }

    @Override
    public Object[] toArray() {
        Object[] a = new Object[size];
        int i = 0;
        for (T x : this) {
            a[i++] = x;
        }
        return a;
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        if (size > a.length) {
            a = (T1[]) new Object[size];
        }
        int i = 0;
        for (T x : this) {
            a[i++] = (T1) x;
        }
        for (; i < a.length; i++) {
            a[i] = null;
        }
        return a;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object x : c) {
            if (!contains(x)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        boolean res = false;
        for (T x : c) {
            res |= add(x);
        }
        return res;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean res = false;
        for (T x : this) {
            if (!c.contains(x)) {
                res |= remove(x);
            }
        }
        return res;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean res = false;
        for (Object x : c) {
            res |= remove(x);
        }
        return res;
    }

    @Override
    public void clear() {
        root = null;
        size = 0;
        if (subTrees != null) {
            subTrees.clear();
        }
    }
}
