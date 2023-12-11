/*
 * Copyright (c) 2024  Alcosi Group Ltd. and affiliates.
 *
 * Portions of this software are licensed as follows:
 *
 *     All content that resides under the "alcosi" and "atomicon" or “deploy” directories of this repository, if that directory exists, is licensed under the license defined in "LICENSE.TXT".
 *
 *     All third-party components incorporated into this software are licensed under the original license provided by the owner of the applicable component.
 *
 *     Content outside of the above-mentioned directories or restrictions above is available under the MIT license as defined below.
 *
 *
 *
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is urnished to do so, subject to the following conditions:
 *
 *
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 *
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.alcosi.lib.secured.logging.files;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;
import org.apache.logging.log4j.core.pattern.PatternConverter;
import org.apache.logging.log4j.util.PerformanceSensitive;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Plugin(name = "CustomMessagePatternConverter", category = PatternConverter.CATEGORY)
@ConverterKeys({"m", "msg", "message"})
@PerformanceSensitive("allocation")
public class MaskLogEventFactory extends LogEventPatternConverter {
    final static Integer MAX_SIZE = 1000;
    public static Set<Pattern> FILE_PATTERNS = Stream.of(
            Pattern.compile("([\\da-fA-F]{" + MAX_SIZE + ",})"),//HEX
            Pattern.compile("([\\da-zA-Z+\\/]{" + MAX_SIZE + ",}={0,3})")//BASE64
    ).collect(Collectors.toSet());
    public static Set<Pattern> SENSITIVE_PATTERNS = Stream.of(
            Pattern.compile("(<SensitiveData>)([\\da-fA-F]*)(</{1,2}SensitiveData>)"),//HEX
            Pattern.compile("(?i)(3c53656e736974697665446174613e)([\\da-fA-F]*)(?i)(3c2f53656e736974697665446174613e)"),//HEX with HEX encoded pattern
            Pattern.compile("(<SensitiveData>)([\\da-zA-Z+\\/]*={0,3})(</{1,2}SensitiveData>)")//BASE64
    ).collect(Collectors.toSet());
    MaskLogEventFactory(final String[] options) {
        super("m", "m");
    }

    public static MaskLogEventFactory newInstance(final String[] options) {
        return new MaskLogEventFactory(options);
    }

    @Override
    public void format(LogEvent event, StringBuilder outputMessage) {
        try {
            String original = event.getMessage().getFormattedMessage();
            String maskedSensitive = maskSensitive(original);
            String maskedFiles = maskFiles(maskedSensitive);
            outputMessage.append(maskedFiles);
        } catch (Exception e) {
            outputMessage.append("EXCEPTION IN LOGGER!").append(e.getClass().getSimpleName()).append(":").append(e.getMessage());
        }
    }

    private String maskSensitive(String message) {
        try {
            String reduced = SENSITIVE_PATTERNS.stream()
                    .reduce(message, (msg, p) ->
                            {
                                Matcher m = p.matcher(msg);
                                boolean found = m.find();
                                if (found) {
                                    String secondGroup = m.group(2);
                                    int secondGroupLength = secondGroup.length();
                                    String replaced = m.replaceAll(
                                            String.format(
                                                    "%s%s%s",
                                                    "$1",
                                                    "LENGTH:" + secondGroupLength,
                                                    "$3"));
                                    return replaced;
                                } else {
                                    return msg;
                                }
                            },
                            (one, second) -> one);
            return reduced;
        } catch (Throwable t) {
            return "EXCEPTION IN LOGGER!!!!!!!!!! " + message;
        }
    }

    private String maskFiles(String message) {
        try {
            return FILE_PATTERNS.stream()
                    .reduce(message, (msg, p) -> {
                        Matcher m = p.matcher(msg);
                        return m.find()
                                ? replaceAll(m)
                                : msg;
                    }, (one, second) -> one);
        } catch (Throwable t) {
            return "EXCEPTION IN LOGGER!" + message;
        }
    }

    private String replaceAll(Matcher m) {
        Integer length=m.group(1).length();
        return m.replaceAll("<TOO BIG:"+length+">");
    }

}
