public class List {
    private Node first, tail, current;

    // Node
    private class Node {
        private Object contentObj;
        private Node nextNode;

        public Node(Object pContent) {
            contentObj = pContent;
            nextNode = null;
        }

        public void setContent(Object pContent) {
            contentObj = pContent;
        }

        public Object content() {
            return contentObj;
        }

        public void setNext(Node pNext) {
            nextNode = pNext;
        }

        public Node getNext() {
            return nextNode;
        }

    } // Ende der Klasse Node


    public List() {
        tail = new Node(null); // Dummy
        first = tail;
        tail.setNext(tail);
        /* Der next-Zeiger des hinteren Dummy-Elementes
         * zeigt auf das vorangehende Element.
         */
        current = first;
    }

    public boolean isEmpty() {
        return first == tail;
    }

    public boolean hasAccess() {
        return (!this.isEmpty()) && (current != tail);
    }

    public void next() {
        if (this.hasAccess())
            current = current.getNext();
    }

    public void toFirst() {
        if (!this.isEmpty())
            current = first;
    }

    public void toLast() {
        if (!this.isEmpty())
            current = tail.getNext();
    }

    public Object getObject() {
        if (this.hasAccess())
            return current.content();
        else
            return null;
    }
    public void remove() {
        Node lPos, lFront;
        if (this.hasAccess() ) {
            if (current == first ) {
                first = current.getNext();
                if (current.getNext() == tail)
                    tail.setNext(first);
                current = first;
            }
            else {
                lPos = current;
                this.toFirst();
                lFront = current;
                while (this.hasAccess() & !(current == lPos)) {
                    lFront = current;
                    this.next();
                }
                lFront.setNext(lPos.getNext());
                current = lFront.getNext();
                if (current == tail)
                    tail.setNext(lFront);
            }
        }
    }

    public void append(Object pObject) {
        if (pObject != null) {
            Node lNewNode, lPos0;
            lPos0 = current;
            lNewNode = new Node(pObject);
            lNewNode.setNext(tail);
            if (this.isEmpty())
                first = lNewNode;
            else {
                Node lPrevious = tail.getNext();
                lPrevious.setNext(lNewNode);
            }
            tail.setNext(lNewNode);
            current = lPos0;
        }
    }

    public Object get(int position) {
        this.toFirst();
        int i = 0;
        while (this.hasAccess() && position > i) {
            i++;
            this.next();

        }
        return this.getObject();
    }
}
