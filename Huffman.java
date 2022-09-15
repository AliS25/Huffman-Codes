import java.io.IOException;
import java.io.InputStream;
import java.util.*;

//Ali Sbeih

/**
 * An implementation of interface for encoding and decoding a text using a prefix
 * code. A prefix code associates a (binary) codeword to each
 * character appearing in a text.
 */
public class Huffman implements PrefixCode {
    //total number of nodes representing distinct characters
    private int size = 0;
    //original size of the text in bytes
    private int orgSize = 0;
    //compressed size of the text in bits
    private int comSize = 0;
    //head of a doubly linked list
    private Node<Integer> head = null;
    //tail of a doubly linked list
    private Node<Integer> tail = null;
    //root of a tree
    private Node root;
    //map of keys as characters and values as strings representing codewords
    private HashMap<Character, String> map = new HashMap();

    /**
     * Generate the initial code from an InputStream
     * in. The PrefixCode should use the character
     * frequencies form "in" in order to generate the
     * code.
     */
    public void generateCode(InputStream in) {
        try {
            //first character
            int character = in.read();
            while (character != -1) {
                //increment the original size for each character
                orgSize++;

                //start at the head of the linked list
                Node<Integer> cur = head;
                for (int i = 0; i < size; ++i) {
                    //if the character is already in the list increment its frequency
                    if (cur.character == character) {
                        cur.frequency++;
                        break;
                    }
                    cur = cur.next;
                }

                //if the character is not in the list, create a new node to represent that character
                if (cur == null) {
                    Node<Integer> nd = new Node<>();
                    nd.character = (char) character;
                    nd.frequency = 1;

// if the list is empty, the newly added node is stored as the head and the tail
                    if (head == null) {
                        head = nd;
                        tail = nd;
                    }
                    //Otherwise, add the node to the end of the list as the new tail
                    else {
                        tail.next = nd;
                        nd.previous = tail;
                        tail = nd;
                    }
                    ++size;
                }
                //read the next character
                character = in.read();
            }

            //make a priority queue to store the characters based on their weights/frequencies
            PriorityQueue Q = new PriorityQueue();
            Node cur = head;
            for (int i = 0; i < size; i++) {
                Q.add(cur);
                cur = cur.next;
            }
            //create tree for decoding
            createTree(Q);

            //use the tree to create a map of characters and their corresponding codewords and save the nodes to the queue again
            for (int i = 0; i < size; i++) {
                createMap(root, Q);
            }

// build the tree again
            createTree(Q);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //A method to create the tree for decoding
    public void createTree(PriorityQueue Q) {
        //Pick the lightest two nodes and give them a parent with frequency equal to that of the two children combined
        while (Q.size() > 1) {
            Node nd0 = (Node) Q.remove();
            Node nd1 = (Node) Q.remove();
            Node nd2 = new Node();
            nd0.parent = nd2;
            nd1.parent = nd2;
            nd2.right = nd0;
            nd2.left = nd1;
            nd2.frequency = nd0.frequency + nd1.frequency;
            Q.add(nd2);
        }
        //Make the last node remaining the root of the tree
        root = (Node) Q.remove();
    }

    //A method to create the map for encoding
    public void createMap(Node nd, PriorityQueue Q) {
        String str = "";
        //Create a string that represents the codeword of some character
        while (true) {
            if (nd.left != null) {
                str = str + 0;
                nd = nd.left;
            } else if (nd.right != null) {
                str = str + 1;
                nd = nd.right;
            } else break;
        }
        //add the node to be removed back to the priority queue
        Q.add(nd);

        Node par = nd.parent;
        int freq = nd.frequency;

        //remove the node
        if (par.left == nd) par.left = null;
        else par.right = null;
        char c = nd.character;
        //remove internal nodes that don't have leaves carrying characters
        while (par.right == null && par.left == null) {
            Node grandparent = par.parent;
            if (grandparent != null) {
                if (grandparent.left == par) grandparent.left = null;
                else grandparent.right = null;
                par = grandparent;
            } else break;
        }
        //increase the compressed size by the frequency of each character multiplied by its length
        comSize += freq * str.length();
        //Add the character and its codeword to the map
        map.put(c, str);
    }

    /**
     * Get the codeword associated to a character, or return the
     * empty string "" if the character does not have an associated
     * codeword.
     */
    public String getCodeword(char ch) {
        String s = map.get(ch);
        if (s != null) return s;
        return "";
    }

    /**
     * Get the (ASCII value of) the character associated with
     * codeword, represented as a (binary)
     * String. If there is no char associated with
     * codeword, then the value -1 is returned.
     */
    public int getChar(String codeword) {
        Node cur = root;
        for (int i = 0; i < codeword.length(); i++) {
            if (cur != null) {
                if (codeword.charAt(i) == '0') cur = cur.left;
                else if (codeword.charAt(i) == '1') cur = cur.right;
            }
        }
        if (cur != null && cur.right == null && cur.left == null) return cur.character;
        return -1;
    }

    /**
     * Get the encoding of a string of characters as a binary
     * string. That is, the returned string consists of the
     * concatenation of the codewords of the individual characters of
     * str
     */
    public String encode(String str) {
        String s = "";
        for (int i = 0; i < str.length(); i++) {
            s += getCodeword(str.charAt(i));
        }
        return s;
    }

    /**
     * Get the decoding of a binary string according to the prefix
     * code. That is, each codeword in str is replaced
     * with its corresponding character. Assumes that the only
     * characters present in str are 0s and 1s.
     */
    public String decode(String str) {
        String string = "";
        String toReturn = "";
        for (int i = 0; i < str.length(); i++) {
            string += str.charAt(i);
            if (getChar(string) != -1) {
                char c = (char) getChar(string);
                toReturn += c;
                string = "";
            }
        }
        return toReturn;
    }

    /**
     * Get the size of the original text in Bytes. Note that this
     * is equivalent to the number of chars in the
     * original InputStream, since each char
     * is encoded using 1 Byte (in ASCII encoding).
     */
    public int originalSize() {
        return orgSize;
    }

    /**
     * Get the compressed size of the input text in Bytes. The
     * number of bits is the sum of the lengths of codewords for all
     * of the characters in text, and the number of Bytes is the
     * number of bits divided by 8.
     */
    public int compressedSize() {
        return comSize / 8;
    }


    /**
     * An inner class representing a node both in the linked list and the tree. Each
     * node stores a reference to a character, its frequency, the next node, the previous nodes, its right child, its left child, and its parent.
     */
    private class Node<E> implements Comparable<Node> {
        Node<E> parent;
        Node<E> right;
        Node<E> left;
        Node<E> next;
        Node<E> previous;
        char character;
        int frequency;

        //overriding the compareTo method in order to make comparisons between nodes based on frequencies.
        @Override
        public int compareTo(Node x) {
            return frequency - x.frequency;
        }
    }
}
