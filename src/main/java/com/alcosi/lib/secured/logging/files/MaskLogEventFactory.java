/*
 * Copyright (c) 2023 Alcosi Group Ltd. and affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
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

/**
 * This class is a custom LogEventPatternConverter that masks sensitive information and file paths in*/
@Plugin(name = "CustomMessagePatternConverter", category = PatternConverter.CATEGORY)
@ConverterKeys({"m", "msg", "message"})
@PerformanceSensitive("allocation")
public class MaskLogEventFactory extends LogEventPatternConverter {
    /**
     * The maximum size of the logged body.
     */
    final static Integer MAX_SIZE = 1000;
    /**
     * A Set of regular expression patterns used to match file names.
     *
     * The FILE_PATTERNS variable is a Set of Patterns that are used to match
     * file names. It contains two patterns - one for hexadecimal strings
     * and one for base64 encoded strings.
     *
     * The patterns in FILE_PATTERNS are compiled using the java.util.regex.Pattern
     * class and are case-sensitive.
     *
     * The FILE_PATTERNS variable is intended to be used for identifying file names
     * that match a specific pattern. It can be used in various scenarios such
     * as file filtering, searching, or validation.
     */
    public static Set<Pattern> FILE_PATTERNS = Stream.of(
            Pattern.compile("([\\da-fA-F]{" + MAX_SIZE + ",})"),//HEX
            Pattern.compile("([\\da-zA-Z+\\/]{" + MAX_SIZE + ",}={0,3})")//BASE64
    ).collect(Collectors.toSet());
    /**
     * Represents a set of sensitive patterns used for masking sensitive data in log messages.
     *
     * The patterns are applied to log messages to identify and mask sensitive information like
     * credit card numbers, passwords, or any other sensitive data that should not be logged.
     *
     * Sensitive patterns can be regular expressions or predefined patterns to match specific types
     * of sensitive data, such as HEX or BASE64 encoded data.
     *
     * This variable is defined in the class MaskLogEventFactory.
     *
     * @see MaskLogEventFactory
     */
    public static Set<Pattern> SENSITIVE_PATTERNS = Stream.of(
            Pattern.compile("(<SensitiveData>)([\\da-fA-F]*)(</{1,2}SensitiveData>)"),//HEX
            Pattern.compile("(?i)(3c53656e736974697665446174613e)([\\da-fA-F]*)(?i)(3c2f53656e736974697665446174613e)"),//HEX with HEX encoded pattern
            Pattern.compile("(<SensitiveData>)([\\da-zA-Z+\\/]*={0,3})(</{1,2}SensitiveData>)")//BASE64
    ).collect(Collectors.toSet());
    MaskLogEventFactory(final String[] options) {
        super("m", "m");
    }

    /**
     * Creates a new instance of MaskLogEventFactory with the specified options.
     *
     * @param options the array of options to be passed to the MaskLogEventFactory constructor
     * @return a new instance of MaskLogEventFactory
     */
    public static MaskLogEventFactory newInstance(final String[] options) {
        return new MaskLogEventFactory(options);
    }

    /**
     * Formats the given log event by masking sensitive information and appending it to the output message.
     *
     * @param event The log event to format. Must not be null.
     * @param outputMessage The string builder to which the formatted event will be appended.*/
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

    /**
     * Masks sensitive patterns in the given message.
     *
     * @param message the message to be masked
     * @return the masked message with sensitive patterns replaced
     */
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

    /**
     * Masks file patterns in the given message.
     *
     * @param message The message containing file patterns.
     * @return The message with masked file patterns.
     */
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

    /**
     * Replaces all occurrences of a specified pattern in the input string with a replacement string.
     * The length of each occurrence is determined and appended to the replacement string.
     *
     * @param m the Matcher object that contains the pattern and input string to be replaced
     * @return the modified string with occurrences of the pattern replaced by the replacement string
     */
    private String replaceAll(Matcher m) {
        Integer length=m.group(1).length();
        return m.replaceAll("<TOO BIG:"+length+">");
    }

}
