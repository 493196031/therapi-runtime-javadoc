package com.github.therapi.runtimejavadoc.internal.parser;

import com.github.therapi.runtimejavadoc.ClassJavadoc;
import com.github.therapi.runtimejavadoc.Comment;
import com.github.therapi.runtimejavadoc.FieldJavadoc;
import com.github.therapi.runtimejavadoc.MethodJavadoc;
import com.github.therapi.runtimejavadoc.OtherJavadoc;
import com.github.therapi.runtimejavadoc.ParamJavadoc;
import com.github.therapi.runtimejavadoc.SeeAlsoJavadoc;
import com.github.therapi.runtimejavadoc.ThrowsJavadoc;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class JavadocParser {

    private static final Pattern blockSeparator = Pattern.compile("^\\s*@(?=\\S)", Pattern.MULTILINE);

    private static final Pattern whitespace = Pattern.compile("\\s");

    public static ClassJavadoc parseClassJavadoc(String className, String javadoc, List<FieldJavadoc> fields,
            List<FieldJavadoc> enumConstants, List<MethodJavadoc> methods, List<MethodJavadoc> constructors) {
        ParsedJavadoc parsed = parse(javadoc);

        List<OtherJavadoc> otherDocs = new ArrayList<>();

        for (BlockTag t : parsed.getBlockTags()) {
            otherDocs.add(new OtherJavadoc(t.name, CommentParser.parse(className, t.value)));
        }

        return new ClassJavadoc(className, CommentParser.parse(className, parsed.getDescription()), fields, enumConstants, methods,
                constructors, otherDocs, new ArrayList<SeeAlsoJavadoc>());
    }

    public static FieldJavadoc parseFieldJavadoc(String owningClass, String fieldName, String javadoc) {
        ParsedJavadoc parsed = parse(javadoc);

        List<OtherJavadoc> otherDocs = new ArrayList<>();
        for (BlockTag t : parsed.getBlockTags()) {
            otherDocs.add(new OtherJavadoc(t.name, CommentParser.parse(owningClass, t.value)));
        }

        return new FieldJavadoc(fieldName, CommentParser.parse(owningClass, parsed.getDescription()), otherDocs, new ArrayList<SeeAlsoJavadoc>());
    }

    public static MethodJavadoc parseMethodJavadoc(String owningClass, String methodName, List<String> paramTypes, String javadoc) {
        ParsedJavadoc parsed = parse(javadoc);
    
        List<OtherJavadoc> otherDocs = new ArrayList<>();
        List<SeeAlsoJavadoc> seeAlsoDocs = new ArrayList<>();
        List<ParamJavadoc> paramDocs = new ArrayList<>();

        Comment returns = null;

        for (BlockTag t : parsed.getBlockTags()) {
            if (t.name.equals("param")) {
                String[] paramNameAndComment = whitespace.split(t.value, 2);
                String paramName = paramNameAndComment[0];
                String paramComment = paramNameAndComment.length == 1 ? "" :paramNameAndComment[1];

                paramDocs.add(new ParamJavadoc(paramName, CommentParser.parse(owningClass, paramComment)));
            } else if (t.name.equals("return")) {
                returns = CommentParser.parse(owningClass, t.value);
            } else if (t.name.equals("see")) {
                SeeAlsoJavadoc seeAlso = SeeAlsoParser.parseSeeAlso(owningClass, t.value);
                if (seeAlso != null) {
                    seeAlsoDocs.add(seeAlso);
                }
            } else {
                otherDocs.add(new OtherJavadoc(t.name, CommentParser.parse(owningClass, t.value)));
            }
        }

        return new MethodJavadoc(methodName, paramTypes, CommentParser.parse(owningClass, parsed.getDescription()), paramDocs,
                new ArrayList<ThrowsJavadoc>(), otherDocs, returns, seeAlsoDocs);
    }

    private static ParsedJavadoc parse(String javadoc) {
        String[] blocks = blockSeparator.split(javadoc);

        ParsedJavadoc result = new ParsedJavadoc();
        result.description = blocks[0].trim();

        for (int i = 1; i < blocks.length; i++) {
            result.blockTags.add(parseBlockTag(blocks[i]));
        }
        return result;
    }

    private static BlockTag parseBlockTag(String block) {
        String[] s = whitespace.split(block.trim(), 2);
        String name = s[0];
        String value = s.length > 1 ? s[1] : "";
        return new BlockTag(name, value);
    }
}
