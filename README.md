# circe-jackson

[![Build status](https://img.shields.io/github/workflow/status/circe/circe-jackson/Continuous%20Integration.svg)](https://github.com/circe/circe-jackson/actions)
[![Coverage status](https://img.shields.io/codecov/c/github/circe/circe-jackson/master.svg)](https://codecov.io/github/circe/circe-jackson)
[![Gitter](https://img.shields.io/badge/gitter-join%20chat-green.svg)](https://gitter.im/circe/circe)
[![Maven Central](https://img.shields.io/maven-central/v/io.circe/circe-jackson28_2.13.svg)](https://maven-badges.herokuapp.com/maven-central/io.circe/circe-jackson28_2.13)

This project provides support for using [Jackson][jackson] for JSON parsing and printing with
[circe][circe], a Scala library for encoding and decoding JSON to Scala types.

Several versions of Jackson are still in widespread use, and `io.circe.jackson` is cross-published
with support for Jackson 2.5, 2.6, 2.7, and 2.8. Each module has a two-digit suffix indicating its
Jackson version (`circe-jackson28` supports Jackson 2.8, etc.).

The project source is mostly shared, with version-specific code in separate source trees. Note that
the source supporting Jackson 2.6 and 2.7 is identical, so these version share a source tree.

There's not a lot of documentation, but the API is fairly minimal, and we do publish the
[API docs][api-docs].

Until the 0.6.2 release, the circe-jackson module lived in the [main circe project][circe], so if
you're interested in finding the source for earlier releases, please check the release tags there.

## Jackson vs. Jawn

The primary purpose of this module is to support circe adopters who already have a Jackson
dependency and would like to avoid adding a dependency on [Jawn][jawn].

In general new projects or projects that aren't currently using Jackson should prefer circe-jawn to
circe-jackson. Not all guarantees that hold for Jawn-based parsing and the default printer will hold
for the Jackson-based versions. Jackson's handling of numbers in particular differs significantly:
it doesn't distinguish positive and negative zeros, it may truncate large JSON numbers or simply
fail to parse them, it may print large numbers as strings, etc.

In our benchmarks circe-jawn outperforms circe-jackson on parsing that involves a lot of JSON
objects, but the default circe printer is not as fast as circe-jackson's:

```
Benchmark                       Mode  Cnt      Score     Error  Units
ParsingBenchmark.parseFoosC    thrpt  100   3421.718 ±  17.133  ops/s
ParsingBenchmark.parseFoosCJ   thrpt  100   2230.284 ±  20.305  ops/s
ParsingBenchmark.parseIntsC    thrpt  100  16894.760 ±  70.032  ops/s
ParsingBenchmark.parseIntsCJ   thrpt  100  17093.963 ± 125.049  ops/s

Benchmark                       Mode  Cnt      Score     Error  Units
PrintingBenchmark.printFoosC   thrpt  100   4173.857 ±  18.818  ops/s
PrintingBenchmark.printFoosCJ  thrpt  100   4997.025 ±  22.633  ops/s
PrintingBenchmark.printIntsC   thrpt  100  25834.763 ±  92.639  ops/s
PrintingBenchmark.printIntsCJ  thrpt  100  56459.382 ± 182.105  ops/s
```

If your project needs JSON printing to be as fast as possible, it may be worth evaluating
circe-jackson in your own benchmarks (especially since it's essentially a drop-in replacement—simply
add the appropriate version to your build, import `io.circe.jackson.syntax._`, and call
`doc.jacksonPrint` on your `Json` values instead of e.g. `doc.noSpaces`).

## Contributors and participation

This project is adapted from the Jackson-based parsing code in [Play JSON][play-json] (another
excellent Scala JSON library).

All circe projects support the [Typelevel][typelevel] [code of conduct][code-of-conduct] and we want
all of their channels (Gitter, GitHub, etc.) to be welcoming environments for everyone.

Please see the [circe contributors' guide][contributing] for details on how to submit a pull
request.

## License

circe-jackson is licensed under the **[Apache License, Version 2.0][apache]**
(the "License"); you may not use this software except in compliance with the
License.

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

[apache]: http://www.apache.org/licenses/LICENSE-2.0
[api-docs]: https://circe.github.io/circe-jackson/api/io/circe/jackson/
[circe]: https://github.com/circe/circe
[code-of-conduct]: http://typelevel.org/conduct.html
[contributing]: https://circe.github.io/circe/contributing.html
[jackson]: https://github.com/FasterXML/jackson
[jawn]: https://github.com/non/jawn
[play-json]: https://www.playframework.com/documentation/2.5.x/ScalaJson
[typelevel]: http://typelevel.org/
