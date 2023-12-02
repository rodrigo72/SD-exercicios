// This is a modified queue where elements are added at the end like a regular queue,
// but when polling, elements are removed if they meet a specified condition.
// If an element doesn't meet the condition, the queue is traversed sequentially
// from the first to the last element until a matching element is found or the queue is empty.

import java.util.*;

public class MeasureSelectorQueue<T extends Measurable> {
    private static class Node<T> {
        public T data;
        public Node<T> prev;
        public Node<T> next;

        public Node(T data) {
            this.data = data;
            this.prev = null;
            this.next = null;
        }
    }

    private Node<T> first;
    private Node<T> last;
    private int length;
    private final int cap;
    private long min;
    private final PriorityQueue<Long> minHeap;
    private final Set<Long> state;

    public MeasureSelectorQueue(int cap) {
        this.first = null;
        this.last = null;
        this.length = 0;
        this.cap = cap;
        this.min = Long.MAX_VALUE;
        this.minHeap = new PriorityQueue<>();
        this.state = new HashSet<>();
    }

    public boolean isEmpty() {
        return this.length == 0;
    }

    public boolean isEmpty(long max) {
        if (this.length == 0) {
            return true;
        } else {
            return this.min > max;
        }
    }

    public boolean isFull() {
        return this.length == this.cap;
    }

    public int size() {
        return this.length;
    }

    private void updateMin(Long measure) {
        this.minHeap.remove(measure);
        if (this.min == measure) {
            Long min = this.minHeap.peek();
            if (min != null) {
                this.min = min;
            } else {
                this.min = Long.MAX_VALUE;
            }
        }
    }

    public void add(T data) {

        if (this.length == this.cap)
            return;

        Node<T> node = new Node<>(data);
        if (this.length == 0) {
            this.first = this.last = node;
            this.min = data.measure();
        } else {
            node.prev = this.last;
            this.last.next = node;
            this.last = node;
            if (this.min > data.measure()) {
                this.min = data.measure();
            }
        }
        this.minHeap.add(data.measure());
        this.length += 1;
    }

    public T poll(long measure, List<Long> available) {
        if (this.length == 0) {
            this.first = this.last = null;
        } else {

            if (this.first.data.measure() <= measure) {

                T data = this.first.data;

                boolean next = false;
                for (Long n : available) {
                    if (n < measure && n >= data.measure()) {
                        next = true;
                        break;
                    }
                }

                if (!next) {
                    if (this.first != this.last) {
                        this.first = this.first.next;
                        this.first.prev = null;
                    } else {
                        this.first = this.last = null;
                    }
                    this.length -= 1;
                    this.updateMin(data.measure());
                    return data;
                }
            }

            Node<T> aux = this.first.next;
            while (aux != null) {
                if (aux.data.measure() <= measure ) {

                    boolean next = false;
                    for (Long n : available) {
                        if (n < measure && n >= aux.data.measure()) {
                            next = true;
                            break;
                        }
                    }

                    if (!next) {
                        if (aux == this.last) {
                            this.last = aux.prev;
                            aux.prev.next = null;
                        } else {
                            aux.next.prev = aux.prev;
                            aux.prev.next = aux.next;
                        }
                        this.length -= 1;
                        this.updateMin(aux.data.measure());
                        return aux.data;
                    } else {
                        aux = aux.next;
                    }
                } else {
                    aux = aux.next;
                }
            }
        }
        return null;
    }

    // this method removes all nodes from the queue that are greater than the limit, return a list
    public List<T> removeIf(long limit) {
        List<T> collected = new ArrayList<>();
        Node<T> current = this.first;
        while (current != null) {
            if (current.data.measure() > limit) {
                if (current == this.first && current == this.last) {
                    this.first = this.last = null;
                } else if (current == this.first) {
                    this.first = this.first.next;
                    this.first.prev = null;
                } else if (current == this.last) {
                    this.last = this.last.prev;
                    this.last.next = null;
                } else {
                    current.next.prev = current.prev;
                    current.prev.next = current.next;
                }

                this.length--;
                collected.add(current.data);
                this.updateMin(current.data.measure());
            }
            current = current.next;
        }
        return collected;
    }

    public void printMin() {
        System.out.println(this.min);
    }

    public Object[] toArray() {
        return this.minHeap.toArray();
    }

    public void print() {
        Node<T> current = this.first;
        while (current != null) {
            System.out.print(current.data + " ");
            current = current.next;
        }
        System.out.println("Length: " + this.length);
    }

}
