/*
 * Copyright (c) 2019 Toast Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.toasttab.protokt.codegen

import com.toasttab.protokt.codegen.algebra.AST
import com.toasttab.protokt.codegen.algebra.Accumulator
import com.toasttab.protokt.codegen.algebra.Annotator
import com.toasttab.protokt.codegen.algebra.Effects
import com.toasttab.protokt.codegen.algebra.Interpreter
import com.toasttab.protokt.codegen.protoc.AnnotatedType
import com.toasttab.protokt.codegen.protoc.Protocol
import com.toasttab.protokt.codegen.protoc.TypeDesc

fun generate(
    p: Protocol,
    a: Annotator<AST<TypeDesc>>,
    e: Effects<AST<TypeDesc>, Accumulator<String>>,
    f: (Throwable) -> Unit
): (Accumulator<String>) -> Unit = { out ->
    Interpreter(a, e, p.types, f) {
        AST(TypeDesc(p.desc, AnnotatedType(it)))
    }(out)
}
