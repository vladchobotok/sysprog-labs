package com.knu;

import java.nio.file.*;
import java.io.*;
import java.util.Arrays;

public class AutomataAnalysis {
    private final String[] keywords = {
            "abstract", "continue", "for", "new", "switch", "assert", "debugger", "default", "goto", "package", "synchronized",
            "boolean", "do", "if", "for", "private", "this", "break", "double", "implements", "protected", "throw", "byte",
            "else", "import", "public", "throws", "case", "enum", "instanceof", "return", "transient", "catch", "extends",
            "int", "short", "try", "char", "final", "interface", "static", "void", "class", "finally", "long", "strictfp",
            "volatile", "const", "float", "native", "super", "while"
    };
    private final String[] operators = {
            "++", "--", "-", "+", "~", "!", "*", "/", "%", "<<", ">>", ">>>", "<", ">", "<=", ">=", "==", "!=", "&",
            "^", "|", "&&", "||", "?", ":", "=", "+=", "-=", "*=", "/=", "%=", "&=", "^=", "|=", "<<=", ">>=", ">>>="
    };
    private final String punctuation = ".,;()[]{}";

    private StringBuilder stringBuilder = new StringBuilder();

    private enum Lexeme {
        None,
        String,
        Number,
        Identifier,
        Commentary,
        Error,
        EndOfFile,
    }

    private Lexeme lexeme = Lexeme.None;
    private boolean decimalPoint = false;
    private boolean decimalExponent = false;
    private boolean hexadecimal = false;

    public static void main(String[] args) {
        String filename = "javacode.txt";
        String text = "";
        try {
            text = Files.readString(Path.of(filename));
        } catch (IOException e) {
            System.err.println(e);
        }

        new AutomataAnalysis().parseCode(text);
    }

    public void parseCode(String text) {
        int i = 0;
        while (lexeme != Lexeme.EndOfFile) {

            if (i >= text.length()) {
                if (lexeme == Lexeme.None) {
                    lexeme = Lexeme.EndOfFile;
                    System.out.println("\nFile is parsed ");
                } else {
                    lexeme = Lexeme.Error;
                    System.err.println("\nError while reading file");
                }
                break;
            }

            char symbol = text.charAt(i);
            String operator;

            switch (lexeme) {
                case None:
                    stringBuilder = new StringBuilder();
                    if (Character.isWhitespace(symbol)) {
                    } else if (punctuation.contains("" + symbol)) {
                        System.out.println("" + symbol + " - " + "Punctuation");
                    } else if (symbol == '/' && text.length() > i + 1 && text.charAt(i + 1) == '/') {
                        lexeme = Lexeme.Commentary;
                        stringBuilder.append("//");
                        i++;
                    } else if ((operator = startsWithOneOf(text.substring(i), operators)) != null) {
                        System.out.println(operator + " - " + "Operator");
                        i += operator.length() - 1;
                    } else if ("$_".contains("" + symbol) || Character.isLetter(symbol)) {
                        lexeme = Lexeme.Identifier;
                        stringBuilder.append(symbol);
                    } else if ("\"'".contains("" + symbol)) {
                        lexeme = Lexeme.String;
                        stringBuilder.append(symbol);
                    } else if (Character.isDigit(symbol)) {
                        lexeme = Lexeme.Number;
                        stringBuilder.append(symbol);

                    } else {
                        lexeme = Lexeme.Error;
                        stringBuilder.append(symbol);
                    }
                    i++;
                    break;
                case Identifier:
                    if (Character.isLetter(symbol) || Character.isDigit(symbol) || "$_".contains("" + symbol)) {
                        stringBuilder.append(symbol);
                        i++;
                    } else {
                        if (Arrays.asList(keywords).contains(stringBuilder.toString())) {
                            System.out.println(stringBuilder + " - " + "Keyword");
                        } else {
                            System.out.println(stringBuilder + " - " + "Identifier");
                        }
                        lexeme = Lexeme.None;
                    }
                    break;
                case String:
                    stringBuilder.append(symbol);
                    if ("\"'`".contains("" + symbol)) {
                        System.out.println(stringBuilder + " - " + "String");
                        lexeme = Lexeme.None;
                    }
                    i++;
                    break;
                case Number:
                    if (Character.isDigit(symbol) || "ABCDEFabcdef".contains("" + symbol) && hexadecimal) {
                        stringBuilder.append(symbol);
                        i++;
                    } else if (symbol == '.') {
                        if (decimalPoint) {
                            lexeme = Lexeme.Error;
                        } else {
                            decimalPoint = true;
                            stringBuilder.append(symbol);
                            i++;
                        }
                    } else if ("eE".contains("" + symbol)) {
                        if (decimalExponent) {
                            lexeme = Lexeme.Error;
                        } else {
                            decimalExponent = true;
                            stringBuilder.append(symbol);
                            i++;
                        }
                    } else if ("xX".contains("" + symbol)) {
                        if (hexadecimal) {
                            lexeme = Lexeme.Error;
                        } else {
                            hexadecimal = true;
                            stringBuilder.append(symbol);
                            i++;
                        }
                    }
                    else if ("+-".contains("" + symbol)) {
                        char last = stringBuilder.charAt(stringBuilder.length() - 1);
                        if ("eE".contains("" + last)) {
                            stringBuilder.append(symbol);
                            i++;
                        } else if (last == '.') {
                            lexeme = Lexeme.Error;
                        } else {
                            lexeme = Lexeme.None;
                        }
                    } else {
                        try {
                            System.out.println(stringBuilder.toString() + " - " + "Number");
                            lexeme = Lexeme.None;
                        } catch (NumberFormatException e) {
                            lexeme = Lexeme.Error;
                        }
                    }
                    break;
                case Commentary:
                    if (symbol != '\n') {
                        stringBuilder.append(symbol);
                    } else {
                        lexeme = Lexeme.None;
                    }
                    i++;
                    break;
                case Error:
                    System.err.println("\nInvalid lexeme");
                    return;
                case EndOfFile:
                    return;
            }
        }
    }

    private String startsWithOneOf(String text, String[] words) {
        for (String word: words) {
            if (text.startsWith(word)) return word;
        }
        return null;
    }
}