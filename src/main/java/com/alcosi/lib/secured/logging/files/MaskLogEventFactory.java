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
