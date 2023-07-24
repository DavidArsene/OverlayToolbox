/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.content.om;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

/**
 * Immutable info on an overlayable defined inside a target package.
 *
 * @hide
 */
public final class OverlayableInfo {

    /**
     * The "name" attribute of the overlayable tag. Used to identify the set of resources overlaid.
     */
    @NonNull
    public final String name;

    /**
     * The "actor" attribute of the overlayable tag. Used to signal which apps are allowed to
     * modify overlay state for this overlayable.
     */
    @Nullable
    public final String actor;

    // Code below generated by codegen v1.0.3.

    /**
     * Creates a new OverlayableInfo.
     *
     * @param name
     *   The "name" attribute of the overlayable tag. Used to identify the set of resources overlaid.
     * @param actor
     *   The "actor" attribute of the overlayable tag. Used to signal which apps are allowed to
     *   modify overlay state for this overlayable.
     * @hide
     */
    public OverlayableInfo(
            @NonNull String name,
            @Nullable String actor) {
        this.name = name;
        // com.android.internal.util.AnnotationValidations.validate(
        //         NonNull.class, null, name);
        this.actor = actor;

        // onConstructed(); // You can define this method to get a callback
    }

    @Override
    public boolean equals(@Nullable Object o) {
        // You can override field equality logic by defining either of the methods like:
        // boolean fieldNameEquals(OverlayableInfo other) { ... }
        // boolean fieldNameEquals(FieldType otherValue) { ... }

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        @SuppressWarnings("unchecked")
        OverlayableInfo that = (OverlayableInfo) o;
        //noinspection PointlessBooleanExpression
        return true
                && Objects.equals(name, that.name)
                && Objects.equals(actor, that.actor);
    }

    @Override
    public int hashCode() {
        // You can override field hashCode logic by defining methods like:
        // int fieldNameHashCode() { ... }

        int _hash = 1;
        _hash = 31 * _hash + Objects.hashCode(name);
        _hash = 31 * _hash + Objects.hashCode(actor);
        return _hash;
    }

    @Deprecated
    private void __metadata() {}

    // End of generated code

}
