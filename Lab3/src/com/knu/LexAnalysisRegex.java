package com.knu;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LexAnalysisRegex {
    public enum LexemeType {
        Whitespace("[\\s]+"),
        Number("\\b([0-9]\\d*(\\.\\d+)?|0[xX][0-9a-fA-F]+)\\b"),
        String("\"[^\"]*\"|'[^']*'"),
        Commentary("\\/\\/.*"),
        Keyword("\\b(abstract|continue|for|new|switch|assert|default|goto|package|synchronized|boolean|do|if|private|this|break|double|implements|protected|throw|byte|else|import|public|throws|case|enum|instanceof|return|transient|catch|extends|int|short|try|char|final|interface|static|void|class|finally|long|strictfp|volatile|const|float|native|super|while)\\b"),
        Identifier("\\b[a-zA-Z_$][a-zA-Z_$0-9]*\\b"),
        Operator("(\\+\\+|--|\\+|-|~|!|%|\\/|\\*|<<|>>|>>>|<|>|<=|>=|==|!=|&|\\^|\\||&&|\\|\\||=|\\+=|-=|\\*=|/=|%=|&=|\\^=|\\|=|<<=|>>=|>>>=|\\?|\\:)"),
        Punctuation("[;,\\.\\{\\}\\[\\]\\(\\)]");

        public final String pattern;

        LexemeType(String pattern) {
            this.pattern = pattern;
        }
    }

    public static void main(String[] args) {
        parseCode();
    }

    public static void parseCode() {
        String filename = "javacode.txt";
        String text;
        try {
            text = Files.readString(Path.of(filename));
        } catch (IOException e) {
            System.err.println("Error while reading file: " + e);
            return;
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (LexemeType type : LexemeType.values()) {
            stringBuilder.append(String.format("|(%s)", type.pattern));
        }

        Pattern pattern = Pattern.compile(stringBuilder.substring(1));
        Matcher matcher = pattern.matcher(text);

        int start = 0;
        while (matcher.find()) {
            if (start != matcher.start()) {
                System.err.println("\nInvalid lexeme: " + text.substring(start, matcher.start()));
                return;
            } else {
                start = matcher.end();
            }
            for (LexemeType type: LexemeType.values()) {
                if (matcher.group().matches(type.pattern)) {
                    if (type != LexemeType.Whitespace && type != LexemeType.Commentary) {
                        System.out.println(matcher.group() + " - " + type.name());
                    }
                    break;
                }
            }
        }
    }
}
