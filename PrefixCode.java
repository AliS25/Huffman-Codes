import java.io.InputStream;

/**
 * <p>An interface for encoding and decoding a text using a prefix
 * code. A prefix code associates a (binary) codeword to each
 * character appearing in a text. The <code>PrefixCode</code>
 * interface specifies methods for generating a code from a text,
 * encoding and decoding individual characters, and strings of
 * characters.</p>
 */

public interface PrefixCode {

    /**
     *
     * <p>Generate the initial code from an <code>InputStream
     * in</code>. The <code>PrefixCode</code> should use the character
     * frequencies form <code>in</code> in order to generate the
     * code.</p>p
     *
     * @param in the input stream containing the characters of the
     * text to be read
     */
    void generateCode(InputStream in);

    /**
     *
     * <p>Get the codeword associated to a character, or return the
     * empty string "" if the character does not have an associated
     * codeword.</p>
     *
     * @param ch the character whose codeword is sought
     * @return a String representing the codeword associated with ch
     */
    String getCodeword(char ch);

    /**
     * <p>Get the (ASCII value of) the character associated with
     * <code>codeword</code>, represented as a (binary)
     * <code>String</code>---i.e., a string in which all characters
     * are 0 or 1. If there is no char associated with
     * <code>codeword</code>, then the value -1 is returned.</p>
     *
     * @param codeword a (binary) string of a codeword
     * @return the ASCII value of a character associate with the
     * codeword, or -1 if there is no such codeword
     */
    int getChar(String codeword);

    /**
     * <p>Get the encoding of a string of characters as a binary
     * string. That is, the returned string consists of the
     * concatenation of the codewords of the individual characters of
     * <code>str</code>.</p>
     *
     * @param str the string to be encoded
     * @return the encoding of str as a binary String
     */
    String encode(String str);

    /**
     * <p>Get the decoding of a binary string according to the prefix
     * code. That is, each codeword in <code>str</code> is replaced
     * with its corresponding character. Assumes that the only
     * characters present in <code>str</code> are 0s and 1s.</p>
     *
     * @param str the binary string to be decoded
     * @return the decoded string
     */
    String decode(String str);

    /**
     * <p>Get the size of the original text in Bytes. Note that this
     * is equivalent to the number of <code>char</code>s in the
     * original <code>InputStream</code>, since each <code>char</code>
     * is encoded using 1 Byte (in ASCII encoding).</p>
     *
     * @return the size of the original text
     */
    int originalSize();

    /**
     * <p>Get the compressed size of the input text in Bytes. The
     * number of bits is the sum of the lengths of codewords for all
     * of the characters in text, and the number of Bytes is the
     * number of bits divided by 8.</p>
     *
     * @return the size of the compressed text in Bytes
     */
    int compressedSize();
	
}
