package com.begintesting;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.file.Path;

public class PrinttokensTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    Printtokens pt = new Printtokens();

    @Test
    public void testIsComment() {
        assertTrue(Printtokens.is_comment("; this is a comment"));
        assertFalse(Printtokens.is_comment("not a comment"));
    }

    @Test
    public void testIsKeyword() {
        assertTrue(Printtokens.is_keyword("and"));
        assertTrue(Printtokens.is_keyword("or"));
        assertTrue(Printtokens.is_keyword("if"));
        assertTrue(Printtokens.is_keyword("xor"));
        assertTrue(Printtokens.is_keyword("lambda"));
        assertTrue(Printtokens.is_keyword("=>"));

        assertFalse(Printtokens.is_keyword("variable"));
        assertFalse(Printtokens.is_keyword("function"));
    }

    @Test
    public void testIsIdentifier() {
        assertTrue(Printtokens.is_identifier("variable"));
        assertTrue(Printtokens.is_identifier("x1"));
        assertTrue(Printtokens.is_identifier("test123"));

        assertFalse(Printtokens.is_identifier("1variable"));
        assertFalse(Printtokens.is_identifier("variable!"));
        assertFalse(Printtokens.is_identifier(""));
    }

    @Test
    public void testIsNumConstant() {
        assertTrue(Printtokens.is_num_constant("123"));
        assertTrue(Printtokens.is_num_constant("0"));
        assertTrue(Printtokens.is_num_constant("456789"));

        assertFalse(Printtokens.is_num_constant("12a3"));
        assertFalse(Printtokens.is_num_constant("abc"));
        assertFalse(Printtokens.is_num_constant(""));
    }

    @Test
    public void testIsStrConstant() {
        assertTrue(Printtokens.is_str_constant("\"hello\""));
        assertTrue(Printtokens.is_str_constant("\"\""));
        assertTrue(Printtokens.is_str_constant("\"a string with spaces\""));

        assertFalse(Printtokens.is_str_constant("\"unclosed string"));
        assertFalse(Printtokens.is_str_constant("no quotes"));
        assertFalse(Printtokens.is_str_constant(""));
    }

    @Test
    public void testIsCharConstant() {
        assertTrue(Printtokens.is_char_constant("#a"));
        assertTrue(Printtokens.is_char_constant("#Z"));
        assertTrue(Printtokens.is_char_constant("#m"));

        assertFalse(Printtokens.is_char_constant("#1"));
        assertFalse(Printtokens.is_char_constant("#$"));
        assertFalse(Printtokens.is_char_constant("#ab"));
        assertFalse(Printtokens.is_char_constant(""));
    }

    @Test
    public void testIsSpecSymbol() {
        assertTrue(Printtokens.is_spec_symbol('('));
        assertTrue(Printtokens.is_spec_symbol(')'));
        assertTrue(Printtokens.is_spec_symbol('['));
        assertTrue(Printtokens.is_spec_symbol(']'));
        assertTrue(Printtokens.is_spec_symbol('/'));
        assertTrue(Printtokens.is_spec_symbol('`'));
        assertTrue(Printtokens.is_spec_symbol(','));
        assertTrue(Printtokens.is_spec_symbol('\''));

        assertFalse(Printtokens.is_spec_symbol('a'));
        assertFalse(Printtokens.is_spec_symbol('1'));
        assertFalse(Printtokens.is_spec_symbol(' '));
    }

    @Test
    public void testTokenType() {
        assertEquals(Printtokens.keyword, Printtokens.token_type("and"));
        assertEquals(Printtokens.keyword, Printtokens.token_type("if"));
        assertEquals(Printtokens.identifier, Printtokens.token_type("variable"));
        assertEquals(Printtokens.num_constant, Printtokens.token_type("123"));
        assertEquals(Printtokens.str_constant, Printtokens.token_type("\"hello\""));
        assertEquals(Printtokens.char_constant, Printtokens.token_type("#a"));
        assertEquals(Printtokens.comment, Printtokens.token_type("; this is a comment"));
        assertEquals(Printtokens.spec_symbol, Printtokens.token_type("("));
        assertEquals(Printtokens.error, Printtokens.token_type("123abc"));
        assertEquals(Printtokens.error, Printtokens.token_type("#$"));
    }

    @Test
    public void testGetToken() {
        String input = "and variable 123 \"hello\" #a ;comment ( )";
        BufferedReader br = new BufferedReader(new StringReader(input));

        String token;

        token = pt.get_token(br);
        assertEquals("and", token);

        token = pt.get_token(br);
        assertEquals("variable", token);

        token = pt.get_token(br);
        assertEquals("123", token);

        token = pt.get_token(br);
        assertEquals("\"hello\"", token);

        token = pt.get_token(br);
        assertEquals("#a", token);

        token = pt.get_token(br);
        assertEquals(";comment", token);

        token = pt.get_token(br);
        assertEquals("(", token);

        token = pt.get_token(br);
        assertEquals(")", token);

        token = pt.get_token(br);
        assertNull(token);
    }

    @Test
    public void testPrintToken() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        pt.print_token("and");
        assertEquals("keyword,\"and\".\n", outContent.toString());
        outContent.reset();

        pt.print_token("variable");
        assertEquals("identifier,\"variable\".\n", outContent.toString());
        outContent.reset();

        pt.print_token("123");
        assertEquals("numeric,123.\n", outContent.toString());
        outContent.reset();

        pt.print_token("\"hello\"");
        assertEquals("string,\"hello\".\n", outContent.toString());
        outContent.reset();

        pt.print_token("#a");
        assertEquals("character,\"a\".\n", outContent.toString());
        outContent.reset();

        pt.print_token(";comment");
        assertEquals("comment,\";comment\".\n", outContent.toString());
        outContent.reset();

        pt.print_token("(");
        assertEquals("lparen.\n", outContent.toString());
        outContent.reset();

        pt.print_token("unknown");
        assertEquals("error,\"unknown\".\n", outContent.toString());
        outContent.reset();

        System.setOut(originalOut);
    }

    @Test
    public void testEdgeCases() throws IOException {
        BufferedReader br = new BufferedReader(new StringReader(""));
        String token = pt.get_token(br);
        assertNull(token);

        br = new BufferedReader(new StringReader("   \n\t  "));
        token = pt.get_token(br);
        assertNull(token);

        br = new BufferedReader(new StringReader("\"unclosed string"));
        token = pt.get_token(br);
        assertEquals("\"unclosed string", token);
        assertEquals(Printtokens.error, Printtokens.token_type(token));

        br = new BufferedReader(new StringReader("#$"));
        token = pt.get_token(br);
        assertEquals("#$", token);
        assertEquals(Printtokens.error, Printtokens.token_type(token));
    }

    @Test
    public void testGetSpecSymbolOutput() {
        assertEquals("lparen.\n", Printtokens.getSpecSymbolOutput("("));
        assertEquals("rparen.\n", Printtokens.getSpecSymbolOutput(")"));
        assertEquals("lsquare.\n", Printtokens.getSpecSymbolOutput("["));
        assertEquals("rsquare.\n", Printtokens.getSpecSymbolOutput("]"));
        assertEquals("quote.\n", Printtokens.getSpecSymbolOutput("'"));
        assertEquals("bquote.\n", Printtokens.getSpecSymbolOutput("`"));
        assertEquals("comma.\n", Printtokens.getSpecSymbolOutput(","));
        assertEquals("slash.\n", Printtokens.getSpecSymbolOutput("/"));
        assertEquals("error,\"@\".\n", Printtokens.getSpecSymbolOutput("@"));
    }

    @Test
    public void testOpenTokenStream_NullFilename() throws Exception {
        String simulatedInput = "Test input from System.in";
        ByteArrayOutputStream simulatedIn = new ByteArrayOutputStream();
        System.setIn(new java.io.ByteArrayInputStream(simulatedInput.getBytes()));

        BufferedReader br = pt.open_token_stream(null);
        String readLine = br.readLine();

        assertNotNull(br, "BufferedReader should not be null for null filename");
        assertEquals(simulatedInput, readLine, "BufferedReader should read from System.in");

        System.setIn(System.in);
    }

    @Test
    public void testOpenTokenStream_EmptyFilename() throws Exception {
        String simulatedInput = "Test input from System.in for empty filename";
        ByteArrayOutputStream simulatedIn = new ByteArrayOutputStream();
        System.setIn(new java.io.ByteArrayInputStream(simulatedInput.getBytes()));

        BufferedReader br = pt.open_token_stream("");
        String readLine = br.readLine();

        assertNotNull(br, "BufferedReader should not be null for empty filename");
        assertEquals(simulatedInput, readLine, "BufferedReader should read from System.in");

        System.setIn(System.in);
    }

    @Test
    public void testOpenTokenStream_ValidFilename(@TempDir Path tempDir) throws Exception {
        File tempFile = tempDir.resolve("test.txt").toFile();
        String fileContent = "Content from the temporary file.";
        try (FileWriter fw = new FileWriter(tempFile)) {
            fw.write(fileContent);
        }

        BufferedReader br = pt.open_token_stream(tempFile.getAbsolutePath());
        String readLine = br.readLine();

        assertNotNull(br, "BufferedReader should not be null for valid filename");
        assertEquals(fileContent, readLine, "BufferedReader should read content from the file");
    }

    @Test
    public void testOpenTokenStream_InvalidFilename() throws Exception {
        String invalidFilename = "non_existent_file_12345.txt";

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        BufferedReader br = pt.open_token_stream(invalidFilename);

        assertNull(br, "BufferedReader should be null for invalid filename");

        String expectedErrorMessage = "The file " + invalidFilename + " doesn't exist\n";
        assertTrue(outContent.toString().contains(expectedErrorMessage),
                "System.out should contain the error message for invalid filename");

        System.setOut(originalOut);
    }

    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
    }

    @ParameterizedTest
    @CsvSource({
            "'(', 'lparen.\n'",
            "')', 'rparen.\n'",
            "'[', 'lsquare.\n'",
            "']', 'rsquare.\n'",
            "'''', 'quote.\n'",
            "'`', 'bquote.\n'",
            "',' , 'comma.\n'",
            "'/', 'slash.\n'",
            "'@', 'error,\"@\".\n'"
    })
    public void testPrintSpecSymbol(String inputSymbol, String expectedOutput) {
        Printtokens.print_spec_symbol(inputSymbol);

        assertEquals(expectedOutput, outContent.toString(),
                "Output should match expected for input: " + inputSymbol);

        outContent.reset();
    }

    @Test
    public void testMain_NoArguments() throws Exception {
        String simulatedInput = "token1\ntoken2\n";
        ByteArrayInputStream testIn = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(testIn);

        String[] args = {};
        Printtokens.main(args);

        String output = outContent.toString();
        assertFalse(output.contains("Error! Please give the token stream"));

        System.setIn(System.in);
    }

    @Test
    public void testMain_OneArgument_ValidFilename(@TempDir Path tempDir) throws Exception {
        File tempFile = tempDir.resolve("valid_tokens.txt").toFile();
        String fileContent = "token1\ntoken2\n";
        try (FileWriter fw = new FileWriter(tempFile)) {
            fw.write(fileContent);
        }

        String[] args = {tempFile.getAbsolutePath()};
        Printtokens.main(args);

        String output = outContent.toString();
        assertTrue(output.contains("token1"));
        assertTrue(output.contains("token2"));
        assertFalse(output.contains("Error! Please give the token stream"));
        assertFalse(output.contains("doesn't exist"));
    }

    @Test
    public void testMain_OneArgument_InvalidFilename() throws Exception {
        String invalidFilename = "non_existent_file_12345.txt";

        String[] args = {invalidFilename};
        Printtokens.main(args);

        String output = outContent.toString();
        assertTrue(output.contains("The file " + invalidFilename + " doesn't exist"));
    }

    @Test
    public void testMain_MultipleArguments() throws Exception {
        String[] args = {"arg1", "arg2"};
        Printtokens.main(args);

        String output = outContent.toString();
        assertTrue(output.contains("Error! Please give the token stream"));
    }
}
