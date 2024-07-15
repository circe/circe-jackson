/*
 * Copyright 2016 circe
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

package io.circe.jackson

import io.circe.Json

/**
 * This package provides syntax for Jackson printing via enrichment classes.
 */
package object syntax {
  implicit final class JacksonPrintingOps[A](val json: Json) extends AnyVal {
    final def jacksonPrint: String = io.circe.jackson.jacksonPrint(json)
  }
}
