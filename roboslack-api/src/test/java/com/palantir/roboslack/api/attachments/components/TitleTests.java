/*
 * Copyright 2017 Palantir Technologies, Inc. All rights reserved.
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
 */

package com.palantir.roboslack.api.attachments.components;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.either;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Strings;
import com.palantir.roboslack.api.testing.MoreAssertions;
import com.palantir.roboslack.api.testing.ResourcesReader;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.MethodSource;

public final class TitleTests {

    private static final String RESOURCES_DIRECTORY = "parameters/attachments/components/titles";

    public static void assertValid(Title title) {
        assertFalse(Strings.isNullOrEmpty(title.text()));
        title.link().ifPresent(link -> assertFalse(Strings.isNullOrEmpty(link.toString())));
    }

    @SuppressWarnings("unused") // Called from reflection
    static Stream<Executable> invalidConstructors() {
        return Stream.of(
                () -> Title.of("")
        );
    }

    @ParameterizedTest
    @MethodSource(value = "invalidConstructors")
    void testConstructionConstraints(Executable executable) {
        Throwable thrown = assertThrows(IllegalArgumentException.class, executable);
        assertThat(thrown.getMessage(),
                either(containsString("cannot contain markdown"))
                        .or(containsString("cannot be null or empty")));
    }

    @ParameterizedTest
    @ArgumentsSource(SerializedTitlesProvider.class)
    void testDeserialization(JsonNode json) {
        MoreAssertions.assertSerializable(json,
                Title.class,
                TitleTests::assertValid);
    }

    static class SerializedTitlesProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
            return ResourcesReader.readJson(RESOURCES_DIRECTORY).map(Arguments::of);
        }

    }

}
